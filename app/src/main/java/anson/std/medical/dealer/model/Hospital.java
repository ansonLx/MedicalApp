package anson.std.medical.dealer.model;

import java.util.List;

/**
 * Created by anson on 17-5-8.
 */

public class Hospital {

    private String id;
    private String name;
    private List<Department> departmentList;

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

    public List<Department> getDepartmentList() {
        return departmentList;
    }

    public void setDepartmentList(List<Department> departmentList) {
        this.departmentList = departmentList;
    }
}
