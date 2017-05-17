package anson.std.medical.dealer.support;

/**
 * Created by anson on 17-5-8.
 */
public enum ServiceHandlerMessageType {

    LoadDataFile,
    WriteDataFile,
    CommitTheDealer,
    CommitVerifyCode,
    Login114,
    ListMedicalResource;

    public static ServiceHandlerMessageType valueOf(int value) {
        for (ServiceHandlerMessageType t : values()) {
            if (t.ordinal() == value) {
                return t;
            }
        }
        return null;
    }
}
