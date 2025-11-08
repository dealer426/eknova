package dev.eknova.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

import dev.eknova.cli.NovaCommand;

/**
 * Clean up and remove an environment
 * 
 * Examples:
 *   nova destroy my-env          # Remove specific environment
 *   nova destroy --all           # Remove all environments
 *   nova destroy --force my-env  # Skip confirmation prompt
 */
@Command(
    name = "destroy",
    aliases = {"rm", "remove"},
    description = "Clean up and remove environment",
    mixinStandardHelpOptions = true
)
public class DestroyCommand implements Runnable {

    @ParentCommand
    NovaCommand parent;

    @Parameters(
        index = "0",
        arity = "0..1",
        description = "Environment name to destroy",
        paramLabel = "ENVIRONMENT"
    )
    String environmentName;

    @Option(names = {"--all"}, description = "Remove all eknova environments")
    boolean destroyAll;

    @Option(names = {"--force", "-f"}, description = "Skip confirmation prompt")
    boolean force;

    @Option(names = {"--keep-data"}, description = "Keep user data, only remove WSL distribution")
    boolean keepData;

    @Override
    public void run() {
        if (destroyAll && environmentName != null) {
            System.err.println("❌ Cannot specify both environment name and --all flag");
            System.exit(1);
        }

        if (!destroyAll && environmentName == null) {
            System.err.println("❌ Must specify environment name or --all flag");
            System.exit(1);
        }

        try {
            if (destroyAll) {
                destroyAllEnvironments();
            } else {
                destroySingleEnvironment(environmentName);
            }

        } catch (Exception e) {
            System.err.println("❌ Failed to destroy environment: " + e.getMessage());
            if (parent.isVerbose()) {
                e.printStackTrace();
            }
            System.exit(1);
        }
    }

    private void destroySingleEnvironment(String envName) {
        System.out.println("🗑️  Destroying environment: " + envName);
        
        if (parent.isVerbose()) {
            System.out.println("  API URL: " + parent.getApiUrl());
            System.out.println("  Keep data: " + keepData);
        }

        // TODO: Check if environment exists
        boolean exists = checkEnvironmentExists(envName);
        if (!exists) {
            System.err.println("❌ Environment '" + envName + "' not found");
            System.exit(1);
        }

        // Confirmation prompt unless --force
        if (!force) {
            System.out.print("⚠️  Are you sure you want to destroy '" + envName + "'? (y/N): ");
            String response = System.console() != null ? System.console().readLine() : "N";
            if (!response.toLowerCase().startsWith("y")) {
                System.out.println("Operation cancelled.");
                return;
            }
        }

        // TODO: Implement destruction logic
        // 1. Stop environment if running
        // 2. Export/backup user data if --keep-data
        // 3. Remove WSL distribution
        // 4. Clean up Nova API records
        // 5. Remove local metadata

        System.out.println("  Stopping environment...");
        System.out.println("  Removing WSL distribution...");
        
        if (!keepData) {
            System.out.println("  Cleaning up data...");
        } else {
            System.out.println("  Preserving user data...");
        }
        
        System.out.println("  Updating registry...");
        System.out.println("✅ Environment '" + envName + "' destroyed successfully!");
    }

    private void destroyAllEnvironments() {
        System.out.println("🗑️  Destroying all eknova environments...");

        // TODO: Get list of all eknova environments
        String[] environments = {"ml-cuda-dev", "go-microservice", "react-app"}; // Mock data

        if (environments.length == 0) {
            System.out.println("No eknova environments found.");
            return;
        }

        System.out.println("Found " + environments.length + " environment(s):");
        for (String env : environments) {
            System.out.println("  - " + env);
        }

        // Confirmation prompt unless --force
        if (!force) {
            System.out.print("⚠️  Are you sure you want to destroy ALL environments? (y/N): ");
            String response = System.console() != null ? System.console().readLine() : "N";
            if (!response.toLowerCase().startsWith("y")) {
                System.out.println("Operation cancelled.");
                return;
            }
        }

        // Destroy each environment
        for (String env : environments) {
            System.out.println("Destroying " + env + "...");
            // TODO: Call destroySingleEnvironment logic for each
        }

        System.out.println("✅ All environments destroyed successfully!");
    }

    private boolean checkEnvironmentExists(String envName) {
        // TODO: Check with Nova API and WSL
        // For now, mock that environments exist
        return true;
    }
}