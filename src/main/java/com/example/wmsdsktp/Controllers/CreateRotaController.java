package com.example.wmsdsktp.Controllers;

import com.example.wmsdsktp.Responses.EquipaResponse;
import com.example.wmsdsktp.Responses.VeiculoResponse;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateRotaController {

    @FXML private TextField nomeField;
    @FXML private ComboBox<VeiculoResponse> veiculoComboBox;
    @FXML private ComboBox<EquipaResponse> equipaComboBox;
    @FXML private javafx.scene.control.DatePicker dataDatePicker;
    @FXML private javafx.scene.control.ListView<com.example.wmsdsktp.Responses.PostoResponse> postosListView;

    private final ObjectMapper mapper = new ObjectMapper();

    @FXML
    public void initialize() {
        loadVeiculos();
        loadEquipas();
        loadPostos();
    }

    private void loadVeiculos() {
        try {
            HttpResponse<String> response = RequestLauncher.newRequest("veiculos", "GET", null);
            if (response == null || response.statusCode() != 200) return;

            List<VeiculoResponse> list = mapper.readValue(response.body(), new TypeReference<>() {});
            veiculoComboBox.getItems().add(null);
            veiculoComboBox.getItems().addAll(list);
            veiculoComboBox.setPromptText("Selecionar veículo...");

            veiculoComboBox.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(VeiculoResponse item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.matricula() + " (" + item.modeloDesignacao() + ")");
                }
            });
            veiculoComboBox.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(VeiculoResponse item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.matricula() + " (" + item.modeloDesignacao() + ")");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadEquipas() {
        try {
            HttpResponse<String> response = RequestLauncher.newRequest("equipas", "GET", null);
            if (response == null || response.statusCode() != 200) return;

            List<EquipaResponse> list = mapper.readValue(response.body(), new TypeReference<>() {});
            equipaComboBox.getItems().add(null);
            equipaComboBox.getItems().addAll(list);
            equipaComboBox.setPromptText("Selecionar equipa...");

            equipaComboBox.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(EquipaResponse item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.designacao());
                }
            });
            equipaComboBox.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(EquipaResponse item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.designacao());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPostos() {
        Thread thread = new Thread(() -> {
            try {
                HttpResponse<String> response = RequestLauncher.newRequest("postos", "GET", "");
                if (response == null || response.statusCode() != 200) return;

                List<com.example.wmsdsktp.Responses.PostoResponse> list = mapper.readValue(response.body(), new TypeReference<>() {});
                javafx.application.Platform.runLater(() -> {
                    postosListView.getItems().addAll(list);
                    postosListView.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);

                    postosListView.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
                        @Override
                        protected void updateItem(com.example.wmsdsktp.Responses.PostoResponse item, boolean empty) {
                            super.updateItem(item, empty);
                            setText(empty || item == null ? null : item.designacao() + " (" + item.local() + ")");
                        }
                    });
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    @FXML
    private void handleSave() {
        try {
            String nome = nomeField.getText();
            VeiculoResponse selectedVeiculo = veiculoComboBox.getValue();
            EquipaResponse selectedEquipa = equipaComboBox.getValue();
            java.time.LocalDate selectedDate = dataDatePicker.getValue();
            List<com.example.wmsdsktp.Responses.PostoResponse> selectedPostos = postosListView.getSelectionModel().getSelectedItems();

            if (nome.isBlank() || selectedVeiculo == null || selectedEquipa == null || selectedDate == null || selectedPostos.isEmpty()) {
                System.out.println("Missing required fields for Route.");
                return;
            }

            Map<String, Object> reqData = new HashMap<>();
            reqData.put("nome", nome);
            reqData.put("veiculoId", selectedVeiculo.id());
            reqData.put("utilizadorCriadorId", UserSession.getInstance().getId());
            reqData.put("equipaTransporteId", selectedEquipa.id());
            reqData.put("dataPrevista", selectedDate.toString());
            
            List<Integer> postosIds = new java.util.ArrayList<>();
            for (com.example.wmsdsktp.Responses.PostoResponse p : selectedPostos) {
                postosIds.add(p.id());
            }
            reqData.put("postosIds", postosIds);

            String json = mapper.writeValueAsString(reqData);

            Thread thread = new Thread(() -> {
                try {
                    HttpResponse<String> response = RequestLauncher.newRequest("rotas", "POST", json);

                    if (response != null && (response.statusCode() == 201 || response.statusCode() == 200)) {
                        javafx.application.Platform.runLater(this::closeModal);
                    } else {
                        System.out.println("Error creating Rota. Status: " + (response != null ? response.statusCode() : "null"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            thread.start();
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
