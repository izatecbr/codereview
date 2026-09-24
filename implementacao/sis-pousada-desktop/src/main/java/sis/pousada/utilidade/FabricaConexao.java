package sis.pousada.utilidade;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class FabricaConexao {

    private static final String URL = "jdbc:postgresql://localhost:5432/sis-pousada-db";

    private static final String USUARIO = "postgres";
    private static final String SENHA = "postgres";

    public static Connection criarConexao() throws SQLException {
        return DriverManager.getConnection(
                URL,
                USUARIO,
                SENHA
        );
    }
}
