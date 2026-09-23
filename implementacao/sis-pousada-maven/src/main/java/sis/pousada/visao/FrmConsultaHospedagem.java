package sis.pousada.visao;

import sis.pousada.modelo.hospedagem.Hospedagem;
import sis.pousada.service.HospedagemService;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Consulta de RESERVAS (reserva = true) ou de HOSPEDAGENS (reserva = false).
 */
public class FrmConsultaHospedagem extends JInternalFrame {

    private final JTextField txtHospede = new JTextField(30);
    private final HospedagemTableModel modelo = new HospedagemTableModel();
    private final JTable tabela = new JTable(modelo);

    private final HospedagemService service = new HospedagemService();
    private final boolean reserva;

    public FrmConsultaHospedagem(boolean reserva) {
        this.reserva = reserva;

        setTitle(reserva ? "Consulta de Reservas" : "Consulta de Hospedagens");
        setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        setClosable(true);
        setMaximizable(true);
        setIconifiable(true);
        setResizable(true);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscar());
        txtHospede.addActionListener(e -> buscar());

        JPanel painelNorte = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        painelNorte.add(new JLabel("Hóspede:"));
        painelNorte.add(txtHospede);
        painelNorte.add(btnBuscar);

        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane painelCentro = new JScrollPane(tabela);

        JPanel painelSul = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));

        JButton btnNovo = new JButton("Novo");
        btnNovo.addActionListener(e -> novo());
        JButton btnAlterar = new JButton("Alterar");
        btnAlterar.addActionListener(e -> alterar());
        painelSul.add(btnNovo);
        painelSul.add(btnAlterar);

        if (reserva) {
            JButton btnHospedar = new JButton("Hospedar");
            btnHospedar.addActionListener(e -> hospedar());
            painelSul.add(btnHospedar);
        }

        JButton btnFechar = new JButton("Fechar");
        btnFechar.addActionListener(e -> dispose());
        painelSul.add(btnFechar);

        setLayout(new BorderLayout());
        add(painelNorte, BorderLayout.NORTH);
        add(painelCentro, BorderLayout.CENTER);
        add(painelSul, BorderLayout.SOUTH);

        setSize(800, 400);
        setLocation(20, 20);
    }

    public boolean isReserva() {
        return reserva;
    }

    private void buscar() {
        try {
            modelo.setHospedagens(reserva
                    ? service.listarReservas(txtHospede.getText())
                    : service.listarHospedagens(txtHospede.getText()));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao consultar: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void novo() {
        abrirCadastro(new FrmCadastroHospedagem(reserva));
    }

    private void alterar() {
        Hospedagem selecionada = selecionada();
        if (selecionada != null) {
            abrirCadastro(new FrmCadastroHospedagem(selecionada, reserva));
        }
    }

    // Check-in: abre a reserva no formulário de hospedagem para informar a unidade.
    private void hospedar() {
        Hospedagem selecionada = selecionada();
        if (selecionada != null) {
            abrirCadastro(new FrmCadastroHospedagem(selecionada, false));
        }
    }

    private Hospedagem selecionada() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um registro.",
                    "Atenção", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return modelo.getHospedagem(tabela.convertRowIndexToModel(linha));
    }

    private void abrirCadastro(FrmCadastroHospedagem frame) {
        frame.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                if (!txtHospede.getText().isBlank() || modelo.getRowCount() > 0) {
                    buscar();
                }
            }
        });
        getDesktopPane().add(frame);
        frame.setVisible(true);
    }

    private static class HospedagemTableModel extends AbstractTableModel {

        private static final String[] COLUNAS =
                {"Id", "Hóspede", "Acomodação", "Data inicial", "Data final", "Status", "Valor total"};
        private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        private static final NumberFormat MOEDA = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));

        private List<Hospedagem> hospedagens = new ArrayList<>();

        void setHospedagens(List<Hospedagem> hospedagens) {
            this.hospedagens = hospedagens;
            fireTableDataChanged();
        }

        Hospedagem getHospedagem(int linha) {
            return hospedagens.get(linha);
        }

        @Override
        public int getRowCount() {
            return hospedagens.size();
        }

        @Override
        public int getColumnCount() {
            return COLUNAS.length;
        }

        @Override
        public String getColumnName(int coluna) {
            return COLUNAS[coluna];
        }

        @Override
        public Object getValueAt(int linha, int coluna) {
            Hospedagem h = hospedagens.get(linha);
            return switch (coluna) {
                case 0 -> h.getId();
                case 1 -> h.getHospede() == null ? null : h.getHospede().getNome();
                case 2 -> h.getUnidadeLocacao() == null ? null
                        : h.getUnidadeLocacao().getLegenda() + " - " + h.getUnidadeLocacao().getNumero();
                case 3 -> h.getDuracao() == null || h.getDuracao().getDataInicial() == null ? null
                        : h.getDuracao().getDataInicial().format(FORMATO_DATA);
                case 4 -> h.getDuracao() == null || h.getDuracao().getDataFinal() == null ? null
                        : h.getDuracao().getDataFinal().format(FORMATO_DATA);
                case 5 -> h.getStatus();
                case 6 -> MOEDA.format(h.getValorTotal());
                default -> null;
            };
        }
    }
}
