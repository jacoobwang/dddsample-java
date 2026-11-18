package com.example.dddsample.app.auditlog.command.dto;

import jakarta.validation.constraints.NotBlank;

public record AuditLogSaveCommand(
    @NotBlank(message = "{100010}") String module,
    @NotBlank(message = "{100010}") String action,
    String detail
) {
}
