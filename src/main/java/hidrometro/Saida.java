package hidrometro;

/**
 * Representa a saída de água do hidrômetro
 */
public class Saida {
    private double vazaoSaida;
    private double diametroSaida;
    private double fluxoSaida;
    
    public Saida(double vazao, double diametro) {
        this.vazaoSaida = vazao;
        this.diametroSaida = diametro;
        this.fluxoSaida = vazao;
    }
    
    public double getFluxoSaida() {
        return fluxoSaida;
    }
    
    public void setVazaoSaida(double vazao) {
        this.vazaoSaida = Math.max(0, vazao);
        this.fluxoSaida = this.vazaoSaida;
    }
    
    public double calcularPressaoSaida() {
        // Calcular pressão na saída baseada no fluxo e diâmetro
        // Fórmula simplificada: P = (fluxo^2) / (área^2)
        double area = Math.PI * Math.pow(diametroSaida / 2000, 2); // Converter mm para m
        double velocidade = (fluxoSaida / 60000) / area; // L/min para m³/s
        
        // Pressão dinâmica simplificada (Pascal)
        return 0.5 * 1000 * Math.pow(velocidade, 2); // ρ = 1000 kg/m³ para água
    }
}
