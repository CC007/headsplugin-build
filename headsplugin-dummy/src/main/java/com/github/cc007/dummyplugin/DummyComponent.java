package com.github.cc007.dummyplugin;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Getter
@RequiredArgsConstructor
public class DummyComponent {

    private final boolean doIExist;
}
