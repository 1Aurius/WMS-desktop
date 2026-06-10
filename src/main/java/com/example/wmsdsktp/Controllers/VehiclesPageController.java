package com.example.wmsdsktp.Controllers;

import com.example.wmsdsktp.Responses.VeiculoResponse;
import com.example.wmsdsktp.util.RequestLauncher;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;

public class VehiclesPageController {

    @FXML private TextField searchField;
    @FXML private Label resultCountLabel;

    @FXML private TableView<VeiculoResponse> vehiclesTable;
    @FXML private TableColumn<VeiculoResponse, Integer> idCol;
    @FXML private TableColumn<VeiculoResponse, String> matriculaCol;
    @FXML private TableColumn<VeiculoResponse, String> modeloCol;
    @FXML private TableColumn<VeiculoResponse, String> anoCol;
    @FXML private TableColumn<VeiculoResponse, Double> volumeCol;
    @FXML private TableColumn<VeiculoResponse, String> postoCol;

    private final ObjectMapper mapper = new ObjectMapper();
    private List<VeiculoResponse> allVehicles;

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().id()).asObject());
        matriculaCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().matricula()));
        modeloCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().modeloDesignacao() != null ? data.getValue().modeloDesignacao() : "N/D"));
        anoCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().ano() != null ? data.getValue().ano() : "N/D"));
        volumeCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().aproxVolume() != null ? data.getValue().aproxVolume() : 0.0).asObject());
        postoCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().postoDesignacao() != null ? data.getValue().postoDesignacao() : "Sem Posto"));

        loadVehicles();
    }

    private void loadVehicles() {
        try {
            HttpResponse<String> response = RequestLauncher.newRequest("veiculos", "GET", null);
            if (response == null || response.statusCode() != 200) return;

            allVehicles = mapper.readValue(response.body(), new TypeReference<>() {});
            updateTable(allVehicles);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTable(List<VeiculoResponse> vehicles) {
        vehiclesTable.setItems(FXCollections.observableArrayList(vehicles));
        resultCountLabel.setText(vehicles.size() + " veículos");
    }

    @FXML
    private void onSearch() {
        if (allVehicles == null) return;
        String query = searchField.getText();
        if (query == null || query.isBlank()) {
            updateTable(allVehicles);
            return;
        }

        String lowerQuery = query.toLowerCase();
        List<VeiculoResponse> filtered = allVehicles.stream()
                .filter(v -> (v.matricula() != null && v.matricula().toLowerCase().contains(lowerQuery)) ||
                             (v.modeloDesignacao() != null && v.modeloDesignacao().toLowerCase().contains(lowerQuery)))
                .collect(Collectors.toList());
        
        updateTable(filtered);
    }

    @FXML
    private void onClear() {
        searchField.clear();
        if (allVehicles != null) {
            updateTable(allVehicles);
        }
    }

    @FXML
    private void onNewVehicle() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/example/wmsdsktp/forms/create-veiculo-modal.fxml"));
            javafx.scene.Parent root = loader.load();

            javafx.stage.Stage modal = new javafx.stage.Stage();
            modal.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            modal.setTitle("Novo Veículo");
            modal.setScene(new javafx.scene.Scene(root));
            modal.showAndWait();

            loadVehicles();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
