package anson.std.medical.dealer.web.api.impl.Support;

import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by anson on 17-5-12.
 */

public class HttpHeaderStore {

    private Map<String, String> headersMap;

    public HttpHeaderStore() {
        init();
    }

    private void init() {
        headersMap = new HashMap<>();
        headersMap.put("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36");
        headersMap.put("Accept", "application/json, text/javascript, */*; q=0.01");
        headersMap.put("Accept-Encoding", "gzip, deflate");
        headersMap.put("Accept-Language", "en-US,en;q=0.8");
        headersMap.put("Connection", "keep-alive");
        headersMap.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        headersMap.put("Host", "www.bjguahao.gov.cn");
        headersMap.put("Origin", "http://www.bjguahao.gov.cn");
        headersMap.put("X-Requested-With", "XMLHttpRequest");
    }

    public void saveResponse(Map<String, List<String>> response) {
        if (response.containsKey("Set-Cookie") || response.containsKey("Cookie")) {
            List<String> list = response.get("Set-Cookie");
            if (list == null) {
                list = response.get("Cookie");
            }
            StringBuilder sb = new StringBuilder();
            for (String value : list) {
                sb.append(value).append(";");
            }
            sb = sb.deleteCharAt(sb.length() - 1);
            headersMap.put("Cookie", sb.toString());
        }
    }

    public void setRequestToConnection(URLConnection connection, List<String> excludeHeaders) {
        for (String key : headersMap.keySet()) {
            if(excludeHeaders != null){
                if(excludeHeaders.contains(key)){
                    continue;
                }
            }
            String value = headersMap.get(key);
            connection.setRequestProperty(key, value);
        }
    }

}
