package anson.std.medical.dealer.activity.support;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import anson.std.medical.dealer.Consumer;
import anson.std.medical.dealer.MedicalForegroundService;
import anson.std.medical.dealer.aservice.MedicalServiceBinder;
import anson.std.medical.dealer.support.LogUtil;

/**
 * Created by xq on 2017/5/14.
 */

public class MedicalServiceConnection implements ServiceConnection {

    Consumer<MedicalForegroundService> onConnectedCallback;

    public MedicalServiceConnection(Consumer<MedicalForegroundService> onConnectedCallback) {
        this.onConnectedCallback = onConnectedCallback;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        onConnectedCallback.apply(((MedicalServiceBinder) service).getMedicalService());
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        LogUtil.logView(name + " BG Service is disconnected...");
    }
}
