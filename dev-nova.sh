#!/bin/bash

# Development helper script for running nova CLI
# Usage: ./dev-nova.sh [command] [args...]

set -e

CLI_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/nova-cli" && pwd)"
BUILD_DIR="$CLI_DIR/build"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[nova-dev]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[nova-dev]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[nova-dev]${NC} $1"
}

print_error() {
    echo -e "${RED}[nova-dev]${NC} $1"
}

# Check if we're in the right directory
if [ ! -f "nova-cli/build.gradle" ]; then
    print_error "This script must be run from the eknova project root directory"
    exit 1
fi

# Function to build the CLI if needed
build_if_needed() {
    if [ ! -f "$BUILD_DIR/quarkus-app/quarkus-run.jar" ]; then
        print_status "Building nova CLI..."
        cd "$CLI_DIR"
        ./gradlew build -x test -q
        cd - > /dev/null
        print_success "Build complete"
    fi
}

# Function to run the CLI
run_cli() {
    build_if_needed
    
    print_status "Running: nova $*"
    cd "$CLI_DIR"
    java -jar "$BUILD_DIR/quarkus-app/quarkus-run.jar" "$@"
    cd - > /dev/null
}

# Function to build native binary
build_native() {
    print_status "Building native binary (this may take a few minutes)..."
    cd "$CLI_DIR"
    ./gradlew build -Dquarkus.native.enabled=true
    cd - > /dev/null
    print_success "Native binary created: $BUILD_DIR/nova-cli-1.0.0-SNAPSHOT-runner"
}

# Function to run native binary
run_native() {
    local native_binary="$BUILD_DIR/nova-cli-1.0.0-SNAPSHOT-runner"
    
    if [ ! -f "$native_binary" ]; then
        print_error "Native binary not found. Run './dev-nova.sh build-native' first."
        exit 1
    fi
    
    print_status "Running native: nova $*"
    "$native_binary" "$@"
}

# Function to clean build artifacts
clean() {
    print_status "Cleaning build artifacts..."
    cd "$CLI_DIR"
    ./gradlew clean -q
    cd - > /dev/null
    print_success "Clean complete"
}

# Function to run in dev mode
dev_mode() {
    print_status "Starting Quarkus dev mode..."
    print_warning "Use Ctrl+C to stop dev mode"
    cd "$CLI_DIR"
    ./gradlew quarkusDev
}

# Function to show help
show_help() {
    echo "eknova CLI Development Helper"
    echo ""
    echo "Usage: $0 [COMMAND] [CLI_ARGS...]"
    echo ""
    echo "Commands:"
    echo "  run [args...]     Build and run CLI with JVM (default)"
    echo "  native [args...]  Run native binary (build with build-native first)"
    echo "  build-native      Build native binary"
    echo "  dev               Start Quarkus dev mode"
    echo "  clean             Clean build artifacts"
    echo "  help              Show this help"
    echo ""
    echo "Examples:"
    echo "  $0 run --help                    # Show CLI help"
    echo "  $0 run list                      # List environments"
    echo "  $0 run version --full            # Show detailed version info"
    echo "  $0 run create --base ubuntu      # Create environment"
    echo "  $0 build-native                  # Build native binary"
    echo "  $0 native --help                 # Run native binary"
    echo "  $0 dev                           # Start dev mode"
    echo ""
    echo "Note: This script must be run from the eknova project root directory."
}

# Main command handling
case "${1:-run}" in
    "run")
        shift
        run_cli "$@"
        ;;
    "native")
        shift
        run_native "$@"
        ;;
    "build-native")
        build_native
        ;;
    "dev")
        dev_mode
        ;;
    "clean")
        clean
        ;;
    "help"|"--help"|"-h")
        show_help
        ;;
    *)
        # Default to run if first arg looks like a CLI command
        run_cli "$@"
        ;;
esac