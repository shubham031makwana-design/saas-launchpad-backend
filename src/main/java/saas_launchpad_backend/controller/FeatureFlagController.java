package saas_launchpad_backend.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import saas_launchpad_backend.dto.FeatureFlagRequestDTO;
import saas_launchpad_backend.dto.FeatureFlagResponseDTO;
import saas_launchpad_backend.service.FeatureFlagService;

@RestController
@RequestMapping("/api/feature-flags")
public class FeatureFlagController {

    private final FeatureFlagService featureFlagService;

    public FeatureFlagController(
            FeatureFlagService featureFlagService) {

        this.featureFlagService =
                featureFlagService;
    }

    // ==========================================
    // CREATE
    // ==========================================

    @PostMapping
    public FeatureFlagResponseDTO createFeatureFlag(
            @RequestBody FeatureFlagRequestDTO request,
            Authentication authentication) {

        String email =
                authentication.getName();

        return featureFlagService.createFeatureFlag(
                request,
                email
        );
    }

    // ==========================================
    // GET ALL
    // ==========================================

    @GetMapping
    public List<FeatureFlagResponseDTO> getFeatureFlags(
            Authentication authentication) {

        String email =
                authentication.getName();

        return featureFlagService.getFeatureFlags(
                email
        );
    }

    // ==========================================
    // GET BY ID
    // ==========================================

    @GetMapping("/{id}")
    public FeatureFlagResponseDTO getFeatureFlagById(
            @PathVariable Long id,
            Authentication authentication) {

        String email =
                authentication.getName();

        return featureFlagService.getFeatureFlagById(
                id,
                email
        );
    }

    // ==========================================
    // UPDATE
    // ==========================================

    @PutMapping("/{id}")
    public FeatureFlagResponseDTO updateFeatureFlag(
            @PathVariable Long id,
            @RequestBody FeatureFlagRequestDTO request,
            Authentication authentication) {

        String email =
                authentication.getName();

        return featureFlagService.updateFeatureFlag(
                id,
                request,
                email
        );
    }

    // ==========================================
    // DELETE
    // ==========================================

    @DeleteMapping("/{id}")
    public String deleteFeatureFlag(
            @PathVariable Long id,
            Authentication authentication) {

        String email =
                authentication.getName();

        featureFlagService.deleteFeatureFlag(
                id,
                email
        );

        return "Feature flag deleted successfully";
    }
}