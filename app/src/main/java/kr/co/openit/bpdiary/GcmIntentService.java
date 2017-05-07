package kr.co.openit.bpdiary;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import kr.co.openit.bpdiary.activity.common.WebViewActivity;
import kr.co.openit.bpdiary.common.constants.ManagerConstants.IntentData;
import kr.co.openit.bpdiary.utils.PreferenceUtil;
import kr.co.openit.bpdiary.activity.intro.IntroActivity;

public class GcmIntentService extends IntentService {

    public static final int NOTIFICATION_ID = 5713;

    private NotificationManager mNotificationManager;

    NotificationCompat.Builder builder;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) { // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM will be extended in the future with
             * new message types, just ignore any message types you're not interested in, or that you don't recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " + extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                boolean isNoti = PreferenceUtil.getNotification(getApplicationContext());

                if (isNoti) {
                    // This loop represents the service doing some work.
                    String type = extras.getString("type");
                    //webview
                    if ("webView".equals(type)) {
                        sendNotificationWeb(extras.getString("url"), extras.getString("message"));
                    } else if ("actionView".equals(type)) {
                        sendNotificationActionView(extras.getString("uri"), extras.getString("message"));
                    } else {
                        sendNotification(extras.getString("message"));
                    }
                } else {
                    //nothing
                }
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        WakefulBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, IntroActivity.class), 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.bpdiary_noti_icon)
                                                                                  .setContentTitle(getString(R.string.application_name))
                                                                                  .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                                                                                  .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mBuilder.setAutoCancel(true);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void sendNotificationWeb(String url, String msg) {
        mNotificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra(IntentData.URL, url);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.bpdiary_noti_icon)
                                                                                  .setContentTitle(getString(R.string.application_name))
                                                                                  .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                                                                                  .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mBuilder.setAutoCancel(true);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void sendNotificationActionView(String uri, String msg) {
        mNotificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.bpdiary_noti_icon)
                                                                                  .setContentTitle(getString(R.string.application_name))
                                                                                  .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                                                                                  .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mBuilder.setAutoCancel(true);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
