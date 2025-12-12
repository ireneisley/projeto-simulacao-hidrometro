# Simulador de Múltiplos Hidrômetros Analógicos (SHA)

Aplicação OO em Java que simula de 1 a 5 hidrômetros residenciais simultaneamente, exibindo vazão, volume e pressão em tempo real para cada um. Projeto acadêmico da disciplina Padrões de Projeto — IFPB.

## 🎯 Padrões de Projeto Implementados

### 🏛️ Padrão Façade + Singleton

O projeto implementa uma **Fachada Singleton** que abstrai toda a complexidade dos subsistemas:

- **HidrometroFachada**: Classe Singleton que oferece interface unificada
- **5 Funcionalidades principais**:
  1. `configSimuladorSHA()` - Configurar parâmetros globais (7 parâmetros diretos)
  2. `criaSHA()` - Criar e iniciar instâncias SHA
  3. `finalizaSHA()` - Finalizar instância específica
  4. `modificaVazaoSHA()` - Alterar vazões em tempo real (implementado com setters)
  5. `habilitaGeracaoImagemSHA()` - Controlar geração de imagens por instância

## Funcionalidades

- **Múltiplos Hidrômetros**: Simulação de 1 a 5 hidrômetros simultâneos
- **Janelas Independentes**: Cada hidrômetro abre em sua própria janela (800x600px fixo)
- **Configurações Individuais**: Cada hidrômetro pode ter parâmetros únicos
- **Fachada Singleton**: Interface unificada para operação do sistema
- **Modificação Dinâmica de Vazão**: Altere vazões em tempo real através de `setVazaoEntrada()` e `setVazaoSaida()`
- **Geração de Imagens**: Captura do display visual e salva em PNG/JPEG configurável
- **Threads Independentes**: 4 threads por SHA (medição 1s, display, eventos 5s, imagens configurável)
- Simulação de vazão, volume e pressão da água/ar
- Interface gráfica com ponteiros rotativos e visor digital
- Status operacional em tempo real (normal, sem fluxo, pressão baixa)
- Parâmetros configuráveis via `config.properties` (inclui configurações de imagem)
- Execução paralela com threads independentes para cada hidrômetro
- **Cliente CLI**: Interface de linha de comando completa (7 opções + sair)

## Tecnologias Utilizadas

- **Java** - Linguagem principal
- **Swing** - Interface gráfica
- **Java 2D** - Desenho e manipulação de imagens
- **PlantUML** - Documentação e diagrama UML
- **Arquivos .properties** - Configuração externa do simulador

## ▶️ Como Executar 

```bash
# Compilar
javac -d target/classes -sourcepath src/main/java src/main/java/hidrometro/*.java src/main/java/com/hidrometro/imagem/*.java

# Executar Cliente CLI (Interface Interativa)
java -cp target/classes hidrometro.ClienteCLI
```

### Opção 2: Execução Direta (Modo Legacy)

1. **Clone o repositório**  
   `git clone <url-do-repositorio>`

2. **Compile o projeto**  
   No terminal, dentro da pasta do projeto:
   ```bash
   javac -d target/classes -sourcepath src/main/java src/main/java/hidrometro/*.java
   ```

3. **Execute o simulador**  
   ```bash
   java -cp target/classes hidrometro.Main
   ```

## ⚙️Configuração 

Edite o arquivo `config.properties` para ajustar os parâmetros necessários.

### Configuração de Múltiplos Hidrômetros

```properties
# Número de hidrômetros a serem simulados (1 a 5)
numero.hidrometros=3

# Configuração Global de Geração de Imagens
gerar.imagens=true
imagem.intervalo.segundos=5
imagem.diretorio=./imagens_hidrometros
imagem.largura=800
imagem.altura=600
imagem.formato=PNG

# Configurações específicas para cada hidrômetro
hidrometro1.vazao.entrada=10.0
hidrometro1.vazao.saida=9.5
hidrometro1.tipo.fluido=AGUA
hidrometro1.modo.debug=false

hidrometro2.vazao.entrada=15.0
hidrometro2.vazao.saida=14.2
hidrometro2.tipo.fluido=AGUA
hidrometro2.modo.debug=true

hidrometro3.vazao.entrada=8.5
hidrometro3.vazao.saida=8.0
hidrometro3.tipo.fluido=AR
hidrometro3.modo.debug=false
```

### Parâmetros Disponíveis por Hidrômetro

- `vazao.entrada` / `vazao.saida`: Vazões em L/min
- `diametro.entrada` / `diametro.saida`: Diâmetros em mm
- `chance.falta.agua`: Probabilidade de falta de água (0-100%)
- `tempo.simulacao`: Duração da simulação em segundos
- `tempo.atualizacao`: Intervalo de atualização do display em ms
- `precisao.medidor`: Precisão do medidor (0.0 a 1.0)
- `tipo.fluido`: AGUA ou AR
- `modo.debug`: true/false para logs detalhados

## Documentação UML

O diagrama UML das classes principais está disponível no arquivo `hidrometro-simulator.puml`.


## 🎨 Características dos Múltiplos Hidrômetros

1. **Janelas Independentes**: Cada hidrômetro abre em uma janela 800x600px (tamanho fixo)
2. **Posicionamento Automático**: Janelas aparecem em cascata para melhor organização
3. **Configurações Independentes**: Cada hidrômetro pode ter parâmetros únicos
4. **Execução Paralela**: Threads independentes para cada hidrômetro
5. **Debug Seletivo**: Ative logs apenas para hidrômetros específicos
6. **Identificação Única**: Cada hidrômetro tem ID único (HIDROMETRO_1, SHA_1, etc.)
7. **Captura de Display**: Geração de imagens captura o estado visual real do display
8. **Modificação em Tempo Real**: Vazões podem ser alteradas sem reiniciar o SHA
9. **Geração de Imagens Configurável**: PNG ou JPEG, com dimensões e intervalo personalizáveis
10. **Prints Silenciosos**: Logs de geração de imagem comentados para não poluir o CLI

## 📁 Estrutura do Projeto

```
projeto-simulacao-hidrometro/
├── src/
│   └── main/
│       ├── java/
│       │   ├── hidrometro/
│       │   │   ├── HidrometroFachada.java      # 🆕 Fachada Singleton (7 parâmetros config)
│       │   │   ├── ClienteCLI.java             # 🆕 Cliente CLI (7 opções)
│       │   │   ├── ExemploClienteSimples.java  # 🆕 Exemplo programático
│       │   │   ├── Configuracao.java           # 🔄 Gerencia configurações + imagens
│       │   │   ├── ConfiguracaoDTO.java        # DTO com ID único por hidrômetro
│       │   │   ├── Controladora.java           # 🔄 Controlador multithread
│       │   │   ├── DadosHidrometro.java
│       │   │   ├── Display.java                # 🔄 Display 800x600 fixo
│       │   │   ├── Entrada.java                # 🔄 Com setVazaoEntrada()
│       │   │   ├── GeradorImagem.java          # 🔄 Configurável (largura, altura, formato)
│       │   │   ├── Hidrometro.java             # 🔄 Com setVazaoEntrada/Saida() + captura
│       │   │   ├── Main.java
│       │   │   ├── Medidor.java
│       │   │   ├── Saida.java                  # 🔄 Com setVazaoSaida()
│       │   │   └── TipoFluido.java
│       │   └── com/
│       │       └── hidrometro/
│       │           └── imagem/
│       │               └── ImagemHidrometro.java
│       └── resources/
│           ├── config.properties              # 🔄 + configurações de imagem
│           └── images/
├── docs/                                      # 🆕 Documentação adicional
├── imagens_hidrometros/                       # 🆕 Diretório de saída de imagens
│   ├── SHA_1_atual.png                        # Imagem mais recente do SHA_1
│   └── SHA_2_atual.png                        # Imagem mais recente do SHA_2
├── target/classes/                            # Arquivos compilados
└── README.md                                  # Este arquivo
```
