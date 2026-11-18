package com.example.dddsample.app.auditlog.command;

import java.util.List;

import com.example.dddsample.app.auditlog.command.dto.AuditLogSaveCommand;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public interface AuditLogCommandService {

    Long create(@NotNull(message = "{100010}") Long operatorId, @NotNull(message = "{100010}") @Valid AuditLogSaveCommand command);

    void updateById(
        @NotNull(message = "{100010}") Long id,
        @NotNull(message = "{100010}") Long operatorId,
        @NotNull(message = "{100010}") @Valid AuditLogSaveCommand command
    );

    void deleteById(@NotNull(message = "{100010}") Long id);

    void batchDelete(@NotEmpty(message = "{100010}") List<Long> ids);
}
