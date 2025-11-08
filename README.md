# eknova - AI-Powered WSL Development Environments

> **Spin up WSL dev environments in <30 seconds with AI-generated blueprints**

![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Java](https://img.shields.io/badge/Java-23-orange.svg)
![.NET](https://img.shields.io/badge/.NET-9.0-purple.svg)
![Node.js](https://img.shields.io/badge/Node.js-20+-green.svg)

---

## 🚀 What is eknova?

**eknova** is a hybrid CLI + API + Web platform that uses AI to generate and provision **WSL (Windows Subsystem for Linux) development environments** in seconds. Think of it as "Docker Compose for dev environments" but WSL-native, AI-powered, and blazing fast.

### Key Features

- 🤖 **AI Blueprint Generation** - Natural language to dev environment: *"Ubuntu + Python ML + Jupyter + GPU"*
- ⚡ **Sub-30s Provisioning** - WSL2 + containers for lightning-fast environment setup
- 🎯 **Hybrid Architecture** - Quarkus CLI (Java) + Aspire API (.NET) + Next.js Web UI
- 📦 **Shareable Blueprints** - `@username/ml-cuda-v2` style marketplace
- 🪟 **WSL-Native** - Optimized for Windows developers using Linux toolchains
- 🔧 **Local-First** - Your environments, your data, your control

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────┐
│                 Web UI (Next.js)                        │
└───────────────┬─────────────────────────────────────────┘
                │
                ▼
┌─────────────────────────────────────────────────────────┐
│           API Layer (Aspire .NET)                       │
│  ┌─────────────────┐  ┌──────────────────────────┐    │
│  │ Blueprint CRUD  │  │  AI Orchestrator          │    │
│  │                 │  │  - Semantic Kernel        │    │
│  └─────────────────┘  └──────────────────────────┘    │
└───────────────┬─────────────────────────────────────────┘
                │
                ▼
┌─────────────────────────────────────────────────────────┐
│            CLI (Quarkus Native)                         │
│            - 25MB binary                                │
│            - <10ms startup                              │
└───────────────┬─────────────────────────────────────────┘
                │
                ▼
┌─────────────────────────────────────────────────────────┐
│              WSL Orchestrator                           │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐ │
│  │ wsl --import │  │ docker run   │  │ VS Code      │ │
│  └──────────────┘  └──────────────┘  └──────────────┘ │
└─────────────────────────────────────────────────────────┘
```

---

## 🛠️ Project Structure

```
eknova/
├── eknova-cli/                  # Quarkus CLI (Java 23 + Gradle)
│   ├── src/main/java/dev/eknova/cli/
│   │   ├── NovaCommand.java     # Main CLI entry point
│   │   ├── commands/            # CLI subcommands
│   │   └── client/              # API client
│   ├── build.gradle             # Gradle build with native compilation
│   └── README.md
│
├── eknova-api/                  # Aspire API (.NET 9.0)
│   ├── eknova-api.AppHost/      # Aspire orchestration
│   ├── eknova-api.ApiService/   # REST API + AI services
│   ├── eknova-api.ServiceDefaults/ # Shared configuration
│   ├── eknova-api.Web/          # Blazor admin UI (optional)
│   └── eknova-api.sln
│
├── eknova-web/                  # Next.js Web UI
│   ├── app/                     # App Router pages
│   ├── components/              # React components
│   ├── lib/                     # Utilities and API clients
│   ├── public/                  # Static assets
│   └── package.json
│
├── blueprints/                  # Sample blueprint definitions
├── docs/                        # Documentation
├── scripts/                     # Development scripts
└── README.md                    # This file
```

---

## 🚦 Quick Start

### Prerequisites

- **Windows 11** with WSL2 enabled
- **Docker Desktop** with WSL2 backend
- **Development tools** (automated installer available)

### 1. Install Development Environment

```bash
# Download and run the setup script
curl -fsSL https://raw.githubusercontent.com/your-username/eknova/main/scripts/setup-dev-env.sh | bash

# Or clone and run manually
git clone https://github.com/your-username/eknova.git
cd eknova
./scripts/setup-dev-env.sh
```

This installs:
- ✅ .NET 9.0 SDK + Aspire templates
- ✅ Java 23 + GraalVM
- ✅ Node.js 20+ LTS
- ✅ Quarkus CLI
- ✅ Azure CLI + GitHub CLI
- ✅ PostgreSQL + pgvector

### 2. Build and Run

```bash
# Terminal 1: Start the API (Aspire)
cd nova-api
dotnet run --project nova-api.AppHost
# Dashboard: https://localhost:18888

# Terminal 2: Build the CLI (Quarkus)
cd eknova-cli
./gradlew build -Dquarkus.native.enabled=true
# Binary: build/eknova-cli-1.0.0-SNAPSHOT-runner

# Terminal 3: Start the Web UI (Next.js)  
cd eknova-web
npm install && npm run dev
# Web UI: http://localhost:3000
```

### 3. First Blueprint

```bash
# Use the CLI to create your first environment
ekn up @templates/ubuntu-python

# Or create interactively
ekn create --base ubuntu --template python
```

---

## 🎯 CLI Usage

```bash
# Core commands
ekn up @user/blueprint-name              # Provision environment
ekn create --base ubuntu --template python # Create from components  
ekn list                                 # List local environments
ekn destroy env-name                     # Clean up environment

# Examples
ekn up @user/ml-cuda                     # Provision ML environment with CUDA
ekn create --name my-go-env --base ubuntu --template go
ekn list --running                       # Show only running environments
ekn destroy --all --force               # Clean up all environments
```

---

## 🌐 Web UI Features

- **Blueprint Marketplace** - Browse, search, and fork community blueprints
- **Visual Editor** - Drag-and-drop blueprint composition
- **AI Chat Interface** - Natural language blueprint generation
- **Environment Dashboard** - Monitor running environments
- **Team Collaboration** - Share blueprints within organizations

---

## 🧠 AI Integration

eknova uses a dual AI approach:

### Semantic Kernel (.NET)
- **Blueprint Generation** - Converts prompts to YAML blueprints
- **RAG Search** - Semantic search over blueprint marketplace
- **Azure OpenAI** - Production AI backend

### LangChain4j (Java)  
- **CLI Intelligence** - Smart command suggestions
- **Error Resolution** - AI-powered troubleshooting
- **Local Models** - Ollama for offline development

---

## 📚 Development

### Building Components

```bash
# CLI (Native binary)
cd eknova-cli
./gradlew build -Dquarkus.native.enabled=true

# API (Aspire)
cd eknova-api  
dotnet build

# Web UI
cd eknova-web
npm run build
```

### Running in Development

```bash
# CLI (JVM mode for faster iteration)
cd eknova-cli && ./gradlew quarkusDev

# API (Hot reload)
cd eknova-api && dotnet watch --project eknova-api.AppHost

# Web UI (Hot reload)
cd eknova-web && npm run dev
```

### Testing

```bash
# CLI tests
cd eknova-cli && ./gradlew test

# API tests  
cd eknova-api && dotnet test

# Web UI tests
cd eknova-web && npm test
```

---

## 🤝 Contributing

We welcome contributions! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

### Areas We Need Help

- 🐛 **Bug fixes** - WSL integration edge cases
- 📝 **Documentation** - Tutorials and guides  
- 🎨 **Web UI** - Blueprint editor improvements
- 🔧 **Blueprints** - Community templates
- 🌍 **Internationalization** - Multi-language support
- 🧪 **Testing** - Integration and E2E tests

---

## 📄 License

MIT License - see [LICENSE](LICENSE) for details.

---

## 🙏 Acknowledgments

- **Microsoft** - Aspire, Semantic Kernel, WSL2
- **Red Hat** - Quarkus framework
- **Vercel** - Next.js framework
- **Community** - All the amazing contributors

---

## 📬 Community

- 💬 **Discord** - [Join our community](https://discord.gg/eknova-dev)
- 🐦 **Twitter** - [@eknova_dev](https://twitter.com/eknova_dev)
- 📧 **Email** - [hello@eknova.dev](mailto:hello@eknova.dev)
- 📖 **Docs** - [docs.eknova.dev](https://docs.eknova.dev)

---

**eknova** - *Your environments, your way, instantly.* ⚡

Built with ❤️ for the Windows + WSL developer community.