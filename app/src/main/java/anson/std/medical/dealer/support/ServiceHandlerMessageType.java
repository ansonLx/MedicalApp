package anson.std.medical.dealer.support;

/**
 * Created by anson on 17-5-8.
 */
public enum ServiceHandlerMessageType {

    LoadConfFile(0), WriteConfFile(1), FixReference(2), DoDealer(3);

    private int value;

    ServiceHandlerMessageType(int value) {
        this.value = value;
    }

    public int value(){
        return this.value;
    }

    public static ServiceHandlerMessageType valueOf(int value) {
        for (ServiceHandlerMessageType t : values()) {
            if (t.value == value) {
                return t;
            }
        }
        return null;
    }
}
