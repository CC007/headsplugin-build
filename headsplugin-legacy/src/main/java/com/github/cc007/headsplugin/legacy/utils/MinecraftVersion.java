/*
 * The MIT License
 *
 * Copyright 2018 Rik Schaaf aka CC007 (http://coolcat007.nl/).
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

import org.bukkit.Bukkit;

/**
 * @author Rik Schaaf aka CC007 (http://coolcat007.nl/)
 */
public class MinecraftVersion {

    private final int major;
    private final int minor;
    private final int patch;
    private final String packageName;

    public MinecraftVersion() {
        Package obcPackage = Bukkit.getServer().getClass().getPackage();
        String obcPackageName = obcPackage.getName();
        this.packageName = obcPackageName.substring(obcPackageName.lastIndexOf(".") + 1);
        String[] splitPackageName = packageName.split("_");
        this.major = Integer.parseInt(splitPackageName[0].substring(1));
        this.minor = Integer.parseInt(splitPackageName[1]);
        this.patch = Integer.parseInt(splitPackageName[2].substring(1));
    }

    /**
     * Get the value of the major version
     *
     * @return the value of the major version
     */
    public int getMajor() {
        return major;
    }

    /**
     * Get the value of the minor version
     *
     * @return the value of the minor version
     */
    public int getMinor() {
        return minor;
    }

    /**
     * Get the value of the patch version
     *
     * @return the value of the patch version
     */
    public int getPatch() {
        return patch;
    }

    /**
     * Get the package name for this version
     *
     * @return the package name for this version
     */
    public String getPackageName() {
        return packageName;
    }


}
