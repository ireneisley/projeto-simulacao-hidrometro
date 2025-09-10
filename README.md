# Simulador de Hidrômetro Analógico

Aplicação OO em Java que simula um hidrômetro residencial, exibindo vazão, volume e pressão em tempo real. Projeto acadêmico da disciplina Padrões de Projeto — IFPB.

## Funcionalidades

- Simulação de vazão, volume e pressão da água.
- Interface gráfica amigável com ponteiros e visor digital.
- Status operacional em tempo real (normal, sem fluxo, pressão baixa).
- Parâmetros configuráveis via arquivo `config.properties`.
- Geração de imagens do estado do hidrômetro.

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

## Documentação UML

O diagrama UML das classes principais está disponível no arquivo `hidrometro-simulator.puml`.


## 🗂️Estrutura do Projeto

```
hidrometro-uml/
├── src/
│   └── main/
│       ├── java/
│       │   └── hidrometro/
│       │       ├── Configuracao.java
│       │       ├── ConfiguracaoDTO.java
│       │       ├── Controladora.java
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
│           ├── config.properties
│           └── images/
