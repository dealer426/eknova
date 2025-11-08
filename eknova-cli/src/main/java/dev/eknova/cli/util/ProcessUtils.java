package dev.eknova.cli.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for executing external processes, particularly WSL commands
 */
public class ProcessUtils {
    
    private static final int DEFAULT_TIMEOUT_SECONDS = 30;
    
    /**
     * Execute a command and return the result
     */
    public static ProcessResult execute(String... command) {
        return execute(DEFAULT_TIMEOUT_SECONDS, command);
    }
    
    /**
     * Execute a command with a timeout and return the result
     */
    public static ProcessResult execute(int timeoutSeconds, String... command) {
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true); // Merge stderr into stdout
            
            Process process = pb.start();
            
            // Read output
            List<String> output = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.add(line);
                }
            }
            
            // Wait for completion with timeout
            boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
            
            if (!finished) {
                process.destroyForcibly();
                return new ProcessResult(false, -1, output, "Process timed out after " + timeoutSeconds + " seconds");
            }
            
            int exitCode = process.exitValue();
            return new ProcessResult(exitCode == 0, exitCode, output, null);
            
        } catch (IOException | InterruptedException e) {
            return new ProcessResult(false, -1, new ArrayList<>(), "Process execution failed: " + e.getMessage());
        }
    }
    
    /**
     * Check if a command is available on the system
     */
    public static boolean isCommandAvailable(String command) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            String[] checkCommand;
            
            if (os.contains("win")) {
                checkCommand = new String[]{"where", command};
            } else {
                checkCommand = new String[]{"which", command};
            }
            
            ProcessResult result = execute(5, checkCommand);
            return result.isSuccess();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Result of a process execution
     */
    public static class ProcessResult {
        private final boolean success;
        private final int exitCode;
        private final List<String> output;
        private final String error;
        
        public ProcessResult(boolean success, int exitCode, List<String> output, String error) {
            this.success = success;
            this.exitCode = exitCode;
            this.output = output;
            this.error = error;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public int getExitCode() {
            return exitCode;
        }
        
        public List<String> getOutput() {
            return output;
        }
        
        public String getOutputAsString() {
            return String.join("\n", output);
        }
        
        public String getError() {
            return error;
        }
        
        public boolean hasOutput() {
            return !output.isEmpty();
        }
        
        @Override
        public String toString() {
            return "ProcessResult{" +
                    "success=" + success +
                    ", exitCode=" + exitCode +
                    ", outputLines=" + output.size() +
                    ", error='" + error + '\'' +
                    '}';
        }
    }
}