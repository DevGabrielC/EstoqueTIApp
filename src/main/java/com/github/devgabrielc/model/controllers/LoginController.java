package com.github.devgabrielc.model.controllers;

import com.github.devgabrielc.model.database.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

// 1. IMPORTAR A BIBLIOTECA BCRYPT
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.github.devgabrielc.model.services.Functions.showAlertError;

public class LoginController {

    @FXML
    private TextField userTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    public static String usuarioLogado;
    @FXML
    private Button registerButton;

    public void setStage(Stage stage) {
    }

    // Ação para acessar a tela principal (Nenhuma mudança aqui)
    @FXML
    public void handleLoginButton(ActionEvent event) {
        String username = userTextField.getText();
        String password = passwordField.getText(); // A senha em texto plano

        // A lógica de validação foi movida para o novo método "validarLogin"
        if (validarLogin(username, password)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/github/devgabrielc/model/views/MainScreen.fxml"));
                Parent root = loader.load();

                // Configura o Stage atual para exibir a MainController
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setMaximized(true);
                stage.setTitle("Estoque TI");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlertError("Erro!", "Usuário ou senha incorretos.");
        }
    }

    @FXML
    void handleRegisterScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/github/devgabrielc/model/views/RegisterScreen.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) registerButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Estoque TI");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static boolean validarCadastro(String username) {
        String query = "SELECT 1 FROM usuarios WHERE username = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean validarLogin(String username, String senhaDigitada) {
        String query = "SELECT fname, lname, password FROM usuarios WHERE username = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    String hashGuardado = rs.getString("password");

                    if (BCrypt.checkpw(senhaDigitada, hashGuardado)) {

                        String fname = rs.getString("fname");
                        String lname = rs.getString("lname");
                        usuarioLogado = fname + " " + lname;

                        return true;
                    } else {
                        // Senha incorreta
                        return false;
                    }
                } else {
                    // Utilizador não encontrado
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlertError("Erro de Login", "Não foi possível verificar as credenciais.");
            return false;
        }
    }
}