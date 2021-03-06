package dev.sagar.smsblocker.tech.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import dev.sagar.smsblocker.R;
import dev.sagar.smsblocker.tech.beans.SMS;
import dev.sagar.smsblocker.ux.activities.ThreadActivity;

/**
 * Created by sagarpawar on 05/11/17.
 */

public class NotificationUtilSingleton {

    //Log Initiate
    LogUtil log = new LogUtil(this.getClass().getName());

    //Java Android

    //Java Core
    private static NotificationUtilSingleton instance = null;

    private NotificationUtilSingleton(){}


    public synchronized static NotificationUtilSingleton getInstance(){
        if(instance == null)
            instance = new NotificationUtilSingleton();
        return instance;
    }

    /**
     * This Method Creates a Notification
     * @param context
     * @param sms
     */
    public void createSMSNotification(Context context, SMS sms){
        final int NOTIFICATION_ID = 123;

        String from = sms.getFrom();
        String fromName = ContactUtilSingleton.getContactName(context, from);
        String text = sms.getBody();

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(fromName)
                        .setContentText(text);

        Intent resultIntent = new Intent(context, ThreadActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(ThreadActivity.KEY_THREAD_ID, from);
        resultIntent.putExtras(bundle);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent)
                .setAutoCancel(true);

        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
