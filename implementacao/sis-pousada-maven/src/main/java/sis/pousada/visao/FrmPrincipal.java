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
        itemCliente.addActionListener(e -> abrirCadastroCliente());
        menuCadastros.add(itemCliente);

        menuBar.add(menuCadastros);
        return menuBar;
    }

    private void abrirCadastroCliente() {
        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            if (frame instanceof FrmCadastroCliente) {
                trazerParaFrente(frame);
                return;
            }
        }

        JInternalFrame frame = new FrmCadastroCliente();
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FrmPrincipal().setVisible(true));
    }
}
