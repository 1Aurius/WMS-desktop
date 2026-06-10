package com.example.wmsdsktp.Controllers;

import com.example.wmsdsktp.Responses.EquipaResponse;
import com.example.wmsdsktp.Responses.RotaResponse;
import com.example.wmsdsktp.Responses.VeiculoResponse;
import com.example.wmsdsktp.Responses.ParagemResponse;
import com.example.wmsdsktp.util.RequestLauncher;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.http.HttpResponse;
import java.util.List;

public class ManageRotaController {

    @FXML private Label routeIdNameLabel;
    @FXML private ComboBox<String> estadoComboBox;
    @FXML private ComboBox<VeiculoResponse> veiculoComboBox;
    @FXML private ComboBox<EquipaResponse> equipaComboBox;
    @FXML private ListView<ParagemResponse> paragensListView;

    private RotaResponse currentRota;
    private final ObjectMapper mapper = new ObjectMapper();

    @FXML
    public void initialize() {
        estadoComboBox.setItems(FXCollections.observableArrayList(
                "CRIADA", "EM_EXECUCAO", "CONCLUIDA", "CANCELADA"
        ));
        
        veiculoComboBox.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(VeiculoResponse v) {
                if (v == null) return null;
                return v.matricula() + " (ID: " + v.id() + ")";
            }

            @Override
            public VeiculoResponse fromString(String string) {
                return null;
            }
        });

        equipaComboBox.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(EquipaResponse e) {
                if (e == null) return null;
                return e.designacao() + " (ID: " + e.id() + ")";
            }

            @Override
            public EquipaResponse fromString(String string) {
                return null;
            }
        });
        
        paragensListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(ParagemResponse item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    boolean isCurrent = item.dhSaida() == null;
                    
                    VBox box = new VBox(4);
                    Label nameLbl = new Label(item.postoNome() + " (" + item.postoLocal() + ")");
                    nameLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");
                    
                    String statusText = "Pendente";
                    if (item.dhChegada() != null && item.dhSaida() != null) {
                        statusText = "Concluída";
                    } else if (item.dhChegada() != null && item.dhSaida() == null) {
                        statusText = "No local (Chegou)";
                    } else if (item.dhChegada() == null && isCurrent) {
                        statusText = "A caminho (Próxima)";
                    }
                    
                    Label statusLbl = new Label("Estado: " + statusText);
                    statusLbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");
                    
                    box.getChildren().addAll(nameLbl, statusLbl);
                    
                    if (isCurrent) {
                        // We highlight the first one that has dhSaida == null.
                        // However, multiple items might have dhSaida == null. 
                        // To accurately find the "current" stop, we find the first in the list.
                        // We will do this logic when populating the list instead.
                    }
                    
                    setGraphic(box);
                }
            }
        });

        loadVeiculos();
        loadEquipas();
    }

    public void setRota(RotaResponse rota) {
        this.currentRota = rota;
        routeIdNameLabel.setText(String.format("Rota #%d - %s", rota.id(), rota.nome()));
        estadoComboBox.setValue(rota.estado());
        
        selectCurrentVeiculo();
        selectCurrentEquipa();
        
        loadParagens();
    }

    private void loadParagens() {
        if (currentRota == null) return;
        Thread thread = new Thread(() -> {
            try {
                HttpResponse<String> response = RequestLauncher.newRequest("rotas/" + currentRota.id() + "/paragens", "GET", null);
                if (response != null && response.statusCode() == 200) {
                    List<ParagemResponse> paragens = mapper.readValue(response.body(), new TypeReference<>() {});
                    Platform.runLater(() -> {
                        // Find the current stop index
                        int currentIndex = -1;
                        for (int i = 0; i < paragens.size(); i++) {
                            if (paragens.get(i).dhSaida() == null) {
                                currentIndex = i;
                                break;
                            }
                        }
                        
                        final int currentStopIdx = currentIndex;
                        
                        paragensListView.setCellFactory(lv -> new ListCell<>() {
                            @Override
                            protected void updateItem(ParagemResponse item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty || item == null) {
                                    setText(null);
                                    setGraphic(null);
                                    setStyle("-fx-background-color: transparent;");
                                } else {
                                    boolean isCurrent = (getIndex() == currentStopIdx);
                                    
                                    VBox box = new VBox(2);
                                    Label nameLbl = new Label((getIndex() + 1) + ". " + item.postoNome() + " (" + item.postoLocal() + ")");
                                    nameLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #f1f5f9;");
                                    
                                    String statusText = "Pendente";
                                    if (item.dhChegada() != null && item.dhSaida() != null) {
                                        statusText = "Concluída";
                                    } else if (item.dhChegada() != null && item.dhSaida() == null) {
                                        statusText = "No local (Chegou)";
                                    } else if (item.dhChegada() == null && isCurrent) {
                                        statusText = "A caminho (Próxima paragem)";
                                    }
                                    
                                    Label statusLbl = new Label(statusText);
                                    statusLbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");
                                    
                                    box.getChildren().addAll(nameLbl, statusLbl);
                                    
                                    if (isCurrent) {
                                        setStyle("-fx-background-color: rgba(34, 197, 94, 0.2); -fx-border-color: #22c55e; -fx-border-width: 0 0 0 4; -fx-padding: 4 8;");
                                        statusLbl.setStyle("-fx-text-fill: #4ade80; -fx-font-size: 11px; -fx-font-weight: bold;");
                                    } else if (item.dhSaida() != null) {
                                        setStyle("-fx-background-color: rgba(255, 255, 255, 0.02); -fx-padding: 4 8; -fx-opacity: 0.6;");
                                    } else {
                                        setStyle("-fx-background-color: transparent; -fx-padding: 4 8;");
                                    }
                                    
                                    setGraphic(box);
                                }
                            }
                        });
                        
                        paragensListView.setItems(FXCollections.observableArrayList(paragens));
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    private void loadVeiculos() {
        try {
            HttpResponse<String> response = RequestLauncher.newRequest("veiculos", "GET", null);
            if (response != null && response.statusCode() == 200) {
                List<VeiculoResponse> veiculos = mapper.readValue(response.body(), new TypeReference<>() {});
                Platform.runLater(() -> {
                    veiculoComboBox.setItems(FXCollections.observableArrayList(veiculos));
                    selectCurrentVeiculo();
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadEquipas() {
        try {
            HttpResponse<String> response = RequestLauncher.newRequest("equipas", "GET", null);
            if (response != null && response.statusCode() == 200) {
                List<EquipaResponse> equipas = mapper.readValue(response.body(), new TypeReference<>() {});
                Platform.runLater(() -> {
                    equipaComboBox.setItems(FXCollections.observableArrayList(equipas));
                    selectCurrentEquipa();
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectCurrentVeiculo() {
        if (currentRota == null || currentRota.veiculoId() == null || veiculoComboBox.getItems() == null) return;
        for (VeiculoResponse v : veiculoComboBox.getItems()) {
            if (v.id().equals(currentRota.veiculoId())) {
                veiculoComboBox.setValue(v);
                break;
            }
        }
    }

    private void selectCurrentEquipa() {
        if (currentRota == null || currentRota.equipaId() == null || equipaComboBox.getItems() == null) return;
        for (EquipaResponse e : equipaComboBox.getItems()) {
            if (e.id().equals(currentRota.equipaId())) {
                equipaComboBox.setValue(e);
                break;
            }
        }
    }

    @FXML
    private void handleCancel() {
        closeModal();
    }

    @FXML
    private void handleSave() {
        String novoEstado = estadoComboBox.getValue();
        Integer novoVeiculoId = veiculoComboBox.getValue() != null ? veiculoComboBox.getValue().id() : null;
        Integer novaEquipaId = equipaComboBox.getValue() != null ? equipaComboBox.getValue().id() : null;

        String json = String.format("""
            {
                "estado": "%s",
                "veiculoId": %s,
                "equipaId": %s
            }
            """, 
            novoEstado != null ? novoEstado : currentRota.estado(),
            novoVeiculoId,
            novaEquipaId
        );

        Thread thread = new Thread(() -> {
            try {
                HttpResponse<String> response = RequestLauncher.newRequest("rotas/" + currentRota.id() + "/status", "PUT", json);

                if (response != null && (response.statusCode() == 200 || response.statusCode() == 201)) {
                    Platform.runLater(() -> {
                        showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Estado da rota atualizado com sucesso!");
                        closeModal();
                    });
                } else {
                    Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível atualizar a rota."));
                }
            } catch (Exception e) {
                Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Erro", "Ocorreu um erro ao atualizar: " + e.getMessage()));
            }
        });
        thread.start();
    }

    private void closeModal() {
        Stage stage = (Stage) routeIdNameLabel.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
