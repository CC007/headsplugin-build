package com.github.cc007.headsplugin.config.properties;

import lombok.Data;

@Data
public class ConfigProperties {

    private String version;
    private HeadspluginProperties headsplugin = new HeadspluginProperties();

}
