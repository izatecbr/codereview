package sis.pousada.visao;

import sis.pousada.dao.AcomodacaoJPA;
import sis.pousada.dao.CadastroJPA;
import sis.pousada.modelo.acomodacao.Acomodacao;
import sis.pousada.modelo.cadastro.Cadastro;
import sis.pousada.modelo.hospedagem.Duracao;
import sis.pousada.modelo.hospedagem.HospedagamStatus;
import sis.pousada.modelo.hospedagem.Hospedagem;
import sis.pousada.modelo.hospedagem.Hospede;
import sis.pousada.modelo.hospedagem.UnidadeLocacao;
import sis.pousada.service.HospedagemService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.function.Function;

/**
 * Formulário de inclusão/alteração usado para RESERVA (sem acomodação/unidade)
 * e para HOSPEDAGEM (com acomodação/unidade e valor total calculado).
 */
public class FrmCadastroHospedagem extends JInternalFrame {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final NumberFormat MOEDA = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));

    private final JComboBox<Cadastro> cbHospede = new JComboBox<>();
    private final JComboBox<Acomodacao> cbAcomodacao = new JComboBox<>();
    private final JTextField txtDataInicial = new JTextField();
    private final JTextField txtDataFinal = new JTextField();
    private final JComboBox<HospedagamStatus> cbStatus;
    private final JTextField txtValorTotal = new JTextField();

    private final CadastroJPA cadastroJPA = new CadastroJPA();
    private final AcomodacaoJPA acomodacaoJPA = new AcomodacaoJPA();
    private final HospedagemService service = new HospedagemService();

    private final boolean reserva;
    private final Hospedagem hospedagemEdicao;

    public FrmCadastroHospedagem(boolean reserva) {
        this(null, reserva);
    }

    public FrmCadastroHospedagem(Hospedagem hospedagemEdicao, boolean reserva) {
        this.reserva = reserva;
        this.hospedagemEdicao = hospedagemEdicao;

        String nome = reserva ? "Reserva" : "Hospedagem";
        setTitle(hospedagemEdicao == null ? "Nova " + nome : "Alteração de " + nome);
        setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        setClosable(true);
        setMaximizable(true);
        setIconifiable(true);
        setResizable(true);

        cbStatus = new JComboBox<>(reserva
                ? new HospedagamStatus[]{HospedagamStatus.RESERVADA, HospedagamStatus.CANCELADA}
                : new HospedagamStatus[]{HospedagamStatus.HOSPEDADA, HospedagamStatus.FINALIZADA,
                HospedagamStatus.CANCELADA});

        cbHospede.setRenderer(rotulo(Cadastro.class, Cadastro::getNome));
        cbAcomodacao.setRenderer(rotulo(Acomodacao.class,
                a -> a.getNumero() == null ? a.getLegenda() : a.getLegenda() + " - " + a.getNumero()));
        txtValorTotal.setEditable(false);

        carregarCombos();

        JPanel painelCampos = new JPanel(new GridLayout(0, 2, 5, 5));
        painelCampos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        painelCampos.add(new JLabel("Hóspede:"));
        painelCampos.add(cbHospede);
        if (!reserva) {
            painelCampos.add(new JLabel("Acomodação:"));
            painelCampos.add(cbAcomodacao);
        }
        painelCampos.add(new JLabel("Data inicial (dd/MM/yyyy):"));
        painelCampos.add(txtDataInicial);
        painelCampos.add(new JLabel("Data final (dd/MM/yyyy):"));
        painelCampos.add(txtDataFinal);
        painelCampos.add(new JLabel("Status:"));
        painelCampos.add(cbStatus);
        painelCampos.add(new JLabel("Valor total:"));
        painelCampos.add(txtValorTotal);

        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(e -> salvar());

        JPanel painelBotoes = new JPanel();
        painelBotoes.add(btnSalvar);

        setLayout(new BorderLayout());
        add(painelCampos, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);

        pack();
        setLocation(20, 20);

        if (hospedagemEdicao != null) {
            preencherCampos(hospedagemEdicao);
        }

        cbAcomodacao.addActionListener(e -> atualizarValorTotal());
        cbStatus.addActionListener(e -> atualizarValorTotal());
        aoDigitar(txtDataInicial, this::atualizarValorTotal);
        aoDigitar(txtDataFinal, this::atualizarValorTotal);
        atualizarValorTotal();
    }

    private void carregarCombos() {
        try {
            cadastroJPA.listarPorNome("").forEach(cbHospede::addItem);
            if (!reserva) {
                acomodacaoJPA.listar().forEach(cbAcomodacao::addItem);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar hóspedes/acomodações: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
        cbHospede.setSelectedIndex(-1);
        cbAcomodacao.setSelectedIndex(-1);
    }

    private void preencherCampos(Hospedagem hospedagem) {
        Hospede hospede = hospedagem.getHospede();
        if (hospede != null) {
            Cadastro cadastro = null;
            for (int i = 0; i < cbHospede.getItemCount(); i++) {
                if (cbHospede.getItemAt(i).getId() == hospede.getId()) {
                    cadastro = cbHospede.getItemAt(i);
                    break;
                }
            }
            if (cadastro == null) {
                cadastro = new Cadastro();
                cadastro.setId(hospede.getId());
                cadastro.setNome(hospede.getNome());
                cbHospede.addItem(cadastro);
            }
            cbHospede.setSelectedItem(cadastro);
        }

        UnidadeLocacao unidade = hospedagem.getUnidadeLocacao();
        if (!reserva && unidade != null) {
            Acomodacao acomodacao = null;
            for (int i = 0; i < cbAcomodacao.getItemCount(); i++) {
                if (cbAcomodacao.getItemAt(i).getId().equals(unidade.getId())) {
                    acomodacao = cbAcomodacao.getItemAt(i);
                    break;
                }
            }
            if (acomodacao == null) {
                acomodacao = new Acomodacao();
                acomodacao.setId(unidade.getId());
                acomodacao.setLegenda(unidade.getLegenda());
                acomodacao.setNumero(unidade.getNumero());
                cbAcomodacao.addItem(acomodacao);
            }
            cbAcomodacao.setSelectedItem(acomodacao);
        }

        Duracao duracao = hospedagem.getDuracao();
        if (duracao != null) {
            if (duracao.getDataInicial() != null) {
                txtDataInicial.setText(duracao.getDataInicial().format(FORMATO_DATA));
            }
            if (duracao.getDataFinal() != null) {
                txtDataFinal.setText(duracao.getDataFinal().format(FORMATO_DATA));
            }
        }

        cbStatus.setSelectedItem(hospedagem.getStatus());
    }

    private Hospedagem montarHospedagem() {
        Hospedagem hospedagem = new Hospedagem();
        if (hospedagemEdicao != null) {
            hospedagem.setId(hospedagemEdicao.getId());
        }

        Cadastro cadastro = (Cadastro) cbHospede.getSelectedItem();
        if (cadastro != null) {
            Hospede hospede = new Hospede();
            hospede.setId(cadastro.getId());
            hospede.setNome(cadastro.getNome());
            hospedagem.setHospede(hospede);
        }

        Duracao duracao = new Duracao();
        duracao.setDataInicial(lerData(txtDataInicial));
        duracao.setDataFinal(lerData(txtDataFinal));
        hospedagem.setDuracao(duracao);

        hospedagem.setStatus((HospedagamStatus) cbStatus.getSelectedItem());

        Acomodacao acomodacao = (Acomodacao) cbAcomodacao.getSelectedItem();
        if (!reserva && acomodacao != null) {
            hospedagem.setUnidadeLocacao(montarUnidade(acomodacao));
        }
        return hospedagem;
    }

    // Se a acomodação não mudou, mantém legenda/número/diária já gravados na hospedagem.
    private UnidadeLocacao montarUnidade(Acomodacao acomodacao) {
        UnidadeLocacao gravada = hospedagemEdicao == null ? null : hospedagemEdicao.getUnidadeLocacao();

        UnidadeLocacao unidade = new UnidadeLocacao();
        unidade.setId(acomodacao.getId());
        if (gravada != null && acomodacao.getId().equals(gravada.getId())) {
            unidade.setLegenda(gravada.getLegenda());
            unidade.setNumero(gravada.getNumero());
            unidade.setValorDiaria(gravada.getValorDiaria());
        } else {
            unidade.setLegenda(acomodacao.getLegenda());
            unidade.setNumero(acomodacao.getNumero());
            unidade.setValorDiaria(acomodacao.getValorDiaria());
        }
        return unidade;
    }

    private LocalDate lerData(JTextField campo) {
        String texto = campo.getText().trim();
        return texto.isEmpty() ? null : LocalDate.parse(texto, FORMATO_DATA);
    }

    private void atualizarValorTotal() {
        try {
            txtValorTotal.setText(MOEDA.format(service.calcularValorTotal(montarHospedagem())));
        } catch (Exception ex) {
            txtValorTotal.setText("");
        }
    }

    private void salvar() {
        try {
            Hospedagem hospedagem = montarHospedagem();

            if (hospedagemEdicao == null) {
                service.incluir(hospedagem);
                JOptionPane.showMessageDialog(this, "Salvo com sucesso!");
                limparCampos();
            } else {
                service.alterar(hospedagem);
                JOptionPane.showMessageDialog(this, "Alterado com sucesso!");
                dispose();
            }
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Data inválida. Use o formato dd/MM/yyyy.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Atenção", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparCampos() {
        cbHospede.setSelectedIndex(-1);
        cbAcomodacao.setSelectedIndex(-1);
        txtDataInicial.setText("");
        txtDataFinal.setText("");
        cbStatus.setSelectedIndex(0);
    }

    private static void aoDigitar(JTextField campo, Runnable acao) {
        campo.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                acao.run();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                acao.run();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                acao.run();
            }
        });
    }

    private static <T> ListCellRenderer<Object> rotulo(Class<T> tipo, Function<T, String> texto) {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                Object exibir = tipo.isInstance(value) ? texto.apply(tipo.cast(value)) : value;
                return super.getListCellRendererComponent(list, exibir, index, isSelected, cellHasFocus);
            }
        };
    }
}
