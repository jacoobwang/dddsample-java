package com.example.dddsample.infra.order.repoimpl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.dddsample.domain.order.Order;
import com.example.dddsample.domain.order.repo.OrderRepo;
import com.example.dddsample.infra.order.converter.OrderConverter;
import com.example.dddsample.infra.order.dataobject.OrderDO;
import com.example.dddsample.infra.order.mapper.OrderMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderRepoImpl implements OrderRepo {

    private final OrderMapper orderMapper;
    private final OrderConverter orderConverter;

    @Override
    public Long save(Order order) {
        OrderDO entity = orderConverter.toDataObject(order);
        if (entity.getId() == null) {
            return orderMapper.insert(entity);
        }
        orderMapper.updateById(entity);
        return entity.getId();
    }

    @Override
    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(orderMapper.selectById(id))
            .map(orderConverter::toDomain);
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        return orderMapper.selectByUserId(userId).stream()
            .map(orderConverter::toDomain)
            .toList();
    }

    @Override
    public void deleteById(Long id) {
        orderMapper.deleteById(id);
    }
}
