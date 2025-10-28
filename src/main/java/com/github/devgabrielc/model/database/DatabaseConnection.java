package com.github.devgabrielc.model.database;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.*;

import static com.github.devgabrielc.model.services.Functions.showAlertError;

public class DatabaseConnection {
    private static final Dotenv dotenv = Dotenv.load();

    private static final String HOST = dotenv.get("DB_HOST");
    private static final String PORT = dotenv.get("DB_PORT");
    private static final String NAME = dotenv.get("DB_NAME"); // <--- MUDADO DE NAME PARA DB_NAME
    private static final String USER = dotenv.get("DB_USER");
    private static final String PASSWORD = dotenv.get("DB_PASSWORD");

    private static final String URL = "jdbc:postgresql://" + HOST + ":" + PORT + "/" + NAME;
    static {
        inicializarBancoDeDados();
    }

    public static void inicializarBancoDeDados() {

        String sqlCriarTabelaMateriais = "CREATE TABLE IF NOT EXISTS materiais ("
                + "id SERIAL PRIMARY KEY,"
                + "grupo_equipamento VARCHAR(50) NOT NULL,"
                + "tipo_equipamento VARCHAR(50) NOT NULL,"
                + "marca VARCHAR(50) NOT NULL,"
                + "modelo VARCHAR(50) NOT NULL,"
                + "numero_serie VARCHAR(50) NOT NULL,"
                + "quantidade INTEGER,"
                + "patrimonio VARCHAR(50),"
                + "descricao TEXT"
                + ");";

        String sqlCriarTabelaUsuarios = "CREATE TABLE IF NOT EXISTS usuarios ("
                + "id SERIAL PRIMARY KEY NOT NULL,"
                + "username VARCHAR(50) NOT NULL,"
                + "password VARCHAR(255) NOT NULL,"
                + "fname VARCHAR(30) NOT NULL,"
                + "lname VARCHAR(30) NOT NULL"
                + ");";

        String sqlCriarTabelaHistorico = "CREATE TABLE IF NOT EXISTS historico ("
                + "id SERIAL PRIMARY KEY NOT NULL,"
                + "usuario VARCHAR(50) NOT NULL,"
                + "acao VARCHAR(50) NOT NULL,"
                + "descricao TEXT,"
                + "data_hora TIMESTAMP"
                + ");";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD); // <--- Este estava correto
             Statement stmt = conn.createStatement()) {

            stmt.execute(sqlCriarTabelaMateriais);
            stmt.execute(sqlCriarTabelaUsuarios);
            stmt.execute(sqlCriarTabelaHistorico);

            System.out.println("PostgreSQL: Tabelas verificadas/criadas com sucesso.");

        } catch (SQLException e) {
            e.printStackTrace();
            showAlertError("Erro Crítico de Base de Dados!",
                    "Não foi possível ligar ou inicializar o PostgreSQL.\n" +
                            "Verifique se o Docker está em execução e as credenciais no .env estão corretas.\n\n" +
                            "Erro: " + e.getMessage());
            System.exit(1);
        }
    }

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Metodo que atualiza após editar algum campo na tabela
    public static void atualizarBancoDeDados(String coluna, Object novoValor, int id) {
        String sql = "UPDATE materiais SET " + coluna + " = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.connect(); // <--- Agora este método funciona!
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (novoValor instanceof Integer) {
                pstmt.setInt(1, (Integer) novoValor);
            } else if (novoValor instanceof String) {
                pstmt.setString(1, (String) novoValor);
            } else {
                showAlertError("Erro!", "Tipo de dado não suportado.");
            }
            pstmt.setInt(2, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlertError("Erro!", "Erro ao atualizar o Banco de Dados.");
        }
    }
}