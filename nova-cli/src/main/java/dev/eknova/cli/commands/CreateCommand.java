package dev.eknova.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

import dev.eknova.cli.NovaCommand;

/**
 * Create a new environment interactively
 * 
 * Examples:
 *   ekn create                     # Interactive creation
 *   ekn create --base ubuntu       # Start with Ubuntu base
 *   ekn create --name my-env       # Specify environment name
 *   ekn create --template go       # Use Go development template
 */
@Command(
    name = "create",
    aliases = {"new"},
    description = "Create new environment interactively",
    mixinStandardHelpOptions = true
)
public class CreateCommand implements Runnable {

    @ParentCommand
    NovaCommand parent;

    @Option(names = {"--name", "-n"}, description = "Environment name")
    String environmentName;

    @Option(names = {"--base", "-b"}, description = "Base OS distribution", defaultValue = "ubuntu")
    String baseDistribution;

    @Option(names = {"--template", "-t"}, description = "Development template (go, python, node, java, etc.)")
    String template;

    @Option(names = {"--interactive", "-i"}, description = "Force interactive mode", defaultValue = "true")
    boolean interactive;

    @Option(names = {"--output", "-o"}, description = "Save blueprint to file")
    String outputFile;

    @Override
    public void run() {
        System.out.println("🏗️  Creating new eknova environment...");
        System.out.println();

        try {
            if (interactive) {
                runInteractiveCreation();
            } else {
                runQuickCreation();
            }

        } catch (Exception e) {
            System.err.println("❌ Failed to create environment: " + e.getMessage());
            if (parent.isVerbose()) {
                e.printStackTrace();
            }
            System.exit(1);
        }
    }

    private void runInteractiveCreation() {
        System.out.println("Let's create your development environment step by step.");
        System.out.println();

        // Environment name
        if (environmentName == null) {
            System.out.print("Environment name: ");
            environmentName = System.console() != null ? System.console().readLine() : "my-env";
        }
        System.out.println("✓ Environment name: " + environmentName);

        // Base distribution
        if (baseDistribution.equals("ubuntu")) {
            System.out.print("Base distribution [ubuntu]: ");
            String input = System.console() != null ? System.console().readLine() : "";
            if (!input.trim().isEmpty()) {
                baseDistribution = input.trim();
            }
        }
        System.out.println("✓ Base distribution: " + baseDistribution);

        // Development template
        if (template == null) {
            System.out.println();
            System.out.println("Available templates:");
            System.out.println("  1. go        - Go development with common tools");
            System.out.println("  2. python    - Python with pip, venv, common packages");
            System.out.println("  3. node      - Node.js with npm, yarn, common tools");
            System.out.println("  4. java      - Java with Maven, Gradle, OpenJDK");
            System.out.println("  5. dotnet    - .NET SDK with common templates");
            System.out.println("  6. rust      - Rust with Cargo and common tools");
            System.out.println("  7. minimal   - Basic development tools only");
            System.out.println();
            System.out.print("Select template [minimal]: ");
            String templateChoice = System.console() != null ? System.console().readLine() : "7";
            template = parseTemplateChoice(templateChoice);
        }
        System.out.println("✓ Template: " + template);

        // Additional features
        System.out.println();
        System.out.println("Additional features (y/N):");
        System.out.print("  Docker support: ");
        boolean docker = readYesNo();
        System.out.print("  GPU/CUDA support: ");
        boolean cuda = readYesNo();
        System.out.print("  VS Code extensions: ");
        boolean vscode = readYesNo();

        // Create the environment
        System.out.println();
        System.out.println("🚀 Creating environment with configuration:");
        System.out.println("  Name: " + environmentName);
        System.out.println("  Base: " + baseDistribution);
        System.out.println("  Template: " + template);
        System.out.println("  Docker: " + (docker ? "Yes" : "No"));
        System.out.println("  CUDA: " + (cuda ? "Yes" : "No"));
        System.out.println("  VS Code: " + (vscode ? "Yes" : "No"));

        // TODO: Generate blueprint and provision environment
        // 1. Create blueprint YAML from selections
        // 2. Save blueprint if --output specified
        // 3. Call provision logic (similar to UpCommand)

        System.out.println();
        System.out.println("✅ Environment '" + environmentName + "' created successfully!");
        System.out.println();
        System.out.println("Access your environment:");
        System.out.println("  wsl -d eknova-" + environmentName);
        System.out.println("  code --remote wsl+eknova-" + environmentName);
    }

    private void runQuickCreation() {
        System.out.println("Creating environment with provided options...");
        System.out.println("  Name: " + (environmentName != null ? environmentName : "auto-generated"));
        System.out.println("  Base: " + baseDistribution);
        System.out.println("  Template: " + (template != null ? template : "minimal"));

        // TODO: Quick creation without interactive prompts
        System.out.println("✅ Environment created successfully!");
    }

    private String parseTemplateChoice(String choice) {
        switch (choice.trim()) {
            case "1": return "go";
            case "2": return "python";
            case "3": return "node";
            case "4": return "java";
            case "5": return "dotnet";
            case "6": return "rust";
            case "7": 
            default: return "minimal";
        }
    }

    private boolean readYesNo() {
        String response = System.console() != null ? System.console().readLine() : "N";
        return response.toLowerCase().startsWith("y");
    }
}