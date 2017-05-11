package anson.std.medical.dealer;

import android.app.Application;
import android.content.Context;

/**
 * Created by anson on 17-5-11.
 */

public class MedicalApplication extends Application {

    public static MedicalApplication medicalApplication;

    public MedicalApplication() {
        medicalApplication = this;
    }

    public static Context getMedicalApplicationContext(){
        return medicalApplication.getApplicationContext();
    }
}
