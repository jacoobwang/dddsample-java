package com.example.dddsample.infra.auditlog.dataobject;

import java.io.Serial;
import java.io.Serializable;

import lombok.Data;

@Data
public class AuditLogDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String module;
    private String action;
    private Long operatorId;
    private String detail;
    private Long createdAt;
}
