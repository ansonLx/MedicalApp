package anson.std.medical.dealer.web.api.impl.Support;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by anson on 17-5-12.
 */

public class HttpUrlConnectionBuilder {

    private boolean doInput = true;
    private boolean doOutput = true;
    private boolean useCaches = true;
    private Charset charset = Charset.forName("UTF-8");

    private String url;
    private String method;
    private HttpHeaderStore httpHeaderStore;
    private List<NameValuePair> specifiedHeaders;
    private List<NameValuePair> parameters;

    private HttpUrlConnectionBuilder() {
    }

    public HttpURLConnection build() {
        HttpURLConnection connection = null;
        try {
            URL targetUrl = new URL(url);
            connection = (HttpURLConnection) targetUrl.openConnection();
            connection.setUseCaches(useCaches);
            connection.setDoInput(doInput);
            connection.setDoOutput(doOutput);
            connection.setRequestMethod(method);

            httpHeaderStore.setRequestToConnection(connection);
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

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static HttpUrlConnectionBuilder getDefaultBuilder(String url, String method) {
        HttpUrlConnectionBuilder builder = new HttpUrlConnectionBuilder();
        builder.url = url;
        builder.method = method.toUpperCase();
        return builder;
    }

    public HttpUrlConnectionBuilder setDoInput(boolean doInput) {
        this.doInput = doInput;
        return this;
    }

    public HttpUrlConnectionBuilder setDoOutput(boolean doOutput) {
        this.doOutput = doOutput;
        return this;
    }

    public HttpUrlConnectionBuilder setUseCaches(boolean useCaches) {
        this.useCaches = useCaches;
        return this;
    }

    public HttpUrlConnectionBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    public HttpUrlConnectionBuilder setHttpHeaderStore(HttpHeaderStore httpHeaderStore) {
        this.httpHeaderStore = httpHeaderStore;
        return this;
    }

    public HttpUrlConnectionBuilder setSpecifiedHeaders(List<NameValuePair> specifiedHeaders) {
        this.specifiedHeaders = specifiedHeaders;
        return this;
    }

    public HttpUrlConnectionBuilder addParameters(NameValuePair... parameter) {
        if (parameters == null) {
            parameters = new ArrayList<>();
        }
        for (int i = 0; i < parameter.length; i++) {
            parameters.add(parameter[i]);
        }
        return this;
    }
}
