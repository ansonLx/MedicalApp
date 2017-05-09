package anson.std.medical.dealer;

import anson.std.medical.dealer.model.Medical;

/**
 * Created by anson on 17-5-9.
 */

public interface MedicalForegroundService {

    void loadMedicalData(Consumer<HandleResult> callback);

    void saveMedicalData(Medical medical, Consumer<HandleResult> callback);
}
