package com.github.cc007.headsplugin.business.services.heads;

import com.github.cc007.headsplugin.integration.daos.heads.interfaces.Searchable;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HeadQueryService
{
	@Autowired
	public Map<String, Searchable> searchConnectors;


}
