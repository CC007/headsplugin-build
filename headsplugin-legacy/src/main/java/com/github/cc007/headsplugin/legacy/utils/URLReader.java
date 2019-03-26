/*
 * The MIT License
 *
 * Copyright 2015 Rik Schaaf aka CC007 (http://coolcat007.nl/).
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

import com.github.cc007.headsplugin.legacy.HeadsPlugin;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.logging.Level;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;

/**
 *
 * @author Rik Schaaf aka CC007 (http://coolcat007.nl/)
 */
public class URLReader {
    
    public static String readUrl(String urlString, String contentType, String requestMethod) throws MalformedURLException, SocketTimeoutException, IOException {
        HttpURLConnection request = null;
        try {
            URL url = new URL(urlString);
            request = (HttpURLConnection) url.openConnection();

            try {
                request.setRequestMethod(requestMethod);
            } catch (ProtocolException ex) {
                Bukkit.getLogger().log(Level.SEVERE, null, ex);
            }

            request.setDoOutput(true);
            request.setDoInput(true);
            request.setInstanceFollowRedirects(false);
            request.setRequestProperty("Content-Type", contentType);
            request.setRequestProperty("charset", "utf-8");
            request.setUseCaches(false);
            request.setConnectTimeout(HeadsPlugin.getHeadsPlugin().getConfig().getInt("connectiontimeout"));

            int responseCode = request.getResponseCode();

            if (responseCode == 200) {
                String encoding = request.getContentEncoding();
                encoding = encoding == null ? "UTF-8" : encoding;
                try {
                    return IOUtils.toString(request.getInputStream(), encoding);
                } catch (IOException ex) {
                    Bukkit.getLogger().log(Level.SEVERE, null, ex);
                }
            } else {
                throw new UnknownHostException("The website didn't return the heads. The url has probably been incorrectly set.\n Url: " + urlString + "\n response code: " + responseCode);
            }
        } finally {

            if (request != null) {
                request.disconnect();
            }
        }
        return null;
    }
}
