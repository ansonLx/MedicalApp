package anson.std.medical.dealer.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import anson.std.medical.dealer.MedicalForegroundService;
import anson.std.medical.dealer.R;
import anson.std.medical.dealer.Consumer;
import anson.std.medical.dealer.HandleResult;
import anson.std.medical.dealer.aservice.MedicalForegroundServiceImpl;
import anson.std.medical.dealer.aservice.MedicalServiceBinder;
import anson.std.medical.dealer.support.Constants;
import anson.std.medical.dealer.support.LogUtil;

public class MainActivity extends AppCompatActivity {

    private static final Uri content_sms = Uri.parse("content://sms/");

    private TextView logView;
    private static MedicalActivityHandler handler;

    private MedicalForegroundService medicalService;
    private MedicalServiceConnection medicalServiceConnection;
    private SmsObserver smsObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logView = (TextView) findViewById(R.id.log_view);
        logView.setMovementMethod(new ScrollingMovementMethod());

        if (ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.READ_SMS") != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.READ_SMS"}, 9527);
            LogUtil.log("no sms read permission");
        }

        if (handler == null) {
            handler = new MedicalActivityHandler();
        }

        // start bg service and bind to it
        Intent startIntent = new Intent(this, MedicalForegroundServiceImpl.class);
        startService(startIntent);
        Intent binderIntent = new Intent(this, MedicalForegroundServiceImpl.class);
        binderIntent.putExtra(Constants.key_service_binder_name, MainActivity.class.getSimpleName());
        medicalServiceConnection = new MedicalServiceConnection();
        bindService(binderIntent, medicalServiceConnection, Context.BIND_AUTO_CREATE);

        // register sms content change observer
        smsObserver = new SmsObserver(handler);
        registerSmsContentObserver();
        LogUtil.log("main activity on create");

    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.log("main activity on start");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.log("main activity on resume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.log("main activity on pause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.log("main activity on stop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.log("main activity on destroy");
        unbindService(medicalServiceConnection);
        unregisterSmsContentObserver();
    }

    private void readUnread114Sms() {
        String[] projection = new String[]{"address", "body", "date", "status"};
        Cursor cursor = getContentResolver().query(content_sms, projection, "address='114'", null, "date desc");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                LogUtil.log("a message ----->");
                String[] fields = cursor.getColumnNames();
                for (int i = 0; i < fields.length; i++) {
                    String fieldName = fields[i];
                    String value = cursor.getString(cursor.getColumnIndex(fieldName));
                    System.out.println("\t " + fieldName + " --> " + value);
                }
            }
        }
    }

    private void registerSmsContentObserver() {
        getContentResolver().registerContentObserver(content_sms, true, smsObserver);
    }

    private void unregisterSmsContentObserver() {
        getContentResolver().unregisterContentObserver(smsObserver);
    }

    private void loadData() {

        medicalService.loadMedicalData(new Consumer<HandleResult>() {

            @Override
            public void apply(HandleResult handleResult) {
                Message message = handler.obtainMessage();
                Object[] os = new Object[2];
                os[0] = new Consumer<HandleResult>() {

                    @Override
                    public void apply(HandleResult handleResult) {
//                        logHandleResult(handleResult);
                    }
                };
                os[1] = handleResult;
                message.obj = os;
                handler.sendMessage(message);
            }
        });
    }

    private void initMedicalData(){

    }

    private class MedicalServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtil.log("BG service connected");
            medicalService = ((MedicalServiceBinder) service).getMedicalService();
            loadData();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtil.logView("BG Service is disconnected...");
        }
    }

    private class SmsObserver extends ContentObserver {

        public SmsObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            LogUtil.log(uri.toString());
            readUnread114Sms();
        }
    }

}
