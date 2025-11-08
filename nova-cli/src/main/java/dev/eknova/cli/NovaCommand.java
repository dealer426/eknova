package dev.eknova.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import dev.eknova.cli.commands.UpCommand;
import dev.eknova.cli.commands.ListCommand;
import dev.eknova.cli.commands.DestroyCommand;
import dev.eknova.cli.commands.CreateCommand;
import dev.eknova.cli.commands.VersionCommand;

/**
 * Main Nova CLI command - entry point for all eknova operations
 * 
 * eknova - AI-powered WSL development environments
 * 
 * Examples:
 *   nova up @user/ml-cuda          # Provision environment from blueprint
 *   nova create --base ubuntu      # Create new environment  
 *   nova list                      # List environments
 *   nova destroy my-env            # Clean up environment
 */
@Command(
    name = "nova",
    description = "AI-powered WSL development environments",
    mixinStandardHelpOptions = true,
    version = "nova 1.0.0-SNAPSHOT",
    subcommands = {
        UpCommand.class,
        ListCommand.class, 
        DestroyCommand.class,
        CreateCommand.class,
        VersionCommand.class
    }
)
public class NovaCommand implements Runnable {

    @Option(names = {"-v", "--verbose"}, description = "Enable verbose output")
    boolean verbose;

    @Option(names = {"--api-url"}, description = "Nova API URL", defaultValue = "http://localhost:5000")
    String apiUrl;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new NovaCommand()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        // When no subcommand is specified, show help
        System.out.println("🚀 eknova - AI-powered WSL development environments");
        System.out.println();
        System.out.println("Usage: nova <command> [options]");
        System.out.println();
        System.out.println("Commands:");
        System.out.println("  up       Provision environment from blueprint");
        System.out.println("  create   Create new environment interactively");
        System.out.println("  list     List local environments");
        System.out.println("  destroy  Clean up environment");
        System.out.println("  version  Show version information");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  nova up @user/ml-cuda          # Provision from marketplace");
        System.out.println("  nova create --base ubuntu      # Create Ubuntu environment");
        System.out.println("  nova list                      # Show all environments");
        System.out.println("  nova destroy my-env            # Remove environment");
        System.out.println();
        System.out.println("Use 'nova <command> --help' for command-specific help.");
    }

    // Getters for global options that subcommands can access
    public boolean isVerbose() {
        return verbose;
    }

    public String getApiUrl() {
        return apiUrl;
    }
}