package saas_launchpad_backend.service;

import org.springframework.stereotype.Service;

import saas_launchpad_backend.entity.FeatureFlag;
import saas_launchpad_backend.entity.SubscriptionPlan;
import saas_launchpad_backend.entity.Tenant;
import saas_launchpad_backend.entity.User;
import saas_launchpad_backend.entity.UsageLimit;
import saas_launchpad_backend.entity.UsageRecord;

import saas_launchpad_backend.repository.FeatureFlagRepository;
import saas_launchpad_backend.repository.UserRepository;
import saas_launchpad_backend.repository.UsageLimitRepository;
import saas_launchpad_backend.repository.UsageRecordRepository;

@Service
public class FeatureUsageService {

    private final UserRepository userRepository;
    private final FeatureFlagRepository featureFlagRepository;
    private final UsageRecordRepository usageRecordRepository;
    private final UsageLimitRepository usageLimitRepository;
    private final AuditLogService auditLogService;

    public FeatureUsageService(
            UserRepository userRepository,
            FeatureFlagRepository featureFlagRepository,
            UsageRecordRepository usageRecordRepository,
            UsageLimitRepository usageLimitRepository,
            AuditLogService auditLogService) {

        this.userRepository = userRepository;
        this.featureFlagRepository = featureFlagRepository;
        this.usageRecordRepository = usageRecordRepository;
        this.usageLimitRepository = usageLimitRepository;
        this.auditLogService = auditLogService;
    }

    private User getLoggedInUser(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Logged-in user not found"));
    }

    private Tenant getUserTenant(String email) {

        User user = getLoggedInUser(email);

        if (user.getTenant() == null) {
            throw new RuntimeException("User has no tenant");
        }

        return user.getTenant();
    }

    public Long getTenantId(String email) {

        Tenant tenant = getUserTenant(email);

        return tenant.getId();
    }

    public boolean isFeatureEnabled(
            String featureName,
            String email) {

        Tenant tenant = getUserTenant(email);

        FeatureFlag featureFlag =
                featureFlagRepository
                        .findByTenantIdAndFeatureName(
                                tenant.getId(),
                                featureName)
                        .orElse(null);

        if (featureFlag == null) {
            return false;
        }

        return featureFlag.isEnabled();
    }

    public boolean useFeature(
            String featureName,
            String email) {

        Tenant tenant = getUserTenant(email);

        FeatureFlag featureFlag =
                featureFlagRepository
                        .findByTenantIdAndFeatureName(
                                tenant.getId(),
                                featureName)
                        .orElse(null);

        if (featureFlag == null || !featureFlag.isEnabled()) {
            return false;
        }

        Long currentUsage =
                usageRecordRepository.getTotalUsage(
                        tenant.getId(),
                        featureName);

        Long applicableLimit =
                getSubscriptionLimit(
                        tenant,
                        featureName);

        if (applicableLimit != null) {

            if (currentUsage >= applicableLimit) {
                return false;
            }

        } else {

            UsageLimit usageLimit =
                    usageLimitRepository
                            .findByTenantIdAndMetricName(
                                    tenant.getId(),
                                    featureName)
                            .orElse(null);

            if (usageLimit != null) {

                if (currentUsage >= usageLimit.getUsageLimit()) {
                    return false;
                }
            }
        }

        /*
         * Record successful feature usage.
         */
        UsageRecord usageRecord = new UsageRecord();

        usageRecord.setTenant(tenant);
        usageRecord.setMetricName(featureName);
        usageRecord.setUsageCount(1L);

        usageRecordRepository.save(usageRecord);

        /*
         * Record successful action in audit log.
         */
        auditLogService.log(
                email,
                tenant.getId(),
                "FEATURE_USED",
                "Feature used: " + featureName);

        return true;
    }

    private Long getSubscriptionLimit(
            Tenant tenant,
            String featureName) {

        SubscriptionPlan subscriptionPlan =
                tenant.getSubscriptionPlan();

        if (subscriptionPlan == null) {
            return null;
        }

        if (featureName == null) {
            return null;
        }

        String metric =
                featureName.trim().toUpperCase();

        if ("API_CALLS".equals(metric)) {

            return subscriptionPlan.getApiCallLimit();
        }

        if (metric.startsWith("PAYMENTS")) {

            return subscriptionPlan.getPaymentLimit();
        }

        return null;
    }
}