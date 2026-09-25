package saas_launchpad_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import saas_launchpad_backend.entity.Tenant;

public interface TenantRepository extends JpaRepository<Tenant, Long> {

    boolean existsBySlug(String slug);

}