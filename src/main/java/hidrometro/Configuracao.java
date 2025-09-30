package hidrometro;

import java.io.*;
import java.util.*;

/**
 * Classe para carregar e gerenciar configurações da simulação
 */
public class Configuracao {
    private List<ConfiguracaoDTO> configuracoes;
    private int numeroHidrometros;

    public Configuracao() {
        this.configuracoes = new ArrayList<>();
        this.numeroHidrometros = 1;
        // Adicionar configuração padrão para um hidrômetro
        this.configuracoes.add(new ConfiguracaoDTO());
    }

    public void carregarDeArquivo(String arquivo) throws IOException {
        Properties props = new Properties();

        try (InputStream input = new FileInputStream(arquivo)) {
            props.load(input);

            // Determinar número de hidrômetros baseado nas propriedades
            numeroHidrometros = Integer.parseInt(props.getProperty("numero.hidrometros", "1"));

            // Validar número de hidrômetros (1 a 5)
            if (numeroHidrometros < 1 || numeroHidrometros > 5) {
                numeroHidrometros = 1;
            }

            configuracoes.clear();

            // Carregar configuração para cada hidrômetro
            for (int i = 1; i <= numeroHidrometros; i++) {
                String prefix = "hidrometro" + i + ".";
                String id = "HIDROMETRO_" + i;

                ConfiguracaoDTO config = new ConfiguracaoDTO(
                        id,
                        Double.parseDouble(props.getProperty(prefix + "vazao.entrada", "10.0")),
                        Double.parseDouble(props.getProperty(prefix + "vazao.saida", "9.5")),
                        Double.parseDouble(props.getProperty(prefix + "diametro.entrada", "25.0")),
                        Double.parseDouble(props.getProperty(prefix + "diametro.saida", "20.0")),
                        Integer.parseInt(props.getProperty(prefix + "chance.falta.agua", "5")),
                        Integer.parseInt(props.getProperty(prefix + "tempo.simulacao", "30")),
                        Integer.parseInt(props.getProperty(prefix + "tempo.atualizacao", "100")),
                        Double.parseDouble(props.getProperty(prefix + "precisao.medidor", "0.95")),
                        TipoFluido.valueOf(props.getProperty(prefix + "tipo.fluido", "AGUA")),
                        Boolean.parseBoolean(props.getProperty(prefix + "modo.debug", "false")));

                configuracoes.add(config);
            }

        } catch (FileNotFoundException e) {
            criarArquivoConfiguracao(arquivo);
            throw new IOException("Arquivo de configuração não encontrado. Criado arquivo padrão: " + arquivo);
        }
    }

    private void criarArquivoConfiguracao(String arquivo) {
        Properties props = new Properties();

        // Configuração padrão para um hidrômetro
        props.setProperty("numero.hidrometros", "2");

        // Configuração para hidrômetro 1
        props.setProperty("hidrometro1.vazao.entrada", "10.0");
        props.setProperty("hidrometro1.vazao.saida", "9.5");
        props.setProperty("hidrometro1.chance.falta.agua", "5");
        props.setProperty("hidrometro1.diametro.entrada", "25.0");
        props.setProperty("hidrometro1.diametro.saida", "20.0");
        props.setProperty("hidrometro1.tempo.simulacao", "30");
        props.setProperty("hidrometro1.tempo.atualizacao", "100");
        props.setProperty("hidrometro1.precisao.medidor", "0.95");
        props.setProperty("hidrometro1.tipo.fluido", "AGUA");
        props.setProperty("hidrometro1.modo.debug", "false");

        props.setProperty("hidrometro2.vazao.entrada", "15.0");
        props.setProperty("hidrometro2.vazao.saida", "14.2");
        props.setProperty("hidrometro2.chance.falta.agua", "10");
        props.setProperty("hidrometro2.diametro.entrada", "30.0");
        props.setProperty("hidrometro2.diametro.saida", "25.0");
        props.setProperty("hidrometro2.tempo.simulacao", "30");
        props.setProperty("hidrometro2.tempo.atualizacao", "150");
        props.setProperty("hidrometro2.precisao.medidor", "0.90");
        props.setProperty("hidrometro2.tipo.fluido", "AGUA");
        props.setProperty("hidrometro2.modo.debug", "true");

        try (OutputStream output = new FileOutputStream(arquivo)) {
            props.store(output, "Configuração do Simulador de Hidrômetro - Múltiplos Hidrômetros");
        } catch (IOException e) {
            System.err.println("Erro ao criar arquivo de configuração: " + e.getMessage());
        }
    }

    public boolean validarConfiguracao() {
        return configuracoes.stream().allMatch(ConfiguracaoDTO::isValida);
    }

    // Métodos para acessar as configurações dos hidrômetros
    public List<ConfiguracaoDTO> getConfiguracoes() {
        return new ArrayList<>(configuracoes);
    }

    public ConfiguracaoDTO getConfiguracao(int indice) {
        if (indice < 0 || indice >= configuracoes.size()) {
            throw new IndexOutOfBoundsException("Índice inválido: " + indice);
        }
        return configuracoes.get(indice);
    }

    public int getNumeroHidrometros() {
        return configuracoes.size();
    }

    public ConfiguracaoDTO getConfig() {
        return configuracoes.isEmpty() ? new ConfiguracaoDTO() : configuracoes.get(0);
    }

    public double getVazaoEntrada() {
        return getConfig().vazaoEntrada();
    }

    public double getVazaoSaida() {
        return getConfig().vazaoSaida();
    }

    public int getChanceFaltaAgua() {
        return getConfig().chanceFaltaAgua();
    }

    public double getDiametroEntrada() {
        return getConfig().diametroEntrada();
    }

    public double getDiametroSaida() {
        return getConfig().diametroSaida();
    }

    public int getTempoSimulacao() {
        return getConfig().tempoSimulacao();
    }

    public int getTempoAtualizacao() {
        return getConfig().tempoAtualizacao();
    }

    public double getPrecisaoMedidor() {
        return getConfig().precisaoMedidor();
    }

    public TipoFluido getTipoFluido() {
        return getConfig().tipoFluido();
    }

    public boolean isModoDebug() {
        return getConfig().modoDebug();
    }
}
