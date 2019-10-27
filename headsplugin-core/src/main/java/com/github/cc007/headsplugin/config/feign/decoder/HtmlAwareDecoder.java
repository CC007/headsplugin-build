package com.github.cc007.headsplugin.config.feign.decoder;


import feign.FeignException;
import feign.Response;
import feign.codec.Decoder;
import feign.optionals.OptionalDecoder;
import lombok.val;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

@Component
public class HtmlAwareDecoder implements Decoder {
    private final Decoder decoder;

    public HtmlAwareDecoder(ObjectFactory<HttpMessageConverters> messageConverters) {
        this.decoder = new OptionalDecoder(new ResponseEntityDecoder(new SpringDecoder(messageConverters)));
    }

    @Override
    public Object decode(Response response, Type type) throws IOException, FeignException {
        if (response.headers().get(HttpHeaders.CONTENT_TYPE).contains("text/html")) {
            String body = getBodyFromHtml(response.body().asInputStream());
            val headers = response.headers();
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

        return decoder.decode(response, type);
    }

    private String getBodyFromHtml(InputStream inputStream) throws IOException {
        Document document = Jsoup.parse(inputStream, StandardCharsets.UTF_8.name(), "");
        return document.select("body").text().trim();
    }
}