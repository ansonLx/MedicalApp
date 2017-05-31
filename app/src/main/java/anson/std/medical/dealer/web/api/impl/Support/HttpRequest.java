package anson.std.medical.dealer.web.api.impl.Support;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by anson on 17-5-17.
 */

public class HttpRequest {

    private static Charset charset = Charset.forName("UTF-8");

    private HttpURLConnection connection;
    private List<NameValuePair> parameters;
    private HttpHeaderStore httpHeaderStore;
    private List<NameValuePair> specifiedHeaders;
    private List<String> excludeHeaders;
    private boolean needReadBody;

    HttpRequest() {
    }

    public HttpResponse request() throws IOException {
        HttpResponse response = null;
        try {
            httpHeaderStore.setRequestToConnection(connection, excludeHeaders);
            if (specifiedHeaders != null) {
                for (NameValuePair header : specifiedHeaders) {
                    connection.setRequestProperty(header.getName(), header.getValue());
                }
            }
            if (parameters != null) {
                StringBuilder sb = new StringBuilder();
                for (NameValuePair param : parameters) {
                    sb.append(param.getName()).append("=").append(param.getValue()).append("&");
                }
                sb.deleteCharAt(sb.length() - 1);
                byte[] contentBytes = sb.toString().getBytes(charset);
                long contentLen = contentBytes.length;
                connection.setRequestProperty("Content-Length", Long.toString(contentLen));
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(contentBytes);
                outputStream.flush();
            } else {
                connection.setRequestProperty("Content-Length", "0");
            }
            connection.connect();
            response = readResponse(connection);
            httpHeaderStore.saveResponse(connection.getHeaderFields());
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return response;
    }

    private HttpResponse readResponse(HttpURLConnection connection) throws IOException {
        HttpResponse response = new HttpResponse();
        response.setResponseCode(connection.getResponseCode());
        response.setResponseMessage(connection.getResponseMessage());
        if (needReadBody) {
            StringBuilder sb = new StringBuilder();
            InputStream inputStream = connection.getInputStream();
            byte[] bytes = new byte[4096];
            int len;
            while ((len = inputStream.read(bytes)) != -1) {
                sb.append(new String(bytes, 0, len, charset));
            }
            inputStream.close();
            response.setResponseBody(sb.toString());
        }
        return response;
    }

    public HttpURLConnection getConnection() {
        return connection;
    }

    public void setConnection(HttpURLConnection connection) {
        this.connection = connection;
    }

    public List<NameValuePair> getParameters() {
        return parameters;
    }

    public void setParameters(List<NameValuePair> parameters) {
        this.parameters = parameters;
    }

    public HttpHeaderStore getHttpHeaderStore() {
        return httpHeaderStore;
    }

    public void setHttpHeaderStore(HttpHeaderStore httpHeaderStore) {
        this.httpHeaderStore = httpHeaderStore;
    }

    public List<NameValuePair> getSpecifiedHeaders() {
        return specifiedHeaders;
    }

    public void setSpecifiedHeaders(List<NameValuePair> specifiedHeaders) {
        this.specifiedHeaders = specifiedHeaders;
    }

    public boolean isNeedReadBody() {
        return needReadBody;
    }

    public void setNeedReadBody(boolean needReadBody) {
        this.needReadBody = needReadBody;
    }

    public List<String> getExcludeHeaders() {
        return excludeHeaders;
    }

    public void setExcludeHeaders(List<String> excludeHeaders) {
        this.excludeHeaders = excludeHeaders;
    }
}
