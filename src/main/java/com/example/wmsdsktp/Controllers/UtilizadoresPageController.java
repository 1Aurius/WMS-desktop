package com.example.wmsdsktp.Controllers;

import com.example.wmsdsktp.Responses.UtilizadorResponse;
import com.example.wmsdsktp.util.RequestLauncher;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URLEncoder;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class UtilizadoresPageController {

    @FXML private TextField queryField;
    @FXML private Label resultCountLabel;

    @FXML private TableView<UtilizadorResponse> utilizadoresTable;
    @FXML private TableColumn<UtilizadorResponse, Integer> idCol;
    @FXML private TableColumn<UtilizadorResponse, String> nomeCol;
    @FXML private TableColumn<UtilizadorResponse, String> dnCol;
    @FXML private TableColumn<UtilizadorResponse, Boolean> isAdminCol;
    @FXML private TableColumn<UtilizadorResponse, Boolean> isGestorRotasCol;
    @FXML private TableColumn<UtilizadorResponse, Boolean> isGestorCol;
    @FXML private TableColumn<UtilizadorResponse, Boolean> isLojaCol;
    @FXML private TableColumn<UtilizadorResponse, Boolean> isArmazemCol;

    private final ObjectMapper mapper = new ObjectMapper();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().id()).asObject());
        nomeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().username()));
        dnCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().dn()));
        isAdminCol.setCellValueFactory(data -> new SimpleBooleanProperty(Boolean.TRUE.equals(data.getValue().admin())).asObject());
        isGestorRotasCol.setCellValueFactory(data -> new SimpleBooleanProperty(Boolean.TRUE.equals(data.getValue().gestorRotas())).asObject());
        isGestorCol.setCellValueFactory(data -> new SimpleBooleanProperty(Boolean.TRUE.equals(data.getValue().gestor())).asObject());
        isLojaCol.setCellValueFactory(data -> new SimpleBooleanProperty(Boolean.TRUE.equals(data.getValue().loja())).asObject());
        isArmazemCol.setCellValueFactory(data -> new SimpleBooleanProperty(Boolean.TRUE.equals(data.getValue().armazem())).asObject());
        load();
    }

    private String buildEndpoint() {
        List<String> params = new ArrayList<>();
        String query = valueOrNull(queryField.getText());

        if (query != null) params.add("query=" + URLEncoder.encode(query, StandardCharsets.UTF_8));

        if (params.isEmpty()) return "users";
        return "users?" + String.join("&", params);
    }

    private void load() {
        try {
            HttpResponse<String> response = RequestLauncher.newRequest(buildEndpoint(), "GET", null);
            if (response == null || response.statusCode() != 200) return;
            System.out.println(response.body());

            List<UtilizadorResponse> utilizadores = mapper.readValue(response.body(), new TypeReference<>() {});
            utilizadoresTable.setItems(FXCollections.observableArrayList(utilizadores));
            resultCountLabel.setText(utilizadores.size() + " resultado(s)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String valueOrNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }

    @FXML
    private void onApplyFilters() {
        load();
    }

    @FXML
    private void onClearFilters() {
        queryField.clear();
        load();
    }

    @FXML
    private void openCreateModal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/wmsdsktp/forms/create-utilizador-modal.fxml"));
            Parent root = loader.load();

            Stage modal = new Stage();
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setTitle("Novo Utilizador");
            modal.setScene(new Scene(root));
            modal.showAndWait();

            load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openEditModal() {
        UtilizadorResponse selected = utilizadoresTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/wmsdsktp/forms/edit-utilizador-modal.fxml"));
            Parent root = loader.load();

            EditUtilizadorModalController controller = loader.getController();
            controller.initData(selected);

            Stage modal = new Stage();
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setTitle("Editar Permissões");
            modal.setScene(new Scene(root));
            modal.showAndWait();

            load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}