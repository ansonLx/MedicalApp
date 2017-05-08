package anson.std.medical.dealer.model;

/**
 * Created by anson on 17-5-8.
 */

public class Patient {

    private String id;
    private String name;
    private String hospitalCard;
    private String medicareCard;

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

    public String getHospitalCard() {
        return hospitalCard;
    }

    public void setHospitalCard(String hospitalCard) {
        this.hospitalCard = hospitalCard;
    }

    public String getMedicareCard() {
        return medicareCard;
    }

    public void setMedicareCard(String medicareCard) {
        this.medicareCard = medicareCard;
    }
}
