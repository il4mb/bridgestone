package com.il4mb.bridgestone.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.il4mb.bridgestone.R;
import com.il4mb.bridgestone.app.api.ResApi;
import com.il4mb.bridgestone.app.background.BackgroundService;
import com.il4mb.bridgestone.tools.Loader;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import okhttp3.Callback;

public class SupportApp extends AppCompatActivity {

    public final String LOADER_TAG = "loader";
    private ResApi resApi;
    Loader loaderFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createNotificationChannel();

        if (!isServiceRunning(BackgroundService.class)) {
            Intent serviceIntent = new Intent(this, BackgroundService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
        }

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        loaderFragment = Loader.newInstance();

        getWindow().setNavigationBarColor(Color.TRANSPARENT);

    }

    public ResApi getResApi(Callback callback) {

        resApi = new ResApi(this);
        resApi.callback = callback;
        return resApi;
    }

    @SuppressLint("CommitTransaction")
    public void showLoader() {

        FragmentManager fmm = getSupportFragmentManager();

        FragmentTransaction fts = fmm.beginTransaction();

        if (fmm.findFragmentByTag(LOADER_TAG) != null) {

            fts.remove(loaderFragment);
        }

        if (fmm.findFragmentByTag(LOADER_TAG) == null) {

            fmm.beginTransaction().add(loaderFragment, LOADER_TAG).commit();

        }
    }

    public void hideLoader() {

        FragmentManager fmm = getSupportFragmentManager();

        if (fmm.findFragmentByTag(LOADER_TAG) != null) {
            fmm.beginTransaction().remove(fmm.findFragmentByTag(LOADER_TAG)).commit();
        }

    }

    public SharedPreferences getPreferences() {

        return PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    public static String CHANNEL_ID = "BASIC_NOTIFICATION";

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = new String("Request Update");
            String description = new String("Notifikasi ini penting karena di gunakan untuk memberitahu bahwa suatu request telah di perbaharui.");
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            List<ActivityManager.RunningServiceInfo> runningServices = manager.getRunningServices(Integer.MAX_VALUE);
            for (ActivityManager.RunningServiceInfo service : runningServices) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }


    public static String getAbsoluteUrl(String basePath, String relativePath) throws MalformedURLException {

        URL baseURL = new URL(basePath);

        String host = baseURL.getProtocol() + "://" + baseURL.getHost();
        String absoluteURL = host + "/" + relativePath;

        return absoluteURL;
    }

}
