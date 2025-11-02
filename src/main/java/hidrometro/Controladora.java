package hidrometro;

import javax.swing.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.ArrayList;

/**
 * Classe principal que orquestra a simulação de múltiplos hidrômetros
 */
public class Controladora {
    private Configuracao configuracao;
    private List<Hidrometro> hidrometros;
    private boolean simulacaoAtiva;

    private ScheduledExecutorService scheduler;
    private ScheduledExecutorService schedulerEventos;
    private ScheduledExecutorService schedulerImagens;

    private List<JFrame> frames;

    public Controladora() {
        this.configuracao = new Configuracao();
        this.hidrometros = new ArrayList<>();
        this.frames = new ArrayList<>();
        this.simulacaoAtiva = false;
        this.scheduler = Executors.newScheduledThreadPool(10);
        this.schedulerEventos = Executors.newScheduledThreadPool(5);
        this.schedulerImagens = Executors.newScheduledThreadPool(5);
    }

    public void carregarConfiguracao(String arquivo) {
        try {
            configuracao.carregarDeArquivo(arquivo);
            if (!configuracao.validarConfiguracao()) {
                throw new IllegalArgumentException("Configuração inválida");
            }
            System.out.println("Configuração carregada com sucesso de: " + arquivo);

            if (configuracao.isModoDebug()) {
                System.out.println("Modo debug ativado");
                System.out.println("Configuração: " + configuracao.getConfig());
            }

        } catch (Exception e) {
            System.out.println("Erro ao carregar configuração, usando valores padrão");
            System.out.println("Erro: " + e.getMessage());
        }
    }

    public void iniciarSimulacao() {
        if (simulacaoAtiva) {
            System.out.println("Simulação já está ativa");
            return;
        }

        // Limpar hidrômetros e frames anteriores
        hidrometros.clear();
        frames.clear();

        // Criar hidrômetros baseados na configuração
        List<ConfiguracaoDTO> configs = configuracao.getConfiguracoes();

        for (int i = 0; i < configs.size(); i++) {
            ConfiguracaoDTO config = configs.get(i);
            Hidrometro h = new Hidrometro(
                config,
                configuracao.getImagemLargura(),
                configuracao.getImagemAltura(),
                configuracao.getImagemDiretorio(),
                configuracao.getImagemFormato()
            );
            h.iniciar();
            hidrometros.add(h);

            // Criar uma janela individual para cada hidrômetro
            JFrame frame = new JFrame("Simulador " + config.id() + " - " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));

            // Configurar janela
            frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE); // Não fechar toda aplicação
            frame.add(h.getDisplay());
            frame.pack();

            // Posicionar janelas em cascade (uma ao lado da outra)
            int offset = i * 50;
            frame.setLocation(100 + offset, 100 + offset);
            frame.setVisible(true);

            frames.add(frame);
        }

        simulacaoAtiva = true;

        System.out.println("Simulação iniciada com " + hidrometros.size() + " hidrômetros em janelas separadas");

        iniciarThreadsSimulacao();
    }

    private void iniciarThreadsSimulacao() {
        // Thread de medição para cada hidrômetro
        for (int i = 0; i < hidrometros.size(); i++) {
            final int index = i;
            scheduler.scheduleAtFixedRate(() -> {
                if (simulacaoAtiva && index < hidrometros.size()) {
                    try {
                        Hidrometro h = hidrometros.get(index);
                        System.out.println("Atualizando medições do " + h.getConfig().id() + " a cada 1s");
                        double medicao = h.medir();
                        double pressao = h.calcularPressao();

                        if (h.getConfig().modoDebug()) {
                            System.out.printf("[DEBUG %s] Medição: %.2f L/min | Pressão: %.2f bar%n",
                                    h.getConfig().id(), medicao, pressao);
                        }

                    } catch (Exception e) {
                        System.err.println("Erro na medição do hidrômetro " + index + ": " + e.getMessage());
                    }
                }
            }, 0, 1, TimeUnit.SECONDS);
        }

        // Thread de atualização do display para cada hidrômetro
        for (int i = 0; i < hidrometros.size(); i++) {
            final int index = i;
            long intervaloDisplay = Math.max(1, configuracao.getTempoAtualizacao() / 1000);  // Mínimo 1 segundo
            scheduler.scheduleAtFixedRate(() -> {
                if (simulacaoAtiva && index < hidrometros.size()) {
                    Hidrometro h = hidrometros.get(index);
                    System.out.println("Mostrando Display do " + h.getConfig().id() + "!");
                    SwingUtilities.invokeLater(() -> {
                        h.getDisplay().atualizarDados();

                    });
                }
            }, 0, intervaloDisplay, TimeUnit.SECONDS);
        }

        // Thread de eventos para cada hidrômetro
        for (int i = 0; i < hidrometros.size(); i++) {
            final int index = i;
            schedulerEventos.scheduleAtFixedRate(() -> {
                if (simulacaoAtiva && index < hidrometros.size()) {
                    Hidrometro h = hidrometros.get(index);
                    // Simular falta de água baseado na configuração específica
                    if (Math.random() * 100 < h.getConfig().chanceFaltaAgua()) {
                        System.out.println("Simulando falta de água no " + h.getConfig().id() + "...");
                        h.simularFaltaAgua();
                    }
                }
            }, 2, 5, TimeUnit.SECONDS);
        }

        // Thread de geração de imagens para cada hidrômetro (se habilitado)
        if (configuracao.isGerarImagens()) {
            int intervaloImagens = configuracao.getImagemIntervaloSegundos();

            for (int i = 0; i < hidrometros.size(); i++) {
                final int index = i;
                schedulerImagens.scheduleAtFixedRate(() -> {
                    if (simulacaoAtiva && index < hidrometros.size()) {
                        try {
                            Hidrometro h = hidrometros.get(index);
                            // System.out.println("Gerando imagem do " + h.getConfig().id() + "...");
                            h.gerarImagemAtualizada();
                        } catch (Exception e) {
                            System.err.println("Erro ao gerar imagem do hidrômetro " + index + ": " + e.getMessage());
                            if (configuracao.isModoDebug()) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, 3, intervaloImagens, TimeUnit.SECONDS);  // Delay inicial de 3 segundos
            }
        } else {
            System.out.println("Geração de imagens desabilitada");
        }
    }

    public void pararSimulacao() {
        if (!simulacaoAtiva) {
            System.out.println("Simulação não está ativa");
            return;
        }

        simulacaoAtiva = false;

        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
        if (schedulerEventos != null && !schedulerEventos.isShutdown()) {
            schedulerEventos.shutdown();
        }
        if (schedulerImagens != null && !schedulerImagens.isShutdown()) {
            schedulerImagens.shutdown();
        }

        // Parar todos os hidrômetros
        for (Hidrometro h : hidrometros) {
            h.parar();
        }

        // Atualizar títulos de todas as janelas
        for (int i = 0; i < frames.size() && i < hidrometros.size(); i++) {
            final int index = i;
            SwingUtilities.invokeLater(() -> {
                JFrame frameHidrometro = frames.get(index);
                Hidrometro h = hidrometros.get(index);
                frameHidrometro.setTitle("Simulador " + h.getConfig().id() + " - " +
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) +
                        " - Simulação Encerrada");
            });
        }

        System.out.println("Simulação parada para " + hidrometros.size() + " hidrômetros");
    }

    public void executar() {
        try {
            iniciarSimulacao();

            int tempoSimulacao = configuracao.getTempoSimulacao();

            if (tempoSimulacao > 0) {
                System.out.println("Executando simulação por " + tempoSimulacao + " segundos...");

                scheduler.schedule(() -> {
                    System.out.println("Encerrando simulação...");
                    pararSimulacao();

                    if (!scheduler.isTerminated()) {
                        // Atualizar títulos de todas as janelas para indicar fim da simulação
                        for (int i = 0; i < frames.size() && i < hidrometros.size(); i++) {
                            final int index = i;
                            SwingUtilities.invokeLater(() -> {
                                JFrame frameHidrometro = frames.get(index);
                                Hidrometro h = hidrometros.get(index);
                                frameHidrometro.setTitle("Simulador " + h.getConfig().id() + " - " +
                                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) +
                                        " - Simulação Encerrada");
                            });
                        }
                    }
                }, tempoSimulacao, TimeUnit.SECONDS);

                // Manter thread principal viva
                while (simulacaoAtiva) {
                    Thread.sleep(1000);
                }
            } else {
                System.out.println("Simulação em execução contínua. Feche a janela para parar.");
                // Manter vivo indefinidamente
                while (simulacaoAtiva) {
                    Thread.sleep(1000);
                }
            }

        } catch (InterruptedException e) {
            System.out.println("Simulação interrompida");
            pararSimulacao();
        } catch (IllegalArgumentException e) {
            System.err.println("ERRO: Argumento ilegal capturado - " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("ERRO inesperado: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Simulação concluída");
    }

    public boolean isSimulacaoAtiva() {
        return simulacaoAtiva;
    }

    public List<Hidrometro> getHidrometros() {
        return new ArrayList<>(hidrometros);
    }

    public Hidrometro getHidrometro(int indice) {
        if (indice < 0 || indice >= hidrometros.size()) {
            throw new IndexOutOfBoundsException("Índice inválido: " + indice);
        }
        return hidrometros.get(indice);
    }
}
