package com.fatorius.duinocoinminer.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.fatorius.duinocoinminer.R;
import com.fatorius.duinocoinminer.activities.ServiceNotificationActivity;
import com.fatorius.duinocoinminer.threads.MiningThread;
import com.fatorius.duinocoinminer.threads.ServiceCommunicationMethods;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MinerBackgroundService extends Service implements ServiceCommunicationMethods {
    private static final int MINUTE_IN_MILLISECONDS = 60_000;

    private PowerManager.WakeLock wakeLock;

    List<Thread> miningThreads;
    List<Integer> threadsHashrate;

    int numberOfMiningThreads;

    int sentShares = 0;
    int acceptedShares = 0;

    float acceptedPercentage = 0.0f;

    long lastNotificationSent = 0;

    NotificationChannel channel;
    PendingIntent pendingIntent;

    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onCreate(){
        super.onCreate();
        channel = new NotificationChannel(
                "duinoCoinAndroidMinerChannel",
                "MinerServicesNotification",
                NotificationManager.IMPORTANCE_MIN
        );

        getSystemService(NotificationManager.class).createNotificationChannel(channel);

        miningThreads = new ArrayList<>();
        threadsHashrate = new ArrayList<>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DuinoCoinMiner::MinerServiceWaveLock");
        wakeLock.acquire();

        String poolIp = intent.getStringExtra("poolIp");
        String ducoUsername = intent.getStringExtra("ducoUsername");
        numberOfMiningThreads = intent.getIntExtra("numberOfThreads", 0);
        int poolPort = intent.getIntExtra("poolPort", 0);
        float efficiency = intent.getFloatExtra("efficiency", 0);

        Log.d("Mining service", "Mining service started");

        Intent notificationIntent = new Intent(this, ServiceNotificationActivity.class);
        pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, "duinoCoinAndroidMinerChannel")
                .setContentTitle("Duino Coin Android Miner")
                .setContentText("The Miner is running in the background")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_launcher_foreground, "Stop mining", pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setTicker("The Miner is running")
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .build();

        int type = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            type = ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC;
        }

        startForeground(NOTIFICATION_ID, notification, type);

        miningThreads = new ArrayList<>();
        threadsHashrate = new ArrayList<>();

        for (int t = 0; t < numberOfMiningThreads; t++){
            Thread miningThread;

            try {
                miningThread = new Thread(new MiningThread(poolIp, poolPort, ducoUsername, efficiency, t, this));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            miningThreads.add(miningThread);
        }

        for (int t = 0; t < numberOfMiningThreads; t++){
            miningThreads.get(t).start();
            threadsHashrate.add(0);
        }

        // In your background thread (e.g., in a service or a separate thread)
        Handler backgroundHandler = new Handler(Looper.getMainLooper());

        // Example: passing a string value
        String messageText = "Hello from background thread!";
        Message message = backgroundHandler.obtainMessage();
        message.obj = messageText;

        // Send the message to the main thread
        backgroundHandler.sendMessage(message);

        if (wakeLock.isHeld()) {
            wakeLock.release();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d("Mining service", "Mining service destroyed");

        for (int t = 0; t < numberOfMiningThreads; t++){
            miningThreads.get(t).interrupt();
        }

        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void newShareSent() {
        sentShares++;
    }

    @Override
    public void newShareAccepted() {
        acceptedShares++;

        acceptedPercentage = ((float) (acceptedShares / sentShares)) * 10000;
        acceptedPercentage = Math.round(acceptedPercentage) / 100.0f;

        long currentTime = SystemClock.elapsedRealtime();

        if (currentTime - lastNotificationSent >= MINUTE_IN_MILLISECONDS){
            Notification notification = new NotificationCompat.Builder(this, "duinoCoinAndroidMinerChannel")
                    .setContentTitle("Duino Coin Android Miner")
                    .setContentText("Mining: (" + acceptedShares + "/" + sentShares + ") - " + acceptedPercentage + "%")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentIntent(pendingIntent)
                    .addAction(R.drawable.ic_launcher_foreground, "Stop mining", pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setTicker("The Miner is running")
                    .setOngoing(true)
                    .setOnlyAlertOnce(true)
                    .build();

            NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification);
        }

        lastNotificationSent = currentTime;
    }
}