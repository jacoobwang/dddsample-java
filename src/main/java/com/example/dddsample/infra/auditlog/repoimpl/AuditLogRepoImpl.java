package com.example.dddsample.infra.auditlog.repoimpl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.dddsample.domain.auditlog.AuditLog;
import com.example.dddsample.domain.auditlog.repo.AuditLogRepo;
import com.example.dddsample.infra.auditlog.converter.AuditLogConverter;
import com.example.dddsample.infra.auditlog.dataobject.AuditLogDO;
import com.example.dddsample.infra.auditlog.mapper.AuditLogMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AuditLogRepoImpl implements AuditLogRepo {

    private final AuditLogMapper auditLogMapper;
    private final AuditLogConverter auditLogConverter;

    @Override
    public Long save(AuditLog auditLog) {
        AuditLogDO entity = auditLogConverter.toDataObject(auditLog);
        if (entity.getId() == null) {
            return auditLogMapper.insert(entity);
        }
        auditLogMapper.updateById(entity);
        return entity.getId();
    }

    @Override
    public Optional<AuditLog> findById(Long id) {
        return Optional.ofNullable(auditLogMapper.selectById(id))
            .map(auditLogConverter::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        auditLogMapper.deleteById(id);
    }

    @Override
    public void batchDeleteByIds(List<Long> ids) {
        if (ids != null) {
            ids.forEach(auditLogMapper::deleteById);
        }
    }
}
