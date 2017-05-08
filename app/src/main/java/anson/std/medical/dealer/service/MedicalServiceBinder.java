package anson.std.medical.dealer.service;

import android.os.Binder;

/**
 * Created by anson on 17-5-8.
 */

public class MedicalServiceBinder extends Binder {

    private MedicalService medicalService;

    public MedicalServiceBinder(MedicalService medicalService) {
        this.medicalService = medicalService;
    }

    public MedicalService getMedicalService() {
        return this.medicalService;
    }
}
