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
import android.widget.TextView;

import anson.std.medical.dealer.MedicalForegroundService;
import anson.std.medical.dealer.R;
import anson.std.medical.dealer.Consumer;
import anson.std.medical.dealer.HandleResult;
import anson.std.medical.dealer.aservice.MedicalForegroundServiceImpl;
import anson.std.medical.dealer.aservice.MedicalServiceBinder;
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

        if (handler == null) {
            handler = new MedicalActivityHandler();
        }

        Intent startServiceIntent = new Intent(this, MedicalForegroundServiceImpl.class);
        medicalServiceConnection = new MedicalServiceConnection();
        bindService(startServiceIntent, medicalServiceConnection, Context.BIND_AUTO_CREATE);

        smsObserver = new SmsObserver(handler);

        // test
        registerSmsContentObserver();

        querySms();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(medicalServiceConnection);
        unregisterSmsContentObserver();
    }

    private void querySms(){

        Consumer<HandleResult> consumer = new Consumer<HandleResult>() {
            @Override
            public void apply(HandleResult s) {
                Cursor cursor = getContentResolver().query(content_sms, null, null, null, null);
                if(cursor != null){
                    System.out.println(cursor.getCount());
                    cursor.moveToFirst();
//                    while (cursor.moveToNext()){
                        System.out.println("a message ----->");
                        String[] fields = cursor.getColumnNames();
                        for (int i = 0; i < fields.length; i++) {
                            String fieldName = fields[i];
                            String value = cursor.getString(cursor.getColumnIndex(fieldName));
                            System.out.println("\t " + fieldName + " --> " + value);
//                        }
                    }
                }
            }
        };

        // get unread sms
        if(ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.READ_SMS") != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.READ_SMS"}, 9527);
            Message message = handler.obtainMessage();
            message.obj = new Object[]{consumer, new HandleResult()};
            handler.sendMessage(message);
        } else {
            consumer.apply(null);
        }

    }

    private void registerSmsContentObserver(){
        getContentResolver().registerContentObserver(content_sms, true, smsObserver);
    }

    private void unregisterSmsContentObserver(){
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

    private class MedicalServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtil.log(MainActivity.this, "medical service connected");
            medicalService = ((MedicalServiceBinder) service).getMedicalService();
            loadData();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {}
    }

    private class SmsObserver extends ContentObserver{

        public SmsObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            System.out.println(uri.toString());


        }
    }

}
