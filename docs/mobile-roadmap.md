# Mobile Roadmap — Kotlin Multiplatform

O **Vehicle Maintenance History Mobile** é o aplicativo companheiro desenvolvido em **Kotlin Multiplatform (KMP)** e **Compose Multiplatform** para Android e iOS.

Este documento detalha o **Roadmap Mobile**, alinhado estritamente com as **14 estórias verticais do produto**.

---

## Arquitetura Mobile

### Camadas e Tecnologias
- **UI & Apresentação**: Compose Multiplatform (Material 3), Navigation Compose, StateFlow e ViewModels (`androidx.lifecycle`).
- **Injeção de Dependências**: Koin (`koin-core`, `koin-compose`, `koin-viewmodel`).
- **Persistência Local (Local-First)**: Room Multiplatform com driver SQLite embutido.
- **Sincronização Offline**: Outbox Pattern (`OutboxOperationEntity`, `SyncStatus.PENDING`).
- **Rede & Autenticação**: Cliente HTTP Ktor (OkHttp no Android, Darwin no iOS, Mock Engine em testes), abstração de repositório de JWT tokens.
- **Tarefas em Segundo Plano**: Android WorkManager / iOS Background Tasks framework.

```mermaid
graph TD
    subgraph Client App - Android & iOS
        UI[Compose Multiplatform UI]
        VM[ViewModels / StateFlow]
        Repo[Repository Layer]
        Room[(Room Database)]
        Outbox[Outbox Queue]
        Ktor[Ktor HTTP Client]
    end

    UI --> VM
    VM --> Repo
    Repo --> Room
    Repo --> Outbox
    Outbox --> Ktor
    Ktor -->|REST API / JWT| Backend[Spring Boot Monolith]
```

---

## Roadmap de Estórias Mobile

| # | Estória | Status | Escopo Específico Mobile |
|---|---|:---: |---|
| **01** | [STORY-001 — Separar claramente CI de AWS/CD](STORY-001) | `[TODO]` | Garantir que o pipeline `Mobile CI` no GitHub Actions execute builds Android (`assembleDebug`) e suíte de testes multiplatform (`allTests`) de forma totalmente isolada. |
| **02** | [STORY-002 — Vehicle Domain v2](STORY-002) | `[TODO]` | Atualizar a entidade `VehicleEntity` no Room, modelos de domínio, validações (placa, VIN), e telas Compose (formulário de veículo, detalhes e cartões). |
| **03** | [STORY-003 — Maintenance Domain v2](STORY-003) | `[TODO]` | Evoluir `MaintenanceEntity`, adicionar seletores de categoria, detalhar custos (peças vs mão de obra), oficina, mecânico, e garantia na UI Compose. |
| **04** | [STORY-004 — Attachments, Photos and Documents](STORY-004) | `[TODO]` | Integração com Câmera e Galeria nativas em Android/iOS, cache local de imagens, entidade de anexos no Room, e upload multipart via Outbox/Ktor. |
| **05** | [STORY-005 — Backend Sync API](STORY-005) | `[TODO]` | Preparar DTOs de sincronização, suporte a timestamps (`clientUpdatedAt`), UUIDs estáveis e idempotência no cliente Ktor. |
| **06** | [STORY-006 — KMP Offline Sync](STORY-006) | `[TODO]` | Motor de sincronização robusto: `OutboxSyncScheduler`, retries com backoff exponencial, monitor de conectividade, status de sync na UI, WorkManager (Android) e BGTaskScheduler (iOS). |
| **07** | [STORY-007 — Dashboard](STORY-007) | `[TODO]` | Tela inicial do app redesenhada com métricas de veículo, atalhos rápidos de cadastro, alertas de manutenção e estatísticas agregadas. |
| **08** | [STORY-008 — Vehicle Expenses](STORY-008) | `[TODO]` | Suporte local e remoto a despesas gerais (combustível, impostos, seguro, pedágio), Room `ExpenseEntity`, formulário de despesas e gráficos de custo/km. |
| **09** | [STORY-009 — Preventive Maintenance Rules](STORY-009) | `[TODO]` | Leitura e exibição de regras preventivas por veículo na UI (indicadores de saúde: verde, amarelo, vermelho). |
| **10** | [STORY-010 — Notifications and Reminders](STORY-010) | `[TODO]` | Agendamento e exibição de Notificações Locais Push no Android (NotificationManager) e iOS (UNUserNotificationCenter) para lembretes de manutenção. |
| **11** | [STORY-011 — Vehicle Sharing and Permissions](STORY-011) | `[TODO]` | Suporte a veículos compartilhados na UI, insígnias de permissão (`OWNER`, `EDITOR`, `VIEWER`), ocultação de ações de edição para leitos. |
| **12** | [STORY-012 — Vehicle History Report](STORY-012) | `[TODO]` | Botão para solicitar, baixar e visualizar o relatório PDF do veículo diretamente no aplicativo mobile. |
| **13** | [STORY-013 — Vehicle External Data](STORY-013) | `[TODO]` | Preenchimento automático do formulário de veículo via consulta por placa/VIN com fallback manual se desconectado. |
| **14** | [STORY-014 — Production Infrastructure](STORY-014) | `[TODO]` | Ajustes de configuração do cliente Ktor para apontar para o domínio de produção com HTTPS e gerenciamento seguro de tokens (Keychain iOS / EncryptedSharedPreferences Android). |

---

## Guia de Execução

As estórias do aplicativo mobile devem ser executadas em sincronia com o repositório principal de backend. Siga o fluxo de execução descrito em [`docs/stories/README.md`](../../vehicle-maintenance-history/docs/stories/README.md).
