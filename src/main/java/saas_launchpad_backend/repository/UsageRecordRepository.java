package saas_launchpad_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import saas_launchpad_backend.entity.UsageRecord;

public interface UsageRecordRepository extends JpaRepository<UsageRecord, Long> {

    List<UsageRecord> findByTenantId(Long tenantId);

    List<UsageRecord> findByTenantIdAndMetricName(
            Long tenantId,
            String metricName
    );

    @Query("""
           SELECT COALESCE(SUM(u.usageCount), 0)
           FROM UsageRecord u
           WHERE u.tenant.id = :tenantId
           AND u.metricName = :metricName
           """)
    Long getTotalUsage(
            @Param("tenantId") Long tenantId,
            @Param("metricName") String metricName
    );

}