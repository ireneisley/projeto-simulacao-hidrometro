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
        this(new ConfiguracaoDTO());
    }
    
    public Hidrometro(ConfiguracaoDTO config) {
        this.config = config;
        
        this.entrada = new Entrada(config.vazaoEntrada(), config.diametroEntrada());
        this.saida = new Saida(config.vazaoSaida(), config.diametroSaida());
        this.display = new Display();
        this.medidor = new Medidor(config.precisaoMedidor());
        this.geradorImagem = new GeradorImagem(); // Inicializa o gerador de imagem
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
            System.out.println("Hidrômetro não está funcionando - não é possível gerar imagem");
            return;
        }
        
        // Criar dados atuais do hidrômetro
        DadosHidrometro dados = new DadosHidrometro(
            medidor.calcularVazao(),
            volumeTotal,
            pressaoAtual
        );
        
        // Gerar imagem com a base do hidrômetro real
        java.awt.image.BufferedImage imagem = geradorImagem.criarImagemHidrometro(dados);
        
        // Salvar com timestamp para identificação única
        String nomeArquivo = String.format("hidrometro_%d", System.currentTimeMillis());
        geradorImagem.salvarImagemJPEG(imagem, nomeArquivo);
        
        System.out.printf("📸 Imagem gerada: %s.jpg | Vazão: %.2f L/min | Volume: %.3f L | Pressão: %.2f bar%n",
                         nomeArquivo, medidor.calcularVazao(), volumeTotal, pressaoAtual);
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
}
