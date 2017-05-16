package anson.std.medical.dealer.model;

/**
 * Created by anson on 17-5-8.
 */

public class MedicalResource {

    private String hospitalId;
    private String departmentId;
    private String doctorId;
    private String doctorName;
    private String doctorTitleName;
    private String date;
    private Boolean amPm;
    private Patient patient;
    private int remainAvailableNumber;
    private String verifyCode;
    private String skill;
    private long dutySourceId;

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

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorTitleName() {
        return doctorTitleName;
    }

    public void setDoctorTitleName(String doctorTitleName) {
        this.doctorTitleName = doctorTitleName;
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

    public int getRemainAvailableNumber() {
        return remainAvailableNumber;
    }

    public void setRemainAvailableNumber(int remainAvailableNumber) {
        this.remainAvailableNumber = remainAvailableNumber;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public long getDutySourceId() {
        return dutySourceId;
    }

    public void setDutySourceId(long dutySourceId) {
        this.dutySourceId = dutySourceId;
    }
}
