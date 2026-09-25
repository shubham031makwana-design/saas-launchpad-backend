package saas_launchpad_backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import saas_launchpad_backend.dto.FeatureFlagRequestDTO;
import saas_launchpad_backend.dto.FeatureFlagResponseDTO;
import saas_launchpad_backend.entity.FeatureFlag;
import saas_launchpad_backend.entity.Tenant;
import saas_launchpad_backend.entity.User;
import saas_launchpad_backend.repository.FeatureFlagRepository;
import saas_launchpad_backend.repository.UserRepository;

@Service
public class FeatureFlagService {

    private final FeatureFlagRepository featureFlagRepository;
    private final UserRepository userRepository;

    public FeatureFlagService(
            FeatureFlagRepository featureFlagRepository,
            UserRepository userRepository) {

        this.featureFlagRepository = featureFlagRepository;
        this.userRepository = userRepository;
    }

    // ==========================================
    // GET LOGGED-IN USER
    // ==========================================

    private User getLoggedInUser(String email) {

        Optional<User> optionalUser =
                userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new RuntimeException(
                    "Logged-in user not found"
            );
        }

        return optionalUser.get();
    }

    // ==========================================
    // GET USER'S TENANT
    // ==========================================

    private Tenant getUserTenant(String email) {

        User user = getLoggedInUser(email);

        if (user.getTenant() == null) {
            throw new RuntimeException(
                    "User has no tenant"
            );
        }

        return user.getTenant();
    }

    // ==========================================
    // CREATE FEATURE FLAG
    // ==========================================

    public FeatureFlagResponseDTO createFeatureFlag(
            FeatureFlagRequestDTO request,
            String email) {

        Tenant tenant = getUserTenant(email);

        if (request.getFeatureName() == null
                || request.getFeatureName().trim().isEmpty()) {

            throw new RuntimeException(
                    "Feature name is required"
            );
        }

        Optional<FeatureFlag> existing =
                featureFlagRepository
                        .findByTenantIdAndFeatureName(
                                tenant.getId(),
                                request.getFeatureName()
                        );

        if (existing.isPresent()) {
            throw new RuntimeException(
                    "Feature flag already exists for this tenant"
            );
        }

        FeatureFlag featureFlag =
                new FeatureFlag();

        featureFlag.setFeatureName(
                request.getFeatureName()
        );

        featureFlag.setEnabled(
                request.isEnabled()
        );

        featureFlag.setTenant(tenant);

        FeatureFlag saved =
                featureFlagRepository.save(featureFlag);

        return convertToResponse(saved);
    }

    // ==========================================
    // GET ALL FEATURE FLAGS
    // ==========================================

    public List<FeatureFlagResponseDTO> getFeatureFlags(
            String email) {

        Tenant tenant = getUserTenant(email);

        List<FeatureFlag> featureFlags =
                featureFlagRepository.findByTenantId(
                        tenant.getId()
                );

        return featureFlags.stream()
                .map(this::convertToResponse)
                .toList();
    }

    // ==========================================
    // GET FEATURE FLAG BY ID
    // SAME TENANT ONLY
    // ==========================================

    public FeatureFlagResponseDTO getFeatureFlagById(
            Long id,
            String email) {

        Tenant tenant = getUserTenant(email);

        Optional<FeatureFlag> optionalFeatureFlag =
                featureFlagRepository
                        .findByIdAndTenantId(
                                id,
                                tenant.getId()
                        );

        if (optionalFeatureFlag.isEmpty()) {
            throw new RuntimeException(
                    "Feature flag not found in your tenant"
            );
        }

        return convertToResponse(
                optionalFeatureFlag.get()
        );
    }

    // ==========================================
    // UPDATE FEATURE FLAG
    // SAME TENANT ONLY
    // ==========================================

    public FeatureFlagResponseDTO updateFeatureFlag(
            Long id,
            FeatureFlagRequestDTO request,
            String email) {

        Tenant tenant = getUserTenant(email);

        Optional<FeatureFlag> optionalFeatureFlag =
                featureFlagRepository
                        .findByIdAndTenantId(
                                id,
                                tenant.getId()
                        );

        if (optionalFeatureFlag.isEmpty()) {
            throw new RuntimeException(
                    "Feature flag not found in your tenant"
            );
        }

        FeatureFlag featureFlag =
                optionalFeatureFlag.get();

        /*
         * The frontend toggle normally sends only:
         *
         * {
         *     "enabled": true
         * }
         *
         * Therefore, we must NOT replace the existing
         * feature name with null.
         */

        if (request.getFeatureName() != null
                && !request.getFeatureName().trim().isEmpty()) {

            featureFlag.setFeatureName(
                    request.getFeatureName()
            );
        }

        featureFlag.setEnabled(
                request.isEnabled()
        );

        /*
         * Keep the feature flag assigned to the
         * authenticated user's tenant.
         */
        featureFlag.setTenant(tenant);

        FeatureFlag updated =
                featureFlagRepository.save(featureFlag);

        return convertToResponse(updated);
    }

    // ==========================================
    // DELETE FEATURE FLAG
    // SAME TENANT ONLY
    // ==========================================

    public void deleteFeatureFlag(
            Long id,
            String email) {

        Tenant tenant = getUserTenant(email);

        Optional<FeatureFlag> optionalFeatureFlag =
                featureFlagRepository
                        .findByIdAndTenantId(
                                id,
                                tenant.getId()
                        );

        if (optionalFeatureFlag.isEmpty()) {
            throw new RuntimeException(
                    "Feature flag not found in your tenant"
            );
        }

        featureFlagRepository.delete(
                optionalFeatureFlag.get()
        );
    }

    // ==========================================
    // CONVERT ENTITY → DTO
    // ==========================================

    private FeatureFlagResponseDTO convertToResponse(
            FeatureFlag featureFlag) {

        FeatureFlagResponseDTO response =
                new FeatureFlagResponseDTO();

        response.setId(
                featureFlag.getId()
        );

        response.setFeatureName(
                featureFlag.getFeatureName()
        );

        response.setEnabled(
                featureFlag.isEnabled()
        );

        if (featureFlag.getTenant() != null) {
            response.setTenantId(
                    featureFlag.getTenant().getId()
            );
        }

        return response;
    }
}