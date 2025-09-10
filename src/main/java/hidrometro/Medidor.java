package hidrometro;

/**
 * Componente responsável pelas medições do hidrômetro
 */
public class Medidor {
    private double volumeAcumulado;
    private double vazaoAtual;
    private long ultimaMedicao;
    private double precisao;
    
    public Medidor() {
        this(0.95); // 95% de precisão padrão
    }
    
    public Medidor(double precisao) {
        this.volumeAcumulado = 0.0;
        this.vazaoAtual = 0.0;
        this.ultimaMedicao = System.currentTimeMillis();
        this.precisao = Math.max(0.1, Math.min(1.0, precisao)); // Entre 10% e 100%
    }
    
    public double medirVolume(double fluxo) {
        long agora = System.currentTimeMillis();
        double tempoDecorrido = (agora - ultimaMedicao) / 1000.0; // segundos
        
        // Calcular volume baseado no fluxo (L/min para L/s)
        double volumeInstantaneo = (fluxo / 60.0) * tempoDecorrido;
        
        // Aplicar precisão do medidor
        volumeInstantaneo *= precisao;
        
        double fatorRuido = (1.0 - precisao) * 0.1; // Ruído proporcional à imprecisão
        double ruido = (Math.random() - 0.5) * fatorRuido;
        volumeInstantaneo *= (1 + ruido);
        
        volumeAcumulado += volumeInstantaneo;
        vazaoAtual = fluxo;
        ultimaMedicao = agora;
        
        return volumeInstantaneo;
    }
    
    public double calcularVazao() {
        return vazaoAtual;
    }
    
    public void resetarMedicao() {
        volumeAcumulado = 0.0;
        vazaoAtual = 0.0;
        ultimaMedicao = System.currentTimeMillis();
    }
    
    public double getVolumeTotal() {
        return volumeAcumulado;
    }
    
    public double getPrecisao() {
        return precisao;
    }
}
