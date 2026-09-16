package sis.pousada.modelo;

import sis.pousada.dao.CadastroDAO;

import java.sql.SQLException;
import java.util.List;

public class Exemplo {
    public static void main(String[] args) throws SQLException {

       Cadastro cadastro = new Cadastro();
        cadastro.setId(1);
        cadastro.setNome("Gleyson");
        cadastro.setCpfCnpj("123.456.789-00");
        cadastro.setDocumento("RG 1234567");
        cadastro.setAniversario(java.time.LocalDate.of(1990, 5, 15));
        cadastro.setEmail("joaosilva@gmail.com");

        CadastroDAO repository = new CadastroDAO();
        repository.incluir(cadastro);

        /*
        List<Cadastro> cadastros = repository.listarTodos();

        for (Cadastro cadastro : cadastros) {

            System.out.println("ID: " + cadastro.getId());
            System.out.println("Nome: " + cadastro.getNome());
            System.out.println("CPF/CNPJ: " + cadastro.getCpfCnpj());
            System.out.println("Documento: " + cadastro.getDocumento());
            System.out.println("Aniversário: " + cadastro.getAniversario());
            System.out.println("E-mail: " + cadastro.getEmail());

            System.out.println("-----------------------------");
        }

         */

    }
}
