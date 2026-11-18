package com.example.dddsample.app.auditlog.query.dto;

public record AuditLogQueryResult(
    Long id,
    String module,
    String action,
    Long operatorId,
    String detail,
    Long createdAt
) {
}
