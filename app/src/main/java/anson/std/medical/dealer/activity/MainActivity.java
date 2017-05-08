package anson.std.medical.dealer.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import anson.std.medical.dealer.R;
import anson.std.medical.dealer.service.MedicalService;
import anson.std.medical.dealer.service.MedicalServiceBinder;
import anson.std.medical.dealer.support.Constants;
import anson.std.medical.dealer.support.FileUtil;
import anson.std.medical.dealer.support.LogUtil;

public class MainActivity extends AppCompatActivity {

    private TextView logView;

    private boolean isMedicalServiceConnected;
    private MedicalService medicalService;
    private MedicalServiceConnection medicalServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logView = (TextView) findViewById(R.id.log_view);
        logView.setMovementMethod(new ScrollingMovementMethod());

        LogUtil.log(logView, "main activity onCreate");

        Intent startServiceIntent = new Intent(this, MedicalService.class);
        startService(startServiceIntent);

        medicalServiceConnection = new MedicalServiceConnection();
        bindService(startServiceIntent, medicalServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(medicalServiceConnection);
    }

    public void loadConf(){
        if(isMedicalServiceConnected){

            // if medical data is exists
            if(!FileUtil.isFileExists(this, Constants.medical_data_file_name)){
                LogUtil.log(logView, "call service to load conf");
                medicalService.loadConf(logView);
            } else {
//                Intent intent = new Intent(this, )
            }
        }
    }

    private class MedicalServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtil.log(MainActivity.this, "medical service connected");
            medicalService = ((MedicalServiceBinder) service).getMedicalService();
            isMedicalServiceConnected = true;
            loadConf();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isMedicalServiceConnected = false;
        }
    }

}
