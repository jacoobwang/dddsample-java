package com.example.dddsample.shared;

import java.util.List;

public record PageResult<T>(
    List<T> records,
    long total,
    long size,
    long current,
    long pages
) {
}
