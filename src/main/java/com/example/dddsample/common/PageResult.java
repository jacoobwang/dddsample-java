package com.example.dddsample.common;

import java.util.List;

public record PageResult<T>(
    List<T> records,
    long total,
    long size,
    long current,
    long pages
) {
}
