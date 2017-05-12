package anson.std.medical.dealer.web.api.impl;

/**
 * Created by anson on 17-5-12.
 */
public class MResponse {

    private boolean hasErroe;
    private int code;
    private String msg;
    private String data;

    public boolean isHasErroe() {
        return hasErroe;
    }

    public void setHasErroe(boolean hasErroe) {
        this.hasErroe = hasErroe;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
