package anson.std.medical.dealer.model;

/**
 * Created by anson on 17-5-8.
 */

public class MedicalResource {

    private String hospitalId;
    private String departmentId;
    private String doctorId;
    private String date;
    private Boolean amPm;
    private Patient patient;

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Boolean getAmPm() {
        return amPm;
    }

    public void setAmPm(Boolean amPm) {
        this.amPm = amPm;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
}
