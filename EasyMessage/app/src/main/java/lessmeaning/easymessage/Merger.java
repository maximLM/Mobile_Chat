package lessmeaning.easymessage;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

public class Merger extends Service implements Runnable {


    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(this).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i("Test", "Service: onTaskRemoved");
        if (Build.VERSION.SDK_INT == 19) {
            Intent restartIntent = new Intent(this, getClass());

            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            PendingIntent pi = PendingIntent.getService(this, 1, restartIntent,
                    PendingIntent.FLAG_ONE_SHOT);

            am.setExact(AlarmManager.RTC, System.currentTimeMillis() + 3000, pi);
        }
    }

    @Override
    public void run() {
//        do something
    }

    private void sendMsgToUpd() {
        if (checkIsActivityAlive(MainActivity.class, this))
            sendBroadcast(new Intent(LocalCore.BROADCAST));

        sendBroadcast(new Intent(this, MessageReceiver.class));

    }

    private boolean checkIsActivityAlive(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
