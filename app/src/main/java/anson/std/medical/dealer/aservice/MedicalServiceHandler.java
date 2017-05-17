package anson.std.medical.dealer.aservice;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import anson.std.medical.dealer.Consumer;
import anson.std.medical.dealer.HandleResult;
import anson.std.medical.dealer.MedicalService;
import anson.std.medical.dealer.model.Medical;
import anson.std.medical.dealer.model.TargetDate;
import anson.std.medical.dealer.support.ServiceHandlerMessageType;

/**
 * Created by anson on 17-5-8.
 */
public class MedicalServiceHandler extends Handler {

    private MedicalService medicalService;

    public MedicalServiceHandler(MedicalService medicalService, Looper looper) {
        super(looper);
        this.medicalService = medicalService;
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
            case Login114: {
                medicalService.login114();
                break;
            }
            case ListMedicalResource: {
                Object[] os = (Object[]) msg.obj;
                String hospitalId = (String) os[0];
                String departmentId = (String) os[1];
                String date = (String) os[2];
                boolean amPm = (boolean) os[3];
                Consumer<HandleResult> callback = (Consumer<HandleResult>) os[4];
                medicalService.listMedicalResource(hospitalId, departmentId, date, amPm, callback);
                break;
            }
            case CommitTheDealer: {
                Object[] os = (Object[]) msg.obj;
                TargetDate targetDate = (TargetDate) os[0];
                Consumer<HandleResult> stepCallback = (Consumer<HandleResult>) os[1];
                medicalService.doAsADealer(targetDate, stepCallback);
                break;
            }
            case CommitVerifyCode: {
                Object[] os = (Object[]) msg.obj;
                String verifyCode = (String) os[0];
                Consumer<HandleResult> stepCallback = (Consumer<HandleResult>) os[1];
                medicalService.submit(verifyCode, stepCallback);
                break;
            }
            default:
                break;
        }
    }
}
