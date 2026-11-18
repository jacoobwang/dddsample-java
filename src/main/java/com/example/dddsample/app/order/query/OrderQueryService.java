package com.example.dddsample.app.order.query;

import java.util.Optional;

import com.example.dddsample.app.order.query.dto.OrderDetailQueryResult;
import com.example.dddsample.app.order.query.dto.OrderPageQuery;
import com.example.dddsample.shared.PageResult;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public interface OrderQueryService {

    Optional<OrderDetailQueryResult> getById(@NotNull(message = "{100010}") Long id);

    String getStatusById(@NotNull(message = "{100010}") Long id);

    PageResult<OrderDetailQueryResult> queryPage(
        @Min(value = 1, message = "{100010}") int pageNum,
        @Min(value = 1, message = "{100010}") @Max(value = 100, message = "{100010}") int pageSize,
        @Valid OrderPageQuery filter
    );
}
