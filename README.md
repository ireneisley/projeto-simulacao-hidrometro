# Simulador de Hidrômetro

Este projeto implementa um simulador de hidrômetro digital em Java, baseado no diagrama UML fornecido.

## Funcionalidades

- ✅ Simulação de medição de vazão, volume e pressão
- ✅ Geração de imagens em JPEG com controle de qualidade
- ✅ Simulação de falta de água e variações no fluxo
- ✅ Configuração via arquivo de propriedades
- ✅ Interface de linha de comando
- ✅ Logs detalhados da simulação

## Estrutura do Projeto

\`\`\`
src/main/java/hidrometro/
├── Main.java              # Classe principal
├── Controladora.java      # Orquestra a simulação
├── Hidrometro.java        # Representa o hidrômetro
├── Entrada.java           # Entrada de água
├── Saida.java            # Saída de água
├── Medidor.java          # Componente de medição
├── Display.java          # Exibição de informações
├── Configuracao.java     # Gerenciamento de configurações
├── GeradorImagem.java    # Geração de imagens JPEG/PNG
└── DadosHidrometro.java  # Encapsulamento de dados
\`\`\`

## Como Executar

### Pré-requisitos
- Java 11 ou superior
- Maven 3.6 ou superior

### Compilação
\`\`\`bash
mvn clean compile
\`\`\`

### Execução
\`\`\`bash
# Com arquivo de configuração padrão
mvn exec:java

# Com arquivo de configuração específico
mvn exec:java -Dexec.args="minha-config.properties"
\`\`\`

### Execução Direta
\`\`\`bash
# Compilar
javac -d target/classes src/main/java/hidrometro/*.java

# Executar
java -cp target/classes hidrometro.Main
\`\`\`

## Configuração

O arquivo `config.properties` permite configurar:

- `vazao.entrada`: Vazão de entrada em L/min (padrão: 10.0)
- `vazao.saida`: Vazão de saída em L/min (padrão: 9.5)
- `diametro.entrada`: Diâmetro da entrada em mm (padrão: 25.0)
- `diametro.saida`: Diâmetro da saída em mm (padrão: 20.0)
- `chance.falta.agua`: Chance de falta de água 0-100% (padrão: 5)
- `tempo.simulacao`: Tempo de simulação em segundos (padrão: 30)

## Saída

O simulador gera:
- **Logs no console**: Medições em tempo real
- **Imagens JPEG**: Mostradores do hidrômetro salvos como `hidrometro_[timestamp].jpg`
- **Relatório final**: Volume total medido ao final da simulação

## Exemplo de Saída

\`\`\`
=== Simulador de Hidrômetro ===
Configuração carregada com sucesso de: config.properties
Simulação iniciada
Hidrômetro iniciado
Executando simulação por 30 segundos...
Medição: 10.15 L/min | Pressão: 0.05 bar
Imagem salva: hidrometro_1703123456789.jpg
⚠️ Simulando falta de água...
💧 Água retornou
Medição: 9.87 L/min | Pressão: 0.03 bar
Hidrômetro parado
Volume total medido: 4.523 litros
Simulação concluída
\`\`\`

## Arquitetura

O projeto segue o padrão de arquitetura definido no diagrama UML:
- **Controladora**: Orquestra toda a simulação
- **Hidrometro**: Componente principal com entrada, saída, medidor e display
- **GeradorImagem**: Cria imagens JPEG com controle de qualidade
- **Configuracao**: Gerencia parâmetros via arquivo de propriedades

## Recursos Avançados

- **Simulação realística**: Variações de fluxo, presença de ar, falta de água
- **Geração de imagens**: Mostradores visuais salvos em JPEG com qualidade configurável
- **Multithreading**: Simulação de eventos assíncronos (retorno da água)
- **Configuração flexível**: Parâmetros ajustáveis via arquivo de propriedades
