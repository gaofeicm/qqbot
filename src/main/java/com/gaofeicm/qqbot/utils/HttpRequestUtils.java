package com.gaofeicm.qqbot.utils;

import com.gaofeicm.qqbot.utils.entity.HttpDeleteWithBody;
import lombok.SneakyThrows;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
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

    public static String doRequest(HttpRequestBase http, Map<String, String> header) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
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
}