#!/bin/bash

# Create Windows test package with Linux native executable
# We'll copy the Linux native binary and create a test setup

set -e

echo "🪟 Creating Windows test package for native executable..."

# Check if we have the Linux native executable
LINUX_EXE="build/eknova-cli-1.0.0-SNAPSHOT-runner"
if [ ! -f "$LINUX_EXE" ]; then
    echo "❌ Linux native executable not found. Building it first..."
    sudo ./gradlew build -Dquarkus.native.enabled=true -Dquarkus.package.jar.enabled=false -Dquarkus.native.container-build=true -Dquarkus.native.builder-image=quay.io/quarkus/ubi-quarkus-mandrel-builder-image:jdk-23
fi

if [ ! -f "$LINUX_EXE" ]; then
    echo "❌ Failed to create Linux native executable"
    exit 1
fi

echo "✅ Linux native executable ready"

# Create Windows test directory
WIN_TEST_DIR="/mnt/c/Users/burns/Desktop/eknova-test/native-test"
mkdir -p "$WIN_TEST_DIR"

# Copy the executable with .exe extension for Windows testing
cp "$LINUX_EXE" "$WIN_TEST_DIR/eknova-cli.exe"
echo "📁 Copied executable to: $WIN_TEST_DIR/eknova-cli.exe"

# Create test script
cat > "$WIN_TEST_DIR/test.bat" << 'EOF'
@echo off
echo ================================================
echo eknova CLI Native Executable Test
echo ================================================
echo.

echo File Information:
echo ------------------
dir eknova-cli.exe
echo.

echo Testing executable:
echo -------------------
echo.

echo 1. Testing --help flag:
eknova-cli.exe --help
echo.

echo 2. Testing version command:
eknova-cli.exe version
echo.

echo 3. Testing list command (requires WSL):
eknova-cli.exe list
echo.

echo ================================================
echo Test completed!
echo ================================================
pause
EOF

# Create PowerShell test script
cat > "$WIN_TEST_DIR/test.ps1" << 'EOF'
Write-Host "================================================" -ForegroundColor Cyan
Write-Host "eknova CLI Native Executable Test" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "File Information:" -ForegroundColor Yellow
Write-Host "------------------"
Get-ChildItem eknova-cli.exe | Format-Table Name, Length, LastWriteTime
Write-Host ""

Write-Host "Testing executable:" -ForegroundColor Yellow
Write-Host "-------------------"
Write-Host ""

try {
    Write-Host "1. Testing --help flag:" -ForegroundColor Green
    .\eknova-cli.exe --help
    Write-Host ""
    
    Write-Host "2. Testing version command:" -ForegroundColor Green
    .\eknova-cli.exe version
    Write-Host ""
    
    Write-Host "3. Testing list command (requires WSL):" -ForegroundColor Green
    .\eknova-cli.exe list
    Write-Host ""
} catch {
    Write-Host "Error running executable: $_" -ForegroundColor Red
}

Write-Host "================================================" -ForegroundColor Cyan
Write-Host "Test completed!" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan

Write-Host "Press any key to exit..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
EOF

# Create README
cat > "$WIN_TEST_DIR/README.md" << 'EOF'
# eknova CLI - Native Executable Windows Test

## About This Build

This package contains the eknova CLI native executable for testing on Windows.

**Important Note:** This is currently a Linux-based native executable that may or may not work on Windows through WSL compatibility layers. True Windows native compilation requires more complex cross-compilation setup.

## Files

- `eknova-cli.exe` - Native executable (renamed from Linux build)
- `test.bat` - Batch test script for Command Prompt
- `test.ps1` - PowerShell test script
- `README.md` - This file

## Testing Instructions

### Option 1: Command Prompt
```cmd
cd C:\Users\burns\Desktop\eknova-test\native-test
test.bat
```

### Option 2: PowerShell
```powershell
cd C:\Users\burns\Desktop\eknova-test\native-test
.\test.ps1
```

### Option 3: Manual Testing
```cmd
eknova-cli.exe --help
eknova-cli.exe version
eknova-cli.exe list
```

## Expected Results

- `--help`: Should display CLI usage information
- `version`: Should show "🚀 eknova 1.0.0-SNAPSHOT"
- `list`: Should either show WSL environments or indicate WSL is not available

## Requirements

- Windows 10/11 (64-bit)
- WSL2 installed (for environment management features)
- **No Java installation required!**

## Troubleshooting

If the executable doesn't work:

1. **Compatibility Issue**: This might be a Linux executable that doesn't run on Windows
2. **Windows Defender**: May block the executable - add an exception
3. **Permissions**: Try running as Administrator
4. **WSL Requirement**: The `list` command requires WSL2 to be installed

## Alternative Options

If this native executable doesn't work on Windows, you can use:

1. **JAR Distribution**: Located in `../windows-dist/` (requires Java 11+)
2. **WSL**: Run the Linux native executable from within WSL
3. **True Windows Native**: Requires complex cross-compilation setup

## File Information

- **Size**: ~309MB (self-contained, includes all dependencies)
- **Build**: GraalVM Native Image with Mandrel JDK 23
- **Architecture**: Designed for x86-64
EOF

# Show what we created
echo ""
echo "✅ Windows test package created successfully!"
echo "📁 Location: C:\\Users\\burns\\Desktop\\eknova-test\\native-test\\"
echo ""
echo "📋 Contents:"
ls -la "$WIN_TEST_DIR"
echo ""
echo "🧪 To test on Windows:"
echo "   1. Open Command Prompt or PowerShell"
echo "   2. Navigate to: C:\\Users\\burns\\Desktop\\eknova-test\\native-test\\"
echo "   3. Run: test.bat (Command Prompt) or .\\test.ps1 (PowerShell)"
echo ""
echo "⚠️  Note: This is a Linux native executable that may not work on Windows."
echo "    If it fails, use the JAR distribution in ../windows-dist/ instead."

# Show file information
echo ""
echo "📊 Executable information:"
ls -lh "$LINUX_EXE"
file "$LINUX_EXE" || echo "File type: Linux ELF executable"