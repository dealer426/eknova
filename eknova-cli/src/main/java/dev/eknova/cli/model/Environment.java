package dev.eknova.cli.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents an eknova environment with its metadata
 */
public class Environment {
    private String name;
    private String wslDistributionName;
    private EnvironmentStatus status;
    private String blueprint;
    private LocalDateTime created;
    private String baseImage;
    private String version;

    public Environment() {}

    public Environment(String name, String wslDistributionName, EnvironmentStatus status, 
                      String blueprint, LocalDateTime created) {
        this.name = name;
        this.wslDistributionName = wslDistributionName;
        this.status = status;
        this.blueprint = blueprint;
        this.created = created;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWslDistributionName() {
        return wslDistributionName;
    }

    public void setWslDistributionName(String wslDistributionName) {
        this.wslDistributionName = wslDistributionName;
    }

    public EnvironmentStatus getStatus() {
        return status;
    }

    public void setStatus(EnvironmentStatus status) {
        this.status = status;
    }

    public String getBlueprint() {
        return blueprint;
    }

    public void setBlueprint(String blueprint) {
        this.blueprint = blueprint;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public String getBaseImage() {
        return baseImage;
    }

    public void setBaseImage(String baseImage) {
        this.baseImage = baseImage;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Environment that = (Environment) o;
        return Objects.equals(name, that.name) &&
               Objects.equals(wslDistributionName, that.wslDistributionName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, wslDistributionName);
    }

    @Override
    public String toString() {
        return "Environment{" +
                "name='" + name + '\'' +
                ", wslDistributionName='" + wslDistributionName + '\'' +
                ", status=" + status +
                ", blueprint='" + blueprint + '\'' +
                ", created=" + created +
                '}';
    }
}