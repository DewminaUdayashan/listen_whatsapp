package dewz.wa.listen.listen_whatsapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class WhatsappAccessibilityService extends NotificationListenerService {
    private static final String TAG = "WA NOTI";
    DbHelper DB;

    private static final class ApplicationPackageNames {
        public static final String WHATSAPP_PACK_NAME = "com.whatsapp";
        public static final String WHATSAPP_4B_PACK_NAME = "com.whatsapp.4b";
        private static final String WHATSAPP_GB_PACK_NAME = "com.gbwhatsapp";
    }

    public static final class InterceptedNotificationCode {
        public static final int OTHER_NOTIFICATIONS_CODE = 0;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        DateFormat df = new SimpleDateFormat("ddMMyyyyHHmmssSSS", Locale.getDefault());
        String date = df.format(Calendar.getInstance().getTime());

        int notificationCode = matchNotificationCode(sbn);
        Bundle extras = sbn.getNotification().extras;
        Log.d(TAG, "onNotificationPosted: " + extras);
        String title = extras.getString("android.title");
        String text = "" + extras.getString("android.text");
        boolean isGroup = extras.getBoolean("android.isGroupConversation");
        String subtext = "";
        Log.d(TAG, "onNotificationPosted: TITLE =======>" + title);
        if (notificationCode != InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE) {
            DB = new DbHelper(getApplicationContext());
            if (!text.contains("This message was deleted") && !title.contains("WhatsApp") && !text.contains("messages from chats") && !text.contains("new messages") && !text.contains("WhatsApp Web is currently active") && !text.contains("WhatsApp Web login")) {
                Parcelable b[] = (Parcelable[]) extras.get("android.messages");
                if (b != null) {
                    for (Parcelable tmp : b) {
                        Bundle msgBundle = (Bundle) tmp;
                        subtext = msgBundle.getString("text");
                    }
                    Log.d("DetailsEzra1 :", subtext);
                }
                if (subtext.isEmpty()) {
                    subtext = text;
                }
                if (!isGroup) {
                    DB.insertData(title, date, subtext);
                    DB.close();
                } else {
                    String hiddenTitle = extras.getString("android.hiddenConversationTitle");
                    String title1 = hiddenTitle.replaceAll("\\(.*?\\)", "");
                    String sender = title.split(":")[1];
                    Log.d(TAG, "onNotificationPosted: Title : " + title1 + " Sender : " + sender + "================================");
                }
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannels() {
        NotificationChannel notificationChannel = new NotificationChannel("CHANNEL_ID", "CHANNEL_NAME", NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setDescription("this is the description of the channel.");
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(notificationChannel);
    }


    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        int notificationCode = matchNotificationCode(sbn);

        if (notificationCode != InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE) {

            StatusBarNotification[] activeNotifications = this.getActiveNotifications();

            if (activeNotifications != null && activeNotifications.length > 0) {
                for (int i = 0; i < activeNotifications.length; i++) {
                    if (notificationCode == matchNotificationCode(activeNotifications[i])) {
                        Intent intent = new Intent("com.example.ssa_ezra.whatsappmonitoring");
                        intent.putExtra("Notification Code", notificationCode);
                        sendBroadcast(intent);
                        break;
                    }
                }
            }
        }
    }

    private int matchNotificationCode(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();

        if (packageName.equals(ApplicationPackageNames.WHATSAPP_4B_PACK_NAME) ||
                packageName.equals(ApplicationPackageNames.WHATSAPP_GB_PACK_NAME) ||
                packageName.equals(ApplicationPackageNames.WHATSAPP_PACK_NAME)) {
            return 1;
        } else {
            return 0;
        }

    }
}