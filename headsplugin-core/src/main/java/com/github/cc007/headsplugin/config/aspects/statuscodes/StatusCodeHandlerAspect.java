package com.github.cc007.headsplugin.config.aspects.statuscodes;

import com.github.cc007.headsplugin.config.feign.statuscodes.DecoderKey;
import com.github.cc007.headsplugin.config.feign.statuscodes.ErrorDecoderException;
import com.github.cc007.headsplugin.config.feign.statuscodes.ErrorDecoderKey;
import com.github.cc007.headsplugin.config.feign.statuscodes.StatusCodeHandlingDecoder;
import com.github.cc007.headsplugin.config.feign.statuscodes.StatusCodeHandlingErrorDecoder;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;

@Aspect
@Component
public class StatusCodeHandlerAspect {
    private final StatusCodeHandlingErrorDecoder errorDecoder;
    private final StatusCodeHandlingDecoder decoder;

    public StatusCodeHandlerAspect(StatusCodeHandlingErrorDecoder errorDecoder, StatusCodeHandlingDecoder decoder) {
        this.errorDecoder = errorDecoder;
        this.decoder = decoder;
    }

    @Around("execution(public * com.github.cc007.headsplugin.integration.rest.clients.MineSkinClient.create (..))")
    public Object handleStatusCodes(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        for (StatusCodeHandler statusCodeHandler : method.getAnnotationsByType(StatusCodeHandler.class)) {
            if (statusCodeHandler.statusCode() / 100 == 2) {
                RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                for (String requestPath : requestMapping.value()) {
                    decoder.addKey(new DecoderKey(requestPath, statusCodeHandler.statusCode()), statusCodeHandler.returnType());
                }
            } else {
                errorDecoder.addKey(new ErrorDecoderKey(method.getName(), statusCodeHandler.statusCode()), statusCodeHandler.returnType());
            }
        }

        try {
            return joinPoint.proceed();
        } catch (ErrorDecoderException ex) {
            return ex.getObj();
        }
    }
}
