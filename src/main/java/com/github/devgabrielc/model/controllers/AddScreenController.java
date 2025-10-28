package com.github.devgabrielc.model.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

import static com.github.devgabrielc.model.services.Functions.showAlertError;

// Tela para escolher materiais que serão adicionados
// Duas alternativas, sendo Ativo Imobilizado e Uso e Consumo
public class AddScreenController {
    @FXML
    private Button cancelButton;
    @FXML
    private Button usoConsumoButton;
    @FXML
    private Button ativoImobilizadoButton;

    // Ação do botão de Ativo Imobilizado
    @FXML
    void handleAtivoImobilizadoScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/github/devgabrielc/model/views/AtivoImobilizado.fxml"));
            Parent initialize = loader.load();
            Stage stage = (Stage) ativoImobilizadoButton.getScene().getWindow();
            Scene scene = new Scene(initialize);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlertError("Erro!", "Não foi possível carregar a tela (Ativo Imobilizado).");
        }
    }

    @FXML
    void handleUsoConsumoScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/github/devgabrielc/model/views/UsoConsumo.fxml"));
            Parent initialize = loader.load();
            Stage stage = (Stage) usoConsumoButton.getScene().getWindow();
            Scene scene = new Scene(initialize);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e){
            showAlertError("Erro!", "Não foi possível carregar a tela (Uso e Consumo).");
        }
    }

    @FXML
    void handleCancelar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/github/devgabrielc/model/views/MainScreen.fxml"));
            Parent initialize = loader.load();

            Stage stage = (Stage) cancelButton.getScene().getWindow();
            Scene scene = new Scene(initialize);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}