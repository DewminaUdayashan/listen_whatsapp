package dewz.wa.listen.listen_whatsapp;

import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;

/**
 * ListenWhatsappPlugin
 */
public class ListenWhatsappPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware, PluginRegistry.ActivityResultListener {
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
    private ReceiveBroadcastReceiver imageChangeBroadcastReceiver;
    private AlertDialog enableNotificationListenerAlertDialog;
    public Activity activity;

    public static MethodChannel channel;
    BinaryMessenger messenger;

    @Override
    public void onAttachedToEngine(FlutterPluginBinding flutterPluginBinding) {
        messenger = flutterPluginBinding.getBinaryMessenger();
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "listen_whatsapp");
        channel.setMethodCallHandler(this);
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        switch (call.method) {
            case "startService":
                if (!isNotificationServiceEnabled()) {
                    activity.startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
                }
                break;

            case "checkNotificationService":
                result.success(isNotificationServiceEnabled());
                break;
            case "getSenders":
                result.success(getSenders());
                break;
            case "getMessages":
                result.success(getMessages());
                break;
            case "getGroups":
                result.success(getGroups());
                break;
            case "getGroupMessages":
                result.success(getGroupMessages());
                break;
            case "deleteContactMessage":
                Map<String, Object> map = call.arguments();
                DbHelper DB = new DbHelper(activity);
                DB.deleteContactMessage(map.get("sender_id").toString(), map.get("message_id").toString());
                break;
            case "deleteContact":
                Map<String, Object> map1 = call.arguments();
                DbHelper DB1 = new DbHelper(activity);
                DB1.deleteContact(map1.get("contact_id").toString());
                DB1.close();
                break;
            case "deleteGroupMessage":
                Map<String, Object> map2 = call.arguments();
                DbHelper DB2 = new DbHelper(activity);
                DB2.deleteGroupMessage(map2.get("group_id").toString(), map2.get("message_id").toString());
                DB2.close();
                break;
            case "deleteGroup":
                Map<String, Object> map3 = call.arguments();
                DbHelper DB3 = new DbHelper(activity);
                DB3.deleteGroup(map3.get("group_id").toString());
                DB3.close();
                break;
        }
    }


    List<Map<String, Object>> getGroups() {
        List<Map<String, Object>> list = new ArrayList<>();
        DbHelper DB = new DbHelper(activity);
        try {
            Cursor res = DB.getGroups();
            if (res.getCount() == 0) {
                return list;
            }
            while (res.moveToNext()) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", res.getInt(0));
                map.put("name", res.getString(1));
                map.put("update_at", res.getString(2));
                list.add(map);
            }
            DB.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    List<Map<String, Object>> getGroupMessages() {
        List<Map<String, Object>> list = new ArrayList<>();
        DbHelper DB = new DbHelper(activity);
        try {
            Cursor res = DB.getGroupMessages();
            if (res.getCount() == 0) {
                return list;
            }
            while (res.moveToNext()) {
                Log.d("TAG", "getGroupMessages: Group Message ========> " + res.getString(3));
                Map<String, Object> map = new HashMap<>();
                map.put("id", res.getInt(0));
                map.put("group_id", res.getInt(1));
                map.put("sender", res.getString(2));
                map.put("message", res.getString(3));
                list.add(map);
            }
            DB.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    List<Map<String, Object>> getSenders() {
        List<Map<String, Object>> list = new ArrayList<>();
        DbHelper DB = new DbHelper(activity);
        try {
            Cursor res = DB.getSenders();
            if (res.getCount() == 0) {
                return list;
            }
            while (res.moveToNext()) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", res.getInt(0));
                map.put("sender", res.getString(1));
                map.put("update_at", res.getString(2));
                list.add(map);
            }
            DB.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    List<Map<String, Object>> getMessages() {
        List<Map<String, Object>> list = new ArrayList<>();
        DbHelper DB = new DbHelper(activity);
        try {
            Cursor res = DB.getMessages();
            if (res.getCount() == 0) {
                return list;
            }
            while (res.moveToNext()) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", res.getInt(0));
                map.put("sender_id", res.getInt(1));
                map.put("message", res.getString(2));
                list.add(map);
            }
            DB.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    @Override
    public void onDetachedFromEngine(FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }


    private boolean isNotificationServiceEnabled() {
        String pkgName = activity.getPackageName();
        final String flat = Settings.Secure.getString(activity.getContentResolver(),
                ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (String name : names) {
                final ComponentName cn = ComponentName.unflattenFromString(name);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void onAttachedToActivity(ActivityPluginBinding binding) {
        activity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        activity = null;
    }

    @Override
    public void onReattachedToActivityForConfigChanges(ActivityPluginBinding binding) {
        activity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivity() {
        activity = null;
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        return false;
    }
}
