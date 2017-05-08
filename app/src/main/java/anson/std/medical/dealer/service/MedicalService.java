package anson.std.medical.dealer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import anson.std.medical.dealer.R;
import anson.std.medical.dealer.activity.MainActivity;
import anson.std.medical.dealer.model.Medical;
import anson.std.medical.dealer.support.Constants;
import anson.std.medical.dealer.support.FileUtil;
import anson.std.medical.dealer.support.LogUtil;

import static anson.std.medical.dealer.support.ServiceHandlerMessageType.LoadConfFile;
import static anson.std.medical.dealer.support.ServiceHandlerMessageType.WriteConfFile;

public class MedicalService extends Service {
    private static final String serviceName = "medical_service";
    private static final int notificationId = 156;

    private HandlerThread handlerThread;

    private int lastStartId;
    private MedicalServiceHandler handler;
    private MedicalServiceBinder binder;

    private Medical medicalData;

    @Override
    public void onCreate() {
        LogUtil.log(this, serviceName + " exec onCreate");

        handlerThread = new HandlerThread(serviceName + "_Handler_Thread", Process.THREAD_PRIORITY_FOREGROUND);
        handlerThread.start();

        handler = new MedicalServiceHandler(this, handlerThread.getLooper());
        binder = new MedicalServiceBinder(this);

        Notification notification = generateNotification("Medical Service exec OnCreate");
        startForeground(notificationId, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.log(this, serviceName + " exec onStartCommand");
        lastStartId = startId;

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.log(this, serviceName + " exec onBind");
        return binder;
    }

    public void loadMedicalData(){
        if(medicalData == null){
            Message message = handler.obtainMessage();
            message.what = LoadConfFile.value();
            handler.sendMessage(message);
        }
    }

    public void setMedicalData(Medical medicalData){
        this.medicalData = medicalData;
    }

    public void saveMedicalData(){
        Message message = handler.obtainMessage();
        message.what = WriteConfFile.value();
        handler.sendMessage(message);
    }

    void doLoadMedicalData(){
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = openFileInput(Constants.medical_data_file_name);
            medicalData = FileUtil.readFile(fileInputStream, Medical.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if(fileInputStream != null){
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        updateNotification("medical data is loaded");
    }

    void doSaveMedicalData(){
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = openFileOutput(Constants.medical_data_file_name, MODE_PRIVATE);
            FileUtil.flushFileByObject(fileOutputStream, medicalData);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if(fileOutputStream != null){
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        updateNotification("medical data is saved");
    }

    @Override
    public void onDestroy() {
        handlerThread.quitSafely();
        LogUtil.log(this, serviceName + " exec onDestroy");
    }

    private void updateNotification(String title) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, generateNotification(title));
    }

    private Notification generateNotification(String title) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        return new Notification.Builder(this)
                .setContentTitle(title)
                .setContentText(getText(R.string.medical_service_notification_text))
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(pendingIntent).build();
    }
}
