package com.example.wmsdsktp.Controllers;

import com.example.wmsdsktp.auth.UserSession;
import com.example.wmsdsktp.util.RequestLauncher;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.net.http.HttpResponse;
import java.util.Map;

public class CreateRequesicaoHeaderController {

    @FXML private TextArea descricaoField;
    private final ObjectMapper mapper = new ObjectMapper();

    @FXML
    private void handleSave() {
        try {
            String descricao = descricaoField.getText();

            if (descricao == null || descricao.isBlank()) return;

            String json = mapper.writeValueAsString(Map.of(
                    "descricao", descricao,
                    "utilizadorCriadorId", UserSession.getInstance().getId()
            ));

            HttpResponse<String> response = RequestLauncher.newRequest("requesicoes-stock/headers", "POST", json);

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
        Stage stage = (Stage) descricaoField.getScene().getWindow();
        stage.close();
    }
}
