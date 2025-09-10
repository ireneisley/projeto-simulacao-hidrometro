package hidrometro;

/**
 * Classe para encapsular os dados de configuração do simulador
 */
public class ConfiguracaoDTO {
    private final double vazaoEntrada;
    private final double vazaoSaida;
    private final double diametroEntrada;
    private final double diametroSaida;
    private final int chanceFaltaAgua;
    private final int tempoSimulacao;
    private final int tempoAtualizacao;
    private final double precisaoMedidor;
    private final TipoFluido tipoFluido;
    private final boolean modoDebug;
    
    /**
     * Construtor completo
     */
    public ConfiguracaoDTO(double vazaoEntrada, double vazaoSaida, double diametroEntrada, 
                          double diametroSaida, int chanceFaltaAgua, int tempoSimulacao,
                          int tempoAtualizacao, double precisaoMedidor, TipoFluido tipoFluido, 
                          boolean modoDebug) {
        this.vazaoEntrada = vazaoEntrada;
        this.vazaoSaida = vazaoSaida;
        this.diametroEntrada = diametroEntrada;
        this.diametroSaida = diametroSaida;
        this.chanceFaltaAgua = chanceFaltaAgua;
        this.tempoSimulacao = tempoSimulacao;
        this.tempoAtualizacao = tempoAtualizacao;
        this.precisaoMedidor = precisaoMedidor;
        this.tipoFluido = tipoFluido;
        this.modoDebug = modoDebug;
    }
    
    /**
     * Construtor com valores padrão
     */
    public ConfiguracaoDTO() {
        this(10.0, 9.5, 25.0, 20.0, 5, 30, 100, 0.95, TipoFluido.AGUA, false);
    }
    
    public double vazaoEntrada() { return vazaoEntrada; }
    public double vazaoSaida() { return vazaoSaida; }
    public double diametroEntrada() { return diametroEntrada; }
    public double diametroSaida() { return diametroSaida; }
    public int chanceFaltaAgua() { return chanceFaltaAgua; }
    public int tempoSimulacao() { return tempoSimulacao; }
    public int tempoAtualizacao() { return tempoAtualizacao; }
    public double precisaoMedidor() { return precisaoMedidor; }
    public TipoFluido tipoFluido() { return tipoFluido; }
    public boolean modoDebug() { return modoDebug; }
    
    /**
     * Valida se a configuração está correta
     */
    public boolean isValida() {
        return vazaoEntrada > 0 && vazaoSaida > 0 && 
               diametroEntrada > 0 && diametroSaida > 0 &&
               chanceFaltaAgua >= 0 && chanceFaltaAgua <= 100 &&
               tempoSimulacao > 0 && tempoAtualizacao > 0 &&
               precisaoMedidor > 0 && precisaoMedidor <= 1.0;
    }
}
