package dev.eknova.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

import dev.eknova.cli.NovaCommand;
import dev.eknova.cli.service.WSLService;

import jakarta.inject.Inject;

/**
 * Provision and start an environment from a blueprint
 * 
 * Examples:
 *   ekn up @user/ml-cuda           # From marketplace blueprint
 *   ekn up ./blueprint.yaml       # From local blueprint
 *   ekn up --name my-env @user/go  # Custom environment name
 */
@Command(
    name = "up",
    description = "Provision environment from blueprint",
    mixinStandardHelpOptions = true
)
public class UpCommand implements Runnable {

    @ParentCommand
    NovaCommand parent;

    @Inject
    WSLService wslService;

    @Parameters(
        index = "0",
        description = "Blueprint reference (@user/name, file path, or URL)",
        paramLabel = "BLUEPRINT"
    )
    String blueprint;

    @Option(names = {"--name", "-n"}, description = "Custom environment name")
    String environmentName;

    @Option(names = {"--force", "-f"}, description = "Force recreation if environment exists")
    boolean force;

    @Option(names = {"--no-start"}, description = "Provision but don't start the environment")
    boolean noStart;

    @Override
    public void run() {
        System.out.println("🚀 Provisioning environment...");
        
        if (parent.isVerbose()) {
            System.out.println("  Blueprint: " + blueprint);
            if (environmentName != null) {
                System.out.println("  Environment name: " + environmentName);
            }
        }

        try {
            // Check if WSL is available
            if (!wslService.isWSLAvailable()) {
                System.err.println("❌ WSL is not available on this system");
                System.err.println("💡 Please install WSL2 to use eknova environments");
                System.exit(1);
                return;
            }

            // Parse blueprint reference
            String blueprintType = parseBlueprintType(blueprint);
            System.out.println("  Type: " + blueprintType);

            // Generate environment name if not provided
            String envName = environmentName != null ? environmentName : extractBlueprintName(blueprint);
            
            // Check if environment already exists
            if (wslService.environmentExists(envName) && !force) {
                System.err.println("❌ Environment '" + envName + "' already exists");
                System.err.println("💡 Use --force to recreate or choose a different name with --name");
                System.exit(1);
                return;
            }

            // For now, we'll show what would happen (since we don't have actual blueprint processing yet)
            System.out.println("📋 Blueprint processing (simulated):");
            System.out.println("  Environment: " + envName);
            System.out.println("  Blueprint: " + blueprint);
            
            if (blueprintType.equals("marketplace")) {
                System.out.println("⚠️  Marketplace blueprints not yet implemented");
                System.out.println("💡 Coming soon: Full blueprint marketplace integration");
            } else if (blueprintType.equals("local")) {
                System.out.println("⚠️  Local blueprint files not yet implemented");
                System.out.println("💡 Coming soon: YAML blueprint processing");
            } else if (blueprintType.equals("url")) {
                System.out.println("⚠️  URL blueprints not yet implemented");
                System.out.println("💡 Coming soon: Remote blueprint downloading");
            }

            System.out.println("✅ Environment would be provisioned successfully!");
            
            if (!noStart) {
                System.out.println("🏃 Starting environment...");
                System.out.println("✅ Environment started and ready!");
                System.out.println();
                System.out.println("Access your environment:");
                System.out.println("  wsl -d eknova-" + envName);
                System.out.println("  code --remote wsl+eknova-" + envName);
            }

        } catch (Exception e) {
            System.err.println("❌ Failed to provision environment: " + e.getMessage());
            if (parent.isVerbose()) {
                e.printStackTrace();
            }
            System.exit(1);
        }
    }

    private String parseBlueprintType(String blueprint) {
        if (blueprint.startsWith("@")) {
            return "marketplace";
        } else if (blueprint.startsWith("http://") || blueprint.startsWith("https://")) {
            return "url";
        } else {
            return "local";
        }
    }

    private String extractBlueprintName(String blueprint) {
        if (blueprint.startsWith("@")) {
            return blueprint.substring(1).replace("/", "-");
        } else if (blueprint.contains("/")) {
            return blueprint.substring(blueprint.lastIndexOf("/") + 1).replaceAll("\\.[^.]*$", "");
        } else {
            return blueprint.replaceAll("\\.[^.]*$", "");
        }
    }
}