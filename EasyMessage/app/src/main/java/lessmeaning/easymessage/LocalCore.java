package lessmeaning.easymessage;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Максим on 03.11.2016.
 */
public class LocalCore {

    private LocalDataBase db;
    private BroadcastReceiver brv;
    private MainActivity activity;
    public static final String BROADCAST = "LESSMEANING.CHATMOBILE.HEY";

    public LocalCore(MainActivity activity) {
        this.activity = activity;
        db = new LocalDataBase(activity);
        brv = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                sendApproved();
            }
        };
        connectToService();
    }

    public void addTemp(String inf) {
        db.addTemp(inf);
    }

    public void sendApproved() {
        ArrayList<Row> rows = db.getApproved();
        Collections.sort(rows);
        int len = rows.size();
        String values[] = new String[len];
        for (int i = 0; i < len; i++) {
            values[i] = rows.get(i).getContent();
        }
        activity.reloadList(values);
    }

    private void connectToService() {
        try {
            activity.unregisterReceiver(brv);
        } catch (IllegalArgumentException e) {
//            do nothing;
        }
        activity.registerReceiver(brv, new IntentFilter(BROADCAST));
        if (isServiceRunning(Merger.class, activity))
            return;
        Intent intent = new Intent(activity, Merger.class);
        activity.startService(intent);
    }

    private boolean isServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void disconnectService() {
        activity.unregisterReceiver(brv);
    }
}
