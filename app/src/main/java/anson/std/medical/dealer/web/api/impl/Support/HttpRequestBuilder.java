package anson.std.medical.dealer.web.api.impl.Support;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by anson on 17-5-12.
 */

public class HttpRequestBuilder {

    private static final int default_timeout = 30 * 1000;

    private boolean doInput = true;
    private boolean doOutput = true;
    private boolean useCaches = false;
    private int connectionTimeout = default_timeout;
    private int readTime = default_timeout;
    private boolean needReadBody = true;

    private String url;
    private String method;
    private HttpHeaderStore httpHeaderStore;
    private List<NameValuePair> specifiedHeaders;
    private List<String> excludeHeaders;
    private List<NameValuePair> parameters;

    private HttpRequestBuilder() {
    }

    public HttpRequest build() {
        HttpRequest httpRequest = new HttpRequest();
        HttpURLConnection connection = null;
        try {
            URL targetUrl = new URL(url);
            connection = (HttpURLConnection) targetUrl.openConnection();
            connection.setUseCaches(useCaches);
            connection.setDoInput(doInput);
            connection.setRequestMethod(method);
            connection.setDoOutput(doOutput);
            connection.setConnectTimeout(connectionTimeout);
            connection.setReadTimeout(readTime);
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        httpRequest.setNeedReadBody(needReadBody);
        httpRequest.setHttpHeaderStore(httpHeaderStore);
        httpRequest.setSpecifiedHeaders(specifiedHeaders);
        httpRequest.setExcludeHeaders(excludeHeaders);
        httpRequest.setParameters(parameters);
        httpRequest.setConnection(connection);
        return httpRequest;
    }

    public static HttpRequestBuilder getDefaultBuilder(String url, String method) {
        HttpRequestBuilder builder = new HttpRequestBuilder();
        builder.url = url;
        builder.method = method.toUpperCase();
        return builder;
    }

    public HttpRequestBuilder setDoInput(boolean doInput) {
        this.doInput = doInput;
        return this;
    }

    public HttpRequestBuilder setDoOutput(boolean doOutput) {
        this.doOutput = doOutput;
        return this;
    }

    public HttpRequestBuilder setUseCaches(boolean useCaches) {
        this.useCaches = useCaches;
        return this;
    }

    public HttpRequestBuilder setHttpHeaderStore(HttpHeaderStore httpHeaderStore) {
        this.httpHeaderStore = httpHeaderStore;
        return this;
    }

    public HttpRequestBuilder setSpecifiedHeaders(List<NameValuePair> specifiedHeaders) {
        this.specifiedHeaders = specifiedHeaders;
        return this;
    }

    public HttpRequestBuilder setConnectionTimeout(int timeout) {
        this.connectionTimeout = timeout;
        return this;
    }

    public HttpRequestBuilder setReadTimeout(int timeout) {
        this.readTime = timeout;
        return this;
    }

    public HttpRequestBuilder setNeedReadbody(boolean needReadbody){
        this.needReadBody = needReadbody;
        return this;
    }

    public HttpRequestBuilder addParameters(NameValuePair... parameter) {
        if (parameters == null) {
            parameters = new ArrayList<>();
        }
        for (int i = 0; i < parameter.length; i++) {
            parameters.add(parameter[i]);
        }
        return this;
    }

    public HttpRequestBuilder setExcludeHeaders(List<String> excludeHeaders){
        this.excludeHeaders = excludeHeaders;
        return this;
    }
}
