# Especificação do Painel de Monitoramento de Hidrômetros

## 📋 Documentação do Projeto

Este diretório contém a especificação completa do **Painel de Monitoramento de Hidrômetros para Concessionária de Água (CAGEPA)**.

## 📁 Arquivos

### Especificação Principal
- **`especificacao-painel-monitoramento.md`** - Documento completo de requisitos (Markdown)
- **`especificacao-painel-monitoramento.pdf`** - Documento em formato PDF (gerado)

### Diagramas PlantUML

#### 📋 Versão 3.0 - Acadêmica Simplificada (Recomendado)

**Diagrama de Arquitetura Geral:**
- **`diagrama-arquitetura-geral-v3.puml`** ⭐ - Visão geral dos 5 subsistemas com padrões
- **`diagrama-geral-subsistemas.mmd`** - Diagrama Mermaid (visualização no GitHub)

**Diagramas de Classes por Subsistema:**
1. **`subsistema-gestao-usuarios.puml`** - Gestão de Clientes (sem login)
   - Padrões: Repository, DTO, Factory
   
2. **`subsistema-monitoramento-consumo-v3.puml`** ⭐ - Monitoramento simplificado
   - Padrões: Strategy (3 análises), Observer
   
3. **`subsistema-alertas.puml`** - Alertas + Notificações unificado
   - Padrões: Chain of Responsibility, Observer, Template Method
   
4. **`subsistema-processamento-imagens-v3.puml`** ⭐ - OCR com Tesseract
   - Padrões: Template Method, Adapter, @Scheduled
   
5. **`subsistema-autenticacao-jwt-admin.puml`** ⭐ - Autenticação Admin JWT
   - Padrões: Singleton, Proxy

**Diagrama de Sequência:**
- **`diagrama-sequencia-fluxo-completo.puml`** ⭐ - Fluxo: OCR → Alerta → Notificação

#### 📚 Versão 2.0 - Profissional (Referência)
<details>
<summary>Diagramas da versão complexa (9 subsistemas)</summary>

- `subsistema-monitoramento-consumo.puml` - Com Redis e ML
- `subsistema-processamento-imagens.puml` - Múltiplas APIs OCR
- `subsistema-autenticacao-seguranca.puml` - OAuth2, LDAP
- `subsistema-notificacoes.puml` - Webhooks, WhatsApp
</details>

## 🎨 Visualizando os Diagramas PlantUML

### Opção 1: VS Code (Recomendado)

1. Instale a extensão **PlantUML** no VS Code:
   - Pressione `Ctrl+P` (ou `Cmd+P` no Mac)
   - Digite: `ext install plantuml`
   - Instale: "PlantUML" por jebbs

2. Pré-requisitos:
   - **Java** instalado (PlantUML requer Java)
   - **Graphviz** (opcional, mas recomendado):
     - Windows: `choco install graphviz` ou baixe de https://graphviz.org/download/
     - Mac: `brew install graphviz`
     - Linux: `sudo apt-get install graphviz`

3. Visualizar diagrama:
   - Abra qualquer arquivo `.puml`
   - Pressione `Alt+D` para preview
   - Ou clique com botão direito → "Preview Current Diagram"

4. Exportar imagem:
   - Com o preview aberto, clique com botão direito
   - Escolha "Export Current Diagram" → PNG, SVG ou PDF

### Opção 2: PlantUML Online

1. Acesse: https://www.plantuml.com/plantuml/uml/
2. Copie o conteúdo de qualquer arquivo `.puml`
3. Cole no editor online
4. Visualize e exporte

### Opção 3: CLI PlantUML

```bash
# Instalar PlantUML
# Windows (Chocolatey)
choco install plantuml

# Mac (Homebrew)
brew install plantuml

# Linux (apt)
sudo apt-get install plantuml

# Gerar PNG de um diagrama
plantuml diagrama-geral-subsistemas.puml

# Gerar todos os diagramas
plantuml *.puml

# Gerar em formato SVG
plantuml -tsvg *.puml
```

## 📄 Gerando o PDF da Especificação

### Opção 1: VS Code com Markdown PDF

1. Instale a extensão **Markdown PDF**:
   ```
   ext install yzane.markdown-pdf
   ```

2. Abra `especificacao-painel-monitoramento.md`

3. Pressione `Ctrl+Shift+P` (ou `Cmd+Shift+P` no Mac)

4. Digite: "Markdown PDF: Export (pdf)"

5. O PDF será gerado na mesma pasta

### Opção 2: Pandoc (Mais Profissional)

```bash
# Instalar Pandoc
# Windows
choco install pandoc

# Mac
brew install pandoc

# Linux
sudo apt-get install pandoc

# Gerar PDF
pandoc especificacao-painel-monitoramento.md -o especificacao-painel-monitoramento.pdf --pdf-engine=xelatex -V geometry:margin=1in

# Com sumário
pandoc especificacao-painel-monitoramento.md -o especificacao-painel-monitoramento.pdf --toc --pdf-engine=xelatex -V geometry:margin=1in
```

### Opção 3: Usando o Script Incluído

No diretório raiz do projeto:

```bash
# Linux/Mac
./gerar-pdf.sh

# Windows (Git Bash)
bash gerar-pdf.sh

# Windows (PowerShell)
.\gerar-pdf.ps1
```

## 🏗️ Estrutura da Especificação

### Versão 3.0 - Acadêmica Simplificada ⭐

### 1. Introdução
- Propósito: Painel Web para monitorar consumo de água via imagens SHA
- Restrições: Sistema acadêmico, apenas Admins com login, Clientes são cadastros

### 2. Requisitos Funcionais (18 total)
- **RF-001 a RF-004:** Gestão de Clientes (cadastro sem login)
- **RF-005 a RF-009:** Monitoramento de Consumo (3 tipos de análise)
- **RF-010 a RF-013:** Sistema de Alertas (preventivo, crítico, vazamento)
- **RF-014 a RF-016:** Processamento OCR (Tesseract, agendado)
- **RF-017 a RF-018:** Autenticação Admin JWT

### 3. Requisitos Não-Funcionais (6 total)
- Desempenho: 100 req/s, OCR < 30s
- Disponibilidade: 99.5%
- Segurança: JWT, BCrypt, HTTPS
- Usabilidade: Interface responsiva

### 4. Arquitetura do Sistema - 5 Subsistemas
1. **Gestão de Clientes** - CRUD + vinculação SHA
2. **Monitoramento de Consumo** - Análise Strategy
3. **Alertas e Notificações** - Chain + Observer
4. **Processamento de Imagens** - Template Method + Tesseract
5. **Autenticação JWT** - Singleton + Proxy

### 5. Especificação da Fachada
- Métodos principais da `PainelMonitoramentoFacade`
- **11 Padrões de Projeto** aplicados
- Interface unificada para 5 subsistemas

### 6. Armazenamento de Dados
- PostgreSQL 14+ (produção) | H2 (desenvolvimento)
- **6 Tabelas:** Cliente, Hidrometro, Leitura, Alerta, Admin, Log

### 7. Diagramas de Classes
- Diagrama geral de arquitetura v3
- 5 diagramas detalhados por subsistema
- 1 diagrama de sequência completo

### 8. Fluxos Principais
- Cadastro de cliente + vinculação SHA
- Processamento OCR agendado
- Disparo de alertas e notificações

### 9. Checklist de Implementação
- **6 Sprints** divididas por subsistema
- Testes unitários e integração

---

<details>
<summary>📚 Histórico - Versão 2.0 Profissional</summary>

- 9 subsistemas (Redis, ML, múltiplas OCR APIs)
- 20 requisitos funcionais
- OAuth2, LDAP, webhooks
- InfluxDB para métricas
</details>

## 🎯 Padrões de Projeto Utilizados (11 Total)

### Padrões Estruturais
- **Facade** ⭐ - Interface unificada `PainelMonitoramentoFacade`
- **Adapter** - Integração com Tesseract OCR
- **Proxy** - Interceptor JWT para autenticação
- **Repository** - Camada de acesso a dados

### Padrões Comportamentais
- **Strategy** ⭐ - 3 estratégias de análise (Diária, Mensal, Por Cliente)
- **Observer** ⭐ - Notificações automáticas (Email + SMS opcional)
- **Template Method** ⭐ - Pipeline OCR (6 etapas)
- **Chain of Responsibility** ⭐ - 3 regras de alerta (Preventivo → Crítico → Vazamento)

### Padrões Criacionais
- **Singleton** - JwtTokenService (gerenciamento de tokens)
- **Factory** - HidrometroFactory (criação de entidades)
- **DTO** - Transferência de dados entre camadas

### Mapeamento por Subsistema

| Subsistema | Padrões Aplicados |
|------------|------------------|
| Gestão de Clientes | Repository, DTO, Factory |
| Monitoramento | Strategy, Observer |
| Alertas | Chain, Observer, Template Method |
| Processamento OCR | Template Method, Adapter |
| Autenticação | Singleton, Proxy |

## 🔧 Tecnologias Recomendadas (Versão 3.0)

### Backend
- **Java 17+** com **Spring Boot 3.x**
- **Spring Data JPA** (Repository pattern)
- **Spring Security** + **JWT** (autenticação Admin)
- **Spring Scheduler** (processamento OCR a cada 5 min)

### Banco de Dados
- **PostgreSQL 14+** (produção)
- **H2 Database** (desenvolvimento/testes)
- **6 Tabelas:** Cliente, Hidrometro, Leitura, Alerta, Admin, Log

### Processamento de Imagens
- **Tesseract OCR 4.x** (reconhecimento de dígitos)
- Pré-processamento: OpenCV ou ImageMagick

### Notificações
- **JavaMail** (Email - obrigatório)
- **Twilio SMS** (opcional)

### Frontend (Sugestão)
- React.js ou Vue.js (dashboard responsivo)
- Chart.js (gráficos de consumo)

### Segurança
- **BCrypt** (hash de senhas Admin)
- **JWT** (tokens com 8h de validade)
- **HTTPS** obrigatório em produção

### Ferramentas de Desenvolvimento
- Maven 3.8+
- Git
- Docker (opcional - containerização)
- PlantUML (diagramas)

## 📊 Diagrama de Contexto (Versão 3.0)

```
┌──────────────┐
│    Admin     │ (Login JWT)
└──────┬───────┘
       │ Gerencia
       ▼
┌─────────────────────────────────────┐
│  Painel de Monitoramento (Facade)   │
│  ┌───────────────────────────────┐  │
│  │ 1. Gestão de Clientes         │  │
│  │ 2. Monitoramento de Consumo   │  │
│  │ 3. Alertas e Notificações     │  │
│  │ 4. Processamento OCR          │  │
│  │ 5. Autenticação JWT           │  │
│  └───────────────────────────────┘  │
└──┬───────────┬────────────────┬─────┘
   │           │                │
   ▼           ▼                ▼
┌──────┐  ┌─────────┐    ┌──────────┐
│  BD  │  │ Tesseract│   │Email/SMS │
│ (6   │  │   OCR    │   │(Notific.)│
│tabelas)│ │          │   │          │
└──────┘  └─────────┘    └──────────┘
              ▲
              │ Lê imagens
              │
        ┌─────────────┐
        │ SHA         │ (Sistema Hidromecânico de Abastecimento)
        │ (Hidrômetros│  ← Clientes sem acesso ao sistema
        │  + Imagens) │     (cadastrados pelo Admin)
        └─────────────┘
```

**Fluxo Principal:**
1. Admin faz login (JWT) → Cadastra Cliente → Vincula SHA
2. @Scheduled (5 min) → Tesseract processa imagens SHA
3. Análise detecta anomalia → Chain avalia regras
4. Observer dispara Email (+ SMS opcional)

## ✅ Checklist de Implementação (6 Sprints)

### Sprint 1: Setup + Autenticação JWT ⏱️ 1 semana
- [ ] Configurar projeto Spring Boot 3.x
- [ ] Configurar PostgreSQL/H2
- [ ] Implementar entidade `Admin`
- [ ] Criar `JwtTokenService` (Singleton)
- [ ] Criar `JwtAuthenticationInterceptor` (Proxy)
- [ ] Testes de login e validação de token

### Sprint 2: Gestão de Clientes 📋 1 semana
- [ ] Implementar entidades `Cliente` e `Hidrometro`
- [ ] Criar `ClienteRepository` e `HidrometroRepository`
- [ ] Implementar `ClienteDTO` e `HidrometroFactory`
- [ ] CRUD completo de clientes
- [ ] Vinculação Cliente ↔ SHA
- [ ] Testes unitários

### Sprint 3: Processamento OCR 🖼️ 1,5 semanas
- [ ] Integrar Tesseract OCR (Adapter)
- [ ] Implementar `ProcessadorImagemTemplate` (Template Method)
- [ ] Criar 6 etapas do pipeline: validação → pré-processamento → OCR → validação → persistência → limpeza
- [ ] Configurar `@Scheduled` (5 minutos)
- [ ] Salvar leituras na tabela `Leitura`
- [ ] Testes com imagens SHA reais

### Sprint 4: Monitoramento de Consumo 📊 1 semana
- [ ] Implementar `AnalisadorConsumoStrategy` (interface)
- [ ] Criar 3 estratégias: `AnaliseConsumoMensal`, `AnaliseDiaria`, `AnalisePorCliente`
- [ ] Implementar `ObservadorLeitura` (Observer)
- [ ] Gerar estatísticas e métricas
- [ ] Dashboard com gráficos
- [ ] Testes de cada estratégia

### Sprint 5: Alertas e Notificações 🚨 1,5 semanas
- [ ] Implementar entidade `Alerta`
- [ ] Criar Chain: `RegraPreventiva` → `RegraCritica` → `RegraVazamento`
- [ ] Implementar `NotificadorTemplate` (Template Method)
- [ ] Configurar JavaMail (Email obrigatório)
- [ ] Integrar Twilio SMS (opcional)
- [ ] Implementar `ObservadorAlerta` (Observer)
- [ ] Testes de disparo de alertas

### Sprint 6: Facade + Integração Final 🎯 1 semana
- [ ] Implementar `PainelMonitoramentoFacade`
- [ ] Integrar todos os 5 subsistemas
- [ ] Testes de integração end-to-end
- [ ] Configurar logs (tabela `Log`)
- [ ] Documentação final
- [ ] Deploy em ambiente de homologação

**Total Estimado:** 7 semanas (1,75 meses)

---

**Validação Acadêmica:**
- ✅ 11 Padrões de Projeto demonstrados
- ✅ 5 Subsistemas bem definidos
- ✅ Complexidade adequada para 6º período
- ✅ Documentação completa com diagramas PlantUML

## 📞 Contato

Para dúvidas sobre a especificação:
- **Disciplina:** Padrões de Projeto
- **Instituição:** IFPB
- **Projeto:** Simulação de Hidrômetros

## 📝 Licença

Este documento é parte de um projeto acadêmico do IFPB.

---

**Data da Especificação:** 18 de Janeiro de 2025  
**Versão:** 3.0 - Acadêmica Simplificada ⭐  
**Instituição:** IFPB - Instituto Federal da Paraíba  
**Disciplina:** Padrões de Projeto  
**Semestre:** 6º período - Engenharia de Computação
