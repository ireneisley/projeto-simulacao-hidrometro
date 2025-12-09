package hidrometro;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Display extends JPanel {
    private double vazaoExibida;
    private String volumeExibido;
    private double volumeDouble;
    private double pressaoExibida;
    private String statusConexao;
    private boolean faltaAgua;
    private BufferedImage imagemHidrometro;

    private JFrame frame;
    private JLabel labelVazao;
    private JLabel labelVolume;
    private JLabel labelPressao;
    private JLabel labelStatus;
    private JSlider sliderVazao;

    public Display() {
        this.vazaoExibida = 0.0;
        this.volumeExibido = "000000";
        this.volumeDouble = 0.0;
        this.pressaoExibida = 0.0;
        this.statusConexao = "Conectado";
        this.faltaAgua = false;

        inicializarInterface();
        carregarImagemHidrometro();
    }

    private void inicializarInterface() {
        frame = new JFrame("Simulador de Hidrômetro");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);  // Tamanho maior e fixo
        frame.setMinimumSize(new Dimension(800, 600));  // Define tamanho mínimo
        frame.setLocationRelativeTo(null);

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 600));  // Define tamanho preferido do painel

        // Painel de informações
        JPanel painelInfo = new JPanel(new GridLayout(4, 1, 25, 30));
        painelInfo.setBorder(BorderFactory.createTitledBorder("Medições"));
        painelInfo.setBackground(new Color(248, 249, 252));
        painelInfo.setOpaque(true);
        painelInfo.setPreferredSize(new Dimension(230, 0));

        Font valor = new Font("Arial", Font.BOLD, 18);

        labelVazao  = new JLabel();
        labelVolume = new JLabel();
        labelPressao= new JLabel();
        labelStatus = new JLabel();

        for (JLabel l : new JLabel[]{labelVazao, labelVolume, labelPressao, labelStatus}) {
            l.setHorizontalAlignment(SwingConstants.LEFT);
            l.setFont(valor);
            l.setOpaque(true);
            l.setBackground(Color.WHITE);
            l.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        }

        labelVazao.setToolTipText("Vazão instantânea estimada (litros por minuto).");
        labelVolume.setToolTipText("Volume acumulado desde o início da simulação (em litros).");
        labelPressao.setToolTipText("Pressão aproximada na rede (bar).");
        labelStatus.setToolTipText("Estado operacional com base em fluxo e pressão.");

        // === Slider de Vazão ===
        sliderVazao = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
        sliderVazao.setBackground(Color.WHITE);
        sliderVazao.setOpaque(true);
        sliderVazao.setBorder(BorderFactory.createEmptyBorder(0, 10, 8, 10)); 
        sliderVazao.setPaintTicks(false);
        sliderVazao.setPaintLabels(false);
        sliderVazao.setToolTipText("Ajuste manual da vazão (0.0 a 10.0 L/min).");
        sliderVazao.setValue((int)Math.round(vazaoExibida * 10));
        sliderVazao.addChangeListener(e -> {
            if (!faltaAgua) {
                double novaVazao = sliderVazao.getValue() / 10.0;
                atualizarDisplay(novaVazao, volumeDouble, pressaoExibida);
            } else {
                sliderVazao.setValue(0); // trava em 0 se faltar água
            }
        });

        // === Painel da Vazão (UM bloco com label em cima e slider embaixo) ===
        JPanel painelVazao = new JPanel(new BorderLayout());
        painelVazao.setOpaque(true);
        painelVazao.setBackground(Color.WHITE);
        painelVazao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 0, 0, 0),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        painelVazao.add(labelVazao, BorderLayout.NORTH);
        painelVazao.add(sliderVazao, BorderLayout.SOUTH);

        painelInfo.add(painelVazao);  
        painelInfo.add(labelVolume);
        painelInfo.add(labelPressao);
        painelInfo.add(labelStatus);

        add(painelInfo, BorderLayout.EAST);
        frame.add(this);

        atualizarDados();
    }

    private void carregarImagemHidrometro() {
        try {
            java.net.URL imageUrl = getClass().getClassLoader().getResource("images/hidrometro-base.png");
            if (imageUrl != null) {
                imagemHidrometro = javax.imageio.ImageIO.read(imageUrl);
            } else {
                criarImagemPadrao();
            }
        } catch (Exception e) {
            criarImagemPadrao();
        }
    }

    private void criarImagemPadrao() {
        imagemHidrometro = new BufferedImage(400, 400, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = imagemHidrometro.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, 400, 400);
        g2d.setColor(Color.DARK_GRAY);
        g2d.setStroke(new BasicStroke(4));
        g2d.drawOval(50, 50, 300, 300);
        g2d.dispose();
    }

    public void atualizarDisplay(double vazao, double volume, double pressao) {
        this.vazaoExibida = vazao;
        this.volumeDouble = volume;
        this.volumeExibido = formatarVolume(volume);
        this.pressaoExibida = pressao;

        if (vazao <= 0) {
            statusConexao = "Sem fluxo";
            faltaAgua = true;
        } else if (pressao < 0.5) {
            statusConexao = "Pressão baixa";
            faltaAgua = false;
        } else {
            statusConexao = "Normal";
            faltaAgua = false;
        }

        // mantém slider coerente
        if (!faltaAgua) {
            sliderVazao.setValue((int)Math.round(vazaoExibida * 10));
        } else {
            sliderVazao.setValue(0);
        }

        atualizarDados();
    }

    // ===== helpers de formatação das legendas =====
    private static String fmtLinha(String titulo, String emoji, String valor, String unidade, Color corValor) {
        String hex = String.format("#%02x%02x%02x", corValor.getRed(), corValor.getGreen(), corValor.getBlue());
        return String.format(
            "<html><div style='font-family: Arial;'>" +
                "<div style='font-size:12px;color:#6b7280;margin-bottom:2px;'>%s %s</div>" +
                "<div style='font-size:20px;color:%s;line-height:1;'>%s" +
                " <span style='font-size:12px;color:#6b7280;'>%s</span></div>" +
             "</div></html>", titulo, emoji, hex, valor, unidade);
    }

    private static String fmtStatus(String texto, Color cor) {
        String hex = String.format("#%02x%02x%02x", cor.getRed(), cor.getGreen(), cor.getBlue());
        return String.format(
            "<html><div style='font-family: Arial; font-size:14px;'>" +
            "<span style='color:%s;'>●</span> <b style='color:%s;'>%s</b>" +
            "</div></html>", hex, hex, texto);
    }
    // ===== fim helpers =====

    public void atualizarDados() {
        SwingUtilities.invokeLater(() -> {
            Color corVazao   = (vazaoExibida > 0) ? new Color(37, 99, 235) : new Color(107, 114, 128);
            Color corVolume  = new Color(16, 185, 129);
            Color corPressao = (pressaoExibida < 0.5) ? new Color(245, 158, 11) : new Color(34, 197, 94);

            labelVazao.setText(
                fmtLinha("Vazão", "",
                        String.format("%.2f", vazaoExibida),
                        "L/min", corVazao)
            );
            labelVolume.setText(
                fmtLinha("Volume", "",
                        volumeExibido,
                        "L", corVolume)
            );
            labelPressao.setText(
                fmtLinha("Pressão", "",
                        String.format("%.2f", pressaoExibida),
                        "bar", corPressao)
            );

            if (faltaAgua) {
                labelStatus.setText(fmtStatus("Sem fluxo", new Color(220,38,38)));
            } else if (pressaoExibida < 0.5) {
                labelStatus.setText(fmtStatus("Pressão baixa", new Color(234,88,12)));
            } else {
                labelStatus.setText(fmtStatus("Normal", new Color(22,163,74)));
            }

            repaint();
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imagemHidrometro == null) return;

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int displayWidth = getWidth() - 200; // espaço para o painel de informações
        int displayHeight = getHeight();

        double scaleX = (double) displayWidth / imagemHidrometro.getWidth();
        double scaleY = (double) displayHeight / imagemHidrometro.getHeight();
        double scale = Math.min(scaleX, scaleY) * 0.8;

        int scaledWidth = (int) (imagemHidrometro.getWidth() * scale);
        int scaledHeight = (int) (imagemHidrometro.getHeight() * scale);

        int x = (displayWidth - scaledWidth) / 2;
        int y = (displayHeight - scaledHeight) / 2;

        g2d.drawImage(imagemHidrometro, x, y, scaledWidth, scaledHeight, null);

        sobreporDadosNaImagemReal(g2d, x, y, scaledWidth, scaledHeight, scale);

        g2d.dispose();
    }

    private void sobreporDadosNaImagemReal(Graphics2D g2d, int x, int y, int width, int height, double scale) {
        int centerX = x + width / 2;
        int centerY = y + height / 2;

        int displayX = centerX - (int)(90 * scale);
        int displayY = y + (int)(246 * scale);
        int displayWidth = (int)(200 * scale);
        int displayHeight = (int)(54 * scale);

        g2d.setColor(new Color(232, 232, 232));
        g2d.fillRect(displayX, displayY, displayWidth, displayHeight);

        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, (int)(36 * scale)));
        String volumeStr = volumeExibido;
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(volumeStr);
        g2d.drawString(volumeStr, displayX + (displayWidth - textWidth) / 2, displayY + displayHeight / 2 + 16 - 10);

        int ponteiro1X = centerX - (int)(-5 * scale);
        int ponteiro1Y = centerY + (int)(146 * scale);
        int ponteiro2X = centerX + (int)(125 * scale);
        int ponteiro2Y = centerY + (int)(86 * scale);

        double anguloVazao = (vazaoExibida) * 36 - 90;
        desenharPonteiroReal(g2d, ponteiro1X, ponteiro1Y, anguloVazao, (int)(25 * scale), Color.RED, 2);

        double anguloPressao = (pressaoExibida) * 36 - 90;
        desenharPonteiroReal(g2d, ponteiro2X, ponteiro2Y, anguloPressao, (int)(25 * scale), Color.BLUE, 2);
    }

    private void desenharPonteiroReal(Graphics2D g2d, int centerX, int centerY, double angulo, int comprimento, Color cor, int espessura) {
        AffineTransform transform = g2d.getTransform();
        g2d.translate(centerX, centerY);
        g2d.rotate(Math.toRadians(angulo));
        g2d.setColor(cor);
        g2d.setStroke(new BasicStroke(espessura));
        g2d.drawLine(0, 0, comprimento, 0);
        g2d.fillPolygon(new int[]{comprimento-3, comprimento, comprimento-3},
                new int[]{-2, 0, 2}, 3);
        g2d.setTransform(transform);
        g2d.setColor(Color.BLACK);
        g2d.fillOval(centerX-2, centerY-2, 4, 4);
    }

    public void mostrarInterface() {
        SwingUtilities.invokeLater(() -> frame.setVisible(true));
    }

    public void ocultarInterface() {
        SwingUtilities.invokeLater(() -> frame.setVisible(false));
    }

    private String formatarVolume(double volume) {
        int volumeInt = (int) Math.round(volume);
        return String.format("%06d", volumeInt);
    }
}