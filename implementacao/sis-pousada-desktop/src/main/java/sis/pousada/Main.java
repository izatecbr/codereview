package sis.pousada;

import sis.pousada.utilidade.FabricaEntityManager;
import sis.pousada.visao.FrmSplash;
import sis.pousada.visao.FrmPrincipal;
import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(FabricaEntityManager::finalizar));

        SwingUtilities.invokeLater(() -> {
            aplicarLookAndFeel();

            FrmSplash splash = new FrmSplash();
            splash.setVisible(true);

            SwingWorker<Void, Void> inicializacao = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    FabricaEntityManager.inicializar();
                    return null;
                }

                @Override
                protected void done() {
                    splash.dispose();
                    new FrmPrincipal().setVisible(true);
                }
            };
            inicializacao.execute();
        });
    }

    private static void aplicarLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Não foi possível aplicar o look and feel do sistema: " + e.getMessage());
        }
    }
}
