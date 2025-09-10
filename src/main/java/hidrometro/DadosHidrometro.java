package hidrometro;

/**
 * Classe para encapsular os dados medidos pelo hidrômetro
 */
public class DadosHidrometro {
    private double vazao;
    private double volume;
    private double pressao;
    private long timestamp;
    private boolean statusAgua;
    
    public DadosHidrometro(double vazao, double volume, double pressao) {
        this.vazao = vazao;
        this.volume = volume;
        this.pressao = pressao;
        this.timestamp = System.currentTimeMillis();
        this.statusAgua = vazao > 0;
    }
    
    public double getVazao() {
        return vazao;
    }
    
    public double getVolume() {
        return volume;
    }
    
    public double getPressao() {
        return pressao;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public boolean isStatusAgua() {
        return statusAgua;
    }
    
    @Override
    public String toString() {
        return String.format(
            "DadosHidrometro{vazao=%.2f L/min, volume=%.3f L, pressao=%.2f bar, timestamp=%d, statusAgua=%s}",
            vazao, volume, pressao, timestamp, statusAgua
        );
    }
}
