package anson.std.medical.dealer;

import anson.std.medical.dealer.model.Doctor;
import anson.std.medical.dealer.model.Medical;

/**
 * Created by anson on 17-5-9.
 */

public interface MedicalForegroundService {

    void loadMedicalData(Consumer<HandleResult> callback);

    void saveMedicalData(Medical medical, Consumer<HandleResult> callback);

    void setTemp(String key, String tempValue);

    String getTemp(String key);

    void clearTemp(boolean clearContact);

    Doctor getDoctorById(String doctorId);

    Medical getMedicalData();

    void login114();

    void listMedicalResource(String hospitalId, String departmentId, String date, boolean amPm, Consumer<HandleResult> callback);
}
