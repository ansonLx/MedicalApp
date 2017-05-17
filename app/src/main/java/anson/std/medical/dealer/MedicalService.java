package anson.std.medical.dealer;

import anson.std.medical.dealer.model.Doctor;
import anson.std.medical.dealer.model.Medical;
import anson.std.medical.dealer.model.TargetDate;

/**
 * Created by anson on 17-5-9.
 */
public interface MedicalService {

    boolean isDataLoaded();

    void loadMedicalData(Consumer<HandleResult> callback);

    void saveMedicalData(Medical medical, Consumer<HandleResult> callback);

    void setTemp(String key, String tempValue);

    String getTemp(String key);

    void clearTemp(boolean clearContact);

    Doctor getDoctorById(String doctorId);

    String getNextExpertDoctorId();

    boolean isExpertDoctor(String doctorId);

    Medical getMedicalData();

    boolean isLogin114();

    void login114();

    void listMedicalResource(String hospitalId, String departmentId, String date, Boolean amPm, Consumer<HandleResult> callback);

    void doAsADealer(TargetDate targetDate, Consumer<HandleResult> stepCallback);

    void submit(String verifyCode, Consumer<HandleResult> stepCallback);

}

