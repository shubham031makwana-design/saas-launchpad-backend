package saas_launchpad_backend.dto;

public class UsageSummaryDTO {

    private String metricName;
    private Long currentUsage;
    private Long usageLimit;
    private Long remainingUsage;

    public UsageSummaryDTO() {
    }

    public UsageSummaryDTO(
            String metricName,
            Long currentUsage,
            Long usageLimit,
            Long remainingUsage) {

        this.metricName = metricName;
        this.currentUsage = currentUsage;
        this.usageLimit = usageLimit;
        this.remainingUsage = remainingUsage;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public Long getCurrentUsage() {
        return currentUsage;
    }

    public void setCurrentUsage(Long currentUsage) {
        this.currentUsage = currentUsage;
    }

    public Long getUsageLimit() {
        return usageLimit;
    }

    public void setUsageLimit(Long usageLimit) {
        this.usageLimit = usageLimit;
    }

    public Long getRemainingUsage() {
        return remainingUsage;
    }

    public void setRemainingUsage(Long remainingUsage) {
        this.remainingUsage = remainingUsage;
    }
}