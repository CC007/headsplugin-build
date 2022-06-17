package com.github.cc007.headsplugin.integration.rest.feign.decoders.statuscode;


import feign.FeignException;
import feign.Response;
import feign.codec.Decoder;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;

@RequiredArgsConstructor
public class StatusCodeHandlerDecoder implements Decoder {
    private final Decoder decoder;

    @Override
    public Object decode(Response response, Type defaultType) throws IOException, FeignException {
        final var returnType = getReturnType(response, defaultType);
        return decoder.decode(response, returnType);

    }

    private Type getReturnType(Response response, Type defaultType) {
        return Arrays.stream(getStatusCodeHandlers(response))
                .filter(statusCodeHandler -> statusCodeHandler.statusCode() == response.status())
                .map(StatusCodeHandler::returnType)
                .map(Type.class::cast)
                .findAny()
                .orElse(defaultType);
    }

    private StatusCodeHandler[] getStatusCodeHandlers(Response response) {
        return response.request()
                .requestTemplate()
                .methodMetadata()
                .method()
                .getAnnotationsByType(StatusCodeHandler.class);
    }

}