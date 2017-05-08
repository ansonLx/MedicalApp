package anson.std.medical.dealer.service;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

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
        switch (ServiceHandlerMessageType.valueOf(msg.what)){
            case LoadConfFile :
                // // TODO: 17-5-8 load conf file
                medicalService.doLoadMedicalData();
                break;
            case WriteConfFile:
                medicalService.doSaveMedicalData();
                break;
            case FixReference:
                break;
            case DoDealer:
                break;
            default:
                break;
        }
    }
}
