package dev.eknova.cli.service;

import dev.eknova.cli.model.Environment;
import dev.eknova.cli.model.EnvironmentStatus;
import dev.eknova.cli.util.ProcessUtils;
import dev.eknova.cli.util.ProcessUtils.ProcessResult;

import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service for managing WSL distributions and eknova environments
 */
@ApplicationScoped
public class WSLService {
    
    private static final String EKNOVA_PREFIX = "eknova-";
    private static final Pattern WSL_LIST_PATTERN = Pattern.compile("\\s*(\\*?)\\s*(.+?)\\s+(Running|Stopped|Installing|Terminated)");
    
    /**
     * Check if WSL is available on the system
     */
    public boolean isWSLAvailable() {
        return ProcessUtils.isCommandAvailable("wsl");
    }
    
    /**
     * Get WSL version information
     */
    public WSLInfo getWSLInfo() {
        if (!isWSLAvailable()) {
            return new WSLInfo(false, "Not available", 0);
        }
        
        try {
            // Try to get WSL status
            ProcessResult statusResult = ProcessUtils.execute("wsl", "--status");
            if (statusResult.isSuccess() && statusResult.hasOutput()) {
                // Parse version from status output
                String output = statusResult.getOutputAsString();
                if (output.contains("WSL 2")) {
                    return new WSLInfo(true, "WSL 2", getDistributionCount());
                } else if (output.contains("WSL 1")) {
                    return new WSLInfo(true, "WSL 1", getDistributionCount());
                }
            }
            
            // Fallback: just check if we can list distributions
            ProcessResult listResult = ProcessUtils.execute("wsl", "--list", "--quiet");
            if (listResult.isSuccess()) {
                return new WSLInfo(true, "WSL (version unknown)", getDistributionCount());
            }
            
            return new WSLInfo(false, "Not functional", 0);
            
        } catch (Exception e) {
            return new WSLInfo(false, "Error: " + e.getMessage(), 0);
        }
    }
    
    /**
     * Get count of all WSL distributions
     */
    private int getDistributionCount() {
        try {
            ProcessResult result = ProcessUtils.execute("wsl", "--list", "--quiet");
            if (result.isSuccess()) {
                return (int) result.getOutput().stream()
                    .filter(line -> !line.trim().isEmpty())
                    .count();
            }
        } catch (Exception e) {
            // Ignore
        }
        return 0;
    }
    
    /**
     * List all eknova environments
     */
    public List<Environment> listEnvironments() {
        List<Environment> environments = new ArrayList<>();
        
        try {
            ProcessResult result = ProcessUtils.execute("wsl", "--list", "--verbose");
            if (!result.isSuccess()) {
                return environments; // Return empty list if WSL command fails
            }
            
            for (String line : result.getOutput()) {
                if (line.trim().isEmpty() || line.contains("NAME") || line.contains("----")) {
                    continue; // Skip header and empty lines
                }
                
                Environment env = parseWSLDistributionLine(line);
                if (env != null && env.getName().startsWith(EKNOVA_PREFIX)) {
                    environments.add(env);
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error listing WSL distributions: " + e.getMessage());
        }
        
        return environments;
    }
    
    /**
     * Parse a line from 'wsl --list --verbose' output
     */
    private Environment parseWSLDistributionLine(String line) {
        try {
            // Remove special characters and normalize whitespace
            String cleanLine = line.replaceAll("[^\\p{Print}]", "").trim();
            
            // Split on whitespace, but handle names with spaces
            String[] parts = cleanLine.split("\\s+");
            if (parts.length < 3) {
                return null;
            }
            
            // Check if first part is default marker (*)
            int nameIndex = 0;
            if (parts[0].equals("*")) {
                nameIndex = 1;
            }
            
            String distributionName = parts[nameIndex];
            String statusStr = parts[nameIndex + 1];
            String version = parts.length > nameIndex + 2 ? parts[nameIndex + 2] : "Unknown";
            
            EnvironmentStatus status = EnvironmentStatus.fromWslState(statusStr);
            
            // Extract environment name from distribution name
            String envName = distributionName;
            if (distributionName.startsWith(EKNOVA_PREFIX)) {
                envName = distributionName.substring(EKNOVA_PREFIX.length());
            }
            
            Environment env = new Environment();
            env.setName(envName);
            env.setWslDistributionName(distributionName);
            env.setStatus(status);
            env.setVersion(version);
            env.setCreated(LocalDateTime.now()); // TODO: Get actual creation time
            env.setBlueprint("unknown"); // TODO: Get from metadata
            
            return env;
            
        } catch (Exception e) {
            return null; // Skip malformed lines
        }
    }
    
    /**
     * Find an environment by name
     */
    public Optional<Environment> findEnvironment(String name) {
        return listEnvironments().stream()
            .filter(env -> env.getName().equals(name))
            .findFirst();
    }
    
    /**
     * Start a WSL distribution
     */
    public boolean startEnvironment(String environmentName) {
        String distributionName = EKNOVA_PREFIX + environmentName;
        ProcessResult result = ProcessUtils.execute("wsl", "-d", distributionName, "echo", "started");
        return result.isSuccess();
    }
    
    /**
     * Stop a WSL distribution
     */
    public boolean stopEnvironment(String environmentName) {
        String distributionName = EKNOVA_PREFIX + environmentName;
        ProcessResult result = ProcessUtils.execute("wsl", "--terminate", distributionName);
        return result.isSuccess();
    }
    
    /**
     * Remove a WSL distribution
     */
    public boolean removeEnvironment(String environmentName) {
        String distributionName = EKNOVA_PREFIX + environmentName;
        ProcessResult result = ProcessUtils.execute("wsl", "--unregister", distributionName);
        return result.isSuccess();
    }
    
    /**
     * Import a new WSL distribution from a tar file
     */
    public boolean importEnvironment(String environmentName, String tarPath, String installPath) {
        String distributionName = EKNOVA_PREFIX + environmentName;
        ProcessResult result = ProcessUtils.execute("wsl", "--import", distributionName, installPath, tarPath);
        return result.isSuccess();
    }
    
    /**
     * Check if an environment exists
     */
    public boolean environmentExists(String environmentName) {
        return findEnvironment(environmentName).isPresent();
    }
    
    /**
     * WSL system information
     */
    public static class WSLInfo {
        private final boolean available;
        private final String version;
        private final int distributionCount;
        
        public WSLInfo(boolean available, String version, int distributionCount) {
            this.available = available;
            this.version = version;
            this.distributionCount = distributionCount;
        }
        
        public boolean isAvailable() {
            return available;
        }
        
        public String getVersion() {
            return version;
        }
        
        public int getDistributionCount() {
            return distributionCount;
        }
    }
}