package com.example.wmsdsktp.Controllers;

import com.example.wmsdsktp.Responses.RequesicaoResponse;
import com.example.wmsdsktp.Responses.RequesicaoStockResponse;
import com.example.wmsdsktp.Responses.StockProductResponse;
import com.example.wmsdsktp.util.RequestLauncher;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

public class CreateRequesicaoController {

    @FXML private ComboBox<RequesicaoResponse> requesicaoStockComboBox;
    @FXML private ComboBox<StockProductResponse> stockComboBox;
    @FXML private TextField quantField;

    private final ObjectMapper mapper = new ObjectMapper();

    @FXML
    public void initialize() {
        loadRequesicaoStocks();
        loadStocks();
    }

    private void loadRequesicaoStocks() {
        try {
            HttpResponse<String> response = RequestLauncher.newRequest("requesicoes-stock", "GET", null);
            if (response == null || response.statusCode() != 200) return;

            List<RequesicaoResponse> list = mapper.readValue(response.body(), new TypeReference<>() {});
            requesicaoStockComboBox.getItems().add(null);
            requesicaoStockComboBox.getItems().addAll(list);
            requesicaoStockComboBox.setPromptText("Selecionar requisição stock...");

            stockComboBox.setCellFactory(lv -> new ListCell<StockProductResponse>() {
                @Override
                protected void updateItem(StockProductResponse item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.productName() + " - " + item.postoDesignacao());
                }
            });
            stockComboBox.setButtonCell(new ListCell<StockProductResponse>() {
                @Override
                protected void updateItem(StockProductResponse item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.productName() + " - " + item.postoDesignacao());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadStocks() {
        try {
            HttpResponse<String> response = RequestLauncher.newRequest("stocks/products", "GET", null);
            if (response == null || response.statusCode() != 200) return;

            List<StockProductResponse> list = mapper.readValue(response.body(), new TypeReference<>() {});
            stockComboBox.getItems().add(null);
            stockComboBox.getItems().addAll(list);
            stockComboBox.setPromptText("Selecionar stock...");

            stockComboBox.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(StockProductResponse item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.productName() + " - " + item.postoDesignacao());
                }
            });
            stockComboBox.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(StockProductResponse item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.productName() + " - " + item.postoDesignacao());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSave() {
        try {
            RequesicaoResponse selectedReqStock = requesicaoStockComboBox.getValue();
            StockProductResponse selectedStock = stockComboBox.getValue();
            String quantText = quantField.getText();

            if (selectedReqStock == null || selectedStock == null || quantText.isBlank()) return;

            String json = mapper.writeValueAsString(Map.of(
                    "requesicaoStockId", selectedReqStock.id(),
                    "stockId", selectedStock.stockId(),
                    "quant", Integer.parseInt(quantText)
            ));

            HttpResponse<String> response = RequestLauncher.newRequest("requesicoes", "POST", json);

            if (response != null && response.statusCode() == 201) {
                closeModal();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        closeModal();
    }

    private void closeModal() {
        Stage stage = (Stage) quantField.getScene().getWindow();
        stage.close();
    }
}