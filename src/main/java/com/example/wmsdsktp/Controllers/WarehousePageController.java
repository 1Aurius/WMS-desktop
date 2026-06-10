package com.example.wmsdsktp.Controllers;

import com.example.wmsdsktp.Responses.PostoResponse;
import com.example.wmsdsktp.util.RequestLauncher;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.net.URLEncoder;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class WarehousePageController {

    @FXML private TextField searchField;
    @FXML private Label resultCountLabel;

    @FXML private TableView<PostoResponse> warehousesTable;
    @FXML private TableColumn<PostoResponse, Integer> idCol;
    @FXML private TableColumn<PostoResponse, String> designacaoCol;
    @FXML private TableColumn<PostoResponse, String> localCol;
    @FXML private TableColumn<PostoResponse, String> cpCol;
    @FXML private TableColumn<PostoResponse, String> tipoCol;
    @FXML private TableColumn<PostoResponse, Integer> gerenteCol;

    private final ObjectMapper mapper = new ObjectMapper();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().id()).asObject());
        designacaoCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().designacao()));
        localCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().local()));
        cpCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().cp()));
        tipoCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().tipo()));
        gerenteCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().gerenteId() == null ? 0 : data.getValue().gerenteId()).asObject());

        loadWarehouses();
    }

    private void loadWarehouses() {
        try {
            String endpoint = "postos";
            String query = searchField.getText();
            if (query != null && !query.isBlank()) {
                endpoint += "?query=" + URLEncoder.encode(query.trim(), StandardCharsets.UTF_8);
            }

            HttpResponse<String> response = RequestLauncher.newRequest(endpoint, "GET", null);
            if (response == null || response.statusCode() != 200) return;

            List<PostoResponse> postos = mapper.readValue(response.body(), new TypeReference<>() {});
            warehousesTable.setItems(FXCollections.observableArrayList(postos));
            resultCountLabel.setText(postos.size() + " armazéns");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onSearch() {
        loadWarehouses();
    }

    @FXML
    private void onClear() {
        searchField.clear();
        loadWarehouses();
    }

    @FXML
    private void onNewWarehouse() {
        // Modal for new warehouse can be added here
        System.out.println("New warehouse action");
    }
}
