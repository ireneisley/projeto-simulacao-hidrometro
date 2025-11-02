package hidrometro;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

/**
 * Classe responsável por gerar e salvar imagens do hidrômetro
 */
public class GeradorImagem {
    private int largura;
    private int altura;
    private int contadorImagens;
    private float qualidadeJpeg;
    private BufferedImage imagemBase; // Adicionada imagem base do hidrômetro real
    private String diretorio;
    private String formato;
    private String idHidrometro;

    public GeradorImagem() {
        this(800, 600, "./imagens_hidrometros", "PNG", "HIDROMETRO");
    }
    
    public GeradorImagem(int largura, int altura, String diretorio, String formato, String idHidrometro) {
        this.largura = largura;
        this.altura = altura;
        this.contadorImagens = 0;
        this.qualidadeJpeg = 0.9f;
        this.diretorio = diretorio;
        this.formato = formato;
        this.idHidrometro = idHidrometro;
        criarDiretorio();
        carregarImagemBase(); // Carrega a imagem real do hidrômetro
    }
    
    private void criarDiretorio() {
        File dir = new File(diretorio);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (created) {
                System.out.println("Diretório criado: " + diretorio);
            }
        }
    }

    private void carregarImagemBase() {
        try {
            // Tenta carregar a imagem do hidrômetro real dos recursos locais
            URL imageUrl = getClass().getClassLoader().getResource("images/hidrometro-base.png");
            if (imageUrl != null) {
                imagemBase = ImageIO.read(imageUrl);
            }

            // Redimensiona para o tamanho desejado
            if (imagemBase != null) {
                Image scaledImage = imagemBase.getScaledInstance(largura, altura, Image.SCALE_SMOOTH);
                BufferedImage resizedImage = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = resizedImage.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.drawImage(scaledImage, 0, 0, null);
                g2d.dispose();
                imagemBase = resizedImage;
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar imagem base: " + e.getMessage());
        }
    }
    public BufferedImage criarImagemHidrometro(DadosHidrometro dados) {
        BufferedImage imagem = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = imagem.createGraphics();

        // Configurar renderização
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (imagemBase != null) {
            g2d.drawImage(imagemBase, 0, 0, null);
        }

        desenharDadosAtualizados(g2d, dados);

        g2d.dispose();
        contadorImagens++;

        return imagem;
    }

    private void desenharDadosAtualizados(Graphics2D g2d, DadosHidrometro dados) {

        // Volume total em m³ (display digital no topo da imagem)
        desenharDisplayDigital(g2d, dados);

        // Ponteiros rotativos (posições reais da imagem)
        desenharPonteirosReais(g2d, dados);

        // Informações de status e dados adicionais
        desenharInformacoesAdicionais(g2d, dados);
    }

    private void desenharDisplayDigital(Graphics2D g2d, DadosHidrometro dados) {
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Monospaced", Font.BOLD, 20)); // Fonte mais adequada e tamanho ajustado

        // Volume total em m³ - posicionado nas caixas do display
        String volumeStr = String.format("%08.3f", dados.getVolume() / 1000.0);

        int startX = 300; // Posição inicial das caixas (ajustada)
        int startY = 280; // Altura das caixas (ajustada)
        int boxWidth = 20; // Largura de cada caixa (ajustada)

        // Desenha fundo branco nas caixas
        g2d.setColor(Color.WHITE);
        g2d.fillRect(startX - 5, startY - 20, volumeStr.length() * boxWidth + 10, 30);

        // Desenha os dígitos
        g2d.setColor(Color.BLACK);
        for (int i = 0; i < volumeStr.length(); i++) {
            char digito = volumeStr.charAt(i);
            if (digito == '.') {
                g2d.fillOval(startX + (i * boxWidth) + 8, startY - 3, 3, 3);
            } else {
                g2d.drawString(String.valueOf(digito), startX + (i * boxWidth), startY);
            }
        }
    }

    private void desenharPonteirosReais(Graphics2D g2d, DadosHidrometro dados) {

        // Ponteiro esquerdo (vazão) - coordenadas do ponteiro real na imagem
        int ponteiroEsqX = 320; // Ajustado para o centro do ponteiro esquerdo
        int ponteiroEsqY = 420; // Ajustado para a altura correta
        double valorVazao = (dados.getVazao() % 10.0) / 10.0; // Normaliza para 0-1
        desenharPonteiroReal(g2d, ponteiroEsqX, ponteiroEsqY, valorVazao, Color.RED, 30); // Raio ajustado

        // Ponteiro direito (volume fracionário) - coordenadas do ponteiro real na imagem
        int ponteiroDirX = 480; // Ajustado para o centro do ponteiro direito
        int ponteiroDirY = 420; // Ajustado para a altura correta
        double valorVolumeFrac = ((dados.getVolume() % 1000.0) / 1000.0); // Fração de m³
        desenharPonteiroReal(g2d, ponteiroDirX, ponteiroDirY, valorVolumeFrac, Color.RED, 30); // Raio ajustado
    }

    private void desenharPonteiroReal(Graphics2D g2d, int centerX, int centerY, double valor, Color cor, int raio) {
        g2d.setColor(cor);
        g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Calcula ângulo baseado no valor (0-360 graus)
        double angulo = valor * 360.0;
        double radianos = Math.toRadians(angulo - 90); // -90 para começar no topo

        int endX = centerX + (int)(Math.cos(radianos) * raio);
        int endY = centerY + (int)(Math.sin(radianos) * raio);

        // Desenha ponteiro com formato de seta
        g2d.drawLine(centerX, centerY, endX, endY);

        // Ponta da seta
        double arrowAngle = Math.toRadians(angulo - 90 + 15);
        int arrowX1 = endX - (int)(Math.cos(arrowAngle) * 8);
        int arrowY1 = endY - (int)(Math.sin(arrowAngle) * 8);

        arrowAngle = Math.toRadians(angulo - 90 - 15);
        int arrowX2 = endX - (int)(Math.cos(arrowAngle) * 8);
        int arrowY2 = endY - (int)(Math.sin(arrowAngle) * 8);

        g2d.drawLine(endX, endY, arrowX1, arrowY1);
        g2d.drawLine(endX, endY, arrowX2, arrowY2);

        // Centro do ponteiro
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillOval(centerX - 4, centerY - 4, 8, 8);
    }

    private void desenharInformacoesAdicionais(Graphics2D g2d, DadosHidrometro dados) {

        // Painel de informações no canto superior esquerdo
        g2d.setColor(new Color(0, 0, 0, 180)); // Fundo semi-transparente
        g2d.fillRoundRect(10, 10, 200, 120, 10, 10);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("DADOS DO MEDIDOR", 20, 30);

        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString(String.format("Vazão: %.2f L/min", dados.getVazao()), 20, 50);
        g2d.drawString(String.format("Volume: %.3f m³", dados.getVolume() / 1000.0), 20, 70);
        g2d.drawString(String.format("Pressão: %.2f bar", dados.getPressao()), 20, 90);

        // Status de água com indicador visual maior
        g2d.setColor(dados.isStatusAgua() ? Color.GREEN : Color.RED);
        g2d.fillOval(20, 100, 15, 15);
        g2d.setColor(Color.WHITE);
        g2d.drawString(dados.isStatusAgua() ? "ÁGUA OK" : "SEM ÁGUA", 45, 112);

        // Timestamp no canto inferior direito
        g2d.setColor(new Color(0, 0, 0, 120));
        g2d.fillRoundRect(580, 520, 210, 70, 8, 8);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        g2d.drawString("Imagem #" + contadorImagens, 590, 540);
        g2d.drawString(new java.util.Date().toString(), 590, 555);
        g2d.drawString(String.format("Timestamp: %d", dados.getTimestamp()), 590, 570);
    }

    public void salvarImagem(BufferedImage imagem, String nome) {
        String caminhoCompleto = diretorio + File.separator + idHidrometro + "_" + nome;
        if (formato.equalsIgnoreCase("JPEG") || formato.equalsIgnoreCase("JPG")) {
            salvarImagemJPEG(imagem, caminhoCompleto);
        } else {
            salvarImagemPNG(imagem, caminhoCompleto);
        }
    }

    public void salvarImagemJPEG(BufferedImage imagem, String nome) {
        salvarImagemJPEG(imagem, nome, qualidadeJpeg);
    }

    public void salvarImagemJPEG(BufferedImage imagem, String nome, float qualidade) {
        try {
            File arquivo = new File(nome + ".jpg");

            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
            if (!writers.hasNext()) {
                throw new IllegalStateException("Nenhum writer JPEG disponível");
            }

            ImageWriter writer = writers.next();
            ImageWriteParam param = writer.getDefaultWriteParam();

            if (param.canWriteCompressed()) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(qualidade);
            }

            try (ImageOutputStream ios = ImageIO.createImageOutputStream(arquivo)) {
                writer.setOutput(ios);
                writer.write(null, new javax.imageio.IIOImage(imagem, null, null), param);
            }

            writer.dispose();

        } catch (IOException e) {
            System.err.println("Erro ao salvar imagem JPEG: " + e.getMessage());
        }
    }

    public void salvarImagemPNG(BufferedImage imagem, String nome) {
        try {
            File arquivo = new File(nome + ".png");
            ImageIO.write(imagem, "png", arquivo);
        } catch (IOException e) {
            System.err.println("Erro ao salvar imagem PNG: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setQualidadeJpeg(float qualidade) {
        this.qualidadeJpeg = Math.max(0.0f, Math.min(1.0f, qualidade));
    }

    private boolean obterFormatoSuportado(String formato) {
        String[] formatosSuportados = ImageIO.getWriterFormatNames();
        for (String f : formatosSuportados) {
            if (f.equalsIgnoreCase(formato)) {
                return true;
            }
        }
        return false;
    }
}
