package saas_launchpad_backend.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import saas_launchpad_backend.service.FeatureUsageService;

@RestController
@RequestMapping("/api/feature-usage")
public class FeatureUsageController {

    private final FeatureUsageService featureUsageService;

    public FeatureUsageController(
            FeatureUsageService featureUsageService) {

        this.featureUsageService = featureUsageService;
    }

    @GetMapping("/check")
    public boolean checkFeature(
            @RequestParam String featureName,
            Authentication authentication) {

        String email = authentication.getName();

        return featureUsageService.isFeatureEnabled(
                featureName,
                email
        );
    }
    @PostMapping("/use")
public boolean useFeature(
        @RequestParam String featureName,
        Authentication authentication) {

    String email = authentication.getName();

    return featureUsageService.useFeature(
            featureName,
            email
    );
}
}