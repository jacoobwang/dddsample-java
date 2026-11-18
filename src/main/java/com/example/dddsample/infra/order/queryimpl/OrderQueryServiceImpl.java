package com.example.dddsample.infra.order.queryimpl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import com.example.dddsample.app.order.query.OrderQueryService;
import com.example.dddsample.app.order.query.dto.OrderDetailQueryResult;
import com.example.dddsample.app.order.query.dto.OrderPageQuery;
import com.example.dddsample.common.PageResult;
import com.example.dddsample.infra.order.converter.OrderConverter;
import com.example.dddsample.infra.order.dataobject.OrderDO;
import com.example.dddsample.infra.order.mapper.OrderMapper;

import lombok.RequiredArgsConstructor;

@Component
@Validated
@RequiredArgsConstructor
public class OrderQueryServiceImpl implements OrderQueryService {

    private final OrderMapper orderMapper;
    private final OrderConverter orderConverter;

    @Override
    public Optional<OrderDetailQueryResult> getById(Long id) {
        return Optional.ofNullable(orderMapper.selectById(id))
            .map(orderConverter::toDetailQueryResult);
    }

    @Override
    public String getStatusById(Long id) {
        OrderDO entity = orderMapper.selectById(id);
        return entity != null ? entity.getStatus() : null;
    }

    @Override
    public PageResult<OrderDetailQueryResult> queryPage(int pageNum, int pageSize, OrderPageQuery filter) {
        List<OrderDetailQueryResult> matched = orderMapper.selectAll().stream()
            .filter(o -> filter == null || filter.userId() == null || filter.userId().equals(o.getUserId()))
            .filter(o -> filter == null || filter.status() == null || filter.status().equals(o.getStatus()))
            .map(orderConverter::toDetailQueryResult)
            .toList();
        long total = matched.size();
        long pages = (total + pageSize - 1) / pageSize;
        return new PageResult<>(matched, total, pageSize, pageNum, pages);
    }
}
