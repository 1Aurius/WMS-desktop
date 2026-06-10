package com.example.wmsdsktp.Controllers;

import com.example.wmsdsktp.util.RequestLauncher;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.http.HttpResponse;
import java.util.Map;

public class CreateUtilizadorModalController {

    @FXML private TextField nomeField;
    @FXML private PasswordField passwordField;
    @FXML private DatePicker dnPicker;
    
    @FXML private CheckBox adminCheck;
    @FXML private CheckBox gestorRotasCheck;
    @FXML private CheckBox gestorCheck;
    @FXML private CheckBox lojaCheck;
    @FXML private CheckBox armazemCheck;

    private final ObjectMapper mapper = new ObjectMapper();

    @FXML
    private void handleSave() {
        try {
            if (nomeField.getText().isBlank() || passwordField.getText().isBlank() || dnPicker.getValue() == null) {
                javafx.application.Platform.runLater(() -> {
                    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
                    alert.setTitle("Aviso");
                    alert.setHeaderText("Campos Inválidos");
                    alert.setContentText("Preencha todos os campos obrigatórios.");
                    alert.showAndWait();
                });
                return;
            }

            String dn = dnPicker.getValue().toString(); // format: yyyy-MM-dd

            java.util.Map<String, Object> reqMap = new java.util.HashMap<>();
            reqMap.put("nome", nomeField.getText());
            reqMap.put("password", passwordField.getText());
            reqMap.put("dn", dn);
            reqMap.put("admin", adminCheck.isSelected());
            reqMap.put("gestorRotas", gestorRotasCheck.isSelected());
            reqMap.put("gestor", gestorCheck.isSelected());
            reqMap.put("loja", lojaCheck.isSelected());
            reqMap.put("armazem", armazemCheck.isSelected());

            String json = mapper.writeValueAsString(reqMap);

            HttpResponse<String> response = RequestLauncher.newRequest("users/register", "POST", json);

            if (response != null && response.statusCode() == 201) {
                closeModal();
            } else {
                javafx.application.Platform.runLater(() -> {
                    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                    alert.setTitle("Erro");
                    alert.setHeaderText("Falha ao criar utilizador");
                    alert.setContentText(response != null ? response.body() : "Sem resposta do servidor.");
                    alert.showAndWait();
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            javafx.application.Platform.runLater(() -> {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText("Ocorreu um erro interno");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            });
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
