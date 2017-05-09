package anson.std.medical.dealer.aservice;

import android.os.Binder;

/**
 * Created by anson on 17-5-8.
 */

public class MedicalServiceBinder extends Binder {

    private MedicalForegroundServiceImpl medicalService;

    public MedicalServiceBinder(MedicalForegroundServiceImpl medicalService) {
        this.medicalService = medicalService;
    }

    public MedicalForegroundServiceImpl getMedicalService() {
        return this.medicalService;
    }
}
