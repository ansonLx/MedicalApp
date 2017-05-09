package anson.std.medical.dealer;

import anson.std.medical.dealer.model.Medical;

/**
 * Created by anson on 17-5-9.
 */

public class HandleResult {

    private boolean occurError;
    private String message;
    private Medical medical;

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
}
