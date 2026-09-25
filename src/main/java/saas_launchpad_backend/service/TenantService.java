package saas_launchpad_backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import saas_launchpad_backend.entity.Tenant;
import saas_launchpad_backend.repository.TenantRepository;

@Service
public class TenantService {

    private final TenantRepository tenantRepository;

    TenantService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    // Create Tenant
    public Tenant createTenant(Tenant tenant) {

        if (tenantRepository.existsBySlug(tenant.getSlug())) {
            throw new RuntimeException("Tenant slug already exists");
        }

        return tenantRepository.save(tenant);
    }

    // Get All Tenants
    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }

    // Get Tenant By ID
    public Optional<Tenant> getTenantById(Long id) {
        return tenantRepository.findById(id);
    }

    // Delete Tenant
    public void deleteTenant(Long id) {
        tenantRepository.deleteById(id);
    }

    // Activate Tenant
    public Tenant activateTenant(Long id) {

        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Tenant not found"));

        tenant.setActive(true);

        return tenantRepository.save(tenant);
    }

    // Deactivate Tenant
    public Tenant deactivateTenant(Long id) {

        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Tenant not found"));

        tenant.setActive(false);

        return tenantRepository.save(tenant);
    }
}