package hidrometro;

/**
 * Enum para representar os tipos de fluido que podem passar pelo hidrômetro
 */
public enum TipoFluido {
    AGUA("Água"),
    AR("Ar");
    
    private final String descricao;
    
    TipoFluido(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    @Override
    public String toString() {
        return descricao;
    }
}
