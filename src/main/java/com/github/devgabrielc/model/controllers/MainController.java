package com.github.devgabrielc.model.controllers;

import com.github.devgabrielc.model.database.DatabaseConnection;
import com.github.devgabrielc.model.repositories.Estoque;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static com.github.devgabrielc.model.database.DatabaseConnection.atualizarBancoDeDados;
import static com.github.devgabrielc.model.controllers.LoginController.*;
import static com.github.devgabrielc.model.services.Functions.*;

public class MainController {
    @FXML
    private TableColumn<Estoque, Integer> ID;
    @FXML
    private TableColumn<Estoque, String> grupoEquipamento;
    @FXML
    private TableColumn<Estoque, String> tipoEquipamento;
    @FXML
    private TableColumn<Estoque, String> marca;
    @FXML
    private TableColumn<Estoque, String> modelo;
    @FXML
    private TableColumn<Estoque, String> numeroSerie;
    @FXML
    private TableColumn<Estoque, Integer> quantidade;
    @FXML
    private TableColumn<Estoque, Integer> patrimonio;
    @FXML
    private TableColumn<Estoque, String> descricao;
    @FXML
    private TableView<Estoque> table;
    @FXML
    private Text dataAtualText;
    @FXML
    private Text saudacaoText;
    @FXML
    private TextField searchField;
    @FXML
    private Label editMode;
    @FXML
    private Button exitButton;
    @FXML
    private Button logoutButton;

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @FXML
    public void initialize() throws SQLException {
        ID.setCellValueFactory(new PropertyValueFactory<>("id"));
        grupoEquipamento.setCellValueFactory(new PropertyValueFactory<>("grupoEquipamento"));
        tipoEquipamento.setCellValueFactory(new PropertyValueFactory<>("tipoEquipamento"));
        marca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        modelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        numeroSerie.setCellValueFactory(new PropertyValueFactory<>("numeroSerie"));
        quantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        patrimonio.setCellValueFactory(new PropertyValueFactory<>("patrimonio"));
        descricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        carregarMateriaisComFiltro(table);

        exibirSaudacao();
        exibirDataAtual();
    }

    @FXML
    private void handleLogout() {
        try {
            String usuarioQueSaiu = LoginController.usuarioLogado;
            LoginController.usuarioLogado = null;
            logger.info("Usuario {} fez logout.", usuarioQueSaiu);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/github/devgabrielc/model/view/LoginScreen.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) logoutButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            logger.error("Falha ao tentar carregar a LoginScreen.fxml após o logout.", e);
            showAlertError("Erro!", "Não foi possível carregar a tela (Login).");
        }
    }

    @FXML
    private void handleEditScreen(ActionEvent event) {
        table.setEditable(true);
        exitButton.setVisible(true);
        editMode.setVisible(true);

        grupoEquipamento.setCellFactory(TextFieldTableCell.forTableColumn());
        grupoEquipamento.setOnEditCommit(event1 -> {
            Estoque grupoEquip = event1.getRowValue();
            String nomeGrupoEquipAnterior = grupoEquip.getGrupoEquipamento();
            String nomeGrupoEquipNovo = event1.getNewValue();
            grupoEquip.setGrupoEquipamento(event1.getNewValue());
            atualizarBancoDeDados("grupo_equipamento", event1.getNewValue(), grupoEquip.getId());
            registrarHistorico(usuarioLogado, "Edicao", "Alteracao do nome do grupo de: " + nomeGrupoEquipAnterior + " para: " + nomeGrupoEquipNovo);
        });

        tipoEquipamento.setCellFactory(TextFieldTableCell.forTableColumn());
        tipoEquipamento.setOnEditCommit(event1 -> {
            Estoque tipoEquip = event1.getRowValue();
            String nomeTipoEquipAntigo = tipoEquip.getTipoEquipamento();
            String nomeTipoEquipNovo = event1.getNewValue();
            tipoEquip.setTipoEquipamento(event1.getNewValue());
            atualizarBancoDeDados("tipo_equipamento", event1.getNewValue(), tipoEquip.getId());
            registrarHistorico(usuarioLogado, "Edicao", "Alteracao do nome do tipo de: " + nomeTipoEquipAntigo + " para: " + nomeTipoEquipNovo);
        });

        marca.setCellFactory(TextFieldTableCell.forTableColumn());
        marca.setOnEditCommit(event1 -> {
            Estoque marca = event1.getRowValue();
            String nomeMarcaAntigo = marca.getMarca();
            String nomeMarcaNovo = event1.getNewValue();
            marca.setMarca(event1.getNewValue());
            atualizarBancoDeDados("marca", event1.getNewValue(), marca.getId());
            registrarHistorico(usuarioLogado, "Edicao", "Alteracao da marca de: " + nomeMarcaAntigo + " para: " + nomeMarcaNovo);
        });

        modelo.setCellFactory(TextFieldTableCell.forTableColumn());
        modelo.setOnEditCommit(event1 -> {
            Estoque modelo = event1.getRowValue();
            String nomeModeloAntigo = modelo.getModelo();
            String nomeModeloNovo = event1.getNewValue();
            modelo.setModelo(event1.getNewValue());
            atualizarBancoDeDados("modelo", event1.getNewValue(), modelo.getId());
            registrarHistorico(usuarioLogado, "Edicao", "Alteracao do modelo de: " + nomeModeloAntigo + " para: " + nomeModeloNovo);
        });

        numeroSerie.setCellFactory(TextFieldTableCell.forTableColumn());
        numeroSerie.setOnEditCommit(event1 -> {
            Estoque numeroSerie = event1.getRowValue();
            String numeroSerieAntigo = numeroSerie.getNumeroSerie();
            String numeroSerieNovo = event1.getNewValue();
            numeroSerie.setNumeroSerie(event1.getNewValue());
            atualizarBancoDeDados("numero_serie", event1.getNewValue(), numeroSerie.getId());
            registrarHistorico(usuarioLogado, "Edicao", "Alteracao do número de série de: " + numeroSerieAntigo + " para: " + numeroSerieNovo);
        });

        patrimonio.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        patrimonio.setOnEditCommit(event1 -> {
            Estoque patrimonio = event1.getRowValue();
            String nomePatrimonioAntigo = String.valueOf(patrimonio.getPatrimonio());
            String nomePatrimonioNovo = String.valueOf(event1.getNewValue());
            patrimonio.setPatrimonio(event1.getNewValue());
            atualizarBancoDeDados("patrimonio", event1.getNewValue(), patrimonio.getId());
            registrarHistorico(usuarioLogado, "Edicao", "Alteracao do nome patrimônio de: " + nomePatrimonioAntigo + " para: " + nomePatrimonioNovo);
        });

        quantidade.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        quantidade.setOnEditCommit(event1 -> {
            Estoque quantidade = event1.getRowValue();
            int quantidadeAntiga = quantidade.getQuantidade();
            int quantidadeNova = event1.getNewValue();
            quantidade.setQuantidade(event1.getNewValue());
            atualizarBancoDeDados("quantidade", event1.getNewValue(), quantidade.getId());
            registrarHistorico(usuarioLogado, "Edicao", "Alteracao da quantidade de: " + quantidadeAntiga + " para: " + quantidadeNova);
        });

        descricao.setCellFactory(TextFieldTableCell.forTableColumn());
        descricao.setOnEditCommit(event1 -> {
            Estoque descricao = event1.getRowValue();
            String nomeDescricaoAntigo = descricao.getDescricao();
            String nomeDescricaoNovo = event1.getNewValue();
            descricao.setDescricao(event1.getNewValue());
            atualizarBancoDeDados("descricao", event1.getNewValue(), descricao.getId());
            registrarHistorico(usuarioLogado, "Edicao", "Alteracao da descrição de: " + nomeDescricaoAntigo + " para: " + nomeDescricaoNovo);
        });
    }

    @FXML
    private void handleMainScreen(ActionEvent event) {
        editMode.setVisible(false);
        exitButton.setVisible(false);
        table.setEditable(false);
    }

    @FXML
    private void handleAddScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(MainController.class.getResource("/com/github/devgabrielc/model/view/AddScreen.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            logger.error("Erro ao tentar acessar a tela AddScreen.fxml | {}", e.getMessage());
            showAlertError("Erro!", "Não foi possível carregar a tela (Adicionar Material).");
        }
    }

    @FXML
    public void handleRemoveScreen(ActionEvent event) {
        Estoque itemSelecionado = table.getSelectionModel().getSelectedItem();

        if (itemSelecionado != null) {
            boolean sucesso = removerItemEstoqueNoBanco(itemSelecionado.getId());

            if (sucesso) {
                ((FilteredList<Estoque>) table.getItems()).getSource().remove(itemSelecionado);
                showAlertSuccess("Sucesso!", "Material removido com sucesso!");
                logger.info("Material removido com sucesso | {}", getTipoEquipamento(itemSelecionado.getId()));
            } else {
                showAlertError("Erro!", "Falha ao remover o material do banco de dados.");
            }
        } else {
            showAlertError("Erro!", "Selecione um material para remoção.");
        }
    }

    public boolean removerItemEstoqueNoBanco(int itemId) {
        String query = "DELETE FROM materiais WHERE id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, itemId);
            String materialRemovido = getTipoEquipamento(itemId);
            String marcaRemovida = getMarca(itemId);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                registrarHistorico(usuarioLogado, "Remocao", "Material removido: "
                                                                                        + materialRemovido
                                                                                        + ", Marca: "
                                                                                        + marcaRemovida);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Erro ao remover item do estoque | {}", e.getMessage());
        }
        return false;
    }

    @FXML
    private void handleEstoqueCSV(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {

                writer.write("id,Grupo Equipamento,Tipo Equipamento,Marca,Modelo,Numero de Serie,Quantidade,Patrimonio,Descricao");
                writer.newLine();

                for (Estoque equipamento : table.getItems()) {
                    writer.write(equipamento.getId()
                            + "," + equipamento.getGrupoEquipamento()
                            + "," + equipamento.getTipoEquipamento()
                            + "," + equipamento.getMarca()
                            + "," + equipamento.getModelo()
                            + "," + equipamento.getNumeroSerie()
                            + "," + equipamento.getQuantidade()
                            + "," + equipamento.getPatrimonio()
                            + "," + equipamento.getDescricao());
                    writer.newLine();
                }

                logger.info("CSV gerado por {}", usuarioLogado);
                showAlertSuccess("Sucesso!", "Arquivo Exportado com Sucesso!");
            } catch (IOException e) {
                logger.error("Erro ao gerar arquivo CSV | {}", e.getMessage());
                showAlertError("Erro!", "Falha ao Exportar o Arquivo CSV");
            }
        }
    }

    @FXML
    // Função para carregar os materiais do banco de dados
    private void carregarMateriaisComFiltro(TableView<Estoque> table) {
        ObservableList<Estoque> materiais = FXCollections.observableArrayList(table.getItems());
        FilteredList<Estoque> materiaisFiltrados = new FilteredList<>(materiais, _ -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            materiaisFiltrados.setPredicate(material -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();

                return material.getGrupoEquipamento().toLowerCase().contains(lowerCaseFilter) ||
                        material.getTipoEquipamento().toLowerCase().contains(lowerCaseFilter) ||
                        material.getMarca().toLowerCase().contains(lowerCaseFilter)           ||
                        material.getModelo().toLowerCase().contains(lowerCaseFilter)          ||
                        material.getNumeroSerie().toLowerCase().contains(lowerCaseFilter)     ||
                        material.getDescricao().toLowerCase().contains(lowerCaseFilter);
            });
        });

        String query = "SELECT * FROM materiais";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            materiais.clear();

            while (rs.next()) {
                int id = rs.getInt("id");
                String grupoEquipamento = rs.getString("grupo_equipamento");
                String tipoEquipamento = rs.getString("tipo_equipamento");
                String marca = rs.getString("marca");
                String modelo = rs.getString("modelo");
                String numeroSerie = rs.getString("numero_serie");
                int quantidade = rs.getInt("quantidade");
                int patrimonio = rs.getInt("patrimonio");
                String descricao = rs.getString("descricao");

                Estoque material = new Estoque(id, grupoEquipamento, tipoEquipamento, marca, modelo, numeroSerie, quantidade, patrimonio, descricao);
                materiais.add(material);
            }
        } catch (SQLException e) {
            logger.error("Erro ao carregar materiais | {}", e.getMessage());
        }

        table.setItems(materiaisFiltrados);
    }

    @FXML
    private void exibirSaudacao() throws SQLException {
        LocalTime horaAtual = LocalTime.now();
        String saudacao;

        // Verifica o horário do dia
        if (horaAtual.isBefore(LocalTime.NOON)) {
            saudacao = "Bom dia, ";
        } else if (horaAtual.isBefore(LocalTime.of(17, 59))) {
            saudacao = "Boa tarde, ";
        } else {
            saudacao = "Boa noite, ";
        }

        String username = String.valueOf(LoginController.usuarioLogado);
        saudacaoText.setText(saudacao + username + "!");
    }

    // Exibir a data atual na tela principal
    @FXML
    private void exibirDataAtual() {
        LocalDate dataAtual = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy", Locale.forLanguageTag("pt-BR"));

        String dataFormatada = dataAtual.format(formatter);
        String diaDaSemana = dataFormatada.substring(0, 1).toUpperCase() + dataFormatada.substring(1);

        dataAtualText.setText(diaDaSemana);
    }

    private String getTipoEquipamento(int itemId) {
        String tipoEquipamento = "";
        String query = "SELECT tipo_equipamento FROM materiais WHERE id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, itemId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                tipoEquipamento = rs.getString("tipo_equipamento");
            }
        } catch (SQLException e) {
            logger.error("Erro ao encontrar um tipo equipamento | {}", e.getMessage());
        }
        return tipoEquipamento;
    }

    private String getMarca(int itemId) {
        String marca = "";
        String query = "SELECT marca FROM materiais WHERE id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, itemId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                marca = rs.getString("marca");
            }
        } catch (SQLException e) {
            logger.error("Erro ao encontrar uma marca | {}", e.getMessage());
        }
        return marca;
    }
}