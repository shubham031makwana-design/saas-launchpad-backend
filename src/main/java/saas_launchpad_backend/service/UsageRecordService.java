package saas_launchpad_backend.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import saas_launchpad_backend.dto.UsageDashboardDTO;
import saas_launchpad_backend.dto.UsageSummaryDTO;
import saas_launchpad_backend.entity.SubscriptionPlan;
import saas_launchpad_backend.entity.Tenant;
import saas_launchpad_backend.entity.UsageLimit;
import saas_launchpad_backend.entity.UsageRecord;
import saas_launchpad_backend.entity.User;
import saas_launchpad_backend.exception.UsageLimitExceededException;
import saas_launchpad_backend.repository.UsageLimitRepository;
import saas_launchpad_backend.repository.UsageRecordRepository;
import saas_launchpad_backend.repository.UserRepository;

@Service
public class UsageRecordService {

    private final UsageRecordRepository usageRecordRepository;
    private final UsageLimitRepository usageLimitRepository;
    private final UserRepository userRepository;

    public UsageRecordService(
            UsageRecordRepository usageRecordRepository,
            UsageLimitRepository usageLimitRepository,
            UserRepository userRepository) {

        this.usageRecordRepository = usageRecordRepository;
        this.usageLimitRepository = usageLimitRepository;
        this.userRepository = userRepository;
    }

    // ==========================================
    // GET LOGGED-IN USER
    // ==========================================

    private User getLoggedInUser(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Logged-in user not found"));
    }

    // ==========================================
    // GET USER TENANT
    // ==========================================

    private Tenant getUserTenant(String email) {

        User user = getLoggedInUser(email);

        if (user.getTenant() == null) {
            throw new RuntimeException(
                    "User has no tenant");
        }

        return user.getTenant();
    }

    // ==========================================
    // GET SUBSCRIPTION PLAN LIMIT
    // ==========================================

    private Long getSubscriptionLimit(
            Tenant tenant,
            String metricName) {

        SubscriptionPlan subscriptionPlan =
                tenant.getSubscriptionPlan();

        if (subscriptionPlan == null ||
                metricName == null) {

            return null;
        }

        String metric =
                metricName.trim().toUpperCase();

        // API CALL LIMIT
        if ("API_CALLS".equals(metric)) {
            return subscriptionPlan.getApiCallLimit();
        }

        // PAYMENT LIMIT
        if (metric.startsWith("PAYMENTS")) {
            return subscriptionPlan.getPaymentLimit();
        }

        return null;
    }

    // ==========================================
    // GET APPLICABLE LIMIT
    // ==========================================

    private Long getApplicableLimit(
            Tenant tenant,
            String metricName) {

        Long subscriptionLimit =
                getSubscriptionLimit(
                        tenant,
                        metricName);

        // Subscription plan limit has priority
        if (subscriptionLimit != null) {
            return subscriptionLimit;
        }

        UsageLimit usageLimit =
                usageLimitRepository
                        .findByTenantIdAndMetricName(
                                tenant.getId(),
                                metricName)
                        .orElse(null);

        if (usageLimit != null) {
            return usageLimit.getUsageLimit();
        }

        // No configured limit
        return 0L;
    }

    // ==========================================
    // RECORD USAGE
    // ==========================================

    public UsageRecord recordUsage(
            String metricName,
            Long usageCount,
            String email) {

        Tenant tenant = getUserTenant(email);

        // Validate metric name
        if (metricName == null ||
                metricName.trim().isEmpty()) {

            throw new RuntimeException(
                    "Metric name is required");
        }

        // Validate usage count
        if (usageCount == null ||
                usageCount <= 0) {

            throw new RuntimeException(
                    "Usage count must be greater than 0");
        }

        // Get current usage
        Long currentUsage =
                usageRecordRepository.getTotalUsage(
                        tenant.getId(),
                        metricName);

        if (currentUsage == null) {
            currentUsage = 0L;
        }

        // Get configured limit
        Long limit =
                getApplicableLimit(
                        tenant,
                        metricName);

        if (limit == null) {
            limit = 0L;
        }

        // ==========================================
        // CHECK USAGE LIMIT
        // ==========================================

        if (currentUsage + usageCount > limit) {

            throw new UsageLimitExceededException(
                    "Usage limit exceeded for "
                            + metricName
                            + ". Current usage: "
                            + currentUsage
                            + ", Limit: "
                            + limit);
        }

        // ==========================================
        // SAVE USAGE
        // ==========================================

        UsageRecord usageRecord =
                new UsageRecord();

        usageRecord.setTenant(tenant);

        usageRecord.setMetricName(
                metricName.trim().toUpperCase());

        usageRecord.setUsageCount(
                usageCount);

        return usageRecordRepository.save(
                usageRecord);
    }

    // ==========================================
    // GET ALL USAGE RECORDS
    // SAME TENANT ONLY
    // ==========================================

    public List<UsageRecord> getUsageRecords(
            String email) {

        Tenant tenant =
                getUserTenant(email);

        return usageRecordRepository
                .findByTenantId(
                        tenant.getId());
    }

    // ==========================================
    // GET USAGE BY METRIC
    // SAME TENANT ONLY
    // ==========================================

    public List<UsageRecord> getUsageByMetric(
            String metricName,
            String email) {

        Tenant tenant =
                getUserTenant(email);

        return usageRecordRepository
                .findByTenantIdAndMetricName(
                        tenant.getId(),
                        metricName);
    }

    // ==========================================
    // GET TOTAL USAGE
    // SAME TENANT ONLY
    // ==========================================

    public Long getTotalUsage(
            String metricName,
            String email) {

        Tenant tenant =
                getUserTenant(email);

        Long total =
                usageRecordRepository.getTotalUsage(
                        tenant.getId(),
                        metricName);

        if (total == null) {
            return 0L;
        }

        return total;
    }

    // ==========================================
    // GET USAGE DASHBOARD
    // ==========================================

    public UsageDashboardDTO getDashboard(
            String metricName,
            String email) {

        Tenant tenant =
                getUserTenant(email);

        Long currentUsage =
                usageRecordRepository.getTotalUsage(
                        tenant.getId(),
                        metricName);

        if (currentUsage == null) {
            currentUsage = 0L;
        }

        Long limit =
                getApplicableLimit(
                        tenant,
                        metricName);

        if (limit == null) {
            limit = 0L;
        }

        Long remaining =
                limit - currentUsage;

        if (remaining < 0) {
            remaining = 0L;
        }

        return new UsageDashboardDTO(
                metricName,
                currentUsage,
                limit,
                remaining);
    }

    // ==========================================
    // GET USAGE SUMMARY
    // SAME TENANT ONLY
    // ==========================================

    public List<UsageSummaryDTO> getUsageSummary(
            String email) {

        Tenant tenant =
                getUserTenant(email);

        List<UsageRecord> records =
                usageRecordRepository
                        .findByTenantId(
                                tenant.getId());

        List<UsageSummaryDTO> summary =
                new ArrayList<>();

        Set<String> metrics =
                new HashSet<>();

        for (UsageRecord record : records) {

            metrics.add(
                    record.getMetricName());
        }

        for (String metricName : metrics) {

            Long currentUsage =
                    usageRecordRepository
                            .getTotalUsage(
                                    tenant.getId(),
                                    metricName);

            if (currentUsage == null) {
                currentUsage = 0L;
            }

            Long limit =
                    getApplicableLimit(
                            tenant,
                            metricName);

            if (limit == null) {
                limit = 0L;
            }

            Long remaining =
                    limit - currentUsage;

            if (remaining < 0) {
                remaining = 0L;
            }

            summary.add(
                    new UsageSummaryDTO(
                            metricName,
                            currentUsage,
                            limit,
                            remaining));
        }

        return summary;
    }
}