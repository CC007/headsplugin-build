package com.github.cc007.headsplugin.integration.rest.feign.decoders;


import com.google.common.net.HttpHeaders;
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

@RequiredArgsConstructor
public class HtmlAwareDecoder implements Decoder {
    private final Decoder decoder;

    @Override
    public Object decode(Response response, Type type) throws IOException, FeignException {
        if (!response.headers().get(HttpHeaders.CONTENT_TYPE).contains("text/html")) {
            return decoder.decode(response, type);
        }

        String body = getBodyFromHtml(response.body().asInputStream());
        final var headers = response.headers();
        headers.get(HttpHeaders.CONTENT_TYPE).remove("text/html");
        headers.get(HttpHeaders.CONTENT_TYPE).add("application/json");

        return decoder.decode(
                response.toBuilder()
                        .body(body, StandardCharsets.UTF_8)
                        .headers(headers)
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