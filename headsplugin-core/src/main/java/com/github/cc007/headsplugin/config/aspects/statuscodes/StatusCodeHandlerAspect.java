package com.github.cc007.headsplugin.config.aspects.statuscodes;

import com.github.cc007.headsplugin.config.feign.statuscodes.ErrorDecoderException;
import com.github.cc007.headsplugin.config.feign.statuscodes.ErrorDecoderKey;
import com.github.cc007.headsplugin.config.feign.statuscodes.StatusCodeHandlingErrorDecoder;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class StatusCodeHandlerAspect {
    private final StatusCodeHandlingErrorDecoder decoder;

    public StatusCodeHandlerAspect(StatusCodeHandlingErrorDecoder decoder) {
        this.decoder = decoder;
    }

    @Around("@annotation(com.github.cc007.headsplugin.config.aspects.statuscodes.StatusCodeHandler)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        StatusCodeHandler statusCodeHandler = method.getAnnotation(StatusCodeHandler.class);

        decoder.addKey(new ErrorDecoderKey(method.getName(), statusCodeHandler.statusCode()), statusCodeHandler.returnType());
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (ErrorDecoderException ex) {
            result = ex.getObj();
        }
        return result;
    }
}
