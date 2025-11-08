#!/bin/bash

# Advanced Windows native executable builder using custom Docker setup
# This creates a Windows-compatible build environment with MinGW cross-compilation

set -e

echo "🪟 Building Windows native executable with custom toolchain..."

# Check if we're in the correct directory
if [ ! -f "build.gradle" ]; then
    echo "❌ Error: build.gradle not found. Please run this script from the eknova-cli directory."
    exit 1
fi

# Check if Docker is running with sudo access
if ! sudo docker info &> /dev/null; then
    echo "❌ Error: Docker is not accessible. Please ensure Docker is running."
    exit 1
fi

echo "✅ Docker is accessible"

# Create a custom Dockerfile for Windows cross-compilation
echo "📝 Creating custom Windows cross-compilation Docker image..."

cat > Dockerfile.windows << 'EOF'
FROM quay.io/quarkus/ubi-quarkus-mandrel-builder-image:jdk-23

USER root

# Install MinGW for Windows cross-compilation
RUN microdnf install -y mingw64-gcc mingw64-gcc-c++ mingw64-winpthreads-static && \
    microdnf clean all

# Set up Windows cross-compilation environment
ENV CC=x86_64-w64-mingw32-gcc
ENV CXX=x86_64-w64-mingw32-g++
ENV AR=x86_64-w64-mingw32-ar
ENV STRIP=x86_64-w64-mingw32-strip

USER 1001
EOF

echo "🏗️ Building custom Docker image with Windows toolchain..."
sudo docker build -t eknova-windows-builder -f Dockerfile.windows .

echo "🧹 Cleaning previous build..."
sudo rm -rf build/

echo "📦 Building Windows executable using custom image..."
sudo docker run --rm \
    -v "$(pwd)":/project \
    -w /project \
    eknova-windows-builder \
    bash -c "
        ./gradlew clean && \
        ./gradlew build \
            -Dquarkus.native.enabled=true \
            -Dquarkus.package.jar.enabled=false \
            -Dquarkus.native.additional-build-args='--static,--libc=musl,-H:+StaticExecutableWithDynamicLibC,-H:TargetPlatform=windows-amd64'
    "

# Check results
echo "🔍 Checking build results..."
if [ -f "build/eknova-cli-1.0.0-SNAPSHOT-runner" ]; then
    echo "✅ Executable created: build/eknova-cli-1.0.0-SNAPSHOT-runner"
    
    # Try to detect if it's Windows-compatible
    file build/eknova-cli-1.0.0-SNAPSHOT-runner
    
    # Rename to .exe for Windows
    cp build/eknova-cli-1.0.0-SNAPSHOT-runner build/eknova-cli-1.0.0-SNAPSHOT-runner.exe
    echo "📝 Created .exe version for Windows testing"
    
    # Copy to Windows test directory
    mkdir -p /mnt/c/Users/burns/Desktop/eknova-test/native-windows-advanced/
    cp build/eknova-cli-1.0.0-SNAPSHOT-runner.exe /mnt/c/Users/burns/Desktop/eknova-test/native-windows-advanced/
    
    # Create test files
    cat > /mnt/c/Users/burns/Desktop/eknova-test/native-windows-advanced/test.bat << 'EOF'
@echo off
echo Testing eknova CLI Windows native executable...
echo.
echo File info:
dir *.exe
echo.
echo Running --help:
eknova-cli-1.0.0-SNAPSHOT-runner.exe --help
echo.
echo Running version:
eknova-cli-1.0.0-SNAPSHOT-runner.exe version
echo.
pause
EOF
    
    echo "✅ Copied to Windows test directory"
    echo "📁 Location: C:\\Users\\burns\\Desktop\\eknova-test\\native-windows-advanced\\"
    
else
    echo "❌ Build failed - no executable found"
    exit 1
fi

# Cleanup
rm -f Dockerfile.windows

echo ""
echo "🎉 Advanced Windows build attempt complete!"
echo "🧪 Test on Windows by running: C:\\Users\\burns\\Desktop\\eknova-test\\native-windows-advanced\\test.bat"