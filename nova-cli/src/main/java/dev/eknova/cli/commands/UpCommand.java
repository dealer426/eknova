package dev.eknova.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

import dev.eknova.cli.NovaCommand;

/**
 * Provision and start an environment from a blueprint
 * 
 * Examples:
 *   nova up @user/ml-cuda           # From marketplace blueprint
 *   nova up ./blueprint.yaml       # From local blueprint
 *   nova up --name my-env @user/go  # Custom environment name
 */
@Command(
    name = "up",
    description = "Provision environment from blueprint",
    mixinStandardHelpOptions = true
)
public class UpCommand implements Runnable {

    @ParentCommand
    NovaCommand parent;

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
            System.out.println("  API URL: " + parent.getApiUrl());
            if (environmentName != null) {
                System.out.println("  Environment name: " + environmentName);
            }
        }

        try {
            // TODO: Parse blueprint reference
            String blueprintType = parseBlueprintType(blueprint);
            System.out.println("  Type: " + blueprintType);

            // TODO: Call Nova API to provision environment
            // 1. Validate blueprint exists and is accessible
            // 2. Check if environment already exists (handle --force)
            // 3. Create WSL instance
            // 4. Install dependencies from blueprint
            // 5. Configure environment
            // 6. Start services (unless --no-start)

            System.out.println("✅ Environment provisioned successfully!");
            
            if (!noStart) {
                System.out.println("🏃 Starting environment...");
                System.out.println("✅ Environment started and ready!");
                System.out.println();
                System.out.println("Access your environment:");
                String envName = environmentName != null ? environmentName : extractBlueprintName(blueprint);
                System.out.println("  wsl -d " + envName);
                System.out.println("  code --remote wsl+" + envName);
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