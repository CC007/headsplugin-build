package com.github.cc007.headsplugin.config.feign.statuscodes;

import com.github.cc007.headsplugin.config.feign.decoder.HtmlAwareDecoder;

import feign.FeignException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Log4j2
@RequiredArgsConstructor
public class StatusCodeHandlingErrorDecoder implements ErrorDecoder {
    private final HtmlAwareDecoder htmlAwareDecoder;
    private final ErrorDecoder defaultErrorDecoder = new ErrorDecoder.Default();
    private final Map<ErrorDecoderKey, Class<?>> errorDecoderActions = new HashMap<>();

    @Value("${headsplugin.suppressHttpClientErrors:#{true}}")
    private boolean suppressHttpClientErrors = true;

    public void addKey(ErrorDecoderKey key, Class<?> aClass) {
        if (!errorDecoderActions.containsKey(key)) {
            errorDecoderActions.put(key, aClass);
        }
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        Exception result;

        val optionalDecoderActionEntry = getDecoderActionEntry(methodKey, response);

        if (optionalDecoderActionEntry.isPresent()) {
            val decoderActionEntry = optionalDecoderActionEntry.get();
            try {
                val resultObject = htmlAwareDecoder.decode(response, decoderActionEntry.getValue());
                result = new ErrorDecoderException(resultObject, resultObject.getClass());
            } catch (IOException | FeignException e) {
                result = e;
            }
        } else {
            logDecoderError(response);
            result = defaultErrorDecoder.decode(methodKey, response);
        }

        return result;
    }

    private Optional<Map.Entry<ErrorDecoderKey, Class<?>>> getDecoderActionEntry(String methodKey, Response response) {
        return errorDecoderActions.entrySet().stream()
                .filter((entry) -> {
                    val errorDecoderKey = entry.getKey();
                    return methodKey.equals(errorDecoderKey.getMethodKey())
                        && response.status() == errorDecoderKey.getStatusCode();
                })
                .findFirst();
    }

    private void logDecoderError(Response response) {
        log.error("Got response with status code: " + response.status() + " (" + response.reason() + ")");
        if(!suppressHttpClientErrors) {
            log.error("For request: " + response.request().url());
            log.error("Request headers:" + getHeaderBuilder(response.request().headers()));
            log.error("Response headers:" + getHeaderBuilder(response.headers()));
            log.error("Body:\n" + response.body().toString());
        }
    }

    private String getHeaderBuilder(Map<String, Collection<String>> headers) {
        val requestHeaderStringBuilder = new StringBuilder();
        headers.forEach((headerKey, headerValues) -> {
            requestHeaderStringBuilder.append("\n - " + headerKey + ": ");
            if (headerValues.size() > 1) {
                requestHeaderStringBuilder.append("[" + String.join(", ", headerValues) + "]");
            } else {
                requestHeaderStringBuilder.append(headerValues.stream().findFirst().get());
            }
        });
        return requestHeaderStringBuilder.toString();
    }
}