package anson.std.medical.dealer.activity;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import anson.std.medical.dealer.R;
import anson.std.medical.dealer.service.MedicalService;
import anson.std.medical.dealer.util.LogUtil;

public class MainActivity extends AppCompatActivity {

    private TextView logView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logView = (TextView) findViewById(R.id.log_view);
        logView.setMovementMethod(new ScrollingMovementMethod());

        LogUtil.log(logView, "main activity onCreate");
    }

    public void startService(View view){
        Intent startServiceIntent = new Intent(this, MedicalService.class);
        startService(startServiceIntent);
    }
}
