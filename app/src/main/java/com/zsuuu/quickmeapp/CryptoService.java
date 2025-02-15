package com.zsuuu.quickmeapp;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

public class CryptoService extends Service implements CryptoThread.ProgressDisplayer {

    public static final int START_FOREGROUND_ID = 1025;

    public static final String INPUT_FILE_NAME_EXTRA_KEY = "com.dewdrop623.androidcrypt.CryptoService.INPUT_URI_KEY";
    public static final String OUTPUT_FILE_NAME_EXTRA_KEY = "com.dewdrop623.androidcrypt.CryptoService.OUTPUT_FILE_NAME_EXTRA_KEY";
    public static final String INPUT_FILENAME_KEY = "com.dewdrop623.androidcrypt.CryptoService.INPUT_FILENAME_KEY";
    public static final String OUTPUT_FILENAME_KEY = "com.dewdrop623.androidcrypt.CryptoService.OUTPUT_FILENAME_KEY";
    public static final String VERSION_EXTRA_KEY = "com.dewdrop623.androidcrypt.CryptoService.VERSION_EXTRA_KEY";
    public static final String OPERATION_TYPE_EXTRA_KEY = "com.dewdrop623.androidcrypt.CryptoService.OPERATION_TYPE_EXTRA_KEY";
    public static final String DELETE_INPUT_FILE_KEY = "com.dewdrop623.androidcrypt.CryptoService.DELETE_INPUT_FILE_KEY";

    public static final String NOTIFICATION_CHANNEL_ID = "com.dewdrop623.androidcrypt.CryptoService.OPERATION_TYPE_EXTRA_KEY";

    private static final String PROGRESS_DISPLAYER_ID = "com.dewdrop623.androidcrypt.CryptoService.PROGRESS_DISPLAYER_ID";

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(START_FOREGROUND_ID, buildProgressNotification(CryptoThread.OPERATION_TYPE_ENCRYPTION, -1, R.string.app_name, -1, -1));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

         
        if (intent == null) {
                         stopForeground(true);
            return START_NOT_STICKY;
        }
        String inputFileName = intent.getStringExtra(INPUT_FILE_NAME_EXTRA_KEY);
        String outputFileName = intent.getStringExtra(OUTPUT_FILE_NAME_EXTRA_KEY);
        int version = intent.getIntExtra(VERSION_EXTRA_KEY, SettingsHelper.AESCRYPT_DEFAULT_VERSION);
        String password = MainActivityFragment.getAndClearPassword();
        boolean operationType = intent.getBooleanExtra(OPERATION_TYPE_EXTRA_KEY, CryptoThread.OPERATION_TYPE_DECRYPTION);
        boolean deleteInputFile = intent.getBooleanExtra(DELETE_INPUT_FILE_KEY, false);

        CryptoThread.registerForProgressUpdate(PROGRESS_DISPLAYER_ID, this);

        if (password != null) {
            CryptoThread cryptoThread = new CryptoThread(this, inputFileName, outputFileName, password, version, operationType, deleteInputFile);
            cryptoThread.start();
        } else {
            showToastOnGuiThread(R.string.error_null_password);
            stopSelf();
        }

        return START_STICKY;
    }

     
    public void showToastOnGuiThread(final int msg) {
        showToastOnGuiThread(getString(msg));
    }

     
    public void showToastOnGuiThread(final String msg) {
        final Context context = this;
        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
                 throw new UnsupportedOperationException("Not yet implemented");
    }

     
    private Notification buildProgressNotification(boolean operationType, int progress, int completedMessageStringId, int minutesToCompletion, int secondsToCompletion) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, 0);

        builder.setSmallIcon(operationType == CryptoThread.OPERATION_TYPE_ENCRYPTION ? R.drawable.ic_lock_png : R.drawable.ic_unlock_png);
        builder.setContentIntent(resultPendingIntent);

        if (progress < 0) {
            builder.setContentTitle(getString(R.string.app_name));
            builder.setContentText(getString(R.string.operation_in_progress));
        } else if (progress < 100) {
            String title = operationType == CryptoThread.OPERATION_TYPE_ENCRYPTION ? getString(R.string.encrypting) : getString(R.string.decrypting);
            if (minutesToCompletion != -1) {
                title = title.concat(" " + minutesToCompletion + "m");
            }
            if (secondsToCompletion != -1) {
                title = title.concat(" " + secondsToCompletion + "s");
            }
            builder.setContentTitle(title);
            builder.setProgress(100, progress, false);
        } else {
            builder.setContentTitle(getString(R.string.app_name));
            builder.setContentText(getString(completedMessageStringId));
        }
        return builder.build();
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        CharSequence channelName = getString(R.string.app_name);
        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_LOW);
        notificationChannel.enableLights(false);
        notificationChannel.enableVibration(false);
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(notificationChannel);
    }

              @Override
    public void update(boolean operationType, int progress, int completedMessageStringId, int minutesToCompletion, int secondsToCompletion) {
        NotificationManagerCompat notificationManager = (NotificationManagerCompat) NotificationManagerCompat.from(this);
        notificationManager.notify(START_FOREGROUND_ID, buildProgressNotification(operationType, progress, completedMessageStringId, minutesToCompletion, secondsToCompletion));
    }
}
