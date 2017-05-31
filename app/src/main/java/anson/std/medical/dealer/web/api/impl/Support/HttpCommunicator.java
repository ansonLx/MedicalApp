package anson.std.medical.dealer.web.api.impl.Support;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by anson on 17-5-11.
 */
public class HttpCommunicator {

    private String host;

    private HttpHeaderStore httpHeaderStore;

    public HttpCommunicator(String host) {
        this.host = host;
        httpHeaderStore = new HttpHeaderStore();
    }

    public HttpResponse post(String url, List<NameValuePair> extraHeaders, List<String> excludeHeaders, NameValuePair... parameters) {
        HttpResponse response = null;
        HttpRequestBuilder connectionBuilder = HttpRequestBuilder.getDefaultBuilder(host + url, "POST")
                .setSpecifiedHeaders(extraHeaders)
                .setExcludeHeaders(excludeHeaders)
                .setHttpHeaderStore(httpHeaderStore);
        if (parameters != null && parameters.length != 0) {
            connectionBuilder.addParameters(parameters);
        }
        HttpRequest httpRequest = connectionBuilder.build();
        while (true) {
            try {
                response = httpRequest.request();
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    public HttpResponse get(String url, boolean needRead, List<NameValuePair> extraHeaders, List<String> excludeHeaders, NameValuePair... parameters) {
        HttpResponse response = null;
        StringBuilder urlBuilder = new StringBuilder(host);
        urlBuilder.append(url);
        if (parameters != null && parameters.length != 0) {
            urlBuilder.append("?");
            for (int i = 0; i < parameters.length; i++) {
                NameValuePair param = parameters[i];
                urlBuilder.append(param.getName()).append("=").append(param.getValue()).append("&");
            }
            urlBuilder = urlBuilder.deleteCharAt(urlBuilder.length() - 1);
        }
        HttpRequestBuilder connectionBuilder = HttpRequestBuilder
                .getDefaultBuilder(urlBuilder.toString(), "GET")
                .setDoOutput(false)
                .setNeedReadbody(needRead)
                .setSpecifiedHeaders(extraHeaders)
                .setExcludeHeaders(excludeHeaders)
                .setHttpHeaderStore(httpHeaderStore);
        HttpRequest httpRequest = connectionBuilder.build();
        while (true) {
            try {
                response = httpRequest.request();
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return response;
    }

}
