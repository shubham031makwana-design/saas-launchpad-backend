package saas_launchpad_backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import saas_launchpad_backend.entity.AuditLog;
import saas_launchpad_backend.service.AuditLogService;

@RestController
@RequestMapping("/api/audit-logs")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public List<AuditLog> getAllLogs() {
        return auditLogService.getAllLogs();
    }

    @GetMapping("/tenant/{tenantId}")
    public List<AuditLog> getTenantLogs(
            @PathVariable Long tenantId) {

        return auditLogService.getTenantLogs(tenantId);
    }
}