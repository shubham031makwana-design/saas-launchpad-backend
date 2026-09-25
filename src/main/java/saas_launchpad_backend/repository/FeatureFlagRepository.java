package saas_launchpad_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import saas_launchpad_backend.entity.FeatureFlag;

public interface FeatureFlagRepository
        extends JpaRepository<FeatureFlag, Long> {

    List<FeatureFlag> findByTenantId(Long tenantId);

    Optional<FeatureFlag> findByIdAndTenantId(
            Long id,
            Long tenantId
    );

    Optional<FeatureFlag> findByTenantIdAndFeatureName(
            Long tenantId,
            String featureName
    );
}