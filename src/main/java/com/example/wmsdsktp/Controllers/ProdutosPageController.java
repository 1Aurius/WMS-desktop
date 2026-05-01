package com.example.wmsdsktp.Controllers;

import com.example.wmsdsktp.Responses.ProdutoResponse;
import com.example.wmsdsktp.util.RequestLauncher;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.SimpleDoubleProperty;
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

import java.net.http.HttpResponse;
import java.util.List;

public class ProdutosPageController {

    @FXML private TextField queryField;
    @FXML private Label resultCountLabel;

    @FXML private TableView<ProdutoResponse> productsTable;
    @FXML private TableColumn<ProdutoResponse, Integer> idCol;
    @FXML private TableColumn<ProdutoResponse, String> nomeCol;
    @FXML private TableColumn<ProdutoResponse, String> serialCodeCol;
    @FXML private TableColumn<ProdutoResponse, String> unidadeCol;
    @FXML private TableColumn<ProdutoResponse, Double> precoCol;
    @FXML private TableColumn<ProdutoResponse, Double> aproxVolumeCol;

    private final ObjectMapper mapper = new ObjectMapper();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().id()).asObject());
        nomeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().nome()));
        serialCodeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().serialCode()));
        unidadeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().unidade()));
        precoCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().preco()).asObject());
        aproxVolumeCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().aproxVolume()).asObject());

        loadProdutos(null);
    }

    private void loadProdutos(String query) {
        try {
            String url = "produtos" + (query != null && !query.isBlank() ? "?query=" + query : "");
            HttpResponse<String> response = RequestLauncher.newRequest(url, "GET", null);
            if (response == null || response.statusCode() != 200) return;

            List<ProdutoResponse> produtos = mapper.readValue(response.body(), new TypeReference<>() {});
            productsTable.setItems(FXCollections.observableArrayList(produtos));
            resultCountLabel.setText(produtos.size() + " result(s)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onApplyFilters() {
        loadProdutos(queryField.getText());
    }

    @FXML
    private void onClearFilters() {
        queryField.clear();
        loadProdutos(null);
    }

    @FXML
    private void openCreateNewProductModal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/wmsdsktp/forms/create-produto-modal.fxml"));

            Parent root = loader.load();

            Stage modal = new Stage();
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setTitle("New Product");
            modal.setScene(new Scene(root));

            modal.showAndWait();
            loadProdutos(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}