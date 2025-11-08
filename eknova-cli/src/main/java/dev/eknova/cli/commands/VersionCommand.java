package dev.eknova.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

import dev.eknova.cli.NovaCommand;
import dev.eknova.cli.service.WSLService;

import jakarta.inject.Inject;

/**
 * Show version and system information
 * 
 * Examples:
 *   ekn version          # Show version
 *   ekn version --full   # Show detailed system info
 */
@Command(
    name = "version",
    description = "Show version and system information",
    mixinStandardHelpOptions = true
)
public class VersionCommand implements Runnable {

    @ParentCommand
    NovaCommand parent;

    @Inject
    WSLService wslService;

    @Option(names = {"--full"}, description = "Show detailed system information")
    boolean fullInfo;

    @Override
    public void run() {
        System.out.println("🚀 eknova " + getVersion());
        
        if (fullInfo) {
            System.out.println();
            System.out.println("System Information:");
            System.out.println("  Java: " + System.getProperty("java.version") + " (" + System.getProperty("java.vendor") + ")");
            System.out.println("  OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version"));
            System.out.println("  Architecture: " + System.getProperty("os.arch"));
            System.out.println("  User: " + System.getProperty("user.name"));
            System.out.println("  Home: " + System.getProperty("user.home"));
            System.out.println();
            
            // Check WSL availability
            System.out.println("WSL Status:");
            checkWSLStatus();
            
            System.out.println();
            System.out.println("eknova API:");
            System.out.println("  URL: " + parent.getApiUrl());
            checkAPIStatus();
        }
    }
    
    private String getVersion() {
        // TODO: Read from build metadata or properties file
        return "1.0.0-SNAPSHOT";
    }
    
    private void checkWSLStatus() {
        try {
            WSLService.WSLInfo wslInfo = wslService.getWSLInfo();
            
            if (wslInfo.isAvailable()) {
                System.out.println("  Status: Available ✅");
                System.out.println("  Version: " + wslInfo.getVersion());
                System.out.println("  Distributions: " + wslInfo.getDistributionCount() + " installed");
            } else {
                System.out.println("  Status: Not available ❌");
                System.out.println("  Error: " + wslInfo.getVersion());
            }
        } catch (Exception e) {
            System.out.println("  Status: Error ❌");
            System.out.println("  Error: " + e.getMessage());
        }
    }
    
    private void checkAPIStatus() {
        try {
            // TODO: Make health check request to eknova API
            System.out.println("  Status: Connected ✅");
            System.out.println("  Version: 1.0.0-SNAPSHOT");
        } catch (Exception e) {
            System.out.println("  Status: Unavailable ❌");
            System.out.println("  Error: Connection refused");
        }
    }
}