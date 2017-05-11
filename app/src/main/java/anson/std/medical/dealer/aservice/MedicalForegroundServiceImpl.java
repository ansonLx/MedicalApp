package anson.std.medical.dealer.aservice;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;

import java.util.ArrayList;
import java.util.List;

import anson.std.medical.dealer.Consumer;
import anson.std.medical.dealer.MedicalForegroundService;
import anson.std.medical.dealer.HandleResult;
import anson.std.medical.dealer.model.Medical;
import anson.std.medical.dealer.MedicalService;
import anson.std.medical.dealer.service.impl.MedicalServiceImpl;
import anson.std.medical.dealer.support.Constants;
import anson.std.medical.dealer.support.LogUtil;
import anson.std.medical.dealer.support.NotificationUtil;
import anson.std.medical.dealer.Medical114Api;
import anson.std.medical.dealer.web.api.impl.Medical114ApiImpl;

import static anson.std.medical.dealer.support.ServiceHandlerMessageType.LoadDataFile;
import static anson.std.medical.dealer.support.ServiceHandlerMessageType.WriteDataFile;

public class MedicalForegroundServiceImpl extends Service implements MedicalForegroundService {
    private static final String serviceName = "medical_service";
    private static final int notificationId = 156;

    private static MedicalServiceHandler handler;
    private static HandlerThread handlerThread;
    private static List<String> binders = new ArrayList<>();

    private MedicalServiceBinder binder;

    @Override
    public void onCreate() {
        handlerThread = new HandlerThread(serviceName + "_Handler_Thread", Process.THREAD_PRIORITY_FOREGROUND);
        handlerThread.start();

        Medical114Api medical114Api = new Medical114ApiImpl();
        MedicalService medicalService = new MedicalServiceImpl(this, medical114Api);
        handler = new MedicalServiceHandler(this, medicalService, notificationId, handlerThread.getLooper());

        binder = new MedicalServiceBinder(this);

        Notification notification = NotificationUtil.generateNotification("Medical Dealer BGService is running");
        startForeground(notificationId, notification);
        LogUtil.log("foreground service is onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.log("foreground service is onStartCommand");
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        String binderName = intent.getStringExtra(Constants.key_service_binder_name);
        if (binderName != null) {
            binders.add(binderName);
            NotificationUtil.updateNotification(notificationId, "add binder " + binderName);
        }
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        String binderName = intent.getStringExtra(Constants.key_service_binder_name);
        if (binderName != null) {
            binders.remove(binderName);
            NotificationUtil.updateNotification(notificationId, "remove binder " + binderName);
        }
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        handlerThread.quitSafely();
        handler = null;
        binder = null;
        NotificationUtil.updateNotification(notificationId, "foreground service is destroy");
        LogUtil.log("foreground service is destroy");
    }

    @Override
    public void loadMedicalData(Consumer<HandleResult> callback) {
        Message message = handler.obtainMessage();
        message.what = LoadDataFile.value();
        message.obj = callback;
        handler.sendMessage(message);
    }

    @Override
    public void saveMedicalData(Medical medical, Consumer<HandleResult> callback) {
        Message message = handler.obtainMessage();
        message.what = WriteDataFile.value();
        message.obj = new Object[]{medical, callback};
        handler.sendMessage(message);
    }
}
