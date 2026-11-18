package com.example.dddsample.app.auditlog.query.dto;

public record AuditLogPageQuery(
    String module,
    Long operatorId
) {
}
