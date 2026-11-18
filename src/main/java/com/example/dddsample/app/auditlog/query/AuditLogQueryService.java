package com.example.dddsample.app.auditlog.query;

import java.util.Optional;

import com.example.dddsample.app.auditlog.query.dto.AuditLogPageQuery;
import com.example.dddsample.app.auditlog.query.dto.AuditLogQueryResult;
import com.example.dddsample.common.PageResult;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public interface AuditLogQueryService {

    Optional<AuditLogQueryResult> getById(@NotNull(message = "{100010}") Long id);

    PageResult<AuditLogQueryResult> queryPage(
        @Min(value = 1, message = "{100010}") int pageNum,
        @Min(value = 1, message = "{100010}") @Max(value = 100, message = "{100010}") int pageSize,
        @Valid AuditLogPageQuery filter
    );
}
