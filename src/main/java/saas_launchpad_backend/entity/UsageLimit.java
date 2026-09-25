package saas_launchpad_backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "usage_limits")
public class UsageLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(name = "metric_name", nullable = false)
    private String metricName;

    @Column(name = "usage_limit", nullable = false)
    private Long usageLimit;

    public UsageLimit() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public Long getUsageLimit() {
        return usageLimit;
    }

    public void setUsageLimit(Long usageLimit) {
        this.usageLimit = usageLimit;
    }
}