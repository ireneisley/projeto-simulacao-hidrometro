package hidrometro;

import java.util.Scanner;

/**
 * Cliente CLI (Command Line Interface) para operação do Sistema SHA
 * através da Fachada Singleton
 */
public class ClienteCLI {
    
    private static final String SEPARADOR = "========================================";
    private static final String TITULO = "Sistema SHA - Command Line Interface";
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        HidrometroFachada fachada = HidrometroFachada.getInstancia();
        
        exibirBanner();
        
        boolean executando = true;
        
        while (executando) {
            exibirMenu();
            
            System.out.print("\nEscolha uma opção: ");
            String opcao = scanner.nextLine().trim();
            
            System.out.println();
            
            switch (opcao) {
                case "1":
                    configurarSimulador(scanner, fachada);
                    break;
                    
                case "2":
                    criarSHA(scanner, fachada);
                    break;
                    
                case "3":
                    finalizarSHA(scanner, fachada);
                    break;
                    
                case "4":
                    modificarVazaoSHA(scanner, fachada);
                    break;
                    
                case "5":
                    habilitarGeracaoImagem(scanner, fachada);
                    break;
                    
                default:
                    System.out.println("Opção inválida! Tente novamente.");
            }
            
            if (executando) {
                aguardarContinuacao(scanner);
            }
        }
        
        scanner.close();
        System.out.println("\n" + SEPARADOR);
        System.out.println("Sistema SHA encerrado. Até logo!");
        System.out.println(SEPARADOR);
    }
    
    private static void exibirBanner() {
        System.out.println("\n" + SEPARADOR);
        System.out.println("    " + TITULO);
        System.out.println(SEPARADOR);
        System.out.println("  Controle total do SHA via CLI");
        System.out.println(SEPARADOR + "\n");
    }
    
    private static void exibirMenu() {
        System.out.println(SEPARADOR);
        System.out.println("              MENU PRINCIPAL");
        System.out.println(SEPARADOR);
        System.out.println("1) Configurar Simulador SHA");
        System.out.println("2) Criar SHA");
        System.out.println("3) Finalizar SHA");
        System.out.println("4) Modificar Vazão SHA");
        System.out.println("5) Habilitar/Desabilitar Geração de Imagens");
        System.out.println(SEPARADOR);
    }
    
    private static void configurarSimulador(Scanner scanner, HidrometroFachada fachada) {
        System.out.println(SEPARADOR);
        System.out.println("  CONFIGURAR SIMULADOR SHA");
        System.out.println(SEPARADOR);
        
        System.out.print("Intervalo passo simulacao (ms) [padrao: 1000]: ");
        String intervaloStr = scanner.nextLine().trim();
        int intervalo = intervaloStr.isEmpty() ? 1000 : Integer.parseInt(intervaloStr);
        
        System.out.print("Gerar imagens? (S/N) [padrao: N]: ");
        String gerarStr = scanner.nextLine().trim().toUpperCase();
        boolean gerar = gerarStr.equals("S");
        
        int intervaloImg = 5;
        String diretorio = "./imagens_hidrometros";
        
        if (gerar) {
            System.out.print("Intervalo geracao imagens (s) [padrao: 5]: ");
            String imgStr = scanner.nextLine().trim();
            intervaloImg = imgStr.isEmpty() ? 5 : Integer.parseInt(imgStr);
            
            System.out.print("Diretorio imagens [padrao: ./imagens_hidrometros]: ");
            String dirStr = scanner.nextLine().trim();
            if (!dirStr.isEmpty()) {
                diretorio = dirStr;
            }
        }
        
        fachada.configSimuladorSHA(intervalo, gerar, intervaloImg, diretorio, 800, 600, "PNG");
    }
    
    private static void criarSHA(Scanner scanner, HidrometroFachada fachada) {
        System.out.println(SEPARADOR);
        System.out.println("  CRIAR SHA");
        System.out.println(SEPARADOR);
        
        if (!fachada.isSistemaAtivo()) {
            System.out.println("Sistema nao configurado!");
            System.out.println("Configurando com valores padrao...");
            fachada.configSimuladorSHA(1000, false, 5, "./imagens_hidrometros", 800, 600, "PNG");
        }
        
        System.out.print("ID do SHA (Enter para auto-gerar): ");
        String id = scanner.nextLine().trim();
        
        System.out.print("Vazão de entrada (L/min) [padrão: 10.0]: ");
        String entradaStr = scanner.nextLine().trim();
        double vazaoEntrada = entradaStr.isEmpty() ? 10.0 : Double.parseDouble(entradaStr);
        
        System.out.print("Vazão de saída (L/min) [padrão: 9.5]: ");
        String saidaStr = scanner.nextLine().trim();
        double vazaoSaida = saidaStr.isEmpty() ? 9.5 : Double.parseDouble(saidaStr);
        
        System.out.print("Tipo de fluido (AGUA/AR) [padrão: AGUA]: ");
        String tipoFluido = scanner.nextLine().trim().toUpperCase();
        if (!tipoFluido.equals("AR")) {
            tipoFluido = "AGUA";
        }
        
        System.out.print("Exibir display visual? (S/N) [padrão: S]: ");
        String displayStr = scanner.nextLine().trim().toUpperCase();
        boolean exibirDisplay = !displayStr.equals("N");
        
        String idCriado = fachada.criaSHA(
            id.isEmpty() ? null : id,
            vazaoEntrada,
            vazaoSaida,
            tipoFluido,
            exibirDisplay
        );
        
        if (idCriado != null) {
            System.out.println("\nSHA criado com sucesso! ID: " + idCriado);
        }
    }

    private static void finalizarSHA(Scanner scanner, HidrometroFachada fachada) {
        System.out.println(SEPARADOR);
        System.out.println("  FINALIZAR SHA");
        System.out.println(SEPARADOR);
        
        if (fachada.getNumeroSHAsAtivos() == 0) {
            System.out.println("Nenhum SHA ativo no momento.");
            return;
        }
        
        System.out.print("\nID do SHA a finalizar: ");
        String id = scanner.nextLine().trim();
        
        System.out.print("Confirmar finalização de " + id + "? (S/N): ");
        String confirmacao = scanner.nextLine().trim().toUpperCase();
        
        if (confirmacao.equals("S")) {
            if (fachada.finalizaSHA(id)) {
                System.out.println("\nSHA finalizado com sucesso!");
            }
        } else {
            System.out.println("Operação cancelada.");
        }
    }

    private static void modificarVazaoSHA(Scanner scanner, HidrometroFachada fachada) {
        System.out.println(SEPARADOR);
        System.out.println("  MODIFICAR VAZÃO SHA");
        System.out.println(SEPARADOR);
        
        if (fachada.getNumeroSHAsAtivos() == 0) {
            System.out.println("Nenhum SHA ativo no momento.");
            return;
        }
        
        System.out.print("\nID do SHA: ");
        String id = scanner.nextLine().trim();
        
        System.out.print("Nova vazão de entrada (L/min) [Enter para não alterar]: ");
        String entradaStr = scanner.nextLine().trim();
        double vazaoEntrada = entradaStr.isEmpty() ? -1 : Double.parseDouble(entradaStr);
        
        System.out.print("Nova vazão de saída (L/min) [Enter para não alterar]: ");
        String saidaStr = scanner.nextLine().trim();
        double vazaoSaida = saidaStr.isEmpty() ? -1 : Double.parseDouble(saidaStr);
        
        if (fachada.modificaVazaoSHA(id, vazaoEntrada, vazaoSaida)) {
            System.out.println("\nVazão modificada!");
        }
    }
    
    private static void habilitarGeracaoImagem(Scanner scanner, HidrometroFachada fachada) {
        System.out.println(SEPARADOR);
        System.out.println("  GERAÇÃO DE IMAGENS SHA");
        System.out.println(SEPARADOR);
        
        if (fachada.getNumeroSHAsAtivos() == 0) {
            System.out.println("Nenhum SHA ativo no momento.");
            return;
        }
        
        System.out.print("\nID do SHA: ");
        String id = scanner.nextLine().trim();
        
        System.out.print("Habilitar geração de imagens? (S/N): ");
        String habilitarStr = scanner.nextLine().trim().toUpperCase();
        boolean habilitar = habilitarStr.equals("S");
        
        int intervalo = -1;
        if (habilitar) {
            System.out.print("Intervalo em segundos [Enter para padrão]: ");
            String intervaloStr = scanner.nextLine().trim();
            if (!intervaloStr.isEmpty()) {
                intervalo = Integer.parseInt(intervaloStr);
            }
        }
        
        if (fachada.habilitaGeracaoImagemSHA(id, habilitar, intervalo)) {
            System.out.println("\nConfiguração de imagens atualizada!");
        }
    }
    
    private static void aguardarContinuacao(Scanner scanner) {
        System.out.print("\nPressione ENTER para continuar...");
        scanner.nextLine();
    }
}
