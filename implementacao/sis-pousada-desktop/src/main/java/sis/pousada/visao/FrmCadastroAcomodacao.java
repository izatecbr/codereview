package sis.pousada.visao;

import sis.pousada.service.AcomodacaoService;
import sis.pousada.modelo.acomodacao.Acomodacao;
import sis.pousada.modelo.acomodacao.AcomodacaoTipo;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class FrmCadastroAcomodacao extends JInternalFrame {

    private final JTextField txtLegenda = new JTextField();
    private final JTextField txtNumero = new JTextField();
    private final JTextField txtDescricao = new JTextField();
    private final JTextField txtValorDiaria = new JTextField();
    private final JComboBox<AcomodacaoTipo> cbTipo = new JComboBox<>(AcomodacaoTipo.values());

    private final JTextField txtItem = new JTextField();
    private final DefaultListModel<String> modeloItens = new DefaultListModel<>();
    private final JList<String> listaItens = new JList<>(modeloItens);

    private final AcomodacaoService acomodacaoService = new AcomodacaoService();
    private final Acomodacao acomodacaoEdicao;

    public FrmCadastroAcomodacao() {
        this(null);
    }

    public FrmCadastroAcomodacao(Acomodacao acomodacaoEdicao) {
        this.acomodacaoEdicao = acomodacaoEdicao;
        setTitle(acomodacaoEdicao == null ? "Tela de Acomodação" : "Alteração de Acomodação");
        setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        setClosable(true);
        setMaximizable(true);
        setIconifiable(true);
        setResizable(true);

        JPanel painelCampos = new JPanel(new GridLayout(0, 2, 5, 5));
        painelCampos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        painelCampos.add(new JLabel("Legenda:"));
        painelCampos.add(txtLegenda);
        painelCampos.add(new JLabel("Número:"));
        painelCampos.add(txtNumero);
        painelCampos.add(new JLabel("Descrição:"));
        painelCampos.add(txtDescricao);
        painelCampos.add(new JLabel("Valor da diária:"));
        painelCampos.add(txtValorDiaria);
        painelCampos.add(new JLabel("Tipo:"));
        painelCampos.add(cbTipo);

        JButton btnAdicionarItem = new JButton("Adicionar");
        btnAdicionarItem.addActionListener(e -> adicionarItem());
        txtItem.addActionListener(e -> adicionarItem());

        JButton btnRemoverItem = new JButton("Remover selecionado");
        btnRemoverItem.addActionListener(e -> removerItem());

        JPanel painelItemEntrada = new JPanel(new BorderLayout(5, 5));
        painelItemEntrada.add(new JLabel("Item:"), BorderLayout.WEST);
        painelItemEntrada.add(txtItem, BorderLayout.CENTER);
        painelItemEntrada.add(btnAdicionarItem, BorderLayout.EAST);

        JPanel painelItens = new JPanel(new BorderLayout(5, 5));
        painelItens.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 10, 10, 10),
                BorderFactory.createTitledBorder("Itens da acomodação")));
        painelItens.add(painelItemEntrada, BorderLayout.NORTH);
        painelItens.add(new JScrollPane(listaItens), BorderLayout.CENTER);
        painelItens.add(btnRemoverItem, BorderLayout.SOUTH);

        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(e -> salvar());

        JPanel painelBotoes = new JPanel();
        painelBotoes.add(btnSalvar);

        setLayout(new BorderLayout());
        add(painelCampos, BorderLayout.NORTH);
        add(painelItens, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);

        setSize(420, 520);
        setLocation(20, 20);

        if (acomodacaoEdicao != null) {
            preencherCampos(acomodacaoEdicao);
        }
    }

    private void adicionarItem() {
        String item = txtItem.getText().trim();
        if (!item.isBlank()) {
            modeloItens.addElement(item);
            txtItem.setText("");
        }
        txtItem.requestFocus();
    }

    private void removerItem() {
        int indice = listaItens.getSelectedIndex();
        if (indice < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um item para remover.",
                    "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        modeloItens.remove(indice);
    }

    private void preencherCampos(Acomodacao acomodacao) {
        txtLegenda.setText(acomodacao.getLegenda());
        txtNumero.setText(acomodacao.getNumero());
        txtDescricao.setText(acomodacao.getDescricao());
        txtValorDiaria.setText(String.valueOf(acomodacao.getValorDiaria()));
        cbTipo.setSelectedItem(acomodacao.getTipo());

        modeloItens.clear();
        if (acomodacao.getItens() != null) {
            acomodacao.getItens().forEach(modeloItens::addElement);
        }
    }

    private void salvar() {
        if (txtNumero.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Informe o número da acomodação.",
                    "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Acomodacao acomodacao = new Acomodacao();
            if (acomodacaoEdicao != null) {
                acomodacao.setId(acomodacaoEdicao.getId());
            }
            acomodacao.setLegenda(txtLegenda.getText());
            acomodacao.setNumero(txtNumero.getText().trim());
            acomodacao.setDescricao(txtDescricao.getText());
            acomodacao.setValorDiaria(Double.parseDouble(txtValorDiaria.getText().replace(",", ".")));
            acomodacao.setTipo((AcomodacaoTipo) cbTipo.getSelectedItem());
            acomodacao.setItens(new ArrayList<>(java.util.Collections.list(modeloItens.elements())));

            if (acomodacaoEdicao == null) {
                acomodacaoService.incluir(acomodacao);
                JOptionPane.showMessageDialog(this, "Acomodação salva com sucesso!");
                limparCampos();
            } else {
                acomodacaoService.alterar(acomodacao);
                JOptionPane.showMessageDialog(this, "Acomodação alterada com sucesso!");
                dispose();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Informe um valor de diária válido.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar acomodação: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparCampos() {
        txtLegenda.setText("");
        txtNumero.setText("");
        txtDescricao.setText("");
        txtValorDiaria.setText("");
        txtItem.setText("");
        cbTipo.setSelectedIndex(0);
        modeloItens.clear();
    }
}
