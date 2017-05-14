package anson.std.medical.dealer.aservice;

import android.os.Binder;

import anson.std.medical.dealer.MedicalForegroundService;

/**
 * Created by anson on 17-5-8.
 */

public class MedicalServiceBinder extends Binder {

    private MedicalForegroundService medicalService;

    public MedicalServiceBinder(MedicalForegroundService medicalService) {
        this.medicalService = medicalService;
    }

    public MedicalForegroundService getMedicalService() {
        return this.medicalService;
    }
}
