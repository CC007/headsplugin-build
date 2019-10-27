package com.github.cc007.headsplugin.config.feign.statuscodes;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorDecoderKey {
    private String methodKey;
    private int statusCode;
}
