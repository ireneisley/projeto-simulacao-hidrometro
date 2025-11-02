package hidrometro;

/**
 * Representa o hidrômetro com todos seus componentes
 */
public class Hidrometro {
    private Entrada entrada;
    private Saida saida;
    private Display display;
    private Medidor medidor;
    private double pressaoAtual;
    private double volumeTotal;
    private boolean funcionando;
    
    private ConfiguracaoDTO config;
    private GeradorImagem geradorImagem; // Adicionado gerador de imagem como atributo da classe
    
    public Hidrometro() {
        this(new ConfiguracaoDTO(), 800, 600, "./imagens_hidrometros", "PNG");
    }
    
    public Hidrometro(ConfiguracaoDTO config) {
        this(config, 800, 600, "./imagens_hidrometros", "PNG");
    }
    
    public Hidrometro(ConfiguracaoDTO config, int largura, int altura, String diretorio, String formato) {
        this.config = config;
        
        this.entrada = new Entrada(config.vazaoEntrada(), config.diametroEntrada());
        this.saida = new Saida(config.vazaoSaida(), config.diametroSaida());
        this.display = new Display();
        this.medidor = new Medidor(config.precisaoMedidor());
        this.geradorImagem = new GeradorImagem(largura, altura, diretorio, formato, config.id());
        this.pressaoAtual = 0.0;
        this.volumeTotal = 0.0;
        this.funcionando = false;
    }
    
    public double medir() {
        if (!funcionando) {
            return 0.0;
        }
        
        processarFluxo();
        double fluxoAtual = entrada.getFluxoAtual();
        double volume = medidor.medirVolume(fluxoAtual);
        
        volumeTotal += volume;
        
        // Atualizar display
        display.atualizarDisplay(medidor.calcularVazao(), volumeTotal, pressaoAtual);
        
        return medidor.calcularVazao();
    }
    
    public double calcularPressao() {
        if (!funcionando) {
            return 0.0;
        }
        
        // Calcular pressão baseada na diferença de fluxo
        double fluxoEntrada = entrada.getFluxoAtual();
        double fluxoSaida = saida.getFluxoSaida();
        
        double densidadeFluido = config.tipoFluido() == TipoFluido.AGUA ? 1000.0 : 1.225; // kg/m³
        double diferenca = Math.max(0, fluxoEntrada - fluxoSaida);
        pressaoAtual = (diferenca * densidadeFluido) / 10000.0; // Conversão para bar
        
        return pressaoAtual;
    }
    
    public void simularFaltaAgua() {
        if (entrada != null) {
            entrada.simularFaltaAgua();
        }
    }
    
    public void gerarImagemAtualizada() {
        if (!funcionando) {
            if (config.modoDebug()) {
                System.out.println("Hidrômetro " + config.id() + " não está funcionando - não é possível gerar imagem");
            }
            return;
        }
        
        // Capturar a imagem diretamente do Display
        java.awt.image.BufferedImage imagem = capturarImagemDisplay();
        
        if (imagem != null) {
            // Salvar com nome fixo (será substituído a cada vez)
            String nomeArquivo = "atual";  // Nome fixo para substituir
            geradorImagem.salvarImagem(imagem, nomeArquivo);
            
        } else {
            System.err.println("[" + config.id() + "] Falha ao capturar imagem do display");
        }
    }
    
    /**
     * Captura a imagem atual do display visual
     */
    private java.awt.image.BufferedImage capturarImagemDisplay() {
        try {
            // Obter o tamanho do painel Display
            int width = display.getWidth();
            int height = display.getHeight();
            
            if (width <= 0 || height <= 0) {
                // Display ainda não foi renderizado, usar tamanho padrão
                width = 800;
                height = 600;
            }
            
            // Garantir que o display seja atualizado antes de capturar
            final int finalWidth = width;
            final int finalHeight = height;
            final java.awt.image.BufferedImage[] imagemCapturada = new java.awt.image.BufferedImage[1];
            
            // Executar na thread do EDT para garantir renderização correta
            javax.swing.SwingUtilities.invokeAndWait(() -> {
                try {
                    // Forçar atualização do display
                    display.revalidate();
                    display.repaint();
                    
                    // Aguardar um pouco para garantir renderização
                    Thread.sleep(100);
                    
                    // Criar BufferedImage para capturar
                    java.awt.image.BufferedImage imagem = new java.awt.image.BufferedImage(
                        finalWidth, finalHeight, java.awt.image.BufferedImage.TYPE_INT_RGB
                    );
                    
                    // Renderizar o Display na imagem
                    java.awt.Graphics2D g2d = imagem.createGraphics();
                    g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, 
                                        java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                    display.paint(g2d);
                    g2d.dispose();
                    
                    imagemCapturada[0] = imagem;
                } catch (Exception e) {
                    System.err.println("Erro ao renderizar display: " + e.getMessage());
                }
            });
            
            return imagemCapturada[0];
        } catch (Exception e) {
            System.err.println("Erro ao capturar imagem do display: " + e.getMessage());
            if (config.modoDebug()) {
                e.printStackTrace();
            }
            return null;
        }
    }
    
    public void exibirImagem() {
        if (!funcionando) {
            return;
        }
        
        // Criar dados para geração da imagem
        DadosHidrometro dados = new DadosHidrometro(
            medidor.calcularVazao(),
            volumeTotal,
            pressaoAtual
        );
        
        // Gerar e salvar imagem
        java.awt.image.BufferedImage imagem = geradorImagem.criarImagemHidrometro(dados);
        
        String nomeArquivo = "hidrometro_" + System.currentTimeMillis();
        geradorImagem.salvarImagemJPEG(imagem, nomeArquivo);
        
        if (config.modoDebug()) {
            System.out.println("Imagem salva: " + nomeArquivo + ".jpg");
        }
    }
    
    public void iniciar() {
        funcionando = true;
        medidor.resetarMedicao();
        System.out.println("Hidrômetro iniciado - Tipo de fluido: " + config.tipoFluido());
    }
    
    public void parar() {
        funcionando = false;
        display.ocultarInterface();
        System.out.println("Hidrômetro parado");
        System.out.println("Volume total medido: " + volumeTotal + " litros");
    }
    
    private void processarFluxo() {
        if (config.tipoFluido() == TipoFluido.AR) {
            // Ar tem mais variações
            entrada.simularVariacaoAr();
        }
        
        // Calcular fluxo atual considerando possível presença de ar
        double fluxo = entrada.calcularFluxoComAr();
        entrada.setVazaoEntrada(fluxo);
    }
    
    public Display getDisplay() {
        return display;
    }
    
    public ConfiguracaoDTO getConfig() {
        return config;
    }
    
    /**
     * Modifica a vazão de entrada do hidrômetro
     * @param novaVazao Nova vazão em L/min
     */
    public void setVazaoEntrada(double novaVazao) {
        if (entrada != null) {
            entrada.setVazaoEntrada(novaVazao);
        }
    }
    
    /**
     * Modifica a vazão de saída do hidrômetro
     * @param novaVazao Nova vazão em L/min
     */
    public void setVazaoSaida(double novaVazao) {
        if (saida != null) {
            saida.setVazaoSaida(novaVazao);
        }
    }
}
