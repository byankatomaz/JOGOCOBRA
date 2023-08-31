import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

public class TelaJogo extends JPanel implements ActionListener {



    private static final int LARGURA_TELA = 1300;
    private static final int ALTURA_TELA = 750;
    private static final int TAMANHO_BLOCO = 50;
    private static final int INTERVALO = 200;
    private static final String NOME_FONTE = "Ink Free";

    private static final int UNIDADES = LARGURA_TELA * ALTURA_TELA / (TAMANHO_BLOCO * TAMANHO_BLOCO);
    private static final Node[] SNAKE = new Node[UNIDADES];
    private int corpoCobra = 6;
    private int blocosComidos;

    private char direcao = 'D';

    protected boolean estaRodando = false;

    private int blocoX;
    private int blocoY;

    Random random;
    JButton botaoNovoJogo;
    Timer timer;


    public TelaJogo(){
        random = new Random();
        setPreferredSize(new Dimension(LARGURA_TELA, ALTURA_TELA));
        addKeyListener(new LeitorDeTeclasAdapter());
        setBackground(Color.DARK_GRAY);
        posicionandoCobra();
        setFocusable(true);


        botaoNovoJogo = new JButton("Novo Jogo");

        botaoNovoJogo.setBounds(475, 650, 150, 50);
        botaoNovoJogo.addActionListener(e);


        iniciarJogo();
    }


    public void iniciarJogo(){
        criarBloco();
        estaRodando = true;
        timer = new Timer(INTERVALO, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        desenharTela(g);
    }

    public void desenharCobra(Graphics g){
        for (int i = 0; i < corpoCobra; i++){
            if (i == 0){
                g.setColor(Color.green);
                g.fillRect(SNAKE[0].x, SNAKE[0].y, TAMANHO_BLOCO, TAMANHO_BLOCO);
            } else {
                g.setColor(new Color(45, 180, 0));
                g.fillRect(SNAKE[i].x, SNAKE[i].y, TAMANHO_BLOCO, TAMANHO_BLOCO);
            }
        }
    }

    public void desenharAnuncioP(Graphics g){

        g.setColor(Color.red);
        g.setFont(new Font(NOME_FONTE, Font.BOLD, 40));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Pontos: " + blocosComidos, (LARGURA_TELA - metrics.stringWidth("Pontos: " + blocosComidos)) / 2, g.getFont().getSize());

    }

    public void desenharComida(Graphics g){
        g.setColor(Color.red);
        g.fillOval(blocoX, blocoY, TAMANHO_BLOCO, TAMANHO_BLOCO);
    }


    public void criarBloco(){
        blocoX = random.nextInt(LARGURA_TELA / TAMANHO_BLOCO) * TAMANHO_BLOCO;
        blocoY = random.nextInt(ALTURA_TELA / TAMANHO_BLOCO) * TAMANHO_BLOCO;
    }

    public void posicionandoCobra(){
        for (int i = 0; i < SNAKE.length; i++) {
            SNAKE[i] = new Node(500,300);
        }
    }

    public void desenharTela(Graphics g){

        if (estaRodando){

            desenharCobra(g);
            desenharComida(g);
            desenharAnuncioP(g);

        } else {
            fimDeJogo(g);
        }

    }

    public void alcancarBloco() {

        if (SNAKE[0].x == blocoX && SNAKE[0].y == blocoY){
            corpoCobra++;
            blocosComidos++;
            criarBloco();
        }

    }

    public void validarLimites() {

        //cabeça bateu no corpo
        for (int i = corpoCobra; i > 0; i--){
            if (SNAKE[0].x == SNAKE[i].x && SNAKE[0].y == SNAKE[i].y){
                estaRodando = false;
                break;
            }
        }

        //A cabeça tocou uma das bordas Direita ou esquerda
        if (SNAKE[0].x < 0 || SNAKE[0].x > LARGURA_TELA){
            estaRodando = false;
        }

        //A cabeça tocou no topo ou embaixo
        if (SNAKE[0].y < 0 || SNAKE[0].y > ALTURA_TELA){
            estaRodando = false;
        }

        if (!estaRodando){
            timer.stop();
        }

    }

    private void andar() {

        for (int i = corpoCobra; i > 0; i--){
            SNAKE[i].x = SNAKE[i - 1].x;
            SNAKE[i].y = SNAKE[i - 1].y;
        }

        switch (direcao){

            case 'W':
                SNAKE[0].y = SNAKE[0].y - TAMANHO_BLOCO;
                break;
            case 'S':
                SNAKE[0].y = SNAKE[0].y + TAMANHO_BLOCO;
                break;
            case 'A':
                SNAKE[0].x = SNAKE[0].x - TAMANHO_BLOCO;
                break;
            case 'D':
                SNAKE[0].x = SNAKE[0].x + TAMANHO_BLOCO;
                break;

            default:
                break;

        }

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
            add(botaoNovoJogo);
        }

    }

    ActionListener e = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

            estaRodando = true;
            blocosComidos = 0;
            corpoCobra = 6;
            posicionandoCobra();
            remove(botaoNovoJogo);
            validarLimites();
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

    private class LeitorDeTeclasAdapter implements KeyListener {

        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()){
                case KeyEvent.VK_LEFT:
                    if (direcao != 'D'){
                        direcao = 'A';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direcao != 'A'){
                        direcao = 'D';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direcao != 'S'){
                        direcao = 'W';
                    }
                    break;
                case KeyEvent.VK_DOWN:
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
