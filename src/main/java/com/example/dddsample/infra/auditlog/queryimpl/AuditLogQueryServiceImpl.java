package com.example.dddsample.infra.auditlog.queryimpl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import com.example.dddsample.app.auditlog.query.AuditLogQueryService;
import com.example.dddsample.app.auditlog.query.dto.AuditLogPageQuery;
import com.example.dddsample.app.auditlog.query.dto.AuditLogQueryResult;
import com.example.dddsample.common.PageResult;
import com.example.dddsample.infra.auditlog.converter.AuditLogConverter;
import com.example.dddsample.infra.auditlog.mapper.AuditLogMapper;

import lombok.RequiredArgsConstructor;

@Component
@Validated
@RequiredArgsConstructor
public class AuditLogQueryServiceImpl implements AuditLogQueryService {

    private final AuditLogMapper auditLogMapper;
    private final AuditLogConverter auditLogConverter;

    @Override
    public Optional<AuditLogQueryResult> getById(Long id) {
        return Optional.ofNullable(auditLogMapper.selectById(id))
            .map(auditLogConverter::toQueryResult);
    }

    @Override
    public PageResult<AuditLogQueryResult> queryPage(int pageNum, int pageSize, AuditLogPageQuery filter) {
        List<AuditLogQueryResult> matched = auditLogMapper.selectAll().stream()
            .filter(o -> filter == null || filter.module() == null || filter.module().equals(o.getModule()))
            .filter(o -> filter == null || filter.operatorId() == null || filter.operatorId().equals(o.getOperatorId()))
            .map(auditLogConverter::toQueryResult)
            .toList();
        long total = matched.size();
        long pages = (total + pageSize - 1) / pageSize;
        return new PageResult<>(matched, total, pageSize, pageNum, pages);
    }
}
