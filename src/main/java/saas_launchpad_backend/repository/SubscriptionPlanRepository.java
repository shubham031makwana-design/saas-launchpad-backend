package saas_launchpad_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import saas_launchpad_backend.entity.SubscriptionPlan;

public interface SubscriptionPlanRepository
        extends JpaRepository<SubscriptionPlan, Long> {
}