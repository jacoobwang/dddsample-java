package com.example.dddsample.domain.auditlog;

import java.time.Instant;

public record AuditLog(
    Long id,
    String module,
    String action,
    Long operatorId,
    String detail,
    Long createdAt
) {

    public static AuditLog create(String module, String action, Long operatorId, String detail) {
        return new AuditLog(null, module, action, operatorId, detail, Instant.now().getEpochSecond());
    }

    public static AuditLog restore(Long id, String module, String action, Long operatorId, String detail, Long createdAt) {
        return new AuditLog(id, module, action, operatorId, detail, createdAt);
    }
}
