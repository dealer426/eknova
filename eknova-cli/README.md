# eknova CLI

The `ekn` command-line interface for **eknova** - AI-powered WSL development environments.

Built with **Quarkus** for blazing-fast startup (<10ms) and small binary size (~25MB) using GraalVM native compilation.

## 🚀 Quick Start

```bash
# Basic usage
ekn --help                              # Show help
ekn version --full                      # System information

# Environment management  
ekn up @user/ml-cuda                    # Provision from blueprint
ekn create --base ubuntu --template go  # Create interactively
ekn list                                # List environments
ekn destroy my-env                      # Clean up environment
```

## 📋 Commands

### `ekn up <blueprint>`
Provision and start an environment from a blueprint.

```bash
ekn up @user/ml-cuda                    # From marketplace
ekn up ./my-blueprint.yaml             # From local file
ekn up --name custom-env @user/go-api   # With custom name
ekn up --force @user/python-ml          # Force recreation
```

### `ekn create`
Create a new environment interactively.

```bash
ekn create                              # Interactive mode
ekn create --base ubuntu --template python
ekn create --name my-env --output blueprint.yaml
```

### `ekn list`
List all local eknova environments.

```bash
ekn list                                # All environments
ekn list --running                      # Only running
ekn list --json                         # JSON output
```

### `ekn destroy <environment>`
Clean up and remove environments.

```bash
ekn destroy my-env                      # Remove specific
ekn destroy --all                       # Remove all
ekn destroy --force my-env              # Skip confirmation
```

### `ekn version`
Show version and system information.

```bash
ekn version                             # Basic version
ekn version --full                      # Detailed system info
```

## 🛠️ Development

### Running in Dev Mode

```bash
./gradlew quarkusDev
```

> **Note:** Dev mode enables live coding and includes a Dev UI at http://localhost:8080/q/dev/

### Testing Commands in Dev Mode

```bash
# Pass arguments to the CLI in dev mode
./gradlew quarkusDev --quarkus-args='list --help'
./gradlew quarkusDev --quarkus-args='version --full'
```

### Building

#### JVM Mode (Development)
```bash
./gradlew build
java -jar build/quarkus-app/quarkus-run.jar --help
```

#### Native Binary (Production)
```bash
./gradlew build -Dquarkus.native.enabled=true
./build/eknova-cli-1.0.0-SNAPSHOT-runner --help
```

#### Container Build (if no GraalVM locally)
```bash
./gradlew build -Dquarkus.native.enabled=true -Dquarkus.native.container-build=true
```

### Creating an Uber JAR
```bash
./gradlew build -Dquarkus.package.jar.type=uber-jar
java -jar build/eknova-cli-1.0.0-SNAPSHOT-runner.jar --help
```

## 🏗️ Architecture

```
eknova CLI (Quarkus + Picocli)
├── NovaCommand.java           # Main entry point
├── commands/                  # Subcommands
│   ├── UpCommand.java        # Provision environments
│   ├── CreateCommand.java    # Interactive creation
│   ├── ListCommand.java      # List environments
│   ├── DestroyCommand.java   # Clean up
│   └── VersionCommand.java   # Version info
├── client/                   # API clients
│   └── EknovaApiClient.java  # REST client for eknova-api
└── model/                    # Data models
    ├── Blueprint.java        # Blueprint definitions
    └── Environment.java      # Environment metadata
```

## 🔧 Configuration

Configuration is handled via `application.properties`:

```properties
# CLI configuration
quarkus.application.name=ekn
quarkus.picocli.top-command=dev.eknova.cli.NovaCommand

# API connection
eknova.api.base-url=http://localhost:5000
eknova.api.timeout=30s

# Logging
quarkus.log.level=INFO
quarkus.log.category."dev.eknova".level=DEBUG
```

## 🧪 Testing

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests="*NovaCommandTest"

# Run with coverage
./gradlew test jacocoTestReport
```

## 📦 Dependencies

- **Quarkus Core** - Application framework
- **Picocli** - Command line interface framework
- **REST Client** - HTTP client for nova-api communication
- **Jackson** - JSON serialization
- **Config YAML** - YAML configuration support

## 🔗 Integration

The CLI communicates with the **eknova-api** (Aspire .NET) backend for:
- Blueprint validation and retrieval
- Environment lifecycle management
- WSL orchestration commands
- Metadata storage and retrieval

## 📚 Related Documentation

- [Quarkus CLI Guide](https://quarkus.io/guides/picocli)
- [Quarkus Native Guide](https://quarkus.io/guides/building-native-image)
- [GraalVM Native Image](https://www.graalvm.org/latest/reference-manual/native-image/)
- [Picocli Documentation](https://picocli.info/)
