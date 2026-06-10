package com.example.wmsdsktp.Controllers;

import com.example.wmsdsktp.Responses.EquipaResponse;
import com.example.wmsdsktp.Responses.UtilizadorResponse;
import com.example.wmsdsktp.util.RequestLauncher;
import com.fasterxml.jackson.core.type.TypeReference;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public class AddTeamMemberController {

    @FXML private ComboBox<UtilizadorResponse> cmbUsers;

    private EquipaResponse equipa;
    private TeamsPageController parentController;

    @FXML
    public void initialize() {
        cmbUsers.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(UtilizadorResponse item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.username() + " (ID: " + item.id() + ")");
                }
            }
        });
        cmbUsers.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(UtilizadorResponse item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.username() + " (ID: " + item.id() + ")");
                }
            }
        });

        loadUsers();
    }

    public void setEquipa(EquipaResponse equipa, TeamsPageController parent) {
        this.equipa = equipa;
        this.parentController = parent;
    }

    private void loadUsers() {
        Thread thread = new Thread(() -> {
            try {
                java.net.http.HttpResponse<String> res = RequestLauncher.newRequest("users", "GET", "");
                if (res != null && res.statusCode() == 200) {
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    List<UtilizadorResponse> users = mapper.readValue(res.body(), new TypeReference<List<UtilizadorResponse>>() {});
                    Platform.runLater(() -> {
                        cmbUsers.setItems(FXCollections.observableArrayList(users));
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    @FXML
    private void onAdd() {
        UtilizadorResponse selected = cmbUsers.getSelectionModel().getSelectedItem();
        if (selected == null || equipa == null) return;

        Thread thread = new Thread(() -> {
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                String json = mapper.writeValueAsString(Map.of("utilizadorId", selected.id()));
                java.net.http.HttpResponse<String> res = RequestLauncher.newRequest("equipas/" + equipa.id() + "/membros", "POST", json);
                if (res != null && res.statusCode() == 201) {
                    Platform.runLater(() -> {
                        parentController.loadMembers(equipa.id());
                        onCancel();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    @FXML
    private void onCancel() {
        Stage stage = (Stage) cmbUsers.getScene().getWindow();
        stage.close();
    }
}
