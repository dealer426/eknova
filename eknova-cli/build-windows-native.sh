#!/bin/bash

# Build Windows-compatible CLI using Java JAR approach
# Since cross-compilation to Windows requires complex Docker setup,
# we'll create a Windows JAR distribution with a batch wrapper

set -e

echo "🔨 Building Windows-compatible eknova CLI..."

# Check if we're in the correct directory
if [ ! -f "build.gradle" ]; then
    echo "❌ Error: build.gradle not found. Please run this script from the eknova-cli directory."
    exit 1
fi

# Clean previous build
echo "🧹 Cleaning previous build..."
./gradlew clean

# Build regular JAR for Windows
echo "🏗️ Building JAR for Windows compatibility..."
./gradlew build

# Check if build was successful
if [ -f "build/quarkus-app/quarkus-run.jar" ]; then
    echo "✅ JAR built successfully!"
    
    # Create Windows distribution directory
    mkdir -p build/windows-dist
    
    # Copy the Quarkus app
    cp -r build/quarkus-app/* build/windows-dist/
    
    # Create Windows batch script
    cat > build/windows-dist/ekn.bat << 'EOF'
@echo off
setlocal

REM Check if Java is available
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Java is not installed or not in PATH
    echo 💡 Please install Java 11+ and add it to your PATH
    echo 📥 Download from: https://adoptium.net/temurin/releases/
    exit /b 1
)

REM Run the CLI
java -jar "%~dp0quarkus-run.jar" %*
EOF

    # Create PowerShell script as alternative
    cat > build/windows-dist/ekn.ps1 << 'EOF'
# Check if Java is available
try {
    $javaVersion = java -version 2>&1
    if ($LASTEXITCODE -ne 0) {
        throw "Java not found"
    }
} catch {
    Write-Host "❌ Java is not installed or not in PATH" -ForegroundColor Red
    Write-Host "� Please install Java 11+ and add it to your PATH" -ForegroundColor Yellow
    Write-Host "📥 Download from: https://adoptium.net/temurin/releases/" -ForegroundColor Cyan
    exit 1
}

# Run the CLI
$jarPath = Join-Path $PSScriptRoot "quarkus-run.jar"
java -jar $jarPath @args
EOF

    # Create README for Windows
    cat > build/windows-dist/README.md << 'EOF'
# eknova CLI for Windows

This directory contains the eknova CLI for Windows.

## Requirements

- Java 11 or higher
- WSL2 (for environment management)

## Usage

### Option 1: Batch Script (Command Prompt)
```cmd
ekn.bat --help
ekn.bat list
ekn.bat version
```

### Option 2: PowerShell Script
```powershell
.\ekn.ps1 --help
.\ekn.ps1 list
.\ekn.ps1 version
```

### Option 3: Direct Java
```cmd
java -jar quarkus-run.jar --help
```

## Installation

1. Ensure Java 11+ is installed and in your PATH
2. Ensure WSL2 is installed for environment management
3. Run `ekn.bat --help` to verify installation

## Troubleshooting

- If Java is not found, install from: https://adoptium.net/temurin/releases/
- If WSL commands fail, ensure WSL2 is properly installed
- Run from PowerShell or Command Prompt as Administrator if needed
EOF

    echo "✅ Windows distribution created successfully!"
    echo "📁 Location: build/windows-dist/"
    echo ""
    echo "📋 Contents:"
    ls -la build/windows-dist/
    echo ""
    echo "🚀 To test on Windows:"
    echo "   1. Copy the entire build/windows-dist/ folder to your Windows machine"
    echo "   2. Ensure Java 11+ is installed and in PATH"
    echo "   3. Run from Command Prompt: ekn.bat --help"
    echo "   4. Or from PowerShell: .\\ekn.ps1 --help"
    echo "   5. Test WSL integration: ekn.bat list"
    echo ""
    
    # Show sizes
    echo "📊 Distribution size:"
    du -sh build/windows-dist/
    
else
    echo "❌ JAR build failed. Check the output above for errors."
    exit 1
fi

echo ""
echo "🎉 Windows-compatible CLI distribution complete!"
echo "💡 This approach works with any Windows system that has Java 11+ installed."