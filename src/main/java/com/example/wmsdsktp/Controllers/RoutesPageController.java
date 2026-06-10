package com.example.wmsdsktp.Controllers;

import com.example.wmsdsktp.Responses.RotaResponse;
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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;

public class RoutesPageController {

    @FXML private TextField searchField;
    @FXML private Label resultCountLabel;
    @FXML private TableView<RotaResponse> routesTable;
    @FXML private TableColumn<RotaResponse, Integer> idCol;
    @FXML private TableColumn<RotaResponse, String> nomeCol;
    @FXML private TableColumn<RotaResponse, String> estadoCol;
    @FXML private TableColumn<RotaResponse, String> veiculoCol;
    @FXML private TableColumn<RotaResponse, String> equipaCol;
    @FXML private TableColumn<RotaResponse, Void> acoesCol;

    private final ObjectMapper mapper = new ObjectMapper();
    private List<RotaResponse> allRoutes;

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().id()).asObject());
        nomeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().nome()));
        estadoCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().estado()));
        veiculoCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().veiculoMatricula() != null ? data.getValue().veiculoMatricula() : "N/D"));
        equipaCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().equipaDesignacao() != null ? data.getValue().equipaDesignacao() : "N/D"));

        acoesCol.setCellFactory(col -> new javafx.scene.control.TableCell<>() {
            private final javafx.scene.control.Button btn = new javafx.scene.control.Button("Gerir");

            {
                btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #3b82f6; -fx-cursor: hand; -fx-font-weight: bold;");
                btn.setOnAction(event -> {
                    RotaResponse rota = getTableView().getItems().get(getIndex());
                    openManageModal(rota);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });

        loadRoutes();
    }

    private void loadRoutes() {
        try {
            HttpResponse<String> response = RequestLauncher.newRequest("rotas", "GET", null);
            if (response == null || response.statusCode() != 200) return;

            allRoutes = mapper.readValue(response.body(), new TypeReference<>() {});
            updateTable(allRoutes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTable(List<RotaResponse> routes) {
        routesTable.setItems(FXCollections.observableArrayList(routes));
        resultCountLabel.setText(routes.size() + " rotas");
    }

    @FXML
    private void onSearch() {
        if (allRoutes == null) return;
        String query = searchField.getText();
        if (query == null || query.isBlank()) {
            updateTable(allRoutes);
            return;
        }

        String lowerQuery = query.toLowerCase();
        List<RotaResponse> filtered = allRoutes.stream()
                .filter(r -> (r.nome() != null && r.nome().toLowerCase().contains(lowerQuery)) ||
                             (r.estado() != null && r.estado().toLowerCase().contains(lowerQuery)))
                .collect(Collectors.toList());
        
        updateTable(filtered);
    }

    @FXML
    private void onClear() {
        searchField.clear();
        if (allRoutes != null) {
            updateTable(allRoutes);
        }
    }

    @FXML
    private void onNewRoute() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/example/wmsdsktp/forms/create-rota-modal.fxml"));
            javafx.scene.Parent root = loader.load();

            Stage modal = new Stage();
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setTitle("Nova Rota");
            modal.setScene(new javafx.scene.Scene(root));
            modal.showAndWait();

            loadRoutes();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openManageModal(RotaResponse rota) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/example/wmsdsktp/forms/manage-rota-modal.fxml"));
            javafx.scene.Parent root = loader.load();

            ManageRotaController controller = loader.getController();
            controller.setRota(rota);

            Stage modal = new Stage();
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setTitle("Gerir Rota");
            modal.setScene(new javafx.scene.Scene(root));
            modal.showAndWait();

            loadRoutes();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
