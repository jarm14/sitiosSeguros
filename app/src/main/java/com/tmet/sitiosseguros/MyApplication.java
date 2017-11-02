package com.tmet.sitiosseguros;

/**
 * Created by diego on 04/08/2017.
 */
import android.app.Application;
import android.content.Context;

import com.onesignal.OneSignal;

public class MyApplication extends Application {

    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        //MyNotificationOpenedHandler : This will be called when a notification is tapped on.
        //MyNotificationReceivedHandler : This will be called when a notification is received while your app is running.
        OneSignal.startInit(this)
                .setNotificationOpenedHandler(new NotificationOpenedHandler())
                .setNotificationReceivedHandler( new NotificationReceivedHandler() )
                .init();
    }


}