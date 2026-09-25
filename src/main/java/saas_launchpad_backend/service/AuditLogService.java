package saas_launchpad_backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import saas_launchpad_backend.entity.AuditLog;
import saas_launchpad_backend.repository.AuditLogRepository;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(
            String userEmail,
            Long tenantId,
            String action,
            String details) {

        AuditLog auditLog = new AuditLog();

        auditLog.setUserEmail(userEmail);
        auditLog.setTenantId(tenantId);
        auditLog.setAction(action);
        auditLog.setDetails(details);
        auditLog.setTimestamp(LocalDateTime.now());

        auditLogRepository.save(auditLog);
    }

    public List<AuditLog> getAllLogs() {

        return auditLogRepository
                .findAllByOrderByTimestampDesc();
    }

    public List<AuditLog> getTenantLogs(Long tenantId) {

        return auditLogRepository
                .findByTenantIdOrderByTimestampDesc(tenantId);
    }
}