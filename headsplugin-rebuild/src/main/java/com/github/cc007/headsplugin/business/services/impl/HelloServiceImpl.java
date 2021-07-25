package com.github.cc007.headsplugin.business.services.impl;

import com.github.cc007.headsplugin.business.services.HelloService;

import lombok.RequiredArgsConstructor;

import javax.inject.Inject;

@RequiredArgsConstructor
public class HelloServiceImpl implements HelloService {
    @Override
    public String getGreeting() {
        return "Hello World!";
    }
}
