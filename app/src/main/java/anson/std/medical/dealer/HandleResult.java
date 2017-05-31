package anson.std.medical.dealer;

import java.util.List;

import anson.std.medical.dealer.model.Medical;
import anson.std.medical.dealer.model.MedicalResource;

/**
 * Created by anson on 17-5-9.
 */

public class HandleResult {

    private boolean occurError;
    private String message;
    private Medical medical;
    private List<MedicalResource> resourceList;

    private boolean commitFinish;

    public boolean isOccurError() {
        return occurError;
    }

    public void setOccurError(boolean occurError) {
        this.occurError = occurError;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Medical getMedical() {
        return medical;
    }

    public void setMedical(Medical medical) {
        this.medical = medical;
    }

    public List<MedicalResource> getResourceList() {
        return resourceList;
    }

    public void setResourceList(List<MedicalResource> resourceList) {
        this.resourceList = resourceList;
    }

    public boolean isCommitFinish() {
        return commitFinish;
    }

    public void setCommitFinish(boolean commitFinish) {
        this.commitFinish = commitFinish;
    }
}
