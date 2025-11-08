package dev.eknova.cli.model;

/**
 * Status of an eknova environment
 */
public enum EnvironmentStatus {
    RUNNING("Running"),
    STOPPED("Stopped"),
    INSTALLING("Installing"),
    TERMINATED("Terminated"),
    UNKNOWN("Unknown");

    private final String displayName;

    EnvironmentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public static EnvironmentStatus fromWslState(String wslState) {
        if (wslState == null) return UNKNOWN;
        
        switch (wslState.toLowerCase().trim()) {
            case "running":
                return RUNNING;
            case "stopped":
                return STOPPED;
            case "installing":
                return INSTALLING;
            case "terminated":
                return TERMINATED;
            default:
                return UNKNOWN;
        }
    }
}