package anson.std.medical.dealer.web.api.impl.Support;


/**
 * Created by anson on 17-5-12.
 */
public class NameValuePair {

    public NameValuePair(String name, String value) {
        this.name = name;
        this.value = value;
    }

    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
