package com.example.wmsdsktp.Controllers;

import com.example.wmsdsktp.Responses.PostoResponse;
import com.example.wmsdsktp.Responses.ProdutoResponse;
import com.example.wmsdsktp.Responses.StockProductResponse;
import com.example.wmsdsktp.util.RequestLauncher;
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

public class EditStockModalController {

    @FXML private ComboBox<PostoResponse> postoComboBox;

    @FXML private ComboBox<ProdutoResponse> produtoComboBox;

    @FXML private TextField quantityField;

    private final ObjectMapper mapper = new ObjectMapper();
    private final PostoController postoController = new PostoController();
    private final ProductController produtoController = new ProductController();
    
    private Integer stockId;
    private Integer initialProdutoId;
    private Integer initialPostoId;

    @FXML
    public void initialize() {
        List<PostoResponse> postos = postoController.getAll();
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
    }

    public void initData(StockProductResponse stock) {
        this.stockId = stock.stockId();
        this.initialProdutoId = stock.productId();
        this.initialPostoId = stock.postoId();
        this.quantityField.setText(String.valueOf(stock.quantity()));
        
        // Select matching items in comboboxes
        for (ProdutoResponse p : produtoComboBox.getItems()) {
            if (p != null && p.id().equals(stock.productId())) {
                produtoComboBox.getSelectionModel().select(p);
                break;
            }
        }
        
        for (PostoResponse p : postoComboBox.getItems()) {
            if (p != null && p.id().equals(stock.postoId())) {
                postoComboBox.getSelectionModel().select(p);
                break;
            }
        }
    }

    public void handleSave(ActionEvent actionEvent) throws Exception {
        ProdutoResponse selectedProduto = produtoComboBox.getValue();
        PostoResponse selectedPosto = postoComboBox.getValue();
        String quantidadeText = quantityField.getText();

        // Fallback to initial values if comboboxes are disabled and not populated fully at time of init
        Long pId = selectedProduto != null ? Long.parseLong(String.valueOf(selectedProduto.id())) : (initialProdutoId != null ? initialProdutoId.longValue() : null);
        Long pstId = selectedPosto != null ? Long.parseLong(String.valueOf(selectedPosto.id())) : (initialPostoId != null ? initialPostoId.longValue() : null);

        if (pId == null || pstId == null || quantidadeText.isBlank()) {
            // validation error
            return;
        }

        String json = new ObjectMapper().writeValueAsString(Map.of(
                "produtoId", pId,
                "postoId", pstId,
                "quantidade", Integer.parseInt(quantidadeText)
        ));
        
        HttpResponse<String> response = RequestLauncher.newRequest("stocks/" + stockId, "PUT", json);

        if (response != null && response.statusCode() == 200) {
            closeModal();
        }
    }

    public void handleCancel(ActionEvent actionEvent) {
        closeModal();
    }
    
    private void closeModal() {
        Stage stage = (Stage) quantityField.getScene().getWindow();
        stage.close();
    }
}
