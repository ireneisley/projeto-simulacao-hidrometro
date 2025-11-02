package hidrometro;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Fachada Singleton para o Sistema de Hidrômetro de Água (SHA)
 */
public class HidrometroFachada {
    
    private static HidrometroFachada instancia;
    
    private int intervaloPasso;
    private boolean geracaoImagensGlobal;
    private int intervaloImagensGlobal;
    private String diretorioImagensGlobal;
    private int larguraImagemGlobal;
    private int alturaImagemGlobal;
    private String formatoImagemGlobal;
    
    private Map<String, Hidrometro> hidrometrosAtivos;
    
    private Map<String, JFrame> janelasAtivas;
    
    private Map<String, ScheduledExecutorService> schedulersMedicao;
    private Map<String, ScheduledExecutorService> schedulersDisplay;
    private Map<String, ScheduledExecutorService> schedulersImagens;
    private Map<String, ScheduledExecutorService> schedulersEventos;
    
    private int contadorIds;
    
    private boolean sistemaAtivo;
    
    /**
     * Construtor privado para implementar Singleton
     */
    private HidrometroFachada() {
        this.hidrometrosAtivos = new HashMap<>();
        this.janelasAtivas = new HashMap<>();
        this.schedulersMedicao = new HashMap<>();
        this.schedulersDisplay = new HashMap<>();
        this.schedulersImagens = new HashMap<>();
        this.schedulersEventos = new HashMap<>();
        this.contadorIds = 1;
        this.sistemaAtivo = false;
        
        // Valores padrão
        this.intervaloPasso = 1000;
        this.geracaoImagensGlobal = false;
        this.intervaloImagensGlobal = 5;
        this.diretorioImagensGlobal = "./imagens_hidrometros";
        this.larguraImagemGlobal = 800;
        this.alturaImagemGlobal = 600;
        this.formatoImagemGlobal = "PNG";
        
        System.out.println("=== Sistema SHA (Sistema de Hidrometro de Agua) Inicializado ===");
    }
    
    /**
     * Obtém a instância única da fachada (Singleton)
     */
    public static synchronized HidrometroFachada getInstancia() {
        if (instancia == null) {
            instancia = new HidrometroFachada();
        }
        return instancia;
    }
    
    /**
     * Configura parâmetros globais do simulador SHA
     * Define os parâmetros de configuração inicial que serão utilizados para executar o SHA.
     * 
     * @param intervaloPassoSimulacao Intervalo de tempo (em milissegundos) para contar um passo de simulação
     * @param gerarImagens Habilitar ou desabilitar geração de imagens globalmente
     * @param intervaloImagensSegundos Intervalo (em segundos) para gerar imagens
     * @param diretorioImagens Diretório onde as imagens serão salvas
     * @param larguraImagem Largura das imagens geradas (em pixels)
     * @param alturaImagem Altura das imagens geradas (em pixels)
     * @param formatoImagem Formato das imagens ("PNG" ou "JPEG")
     */
    public void configSimuladorSHA(
            int intervaloPassoSimulacao,
            boolean gerarImagens,
            int intervaloImagensSegundos,
            String diretorioImagens,
            int larguraImagem,
            int alturaImagem,
            String formatoImagem) {
        
        System.out.println("\n=== Configurando Simulador SHA ===");
        
        this.intervaloPasso = intervaloPassoSimulacao;
        this.geracaoImagensGlobal = gerarImagens;
        this.intervaloImagensGlobal = intervaloImagensSegundos;
        this.diretorioImagensGlobal = diretorioImagens;
        this.larguraImagemGlobal = larguraImagem;
        this.alturaImagemGlobal = alturaImagem;
        this.formatoImagemGlobal = formatoImagem.toUpperCase();
        
        System.out.println("Configuracao do SHA definida com sucesso");
        System.out.println("  - Intervalo passo simulacao: " + intervaloPassoSimulacao + " ms");
        System.out.println("  - Geracao de imagens: " + (gerarImagens ? "HABILITADA" : "DESABILITADA"));
        System.out.println("  - Intervalo de imagens: " + intervaloImagensSegundos + "s");
        System.out.println("  - Diretorio de imagens: " + diretorioImagens);
        System.out.println("  - Dimensoes imagem: " + larguraImagem + "x" + alturaImagem);
        System.out.println("  - Formato: " + formatoImagem);
        
        sistemaAtivo = true;
    }
    
    /**
     * Cria e inicia uma instância do SHA com parâmetros específicos
     * 
     * @param id Identificador único para o hidrômetro (opcional, null para auto-gerado)
     * @param vazaoEntrada Taxa de vazão de entrada em L/min
     * @param vazaoSaida Taxa de vazão de saída em L/min
     * @param tipoFluido Tipo de fluido (AGUA ou AR)
     * @param exibirDisplay Se deve exibir a janela visual
     * @return ID do hidrômetro criado
     */
    public String criaSHA(
            String id,
            double vazaoEntrada,
            double vazaoSaida,
            String tipoFluido,
            boolean exibirDisplay) {
        
        if (!sistemaAtivo) {
            System.err.println("✗ Sistema não está configurado. Execute configSimuladorSHA() primeiro.");
            return null;
        }

        if (id == null || id.isEmpty()) {
            id = "SHA_" + contadorIds++;
        }

        if (hidrometrosAtivos.containsKey(id)) {
            System.err.println("✗ Já existe um SHA com ID: " + id);
            return null;
        }
        
        System.out.println("\n=== Criando SHA: " + id + " ===");
        
        try {
            TipoFluido tipo = tipoFluido.equalsIgnoreCase("AR") ? TipoFluido.AR : TipoFluido.AGUA;
            
            ConfiguracaoDTO config = new ConfiguracaoDTO(
                id,
                vazaoEntrada,
                vazaoSaida,
                25.0,  // diâmetro entrada padrão
                20.0,  // diâmetro saída padrão
                5,     // chance falta água
                0,     // tempo simulação (infinito)
                100,   // tempo atualização
                0.95,  // precisão medidor
                tipo,
                false  // modo debug
            );
            
            Hidrometro hidrometro = new Hidrometro(
                config,
                larguraImagemGlobal,
                alturaImagemGlobal,
                diretorioImagensGlobal,
                formatoImagemGlobal
            );
            
            hidrometro.iniciar();
            hidrometrosAtivos.put(id, hidrometro);
            
            if (exibirDisplay) {
                criarJanela(id, hidrometro);
            }
            
            iniciarThreadsHidrometro(id, hidrometro);
            
            System.out.println("✓ SHA " + id + " criado e iniciado com sucesso");
            System.out.println("  - Vazão entrada: " + vazaoEntrada + " L/min");
            System.out.println("  - Vazão saída: " + vazaoSaida + " L/min");
            System.out.println("  - Tipo fluido: " + tipo);
            System.out.println("  - Display: " + (exibirDisplay ? "VISÍVEL" : "OCULTO"));
            
            return id;
            
        } catch (Exception e) {
            System.err.println("✗ Erro ao criar SHA: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Finaliza a execução de uma instância específica do SHA
     * 
     * @param id Identificador do hidrômetro a ser finalizado
     * @return true se finalizado com sucesso, false caso contrário
     */
    public boolean finalizaSHA(String id) {
        System.out.println("\n=== Finalizando SHA: " + id + " ===");
        
        if (!hidrometrosAtivos.containsKey(id)) {
            System.err.println("✗ SHA " + id + " não encontrado");
            return false;
        }
        
        try {
            Hidrometro hidrometro = hidrometrosAtivos.get(id);
            hidrometro.parar();
            
            pararThreadsHidrometro(id);

            if (janelasAtivas.containsKey(id)) {
                JFrame frame = janelasAtivas.get(id);
                SwingUtilities.invokeLater(() -> frame.dispose());
                janelasAtivas.remove(id);
            }
            
            hidrometrosAtivos.remove(id);
            
            System.out.println("✓ SHA " + id + " finalizado com sucesso");
            return true;
            
        } catch (Exception e) {
            System.err.println("✗ Erro ao finalizar SHA: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Modifica parâmetros de vazão de uma instância SHA em execução
     * 
     * @param id Identificador do hidrômetro
     * @param novaVazaoEntrada Nova vazão de entrada em L/min (opcional, -1 para não alterar)
     * @param novaVazaoSaida Nova vazão de saída em L/min (opcional, -1 para não alterar)
     * @return true se modificado com sucesso, false caso contrário
     */
    public boolean modificaVazaoSHA(String id, double novaVazaoEntrada, double novaVazaoSaida) {
        System.out.println("\n=== Modificando Vazão do SHA: " + id + " ===");
        
        if (!hidrometrosAtivos.containsKey(id)) {
            System.err.println("✗ SHA " + id + " não encontrado");
            return false;
        }
        
        try {
            Hidrometro hidrometro = hidrometrosAtivos.get(id);
            
            // Modificar as vazões através dos novos métodos
            if (novaVazaoEntrada > 0) {
                hidrometro.setVazaoEntrada(novaVazaoEntrada);
                System.out.println("  - Nova vazão entrada: " + novaVazaoEntrada + " L/min");
            }
            
            if (novaVazaoSaida > 0) {
                hidrometro.setVazaoSaida(novaVazaoSaida);
                System.out.println("  - Nova vazão saída: " + novaVazaoSaida + " L/min");
            }
            
            System.out.println("✓ Parâmetros de vazão atualizados com sucesso!");
            
            return true;
            
        } catch (Exception e) {
            System.err.println("✗ Erro ao modificar vazão: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Habilita ou desabilita a geração de imagens para uma instância SHA
     * 
     * @param id Identificador do hidrômetro
     * @param habilitar true para habilitar, false para desabilitar
     * @param intervaloSegundos Intervalo em segundos para geração (opcional, -1 para manter atual)
     * @return true se alterado com sucesso, false caso contrário
     */
    public boolean habilitaGeracaoImagemSHA(String id, boolean habilitar, int intervaloSegundos) {
        System.out.println("\n=== " + (habilitar ? "Habilitando" : "Desabilitando") + 
                          " Geração de Imagens do SHA: " + id + " ===");
        
        if (!hidrometrosAtivos.containsKey(id)) {
            System.err.println("✗ SHA " + id + " não encontrado");
            return false;
        }
        
        try {
            if (habilitar) {
                if (schedulersImagens.containsKey(id)) {
                    ScheduledExecutorService scheduler = schedulersImagens.get(id);
                    scheduler.shutdown();
                }
                
                ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                int intervalo = intervaloSegundos > 0 ? intervaloSegundos : intervaloImagensGlobal;
                
                Hidrometro hidrometro = hidrometrosAtivos.get(id);
                
                scheduler.scheduleAtFixedRate(() -> {
                    try {
                        hidrometro.gerarImagemAtualizada();
                    } catch (Exception e) {
                        System.err.println("Erro ao gerar imagem: " + e.getMessage());
                    }
                }, 3, intervalo, TimeUnit.SECONDS);
                
                schedulersImagens.put(id, scheduler);
                
                System.out.println("Geracao de imagens HABILITADA");
                System.out.println("  - Intervalo: " + intervalo + " segundos");
                System.out.println("  - Diretorio: " + diretorioImagensGlobal);
                
            } else {
                if (schedulersImagens.containsKey(id)) {
                    ScheduledExecutorService scheduler = schedulersImagens.get(id);
                    scheduler.shutdown();
                    schedulersImagens.remove(id);
                }
                
                System.out.println("✓ Geração de imagens DESABILITADA");
            }
            
            return true;
            
        } catch (Exception e) {
            System.err.println("✗ Erro ao alterar geração de imagens: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private void criarJanela(String id, Hidrometro hidrometro) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("SHA - " + id + " - " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.add(hidrometro.getDisplay());
            frame.pack();
            
            // Posicionar janelas em cascade
            int offset = janelasAtivas.size() * 50;
            frame.setLocation(100 + offset, 100 + offset);
            frame.setVisible(true);
            
            janelasAtivas.put(id, frame);
        });
    }
    
    private void iniciarThreadsHidrometro(String id, Hidrometro hidrometro) {
        // Thread de medição (1 segundo)
        ScheduledExecutorService schedulerMedicao = Executors.newScheduledThreadPool(1);
        schedulerMedicao.scheduleAtFixedRate(() -> {
            try {
                double medicao = hidrometro.medir();
                double pressao = hidrometro.calcularPressao();
                
                if (hidrometro.getConfig().modoDebug()) {
                    System.out.printf("[DEBUG %s] Medição: %.2f L/min | Pressão: %.2f bar%n",
                                     id, medicao, pressao);
                }
            } catch (Exception e) {
                System.err.println("Erro na medição de " + id + ": " + e.getMessage());
            }
        }, 0, 1, TimeUnit.SECONDS);
        schedulersMedicao.put(id, schedulerMedicao);
        
        // Thread de atualização do display (configurável)
        ScheduledExecutorService schedulerDisplay = Executors.newScheduledThreadPool(1);
        long intervalo = Math.max(1, intervaloPasso / 1000);  // Converter ms para segundos
        schedulerDisplay.scheduleAtFixedRate(() -> {
            try {
                SwingUtilities.invokeLater(() -> {
                    hidrometro.getDisplay().atualizarDados();
                });
            } catch (Exception e) {
                System.err.println("Erro na atualização do display: " + e.getMessage());
            }
        }, 0, intervalo, TimeUnit.SECONDS);
        schedulersDisplay.put(id, schedulerDisplay);
        
        // Thread de eventos (simulação de falta de água)
        ScheduledExecutorService schedulerEventos = Executors.newScheduledThreadPool(1);
        schedulerEventos.scheduleAtFixedRate(() -> {
            try {
                if (Math.random() * 100 < hidrometro.getConfig().chanceFaltaAgua()) {
                    System.out.println("Simulando falta de água no " + id + "...");
                    hidrometro.simularFaltaAgua();
                }
            } catch (Exception e) {
                System.err.println("Erro na simulação de eventos: " + e.getMessage());
            }
        }, 2, 5, TimeUnit.SECONDS);
        schedulersEventos.put(id, schedulerEventos);
        
        // Thread de geração de imagens (se habilitado globalmente)
        if (geracaoImagensGlobal) {
            habilitaGeracaoImagemSHA(id, true, intervaloImagensGlobal);
        }
    }
    
    private void pararThreadsHidrometro(String id) {
        if (schedulersMedicao.containsKey(id)) {
            schedulersMedicao.get(id).shutdown();
            schedulersMedicao.remove(id);
        }
        
        if (schedulersDisplay.containsKey(id)) {
            schedulersDisplay.get(id).shutdown();
            schedulersDisplay.remove(id);
        }
        
        if (schedulersEventos.containsKey(id)) {
            schedulersEventos.get(id).shutdown();
            schedulersEventos.remove(id);
        }
        
        if (schedulersImagens.containsKey(id)) {
            schedulersImagens.get(id).shutdown();
            schedulersImagens.remove(id);
        }
    }
    
    /**
     * Finaliza todos os SHAs ativos e encerra o sistema
     */
    public void finalizarSistema() {
        System.out.println("\n=== Finalizando Sistema SHA ===");
        
        // Copiar IDs para evitar ConcurrentModificationException
        String[] ids = hidrometrosAtivos.keySet().toArray(new String[0]);
        
        for (String id : ids) {
            finalizaSHA(id);
        }
        
        sistemaAtivo = false;
        System.out.println("✓ Sistema SHA finalizado completamente");
    }
    
    /**
     * Lista todos os SHAs ativos
     */
    public void listarSHAsAtivos() {
        System.out.println("\n=== SHAs Ativos ===");
        
        if (hidrometrosAtivos.isEmpty()) {
            System.out.println("  Nenhum SHA ativo no momento");
            return;
        }
        
        for (Map.Entry<String, Hidrometro> entry : hidrometrosAtivos.entrySet()) {
            String id = entry.getKey();
            Hidrometro h = entry.getValue();
            ConfiguracaoDTO config = h.getConfig();
            
            System.out.println("\n  SHA: " + id);
            System.out.println("    - Vazão entrada: " + config.vazaoEntrada() + " L/min");
            System.out.println("    - Vazão saída: " + config.vazaoSaida() + " L/min");
            System.out.println("    - Tipo fluido: " + config.tipoFluido());
            System.out.println("    - Display: " + (janelasAtivas.containsKey(id) ? "VISÍVEL" : "OCULTO"));
            System.out.println("    - Geração imagens: " + (schedulersImagens.containsKey(id) ? "ATIVA" : "INATIVA"));
        }
        
        System.out.println("\n  Total: " + hidrometrosAtivos.size() + " SHA(s) ativo(s)");
    }
    
    /**
     * Verifica se o sistema está ativo
     */
    public boolean isSistemaAtivo() {
        return sistemaAtivo;
    }
    
    /**
     * Obtém número de SHAs ativos
     */
    public int getNumeroSHAsAtivos() {
        return hidrometrosAtivos.size();
    }
}
