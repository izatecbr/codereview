package sis.pousada.visao;

import javax.swing.*;
import java.awt.*;

public class FrmSplash extends JWindow {

    public FrmSplash() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JLabel titulo = new JLabel("Sistema Pousada", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 20f));
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));

        JLabel mensagem = new JLabel("Carregando, aguarde...", SwingConstants.CENTER);
        mensagem.setBorder(BorderFactory.createEmptyBorder(5, 20, 10, 20));

        JProgressBar progresso = new JProgressBar();
        progresso.setIndeterminate(true);
        progresso.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        painel.add(titulo, BorderLayout.NORTH);
        painel.add(mensagem, BorderLayout.CENTER);
        painel.add(progresso, BorderLayout.SOUTH);

        setContentPane(painel);
        setSize(320, 160);
        setLocationRelativeTo(null);
    }
}
