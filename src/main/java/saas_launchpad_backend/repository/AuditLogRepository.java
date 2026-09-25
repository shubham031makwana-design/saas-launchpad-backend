package saas_launchpad_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import saas_launchpad_backend.entity.AuditLog;

public interface AuditLogRepository
        extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByTenantIdOrderByTimestampDesc(Long tenantId);

    List<AuditLog> findAllByOrderByTimestampDesc();
}