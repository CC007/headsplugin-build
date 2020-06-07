package com.github.cc007.headsplugin.config.aspects.profiler;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;

@Aspect
@Component
@Slf4j
public class ProfilerAspect {
    @Around("@annotation(com.github.cc007.headsplugin.config.aspects.profiler.Profiler)")
    public Object handleStatusCodes(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long end = System.currentTimeMillis();
        double duration = (end - start) / 1000.0;

        logDuration(joinPoint, duration);
        return result;
    }

    private void logDuration(ProceedingJoinPoint joinPoint, double duration) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Profiler profiler = method.getAnnotation(Profiler.class);

        String formattedString = String.format("%s in %.3fs.", profiler.message(), duration);
        switch (profiler.logLevel()){
            case ERROR:
                log.error(formattedString);
                break;
            case WARN:
                log.warn(formattedString);
                break;
            case INFO:
                log.info(formattedString);
                break;
            case DEBUG:
                log.debug(formattedString);
                break;
            case TRACE:
                log.trace(formattedString);
                break;
        }
    }
}
