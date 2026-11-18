package com.example.dddsample.infra.order.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;

import com.example.dddsample.infra.order.dataobject.OrderDO;

@Component
public class OrderMapper {

    private final Map<Long, OrderDO> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1000L);

    public Long insert(OrderDO entity) {
        long id = idGenerator.incrementAndGet();
        entity.setId(id);
        store.put(id, entity);
        return id;
    }

    public void updateById(OrderDO entity) {
        store.put(entity.getId(), entity);
    }

    public OrderDO selectById(Long id) {
        return store.get(id);
    }

    public List<OrderDO> selectByUserId(Long userId) {
        return store.values().stream()
            .filter(o -> userId.equals(o.getUserId()))
            .toList();
    }

    public List<OrderDO> selectAll() {
        return new ArrayList<>(store.values());
    }

    public void deleteById(Long id) {
        store.remove(id);
    }
}
