package saas_launchpad_backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import saas_launchpad_backend.entity.SubscriptionPlan;
import saas_launchpad_backend.service.SubscriptionPlanService;

@RestController
@RequestMapping("/api/subscription-plans")
public class SubscriptionPlanController {

    private final SubscriptionPlanService subscriptionPlanService;

    public SubscriptionPlanController(
            SubscriptionPlanService subscriptionPlanService) {

        this.subscriptionPlanService = subscriptionPlanService;
    }

    @GetMapping
    public List<SubscriptionPlan> getAllPlans() {
        return subscriptionPlanService.getAllPlans();
    }

    @GetMapping("/{id}")
    public SubscriptionPlan getPlanById(
            @PathVariable Long id) {

        return subscriptionPlanService.getPlanById(id);
    }
}