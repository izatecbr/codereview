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
        JMenuItem itemAcomodacao = new JMenuItem("Acomodação");
        itemAcomodacao.addActionListener(e -> abrirConsultaAcomodacao());
        menuCadastros.add(itemCliente);
        menuCadastros.add(itemAcomodacao);

        JMenu menuHospedagem = new JMenu("Hospedagem");
        JMenuItem itemReservas = new JMenuItem("Reservas");
        itemReservas.addActionListener(e -> abrirConsultaHospedagem(true));
        JMenuItem itemHospedagens = new JMenuItem("Hospedagens");
        itemHospedagens.addActionListener(e -> abrirConsultaHospedagem(false));
        menuHospedagem.add(itemReservas);
        menuHospedagem.add(itemHospedagens);

        menuBar.add(menuCadastros);
        menuBar.add(menuHospedagem);
        return menuBar;
    }

    private void abrirConsultaCliente() {
        abrirUnica(frame -> frame instanceof FrmConsultaCliente, FrmConsultaCliente::new);
    }

    private void abrirConsultaAcomodacao() {
        abrirUnica(frame -> frame instanceof FrmConsultaAcomodacao, FrmConsultaAcomodacao::new);
    }

    private void abrirConsultaHospedagem(boolean reserva) {
        abrirUnica(frame -> frame instanceof FrmConsultaHospedagem consulta && consulta.isReserva() == reserva,
                () -> new FrmConsultaHospedagem(reserva));
    }

    private void abrirUnica(java.util.function.Predicate<JInternalFrame> jaAberta,
                            java.util.function.Supplier<JInternalFrame> fabrica) {
        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            if (jaAberta.test(frame)) {
                trazerParaFrente(frame);
                return;
            }
        }

        JInternalFrame frame = fabrica.get();
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
