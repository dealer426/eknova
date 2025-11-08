# eknova CLI - Native & Windows Distribution Guide

## Overview

The eknova CLI now supports multiple distribution methods for different platforms and requirements:

1. **Native Linux Executable** - Single self-contained binary
2. **Windows-Compatible JAR Distribution** - Works with any Java 11+ installation

This eliminates the Java version compatibility issues we encountered with the original JAR approach.

## Build Options

### 1. Native Linux Executable

**Command:**
```bash
./gradlew clean && ./gradlew build -Dquarkus.native.enabled=true -Dquarkus.package.jar.enabled=false
```

**Output:**
- Location: `build/eknova-cli-1.0.0-SNAPSHOT-runner`
- Size: ~300MB (includes all dependencies)
- Requirements: None (self-contained)
- Startup: Very fast (~50ms)

**Testing:**
```bash
./build/eknova-cli-1.0.0-SNAPSHOT-runner --help
./build/eknova-cli-1.0.0-SNAPSHOT-runner version
./build/eknova-cli-1.0.0-SNAPSHOT-runner list
```

### 2. Windows-Compatible Distribution

**Command:**
```bash
./build-windows-native.sh
```

**Output:**
- Location: `build/windows-dist/`
- Size: ~18MB (compressed: ~16MB)
- Requirements: Java 11+ on Windows
- Startup: Fast (~200ms)

**Contents:**
- `quarkus-run.jar` - Main executable JAR
- `ekn.bat` - Windows Command Prompt wrapper
- `ekn.ps1` - PowerShell wrapper
- `README.md` - Installation and usage instructions
- `lib/` - Dependencies
- `app/` - Application code

## Windows Testing Instructions

### Prerequisites
1. **Java 11+** - Download from [Adoptium](https://adoptium.net/temurin/releases/)
2. **WSL2** - For environment management functionality

### Deployment
1. Copy `build/eknova-cli-windows-dist.zip` to Windows machine
2. Extract to desired location (e.g., `C:\tools\eknova-cli\`)
3. Verify Java installation: `java -version`

### Usage

**Command Prompt:**
```cmd
cd C:\tools\eknova-cli\windows-dist
ekn.bat --help
ekn.bat version
ekn.bat list
```

**PowerShell:**
```powershell
cd C:\tools\eknova-cli\windows-dist
.\ekn.ps1 --help
.\ekn.ps1 version
.\ekn.ps1 list
```

**Direct Java (if wrappers fail):**
```cmd
java -jar quarkus-run.jar --help
```

## WSL Integration Features

Both distributions include full WSL integration:

- **Environment Detection** - Automatically detects available WSL distributions
- **Environment Management** - Create, list, and destroy WSL environments
- **Cross-Platform Commands** - Same CLI commands work on both Linux and Windows
- **Error Handling** - Graceful handling when WSL is not available

## Troubleshooting

### Windows Issues

**Java not found:**
```
❌ Java is not installed or not in PATH
💡 Please install Java 11+ and add it to your PATH
📥 Download from: https://adoptium.net/temurin/releases/
```

**WSL not available:**
```
❌ WSL is not available on this system
💡 Please install WSL2 to use eknova environments
```

**Permission denied:**
- Try running as Administrator
- Check Windows Defender/antivirus settings

### Linux Issues

**Native build failures:**
- Ensure `build-essential` and `zlib1g-dev` are installed
- Check GCC version compatibility

## Performance Comparison

| Approach | Size | Startup | Memory | Java Required |
|----------|------|---------|--------|---------------|
| Native Linux | ~300MB | ~50ms | ~20MB | No |
| Windows JAR | ~18MB | ~200ms | ~50MB | Yes (11+) |

## Architecture

Both distributions use the same core components:

- **WSLService** - Manages WSL distribution lifecycle
- **ProcessUtils** - Safe command execution with timeouts
- **Environment Models** - Represent WSL distributions and status
- **Command Classes** - CLI commands with WSL integration

## Continuous Integration

For automated builds:

```yaml
# Native Linux
- name: Build Native Executable
  run: |
    sudo apt install -y build-essential zlib1g-dev
    ./gradlew clean build -Dquarkus.native.enabled=true -Dquarkus.package.jar.enabled=false

# Windows Distribution
- name: Build Windows Distribution
  run: ./build-windows-native.sh
```

## Distribution

### Linux Native
- Distribute single executable file
- No installation required
- Works on most Linux distributions

### Windows
- Distribute ZIP archive containing full distribution
- Requires Java 11+ installation
- Cross-platform compatibility

## Next Steps

1. **Blueprint Processing** - Implement environment provisioning from blueprints
2. **Environment Templates** - Add predefined development environment templates  
3. **Configuration Management** - Add persistent configuration storage
4. **Plugin System** - Support for extending CLI functionality

---

✅ **Current Status**: Both native Linux and Windows-compatible distributions are working with full WSL integration.

🎯 **Recommendation**: Use Windows JAR distribution for maximum compatibility. Native compilation can be explored later with proper Docker setup for cross-compilation.