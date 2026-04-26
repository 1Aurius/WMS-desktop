package com.example.wmsdsktp.Controllers;

import com.example.wmsdsktp.Responses.RequesicaoResponse;
import com.example.wmsdsktp.Responses.RequesicaoStockResponse;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.http.HttpResponse;
import java.util.List;

public class RequisicaoPageController {

    @FXML private Label resultCountLabel;

    @FXML private TableView<RequesicaoResponse> requisicaoTable;
    @FXML private TableColumn<RequesicaoResponse, Integer> idCol;
    @FXML private TableColumn<RequesicaoResponse, Integer> requesicaoStockIdCol;
    @FXML private TableColumn<RequesicaoResponse, Integer> stockIdCol;
    @FXML private TableColumn<RequesicaoResponse, String> productNameCol;
    @FXML private TableColumn<RequesicaoResponse, String> postoDesignacaoCol;
    @FXML private TableColumn<RequesicaoResponse, Integer> quantCol;

    private final ObjectMapper mapper = new ObjectMapper();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().id()).asObject());
        requesicaoStockIdCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().requesicaoStockId()).asObject());
        stockIdCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().stockId()).asObject());
        productNameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().productName()));
        postoDesignacaoCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().postoDesignacao()));
        quantCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().quant()).asObject());

        load();
    }

    private void load() {
        try {
            HttpResponse<String> response = RequestLauncher.newRequest("requesicoes", "GET", null);
            if (response == null || response.statusCode() != 200) return;

            List<RequesicaoStockResponse> requisicoes = mapper.readValue(response.body(), new TypeReference<>() {});
            requisicaoTable.setItems(FXCollections.observableArrayList());
            resultCountLabel.setText(requisicoes.size() + " resultado(s)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openCreateModal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/wmsdsktp/forms/create-requesicao-modal.fxml"));
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
}