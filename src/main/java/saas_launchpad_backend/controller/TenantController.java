package saas_launchpad_backend.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import saas_launchpad_backend.entity.Tenant;
import saas_launchpad_backend.service.TenantExportService;
import saas_launchpad_backend.service.TenantService;

@RestController
@RequestMapping("/api/tenants")
public class TenantController {

    @Autowired
    private TenantService tenantService;

    @Autowired
    private TenantExportService tenantExportService;

    // Create Tenant
    @PostMapping
    public Tenant createTenant(@RequestBody Tenant tenant) {
        return tenantService.createTenant(tenant);
    }

    // Get All Tenants
    @GetMapping
    public List<Tenant> getAllTenants() {
        return tenantService.getAllTenants();
    }

    // Get Tenant By ID
    @GetMapping("/{id}")
    public Optional<Tenant> getTenantById(@PathVariable Long id) {
        return tenantService.getTenantById(id);
    }

    // Activate Tenant
    @PutMapping("/{id}/activate")
    public Tenant activateTenant(@PathVariable Long id) {
        return tenantService.activateTenant(id);
    }

    // Deactivate Tenant
    @PutMapping("/{id}/deactivate")
    public Tenant deactivateTenant(@PathVariable Long id) {
        return tenantService.deactivateTenant(id);
    }

    // Export Tenant Data
    @GetMapping("/{id}/export")
    public Map<String, Object> exportTenant(
            @PathVariable Long id) {

        return tenantExportService.exportTenant(id);
    }

    // Delete Tenant
    @DeleteMapping("/{id}")
    public String deleteTenant(@PathVariable Long id) {

        tenantService.deleteTenant(id);

        return "Tenant deleted successfully";
    }
}