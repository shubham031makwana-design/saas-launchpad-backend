package saas_launchpad_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import saas_launchpad_backend.entity.UsageLimit;

public interface UsageLimitRepository extends JpaRepository<UsageLimit, Long> {

    Optional<UsageLimit> findByTenantIdAndMetricName(
            Long tenantId,
            String metricName
    );
}