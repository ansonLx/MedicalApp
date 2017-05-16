package anson.std.medical.dealer.model;

import java.util.List;

/**
 * Created by anson on 17-5-8.
 */

public class Doctor {

    private String id;
    private String name;
    private String title;
    private String skill;
    private List<TargetDate> targetDateList;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public List<TargetDate> getTargetDateList() {
        return targetDateList;
    }

    public void setTargetDateList(List<TargetDate> targetDateList) {
        this.targetDateList = targetDateList;
    }
}
