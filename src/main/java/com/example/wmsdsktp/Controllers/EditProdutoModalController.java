package com.example.wmsdsktp.Controllers;

import com.example.wmsdsktp.Responses.ProdutoResponse;
import com.example.wmsdsktp.auth.UserSession;
import com.example.wmsdsktp.util.RequestLauncher;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.http.HttpResponse;
import java.util.Map;

public class EditProdutoModalController {

    @FXML private TextField serialCodeField;
    @FXML private TextField nomeField;
    @FXML private TextField unidadeField;
    @FXML private TextField precoField;
    @FXML private TextField aproxVolumeField;

    private final ObjectMapper mapper = new ObjectMapper();
    private Integer produtoId;

    public void initData(ProdutoResponse produto) {
        this.produtoId = produto.id();
        serialCodeField.setText(produto.serialCode());
        nomeField.setText(produto.nome());
        unidadeField.setText(produto.unidade());
        precoField.setText(produto.preco() != null ? String.valueOf(produto.preco()) : "");
        aproxVolumeField.setText(produto.aproxVolume() != null ? String.valueOf(produto.aproxVolume()) : "");
    }

    @FXML
    private void handleSave() {
        try {
            if (nomeField.getText().isBlank() || serialCodeField.getText().isBlank()) return;

            String json = mapper.writeValueAsString(Map.of(
                    "serialCode", serialCodeField.getText(),
                    "nome", nomeField.getText(),
                    "unidade", unidadeField.getText(),
                    "preco", Double.parseDouble(precoField.getText()),
                    "aproxVolume", Double.parseDouble(aproxVolumeField.getText()),
                    "utilizadorCriadorId", UserSession.getInstance().getId()
            ));

            HttpResponse<String> response = RequestLauncher.newRequest("produtos/" + produtoId, "PUT", json);

            if (response != null && response.statusCode() == 200) {
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
        Stage stage = (Stage) nomeField.getScene().getWindow();
        stage.close();
    }
}
