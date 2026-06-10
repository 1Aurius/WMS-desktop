package com.example.wmsdsktp.Controllers;

import com.example.wmsdsktp.Responses.RequesicaoStockHeaderResponse;
import com.example.wmsdsktp.auth.UserSession;
import com.example.wmsdsktp.util.RequestLauncher;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.http.HttpResponse;
import java.util.List;

public class RequisicaoPageController {

    @FXML private Label resultCountLabel;
    @FXML private Button btnAprovar;
    @FXML private Button btnRejeitar;

    @FXML private TableView<RequesicaoStockHeaderResponse> requisicaoTable;
    @FXML private TableColumn<RequesicaoStockHeaderResponse, Integer> idCol;
    @FXML private TableColumn<RequesicaoStockHeaderResponse, String> dhRegistoCol;
    @FXML private TableColumn<RequesicaoStockHeaderResponse, String> estadoCol;
    @FXML private TableColumn<RequesicaoStockHeaderResponse, String> descricaoCol;
    @FXML private TableColumn<RequesicaoStockHeaderResponse, String> criadorCol;
    @FXML private TableColumn<RequesicaoStockHeaderResponse, String> validadorCol;
    @FXML private TableColumn<RequesicaoStockHeaderResponse, String> dhValidacaoCol;

    private final ObjectMapper mapper = new ObjectMapper();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().id()).asObject());
        dhRegistoCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().dhRegisto()));
        estadoCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().estado()));
        descricaoCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().descricao()));
        criadorCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().utilizadorCriadorUsername()));
        validadorCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().utilizadorValidadorUsername()));
        dhValidacaoCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().dhValidacao()));

        requisicaoTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean isManager = UserSession.getInstance().isAdmin() || UserSession.getInstance().isArmazem();
            boolean hasSelection = newSelection != null;
            btnAprovar.setDisable(!hasSelection || !isManager);
            btnRejeitar.setDisable(!hasSelection || !isManager);
        });

        load();
    }

    private void load() {
        try {
            HttpResponse<String> response = RequestLauncher.newRequest("requesicoes-stock/headers", "GET", null);
            if (response == null || response.statusCode() != 200) return;

            List<RequesicaoStockHeaderResponse> requisicoes = mapper.readValue(response.body(), new TypeReference<>() {});
            requisicaoTable.setItems(FXCollections.observableArrayList(requisicoes));
            resultCountLabel.setText(requisicoes.size() + " resultado(s)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openCreateModal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/wmsdsktp/forms/create-requesicao-header-modal.fxml"));
            Parent root = loader.load();

            Stage modal = new Stage();
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setTitle("Nova Requisição");
            modal.setScene(new Scene(root));
            modal.showAndWait();

            load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void approveSelected() {
        changeStatus("APROVADO");
    }

    @FXML
    private void rejectSelected() {
        changeStatus("REJEITADO");
    }

    private void changeStatus(String status) {
        RequesicaoStockHeaderResponse selected = requisicaoTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        
        Long userId = UserSession.getInstance().getId();

        try {
            HttpResponse<String> response = RequestLauncher.newRequest(
                    "requesicoes-stock/headers/" + selected.id() + "/validate?validadorId=" + userId + "&status=" + status,
                    "PUT", null);
            if (response != null && response.statusCode() == 200) {
                load();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}