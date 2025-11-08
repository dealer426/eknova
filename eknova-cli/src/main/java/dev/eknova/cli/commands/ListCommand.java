package dev.eknova.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

import dev.eknova.cli.NovaCommand;
import dev.eknova.cli.model.Environment;
import dev.eknova.cli.model.EnvironmentStatus;
import dev.eknova.cli.service.WSLService;

import jakarta.inject.Inject;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

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

    @Inject
    WSLService wslService;

    @Option(names = {"--running", "-r"}, description = "Show only running environments")
    boolean runningOnly;

    @Option(names = {"--json"}, description = "Output in JSON format")
    boolean jsonOutput;

    @Option(names = {"--all", "-a"}, description = "Include system WSL distributions")
    boolean includeSystem;

    @Override
    public void run() {
        if (parent.isVerbose()) {
            System.out.println("Querying WSL distributions...");
        }

        try {
            // Check if WSL is available
            if (!wslService.isWSLAvailable()) {
                System.err.println("❌ WSL is not available on this system");
                System.err.println("💡 Please install WSL2 to use eknova environments");
                System.exit(1);
                return;
            }

            // Get environments from WSL
            List<Environment> environments = wslService.listEnvironments();
            
            // Filter by running status if requested
            if (runningOnly) {
                environments = environments.stream()
                    .filter(env -> env.getStatus() == EnvironmentStatus.RUNNING)
                    .collect(Collectors.toList());
            }

            if (jsonOutput) {
                outputJson(environments);
            } else {
                outputTable(environments);
            }

        } catch (Exception e) {
            System.err.println("❌ Failed to list environments: " + e.getMessage());
            if (parent.isVerbose()) {
                e.printStackTrace();
            }
            System.exit(1);
        }
    }

    private void outputTable(List<Environment> environments) {
        System.out.println("📋 eknova environments:");
        System.out.println();
        
        if (environments.isEmpty()) {
            System.out.println("No eknova environments found.");
            System.out.println();
            System.out.println("💡 Create your first environment with: ekn create");
            System.out.println("💡 Or provision from a blueprint: ekn up @user/blueprint-name");
            return;
        }
        
        System.out.printf("%-20s %-12s %-15s %-20s%n", "NAME", "STATUS", "BLUEPRINT", "CREATED");
        System.out.println("─".repeat(70));
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        for (Environment env : environments) {
            String createdStr = env.getCreated() != null ? 
                env.getCreated().format(formatter) : "Unknown";
            String blueprint = env.getBlueprint() != null ? env.getBlueprint() : "unknown";
            
            System.out.printf("%-20s %-12s %-15s %-20s%n", 
                env.getName(), 
                env.getStatus().getDisplayName(),
                blueprint,
                createdStr);
        }
        
        System.out.println();
        System.out.println("💡 Use 'ekn up <name>' to start stopped environments");
        System.out.println("💡 Use 'ekn destroy <name>' to remove environments");
    }

    private void outputJson(List<Environment> environments) {
        System.out.println("{");
        System.out.println("  \"environments\": [");
        
        for (int i = 0; i < environments.size(); i++) {
            Environment env = environments.get(i);
            System.out.println("    {");
            System.out.println("      \"name\": \"" + env.getName() + "\",");
            System.out.println("      \"status\": \"" + env.getStatus().name().toLowerCase() + "\",");
            System.out.println("      \"blueprint\": \"" + (env.getBlueprint() != null ? env.getBlueprint() : "unknown") + "\",");
            System.out.println("      \"created\": \"" + (env.getCreated() != null ? env.getCreated().toString() : "unknown") + "\",");
            System.out.println("      \"wsl_distribution\": \"" + env.getWslDistributionName() + "\",");
            System.out.println("      \"version\": \"" + (env.getVersion() != null ? env.getVersion() : "unknown") + "\"");
            System.out.print("    }");
            if (i < environments.size() - 1) {
                System.out.println(",");
            } else {
                System.out.println();
            }
        }
        
        System.out.println("  ]");
        System.out.println("}");
    }
}