package saas_launchpad_backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "subscription_plans")
public class SubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plan_name", nullable = false)
    private String planName;

    @Column(name = "api_call_limit", nullable = false)
    private Long apiCallLimit;

    @Column(name = "payment_limit", nullable = false)
    private Long paymentLimit;

    public SubscriptionPlan() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public Long getApiCallLimit() {
        return apiCallLimit;
    }

    public void setApiCallLimit(Long apiCallLimit) {
        this.apiCallLimit = apiCallLimit;
    }

    public Long getPaymentLimit() {
        return paymentLimit;
    }

    public void setPaymentLimit(Long paymentLimit) {
        this.paymentLimit = paymentLimit;
    }
}