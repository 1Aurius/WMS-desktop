package com.example.wmsdsktp.Controllers;

import com.example.wmsdsktp.auth.UserSession;
import com.example.wmsdsktp.util.RequestLauncher;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.http.HttpResponse;
import java.util.Map;

public class CreateNewProdutoModalController {

    @FXML private TextField serialCodeField;
    @FXML private TextField nomeField;
    @FXML private TextField unidadeField;
    @FXML private TextField precoField;
    @FXML private TextField aproxVolumeField;

    private final ObjectMapper mapper = new ObjectMapper();

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

            HttpResponse<String> response = RequestLauncher.newRequest("produtos", "POST", json);

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
        Stage stage = (Stage) nomeField.getScene().getWindow();
        stage.close();
    }
}