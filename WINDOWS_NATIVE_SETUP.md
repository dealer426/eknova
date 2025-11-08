# eknova CLI - Windows Native Development Setup Guide

## Overview

This guide provides complete instructions for setting up Windows native development for the eknova CLI, enabling you to build true Windows `.exe` files without Java dependencies.

## Prerequisites

### Required Software

1. **Windows 10/11 (64-bit)**
2. **Git for Windows**
3. **Java Development Kit (JDK 21+)**
4. **Visual Studio Build Tools 2022**
5. **GraalVM Native Image**

## Step 1: Install Git for Windows

```powershell
# Using winget (recommended)
winget install Git.Git

# Or download from: https://git-scm.com/download/win
```

## Step 2: Install Java Development Kit

### Option A: Eclipse Temurin (Recommended)
```powershell
# Install JDK 21 (LTS)
winget install EclipseAdoptium.Temurin.21.JDK

# Or JDK 23 (latest)
winget install EclipseAdoptium.Temurin.23.JDK
```

### Option B: Oracle JDK
```powershell
winget install Oracle.JDK.21
```

### Verify Installation:
```cmd
java -version
javac -version
```

## Step 3: Install Visual Studio Build Tools

Native compilation requires Microsoft C++ build tools.

### Option A: Build Tools Only (Lightweight)
```powershell
winget install Microsoft.VisualStudio.2022.BuildTools
```

### Option B: Full Visual Studio Community (Full IDE)
```powershell
winget install Microsoft.VisualStudio.2022.Community
```

### Manual Installation:
1. Download from: https://visualstudio.microsoft.com/downloads/
2. Select "Build Tools for Visual Studio 2022"
3. Install with "C++ build tools" workload

## Step 4: Install GraalVM Native Image

### Option A: Using GraalVM Distribution

1. **Download GraalVM:**
   - Go to: https://github.com/graalvm/graalvm-ce-builds/releases
   - Download: `graalvm-jdk-23_windows-x64_bin.zip`
   - Extract to: `C:\Program Files\GraalVM\`

2. **Set Environment Variables:**
   ```cmd
   setx JAVA_HOME "C:\Program Files\GraalVM\graalvm-jdk-23.0.1+11.1"
   setx PATH "%PATH%;%JAVA_HOME%\bin"
   ```

3. **Install Native Image Component:**
   ```cmd
   gu install native-image
   ```

### Option B: Using Existing JDK + Native Image

If you already have a JDK installed:

```cmd
# Download and install native-image tool
# This varies by JDK distribution - check documentation
```

### Verify Installation:
```cmd
native-image --version
```

## Step 5: Clone and Setup Project

### Clone Repository:
```cmd
# Open Command Prompt or PowerShell
cd C:\Development
git clone https://github.com/dealer426/eknova.git
cd eknova\eknova-cli
```

### Verify Gradle:
```cmd
# Test Gradle wrapper
gradlew.bat --version
```

## Step 6: Build Native Windows Executable

### Standard Build:
```cmd
# Build native Windows executable
gradlew.bat build -Dquarkus.native.enabled=true -Dquarkus.package.jar.enabled=false
```

### Alternative Build (if issues):
```cmd
# Clean and build
gradlew.bat clean
gradlew.bat build -Dquarkus.native.enabled=true
```

### Expected Output:
- Location: `build\eknova-cli-1.0.0-SNAPSHOT-runner.exe`
- Size: ~300MB (self-contained)
- Type: Windows PE executable

## Step 7: Test the Executable

```cmd
# Navigate to build directory
cd build

# Test basic functionality
eknova-cli-1.0.0-SNAPSHOT-runner.exe --help
eknova-cli-1.0.0-SNAPSHOT-runner.exe version

# Test WSL integration (requires WSL2)
eknova-cli-1.0.0-SNAPSHOT-runner.exe list
```

## Environment Variables Summary

Add these to your Windows PATH and environment:

```cmd
# Java/GraalVM
JAVA_HOME=C:\Program Files\GraalVM\graalvm-jdk-23.0.1+11.1
PATH=%PATH%;%JAVA_HOME%\bin

# Git (usually set automatically)
PATH=%PATH%;C:\Program Files\Git\bin

# Visual Studio Build Tools (usually set automatically)
PATH=%PATH%;C:\Program Files (x86)\Microsoft Visual Studio\2022\BuildTools\MSBuild\Current\Bin
```

## Troubleshooting

### Build Fails with "C compiler not found"
- Ensure Visual Studio Build Tools are installed
- Run build from "Developer Command Prompt for VS 2022"
- Or run: `call "C:\Program Files (x86)\Microsoft Visual Studio\2022\BuildTools\VC\Auxiliary\Build\vcvars64.bat"`

### Native Image Tool Not Found
```cmd
# Verify native-image installation
where native-image
native-image --version

# If missing, reinstall:
gu install native-image
```

### Permission Errors
- Run Command Prompt or PowerShell as Administrator
- Check Windows Defender exclusions
- Ensure antivirus isn't blocking the build

### Out of Memory During Build
```cmd
# Increase heap size
set GRADLE_OPTS=-Xmx8g
gradlew.bat build -Dquarkus.native.enabled=true
```

## Quick Reference Commands

### Development Workflow:
```cmd
# 1. Pull latest changes
git pull origin dev

# 2. Clean build
gradlew.bat clean

# 3. Build native executable
gradlew.bat build -Dquarkus.native.enabled=true -Dquarkus.package.jar.enabled=false

# 4. Test executable
build\eknova-cli-1.0.0-SNAPSHOT-runner.exe --help
```

### Build Variations:
```cmd
# JAR build (requires Java runtime)
gradlew.bat build

# Native build (no Java required)
gradlew.bat build -Dquarkus.native.enabled=true

# Native with debugging info
gradlew.bat build -Dquarkus.native.enabled=true -Dquarkus.native.debug.enabled=true
```

## Project Structure

```
eknova\
├── eknova-cli\               # CLI project root
│   ├── src\main\java\        # Java source code
│   ├── src\main\resources\   # Configuration files
│   ├── build.gradle          # Build configuration
│   ├── gradle\wrapper\       # Gradle wrapper files
│   ├── gradlew.bat          # Windows Gradle wrapper
│   └── build\               # Build output
│       └── eknova-cli-1.0.0-SNAPSHOT-runner.exe
├── eknova-aspire\           # .NET Aspire API
└── eknova-web\              # Next.js Web Interface
```

## Key Files for Development

### Configuration:
- `src\main\resources\application.properties` - Quarkus configuration
- `build.gradle` - Build and dependency configuration

### Source Code:
- `src\main\java\dev\eknova\cli\` - Main CLI code
- `src\main\java\dev\eknova\cli\commands\` - CLI command implementations
- `src\main\java\dev\eknova\cli\service\` - Business logic services

## When Sharing Context with AI Assistant

When opening this project in a Windows environment and working with an AI assistant, provide:

1. **Environment Details:**
   ```
   - OS: Windows 11 (or 10)
   - Java Version: [output of `java -version`]
   - GraalVM Version: [output of `native-image --version`]
   - Current Directory: [current working directory]
   ```

2. **Project Status:**
   ```
   - Branch: [current git branch]
   - Last Build: [when you last built successfully]
   - Current Issues: [any build errors or issues]
   ```

3. **Build Command Used:**
   ```
   gradlew.bat build -Dquarkus.native.enabled=true -Dquarkus.package.jar.enabled=false
   ```

4. **Expected Output Location:**
   ```
   build\eknova-cli-1.0.0-SNAPSHOT-runner.exe
   ```

## Success Criteria

✅ **You have successfully set up Windows native development when:**

1. `java -version` shows JDK 21+ or GraalVM
2. `native-image --version` works without error
3. `gradlew.bat --version` shows Gradle information
4. Build creates `build\eknova-cli-1.0.0-SNAPSHOT-runner.exe`
5. Executable runs with `--help` flag
6. Executable shows version with `version` command
7. File size is approximately 300MB (self-contained)

## Next Steps After Setup

1. **Test WSL Integration:** Ensure `list` command works with WSL2
2. **Performance Testing:** Compare startup times vs JAR version
3. **Distribution:** Package for deployment to other Windows machines
4. **CI/CD Integration:** Set up automated Windows builds

---

📝 **Save this document as:** `WINDOWS_NATIVE_SETUP.md`

🔄 **Keep this updated** as the project evolves and build requirements change.

📤 **Share with AI Assistant** when working on Windows to provide complete context.