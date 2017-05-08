package anson.std.medical.dealer.model;

import java.util.List;

/**
 * Created by anson on 17-5-8.
 */

public class Medical {

    private String userName;
    private String pwd;
    private List<Patient> patientList;
    private List<Hospital> hospitalList;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public List<Patient> getPatientList() {
        return patientList;
    }

    public void setPatientList(List<Patient> patientList) {
        this.patientList = patientList;
    }

    public List<Hospital> getHospitalList() {
        return hospitalList;
    }

    public void setHospitalList(List<Hospital> hospitalList) {
        this.hospitalList = hospitalList;
    }
}
