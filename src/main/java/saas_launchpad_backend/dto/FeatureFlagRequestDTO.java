package saas_launchpad_backend.dto;

public class FeatureFlagRequestDTO {

    private String featureName;

    private boolean enabled;

    public FeatureFlagRequestDTO() {
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}