/*
 * The MIT License
 *
 * Copyright 2017 Rik Schaaf aka CC007 (http://coolcat007.nl/).
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
package com.github.cc007.headsplugin.legacy.utils.loader;

import com.github.cc007.headsplugin.legacy.utils.heads.Head;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Rik Schaaf aka CC007 (http://coolcat007.nl/)
 */
public interface DatabaseLoader {

    public abstract List<Head> getHeads(String urlString, String searchTerm) throws MalformedURLException, SocketTimeoutException, IOException;
    
    public abstract Head addHead(String urlString, UUID playerUuid, String headName) throws MalformedURLException, SocketTimeoutException, IOException;
    
    public abstract String getCategoriesUrl() throws UnsupportedOperationException;
    public abstract String getSearchUrl() throws UnsupportedOperationException;
    public abstract String getGenerateUrl() throws UnsupportedOperationException;

}
