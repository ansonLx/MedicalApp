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

    public HttpResponse post(String url, List<NameValuePair> extraHeaders, NameValuePair... parameters) {
        HttpResponse response = null;
        HttpUrlConnectionBuilder connectionBuilder = HttpUrlConnectionBuilder.getDefaultBuilder(host + url, "POST")
                .setHttpHeaderStore(httpHeaderStore);
        if (extraHeaders != null) {
            connectionBuilder.setSpecifiedHeaders(extraHeaders);
        }
        if (parameters != null) {
            connectionBuilder.addParameters(parameters);
        }
        HttpURLConnection connection = connectionBuilder.build();
        try {
            connection.connect();
            response = readResponse(connection, true);
            httpHeaderStore.saveResponse(connection.getHeaderFields());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
        return response;
    }

    public HttpResponse get(String url, boolean needRead, List<NameValuePair> extraHeaders, NameValuePair... parameters) {
        HttpResponse response = null;
        StringBuilder urlBuilder = new StringBuilder(host);
        urlBuilder.append(url);
        if (parameters != null) {
            urlBuilder.append("?");
            for (int i = 0; i < parameters.length; i++) {
                NameValuePair param = parameters[i];
                urlBuilder.append(param.getName()).append("=").append(param.getValue()).append("&");
            }
            urlBuilder = urlBuilder.deleteCharAt(urlBuilder.length() - 1);
        }
        HttpUrlConnectionBuilder connectionBuilder = HttpUrlConnectionBuilder
                .getDefaultBuilder(urlBuilder.toString(), "GET")
                .setHttpHeaderStore(httpHeaderStore);
        if (extraHeaders != null) {
            connectionBuilder.setSpecifiedHeaders(extraHeaders);
        }
        HttpURLConnection connection = connectionBuilder.build();
        try {
            connection.connect();
            response = readResponse(connection, needRead);
            httpHeaderStore.saveResponse(connection.getHeaderFields());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
        return response;
    }

    private HttpResponse readResponse(HttpURLConnection connection, boolean needReadBody) throws IOException {
        HttpResponse response = new HttpResponse();
        response.setResponseCode(connection.getResponseCode());
        response.setResponseMessage(connection.getResponseMessage());
        if (needReadBody) {
            StringBuilder sb = new StringBuilder();
            InputStream inputStream = connection.getInputStream();
            byte[] bytes = new byte[4096];
            int len;
            while ((len = inputStream.read(bytes)) != -1) {
                sb.append(new String(bytes, 0, len, Charset.forName("UTF-8")));
            }
            inputStream.close();
            response.setResponseBody(sb.toString());
        }
        return response;
    }
}
