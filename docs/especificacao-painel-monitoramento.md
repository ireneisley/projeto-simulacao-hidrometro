# Especificação de Requisitos - Painel de Monitoramento de Hidrômetros

**Sistema:** Painel de Monitoramento de Hidrômetros para Concessionária de Água (CAGEPA)  
**Versão:** 3.0 - Versão Acadêmica Simplificada  
**Data:** 18 de Novembro de 2025  
**Equipe:** Projeto Padrões de Projeto - IFPB  
**Disciplina:** Padrões de Projeto - 6º Período

---

## 1. INTRODUÇÃO

### 1.1 Propósito
Este documento especifica os requisitos funcionais e não-funcionais para o **Painel de Monitoramento de Hidrômetros**, um sistema destinado à gestão e monitoramento de consumo de água através da análise de imagens geradas por Sistemas de Hidrômetro de Água (SHA).

### 1.2 Escopo
O Painel de Monitoramento será utilizado pela CAGEPA (Companhia de Água e Esgotos da Paraíba) para:
- **Cadastro de Clientes:** Administradores cadastram clientes (donos de residências/comércios)
- **Vinculação de SHA:** Administradores vinculam hidrômetros (SHA) aos clientes cadastrados
- **Monitoramento de Consumo:** Administradores visualizam consumo de todos os clientes via análise de imagens dos SHA
- **Alertas Automáticos:** Sistema gera alertas de consumo excessivo para notificação dos clientes
- **Histórico e Relatórios:** Manter histórico de consumo e gerar relatórios administrativos

**IMPORTANTE:** Apenas **Administradores** têm acesso ao painel. **Clientes não fazem login** - são apenas cadastros gerenciados pelos administradores.

### 1.3 Restrições Arquiteturais

**RESTRIÇÃO 1:** O Painel **NÃO PODE** ter acesso direto às funcionalidades internas dos SHA (Sistemas de Hidrômetro de Água).

**RESTRIÇÃO 2:** A leitura do consumo dos SHA pelo Painel **DEVE** acontecer exclusivamente pela análise das imagens geradas pelos SHA.

### 1.4 Contexto do Sistema

```
┌─────────────────────────────────────────────────────────────────┐
│                    CONCESSIONÁRIA DE ÁGUA (CAGEPA)              │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐  │
│  │        PAINEL DE MONITORAMENTO (Sistema Proposto)       │  │
│  │                                                          │  │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │  │
│  │  │   GUI Web    │  │   CLI Admin  │  │   API REST   │  │  │
│  │  └──────────────┘  └──────────────┘  └──────────────┘  │  │
│  │           │               │                  │          │  │
│  │           └───────────────┴──────────────────┘          │  │
│  │                           │                             │  │
│  │              ┌────────────▼─────────────┐               │  │
│  │              │  FACHADA DO PAINEL       │               │  │
│  │              │  (PainelMonitoramento    │               │  │
│  │              │   Facade)                │               │  │
│  │              └────────────┬─────────────┘               │  │
│  │                           │                             │  │
│  │         ┌─────────────────┼─────────────────┐           │  │
│  │         │                 │                 │           │  │
│  │    ┌────▼────┐      ┌────▼────┐      ┌────▼────┐       │  │
│  │    │Gestão   │      │Monitor. │      │Sistema  │       │  │
│  │    │Usuários │      │Consumo  │      │Alertas  │       │  │
│  │    └─────────┘      └─────────┘      └─────────┘       │  │
│  │         │                 │                 │           │  │
│  │    ┌────▼──────────────┬──▼──────────┬──────▼────┐     │  │
│  │    │  Persistência    │   Logs      │Notificação│     │  │
│  │    │  (BD/Arquivos)   │  (Auditoria)│(Email/SMS)│     │  │
│  │    └──────────────────┴─────────────┴───────────┘     │  │
│  └──────────────────────────────────────────────────────┘  │
│                                  ▲                          │
│                                  │ Leitura de Imagens       │
│                                  │ (PNG/JPEG)               │
└──────────────────────────────────┼──────────────────────────┘
                                   │
        ┌──────────────────────────┴───────────────────────┐
        │         SISTEMAS SHA (Existentes)                │
        │  ┌─────────┐  ┌─────────┐  ┌─────────┐          │
        │  │ SHA-001 │  │ SHA-002 │  │ SHA-nnn │          │
        │  │ (Casa 1)│  │ (Casa 2)│  │ (Casa n)│          │
        │  └────┬────┘  └────┬────┘  └────┬────┘          │
        │       │            │            │                │
        │   [img.png]    [img.png]    [img.png]           │
        │       └────────────┴────────────┘                │
        │          Diretório de Imagens                    │
        └──────────────────────────────────────────────────┘
```

---

## 2. REQUISITOS FUNCIONAIS

### 2.1 Gestão de Clientes (CRUD)

#### RF-001: Cadastrar Cliente
**Descrição:** Administrador cadastra novos clientes (proprietários de residências/comércios com hidrômetros).

**Dados do Cliente:**
- CPF/CNPJ (único, obrigatório)
- Nome completo
- Email (para receber notificações)
- Telefone (para receber alertas via SMS)
- Endereço completo
- Tipo de cliente (Residencial, Comercial, Industrial)
- Status (Ativo, Inativo, Suspenso)

**Regras:**
- CPF/CNPJ deve ser único no sistema
- Email deve ser validado
- **Cliente NÃO possui senha** - não faz login no sistema
- Apenas Admin pode criar, editar ou excluir clientes

#### RF-002: Vincular Hidrômetro (SHA) ao Cliente
**Descrição:** Administrador vincula um ou mais hidrômetros (SHA) a um cliente cadastrado. Um cliente pode ter múltiplos hidrômetros (ex: casa, apartamento, comércio).

**Dados do Hidrômetro:**
- ID do SHA (identificador único, ex: SHA-001)
- Cliente proprietário (CPF/CNPJ)
- Endereço de instalação
- Data de instalação
- Status (Ativo, Inativo, Manutenção)
- Limite de consumo mensal (m³)
- Caminho/diretório das imagens geradas pelo SHA

**Regras:**
- Um hidrômetro só pode estar vinculado a um cliente por vez
- Admin deve validar se o SHA existe e está gerando imagens
- Sistema deve notificar o cliente (email/SMS) quando SHA for vinculado

#### RF-003: Consultar Cliente
**Descrição:** Administrador consulta informações de um cliente por CPF, nome ou ID do hidrômetro.

**Resultado:**
- Dados cadastrais do cliente
- Lista de hidrômetros (SHA) vinculados
- Consumo atual do mês (de todos os SHA do cliente)
- Histórico de consumo
- Alertas gerados

#### RF-004: Atualizar Cliente
**Descrição:** Administrador altera dados cadastrais do cliente (exceto CPF/CNPJ).

**Dados Editáveis:**
- Nome, email, telefone, endereço
- Tipo de cliente, status
- Limites de consumo

#### RF-005: Desativar Cliente
**Descrição:** Administrador desativa logicamente um cliente (não deleta dados históricos para auditoria).

**Regras:**
- Cliente desativado não recebe mais alertas
- Hidrômetros vinculados são também desativados
- Histórico de consumo permanece acessível

---

### 2.2 Monitoramento de Consumo

#### RF-006: Processar Imagem do Hidrômetro
**Descrição:** O sistema deve processar periodicamente as imagens geradas pelos SHA para extrair dados de consumo.

**Processamento:**
1. Ler imagem do diretório configurado
2. Aplicar OCR (Optical Character Recognition) nos valores do display
3. Extrair: Vazão (L/min), Volume Total (L), Pressão (bar), Timestamp
4. Validar leitura com histórico (detectar anomalias)
5. Persistir dados no banco

**Periodicidade:** Configurável (padrão: a cada 5 minutos)

#### RF-007: Monitorar Consumo de um SHA
**Descrição:** Visualizar consumo em tempo real de um hidrômetro específico.

**Informações Exibidas:**
- Vazão atual (L/min)
- Volume acumulado no dia/mês (m³)
- Pressão atual (bar)
- Última leitura (data/hora)
- Gráfico de consumo das últimas 24h
- Status do hidrômetro (Normal, Sem fluxo, Vazamento detectado)

#### RF-008: Monitorar Consumo de um Usuário
**Descrição:** Consolidar consumo de todos os hidrômetros de um usuário.

**Informações:**
- Consumo total do mês (m³)
- Consumo por hidrômetro
- Projeção de consumo mensal
- Comparativo com mês anterior
- Média de consumo diário

#### RF-009: Monitorar Consumo por Intervalo de Tempo
**Descrição:** Gerar relatórios de consumo por período (dia, semana, mês, ano).

**Filtros:**
- Período (data início/fim)
- Usuário específico ou todos
- Hidrômetro específico
- Tipo de cliente (Residencial, Comercial, Industrial)

**Formatos de Exportação:** PDF, CSV, Excel

---

### 2.3 Sistema de Alertas

#### RF-010: Configurar Limite de Consumo
**Descrição:** Administrador configura limites de consumo para cada cliente/SHA.

**Tipos de Limite:**
- Limite mensal (m³)
- Limite de vazão (L/min - detectar vazamento)

**Regras:**
- Limites podem ser configurados por cliente (todos os SHA) ou por SHA específico
- Sistema utiliza esses limites para gerar alertas automáticos

#### RF-011: Gerar Alerta de Consumo Excessivo
**Descrição:** Sistema deve gerar alertas quando usuário ultrapassar limites.

**Tipos de Alerta:**
- **Alerta Preventivo:** 80% do limite atingido
- **Alerta Crítico:** 100% do limite atingido
- **Alerta de Vazamento:** Vazão constante por mais de 6h seguidas

**Destinatários:**
- Usuário (email, SMS)
- Concessionária (dashboard, email)
- Sistemas externos (API webhook)

#### RF-012: Notificar Cliente sobre Alerta
**Descrição:** Sistema envia notificação automática para o cliente quando alerta é gerado.

**Canais de Notificação:**
- **Email:** Sempre enviado (canal principal)
- **SMS:** Opcional, apenas para alertas críticos (requer integração com provedor SMS)

**Conteúdo da Notificação:**
- Tipo de alerta (Preventivo, Crítico, Vazamento)
- Consumo atual vs. limite configurado
- Recomendações básicas (ex: "Verifique se há vazamentos")
- Contato da concessionária para dúvidas

#### RF-013: Dashboard de Alertas para Admin
**Descrição:** Administrador visualiza todos os alertas gerados no sistema.

**Funcionalidades:**
- Lista de alertas ativos/históricos
- Filtros: por tipo (Preventivo/Crítico/Vazamento), por cliente, por data
- Indicadores resumidos: Total de alertas no dia, alertas críticos pendentes
- Marcação de alerta como "resolvido" ou "em andamento"

---

### 2.4 Relatórios e Auditoria

#### RF-014: Gerar Relatório de Consumo
**Descrição:** Administrador gera relatórios consolidados de consumo.

**Tipos de Relatório:**
- **Consumo por Cliente:** Consumo mensal de um cliente específico (todos os SHA)
- **Consumo por Período:** Consumo de todos os clientes em um período específico
- **Ranking de Consumidores:** Top 10 maiores consumidores do mês

**Formato de Exportação:** PDF, CSV

#### RF-015: Registrar Auditoria (Log)
**Descrição:** Sistema registra operações relevantes para rastreamento.

**Eventos Auditados:**
- Login/Logout de administradores
- Cadastro/Alteração/Exclusão de clientes
- Vinculação/Desvinculação de SHA
- Processamento de imagens (sucesso/falha)
- Geração de alertas
- Tentativas de acesso negado

**Dados do Log:**
- Timestamp
- Usuário/Admin
- Operação realizada
- Resultado (Sucesso/Falha)

#### RF-016: Consultar Logs
**Descrição:** Admin consulta logs com filtros simples (data, usuário, tipo de operação).

---

### 2.5 Autenticação e Segurança

#### RF-017: Autenticar Administrador
**Descrição:** Apenas administradores fazem login no sistema usando JWT (JSON Web Token).

**Fluxo de Autenticação:**
1. Admin entra com email e senha
2. Sistema valida credenciais no banco de dados
3. Se válido, gera token JWT com validade de 8 horas
4. Token é enviado ao cliente (navegador) e armazenado (LocalStorage ou Cookie)
5. Todas as requisições subsequentes incluem o token no header `Authorization: Bearer <token>`
6. Sistema valida token em cada requisição

**Perfil de Usuário:**
- **Administrador:** Único tipo de usuário com login. Acesso total ao sistema.

**Segurança:**
- Senha criptografada com bcrypt no banco de dados
- Token JWT assinado com chave secreta (configurável)
- Logout: token é removido do cliente (blacklist de tokens é opcional/avançado)

#### RF-018: Recuperar Senha (Admin)
**Descrição:** Administrador pode recuperar senha via email (funcionalidade simples de recuperação).

**Fluxo:**
1. Admin clica em "Esqueci minha senha"
2. Sistema envia email com link de redefinição (token temporário com validade de 1h)
3. Admin acessa link e define nova senha

---

## 3. REQUISITOS NÃO-FUNCIONAIS

### RNF-001: Desempenho
- Processar imagem de hidrômetro em até 5 segundos (OCR simples)
- Suportar pelo menos 100 hidrômetros (escala reduzida para projeto acadêmico)
- Tempo de resposta da interface < 3 segundos

### RNF-002: Disponibilidade
- Sistema deve funcionar durante horário comercial (demonstração acadêmica)
- Backup manual ou automático simples (opcional)

### RNF-003: Segurança
- Autenticação via JWT para administradores
- Senhas criptografadas com bcrypt
- Proteção básica contra SQL Injection (uso de PreparedStatement/JPA)
- Token JWT com expiração de 8 horas

### RNF-004: Usabilidade
- Interface web simples e funcional (desktop)
- Interface em português
- Design básico com Bootstrap ou similar

### RNF-005: Manutenibilidade
- Código documentado com comentários explicando padrões de projeto aplicados
- Testes unitários básicos para demonstrar qualidade (cobertura > 30%)
- Estrutura de código organizada por subsistemas

### RNF-006: Portabilidade
- Executar em Windows ou Linux
- Banco de dados: PostgreSQL (recomendado) ou H2 (para desenvolvimento)

---

## 4. ARQUITETURA DO SISTEMA

### 4.1 Visão Geral dos Subsistemas

O Painel de Monitoramento é composto por **5 subsistemas principais** orquestrados pela **Fachada**:

```
┌─────────────────────────────────────────────────────────────┐
│                    CAMADA DE APRESENTAÇÃO                   │
│  ┌──────────────┐  ┌──────────────┐                         │
│  │   GUI Web    │  │   API REST   │                         │
│  │              │  │              │                         │
│  └──────┬───────┘  └───────┬──────┘                         │
└─────────┼──────────────────┼────────────────────────────────┘
          │                  │
┌─────────▼──────────────────▼─────────────────────────────────┐
│               CAMADA DE FACHADA (Facade Pattern)             │
│                                                              │
│         ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓             │
│         ┃  PainelMonitoramentoFacade(Singleton)┃             │
│         ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛             │
│                          │                                   │
└──────────────────────────┼───────────────────────────────────┘
                           │
┌──────────────────────────▼────────────────────────────────────┐
│                 CAMADA DE SERVIÇOS                            │
│                                                               │
│  ┌─────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │1. Gestão    │  │2. Monitor.   │  │3. Alertas    │          │
│  │  Clientes   │  │   Consumo    │  │ Notificações │          │
│  └──────┬──────┘  └──────┬───────┘  └───────┬──────┘          │
│         │                │                  │                 │
│  ┌──────▼──────┐  ┌──────▼────────┐                           │
│  │4. Processa- │  │5. Autenticação│                           │
│  │mento Imagens│  │   JWT (Admin) │                           │
│  └──────┬──────┘  └───────┬───────┘                           │
└─────────┼─────────────────┼───────────────────────────────────┘
          │                 │                  │
┌─────────▼─────────────────▼──────────────────▼────────────────┐
│              CAMADA DE INFRAESTRUTURA                         │
│                                                               │
│  ┌─────────────┐  ┌──────────────┐  ┌─────────────┐           │
│  │Persistência │  │  Logs        │  │Notificações │           │
│  │             │  │  + Auditoria │  │(Email/SMS)  │           │
│  └─────────────┘  └──────────────┘  └─────────────┘           │
└───────────────────────────────────────────────────────────────┘
          │                 │                  │
          ▼                 ▼                  ▼
    ┌──────────┐      ┌──────────┐      ┌─────────────┐
    │PostgreSQL│      │Arquivos  │      │Resend/Twilio│
    │  Banco   │      │   .log   │      │             │
    │  Dados   │      │          │      │             │
    └──────────┘      └──────────┘      └─────────────┘
```

### 4.2 Descrição dos Subsistemas

#### **Subsistema 1: Gestão de Clientes**
- **Responsabilidade:** CRUD completo de clientes e vinculação com hidrômetros (SHA)
- **Padrões de Projeto:** 
  - **Repository:** Abstração de acesso a dados
  - **DTO (Data Transfer Object):** Transferência de dados entre camadas
  - **Factory:** Criação de objetos Cliente
- **Persistência:** PostgreSQL via JPA/Hibernate
- **Classes Principais:** `ClienteService`, `ClienteRepository`, `ClienteDTO`, `ClienteFactory`

#### **Subsistema 2: Monitoramento de Consumo**
- **Responsabilidade:** Consultar, agregar e exibir dados de consumo dos hidrômetros
- **Padrões de Projeto:**
  - **Strategy:** Diferentes estratégias de análise de consumo (diário, mensal, por cliente)
  - **Observer:** Notificação quando novos dados de consumo são processados
- **Persistência:** PostgreSQL para histórico de leituras
- **Classes Principais:** `ConsumoService`, `ConsumoRepository`, `AnalisadorConsumoStrategy`, `ConsumoObserver`

#### **Subsistema 3: Sistema de Alertas e Notificações**
- **Responsabilidade:** Detectar consumo excessivo e enviar notificações aos clientes
- **Padrões de Projeto:**
  - **Chain of Responsibility:** Pipeline de regras de validação de alertas
  - **Observer:** Notificação automática quando limite é ultrapassado
  - **Template Method:** Estrutura padrão para envio de notificações
- **Canais:** Email (obrigatório), SMS (opcional)
- **Classes Principais:** `AlertaService`, `RegrasAlertaChain`, `NotificadorEmail`, `NotificadorSMS`

#### **Subsistema 4: Processamento de Imagens (OCR)**
- **Responsabilidade:** Ler imagens dos SHA, aplicar OCR e extrair dados de consumo
- **Padrões de Projeto:**
  - **Template Method:** Pipeline fixo de processamento (ler → pré-processar → OCR → validar → persistir)
  - **Adapter:** Adaptação para diferentes bibliotecas OCR (Tesseract)
- **Biblioteca:** Tesseract OCR
- **Agendamento:** Spring `@Scheduled` para processamento periódico (ex: a cada 5 minutos)
- **Classes Principais:** `ProcessadorImagemService`, `OCRAdapter`, `TesseractOCRImpl`

#### **Subsistema 5: Autenticação JWT (Admin)**
- **Responsabilidade:** Login de administradores e controle de acesso
- **Padrões de Projeto:**
  - **Singleton:** Gerenciador de tokens JWT (um único ponto de controle)
  - **Proxy:** Interceptação de requisições para validação de token
- **Tecnologia:** Spring Security + JWT (JSON Web Token)
- **Fluxo:** Login → Gera token JWT → Token em todas as requisições → Valida token
- **Classes Principais:** `AuthService`, `JWTTokenManager (Singleton)`, `AuthenticationProxy`

---

## 5. ESPECIFICAÇÃO DA FACHADA

### 5.1 Classe: PainelMonitoramentoFacade

A Fachada é o **único ponto de entrada** para todas as funcionalidades do Painel. Implementa o padrão **Singleton** para garantir instância única e simplificar o acesso aos subsistemas.

#### **Responsabilidades da Fachada:**
- Orquestrar chamadas entre os 5 subsistemas
- Simplificar a interface para a camada de apresentação
- Centralizar tratamento de exceções
- Aplicar o padrão Singleton

#### **Métodos Principais:**

```java
public class PainelMonitoramentoFacade {
    
    // Singleton
    private static PainelMonitoramentoFacade instance;
    
    private PainelMonitoramentoFacade() { /* Construtor privado */ }
    
    public static synchronized PainelMonitoramentoFacade getInstance() {
        if (instance == null) {
            instance = new PainelMonitoramentoFacade();
        }
        return instance;
    }

    // ===== GESTÃO DE CLIENTES =====

    ClienteDTO cadastrarCliente(DadosCadastroCliente dados) throws ClienteException;

    ClienteDTO consultarCliente(String cpfCnpj) throws ClienteException;

    ClienteDTO atualizarCliente(String cpfCnpj, DadosAtualizacaoCliente dados) throws ClienteException;

    boolean desativarCliente(String cpfCnpj) throws ClienteException;

    List<ClienteDTO> listarClientes(FiltroBuscaCliente filtro);

    // ===== GESTÃO DE HIDRÔMETROS (SHA) =====

    HidrometroDTO vincularHidrometro(String cpfCnpjCliente, DadosHidrometro dados) throws HidrometroException;

    boolean desvincularHidrometro(String idHidrometro) throws HidrometroException;

    List<HidrometroDTO> listarHidrometrosCliente(String cpfCnpj);

    // ===== MONITORAMENTO DE CONSUMO =====

    ConsumoAtualDTO monitorarConsumoSHA(String idHidrometro) throws MonitoramentoException;

    ConsumoConsolidadoDTO monitorarConsumoCliente(String cpfCnpj) throws MonitoramentoException;

    List<DadosConsumoDTO> consultarHistoricoConsumo(String idHidrometro, LocalDate inicio, LocalDate fim);

    // ===== SISTEMA DE ALERTAS =====

    AlertaDTO configurarLimiteConsumo(String cpfCnpj, double limiteM3, double limiteVazaoLMin);

    List<AlertaDTO> listarAlertasAtivos();

    boolean marcarAlertaResolvido(String idAlerta);

    // ===== RELATÓRIOS =====

    RelatorioConsumoDTO gerarRelatorioCliente(String cpfCnpj, LocalDate inicio, LocalDate fim);

    RelatorioConsumoDTO gerarRelatorioGeral(LocalDate inicio, LocalDate fim);

    // ===== PROCESSAMENTO DE IMAGENS =====

    boolean processarImagemManual(String idHidrometro, String caminhoImagem) throws ProcessamentoException;

    StatusProcessamentoDTO consultarStatusProcessamento();

    // ===== AUTENTICAÇÃO (ADMIN) =====

    String autenticar(String email, String senha) throws AutenticacaoException; // Retorna token JWT

    boolean validarToken(String token);

    void logout(String token);

    // ===== LOGS E AUDITORIA =====

    List<LogDTO> consultarLogs(FiltroLog filtro);
}
```

---

## 6. STACK TECNOLÓGICO SIMPLIFICADO

### 6.1 Backend (Java)

**Framework Principal:**
- **Spring Boot 3.x** - Framework base para desenvolvimento rápido
  - Spring Web (REST APIs)
  - Spring Data JPA (Persistência)
  - Spring Security (Autenticação JWT)

**Banco de Dados:**
- **PostgreSQL 14+** (Produção) - Banco relacional robusto
- **H2 Database** (Desenvolvimento) - Banco em memória para testes

**Processamento de Imagens:**
- **Tesseract OCR 4.x** - Biblioteca open-source para OCR
- **Tess4J** - Wrapper Java para Tesseract

**Autenticação:**
- **JWT (JSON Web Token)** - Tokens stateless para autenticação
- **jjwt (Java JWT)** - Biblioteca para gerar/validar tokens

**Notificações:**
- **JavaMail API** - Envio de emails (obrigatório)
- **Twilio API** (opcional) - Envio de SMS

**Build e Testes:**
- **Maven** - Gerenciamento de dependências
- **JUnit 5** - Testes unitários
- **Mockito** - Mocks para testes

### 6.2 Frontend (Opcional - Simplificado)

**Opção 1: Templates Server-Side**
- **Thymeleaf** - Template engine integrado com Spring Boot
- **Bootstrap 5** - Framework CSS para interface responsiva

**Opção 2: SPA Simples**
- **HTML + JavaScript puro** - Sem frameworks complexos
- **Fetch API** - Chamadas REST
- **Bootstrap 5** - Estilização

### 6.3 Persistência

**Estratégia Única: Banco de Dados Relacional**
- **ORM:** Hibernate/JPA
- **Modelo:** Relacional com tabelas principais:
  - `clientes` - Dados dos clientes
  - `hidrometros` - SHA vinculados
  - `leituras` - Dados de consumo por timestamp
  - `alertas` - Alertas gerados
  - `admins` - Administradores do sistema
  - `logs` - Auditoria de operações
- **Cache:** Redis (dados em tempo real)

**Opção 3: Arquivos + Banco**
### 6.4 Modelo de Dados (Principais Entidades)

```
CLIENTE
├── cpf_cnpj (PK)
├── nome
├── email
├── telefone
├── endereco
├── tipo_cliente (RESIDENCIAL, COMERCIAL, INDUSTRIAL)
├── status (ATIVO, INATIVO, SUSPENSO)
└── data_cadastro

HIDROMETRO
├── id_hidrometro (PK)
├── cpf_cnpj_cliente (FK)
├── endereco_instalacao
├── data_instalacao
├── status (ATIVO, INATIVO, MANUTENCAO)
├── limite_consumo_mensal_m3
├── limite_vazao_lmin
└── caminho_imagens

LEITURA_CONSUMO
├── id_leitura (PK)
├── id_hidrometro (FK)
├── timestamp
├── vazao_lmin
├── volume_total_litros
├── pressao_bar
└── imagem_origem_path

ALERTA
├── id_alerta (PK)
├── cpf_cnpj_cliente (FK)
├── id_hidrometro (FK)
├── tipo_alerta (PREVENTIVO, CRITICO, VAZAMENTO)
├── timestamp_geracao
├── valor_limite
├── valor_atual
├── status (ATIVO, RESOLVIDO, EM_ANDAMENTO)
└── notificado (true/false)

ADMIN
├── id_admin (PK)
├── email (UNIQUE)
├── senha_hash (bcrypt)
├── nome
└── data_cadastro

LOG_AUDITORIA
├── id_log (PK)
├── timestamp
├── admin_email (FK)
├── operacao (LOGIN, CADASTRO, ALTERACAO, EXCLUSAO, etc.)
├── entidade (CLIENTE, HIDROMETRO, ALERTA)
├── id_entidade
└── resultado (SUCESSO, FALHA)
```

---

## 7. DIAGRAMAS DE CLASSES

### 7.1 Diagrama Geral - Arquitetura de Subsistemas

Ver arquivo: `diagrama-geral-subsistemas.puml` ou `diagrama-geral-subsistemas.mmd` (Mermaid)

**Observação:** O diagrama Mermaid pode ser visualizado diretamente no GitHub/GitLab ou usando a extensão Markdown Preview Mermaid Support no VS Code.

### 7.2 Subsistema 1: Gestão de Clientes

Ver arquivo: `subsistema-gestao-usuarios.puml`

**Padrões Aplicados:**
- **Repository Pattern:** `ClienteRepository` - Abstração de acesso a dados
- **DTO Pattern:** `ClienteDTO`, `DadosCadastroCliente` - Transferência entre camadas
- **Factory Pattern:** `ClienteFactory` - Criação de objetos Cliente
- **Service Layer:** `GestaoClientesService` - Lógica de negócio

**Diagrama Conceitual:**
```
GestaoClientesFacade
    ↓
GestaoClientesService
    ↓
ClienteRepository (interface)
    ↓
ClienteRepositoryImpl (JPA)
    ↓
PostgreSQL
```

### 7.3 Subsistema 2: Monitoramento de Consumo

Ver arquivo: `subsistema-monitoramento-consumo.puml`

**Padrões Aplicados:**
- **Strategy Pattern:** Diferentes estratégias de análise (diária, mensal, por cliente)
  - `AnalisadorConsumoDiario`
  - `AnalisadorConsumoMensal`
  - `AnalisadorConsumoCliente`
- **Observer Pattern:** Notificação quando novos dados são processados
- **Repository Pattern:** `ConsumoRepository`

**Fluxo:**
```
Admin solicita relatório → ConsumoService → Strategy selecionada → Repository → BD
```

### 7.4 Subsistema 3: Sistema de Alertas e Notificações

Ver arquivo: `subsistema-alertas.puml`

**Padrões Aplicados:**
- **Chain of Responsibility:** Pipeline de regras de validação
  - `RegraLimiteMensal` → `RegraVazamento` → `RegraCritico`
- **Observer Pattern:** Notificação automática quando alerta é gerado
  - `NotificadorEmail` (sempre)
  - `NotificadorSMS` (opcional, apenas críticos)
- **Template Method:** Estrutura padrão para envio de notificações

**Fluxo de Alerta:**
```
Leitura processada → Chain valida regras → Alerta gerado → Observers notificados → Email/SMS enviado
```

### 7.5 Subsistema 4: Processamento de Imagens (OCR)

Ver arquivo: `subsistema-processamento-imagens.puml`

**Padrões Aplicados:**
- **Template Method:** Pipeline fixo de processamento
  1. `lerImagem()`
  2. `preprocessarImagem()`
  3. `aplicarOCR()`
  4. `validarDados()`
  5. `persistirDados()`
- **Adapter Pattern:** Adaptação do Tesseract OCR
  - Interface: `OCRService`
  - Implementação: `TesseractOCRAdapter`

**Agendamento:**
```java
@Scheduled(fixedRate = 300000) // A cada 5 minutos
public void processarImagensPendentes() {
    // Processa imagens de todos os SHA
}
```

### 7.6 Subsistema 5: Autenticação JWT

Ver arquivo: `subsistema-autenticacao-seguranca.puml`

**Padrões Aplicados:**
- **Singleton Pattern:** `JWTTokenManager` - Gerenciador único de tokens
- **Proxy Pattern:** `AuthenticationProxy` - Intercepta requisições para validar token

**Fluxo de Autenticação:**
```
1. Admin → POST /api/auth/login {email, senha}
2. AuthService valida credenciais no BD
3. JWTTokenManager gera token (válido por 8h)
4. Token retornado ao cliente
5. Todas requisições incluem: Authorization: Bearer <token>
6. AuthenticationProxy valida token antes de permitir acesso
```
- **Scheduler Pattern:** Agendamento de processamento periódico

---

## 8. FLUXOS PRINCIPAIS DO SISTEMA

### 8.1 Fluxo: Cadastrar Cliente e Vincular Hidrômetro

```
1. Admin acessa interface web
2. Preenche formulário de cadastro do cliente
3. [GUI] → PainelMonitoramentoFacade.cadastrarCliente()
              ↓
          GestaoClientesService.cadastrar()
              ↓
          ClienteValidator.validar(CPF único, email válido)
              ↓
          ClienteRepository.salvar()
              ↓
          LogService.registrar("CLIENTE_CADASTRADO")
              ↓
4. [GUI] ← ClienteDTO (retorna dados do cliente criado)

5. Admin vincula SHA ao cliente
6. [GUI] → PainelMonitoramentoFacade.vincularHidrometro()
              ↓
          GestaoHidrometrosService.vincular()
              ↓
          HidrometroValidator.validarExistencia()
              ↓
          HidrometroRepository.salvar()
              ↓
          NotificacaoService.notificarCliente("SHA vinculado")
              ↓
          LogService.registrar("SHA_VINCULADO")
              ↓
7. [GUI] ← HidrometroDTO
```

### 8.2 Fluxo: Processamento Automático de Imagens (OCR)

```
1. [Scheduler Spring] → Executa a cada 5 minutos
               ↓
2. ProcessamentoImagensService.processarPendentes()
               ↓
3. FileSystemAdapter.listarImagensNovas(diretório configurado)
               ↓
4. PARA CADA IMAGEM:
               ↓
5. ProcessadorImagem.processar(imagem) [Template Method]
               ├─ lerImagem()
               ├─ preprocessarImagem() (ajuste contraste, binarização)
               ├─ aplicarOCR() → TesseractOCRAdapter.extrair()
               ├─ validarDados() (vazão, volume, pressão)
               └─ persistirDados()
               ↓
6. ConsumoRepository.salvarLeitura()
               ↓
7. AlertaService.verificarLimites() [Chain of Responsibility]
               ↓
8. SE limite ultrapassado:
   ├─ AlertaRepository.salvar()
   ├─ NotificadorEmail.enviar(cliente)
   └─ SE crítico: NotificadorSMS.enviar(cliente)
               ↓
9. LogService.registrar("IMAGEM_PROCESSADA", sucesso/falha)
```

### 8.3 Fluxo: Autenticação Admin (JWT)

```
1. Admin acessa página de login
2. [GUI] → POST /api/auth/login
           Body: { email: "admin@cagepa.pb.gov.br", senha: "***" }
               ↓
3. AuthService.autenticar(email, senha)
               ↓
4. AdminRepository.buscarPorEmail(email)
               ↓
5. BCrypt.checkPassword(senha, senhaHash)
               ↓
6. SE válido:
   ├─ JWTTokenManager.gerarToken(email, expiração: 8h)
   ├─ LogService.registrar("LOGIN_SUCESSO")
   └─ Retorna: { token: "eyJhbGc...", expiresIn: 28800 }
               ↓
7. [GUI] armazena token no LocalStorage
               ↓
8. Todas próximas requisições incluem:
   Header: Authorization: Bearer eyJhbGc...
               ↓
9. AuthenticationProxy intercepta e valida token
               ↓
10. SE token válido: permite acesso
    SE token inválido/expirado: retorna 401 Unauthorized
```

### 8.4 Fluxo: Geração de Alerta Automático

```
1. ConsumoRepository.salvarLeitura(leitura) [após OCR]
               ↓
2. ObserverManager.notificar("NOVA_LEITURA", leitura)
               ↓
3. AlertaService.onNovaLeitura(leitura) [Observer]
               ↓
4. Chain of Responsibility valida regras:
   ├─ RegraLimiteMensal.avaliar()
   │  └─ SE consumo >= 80% limite: gera ALERTA_PREVENTIVO
   │  └─ SE consumo >= 100% limite: gera ALERTA_CRITICO
   ├─ RegraVazamento.avaliar()
   │  └─ SE vazão constante > 6h: gera ALERTA_VAZAMENTO
   └─ RegraPadrao.avaliar()
               ↓
5. SE alerta gerado:
   ├─ AlertaRepository.salvar(alerta)
   ├─ ObserverManager.notificar("ALERTA_GERADO", alerta)
   │   ↓
   │   Observers notificados:
   │   ├─ NotificadorEmail.onAlertaGerado() → envia email
   │   ├─ NotificadorSMS.onAlertaGerado() → envia SMS (se crítico)
   │   └─ DashboardObserver.onAlertaGerado() → atualiza dashboard
   └─ LogService.registrar("ALERTA_GERADO")
```

---

## 9. PADRÕES DE PROJETO APLICADOS (RESUMO)

### Padrões Criacionais:
1. **Singleton** - `PainelMonitoramentoFacade`, `JWTTokenManager`
2. **Factory** - `ClienteFactory`, criação de objetos de domínio

### Padrões Estruturais:
3. **Facade** - `PainelMonitoramentoFacade` (ponto único de entrada)
4. **Adapter** - `TesseractOCRAdapter` (adapta biblioteca Tesseract)
5. **Proxy** - `AuthenticationProxy` (intercepta e valida tokens)
6. **Repository** - Abstração de acesso a dados

### Padrões Comportamentais:
7. **Strategy** - Diferentes estratégias de análise de consumo
8. **Observer** - Notificação automática de alertas
9. **Chain of Responsibility** - Pipeline de regras de validação de alertas
10. **Template Method** - Pipeline fixo de processamento OCR

### Padrões Arquiteturais:
11. **DTO (Data Transfer Object)** - Transferência entre camadas
12. **Service Layer** - Lógica de negócio centralizada

---

## 10. CHECKLIST DE IMPLEMENTAÇÃO

### Sprint 1: Infraestrutura Base
- [ ] Configurar projeto Spring Boot 3.x com Maven
- [ ] Configurar banco PostgreSQL ou H2
- [ ] Implementar entidades JPA (Cliente, Hidrometro, Leitura, Alerta, Admin)
- [ ] Criar repositories (ClienteRepository, HidrometroRepository, etc.)
- [ ] Implementar autenticação JWT básica

### Sprint 2: Gestão de Clientes
- [ ] Implementar CRUD de clientes
- [ ] Implementar vinculação/desvinculação de SHA
- [ ] Criar DTOs e validadores
- [ ] Implementar Factory de clientes
- [ ] Testes unitários básicos

### Sprint 3: Processamento de Imagens (OCR)
- [ ] Integrar Tesseract OCR via Tess4J
- [ ] Implementar Template Method para pipeline OCR
- [ ] Implementar Adapter para Tesseract
- [ ] Configurar agendamento Spring @Scheduled
- [ ] Testar extração de dados de imagens reais

### Sprint 4: Sistema de Alertas
- [ ] Implementar Chain of Responsibility para regras
- [ ] Implementar Observer para notificações
- [ ] Integrar JavaMail para envio de emails
- [ ] (Opcional) Integrar Twilio para SMS
- [ ] Criar dashboard de alertas

### Sprint 5: Monitoramento e Relatórios
- [ ] Implementar Strategy para análises de consumo
- [ ] Criar endpoints REST para consultas
- [ ] Implementar geração de relatórios (PDF/CSV)
- [ ] Criar gráficos de consumo

### Sprint 6: Interface e Finalização
- [ ] Desenvolver interface web (Thymeleaf + Bootstrap)
- [ ] Implementar telas de login, dashboard, CRUD
- [ ] Implementar auditoria e logs
- [ ] Documentação final (README, diagramas)
- [ ] Testes de integração

---

## 11. TECNOLOGIAS E BIBLIOTECAS

### Essenciais:
- **Java 17+** (LTS)
- **Spring Boot 3.2+** (Framework base)
- **PostgreSQL 14+** ou **H2** (Banco de dados)
- **Tesseract OCR 4.x** (via Tess4J)
- **JWT (jjwt)** (Autenticação)
- **JavaMail API** (Notificações email)

### Opcionais:
- **Twilio API** (SMS)
- **JasperReports** (Relatórios PDF)
- **Chart.js** (Gráficos frontend)
- **Bootstrap 5** (Interface)

### Build e Testes:
- **Maven** (Gerenciamento de dependências)
- **JUnit 5** (Testes unitários)
- **Mockito** (Mocks)

---

## 12. CONSIDERAÇÕES FINAIS

Este projeto foi desenvolvido com foco **acadêmico** para a disciplina de **Padrões de Projeto**, demonstrando a aplicação prática de **11 padrões GoF** em um sistema real.

### Diferenciais Pedagógicos:
✅ Arquitetura simplificada (5 subsistemas) mas completa
✅ Código focado em demonstrar padrões de forma clara
✅ Complexidade adequada ao 6º período de Engenharia
✅ Escopo realista para implementação em um semestre
✅ Integração com tecnologia atual (OCR, JWT, Spring Boot)

### Possíveis Extensões Futuras:
- Interface mobile (React Native, Flutter)
- Análise preditiva de consumo (Machine Learning básico)
- Integração com sistema de faturamento
- Dashboard em tempo real com WebSockets
- Múltiplos níveis de administradores

---

**Fim da Especificação**
               ↓
          DadosConsumoValidator.validar(dados)
               ↓
          ConsumoRepository.salvar(dados)
               ↓
          AlertaService.verificarLimites(dados)  ← [PODE GERAR ALERTA]
               ↓
          LogService.registrar("IMAGEM_PROCESSADA")
```

### 8.3 Fluxo: Geração e Envio de Alerta

```
[AlertaService] → detectarViolacaoLimite(dadosConsumo)
                   ↓
              validarRegrasAlerta()  (Chain of Responsibility)
                   ↓
              AlertaRepository.salvar(alerta)
                   ↓
              NotificacaoService.notificar(alerta)
                   ├────→ EmailStrategy.enviar(usuario)
                   ├────→ SMSStrategy.enviar(usuario)
                   ├────→ PushStrategy.enviar(usuario)
                   └────→ WebhookStrategy.enviar(sistemasExternos)
                   ↓
              LogService.registrar("ALERTA_GERADO")
```

---

## 9. CONSIDERAÇÕES DE IMPLEMENTAÇÃO

### 9.1 Tecnologias Sugeridas

**Backend:**
- Java 17+ (LTS)
- Spring Boot 3.x (Framework principal)
- Spring Security (Autenticação)
- Spring Data JPA (Persistência)
- Spring Scheduler (Agendamento)

**Frontend:**
- React.js ou Vue.js (GUI Web)
- Bootstrap ou Material-UI (UI Components)
- Chart.js (Gráficos)

**Processamento de Imagens:**
- Tesseract OCR (Biblioteca de OCR open-source)
- OpenCV (Processamento de imagem)
- Apache Commons Imaging

**Banco de Dados:**
- PostgreSQL 14+ (Dados principais)
- Redis (Cache)
- InfluxDB ou TimescaleDB (Séries temporais - opcional)

**Notificações:**
- JavaMail (Email)
- Twilio API (SMS)
- Firebase Cloud Messaging (Push)

**Logs:**
- SLF4J + Logback
- ELK Stack (Elasticsearch, Logstash, Kibana) - para análise avançada

**Testes:**
- JUnit 5
- Mockito
- Testcontainers (testes de integração com BD)

### 9.2 Estrutura de Pacotes Sugerida

```
com.cagepa.painel
├── config/                    # Configurações Spring
├── domain/                    # Entidades de domínio
│   ├── model/
│   ├── repository/
│   └── service/
├── application/               # Casos de uso
│   ├── dto/
│   └── facade/
│       └── PainelMonitoramentoFacade.java
├── infrastructure/            # Infraestrutura
│   ├── persistence/
│   ├── ocr/
│   ├── notification/
│   └── logging/
├── presentation/              # Camada de apresentação
│   ├── rest/                  # Controllers REST
│   ├── cli/                   # Interface CLI
│   └── web/                   # Frontend (se monolito)
└── util/                      # Utilitários
```

---

## 10. CASOS DE USO DETALHADOS

### UC-001: Cadastrar Novo Usuário
**Ator:** Administrador/Operador  
**Pré-condições:** Usuário autenticado com permissão de cadastro  
**Fluxo Principal:**
1. Administrador acessa tela de cadastro
2. Informa dados do usuário (CPF, nome, email, etc.)
3. Sistema valida CPF (não duplicado)
4. Sistema valida email
5. Sistema gera senha temporária
6. Sistema persiste usuário
7. Sistema envia email de boas-vindas com senha
8. Sistema registra log de auditoria
9. Sistema exibe confirmação

**Fluxos Alternativos:**
- 3a. CPF já cadastrado → Exibir erro e sugerir consulta
- 4a. Email inválido → Solicitar correção
- 6a. Erro de banco de dados → Exibir erro e fazer rollback

### UC-002: Monitorar Consumo em Tempo Real
**Ator:** Operador/Cliente  
**Pré-condições:** 
- Usuário autenticado
- Hidrômetro associado ao usuário
- Imagens sendo geradas pelo SHA

**Fluxo Principal:**
1. Usuário acessa dashboard
2. Sistema busca última leitura processada
3. Sistema calcula consumo acumulado do dia/mês
4. Sistema gera gráfico de consumo das últimas 24h
5. Sistema exibe dados em tempo real
6. Sistema atualiza automaticamente a cada 1 minuto

**Fluxos Alternativos:**
- 2a. Nenhuma leitura recente (>10 min) → Exibir alerta de "Hidrômetro offline"
- 3a. Anomalia detectada (consumo inconsistente) → Destacar com ícone de aviso

### UC-003: Processar Imagem do Hidrômetro
**Ator:** Sistema (Scheduler)  
**Pré-condições:** Imagens sendo geradas pelos SHA  
**Fluxo Principal:**
1. Scheduler dispara a cada 5 minutos
2. Sistema lista arquivos novos no diretório de imagens
3. Para cada imagem:
   - 3.1 Identifica ID do hidrômetro pelo nome do arquivo
   - 3.2 Valida se hidrômetro está ativo
   - 3.3 Aplica pré-processamento (contraste, binarização)
   - 3.4 Executa OCR nos campos do display
   - 3.5 Extrai: vazão, volume, pressão, timestamp
   - 3.6 Valida dados (compara com última leitura)
   - 3.7 Persiste no banco de dados
   - 3.8 Verifica se há violação de limites
   - 3.9 Move imagem para pasta "processadas"
4. Sistema registra estatísticas de processamento

**Fluxos Alternativos:**
- 3.2a. Hidrômetro inativo → Pular processamento
- 3.4a. OCR falha → Registrar erro, tentar novamente após 1 min
- 3.6a. Dados inconsistentes → Marcar leitura como "pendente revisão"
- 3.8a. Limite violado → Disparar fluxo de geração de alerta

### UC-004: Gerar Alerta de Consumo Excessivo
**Ator:** Sistema  
**Pré-condições:** Limite de consumo configurado  
**Fluxo Principal:**
1. Sistema detecta consumo > limite configurado
2. Sistema verifica se já existe alerta ativo para este usuário/hidrômetro
3. Sistema cria registro de alerta no banco
4. Sistema obtém preferências de notificação do usuário
5. Sistema dispara notificações:
   - 5.1 Email para o usuário
   - 5.2 SMS (se configurado)
   - 5.3 Notificação no dashboard da concessionária
   - 5.4 Webhook para sistemas externos (se configurado)
6. Sistema registra log de auditoria

**Fluxos Alternativos:**
- 2a. Alerta já ativo → Atualizar alerta existente (não criar duplicado)
- 5.1a. Falha no envio de email → Registrar erro, tentar reenvio após 5 min
- 5.4a. Webhook timeout → Registrar falha, tentar novamente (máx. 3 tentativas)

---

## 11. INTERFACE DE EXEMPLO (Mockup Textual)

### Dashboard Principal - Visão Operador

```
╔══════════════════════════════════════════════════════════════════╗
║  CAGEPA - Painel de Monitoramento de Hidrômetros              ☰ ║
║  Operador: João Silva | Perfil: Operador | [Sair]               ║
╠══════════════════════════════════════════════════════════════════╣
║                                                                  ║
║  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐ ║
║  │  📊 10.245      │  │  ⚠️  23 Alertas │  │  📈 +3,2%       │ ║
║  │  Hidrômetros    │  │  Críticos       │  │  Consumo Médio  │ ║
║  │  Ativos         │  │                 │  │  vs. Mês Passado│ ║
║  └─────────────────┘  └─────────────────┘  └─────────────────┘ ║
║                                                                  ║
║  🔔 ALERTAS RECENTES                              [Ver Todos]   ║
║  ┌──────────────────────────────────────────────────────────┐  ║
║  │ ⚠️  CRÍTICO | SHA-1234 | João da Silva                    │  ║
║  │     Consumo: 15,2 m³/dia (Limite: 10 m³)                  │  ║
║  │     Ações: [Ver Detalhes] [Notificar] [Ignorar]           │  ║
║  ├──────────────────────────────────────────────────────────┤  ║
║  │ ⚠️  PREVENTIVO | SHA-5678 | Maria Santos                  │  ║
║  │     Consumo: 8,5 m³/dia (80% do limite)                   │  ║
║  │     Ações: [Ver Detalhes] [Notificar]                     │  ║
║  └──────────────────────────────────────────────────────────┘  ║
║                                                                  ║
║  📊 CONSUMO POR REGIÃO                                           ║
║  ┌──────────────────────────────────────────────────────────┐  ║
║  │  Centro: ████████████████░░ 82%                           │  ║
║  │  Zona Norte: ██████████░░░░░░░░ 65%                       │  ║
║  │  Zona Sul: ██████████████████░░ 91% 🔴                    │  ║
║  └──────────────────────────────────────────────────────────┘  ║
║                                                                  ║
║  [🔍 Buscar Usuário] [➕ Novo Usuário] [📄 Relatórios] [⚙️ Config] ║
╚══════════════════════════════════════════════════════════════════╝
```

### Tela de Monitoramento Individual

```
╔══════════════════════════════════════════════════════════════════╗
║  ← Voltar | Hidrômetro SHA-1234 - João da Silva                 ║
╠══════════════════════════════════════════════════════════════════╣
║                                                                  ║
║  📍 Rua das Flores, 123 - Centro - João Pessoa/PB              ║
║  🆔 SHA-1234 | ✅ Status: Ativo | 🕐 Última leitura: 10:45      ║
║                                                                  ║
║  ┌─────────────────────────────────────────────────────────────┐║
║  │              LEITURA ATUAL (Tempo Real)                     │║
║  │                                                              │║
║  │    Vazão: 12,5 L/min 📈                                     │║
║  │    Volume Total Hoje: 324,8 L                               │║
║  │    Volume Total Mês: 8.245 L (8,24 m³)                      │║
║  │    Pressão: 2,8 bar ✅                                       │║
║  └─────────────────────────────────────────────────────────────┘║
║                                                                  ║
║  📊 GRÁFICO DE CONSUMO (Últimas 24h)                            ║
║  ┌─────────────────────────────────────────────────────────────┐║
║  │ 15L/min│                          ⚪                        │║
║  │ 10L/min│              ⚪⚪⚪⚪⚪⚪⚪⚪                        │║
║  │  5L/min│    ⚪⚪⚪⚪⚪                        ⚪⚪⚪⚪      │║
║  │  0L/min│⚪⚪                                            ⚪⚪│║
║  │        └──────────────────────────────────────────────────  │║
║  │         10h  12h  14h  16h  18h  20h  22h  00h  02h  04h    │║
║  └─────────────────────────────────────────────────────────────┘║
║                                                                  ║
║  ⚙️  CONFIGURAÇÕES DE ALERTA                                    ║
║  ┌─────────────────────────────────────────────────────────────┐║
║  │  Limite Diário: 10 m³  [Editar]                             │║
║  │  Limite Mensal: 30 m³  [Editar]                             │║
║  │  Notificar por: ☑️ Email  ☑️ SMS  ☐ WhatsApp                │║
║  └─────────────────────────────────────────────────────────────┘║
║                                                                  ║
║  [📜 Ver Histórico] [🖼️ Ver Imagens] [📄 Gerar Relatório]       ║
╚══════════════════════════════════════════════════════════════════╝
```

---

## 12. GLOSSÁRIO

- **SHA:** Sistema de Hidrômetro de Água (hidrômetro físico que gera imagens)
- **OCR:** Optical Character Recognition (reconhecimento óptico de caracteres)
- **m³:** Metro cúbico (unidade de volume)
- **L/min:** Litros por minuto (unidade de vazão)
- **bar:** Unidade de pressão
- **DTO:** Data Transfer Object (objeto de transferência de dados)
- **DAO:** Data Access Object (objeto de acesso a dados)
- **CRUD:** Create, Read, Update, Delete (operações básicas de banco de dados)
- **Webhook:** Callback HTTP para notificação de eventos
- **JWT:** JSON Web Token (token de autenticação)

---

## 13. REFERÊNCIAS

- GAMMA, Erich et al. **Design Patterns: Elements of Reusable Object-Oriented Software**. Addison-Wesley, 1994.
- FOWLER, Martin. **Patterns of Enterprise Application Architecture**. Addison-Wesley, 2002.
- SOMMERVILLE, Ian. **Engenharia de Software**. 10ª ed. Pearson, 2018.
- Spring Framework Documentation: https://spring.io/projects/spring-boot
- Tesseract OCR: https://github.com/tesseract-ocr/tesseract

---

## APÊNDICE: Checklist de Implementação

### Fase 1: Fundação (Sprints 1-2)
- [ ] Configurar projeto Spring Boot
- [ ] Configurar banco de dados PostgreSQL
- [ ] Implementar Fachada (PainelMonitoramentoFacade)
- [ ] Implementar Subsistema de Persistência (DAO/Repository)
- [ ] Implementar Subsistema de Logs
- [ ] Implementar modelo de dados (entidades JPA)

### Fase 2: Gestão de Usuários (Sprint 3)
- [ ] Implementar CRUD de usuários
- [ ] Implementar CRUD de hidrômetros
- [ ] Implementar validações
- [ ] Testes unitários

### Fase 3: Processamento de Imagens (Sprints 4-5)
- [ ] Integrar Tesseract OCR
- [ ] Implementar pipeline de processamento
- [ ] Implementar scheduler de processamento
- [ ] Validação de dados extraídos
- [ ] Testes de integração

### Fase 4: Monitoramento de Consumo (Sprint 6)
- [ ] Implementar consultas de consumo
- [ ] Implementar agregações (diário, mensal)
- [ ] Implementar geração de gráficos
- [ ] Dashboard em tempo real

### Fase 5: Sistema de Alertas (Sprint 7)
- [ ] Implementar regras de alertas
- [ ] Implementar detecção de violações
- [ ] Integrar com subsistema de notificações

### Fase 6: Notificações (Sprint 8)
- [ ] Implementar envio de email
- [ ] Implementar envio de SMS
- [ ] Implementar webhooks
- [ ] Testes de integração

### Fase 7: Autenticação e Segurança (Sprint 9)
- [ ] Implementar login/logout
- [ ] Implementar controle de acesso (RBAC)
- [ ] Implementar criptografia de dados
- [ ] Testes de segurança

### Fase 8: Relatórios (Sprint 10)
- [ ] Implementar geração de relatórios PDF
- [ ] Implementar exportação CSV/Excel
- [ ] Relatórios customizados

### Fase 9: Interface do Usuário (Sprints 11-12)
- [ ] Implementar frontend web (React/Vue)
- [ ] Implementar CLI de administração
- [ ] Implementar API REST
- [ ] Testes de usabilidade

### Fase 10: Testes e Deploy (Sprint 13)
- [ ] Testes de integração completos
- [ ] Testes de performance
- [ ] Testes de carga
- [ ] Deploy em ambiente de produção
- [ ] Documentação final

---

**FIM DA ESPECIFICAÇÃO**
