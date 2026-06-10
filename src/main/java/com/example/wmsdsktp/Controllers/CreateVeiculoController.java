package com.example.wmsdsktp.Controllers;

import com.example.wmsdsktp.Responses.PostoResponse;
import com.example.wmsdsktp.auth.UserSession;
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

public class CreateVeiculoController {

    @FXML private TextField matriculaField;
    @FXML private TextField anoField;
    @FXML private TextField volumeField;
    @FXML private ComboBox<PostoResponse> postoComboBox;

    private final ObjectMapper mapper = new ObjectMapper();

    @FXML
    public void initialize() {
        loadPostos();
    }

    private void loadPostos() {
        try {
            HttpResponse<String> response = RequestLauncher.newRequest("postos", "GET", null);
            if (response == null || response.statusCode() != 200) return;

            List<PostoResponse> list = mapper.readValue(response.body(), new TypeReference<>() {});
            postoComboBox.getItems().addAll(list);
            
            postoComboBox.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(PostoResponse item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.designacao());
                }
            });
            postoComboBox.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(PostoResponse item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.designacao());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSave() {
        try {
            String matricula = matriculaField.getText();
            String ano = anoField.getText();
            String volumeStr = volumeField.getText();
            PostoResponse selectedPosto = postoComboBox.getValue();

            if (matricula.isBlank() || volumeStr.isBlank() || selectedPosto == null) return;

            Double volume = Double.parseDouble(volumeStr);

            String json = mapper.writeValueAsString(Map.of(
                    "matricula", matricula,
                    "ano", ano,
                    "aproxVolume", volume,
                    "postoId", selectedPosto.id(),
                    "utilizadorCriadorId", UserSession.getInstance().getId()
            ));

            HttpResponse<String> response = RequestLauncher.newRequest("veiculos", "POST", json);

            if (response != null && response.statusCode() == 201) {
                closeModal();
            } else {
                System.out.println("Error saving vehicle. Status: " + (response != null ? response.statusCode() : "null"));
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
        Stage stage = (Stage) matriculaField.getScene().getWindow();
        stage.close();
    }
}
