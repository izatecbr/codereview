package sis.pousada;

import sis.pousada.utilidade.FabricaEntityManager;
import sis.pousada.visao.FrmPrincipal;
import sis.pousada.visao.FrmSplash;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(FabricaEntityManager::finalizar));

        SwingUtilities.invokeLater(() -> {
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
}
