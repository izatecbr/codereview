package sis.pousada;

import sis.pousada.modelo.cadastro.Cadastro;
import sis.pousada.modelo.cadastro.Celular;
import sis.pousada.modelo.cadastro.Endereco;
import sis.pousada.service.CadastroService;

public class Exemplo {
    public static void main(String[] args) {

        Cadastro cadastro = new Cadastro();
        cadastro.setNome("Izabelly");
        cadastro.setCpfCnpj("123.456.789-00");
        cadastro.setDocumento("RG 1234567");
        cadastro.setAniversario(java.time.LocalDate.of(1990, 5, 15));
        cadastro.setEmail("joaosilva@gmail.com");

        Endereco endereco = new Endereco();
        endereco.setLogradouro("Rua das Flores");
        endereco.setNumero("123");
        endereco.setComplemento("Apto 101");
        endereco.setBairro("Centro");
        endereco.setCidade("São Paulo");
        endereco.setUf("SP");
        endereco.setCep(12345678);
        endereco.setIbge(1234567);

        Celular celular = new Celular();
        celular.setNumero(11987654321L);
        celular.setWhatsapp(true);

        cadastro.setEndereco(endereco);
        cadastro.setCelular(celular);

        CadastroService service = new CadastroService();
        Cadastro criado = service.incluir(cadastro);
        System.out.println("Cadastro criado pela API com o código " + criado.getId());



    }
}
