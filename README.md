# Simulador de Múltiplos Hidrômetros Analógicos

Aplicação OO em Java que simula de 1 a 5 hidrômetros residenciais simultaneamente, exibindo vazão, volume e pressão em tempo real para cada um. Projeto acadêmico da disciplina Padrões de Projeto — IFPB.

## Funcionalidades

- **Múltiplos Hidrômetros**: Simulação de 1 a 5 hidrômetros simultâneos
- **Janelas Independentes**: Cada hidrômetro abre em sua própria janela
- **Configurações Individuais**: Cada hidrômetro pode ter parâmetros únicos
- Simulação de vazão, volume e pressão da água/ar
- Interface gráfica amigável com ponteiros e visor digital
- Status operacional em tempo real (normal, sem fluxo, pressão baixa)
- Parâmetros configuráveis via arquivo `config.properties`
- Geração de imagens do estado de cada hidrômetro
- Execução paralela com threads independentes para cada hidrômetro

## Tecnologias Utilizadas

- **Java** - Linguagem principal
- **Swing** - Interface gráfica
- **Java 2D** - Desenho e manipulação de imagens
- **PlantUML** - Documentação e diagrama UML
- **Arquivos .properties** - Configuração externa do simulador

## ▶️Como Executar 

1. **Clone o repositório**  
   `git clone <url-do-repositorio>`

2. **Compile o projeto**  
   No terminal, dentro da pasta do projeto:
   ```
   javac -d bin src/main/java/hidrometro/*.java
   ```

3. **Execute o simulador**  
   ```
   java -cp bin hidrometro.Main
   ```

## ⚙️Configuração 

Edite o arquivo `config.properties` para ajustar os parâmetros necessários.

### Configuração de Múltiplos Hidrômetros

```properties
# Número de hidrômetros a serem simulados (1 a 5)
numero.hidrometros=3

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


## � Características dos Múltiplos Hidrômetros

1. **Janelas Independentes**: Cada hidrômetro abre em uma janela separada
2. **Posicionamento Automático**: Janelas aparecem em cascata para melhor organização
3. **Configurações Independentes**: Cada hidrômetro pode ter parâmetros únicos
4. **Execução Paralela**: Threads independentes para cada hidrômetro
5. **Debug Seletivo**: Ative logs apenas para hidrômetros específicos
6. **Identificação Única**: Cada hidrômetro tem ID único (HIDROMETRO_1, HIDROMETRO_2, etc.)

## �🗂️Estrutura do Projeto

```
projeto-simulacao-hidrometro/
├── src/
│   └── main/
│       ├── java/
│       │   └── hidrometro/
│       │       ├── Configuracao.java          # Gerencia múltiplas configurações
│       │       ├── ConfiguracaoDTO.java       # DTO com ID único por hidrômetro
│       │       ├── Controladora.java          # Controlador para múltiplos hidrômetros
│       │       ├── DadosHidrometro.java
│       │       ├── Display.java
│       │       ├── Entrada.java
│       │       ├── GeradorImagem.java
│       │       ├── Hidrometro.java
│       │       ├── Main.java
│       │       ├── Medidor.java
│       │       ├── Saida.java
│       │       └── TipoFluido.java
│       └── resources/
│           ├── config.properties             # Configuração para múltiplos hidrômetros
│           └── images/
├── target/classes/                          # Arquivos compilados
├── MULTIPLOS_HIDROMETROS.md                # Documentação das modificações
└── README.md                               # Este arquivo
