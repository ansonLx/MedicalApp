package anson.std.medical.dealer.model;

/**
 * Created by anson on 17-5-18.
 */
public class CommitSuccess {

    private String numericalSequence;
    private String departmentName;
    private String ampm;
    private String recognitionCode;
    private String offerTime;
    private String temporalSequence;
    private String dutyDate;

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDutyDate() {
        return dutyDate;
    }

    public void setDutyDate(String dutyDate) {
        this.dutyDate = dutyDate;
    }

    public String getNumericalSequence() {
        return numericalSequence;
    }

    public void setNumericalSequence(String numericalSequence) {
        this.numericalSequence = numericalSequence;
    }

    public String getAmpm() {
        return ampm;
    }

    public void setAmpm(String ampm) {
        this.ampm = ampm;
    }

    public String getRecognitionCode() {
        return recognitionCode;
    }

    public void setRecognitionCode(String recognitionCode) {
        this.recognitionCode = recognitionCode;
    }

    public String getOfferTime() {
        return offerTime;
    }

    public void setOfferTime(String offerTime) {
        this.offerTime = offerTime;
    }

    public String getTemporalSequence() {
        return temporalSequence;
    }

    public void setTemporalSequence(String temporalSequence) {
        this.temporalSequence = temporalSequence;
    }

    public String toString() {
        return dutyDate + " " + departmentName + " " + ampm + " " + offerTime + " " + numericalSequence + "号";
    }
}
