#!/bin/bash

# eknova CLI Windows Testing Script
# This script builds the CLI in WSL and copies it to Windows for proper testing

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🏗️  eknova CLI Windows Testing Setup${NC}"
echo

# Change to the CLI directory
cd "$(dirname "$0")/eknova-cli"

echo -e "${YELLOW}🔨 Building eknova CLI for Windows compatibility...${NC}"

# Clean and build the project with Java 11 compatibility
if ./gradlew clean build -x test -q; then
    echo -e "${GREEN}✅ Build successful (Java 11 compatible)${NC}"
else
    echo -e "${RED}❌ Build failed${NC}"
    exit 1
fi

# Determine Windows user directory
WINDOWS_USER=$(cmd.exe /c "echo %USERNAME%" 2>/dev/null | tr -d '\r\n' || echo "User")
WINDOWS_TEMP="/mnt/c/Users/${WINDOWS_USER}/Desktop/eknova-test"

echo -e "${YELLOW}📁 Creating Windows test directory...${NC}"
mkdir -p "$WINDOWS_TEMP"

echo -e "${YELLOW}📋 Copying CLI to Windows...${NC}"
cp build/quarkus-app/quarkus-run.jar "$WINDOWS_TEMP/"
cp -r build/quarkus-app/lib "$WINDOWS_TEMP/"
cp -r build/quarkus-app/app "$WINDOWS_TEMP/"
cp -r build/quarkus-app/quarkus "$WINDOWS_TEMP/"

# Create a Windows batch file to run the CLI
cat > "$WINDOWS_TEMP/ekn.bat" << 'EOF'
@echo off
java -jar "%~dp0quarkus-run.jar" %*
EOF

echo -e "${GREEN}✅ CLI copied to Windows${NC}"
echo
echo -e "${BLUE}📍 Test Location:${NC} C:\\Users\\${WINDOWS_USER}\\Desktop\\eknova-test"
echo
echo -e "${YELLOW}🚀 To test from Windows PowerShell:${NC}"
echo "1. Open Windows PowerShell (not WSL)"
echo "2. Check Java version: java -version"
echo "   ${YELLOW}(Need Java 11+ for compatibility)${NC}"
echo "3. Navigate to: cd C:\\Users\\${WINDOWS_USER}\\Desktop\\eknova-test"
echo "4. Run: .\\ekn.bat --help"
echo "5. Test: .\\ekn.bat version --full"
echo "6. Test: .\\ekn.bat list"
echo
echo -e "${BLUE}📝 Java Requirements:${NC}"
echo "• Minimum: Java 11"
echo "• Recommended: Java 17 or Java 21 LTS"
echo "• Download: https://adoptium.net/temurin/releases/"
echo
echo -e "${GREEN}✨ Ready for Windows testing!${NC}"