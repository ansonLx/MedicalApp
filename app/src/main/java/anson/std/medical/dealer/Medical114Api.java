package anson.std.medical.dealer;

import java.util.List;

import anson.std.medical.dealer.model.MedicalResource;
import anson.std.medical.dealer.web.api.impl.DoctorFilter;

/**
 * Created by anson on 17-5-9.
 */

public interface Medical114Api {

    boolean login(String userName, String pwd);

    List<MedicalResource> getMedicalResources(String hospitalId, String departmentId, String date, boolean amPm);

    MedicalResource getMedicalResource(String hospitalId, String departmentId, String doctorId, String date, Boolean amPm, DoctorFilter doctorFilter);

    boolean sendVerifySms(String hospitalId, String departmentId, String doctorId, long sourceId);

    /**
     * submit
     *
     * @param medicalResource
     * @return if success return order number otherwise null
     */
    String commit(MedicalResource medicalResource);
}
