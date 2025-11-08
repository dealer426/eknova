#!/bin/bash

# Test Windows-compatible CLI distribution
# This script provides instructions and creates a test package

set -e

echo "🧪 Testing Windows-compatible eknova CLI distribution"
echo ""

# Check if Windows distribution exists
if [ ! -d "build/windows-dist" ]; then
    echo "❌ Windows distribution not found!"
    echo "🔨 Please run './build-windows-native.sh' first to build the distribution."
    exit 1
fi

echo "✅ Windows distribution found: build/windows-dist/"

# Get distribution size
SIZE=$(du -sh build/windows-dist/ | cut -f1)
echo "📊 Distribution size: ${SIZE}"
echo ""

echo "� Distribution contents:"
ls -la build/windows-dist/
echo ""

echo "�🖥️ To test on Windows:"
echo ""
echo "1. Copy the entire windows-dist folder to your Windows machine:"
echo "   scp -r build/windows-dist/ user@windows-machine:/path/to/destination/"
echo ""
echo "2. On Windows, ensure Java 11+ is installed and in PATH"
echo "   Download from: https://adoptium.net/temurin/releases/"
echo ""
echo "3. Test from Command Prompt:"
echo "   cd \\path\\to\\windows-dist"
echo "   ekn.bat --help"
echo "   ekn.bat version"
echo ""
echo "4. Or test from PowerShell:"
echo "   cd C:\\path\\to\\windows-dist"
echo "   .\\ekn.ps1 --help"
echo "   .\\ekn.ps1 version"
echo ""
echo "5. Test WSL integration (requires WSL2 to be installed):"
echo "   ekn.bat list"
echo "   .\\ekn.ps1 list"
echo ""

echo "🔍 Advantages of this approach:"
echo "✅ Works on any Windows system with Java 11+"
echo "✅ No Docker or complex compilation required"
echo "✅ Full Quarkus application with all dependencies"
echo "✅ Easy to distribute and deploy"
echo "✅ Same WSL integration as native build"
echo ""

echo "🐛 If you encounter issues on Windows:"
echo "• Ensure Java 11+ is installed: java -version"
echo "• Ensure WSL2 is installed and configured"
echo "• Run 'wsl --list --verbose' in PowerShell to verify WSL distributions"
echo "• Try running as Administrator if permission issues occur"
echo ""

# Create a test archive for easy transfer
echo "📦 Creating test package for Windows..."
if command -v zip &> /dev/null; then
    cd build
    zip -r eknova-cli-windows-dist.zip windows-dist/
    echo "✅ Created test package: build/eknova-cli-windows-dist.zip"
    echo "📁 Contains the complete Windows distribution"
    ARCHIVE_SIZE=$(du -sh eknova-cli-windows-dist.zip | cut -f1)
    echo "📊 Archive size: ${ARCHIVE_SIZE}"
    cd ..
else
    echo "💡 Install zip to create a test package: sudo apt install zip"
fi

# Test the distribution locally (simulating Windows usage)
echo ""
echo "🧪 Testing distribution locally..."
echo "Testing Java version check..."
if java -version &> /dev/null; then
    echo "✅ Java is available"
    echo "Testing CLI execution..."
    if cd build/windows-dist && java -jar quarkus-run.jar --help; then
        echo "✅ CLI executes successfully"
    else
        echo "❌ CLI execution failed"
    fi
    cd ../..
else
    echo "❌ Java not available for local testing"
fi

echo ""
echo "🎉 Ready for Windows testing!"
echo "📋 This JAR-based approach is compatible with any Windows system that has Java 11+."