package com.example.dddsample.app.auditlog.command.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.example.dddsample.app.auditlog.command.AuditLogCommandService;
import com.example.dddsample.app.auditlog.command.dto.AuditLogSaveCommand;
import com.example.dddsample.domain.auditlog.AuditLog;
import com.example.dddsample.domain.auditlog.repo.AuditLogRepo;

import lombok.RequiredArgsConstructor;

@Service
@Validated
@RequiredArgsConstructor
public class AuditLogCommandServiceImpl implements AuditLogCommandService {

    private final AuditLogRepo auditLogRepo;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(Long operatorId, AuditLogSaveCommand command) {
        AuditLog log = AuditLog.create(command.module(), command.action(), operatorId, command.detail());
        return auditLogRepo.save(log);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateById(Long id, Long operatorId, AuditLogSaveCommand command) {
        auditLogRepo.findById(id).ifPresent(existing -> {
            AuditLog updated = AuditLog.restore(
                id,
                command.module(),
                command.action(),
                operatorId,
                command.detail(),
                existing.createdAt()
            );
            auditLogRepo.save(updated);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        auditLogRepo.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(List<Long> ids) {
        auditLogRepo.batchDeleteByIds(ids);
    }
}
