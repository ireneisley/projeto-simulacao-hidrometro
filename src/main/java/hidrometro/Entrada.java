package hidrometro;

/**
 * Representa a entrada de água do hidrômetro
 */
public class Entrada {
    private double vazaoEntrada;
    private double diametroEntrada;
    private double fluxoAtual;
    private boolean temAgua;
    
    public Entrada(double vazao, double diametro) {
        this.vazaoEntrada = vazao;
        this.diametroEntrada = diametro;
        this.fluxoAtual = vazao;
        this.temAgua = true;
    }
    
    public double getFluxoAtual() {
        return fluxoAtual;
    }
    
    public boolean isTemAgua() {
        return temAgua;
    }
    
    public void setVazaoEntrada(double vazao) {
        this.vazaoEntrada = Math.max(0, vazao);
        if (temAgua) {
            this.fluxoAtual = this.vazaoEntrada;
        }
    }
    
    public void simularFaltaAgua() {
        temAgua = false;
        fluxoAtual = 0.0;
        
        // Simular retorno da água após um tempo
        new Thread(() -> {
            try {
                Thread.sleep(2000 + (int)(Math.random() * 3000)); // 2-5 segundos
                temAgua = true;
                fluxoAtual = vazaoEntrada;
                System.out.println("💧 Água retornou");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
    
    public void simularVariacaoAr() {
        if (!temAgua) return;
        
        // Ar tem variações mais bruscas
        double variacao = (Math.random() - 0.5) * 0.4; // -20% a +20%
        double fluxoComVariacao = vazaoEntrada * (1 + variacao);
        fluxoAtual = Math.max(0, fluxoComVariacao);
    }
    
    public double calcularFluxoComAr() {
        if (!temAgua) {
            return 0.0;
        }
        
        // Simular variações no fluxo (±10%)
        double variacao = (Math.random() - 0.5) * 0.2; // -10% a +10%
        double fluxoComVariacao = vazaoEntrada * (1 + variacao);
        
        // Simular presença ocasional de ar (reduz fluxo)
        if (Math.random() < 0.05) { // 5% chance
            fluxoComVariacao *= 0.7; // Reduz 30% por causa do ar
        }
        
        return Math.max(0, fluxoComVariacao);
    }
}
