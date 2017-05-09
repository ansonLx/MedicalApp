package anson.std.medical.dealer;

import anson.std.medical.dealer.model.Medical;

/**
 * Created by anson on 17-5-9.
 */

public interface MedicalService {

    void loadMedicalData(Consumer<HandleResult> callback);

    void saveMedicalData(Medical medical, Consumer<HandleResult> callback);

    void encryptModel(Medical medical);

    void deEncryptModel(Medical medical);

}

