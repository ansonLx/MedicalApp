package anson.std.medical.dealer.activity;

import android.os.Handler;
import android.os.Message;

import anson.std.medical.dealer.Consumer;
import anson.std.medical.dealer.HandleResult;

/**
 * Created by anson on 17-5-9.
 */

public class MedicalActivityHandler extends Handler {

    public MedicalActivityHandler() {
        super();
    }

    @Override
    public void handleMessage(Message msg) {
        Object[] data = (Object[]) msg.obj;
        Consumer<HandleResult> callback = (Consumer<HandleResult>) data[0];
        HandleResult result = (HandleResult) data[1];
        callback.apply(result);
    }
}
