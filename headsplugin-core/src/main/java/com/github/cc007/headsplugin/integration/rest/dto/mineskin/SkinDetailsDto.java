package com.github.cc007.headsplugin.integration.rest.dto.mineskin;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class SkinDetailsDto
{
	private long id;
	private String name;
	private String model;
	private SkinDataDto data;
	private long timestamp;
	private long duration;
	private long accountId;

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private boolean _private;
	private int views;
	private int nextRequest;


	public boolean isPrivate()
	{
		return _private;
	}

	public void setPrivate(boolean _private)
	{
		this._private = _private;
	}


}