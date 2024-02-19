package com.icofound.Notification.Services;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.icofound.Activity.MainScreenActivity;
import com.icofound.R;

import java.util.Map;

public class MyFireBaseMessagingService extends FirebaseMessagingService {

    private static final String NOTIFICATION_CHANNEL_ID = "icofound_push_notifications_channel";
    private static final String NOTIFICATION_CHANNEL_NAME = "icofound Notifications";
    public static boolean shownotification = false;
    FirebaseFirestore firestore;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


        System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& " + remoteMessage.getData());


        // Handle message within 10 seconds
        handleNow(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), remoteMessage.getData());


        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
        }

    }

    @Override
    public void onNewToken(String token) {

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        System.out.println("...................newToken........." + token);
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow(String title, String message, Map<String, String> data) {

        if (!shownotification){
            sendNotification(title, message,data);
        }

    }

    private void sendNotification(String title, String messageBody, Map<String, String> data) {
        final Context context = getApplicationContext();

        final Intent notificationIntent = new Intent(context, MainScreenActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        String type = data.get("requestType");
        String eventId = data.get("eventId");
        String conversationID = data.get("conversationID");
        String userId = data.get("userId");


        notificationIntent.putExtra("requestType", type );
        notificationIntent.putExtra("eventId", eventId);
        notificationIntent.putExtra("conversationID", conversationID);
        notificationIntent.putExtra("userId", userId);

        final PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        }else {
            pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.drawable.rounded_appicon)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME,
                    NotificationManagerCompat.IMPORTANCE_HIGH, context);
        }

        notificationManager.notify(0, notificationBuilder.build());


    }

    @TargetApi(26)
    private void createNotificationChannel(@NonNull String aChannelId, @NonNull String aChannelName,
                                           int aImportance, Context aContext) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(aChannelId, aChannelName, aImportance);

            final NotificationManager manager = aContext.getSystemService(NotificationManager.class);

            switch (aChannelId) {
                case NOTIFICATION_CHANNEL_ID:
                    channel.enableVibration(false);
                    channel.setVibrationPattern(new long[]{1000, 1000, 1000, 1000, 1000});
                    channel.enableLights(true);
                    channel.setLightColor(Color.RED);
                    channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                            new AudioAttributes.Builder()
                                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                                    .build());

                    break;
                default:
                    break;
            }

            assert manager != null;

            manager.createNotificationChannel(channel);
        }
    }

}
