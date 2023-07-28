package com.il4mb.bridgestone.app.background;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.il4mb.bridgestone.R;

import java.util.HashMap;
import java.util.Map;

public class BackgroundService extends Service {
    public static boolean isRunning = false;
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "ForegroundServiceChannel";

    private Map<Integer, ServiceTask> serviceTaskMap = new HashMap<>();

    public RequestControl requestControl;
    @Override
    public void onCreate() {
        super.onCreate();
        // Perform any initialization here
        requestControl = new RequestControl(getApplicationContext());
    }

    private int SleepTime = 80;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        createNotificationChannel();
        startForeground(NOTIFICATION_ID, buildNotification());

        // Perform your looping task here
        new Thread(() -> {
            while (true) {
                // Sleep for a certain interval
                try {

                    requestControl.onExecute();

                    Thread.sleep(SleepTime); // Sleep for 4 seconds
                    if(SleepTime < 1000) {
                        SleepTime = 1000;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        isRunning = true;
        // Return START_STICKY to indicate that the service should be restarted if it's terminated by the system
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        serviceTaskMap = null;

        // Clean up any resources here
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // Return the instance of MyBinder when the service is bound
        return new BackgroundServiceBinder();
    }

    public class BackgroundServiceBinder extends Binder {
        public BackgroundService getService() {
            return BackgroundService.this;
        }
    }
    static class ServiceTask {
        public int id;
        public IServiceTask iServiceTask;
    }
    public interface IServiceTask {
        void onExecute();
    }





    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private Notification buildNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Service")
                .setContentText("Service is running")
                .setSmallIcon(R.drawable.bridgestone_logo_transparent);
        return builder.build();
    }
}

