package com.hidrometro.imagem;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImagemHidrometro {
    private final int largura;
    private final int altura;
    private final String diretorio;
    private final String formato;

    public ImagemHidrometro(int largura, int altura, String diretorio, String formato) {
        this.largura = largura;
        this.altura = altura;
        this.diretorio = diretorio;
        this.formato = formato;
        criarDiretorio();
    }

    private void criarDiretorio() {
        File dir = new File(diretorio);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public void gerarImagem(int idHidrometro, double volumeAtual, double vazaoEntrada, 
                           double vazaoSaida, String tipoFluido, boolean temAgua) {
        BufferedImage imagem = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = imagem.createGraphics();
        
        // Anti-aliasing para melhor qualidade
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Fundo branco
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, largura, altura);
        
        // Desenhar hidrômetro
        desenharHidrometro(g2d, idHidrometro, volumeAtual, vazaoEntrada, vazaoSaida, tipoFluido, temAgua);
        
        g2d.dispose();
        
        // Salvar imagem
        salvarImagem(imagem, idHidrometro);
    }

    private void desenharHidrometro(Graphics2D g2d, int idHidrometro, double volumeAtual,
                                   double vazaoEntrada, double vazaoSaida, String tipoFluido, boolean temAgua) {
        // Título
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.drawString("Hidrômetro #" + idHidrometro, 50, 50);
        
        // Status
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        g2d.drawString("Status: " + (temAgua ? "ATIVO" : "SEM ÁGUA"), 50, 80);
        g2d.drawString("Tipo: " + tipoFluido, 50, 105);
        g2d.drawString("Timestamp: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()), 50, 130);
        
        // Corpo do hidrômetro (círculo)
        int centroX = largura / 2;
        int centroY = altura / 2;
        int raio = 150;
        
        // Borda externa
        g2d.setColor(Color.DARK_GRAY);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawOval(centroX - raio, centroY - raio, raio * 2, raio * 2);
        
        // Fundo do mostrador
        g2d.setColor(temAgua ? new Color(230, 240, 255) : new Color(255, 230, 230));
        g2d.fillOval(centroX - raio + 5, centroY - raio + 5, raio * 2 - 10, raio * 2 - 10);
        
        // Volume no centro
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 32));
        String volumeStr = String.format("%.2f L", volumeAtual);
        FontMetrics fm = g2d.getFontMetrics();
        int textoX = centroX - fm.stringWidth(volumeStr) / 2;
        g2d.drawString(volumeStr, textoX, centroY);
        
        // Vazões
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        g2d.setColor(new Color(0, 150, 0));
        String entradaStr = String.format("↓ %.2f L/s", vazaoEntrada);
        g2d.drawString(entradaStr, centroX - 80, centroY - 70);
        
        g2d.setColor(new Color(200, 0, 0));
        String saidaStr = String.format("↑ %.2f L/s", vazaoSaida);
        g2d.drawString(saidaStr, centroX - 80, centroY + 90);
        
        // Indicador visual de fluxo
        desenharIndicadorFluxo(g2d, centroX, centroY, vazaoEntrada, vazaoSaida, temAgua);
        
        // Legenda inferior
        g2d.setColor(Color.GRAY);
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString("Simulação de Hidrômetro - Sistema de Monitoramento", 50, altura - 30);
    }

    private void desenharIndicadorFluxo(Graphics2D g2d, int centroX, int centroY, 
                                       double vazaoEntrada, double vazaoSaida, boolean temAgua) {
        if (!temAgua) return;
        
        double diferenca = vazaoEntrada - vazaoSaida;
        int barraLargura = 30;
        int barraAltura = 100;
        int barraX = centroX + 180;
        int barraY = centroY - barraAltura / 2;
        
        // Borda da barra
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawRect(barraX, barraY, barraLargura, barraAltura);
        
        // Preenchimento baseado na diferença de vazão
        int preenchimento = (int) Math.min(barraAltura, Math.abs(diferenca) * 10);
        Color cor = diferenca > 0 ? new Color(100, 200, 100) : new Color(200, 100, 100);
        g2d.setColor(cor);
        g2d.fillRect(barraX + 2, barraY + barraAltura - preenchimento - 2, barraLargura - 4, preenchimento);
        
        // Etiqueta
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        g2d.drawString("Fluxo", barraX - 10, barraY - 10);
    }

    private void salvarImagem(BufferedImage imagem, int idHidrometro) {
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String nomeArquivo = String.format("%s/hidrometro_%d_%s.%s", 
                                              diretorio, idHidrometro, timestamp, formato.toLowerCase());
            File arquivo = new File(nomeArquivo);
            ImageIO.write(imagem, formato, arquivo);
            System.out.println("Imagem salva: " + nomeArquivo);
        } catch (IOException e) {
            System.err.println("Erro ao salvar imagem: " + e.getMessage());
        }
    }
}
