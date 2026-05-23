# Vehicle Maintenance KMP

Aplicativo Kotlin Multiplatform para consulta e registro de histórico de manutenção de veículos, construído como um projeto de portfólio profissional com foco em arquitetura mobile moderna, integração com backend e evolução incremental para offline-first.

O objetivo deste repositório não é demonstrar a maior quantidade possível de bibliotecas. A proposta é construir uma base coerente, sustentável e próxima de um produto real: UI compartilhada com Compose Multiplatform, estado previsível, persistência local, networking compartilhado e sincronização evoluindo de forma pragmática.

## Status

Projeto em desenvolvimento ativo.

Atualmente o app possui:

- Kotlin Multiplatform com targets Android e iOS.
- UI compartilhada com Compose Multiplatform.
- Navegação compartilhada.
- DI com Koin.
- Networking compartilhado com Ktor Client.
- Persistência local com Room Multiplatform.
- Fluxo inicial de busca de veículo.
- Cadastro local de veículo.
- Cadastro local de manutenção.
- Leitura local-first para veículos e manutenções.
- Primeira versão de sync best-effort para itens pendentes.
- Testes compartilhados para ViewModels.

Ainda não possui:

- Login/JWT completo.
- Integração com backend real.
- Upload de imagens.
- Outbox robusta com retry/backoff.
- Observabilidade mobile.
- Resolução de conflitos.

Esses pontos fazem parte do roadmap incremental.

## Domínio

O app representa um fluxo simples e realista para histórico de manutenção de veículos:

1. O usuário pesquisa uma placa.
2. O app consulta primeiro o banco local.
3. A rede tenta sincronizar os dados mais recentes.
4. Se o veículo existir, o app exibe dados do veículo e histórico de manutenções.
5. Se o veículo não existir, o usuário pode cadastrar um novo veículo.
6. O usuário pode registrar manutenções localmente.
7. Operações criadas offline ficam pendentes para sincronização futura.

O backend esperado é uma arquitetura Java Spring Boot baseada em microsserviços, APIs REST, JWT, PostgreSQL, Kafka, Docker e OpenAPI.

## Stack

- Kotlin Multiplatform
- Compose Multiplatform
- Android
- iOS
- Ktor Client
- Kotlinx Serialization
- Coroutines
- Flow / StateFlow
- Koin
- Room Multiplatform
- Navigation Compose
- Gradle Kotlin DSL
- Kotlin Test

## Arquitetura

A arquitetura atual usa uma separação pragmática por feature, com camadas claras sem excesso de abstrações:

```text
composeApp/src/commonMain/kotlin/com/mmetzner/vehiclemaintenance
├── core
│   ├── database
│   ├── di
│   ├── navigation
│   ├── network
│   └── util
└── feature
    └── vehicle
        ├── data
        │   ├── local
        │   │   ├── dao
        │   │   └── entity
        │   ├── mapper
        │   └── remote
        │       └── dto
        ├── domain
        │   ├── model
        │   └── repository
        └── presentation
            ├── addmaintenance
            ├── addvehicle
            └── search
```

### Camadas

`presentation`

Contém telas Compose, ViewModels e UI state. A UI observa `StateFlow` e envia eventos explícitos para os ViewModels.

`domain`

Contém modelos e contratos usados pela feature. A camada permanece leve porque o projeto ainda não precisa de uma árvore grande de use cases.

`data`

Coordena persistência local, chamadas remotas e mapeamentos. O repository é o ponto principal de orquestração offline-first.

`core`

Agrupa infraestrutura compartilhada: banco, DI, navegação, networking e utilitários multiplatform.

## Estratégia Offline-First

A estratégia offline-first está sendo construída de forma incremental.

Decisão atual:

- O banco local é a fonte primária de leitura.
- A busca observa dados locais via `Flow`.
- A rede sincroniza e atualiza o banco local.
- Cadastros são persistidos localmente com `SyncStatus.PENDING`.
- O app tenta sincronizar pendências em background usando uma abordagem best-effort.

Essa abordagem já permite exercitar o fluxo local-first sem introduzir cedo demais uma engine de sincronização complexa.

Próxima evolução planejada:

- Criar uma outbox dedicada para operações pendentes.
- Registrar tipo de operação, payload, tentativa, erro e timestamps.
- Implementar retry com backoff.
- Separar sync manual de sync automático.
- Adicionar controle de conectividade.
- Adicionar fila específica para upload de imagens.
- Representar estados `PENDING`, `SYNCING`, `SYNCED` e `FAILED`.

O projeto evita, neste momento, soluções mais pesadas como event sourcing, CRDTs ou uma engine genérica de conflitos. Para este domínio, consistência eventual simples e idempotência no backend são escolhas mais realistas.

## Networking

O networking é compartilhado em `commonMain` com Ktor Client.

A configuração de base URL usa `expect/actual`:

- Android Emulator: `http://10.0.2.2:8080`
- iOS Simulator: `http://localhost:8080`

As chamadas da feature de veículos passam por um `VehicleRemoteDataSource`, mantendo URLs e detalhes HTTP fora do repository.

Estrutura atual:

```text
core/network
├── ApiConfig.kt
└── createHttpClient.kt

feature/vehicle/data/remote
├── VehicleRemoteDataSource.kt
└── dto
    ├── VehicleResponse.kt
    └── MaintenanceResponse.kt
```

## Persistência Local

O projeto usa Room Multiplatform com SQLite bundled driver.

Entidades atuais:

- `VehicleEntity`
- `MaintenanceEntity`
- `MaintenancePhotoEntity`

Relacionamentos:

- Um veículo possui várias manutenções.
- Uma manutenção possui várias fotos.

As entidades já carregam `syncStatus`, preparando o caminho para sincronização offline-first mais robusta.

## Decisões Técnicas

### Room Multiplatform em vez de SQLDelight

Room Multiplatform reduz atrito para quem vem de Android e mantém uma experiência familiar com DAO, entidades e relações.

Trade-off:

- Bom para produtividade e leitura por recrutadores Android.
- Menos explícito que SQLDelight em controle de SQL e migrações manuais.

### Compose Multiplatform compartilhado

A UI está em `commonMain`, permitindo reaproveitar tela e estado entre Android e iOS.

Trade-off:

- Excelente para demonstrar KMP real.
- Exige cuidado com UX nativa e limitações específicas de cada plataforma.

### Repository como boundary principal

O projeto não cria interfaces para cada data source neste momento. A interface principal é o repository da feature.

Trade-off:

- Menos boilerplate.
- Arquitetura mais simples.
- Testes mais profundos de sync podem justificar novas abstrações depois.

### Use cases sob demanda

Use cases serão adicionados apenas quando houver regra de negócio suficiente para justificar uma camada dedicada.

Neste estágio, criar um use case para cada chamada seria mais aparência de arquitetura do que necessidade real.

## Como Rodar

Pré-requisitos:

- Android Studio recente com suporte a Kotlin Multiplatform.
- JDK compatível com o Android Gradle Plugin.
- Xcode para build iOS.
- Backend local rodando na porta `8080`, quando a integração real estiver disponível.

Build Android:

```powershell
.\gradlew.bat :composeApp:assembleDebug
```

Compilar Kotlin Android e rodar testes compartilhados:

```powershell
.\gradlew.bat :composeApp:compileDebugKotlinAndroid :composeApp:allTests
```

Rodar iOS:

Abra o projeto `iosApp` no Xcode e execute no simulator.

## Testes

O projeto possui testes em `commonTest`, cobrindo comportamento de ViewModels e fluxo local-first inicial.

Exemplos:

- Quando há cache local, a busca mostra sucesso mesmo com falha de rede.
- Quando não há cache e a rede falha, a busca mostra erro.
- Cadastro de veículo válido salva no repository e emite evento de navegação.
- Entradas inválidas não disparam salvamento.

## Roadmap

### Fase 1: Fundação Mobile

- Estrutura KMP Android/iOS.
- Compose Multiplatform.
- Koin.
- Room Multiplatform.
- Ktor Client.
- Navegação compartilhada.
- Fluxo inicial de veículo.

Status: em andamento.

### Fase 2: Integração Real com Backend

- Configuração por ambiente.
- Contratos alinhados com OpenAPI.
- Tratamento centralizado de erros HTTP.
- Login.
- Armazenamento seguro de token.
- Interceptor JWT no Ktor.

### Fase 3: Offline-First Profissional

- Outbox dedicada.
- Retry com backoff.
- Sync manual.
- Sync automático.
- Controle de conectividade.
- Estados de sincronização na UI.

### Fase 4: Upload de Imagens

- Seleção de múltiplas fotos.
- Persistência local de arquivos pendentes.
- Upload posterior.
- Retry independente por foto.
- Associação foto/manutenção após sync.

### Fase 5: Qualidade de Produção

- Logs estruturados.
- Métricas de sync.
- Testes de repository.
- Testes de mappers.
- Testes de data sources com mock engine.
- Documentação de arquitetura.
- CI no GitHub Actions.

## O Que Este Projeto Demonstra

- Evolução incremental de um app KMP.
- Separação clara entre UI, domínio e dados.
- Uso pragmático de arquitetura mobile moderna.
- Pensamento offline-first desde o início.
- Integração preparada para backend Java Spring Boot.
- Consciência de trade-offs entre portfólio e produção.
- Cuidado com multiplatform real, incluindo diferenças Android/iOS.

## Limitações Atuais

- A sincronização ainda não possui retry persistente.
- Falhas de sync ainda não são expostas de forma rica para a UI.
- O armazenamento de token ainda não foi implementado.
- A camada de network ainda não possui refresh token.
- Upload de imagens ainda está apenas modelado no banco.
- O backend real ainda não está conectado.

Essas limitações são intencionais para manter o projeto incremental e revisável.

## Convenção de Commits

Sugestões de commits semânticos usados durante a evolução:

```text
feat: add vehicle search flow
feat: add local vehicle persistence
feat: add maintenance registration
refactor: extract vehicle remote data source and api config
fix: make shared vehicle flow compile across kmp targets
test: cover vehicle search offline behavior
docs: document kmp architecture and roadmap
```

## Licença

Este projeto ainda não possui licença definida.

Antes de publicar como open source, adicionar uma licença explícita como MIT ou Apache 2.0.
