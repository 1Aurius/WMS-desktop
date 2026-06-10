package com.example.wmsdsktp.Controllers;

import com.example.wmsdsktp.Responses.UtilizadorResponse;
import com.example.wmsdsktp.util.RequestLauncher;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.http.HttpResponse;
import java.util.Map;

public class EditUtilizadorModalController {

    @FXML private TextField nomeField;
    @FXML private CheckBox adminCheck;
    @FXML private CheckBox gestorRotasCheck;
    @FXML private CheckBox gestorCheck;
    @FXML private CheckBox lojaCheck;
    @FXML private CheckBox armazemCheck;

    private final ObjectMapper mapper = new ObjectMapper();
    private Integer utilizadorId;
    private String dn; // Keep track of the dn so we can send it back unchanged

    public void initData(UtilizadorResponse utilizador) {
        this.utilizadorId = utilizador.id();
        this.dn = utilizador.dn();
        this.nomeField.setText(utilizador.username());
        
        this.adminCheck.setSelected(Boolean.TRUE.equals(utilizador.admin()));
        this.gestorRotasCheck.setSelected(Boolean.TRUE.equals(utilizador.gestorRotas()));
        this.gestorCheck.setSelected(Boolean.TRUE.equals(utilizador.gestor()));
        this.lojaCheck.setSelected(Boolean.TRUE.equals(utilizador.loja()));
        this.armazemCheck.setSelected(Boolean.TRUE.equals(utilizador.armazem()));
    }

    @FXML
    private void handleSave() {
        try {
            // Note: The backend requires nome and dn for an update.
            // Since we're only editing permissions, we don't send a new password.
            // And we grab the dn from when we initialized the modal.
            String formattedDn = dn != null && dn.contains("T") ? dn.substring(0, dn.indexOf("T")) : dn;
            
            java.util.Map<String, Object> reqMap = new java.util.HashMap<>();
            reqMap.put("nome", nomeField.getText());
            reqMap.put("dn", formattedDn);
            reqMap.put("admin", adminCheck.isSelected());
            reqMap.put("gestorRotas", gestorRotasCheck.isSelected());
            reqMap.put("gestor", gestorCheck.isSelected());
            reqMap.put("loja", lojaCheck.isSelected());
            reqMap.put("armazem", armazemCheck.isSelected());

            String json = mapper.writeValueAsString(reqMap);

            HttpResponse<String> response = RequestLauncher.newRequest("users/" + utilizadorId, "PUT", json);

            if (response != null && response.statusCode() == 200) {
                closeModal();
            } else {
                javafx.application.Platform.runLater(() -> {
                    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                    alert.setTitle("Erro");
                    alert.setHeaderText("Falha ao atualizar permissões");
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
