package sis.pousada.visao;

import sis.pousada.dao.CadastroJPA;
import sis.pousada.modelo.Cadastro;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FrmConsultaCliente extends JInternalFrame {

    private final JTextField txtNome = new JTextField(30);
    private final CadastroTableModel modelo = new CadastroTableModel();
    private final JTable tabela = new JTable(modelo);

    private final CadastroJPA cadastroJPA = new CadastroJPA();

    public FrmConsultaCliente() {
        setTitle("Consulta de Clientes");
        setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        setClosable(true);
        setMaximizable(true);
        setIconifiable(true);
        setResizable(true);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscar());
        txtNome.addActionListener(e -> buscar());

        JPanel painelNorte = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        painelNorte.add(new JLabel("Nome:"));
        painelNorte.add(txtNome);
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
            modelo.setCadastros(cadastroJPA.listarPorNome(txtNome.getText()));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao consultar cadastros: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void novo() {
        abrirCadastro(new FrmCadastroCliente());
    }

    private void abrirCadastro(FrmCadastroCliente frame) {
        frame.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                if (!txtNome.getText().isBlank() || modelo.getRowCount() > 0) {
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
            JOptionPane.showMessageDialog(this, "Selecione um cadastro para alterar.",
                    "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Cadastro selecionado = modelo.getCadastro(tabela.convertRowIndexToModel(linha));
        abrirCadastro(new FrmCadastroCliente(selecionado));
    }

    private static class CadastroTableModel extends AbstractTableModel {

        private static final String[] COLUNAS = {"Id", "Nome", "CPF/CNPJ", "Email", "Cidade"};

        private List<Cadastro> cadastros = new ArrayList<>();

        void setCadastros(List<Cadastro> cadastros) {
            this.cadastros = cadastros;
            fireTableDataChanged();
        }

        Cadastro getCadastro(int linha) {
            return cadastros.get(linha);
        }

        @Override
        public int getRowCount() {
            return cadastros.size();
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
            Cadastro c = cadastros.get(linha);
            return switch (coluna) {
                case 0 -> c.getId();
                case 1 -> c.getNome();
                case 2 -> c.getCpfCnpj();
                case 3 -> c.getEmail();
                case 4 -> c.getEndereco() == null ? null : c.getEndereco().getCidade();
                default -> null;
            };
        }
    }
}
