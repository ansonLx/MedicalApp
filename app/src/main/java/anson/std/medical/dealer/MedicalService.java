package anson.std.medical.dealer;

import java.util.List;

import anson.std.medical.dealer.model.Medical;
import anson.std.medical.dealer.model.MedicalResource;

/**
 * Created by anson on 17-5-9.
 */

public interface MedicalService {

    void loadMedicalData(Consumer<HandleResult> callback);

    void saveMedicalData(Medical medical, Consumer<HandleResult> callback);

    void login114();

    void listMedicalResource(String hospitalId, String departmentId, String date, Boolean amPm, Consumer<HandleResult> callback);

}

