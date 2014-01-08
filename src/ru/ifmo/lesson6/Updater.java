package ru.ifmo.lesson6;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: asus
 * Date: 24.10.13
 * Time: 2:41
 * To change this template use File | Settings | File Templates.
 */
public class Updater extends BroadcastReceiver {
    private static String BOOT_ACTION = "android.intent.action.BOOT_COMPLETED";
    private static ArrayList<String> urlList;

    public Updater(){
        super();
    }

    @Override
    public void onReceive(Context context, Intent broadcastIntent) {

        if (BOOT_ACTION.equalsIgnoreCase(broadcastIntent.getAction())){
            Updater.this.start(context);
        }

        Intent intentService = new Intent(context, Reloader.class);
        context.startService(intentService);
    }

    public void start(Context context){
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, Updater.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis(), AlarmManager.INTERVAL_HALF_HOUR, pi);
    }
}
