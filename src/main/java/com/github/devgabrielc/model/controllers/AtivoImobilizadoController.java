package com.github.devgabrielc.model.controllers;

import com.github.devgabrielc.model.database.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

import static com.github.devgabrielc.model.controllers.LoginController.*;
import static com.github.devgabrielc.model.services.Functions.*;

// Tela de Ativo Imobilizado
public class AtivoImobilizadoController {
    @FXML
    public Button addButton;
    @FXML
    private Button cancelButton;
    @FXML
    private TextArea descricaoArea;
    @FXML
    private TextField grupoEquipamentoField;
    @FXML
    private TextField marcaField;
    @FXML
    private TextField modeloField;
    @FXML
    private TextField numeroSerieField;
    @FXML
    private TextField patrimonioField;
    @FXML
    private TextField tipoEquipamentoField;

    private static final Logger logger = LoggerFactory.getLogger(AtivoImobilizadoController.class);

    // Ação para adicionar materiais
    // Descrição é opcional
    @FXML
    void handleAddMateriais(ActionEvent event) {
        String grupoEquipamento = grupoEquipamentoField.getText();
        String tipoEquipamento = tipoEquipamentoField.getText();
        String marca = marcaField.getText();
        String modelo = modeloField.getText();
        String numeroSerie = numeroSerieField.getText();
        String descricao = descricaoArea.getText();
        String patrimonio = patrimonioField.getText();

        // Obrigatório ter os campos grupoEquipamento, tipoEquipamento, marca, modelo, numeroSerie e patrimonio preenchidos
        // Condição para verificar campos preenchidos
        if (grupoEquipamento.isEmpty() || tipoEquipamento.isEmpty() || marca.isEmpty() || modelo.isEmpty() ||
                numeroSerie.isEmpty() || patrimonio.isEmpty()) {
            showAlertError("Erro!", "Campos obrigatórios não preenchidos.");
            return;
        }
        // Query para inserir os parâmetros
        // Quantidade do ativo vem por padrão com o valor "1", caso queira alterar, só adicionar o ativo e alterar na tabela posteriormente
        String query = "INSERT INTO materiais (grupo_equipamento, tipo_equipamento, marca, modelo, numero_serie, quantidade, " +
                "patrimonio, descricao) VALUES (?, ?, ?, ?, ?, 1, ?, ?)";

            try (Connection conn = DatabaseConnection.connect();
                 PreparedStatement stmt = conn.prepareStatement(query)){

                stmt.setString(1, grupoEquipamento);
                stmt.setString(2, tipoEquipamento);
                stmt.setString(3, marca);
                stmt.setString(4, modelo);
                stmt.setString(5, numeroSerie);
                stmt.setString(6, patrimonio);
                stmt.setString(7, descricao);

                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    showAlertSuccess("Sucesso!", "Material adicionado com sucesso!");
                    registrarHistorico(usuarioLogado, "Inclusao", "Material: " + tipoEquipamento + ", Quantidade: 1");
                }
            } catch (Exception e) {
                logger.error("Erro ao adicionar o material | {}", e.getMessage());
                showAlertError("Erro!", "Ocorreu um erro ao adicionar o material. Tente novamente.");
        }
    }

    // Ação para cancelar a adição do material
    @FXML
    void handleCancelar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/github/devgabrielc/model/views/AddScreen.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) cancelButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            logger.error("Erro ao tentar acessar a tela AddScreen.fxml | {}", e.getMessage());
            showAlertError("Erro!", "Não foi possível carregar a tela (Adicionar Material).");
        }
    }
}