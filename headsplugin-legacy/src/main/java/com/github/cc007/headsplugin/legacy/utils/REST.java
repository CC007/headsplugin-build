/*
 * The MIT License
 *
 * Copyright 2015 Rik Schaaf aka CC007 <Coolcat007.nl>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.cc007.headsplugin.legacy.utils;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.bukkit.Bukkit;

import javax.net.ssl.HttpsURLConnection;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * @author Rik Schaaf aka CC007 (Coolcat007.nl)
 */
public class REST {

    public static String get(String host, String restPath, String restEndpoint, String restParam) throws ConnectTimeoutException, SocketTimeoutException {
        String result = "";

        try {
            HttpClient client
                    = HttpClientBuilder.create()
                    .setDefaultRequestConfig(
                            RequestConfig.custom()
                                    .setConnectTimeout(5000)
                                    .setConnectionRequestTimeout(5000)
                                    .setSocketTimeout(5000)
                                    .build())
                    .build();
            HttpGet get = new HttpGet(host + "/" + restPath + "/" + restEndpoint + restParam);
            HttpResponse response = client.execute(get);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();

                Reader reader = new InputStreamReader(content);

                StringWriter writer = new StringWriter();
                IOUtils.copy(reader, writer);

                result = writer.toString();

                content.close();
            }
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public static String get(String host, String restPath, String restEndpoint, Map<String, String> params, boolean ssl) throws ConnectException, ConnectTimeoutException, SocketTimeoutException {
        String result = "";
        if (!ssl) {
            try {
                HttpClient client
                        = HttpClientBuilder.create()
                        .setDefaultRequestConfig(
                                RequestConfig.custom()
                                        .setConnectTimeout(5000)
                                        .setConnectionRequestTimeout(5000)
                                        .setSocketTimeout(5000)
                                        .build())
                        .build();
                StringBuilder sb = new StringBuilder()
                        .append(host)
                        .append("/")
                        .append(restPath)
                        .append("/")
                        .append(restEndpoint);
                if (!params.isEmpty()) {
                    sb.append("?");
                    for (Map.Entry<String, String> entrySet : params.entrySet()) {
                        sb.append(entrySet.getKey()).append("=").append(entrySet.getValue()).append("&");
                    }
                    sb.setLength(sb.length() - 1);
                }
                HttpGet get = new HttpGet(sb.toString());
                HttpResponse response = client.execute(get);
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == 200) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();

                    Reader reader = new InputStreamReader(content);

                    StringWriter writer = new StringWriter();
                    IOUtils.copy(reader, writer);

                    result = writer.toString();

                    content.close();
                }
            } catch (IOException ex) {
                Bukkit.getLogger().log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                //generate url
                StringBuilder sb = new StringBuilder()
                        .append(host)
                        .append("/")
                        .append(restPath)
                        .append("/")
                        .append(restEndpoint);
                if (!params.isEmpty()) {
                    sb.append("?");
                    for (Map.Entry<String, String> entrySet : params.entrySet()) {
                        sb.append(entrySet.getKey()).append("=").append(entrySet.getValue()).append("&");
                    }
                    sb.setLength(sb.length() - 1);
                }
                URL url = new URL(sb.toString());

                //open connection
                HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

                //add request header
                con.setRequestMethod("GET");
                con.setRequestProperty("content-type", "application/json");

                // set timeout
                con.setConnectTimeout(5000);
                con.setReadTimeout(5000);
                //get result
                int responseCode = con.getResponseCode();
                if (responseCode == 200) {
                    InputStream ins = con.getInputStream();
                    InputStreamReader isr = new InputStreamReader(ins);

                    StringWriter writer = new StringWriter();
                    IOUtils.copy(isr, writer);

                    result = writer.toString();
                }

            } catch (IOException ex) {
                Bukkit.getLogger().log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }

    public static String post(String host, String restPath, String restEndpoint, String restParam) throws ConnectTimeoutException, SocketTimeoutException {
        String result = "";
        try {
            HttpClient client
                    = HttpClientBuilder.create()
                    .setDefaultRequestConfig(
                            RequestConfig.custom()
                                    .setConnectTimeout(5000)
                                    .setConnectionRequestTimeout(5000)
                                    .setSocketTimeout(5000)
                                    .build())
                    .build();
            HttpPost post = new HttpPost(host + "/" + restPath + "/" + restEndpoint + "/" + restParam);
            HttpResponse response = client.execute(post);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();

                Reader reader = new InputStreamReader(content);

                StringWriter writer = new StringWriter();
                IOUtils.copy(reader, writer);

                result = writer.toString();

                content.close();
            }
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public static String post(String host, String restPath, String restEndpoint, Map<String, String> params) throws ConnectTimeoutException, SocketTimeoutException {
        String result = "";
        try {
            HttpClient client
                    = HttpClientBuilder.create()
                    .setDefaultRequestConfig(
                            RequestConfig.custom()
                                    .setConnectTimeout(5000)
                                    .setConnectionRequestTimeout(5000)
                                    .setSocketTimeout(5000)
                                    .build())
                    .build();
            HttpPost post = new HttpPost(host + "/" + restPath + "/" + restEndpoint);
            List<NameValuePair> postParams = new ArrayList<>();
            for (Map.Entry<String, String> entrySet : params.entrySet()) {
                postParams.add(new BasicNameValuePair(entrySet.getKey(), entrySet.getValue()));
            }
            post.setEntity(new UrlEncodedFormEntity(postParams));
            HttpResponse response = client.execute(post);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();

                Reader reader = new InputStreamReader(content);

                StringWriter writer = new StringWriter();
                IOUtils.copy(reader, writer);

                result = writer.toString();

                content.close();
            }
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, null, ex);
        }
        return result;
    }

}
