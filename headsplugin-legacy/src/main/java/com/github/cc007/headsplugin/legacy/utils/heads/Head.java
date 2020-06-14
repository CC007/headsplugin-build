/*
 * The MIT License
 *
 * Copyright 2015 Rik Schaaf aka CC007 <http://coolcat007.nl/>.
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
package com.github.cc007.headsplugin.legacy.utils.heads;

import java.util.UUID;

/**
 * @author Rik Schaaf aka CC007 (http://coolcat007.nl/)
 */
public class Head {

    private String name;
    private String value;
    private UUID headOwner;

    /**
     * Create a new head based on a name, headOwner and base64 encoded texture url
     *
     * @param name      the value of name
     * @param value     the base64 encoded value with the head texture url
     * @param headOwner the value of headOwner
     */
    public Head(String name, String value, UUID headOwner) {
        this.name = name;
        this.value = value;
        this.headOwner = headOwner;
    }

    /**
     * Get the value of name
     *
     * @return the value of name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the value of name
     *
     * @param name new value of name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the value of value
     *
     * @return the base64 encoded value with the head texture url
     */
    public String getValue() {
        return value;
    }

    /**
     * Set the value of value
     *
     * @param value new base64 encoded value with the head texture url
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Get the value of headOwner
     *
     * @return the value of headOwner
     */
    public UUID getHeadOwner() {
        return headOwner;
    }

    /**
     * Set the value of headOwner
     *
     * @param headOwner new value of headOwner
     */
    public void setHeadOwner(UUID headOwner) {
        this.headOwner = headOwner;
    }

    @Override
    public String toString() {
        return "Head{" + "name=" + name + ", value=" + value + ", headOwner=" + headOwner + '}';
    }

}
