package sis.pousada;

import sis.pousada.api.ApiClient;
import sis.pousada.visao.FrmPrincipal;
import sis.pousada.visao.FrmSplash;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            aplicarLookAndFeel();
            iniciar();
        });
    }

    private static void iniciar() {
        FrmSplash splash = new FrmSplash();
        splash.setVisible(true);

        SwingWorker<Void, Void> conexao = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                ApiClient.instancia().verificarConexao();
                return null;
            }

            @Override
            protected void done() {
                splash.dispose();
                try {
                    get();
                    new FrmPrincipal().setVisible(true);
                } catch (Exception e) {
                    tratarFalhaDeConexao(e.getCause() != null ? e.getCause() : e);
                }
            }
        };
        conexao.execute();
    }

    private static void tratarFalhaDeConexao(Throwable causa) {
        Object[] opcoes = {"Tentar novamente", "Sair"};
        int escolha = JOptionPane.showOptionDialog(null,
                causa.getMessage() + "\nVerifique se a API está em execução.",
                "API indisponível", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                null, opcoes, opcoes[0]);
        if (escolha == 0) {
            iniciar();
        } else {
            System.exit(0);
        }
    }

    private static void aplicarLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Não foi possível aplicar o look and feel do sistema: " + e.getMessage());
        }
    }
}
