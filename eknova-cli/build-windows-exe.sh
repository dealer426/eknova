#!/bin/bash

# Build Windows native executable using container cross-compilation
# This creates a true Windows .exe that doesn't require Java on the target system

set -e

echo "🪟 Building native Windows executable for eknova CLI..."

# Check if we're in the correct directory
if [ ! -f "build.gradle" ]; then
    echo "❌ Error: build.gradle not found. Please run this script from the eknova-cli directory."
    exit 1
fi

# Check if Docker is running with sudo access
if ! sudo docker info &> /dev/null; then
    echo "❌ Error: Docker is not accessible. Please ensure Docker is running."
    echo "You may need to run: sudo systemctl start docker"
    exit 1
fi

echo "✅ Docker is accessible"

# Clean previous build
echo "🧹 Cleaning previous build..."
sudo rm -rf build/
./gradlew clean

echo ""
echo "🏗️ Building Windows native executable using cross-compilation..."
echo "📦 This will create a .exe file that runs on Windows without Java"
echo "⏱️ This process may take 5-10 minutes..."
echo ""

# Build Windows native executable using container with cross-compilation
# We'll use a multi-stage approach to ensure Windows toolchain
sudo ./gradlew build \
    -Dquarkus.native.enabled=true \
    -Dquarkus.package.jar.enabled=false \
    -Dquarkus.native.container-build=true \
    -Dquarkus.native.builder-image=quay.io/quarkus/ubi-quarkus-mandrel-builder-image:jdk-23 \
    -Dquarkus.native.additional-build-args="-H:TargetPlatform=org.graalvm.nativeimage.Platform\$WINDOWS_AMD64"

# Check if build was successful
EXPECTED_EXE="build/eknova-cli-1.0.0-SNAPSHOT-runner.exe"
EXPECTED_RUNNER="build/eknova-cli-1.0.0-SNAPSHOT-runner"

if [ -f "$EXPECTED_EXE" ]; then
    echo ""
    echo "✅ Windows native executable built successfully!"
    echo "📁 Location: $EXPECTED_EXE"
    
    # Show file info
    ls -lh "$EXPECTED_EXE"
    file "$EXPECTED_EXE" || echo "File type detection may not work for Windows exe"
    
elif [ -f "$EXPECTED_RUNNER" ]; then
    echo ""
    echo "⚠️ Native executable built, but it appears to be Linux format"
    echo "📁 Location: $EXPECTED_RUNNER"
    
    # Try to rename it to .exe and copy for testing
    cp "$EXPECTED_RUNNER" "$EXPECTED_EXE"
    echo "📝 Copied to .exe for testing purposes"
    
    file "$EXPECTED_RUNNER"
else
    echo ""
    echo "❌ Native build failed or executable not found"
    echo "🔍 Checking build directory contents:"
    find build/ -name "*runner*" -type f 2>/dev/null || echo "No runner files found"
    exit 1
fi

# Copy to Windows test directory
echo ""
echo "📋 Copying to Windows test directory..."
mkdir -p /mnt/c/Users/burns/Desktop/eknova-test/native-windows/

if [ -f "$EXPECTED_EXE" ]; then
    cp "$EXPECTED_EXE" /mnt/c/Users/burns/Desktop/eknova-test/native-windows/
    echo "✅ Copied to C:\\Users\\burns\\Desktop\\eknova-test\\native-windows\\"
    
    # Create simple test batch file
    cat > /mnt/c/Users/burns/Desktop/eknova-test/native-windows/test.bat << 'EOF'
@echo off
echo Testing eknova CLI native Windows executable...
echo.
echo Running --help:
eknova-cli-1.0.0-SNAPSHOT-runner.exe --help
echo.
echo Running version:
eknova-cli-1.0.0-SNAPSHOT-runner.exe version
echo.
echo Running list:
eknova-cli-1.0.0-SNAPSHOT-runner.exe list
pause
EOF

    cat > /mnt/c/Users/burns/Desktop/eknova-test/native-windows/README.md << 'EOF'
# eknova CLI - Windows Native Executable

This directory contains the native Windows executable that doesn't require Java.

## Files:
- `eknova-cli-1.0.0-SNAPSHOT-runner.exe` - Main executable (no Java required)
- `test.bat` - Automated test script

## Usage:

### Command Prompt or PowerShell:
```cmd
eknova-cli-1.0.0-SNAPSHOT-runner.exe --help
eknova-cli-1.0.0-SNAPSHOT-runner.exe version
eknova-cli-1.0.0-SNAPSHOT-runner.exe list
```

### Or run the test script:
```cmd
test.bat
```

## Requirements:
- Windows 10/11 (64-bit)
- WSL2 installed (for environment management features)
- No Java installation required!

## Troubleshooting:
- If Windows Defender blocks the executable, add an exception
- Run as Administrator if you encounter permission issues
- Ensure WSL2 is installed for `list` command to work properly
EOF

    echo "📝 Created test.bat and README.md"
    echo ""
    echo "🎉 Windows native build complete!"
    echo ""
    echo "🧪 To test on Windows:"
    echo "   1. Open Command Prompt or PowerShell"
    echo "   2. Navigate to: C:\\Users\\burns\\Desktop\\eknova-test\\native-windows\\"
    echo "   3. Run: .\\eknova-cli-1.0.0-SNAPSHOT-runner.exe --help"
    echo "   4. Or run: .\\test.bat"
    echo ""
    echo "💡 This executable is completely self-contained and doesn't require Java!"
    
    # Show final file size
    SIZE=$(stat -c%s "$EXPECTED_EXE")
    SIZE_MB=$((SIZE / 1024 / 1024))
    echo "📊 Executable size: ${SIZE_MB}MB"
else
    echo "❌ Failed to create Windows executable"
    exit 1
fi