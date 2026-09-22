package sis.pousada.dao;
import sis.pousada.modelo.cadastro.Cadastro;
import sis.pousada.utilidade.FabricaConexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CadastroDAO {

    public void incluir(Cadastro cadastro) throws SQLException {

        String sql = """
                INSERT INTO tab_cadastro
                    (nome, cpf_cnpj, documento, aniversario, email)
                VALUES
                    (?, ?, ?, ?, ?)
                """;

        try (Connection conexao = FabricaConexao.criarConexao();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setString(1, cadastro.getNome());
            comando.setString(2, cadastro.getCpfCnpj());
            comando.setString(3, cadastro.getDocumento());
            comando.setObject(4, cadastro.getAniversario());
            comando.setString(5, cadastro.getEmail());

            comando.executeUpdate();
        }
    }

    public void alterar(Cadastro cadastro) throws SQLException {

        String sql = """
            UPDATE tab_cadastro
               SET nome = ?,
                   cpf_cnpj = ?,
                   documento = ?,
                   aniversario = ?,
                   email = ?
             WHERE id = ?
            """;

        try (Connection conexao = FabricaConexao.criarConexao();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setString(1, cadastro.getNome());
            comando.setString(2, cadastro.getCpfCnpj());
            comando.setString(3, cadastro.getDocumento());
            comando.setObject(4, cadastro.getAniversario());
            comando.setString(5, cadastro.getEmail());
            comando.setInt(6, cadastro.getId());

            comando.executeUpdate();
        }
    }

    public List<Cadastro> listarTodos() throws SQLException {

        String sql = """
            SELECT id,
                   nome,
                   cpf_cnpj,
                   documento,
                   aniversario,
                   email
              FROM tab_cadastro
             ORDER BY id
            """;

        List<Cadastro> cadastros = new ArrayList<>();

        try (Connection conexao = FabricaConexao.criarConexao();
             PreparedStatement comando = conexao.prepareStatement(sql);
             ResultSet resultado = comando.executeQuery()) {

            while (resultado.next()) {

                Cadastro cadastro = new Cadastro();

                cadastro.setId(resultado.getInt("id"));
                cadastro.setNome(resultado.getString("nome"));
                cadastro.setCpfCnpj(resultado.getString("cpf_cnpj"));
                cadastro.setDocumento(resultado.getString("documento"));
                cadastro.setAniversario(
                        resultado.getObject("aniversario", java.time.LocalDate.class)
                );
                cadastro.setEmail(resultado.getString("email"));

                cadastros.add(cadastro);
            }
        }

        return cadastros;
    }

}
