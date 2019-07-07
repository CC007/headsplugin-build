package com.github.cc007.headsplugin.config.feign.statuscodes;

import lombok.Getter;

@Getter
public class ErrorDecoderException extends RuntimeException
{
	Object obj;
	Class<?> objType;

	public ErrorDecoderException(Object obj, Class<?> objType)
	{
		super();
		this.obj = obj;
		this.objType = objType;
	}
}
