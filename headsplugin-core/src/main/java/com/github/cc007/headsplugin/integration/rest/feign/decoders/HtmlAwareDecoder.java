package com.github.cc007.headsplugin.integration.rest.feign.decoders;


import feign.FeignException;
import feign.Response;
import feign.codec.Decoder;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;

import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static java.util.Collections.unmodifiableCollection;
import static java.util.Collections.unmodifiableMap;

@RequiredArgsConstructor
public class HtmlAwareDecoder implements Decoder {
    private final Decoder decoder;

    @Override
    public Object decode(Response response, Type type) throws IOException, FeignException {
        final var headers = response.headers();
        final var contentTypeHeader = headers.get(CONTENT_TYPE);
        if (contentTypeHeader == null || !contentTypeHeader.contains("text/html")) {
            return decoder.decode(response, type);
        }

        String body = getBodyFromHtml(response.body().asInputStream());

        final var modifiedContentTypeHeader = new HashSet<>(contentTypeHeader);
        modifiedContentTypeHeader.remove("text/html");
        modifiedContentTypeHeader.add("application/json");

        final var modifiedHeaders = new HashMap<>(headers);
        modifiedHeaders.put(CONTENT_TYPE, unmodifiableCollection(modifiedContentTypeHeader));

        return decoder.decode(
                response.toBuilder()
                        .body(body, StandardCharsets.UTF_8)
                        .headers(unmodifiableMap(modifiedHeaders))
                        .build(),
                type
        );

    }

    private String getBodyFromHtml(InputStream inputStream) throws IOException {
        Document document = Jsoup.parse(inputStream, StandardCharsets.UTF_8.name(), "");
        String body = document.select("body").text().trim();
        if (body.startsWith("No results")) {
            return "[]";
        }
        return body;
    }
}