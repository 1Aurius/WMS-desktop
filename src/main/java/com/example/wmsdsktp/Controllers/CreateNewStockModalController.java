package com.example.wmsdsktp.Controllers;

import com.example.wmsdsktp.Responses.PostoResponse;
import com.example.wmsdsktp.Responses.ProdutoResponse;
import com.example.wmsdsktp.util.RequestLauncher;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

public class CreateNewStockModalController {

    @FXML private ComboBox<PostoResponse> postoComboBox;

    @FXML private ComboBox<ProdutoResponse> produtoComboBox;

    @FXML private TextField quantityField;

    private final ObjectMapper mapper = new ObjectMapper();
    private final PostoController postoController = new PostoController();
    private final ProductController produtoController = new ProductController();

    @FXML
    public void initialize() {

        List postos = postoController.getAll();
        postoComboBox.getItems().add(null);
        postoComboBox.getItems().addAll(postos);
        postoComboBox.setPromptText("Selecionar posto...");

        postoComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(PostoResponse posto, boolean empty) {
                super.updateItem(posto, empty);
                setText(empty || posto == null ? null : posto.designacao());
            }
        });

        postoComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(PostoResponse posto, boolean empty) {
                super.updateItem(posto, empty);
                setText(empty || posto == null ? null : posto.designacao());
            }
        });

        List<ProdutoResponse> produtos = produtoController.getAll();
        produtoComboBox.getItems().add(null);
        produtoComboBox.getItems().addAll(produtos);
        produtoComboBox.setPromptText("Selecionar produto...");

        produtoComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(ProdutoResponse produto, boolean empty) {
                super.updateItem(produto, empty);
                setText(empty || produto == null ? null : produto.nome());
            }
        });

        produtoComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(ProdutoResponse produto, boolean empty) {
                super.updateItem(produto, empty);
                setText(empty || produto == null ? null : produto.nome());
            }
        });
    };

    public void handleSave(ActionEvent actionEvent) throws Exception {
        ProdutoResponse selectedProduto = produtoComboBox.getValue();
        PostoResponse selectedPosto = postoComboBox.getValue();
        String quantidadeText = quantityField.getText();

        if (selectedProduto == null || selectedPosto == null || quantidadeText.isBlank()) {
            // show validation error
            return;
        }

        String json = new ObjectMapper().writeValueAsString(Map.of(
                "produtoId", Long.parseLong(String.valueOf(selectedProduto.id())),
                "postoId", Long.parseLong(String.valueOf(selectedPosto.id())),
                "quantidade", Integer.parseInt(quantidadeText)
        ));
        System.out.println(json);
        HttpResponse<String> response = RequestLauncher.newRequest("stocks", "POST", json);

        if (response != null && response.statusCode() == 201) {
            Stage stage = (Stage) produtoComboBox.getScene().getWindow();
            stage.close();
        }
    }

    public void handleCancel(ActionEvent actionEvent) {

    }
}
