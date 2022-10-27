package com.gaofeicm.qqbot.utils;

import com.gaofeicm.qqbot.utils.entity.HttpDeleteWithBody;
import lombok.SneakyThrows;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BestMatchSpecFactory;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

public class HttpRequestUtils {

    /**
     * @Description: 发送get请求
     */
    public static String doGet(String url) {
        return doRequest(new HttpGet(url), null);
    }

    public static String doGet(String url, Map<String, String> header) {
        return doRequest(new HttpGet(url), header);
    }

    /**
     * @Description: 发送http post请求
     */
    @SneakyThrows
    public static String doPost(String url, String jsonStr, Map<String, String> header) {
        HttpPost http = new HttpPost(url);
        http.setEntity(new StringEntity(jsonStr));
        return doRequest(http, header);
    }

    /**
     * @Description: 发送http post请求
     */
    @SneakyThrows
    public static String doPost(String url, Map<String, String> header) {
        HttpPost http = new HttpPost(url);
        return doRequest(http, header);
    }

    /**
     * @Description: 发送http post请求
     */
    @SneakyThrows
    public static String doPost(String url, String jsonStr) {
        HttpPost http = new HttpPost(url);
        http.setEntity(new StringEntity(jsonStr));
        return doRequest(http, null);
    }

    @SneakyThrows
    public static String doDelete(String url, String jsonStr, Map<String, String> header) {
        HttpDeleteWithBody http = new HttpDeleteWithBody(url);
        http.setEntity(new StringEntity(jsonStr));
        return doRequest(http, header);
    }

    @SneakyThrows
    public static String doPut(String url, String jsonStr, Map<String, String> header) {
        HttpPut http = new HttpPut(url);
        http.setEntity(new StringEntity(jsonStr));
        return doRequest(http, header);
    }

    public static String doRequest(HttpRequestBase http, Map<String, String> header) {
        CookieSpecProvider easySpecProvider = context -> new BrowserCompatSpec() {
            @Override
            public void validate(Cookie cookie, CookieOrigin origin) {
            }
        };
        Registry<CookieSpecProvider> r = RegistryBuilder.<CookieSpecProvider> create()
                .register(CookieSpecs.BEST_MATCH, new BestMatchSpecFactory())
                .register(CookieSpecs.BROWSER_COMPATIBILITY,
                        new BrowserCompatSpecFactory())
                .register("easy", easySpecProvider).build();
        BasicCookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).setDefaultCookieSpecRegistry(r).build();
        http.setHeader("Content-type", "application/json");
        http.setHeader("DataEncoding", "UTF-8");
        if(header != null) {
            header.forEach(http::setHeader);
        }
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(35000).setConnectionRequestTimeout(35000).setSocketTimeout(60000).build();
        http.setConfig(requestConfig);
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(http);
            HttpEntity entity = httpResponse.getEntity();
            if (httpResponse.getStatusLine().getStatusCode() != 200) {
                return null;
            }
            return EntityUtils.toString(entity);
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @SneakyThrows
    public static String get(String url){
        URL realUrl = new URL(url);
        HttpsURLConnection conn = (HttpsURLConnection) realUrl.openConnection();
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.setDoOutput(false);
        conn.setUseCaches(false);
        conn.setRequestProperty("Connection", "close");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
        StringBuffer sb = new StringBuffer();
        String line = "";
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    @SneakyThrows
    public static String post(String url){
        HttpClient httpClient = HttpClientFactory.getHttpsClient();
        HttpPost request = new HttpPost(url);
        HttpResponse httpResponse = httpClient.execute(request);
        String resultStr = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
        httpResponse.getEntity().getContent().close();
        return resultStr;
    }

    public class HttpClientFactory {
        private static CloseableHttpClient client;
        public static HttpClient getHttpsClient() throws Exception {
            if (client != null) {
                return client;
            }
            SSLContext sslcontext = SSLContexts.custom().useSSL().build();
            sslcontext.init(null, new X509TrustManager[]{new HttpsTrustManager()}, new SecureRandom());
            SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(sslcontext,new String[] { "SSLv3", "TLSv1", "TLSv1.1", "TLSv1.2" }, null,
                    SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            client = HttpClients.custom().setSSLSocketFactory(factory).build();
            return client;
        }
    }

    public static class HttpsTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }
}
