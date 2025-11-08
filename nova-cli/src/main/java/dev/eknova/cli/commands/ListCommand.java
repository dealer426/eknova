package dev.eknova.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

import dev.eknova.cli.NovaCommand;

/**
 * List all local eknova environments
 * 
 * Examples:
 *   ekn list              # List all environments  
 *   ekn list --running    # Only running environments
 *   ekn list --json       # JSON output format
 */
@Command(
    name = "list",
    aliases = {"ls"},
    description = "List local environments",
    mixinStandardHelpOptions = true
)
public class ListCommand implements Runnable {

    @ParentCommand
    NovaCommand parent;

    @Option(names = {"--running", "-r"}, description = "Show only running environments")
    boolean runningOnly;

    @Option(names = {"--json"}, description = "Output in JSON format")
    boolean jsonOutput;

    @Option(names = {"--all", "-a"}, description = "Include system WSL distributions")
    boolean includeSystem;

    @Override
    public void run() {
        if (parent.isVerbose()) {
            System.out.println("Fetching environment list from: " + parent.getApiUrl());
        }

        try {
            // TODO: Get environments from Nova API and WSL
            // 1. Query WSL for all distributions
            // 2. Filter for eknova-managed environments
            // 3. Get status and metadata from Nova API
            // 4. Format output

            if (jsonOutput) {
                outputJson();
            } else {
                outputTable();
            }

        } catch (Exception e) {
            System.err.println("❌ Failed to list environments: " + e.getMessage());
            if (parent.isVerbose()) {
                e.printStackTrace();
            }
            System.exit(1);
        }
    }

    private void outputTable() {
        System.out.println("📋 eknova environments:");
        System.out.println();
        
        // Mock data for now - TODO: Replace with real data
        System.out.printf("%-20s %-12s %-15s %-30s%n", "NAME", "STATUS", "BLUEPRINT", "CREATED");
        System.out.println("─".repeat(80));
        
        // Example environments
        System.out.printf("%-20s %-12s %-15s %-30s%n", "ml-cuda-dev", "Running", "@user/ml-cuda", "2024-01-15 10:30:45");
        System.out.printf("%-20s %-12s %-15s %-30s%n", "go-microservice", "Stopped", "@user/go-api", "2024-01-14 14:22:10");
        System.out.printf("%-20s %-12s %-15s %-30s%n", "react-app", "Running", "local/react.yaml", "2024-01-13 09:15:33");
        
        System.out.println();
        System.out.println("💡 Use 'ekn up <name>' to start stopped environments");
        System.out.println("💡 Use 'ekn destroy <name>' to remove environments");
    }

    private void outputJson() {
        // TODO: Generate proper JSON output
        System.out.println("{");
        System.out.println("  \"environments\": [");
        System.out.println("    {");
        System.out.println("      \"name\": \"ml-cuda-dev\",");
        System.out.println("      \"status\": \"running\",");
        System.out.println("      \"blueprint\": \"@user/ml-cuda\",");
        System.out.println("      \"created\": \"2024-01-15T10:30:45Z\",");
        System.out.println("      \"wsl_distribution\": \"eknova-ml-cuda-dev\"");
        System.out.println("    },");
        System.out.println("    {");
        System.out.println("      \"name\": \"go-microservice\",");
        System.out.println("      \"status\": \"stopped\",");
        System.out.println("      \"blueprint\": \"@user/go-api\",");
        System.out.println("      \"created\": \"2024-01-14T14:22:10Z\",");
        System.out.println("      \"wsl_distribution\": \"eknova-go-microservice\"");
        System.out.println("    }");
        System.out.println("  ]");
        System.out.println("}");
    }
}