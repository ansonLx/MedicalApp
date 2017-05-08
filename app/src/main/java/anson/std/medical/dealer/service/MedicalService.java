package anson.std.medical.dealer.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;

import anson.std.medical.dealer.R;
import anson.std.medical.dealer.activity.MainActivity;
import anson.std.medical.dealer.util.LogUtil;

public class MedicalService extends Service {
    private static final String serviceName = "medical_service";

    private Notification notification;

    public MedicalService() {
    }

    @Override
    public void onCreate() {
        LogUtil.log(serviceName + " exec onCreate");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        notification = new Notification.Builder(this)
                .setContentTitle(getText(R.string.foregroundService_notification_title))
                .setContentText(getText(R.string.foregroundService_notification_text))
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(pendingIntent)
                .setTicker(getText(R.string.foregroundService_ticker_text))
                .build();

        startForeground(5, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.log(serviceName + " exec onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.log(serviceName + " exec onBind");
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        LogUtil.log(serviceName + " exec onDestroy");
    }
}
