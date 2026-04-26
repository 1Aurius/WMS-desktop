package com.example.wmsdsktp.Controllers;

import com.example.wmsdsktp.Responses.StockProductResponse;
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
import javafx.scene.control.CheckBox;
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

public class StockPageController {

    @FXML private TextField queryField;
    @FXML private TextField postoIdField;
    @FXML private TextField minQtyField;
    @FXML private TextField maxQtyField;
    @FXML private CheckBox inStockOnlyCheck;
    @FXML private Label resultCountLabel;

    @FXML private TableView<StockProductResponse> productsTable;
    @FXML private TableColumn<StockProductResponse, Integer> productIdCol;
    @FXML private TableColumn<StockProductResponse, String> productNameCol;
    @FXML private TableColumn<StockProductResponse, String> serialCodeCol;
    @FXML private TableColumn<StockProductResponse, String> unitCol;
    @FXML private TableColumn<StockProductResponse, Integer> quantityCol;
    @FXML private TableColumn<StockProductResponse, String> postoCol;

    private final ObjectMapper mapper = new ObjectMapper();

    @FXML
    public void initialize() {
        productIdCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().productId()).asObject());
        productNameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().productName()));
        serialCodeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().serialCode()));
        unitCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().unidade()));
        quantityCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().quantity()).asObject());
        postoCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().postoDesignacao()));

        loadStock();
    }

    private String buildEndpoint() {
        List<String> params = new ArrayList<>();
        String query = valueOrNull(queryField.getText());
        String postoId = valueOrNull(postoIdField.getText());
        String minQty = valueOrNull(minQtyField.getText());
        String maxQty = valueOrNull(maxQtyField.getText());

        if (query != null) params.add("query=" + URLEncoder.encode(query, StandardCharsets.UTF_8));
        if (postoId != null) params.add("postoId=" + postoId);
        if (minQty != null) params.add("minQty=" + minQty);
        if (maxQty != null) params.add("maxQty=" + maxQty);
        params.add("inStockOnly=" + inStockOnlyCheck.isSelected());

        if (params.isEmpty()) return "stocks/products";
        return "stocks/products?" + String.join("&", params);
    }

    private void loadStock() {
        try {
            String endpoint = buildEndpoint();
            System.out.println("Fetching: " + endpoint);

            HttpResponse<String> response = RequestLauncher.newRequest(endpoint, "GET", null);
            System.out.println("Status: " + response.statusCode());
            System.out.println("Body: " + response.body());

            if (response == null || response.statusCode() != 200) return;

            List<StockProductResponse> stocks = mapper.readValue(response.body(), new TypeReference<>() {});
            productsTable.setItems(FXCollections.observableArrayList(stocks));
            resultCountLabel.setText(stocks.size() + " result(s)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String valueOrNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }

    @FXML
    private void onApplyFilters() {
        loadStock();
    }

    @FXML
    private void onClearFilters() {
        queryField.clear();
        postoIdField.clear();
        minQtyField.clear();
        maxQtyField.clear();
        inStockOnlyCheck.setSelected(true);
        loadStock();
    }

    @FXML
    private void openCreateNewStockModal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/wmsdsktp/forms/create-new-stock-modal.fxml"));
            Parent root = loader.load();

            Stage modal = new Stage();
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setTitle("New Stock");
            modal.setScene(new Scene(root));
            modal.showAndWait();

            loadStock();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}