package sis.pousada.visao;

import javax.swing.*;
import java.beans.PropertyVetoException;

public class FrmPrincipal extends JFrame {

    private final JDesktopPane desktopPane = new JDesktopPane();

    public FrmPrincipal() {
        setTitle("Sistema Pousada");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null);

        setJMenuBar(criarMenuBar());
        setContentPane(desktopPane);
    }

    private JMenuBar criarMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuCadastros = new JMenu("Cadastros");
        JMenuItem itemCliente = new JMenuItem("Cliente");
        itemCliente.addActionListener(e -> abrirConsultaCliente());
        menuCadastros.add(itemCliente);

        menuBar.add(menuCadastros);
        return menuBar;
    }

    private void abrirConsultaCliente() {
        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            if (frame instanceof FrmConsultaCliente) {
                trazerParaFrente(frame);
                return;
            }
        }

        JInternalFrame frame = new FrmConsultaCliente();
        desktopPane.add(frame);
        frame.setVisible(true);
        trazerParaFrente(frame);
    }

    private void trazerParaFrente(JInternalFrame frame) {
        try {
            if (frame.isIcon()) {
                frame.setIcon(false);
            }
            frame.setSelected(true);
        } catch (PropertyVetoException ignored) {
        }
    }
}
