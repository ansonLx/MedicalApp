package anson.std.medical.dealer.aservice;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import anson.std.medical.dealer.Consumer;
import anson.std.medical.dealer.MedicalForegroundService;
import anson.std.medical.dealer.HandleResult;
import anson.std.medical.dealer.model.Department;
import anson.std.medical.dealer.model.Doctor;
import anson.std.medical.dealer.model.Hospital;
import anson.std.medical.dealer.model.Medical;
import anson.std.medical.dealer.MedicalService;
import anson.std.medical.dealer.model.Patient;
import anson.std.medical.dealer.model.TargetDate;
import anson.std.medical.dealer.service.impl.MedicalServiceImpl;
import anson.std.medical.dealer.support.Constants;
import anson.std.medical.dealer.support.LogUtil;
import anson.std.medical.dealer.support.NotificationUtil;
import anson.std.medical.dealer.Medical114Api;
import anson.std.medical.dealer.web.api.impl.Medical114ApiImpl;

import static anson.std.medical.dealer.support.ServiceHandlerMessageType.CommitTheDealer;
import static anson.std.medical.dealer.support.ServiceHandlerMessageType.CommitVerifyCode;
import static anson.std.medical.dealer.support.ServiceHandlerMessageType.ListMedicalResource;
import static anson.std.medical.dealer.support.ServiceHandlerMessageType.LoadDataFile;
import static anson.std.medical.dealer.support.ServiceHandlerMessageType.Login114;
import static anson.std.medical.dealer.support.ServiceHandlerMessageType.WriteDataFile;

public class MedicalForegroundServiceImpl extends Service implements MedicalForegroundService {
    private static final String serviceName = "medical_service";
    private static final int notificationId = 156;

    private static MedicalServiceHandler handler;
    private static HandlerThread handlerThread;


    private MedicalServiceBinder binder;
    private MedicalService medicalService;

    @Override
    public void onCreate() {
        handlerThread = new HandlerThread(serviceName + "_Handler_Thread", Process.THREAD_PRIORITY_FOREGROUND);
        handlerThread.start();

        Medical114Api medical114Api = new Medical114ApiImpl();
        medicalService = new MedicalServiceImpl(medical114Api);
        handler = new MedicalServiceHandler(medicalService, handlerThread.getLooper());

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
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
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
        if (!medicalService.isDataLoaded()) {
            Message message = handler.obtainMessage();
            message.what = LoadDataFile.ordinal();
            message.obj = callback;
            handler.sendMessage(message);
        } else {
            Medical medicalData = medicalService.getMedicalData();
            HandleResult handleResult = new HandleResult();
            handleResult.setOccurError(false);
            handleResult.setMessage("data load success");
            handleResult.setMedical(medicalData);
            callback.apply(handleResult);
        }
    }

    @Override
    public void saveMedicalData(Medical medical, Consumer<HandleResult> callback) {
        Message message = handler.obtainMessage();
        message.what = WriteDataFile.ordinal();
        message.obj = new Object[]{medical, callback};
        handler.sendMessage(message);
    }

    @Override
    public void setTemp(String key, String tempValue) {
        medicalService.setTemp(key, tempValue);
    }

    @Override
    public String getTemp(String key) {
        return medicalService.getTemp(key);
    }

    @Override
    public void clearTemp(boolean clearContact) {
        medicalService.clearTemp(clearContact);
    }

    @Override
    public Doctor getDoctorById(String doctorId) {
        return medicalService.getDoctorById(doctorId);
    }

    @Override
    public String getNextExpertDoctorId() {
        return medicalService.getNextExpertDoctorId();
    }

    @Override
    public boolean isExpertDoctor(Doctor doctor) {
        return medicalService.isExpertDoctor(doctor.getId());
    }

    @Override
    public Medical getMedicalData() {
        return medicalService.getMedicalData();
    }

    @Override
    public void login114() {
        if (!medicalService.isLogin114()) {
            Message message = handler.obtainMessage();
            message.what = Login114.ordinal();
            handler.sendMessage(message);
        }
    }

    @Override
    public void listMedicalResource(String hospitalId, String departmentId, String date, boolean amPm, Consumer<HandleResult> callback) {
        Message message = handler.obtainMessage();
        message.what = ListMedicalResource.ordinal();
        message.obj = new Object[]{hospitalId, departmentId, date, amPm, callback};
        handler.sendMessage(message);
    }

    @Override
    public void start(TargetDate targetDate, Consumer<HandleResult> stepCallback) {
        Message message = handler.obtainMessage();
        message.what = CommitTheDealer.ordinal();
        message.obj = new Object[]{targetDate, stepCallback};
        handler.sendMessage(message);
    }

    @Override
    public void submitVerifyCode(String verifyCode) {
        if (medicalService.getTemp(Constants.temp_submiting) == null) {
            medicalService.setTemp(Constants.temp_submiting, "true");
//            medicalService.setTemp(Constants.temp_verify_code_key_key_key, verifyCode);
            Message message = handler.obtainMessage();
            message.what = CommitVerifyCode.ordinal();
            message.obj = verifyCode;
            handler.sendMessage(message);
        } else {
            LogUtil.log("commit is running in BG service");
        }
    }
}
