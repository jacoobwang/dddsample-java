package com.example.dddsample.infra.auditlog.converter;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.example.dddsample.app.auditlog.query.dto.AuditLogQueryResult;
import com.example.dddsample.domain.auditlog.AuditLog;
import com.example.dddsample.infra.auditlog.dataobject.AuditLogDO;

@Mapper(
    componentModel = "spring",
    unmappedSourcePolicy = ReportingPolicy.WARN,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    typeConversionPolicy = ReportingPolicy.ERROR
)
public interface AuditLogConverter {

    AuditLog toDomain(AuditLogDO entity);

    AuditLogDO toDataObject(AuditLog domain);

    AuditLogQueryResult toQueryResult(AuditLogDO entity);
}
