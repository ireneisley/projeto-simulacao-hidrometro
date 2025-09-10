package hidrometro;

import javax.swing.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Classe principal que orquestra a simulação do hidrômetro
 */
public class Controladora {
    private Configuracao configuracao;
    private Hidrometro hidrometro;
    private boolean simulacaoAtiva;
    
    private ScheduledExecutorService scheduler;
    private ScheduledExecutorService schedulerEventos;
    private ScheduledExecutorService schedulerImagens;
    
    private JFrame frame;
    
    public Controladora() {
        this.configuracao = new Configuracao();
        this.simulacaoAtiva = false;
        this.scheduler = Executors.newScheduledThreadPool(2);
        this.schedulerEventos = Executors.newScheduledThreadPool(1);
        this.schedulerImagens = Executors.newScheduledThreadPool(1);
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
        
        hidrometro = new Hidrometro(configuracao.getConfig());
        hidrometro.iniciar();
        simulacaoAtiva = true;
        
        frame = new JFrame("Simulador de Hidrômetro " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(hidrometro.getDisplay());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        System.out.println("Simulação iniciada");
        
        iniciarThreadsSimulacao();
    }
    
    private void iniciarThreadsSimulacao() {
        scheduler.scheduleAtFixedRate(() -> {
            if (simulacaoAtiva) {
                try {
                    System.out.println("Atualizando medições a cada 1s");
                    double medicao = hidrometro.medir();
                    double pressao = hidrometro.calcularPressao();
                    
                    if (configuracao.isModoDebug()) {
                        System.out.printf("[DEBUG] Medição: %.2f L/min | Pressão: %.2f bar%n", 
                                        medicao, pressao);
                    }
                    
                } catch (Exception e) {
                    System.err.println("Erro na medição: " + e.getMessage());
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
        
        scheduler.scheduleAtFixedRate(() -> {
            if (simulacaoAtiva && hidrometro != null) {
                System.out.println("Mostrando Display!");
                SwingUtilities.invokeLater(() -> {
                    hidrometro.getDisplay().atualizarDados();
                    // Atualizar título da janela com timestamp atual
                    if (frame != null) {
                        frame.setTitle("Simulador de Hidrômetro " + 
                                     LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
                    }
                });
            }
        }, 0, configuracao.getTempoAtualizacao() / 1000, TimeUnit.SECONDS);
        
        // Thread de eventos (falta de água, variações)
        schedulerEventos.scheduleAtFixedRate(() -> {
            if (simulacaoAtiva) {
                // Simular falta de água baseado na configuração
                if (Math.random() * 100 < configuracao.getChanceFaltaAgua()) {
                    System.out.println("⚠️ Simulando falta de água...");
                    hidrometro.simularFaltaAgua();
                }
            }
        }, 2, 5, TimeUnit.SECONDS);
        
        schedulerImagens.scheduleAtFixedRate(() -> {
            if (simulacaoAtiva && hidrometro != null) {
                try {
                    System.out.println("📸 Gerando imagem do hidrômetro...");
                    hidrometro.gerarImagemAtualizada();
                } catch (Exception e) {
                    System.err.println("Erro ao gerar imagem: " + e.getMessage());
                }
            }
        }, 3, 3, TimeUnit.SECONDS);
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
        
        if (hidrometro != null) {
            hidrometro.parar();
        }
        
        if (frame != null) {
            SwingUtilities.invokeLater(() -> {
                frame.setTitle("Simulador de Hidrômetro " + 
                             LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + 
                             " - Simulação Encerrada");
            });
        }
        
        System.out.println("Simulação parada");
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
                        SwingUtilities.invokeLater(() -> {
                            if (frame != null) {
                                frame.setTitle("Simulador de Hidrômetro " + 
                                             LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + 
                                             " - Simulação Encerrada");
                            }
                        });
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
        } catch (IllegalArgumentException ignored) {
            // Ignorar exceções de argumentos inválidos como no simulador de referência
        }
        
        System.out.println("Simulação concluída");
    }
    
    public boolean isSimulacaoAtiva() {
        return simulacaoAtiva;
    }
    
    public Hidrometro getHidrometro() {
        return hidrometro;
    }
}
