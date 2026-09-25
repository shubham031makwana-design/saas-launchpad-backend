package saas_launchpad_backend.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import saas_launchpad_backend.entity.AuditLog;
import saas_launchpad_backend.entity.FeatureFlag;
import saas_launchpad_backend.entity.Tenant;
import saas_launchpad_backend.entity.UsageRecord;
import saas_launchpad_backend.entity.User;
import saas_launchpad_backend.repository.AuditLogRepository;
import saas_launchpad_backend.repository.FeatureFlagRepository;
import saas_launchpad_backend.repository.TenantRepository;
import saas_launchpad_backend.repository.UsageRecordRepository;
import saas_launchpad_backend.repository.UserRepository;

@Service
public class TenantExportService {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final FeatureFlagRepository featureFlagRepository;
    private final UsageRecordRepository usageRecordRepository;
    private final AuditLogRepository auditLogRepository;

    public TenantExportService(
            TenantRepository tenantRepository,
            UserRepository userRepository,
            FeatureFlagRepository featureFlagRepository,
            UsageRecordRepository usageRecordRepository,
            AuditLogRepository auditLogRepository) {

        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.featureFlagRepository = featureFlagRepository;
        this.usageRecordRepository = usageRecordRepository;
        this.auditLogRepository = auditLogRepository;
    }

    public Map<String, Object> exportTenant(Long tenantId) {

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() ->
                        new RuntimeException("Tenant not found"));

        /*
         * Export users without passwords.
         */
        List<Map<String, Object>> safeUsers =
                new ArrayList<>();

        List<User> users = userRepository.findAll();

        for (User user : users) {

            if (user.getTenant() != null
                    && tenantId.equals(
                            user.getTenant().getId())) {

                Map<String, Object> safeUser =
                        new LinkedHashMap<>();

                safeUser.put("id", user.getId());
                safeUser.put("fullName", user.getFullName());
                safeUser.put("email", user.getEmail());
                safeUser.put("role", user.getRole());

                safeUsers.add(safeUser);
            }
        }

        /*
         * Export feature flags belonging to this tenant.
         */
        List<FeatureFlag> featureFlags =
                featureFlagRepository.findAll()
                .stream()
                .filter(featureFlag ->
                        featureFlag.getTenant() != null
                        && tenantId.equals(
                                featureFlag.getTenant().getId()))
                .toList();

        /*
         * Export usage records belonging to this tenant.
         */
        List<UsageRecord> usageRecords =
                usageRecordRepository.findAll()
                .stream()
                .filter(usageRecord ->
                        usageRecord.getTenant() != null
                        && tenantId.equals(
                                usageRecord.getTenant().getId()))
                .toList();

        /*
         * Export audit logs belonging to this tenant.
         */
        List<AuditLog> auditLogs =
                auditLogRepository
                        .findByTenantIdOrderByTimestampDesc(
                                tenantId);

        /*
         * Build export response.
         */
        Map<String, Object> exportData =
                new LinkedHashMap<>();

        exportData.put("tenant", tenant);
        exportData.put("users", safeUsers);
        exportData.put("featureFlags", featureFlags);
        exportData.put("usageRecords", usageRecords);
        exportData.put("auditLogs", auditLogs);

        return exportData;
    }
}