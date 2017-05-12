package anson.std.medical.dealer.web.api.impl.Support;

/**
 * Created by anson on 17-5-12.
 */

public class HttpResponse {

    private int responseCode;
    private String responseMessage;
    private String responseBody;

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }
}
