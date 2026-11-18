package com.example.dddsample.adapter.auditlog.controller;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dddsample.app.auditlog.command.AuditLogCommandService;
import com.example.dddsample.app.auditlog.command.dto.AuditLogSaveCommand;
import com.example.dddsample.app.auditlog.query.AuditLogQueryService;
import com.example.dddsample.app.auditlog.query.dto.AuditLogPageQuery;
import com.example.dddsample.app.auditlog.query.dto.AuditLogQueryResult;
import com.example.dddsample.shared.BaseResponse;
import com.example.dddsample.shared.PageResult;
import com.example.dddsample.shared.RequestContext;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/v1/admin/audit-logs")
@RequiredArgsConstructor
public class AuditLogAdminController {

    private final AuditLogCommandService commandService;
    private final AuditLogQueryService queryService;

    @PostMapping("/create")
    public BaseResponse<Long> create(@RequestBody @Valid AuditLogSaveCommand command) {
        Long operatorId = RequestContext.getUserIdRequired();
        return BaseResponse.success(commandService.create(operatorId, command));
    }

    @PutMapping("/{id}")
    public BaseResponse<Void> updateById(
            @PathVariable @NotNull(message = "{100010}") Long id,
            @RequestBody @Valid AuditLogSaveCommand command) {
        Long operatorId = RequestContext.getUserIdRequired();
        commandService.updateById(id, operatorId, command);
        return BaseResponse.success();
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Void> deleteById(@PathVariable @NotNull(message = "{100010}") Long id) {
        commandService.deleteById(id);
        return BaseResponse.success();
    }

    @PostMapping("/batch-delete")
    public BaseResponse<Void> batchDelete(@RequestBody @NotEmpty(message = "{100010}") List<Long> ids) {
        commandService.batchDelete(ids);
        return BaseResponse.success();
    }

    @GetMapping("/{id}")
    public BaseResponse<AuditLogQueryResult> getDetail(@PathVariable @NotNull(message = "{100010}") Long id) {
        return BaseResponse.success(queryService.getById(id).orElse(null));
    }

    @PostMapping("/{pageNum}/{pageSize}")
    public BaseResponse<PageResult<AuditLogQueryResult>> queryPage(
            @PathVariable @Min(value = 1, message = "{100010}") Integer pageNum,
            @PathVariable @Min(value = 1, message = "{100010}") @Max(value = 100, message = "{100010}") Integer pageSize,
            @RequestBody(required = false) @Valid AuditLogPageQuery filter) {
        return BaseResponse.success(queryService.queryPage(pageNum, pageSize, filter));
    }
}
