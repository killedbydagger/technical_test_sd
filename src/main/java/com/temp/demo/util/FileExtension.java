package com.temp.demo.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileExtension {
    PNG("png"),
    JPEG("jpg");

    private final String alias;
}
