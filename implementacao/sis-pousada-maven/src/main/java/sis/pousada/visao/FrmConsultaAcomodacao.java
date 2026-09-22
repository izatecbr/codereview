package sis.pousada.visao;

import sis.pousada.dao.AcomodacaoJPA;
import sis.pousada.modelo.acomodacao.Acomodacao;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FrmConsultaAcomodacao extends JInternalFrame {

    private final JTextField txtLegenda = new JTextField(30);
    private final AcomodacaoTableModel modelo = new AcomodacaoTableModel();
    private final JTable tabela = new JTable(modelo);

    private final AcomodacaoJPA acomodacaoJPA = new AcomodacaoJPA();

    public FrmConsultaAcomodacao() {
        setTitle("Consulta de Acomodações");
        setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        setClosable(true);
        setMaximizable(true);
        setIconifiable(true);
        setResizable(true);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscar());
        txtLegenda.addActionListener(e -> buscar());

        JPanel painelNorte = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        painelNorte.add(new JLabel("Legenda:"));
        painelNorte.add(txtLegenda);
        painelNorte.add(btnBuscar);

        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane painelCentro = new JScrollPane(tabela);

        JButton btnNovo = new JButton("Novo");
        btnNovo.addActionListener(e -> novo());
        JButton btnAlterar = new JButton("Alterar");
        btnAlterar.addActionListener(e -> alterar());
        JButton btnFechar = new JButton("Fechar");
        btnFechar.addActionListener(e -> dispose());

        JPanel painelSul = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        painelSul.add(btnNovo);
        painelSul.add(btnAlterar);
        painelSul.add(btnFechar);

        setLayout(new BorderLayout());
        add(painelNorte, BorderLayout.NORTH);
        add(painelCentro, BorderLayout.CENTER);
        add(painelSul, BorderLayout.SOUTH);

        setSize(700, 400);
        setLocation(20, 20);
    }

    private void buscar() {
        try {
            modelo.setAcomodacoes(acomodacaoJPA.listarPorLegenda(txtLegenda.getText()));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao consultar acomodações: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void novo() {
        abrirCadastro(new FrmCadastroAcomodacao());
    }

    private void abrirCadastro(FrmCadastroAcomodacao frame) {
        frame.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                if (!txtLegenda.getText().isBlank() || modelo.getRowCount() > 0) {
                    buscar();
                }
            }
        });
        getDesktopPane().add(frame);
        frame.setVisible(true);
    }

    private void alterar() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma acomodação para alterar.",
                    "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Acomodacao selecionada = modelo.getAcomodacao(tabela.convertRowIndexToModel(linha));
        abrirCadastro(new FrmCadastroAcomodacao(selecionada));
    }

    private static class AcomodacaoTableModel extends AbstractTableModel {

        private static final String[] COLUNAS = {"Id", "Legenda", "Tipo", "Valor diária"};

        private List<Acomodacao> acomodacoes = new ArrayList<>();

        void setAcomodacoes(List<Acomodacao> acomodacoes) {
            this.acomodacoes = acomodacoes;
            fireTableDataChanged();
        }

        Acomodacao getAcomodacao(int linha) {
            return acomodacoes.get(linha);
        }

        @Override
        public int getRowCount() {
            return acomodacoes.size();
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
            Acomodacao a = acomodacoes.get(linha);
            return switch (coluna) {
                case 0 -> a.getId();
                case 1 -> a.getLegenda();
                case 2 -> a.getTipo() == null ? null : a.getTipo().getDescricao();
                case 3 -> a.getValorDiaria();
                default -> null;
            };
        }
    }
}
