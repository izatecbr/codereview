package sis.pousada.visao;

import sis.pousada.dao.CadastroJPA;
import sis.pousada.modelo.Cadastro;
import sis.pousada.modelo.Celular;
import sis.pousada.modelo.Endereco;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class FrmCadastroCliente extends JInternalFrame {

    private final JTextField txtNome = new JTextField();
    private final JTextField txtCpfCnpj = new JTextField();
    private final JTextField txtDocumento = new JTextField();
    private final JTextField txtAniversario = new JTextField();
    private final JTextField txtEmail = new JTextField();

    private final JTextField txtLogradouro = new JTextField();
    private final JTextField txtNumero = new JTextField();
    private final JTextField txtComplemento = new JTextField();
    private final JTextField txtBairro = new JTextField();
    private final JTextField txtCidade = new JTextField();
    private final JTextField txtUf = new JTextField();
    private final JTextField txtCep = new JTextField();
    private final JTextField txtIbge = new JTextField();

    private final JTextField txtCelular = new JTextField();
    private final JTextField txtWhatsapp = new JTextField();

    private final CadastroJPA cadastroJPA = new CadastroJPA();

    public FrmCadastroCliente() {
        setTitle("Tela de Cadastro");
        setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        setClosable(true);
        setMaximizable(true);
        setIconifiable(true);
        setResizable(true);

        JPanel painelCampos = new JPanel(new GridLayout(0, 2, 5, 5));
        painelCampos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        painelCampos.add(new JLabel("Nome:"));
        painelCampos.add(txtNome);
        painelCampos.add(new JLabel("CPF/CNPJ:"));
        painelCampos.add(txtCpfCnpj);
        painelCampos.add(new JLabel("Documento:"));
        painelCampos.add(txtDocumento);
        painelCampos.add(new JLabel("Aniversário (dd/MM/yyyy):"));
        painelCampos.add(txtAniversario);
        painelCampos.add(new JLabel("Email:"));
        painelCampos.add(txtEmail);

        painelCampos.add(new JLabel("Logradouro:"));
        painelCampos.add(txtLogradouro);
        painelCampos.add(new JLabel("Número:"));
        painelCampos.add(txtNumero);
        painelCampos.add(new JLabel("Complemento:"));
        painelCampos.add(txtComplemento);
        painelCampos.add(new JLabel("Bairro:"));
        painelCampos.add(txtBairro);
        painelCampos.add(new JLabel("Cidade:"));
        painelCampos.add(txtCidade);
        painelCampos.add(new JLabel("UF:"));
        painelCampos.add(txtUf);
        painelCampos.add(new JLabel("CEP:"));
        painelCampos.add(txtCep);
        painelCampos.add(new JLabel("IBGE:"));
        painelCampos.add(txtIbge);

        painelCampos.add(new JLabel("Celular:"));
        painelCampos.add(txtCelular);
        painelCampos.add(new JLabel("Whatsapp (S/N):"));
        painelCampos.add(txtWhatsapp);

        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(e -> salvar());

        JPanel painelBotoes = new JPanel();
        painelBotoes.add(btnSalvar);

        setLayout(new BorderLayout());
        add(painelCampos, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);

        pack();
        setLocation(20, 20);
    }

    private void salvar() {
        try {
            Cadastro cadastro = new Cadastro();
            cadastro.setNome(txtNome.getText());
            cadastro.setCpfCnpj(txtCpfCnpj.getText());
            cadastro.setDocumento(txtDocumento.getText());
            cadastro.setEmail(txtEmail.getText());

            if (!txtAniversario.getText().isBlank()) {
                DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                cadastro.setAniversario(LocalDate.parse(txtAniversario.getText(), formato));
            }

            Endereco endereco = new Endereco();
            endereco.setLogradouro(txtLogradouro.getText());
            endereco.setNumero(txtNumero.getText());
            endereco.setComplemento(txtComplemento.getText());
            endereco.setBairro(txtBairro.getText());
            endereco.setCidade(txtCidade.getText());
            endereco.setUf(txtUf.getText());
            if (!txtCep.getText().isBlank()) {
                endereco.setCep(Integer.parseInt(txtCep.getText()));
            }
            if (!txtIbge.getText().isBlank()) {
                endereco.setIbge(Integer.parseInt(txtIbge.getText()));
            }
            cadastro.setEndereco(endereco);

            Celular celular = new Celular();
            if (!txtCelular.getText().isBlank()) {
                celular.setNumero(Long.parseLong(txtCelular.getText()));
            }
            celular.setWhatsapp(txtWhatsapp.getText().trim().equalsIgnoreCase("S"));
            cadastro.setCelular(celular);

            cadastroJPA.incluir(cadastro);

            JOptionPane.showMessageDialog(this, "Cadastro salvo com sucesso!");
            limparCampos();
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Data de aniversário inválida. Use o formato dd/MM/yyyy.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Verifique os campos numéricos (CEP, IBGE, Celular).",
                    "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar cadastro: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparCampos() {
        for (JTextField campo : new JTextField[]{
                txtNome, txtCpfCnpj, txtDocumento, txtAniversario, txtEmail,
                txtLogradouro, txtNumero, txtComplemento, txtBairro, txtCidade, txtUf, txtCep, txtIbge,
                txtCelular, txtWhatsapp
        }) {
            campo.setText("");
        }
    }
}
