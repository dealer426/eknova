package dev.eknova.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

import dev.eknova.cli.NovaCommand;
import dev.eknova.cli.model.Environment;
import dev.eknova.cli.service.WSLService;

import jakarta.inject.Inject;
import java.io.Console;
import java.util.List;

/**
 * Clean up and remove an environment
 * 
 * Examples:
 *   ekn destroy my-env          # Remove specific environment
 *   ekn destroy --all           # Remove all environments
 *   ekn destroy --force my-env  # Skip confirmation prompt
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

    @Inject
    WSLService wslService;

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

        // Check if environment exists
        if (!wslService.environmentExists(envName)) {
            System.err.println("❌ Environment '" + envName + "' not found");
            System.exit(1);
        }

        // Confirmation prompt unless --force
        if (!force) {
            System.out.print("⚠️  Are you sure you want to destroy '" + envName + "'? (y/N): ");
            Console console = System.console();
            String response = console != null ? console.readLine() : "N";
            if (!response.toLowerCase().startsWith("y")) {
                System.out.println("Operation cancelled.");
                return;
            }
        }

        // Implement destruction logic
        System.out.println("  Stopping environment...");
        boolean stopped = wslService.stopEnvironment(envName);
        if (!stopped && parent.isVerbose()) {
            System.out.println("  (Environment may not have been running)");
        }
        
        if (!keepData) {
            System.out.println("  Removing WSL distribution...");
            boolean removed = wslService.removeEnvironment(envName);
            if (removed) {
                System.out.println("✅ Environment '" + envName + "' destroyed successfully!");
            } else {
                System.err.println("❌ Failed to remove WSL distribution for '" + envName + "'");
                System.exit(1);
            }
        } else {
            System.out.println("  Preserving user data (--keep-data specified)...");
            System.out.println("⚠️  Environment stopped but WSL distribution preserved");
        }
    }

    private void destroyAllEnvironments() {
        System.out.println("🗑️  Destroying all eknova environments...");

        // Get list of all eknova environments
        List<Environment> environments = wslService.listEnvironments();

        if (environments.isEmpty()) {
            System.out.println("No eknova environments found.");
            return;
        }

        System.out.println("Found " + environments.size() + " environment(s):");
        for (Environment env : environments) {
            System.out.println("  - " + env.getName() + " (" + env.getStatus().getDisplayName() + ")");
        }

        // Confirmation prompt unless --force
        if (!force) {
            System.out.print("⚠️  Are you sure you want to destroy ALL environments? (y/N): ");
            Console console = System.console();
            String response = console != null ? console.readLine() : "N";
            if (!response.toLowerCase().startsWith("y")) {
                System.out.println("Operation cancelled.");
                return;
            }
        }

        // Destroy each environment
        int success = 0;
        int failed = 0;
        
        for (Environment env : environments) {
            System.out.println("Destroying " + env.getName() + "...");
            
            // Stop and remove the environment
            wslService.stopEnvironment(env.getName());
            
            if (!keepData) {
                boolean removed = wslService.removeEnvironment(env.getName());
                if (removed) {
                    success++;
                    System.out.println("  ✅ " + env.getName() + " destroyed");
                } else {
                    failed++;
                    System.out.println("  ❌ Failed to destroy " + env.getName());
                }
            } else {
                success++;
                System.out.println("  ⚠️  " + env.getName() + " stopped (data preserved)");
            }
        }

        System.out.println();
        if (failed == 0) {
            System.out.println("✅ All environments destroyed successfully!");
        } else {
            System.out.println("⚠️  " + success + " environments destroyed, " + failed + " failed");
        }
    }


}