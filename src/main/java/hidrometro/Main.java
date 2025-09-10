package hidrometro;

/**
 * Classe principal para executar o simulador de hidrômetro
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=== Simulador de Hidrômetro ===");
        
        try {
            // Criar e configurar a controladora
            Controladora controladora = new Controladora();
            
            // Carregar configuração (arquivo padrão ou especificado)
            String arquivoConfig = args.length > 0 ? args[0] : "config.properties";
            controladora.carregarConfiguracao(arquivoConfig);
            
            // Executar simulação
            controladora.executar();
            
        } catch (Exception e) {
            System.err.println("Erro na execução: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
