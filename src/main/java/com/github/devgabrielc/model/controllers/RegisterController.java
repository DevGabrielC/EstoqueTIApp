package com.github.devgabrielc.model.controllers;

import com.github.devgabrielc.model.database.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.github.devgabrielc.model.controllers.LoginController.*;
import static com.github.devgabrielc.model.services.Functions.showAlertError;
import static com.github.devgabrielc.model.services.Functions.showAlertSuccess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController {
    @FXML
    private Button cancelButton;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField primeiroNomeField;
    @FXML
    private TextField sobrenomeField;
    @FXML
    private TextField userTextField;

    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);

    // Lógica para a Tela de Cadastro
    @FXML
    private void handleRegisterButton() {
        String username = userTextField.getText();
        String password = passwordField.getText();
        String fname = primeiroNomeField.getText();
        String lname = sobrenomeField.getText();
        String hashDaSenha = BCrypt.hashpw(password, BCrypt.gensalt());

        if (username.isEmpty() || password.isEmpty() || fname.isEmpty() || lname.isEmpty()) {
            showAlertError("Erro!", "Campos obrigatórios não preenchidos.");
        } else if (validarCadastro(username)) {
                showAlertError("Erro!", "Já existe um usuário cadastrado com esse nome.");
            } else {
                String query = "INSERT INTO usuarios (username, password, fname, lname) VALUES (?, ?, ?, ?)";
                try (Connection conn = DatabaseConnection.connect();
                     PreparedStatement pstmt = conn.prepareStatement(query)) {

                    pstmt.setString(1, username);
                    pstmt.setString(2, hashDaSenha);
                    pstmt.setString(3, fname);
                    pstmt.setString(4, lname);

                    int affectedRows = pstmt.executeUpdate();

                    if (affectedRows > 0) {
                        showAlertSuccess("Sucesso!", "Cadastro realizado.");
                        logger.info("Usuario cadastrado | usuario: {}", username);
                    }
                } catch (SQLException e) {
                    showAlertError("Erro!", "Erro ao realizar o cadastro.");
                    logger.error("Erro ao realizar o cadastro | {}", e.getMessage());
                }
            }
        }

    @FXML
    void handleCancelar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/github/devgabrielc/model/views/LoginScreen.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) cancelButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            logger.error("Erro ao tentar acessar a tela LoginScreen.fxml | {}", e.getMessage());
            showAlertError("Erro!", "Não foi possível carregar a tela (Login).");
        }
    }
}