package com.github.cc007.headsplugin.business.services.heads;

import com.github.cc007.headsplugin.integration.daos.heads.interfaces.Searchable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class HeadSearcher {
    @Autowired
    public Map<String, Searchable> searchConnectors;


}
