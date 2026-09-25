package saas_launchpad_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import saas_launchpad_backend.entity.SubscriptionPlan;
import saas_launchpad_backend.repository.SubscriptionPlanRepository;

@Service
public class SubscriptionPlanService {

    private final SubscriptionPlanRepository subscriptionPlanRepository;

    public SubscriptionPlanService(
            SubscriptionPlanRepository subscriptionPlanRepository) {

        this.subscriptionPlanRepository = subscriptionPlanRepository;
    }

    public List<SubscriptionPlan> getAllPlans() {
        return subscriptionPlanRepository.findAll();
    }

    public SubscriptionPlan getPlanById(Long id) {
        return subscriptionPlanRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Subscription plan not found"));
    }
}