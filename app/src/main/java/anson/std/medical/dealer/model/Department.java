package anson.std.medical.dealer.model;

import java.util.List;

/**
 * Created by anson on 17-5-8.
 */

public class Department {

    private String id;
    private String name;
    private List<Doctor> doctorList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Doctor> getDoctorList() {
        return doctorList;
    }

    public void setDoctorList(List<Doctor> doctorList) {
        this.doctorList = doctorList;
    }
}
