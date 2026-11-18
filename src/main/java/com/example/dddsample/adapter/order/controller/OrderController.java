package com.example.dddsample.adapter.order.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dddsample.adapter.order.converter.OrderAdapterConverter;
import com.example.dddsample.adapter.order.dto.OrderDisplayResponse;
import com.example.dddsample.app.order.command.OrderCommandService;
import com.example.dddsample.app.order.command.dto.OrderCreateCommand;
import com.example.dddsample.app.order.command.dto.OrderShipCommand;
import com.example.dddsample.app.order.query.OrderQueryService;
import com.example.dddsample.app.order.query.dto.OrderDetailQueryResult;
import com.example.dddsample.app.order.query.dto.OrderPageQuery;
import com.example.dddsample.shared.BaseResponse;
import com.example.dddsample.shared.PageResult;
import com.example.dddsample.shared.RequestContext;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderCommandService orderCommandService;
    private final OrderQueryService orderQueryService;
    private final OrderAdapterConverter orderAdapterConverter;

    @PostMapping("/create")
    public BaseResponse<Long> create(@RequestBody @Valid OrderCreateCommand command) {
        Long userId = RequestContext.getUserIdRequired();
        return BaseResponse.success(orderCommandService.create(userId, command));
    }

    @PostMapping("/{id}/pay")
    public BaseResponse<String> pay(@PathVariable @NotNull(message = "{100010}") Long id) {
        Long userId = RequestContext.getUserIdRequired();
        return BaseResponse.success(orderCommandService.pay(id, userId));
    }

    @PutMapping("/{id}/ship")
    public BaseResponse<Void> ship(
            @PathVariable @NotNull(message = "{100010}") Long id,
            @RequestBody @Valid OrderShipCommand command) {
        orderCommandService.ship(id, command);
        return BaseResponse.success();
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Void> deleteById(@PathVariable @NotNull(message = "{100010}") Long id) {
        orderCommandService.deleteById(id);
        return BaseResponse.success();
    }

    @GetMapping("/{id}")
    public BaseResponse<OrderDetailQueryResult> getDetail(@PathVariable @NotNull(message = "{100010}") Long id) {
        return BaseResponse.success(orderQueryService.getById(id).orElse(null));
    }

    @GetMapping("/{id}/display")
    public BaseResponse<OrderDisplayResponse> getDisplayDetail(@PathVariable @NotNull(message = "{100010}") Long id) {
        return BaseResponse.success(
            orderQueryService.getById(id)
                .map(orderAdapterConverter::toDisplayResponse)
                .orElse(null)
        );
    }

    @PostMapping("/{pageNum}/{pageSize}")
    public BaseResponse<PageResult<OrderDetailQueryResult>> queryPage(
            @PathVariable @Min(value = 1, message = "{100010}") Integer pageNum,
            @PathVariable @Min(value = 1, message = "{100010}") @Max(value = 100, message = "{100010}") Integer pageSize,
            @RequestBody(required = false) @Valid OrderPageQuery filter) {
        return BaseResponse.success(orderQueryService.queryPage(pageNum, pageSize, filter));
    }
}
