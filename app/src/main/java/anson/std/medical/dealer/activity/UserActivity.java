package anson.std.medical.dealer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import anson.std.medical.dealer.Consumer;
import anson.std.medical.dealer.HandleResult;
import anson.std.medical.dealer.MedicalForegroundService;
import anson.std.medical.dealer.R;
import anson.std.medical.dealer.activity.support.MedicalServiceConnection;
import anson.std.medical.dealer.aservice.MedicalForegroundServiceImpl;
import anson.std.medical.dealer.model.Medical;

public class UserActivity extends AppCompatActivity {

    private MedicalServiceConnection medicalServiceConnection;
    private MedicalForegroundService medicalForegroundService;
    private Button button;
    private EditText loginUserName;
    private EditText loginPwd;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        setTitle(R.string.activity_label_login);
        context = this;
        button = (Button) findViewById(R.id.login_btn);
        button.setActivated(false);
        loginUserName = (EditText) findViewById(R.id.login_user_name);
        loginPwd = (EditText) findViewById(R.id.login_user_pwd);

        Intent bindIntent = new Intent(this, MedicalForegroundServiceImpl.class);
        medicalServiceConnection = new MedicalServiceConnection(new Consumer<MedicalForegroundService>() {
            @Override
            public void apply(MedicalForegroundService medicalService) {
                medicalForegroundService = medicalService;
                init();
                button.setActivated(true);
            }
        });
        bindService(bindIntent, medicalServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void click(View view){
        String userName = loginUserName.getText().toString();
        String pwd = loginPwd.getText().toString();
        Medical medical = medicalForegroundService.getMedicalData();
        if(medical == null){
            medical = new Medical();
        }
        medical.setUserName(userName);
        medical.setPwd(pwd);
        medicalForegroundService.saveMedicalData(medical, new Consumer<HandleResult>() {
            @Override
            public void apply(HandleResult handleResult) {
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void init(){
        Medical medical = medicalForegroundService.getMedicalData();
        if(medical != null){
            loginUserName.setText(medical.getUserName());
            loginPwd.setText(medical.getPwd());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(medicalServiceConnection);
    }
}
