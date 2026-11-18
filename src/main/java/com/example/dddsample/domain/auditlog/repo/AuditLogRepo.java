package com.example.dddsample.domain.auditlog.repo;

import java.util.List;
import java.util.Optional;

import com.example.dddsample.domain.auditlog.AuditLog;

public interface AuditLogRepo {

    Long save(AuditLog auditLog);

    Optional<AuditLog> findById(Long id);

    void deleteById(Long id);

    void batchDeleteByIds(List<Long> ids);
}
