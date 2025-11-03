package com.github.devgabrielc.model.services;

import com.github.devgabrielc.model.controllers.LoginController;
import com.github.devgabrielc.model.database.DatabaseConnection;
import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.devgabrielc.model.database.DatabaseConnection.connect;
import static com.github.devgabrielc.model.controllers.LoginController.usuarioLogado;

public class Functions {

    private static final Logger logger = LoggerFactory.getLogger(Functions.class);
    public static String dataHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    public static void registrarHistorico(String usuario, String acao, String descricao) {
        String historico = "INSERT INTO historico (usuario, acao, descricao, data_hora) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.connect()) {

            PreparedStatement pstmt = connect().prepareStatement(historico);
            pstmt.setString(1, usuarioLogado);
            pstmt.setString(2, acao);
            pstmt.setString(3, descricao);
            pstmt.setString(4, dataHora);
            pstmt.executeUpdate();

            logger.info("Registro no historico | {} | {} | {}", usuarioLogado, acao, descricao);
        } catch (SQLException e) {
            logger.error("Erro ao registrar equipamento no historico | {}", e.getMessage());
        }
    }

    // Metodo que apresenta mensagem de erro na tela
    public static void showAlertError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    // Metodo que apresenta mensagem de sucesso na tela
    public static void showAlertSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}