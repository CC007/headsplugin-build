package com.github.cc007.headsplugin.business.domain;

import java.util.UUID;
import lombok.Data;

@Data
public class Head
{
	private String name;
	private String value;
	private UUID headOwner;
	private String headDatabase;
}
