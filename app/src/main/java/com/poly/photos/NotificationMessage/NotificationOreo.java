package com.poly.photos.NotificationMessage;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.squareup.picasso.Picasso;

import java.io.IOException;

public class NotificationOreo extends ContextWrapper {
    private static final String CHANNEL_ID = "com.poly.photos";
    private static final String CHANNEL_NAME = "piny";
    private NotificationManager notificationManager;

    public NotificationOreo(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(false);
        channel.enableVibration(true);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(channel);


    }

    public NotificationManager getManager() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return notificationManager;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getOreoNotification(String title, String body,
                                                    PendingIntent pendingIntent, Uri soundUri, String icon, String avartar) {
        Notification.Builder builder = new Notification.Builder(this, CHANNEL_ID);
        builder.setContentIntent(pendingIntent);
        builder.setContentTitle(title);
        builder.setContentText(body);
        builder.setSmallIcon(Integer.parseInt(icon));
        builder.setSound(soundUri);
        builder.setAutoCancel(true);
        try {
            Bitmap bmp = Picasso.with(getApplicationContext()).load(avartar).get();
            builder.setLargeIcon(bmp);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  builder;
    }
}
