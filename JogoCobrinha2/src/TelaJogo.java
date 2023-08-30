import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Time;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Random;

import static java.awt.event.KeyEvent.*;
import static java.awt.event.KeyEvent.VK_S;

public class TelaJogo extends JPanel implements ActionListener {




    private static final int LARGURA_TELA = 1300; //--
    private static final int ALTURA_TELA = 750; //--
    private static final int TAMANHO_BLOCO = 20; //--
    private static final int VELOCIDADE = 200; //--
    private static final String NOME_FONTE = "Ink Free";
    private int X_COBRA = LARGURA_TELA / 2; //--
    private int Y_COBRA = ALTURA_TELA / 2; //--
    private int X_SEGUNDO = VELOCIDADE; //--
    private int Y_SEGUNDO = 0; //--
    private int X_MACA; //--
    private int Y_MACA; //--
    private  ArrayList listSnake = new ArrayList(); //--
    private int corpoCobra = 6; //--
    private int blocosComidos ; //--
    private char direcao = 'D'; //--
    private boolean perdeu = false; //--
    JButton botao;
    Timer timer; //--
    Random random; //--

    TelaJogo(){
        random = new Random();
        setPreferredSize(new Dimension(LARGURA_TELA, ALTURA_TELA));
        setBackground(Color.DARK_GRAY);
        setFocusable(true);
        addKeyListener(new LeitorDeTeclasAdapter());

        botao = new JButton("Novo Jogo");

        botao.setBounds(650, 650, 100, 50);
        botao.addActionListener(e);

        iniciarJogo();
    }

    public void desenhandoCobra(Graphics g){

        for (int i = 0; i < corpoCobra; i++){
            if (i == 0){
                g.setColor(Color.green);
                g.fillRect(X_COBRA, Y_COBRA, TAMANHO_BLOCO, TAMANHO_BLOCO);

            } else {
                g.setColor(new Color(45, 180, 0));
                g.fillRect(eixoX[i], eixoY[i], TAMANHO_BLOCO, TAMANHO_BLOCO);
            }
        }

    }

    public void desenhandoBolinha(Graphics g){
        g.setColor(Color.red);
        g.fillOval(blocoX, blocoY, TAMANHO_BLOCO, TAMANHO_BLOCO);
    }

    public void desenhandoEscritaPontos (Graphics g){
        g.setColor(Color.red);
        g.setFont(new Font(NOME_FONTE, Font.BOLD, 40));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Pontos: " + blocosComidos, (LARGURA_TELA - metrics.stringWidth("Pontos: " + blocosComidos)) / 2, g.getFont().getSize());
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        desenharTela(g);
    }

    public void desenharTela(Graphics g){

        if (estaRodando){

            desenhandoCobra(g);
            desenhandoBolinha(g);
            desenhandoEscritaPontos(g);

        } else {

            fimDeJogo(g);
        }

    }

    public void iniciarJogo(){
        criarBloco();
        estaRodando = true;
        timer = new Timer(INTERVALO, this);
        timer.start();
    }

    private void criarBloco(){
        X_MACA = random.nextInt(LARGURA_TELA / TAMANHO_BLOCO) * TAMANHO_BLOCO;
        Y_MACA = random.nextInt(ALTURA_TELA / TAMANHO_BLOCO) * TAMANHO_BLOCO;
    }

    public void fimDeJogo(Graphics g){

        g.setColor(Color.red);
        g.setFont(new Font(NOME_FONTE, Font.BOLD, 40));
        FontMetrics fontePontuacao = getFontMetrics(g.getFont());
        g.drawString("Pontos: " + blocosComidos, (LARGURA_TELA - fontePontuacao.stringWidth("Pontos: " + blocosComidos)) / 2, g.getFont().getSize());
        g.setColor(Color.red);
        g.setFont(new Font(NOME_FONTE, Font.BOLD, 75));
        FontMetrics fonteFinal = getFontMetrics(g.getFont());
        g.drawString("\uD83D\uDE00 Fim do Jogo", (LARGURA_TELA - fonteFinal.stringWidth("Fim do Jogo")) / 2, ALTURA_TELA / 2);

        if (!estaRodando){

            add(botao);
        }

    }

    ActionListener e = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

            blocosComidos = 0;
            corpoCobra = 6;

            iniciarJogo();

            repaint();
        }
    };


    @Override
    public void actionPerformed(ActionEvent e) {

        if (estaRodando){
            andar();
            alcancarBloco();
            validarLimites();
        }
        repaint();

    }

    private void validarLimites() {

        //cabeça bateu no corpo
        for (int i = corpoCobra; i > 0; i--){
            if (eixoX[0] == eixoX[i] && eixoY[0] == eixoY[i]){
                estaRodando = false;
                break;
            }
        }

        //A cabeça tocou uma das bordas Direita ou esquerda
        if (eixoX[0] < 0 || eixoX[0] > LARGURA_TELA){
            estaRodando = false;
        }

        //A cabeça tocou no topo ou embaixo
        if (eixoY[0] < 0 || eixoY[0] > ALTURA_TELA){
            estaRodando = false;
        }

        if (!estaRodando){
            timer.stop();
        }

    }

    private void alcancarBloco() {

        if (eixoX[0] == blocoX && eixoY[0] == blocoY){
            corpoCobra++;
            blocosComidos++;
            criarBloco();
        }

    }

    private void andar() {

        for (int i = corpoCobra; i > 0; i--){

            eixoX[i] = eixoX[i - 1];
            eixoY[i] = eixoY[i - 1];
        }

        switch (direcao){

            case 'W':
                eixoY[0] = eixoY[0] - TAMANHO_BLOCO;
                break;
            case 'S':
                eixoY[0] = eixoY[0] + TAMANHO_BLOCO;
                break;
            case 'A':
                eixoX[0] = eixoX[0] - TAMANHO_BLOCO;
                break;
            case 'D':
                eixoX[0] = eixoX[0] + TAMANHO_BLOCO;
                break;

            default:
                break;

        }

    }


    private class LeitorDeTeclasAdapter implements KeyListener {

        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()){
                case VK_LEFT:
                    if (direcao != 'D'){
                        direcao = 'A';
                    }
                    break;
                case VK_RIGHT:
                    if (direcao != 'A'){
                        direcao = 'D';
                    }
                    break;
                case VK_UP:
                    if (direcao != 'S'){
                        direcao = 'W';
                    }
                    break;
                case VK_DOWN:
                    if (direcao != 'W'){
                        direcao = 'S';
                    }
                    break;

                default:
                    break;
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    }
}
