package dewz.wa.listen.listen_whatsapp;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ReceiveBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        int receivedNotificationCode = intent.getIntExtra("Notification Code", -1);
        String packages = intent.getStringExtra("package");
        String title = intent.getStringExtra("title");
        String text = intent.getStringExtra("text");

        if (text != null) {

            if (!text.contains("new messages") && !text.contains("WhatsApp Web is currently active") && !text.contains("WhatsApp Web login")) {
                DateFormat df = new SimpleDateFormat("ddMMyyyyHHmmssSSS", Locale.getDefault());
                String date = df.format(Calendar.getInstance().getTime());

//                Log.d("DetailsEzraatext2 :", "Notification : " + receivedNotificationCode + "\nPackages : " + packages + "\nTitle : " + title + "\nText : " + text + "\nId : " + date + "\nandroid_id : " + android_id + "\ndevicemodel : " + devicemodel);
            }
        }
    }
}
