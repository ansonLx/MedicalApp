package anson.std.medical.dealer.aservice;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import anson.std.medical.dealer.Consumer;
import anson.std.medical.dealer.HandleResult;
import anson.std.medical.dealer.MedicalService;
import anson.std.medical.dealer.model.Medical;
import anson.std.medical.dealer.support.NotificationUtil;
import anson.std.medical.dealer.support.ServiceHandlerMessageType;

/**
 * Created by anson on 17-5-8.
 */
public class MedicalServiceHandler extends Handler {

    private Context context;

    private MedicalService medicalService;
    private int notificationId;

    public MedicalServiceHandler(Context context, MedicalService medicalService, int notificationId, Looper looper) {
        super(looper);
        this.medicalService = medicalService;
        this.notificationId = notificationId;
        this.context = context;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (ServiceHandlerMessageType.valueOf(msg.what)) {
            case LoadDataFile: {
                Consumer<HandleResult> callback = (Consumer<HandleResult>) msg.obj;
                medicalService.loadMedicalData(callback);
                break;
            }
            case WriteDataFile: {
                Object[] os = (Object[]) msg.obj;
                Consumer<HandleResult> callback = (Consumer<HandleResult>) os[1];
                Medical medical = (Medical) os[0];
                medicalService.saveMedicalData(medical, callback);
                break;
            }
            case FixReference:
                break;
            case DoDealer:
                break;
            default:
                break;
        }
    }
}
