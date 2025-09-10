package hidrometro;

import java.io.*;
import java.util.Properties;

/**
 * Classe para carregar e gerenciar configurações da simulação
 */
public class Configuracao {
    private ConfiguracaoDTO config;
    
    public Configuracao() {
        this.config = new ConfiguracaoDTO(); // Valores padrão
    }
    
    public void carregarDeArquivo(String arquivo) throws IOException {
        Properties props = new Properties();
        
        try (InputStream input = new FileInputStream(arquivo)) {
            props.load(input);
            
            config = new ConfiguracaoDTO(
                Double.parseDouble(props.getProperty("vazao.entrada", "10.0")),
                Double.parseDouble(props.getProperty("vazao.saida", "9.5")),
                Double.parseDouble(props.getProperty("diametro.entrada", "25.0")),
                Double.parseDouble(props.getProperty("diametro.saida", "20.0")),
                Integer.parseInt(props.getProperty("chance.falta.agua", "5")),
                Integer.parseInt(props.getProperty("tempo.simulacao", "30")),
                Integer.parseInt(props.getProperty("tempo.atualizacao", "100")),
                Double.parseDouble(props.getProperty("precisao.medidor", "0.95")),
                TipoFluido.valueOf(props.getProperty("tipo.fluido", "AGUA")),
                Boolean.parseBoolean(props.getProperty("modo.debug", "false"))
            );
            
        } catch (FileNotFoundException e) {
            criarArquivoConfiguracao(arquivo);
            throw new IOException("Arquivo de configuração não encontrado. Criado arquivo padrão: " + arquivo);
        }
    }
    
    private void criarArquivoConfiguracao(String arquivo) {
        Properties props = new Properties();
        props.setProperty("vazao.entrada", String.valueOf(config.vazaoEntrada()));
        props.setProperty("vazao.saida", String.valueOf(config.vazaoSaida()));
        props.setProperty("chance.falta.agua", String.valueOf(config.chanceFaltaAgua()));
        props.setProperty("diametro.entrada", String.valueOf(config.diametroEntrada()));
        props.setProperty("diametro.saida", String.valueOf(config.diametroSaida()));
        props.setProperty("tempo.simulacao", String.valueOf(config.tempoSimulacao()));
        props.setProperty("tempo.atualizacao", String.valueOf(config.tempoAtualizacao()));
        props.setProperty("precisao.medidor", String.valueOf(config.precisaoMedidor()));
        props.setProperty("tipo.fluido", config.tipoFluido().name());
        props.setProperty("modo.debug", String.valueOf(config.modoDebug()));
        
        try (OutputStream output = new FileOutputStream(arquivo)) {
            props.store(output, "Configuração do Simulador de Hidrômetro");
        } catch (IOException e) {
            System.err.println("Erro ao criar arquivo de configuração: " + e.getMessage());
        }
    }
    
    public boolean validarConfiguracao() {
        return config.isValida();
    }
    
    public ConfiguracaoDTO getConfig() { return config; }
    public double getVazaoEntrada() { return config.vazaoEntrada(); }
    public double getVazaoSaida() { return config.vazaoSaida(); }
    public int getChanceFaltaAgua() { return config.chanceFaltaAgua(); }
    public double getDiametroEntrada() { return config.diametroEntrada(); }
    public double getDiametroSaida() { return config.diametroSaida(); }
    public int getTempoSimulacao() { return config.tempoSimulacao(); }
    public int getTempoAtualizacao() { return config.tempoAtualizacao(); }
    public double getPrecisaoMedidor() { return config.precisaoMedidor(); }
    public TipoFluido getTipoFluido() { return config.tipoFluido(); }
    public boolean isModoDebug() { return config.modoDebug(); }
}
