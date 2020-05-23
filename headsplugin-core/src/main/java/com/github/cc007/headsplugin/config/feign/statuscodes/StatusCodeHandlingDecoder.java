package com.github.cc007.headsplugin.config.feign.statuscodes;

import com.github.cc007.headsplugin.config.feign.decoder.HtmlAwareDecoder;

import feign.FeignException;
import feign.Response;
import feign.codec.Decoder;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Primary
@Component
@RequiredArgsConstructor
public class StatusCodeHandlingDecoder implements Decoder {
    private final HtmlAwareDecoder htmlAwareDecoder;

    private final Map<DecoderKey, Class<?>> decoderActions = new HashMap<>();

    public void addKey(DecoderKey decoderKey, Class<?> returnType) {
        if (!decoderActions.containsKey(decoderKey)) {
            decoderActions.put(decoderKey, returnType);
        }
    }

    @Override
    public Object decode(Response response, Type type) throws IOException, FeignException {
        Object result;

        val optionalDecoderActionEntry = getDecoderActionEntry(response);

        if (optionalDecoderActionEntry.isPresent()) {
            val decoderActionEntry = optionalDecoderActionEntry.get();
            decoderActions.remove(decoderActionEntry.getKey());
            return htmlAwareDecoder.decode(response, decoderActionEntry.getValue());
        } else {
            result = htmlAwareDecoder.decode(response, type);
        }

        return result;
    }

    private Optional<Map.Entry<DecoderKey, Class<?>>> getDecoderActionEntry(Response response) {
        return decoderActions.entrySet().stream()
                .filter(entry -> {
                    String regex = entry.getKey()
                            .getPath()
                            .replaceAll("\\?", "\\\\\\?")
                            .replaceAll("\\{([^}]+)}", ".*?");
                    Pattern regexPattern = Pattern.compile("^.*?" + regex + "$");
                    Matcher matcher = regexPattern.matcher(response.request().url());
                    return matcher.matches() && entry.getKey().getStatusCode() == response.status();
                })
                .findFirst();
    }
}
