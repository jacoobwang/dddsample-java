package com.example.dddsample.infra.auditlog.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;

import com.example.dddsample.infra.auditlog.dataobject.AuditLogDO;

@Component
public class AuditLogMapper {

    private final Map<Long, AuditLogDO> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(5000L);

    public Long insert(AuditLogDO entity) {
        long id = idGenerator.incrementAndGet();
        entity.setId(id);
        store.put(id, entity);
        return id;
    }

    public void updateById(AuditLogDO entity) {
        store.put(entity.getId(), entity);
    }

    public AuditLogDO selectById(Long id) {
        return store.get(id);
    }

    public List<AuditLogDO> selectAll() {
        return new ArrayList<>(store.values());
    }

    public void deleteById(Long id) {
        store.remove(id);
    }
}
