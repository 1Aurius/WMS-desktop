package com.example.wmsdsktp.Controllers;

import com.example.wmsdsktp.Responses.EquipaMembroResponse;
import com.example.wmsdsktp.Responses.EquipaResponse;
import com.example.wmsdsktp.util.RequestLauncher;
import com.fasterxml.jackson.core.type.TypeReference;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public class TeamsPageController {

    @FXML private ListView<EquipaResponse> teamsListView;
    @FXML private Label lblTeamName;
    @FXML private Button btnAddMember;
    @FXML private TableView<EquipaMembroResponse> membersTable;
    @FXML private TableColumn<EquipaMembroResponse, String> colMemberId;
    @FXML private TableColumn<EquipaMembroResponse, String> colMemberName;
    @FXML private TableColumn<EquipaMembroResponse, String> colMemberDate;
    @FXML private TableColumn<EquipaMembroResponse, Void> colMemberActions;

    private ObservableList<EquipaResponse> equipasList = FXCollections.observableArrayList();
    private ObservableList<EquipaMembroResponse> membrosList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        teamsListView.setItems(equipasList);
        membersTable.setItems(membrosList);

        teamsListView.setCellFactory(param -> new ListCell<EquipaResponse>() {
            @Override
            protected void updateItem(EquipaResponse item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.designacao() + " (ID: " + item.id() + ")");
                }
            }
        });

        teamsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                lblTeamName.setText(newV.designacao());
                btnAddMember.setVisible(true);
                loadMembers(newV.id());
            } else {
                lblTeamName.setText("Select a team");
                btnAddMember.setVisible(false);
                membrosList.clear();
            }
        });

        colMemberId.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().utilizadorId())));
        colMemberName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().nome()));
        colMemberDate.setCellValueFactory(cellData -> {
            String dh = cellData.getValue().dhAssociacao();
            return new SimpleStringProperty(dh != null && dh.length() > 10 ? dh.substring(0, 10) : dh);
        });

        colMemberActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnRemove = new Button("Remove");
            {
                btnRemove.getStyleClass().addAll("btn", "btn-danger");
                btnRemove.setOnAction(e -> {
                    EquipaMembroResponse membro = getTableView().getItems().get(getIndex());
                    removeMember(membro);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnRemove);
                }
            }
        });

        loadEquipas();
    }

    public void loadEquipas() {
        Thread thread = new Thread(() -> {
            try {
                java.net.http.HttpResponse<String> res = RequestLauncher.newRequest("equipas", "GET", "");
                if (res != null && res.statusCode() == 200) {
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    List<EquipaResponse> equipas = mapper.readValue(res.body(), new TypeReference<List<EquipaResponse>>() {});
                    Platform.runLater(() -> {
                        equipasList.clear();
                        equipasList.addAll(equipas);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    public void loadMembers(Integer equipaId) {
        Thread thread = new Thread(() -> {
            try {
                java.net.http.HttpResponse<String> res = RequestLauncher.newRequest("equipas/" + equipaId + "/membros", "GET", "");
                if (res != null && res.statusCode() == 200) {
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    List<EquipaMembroResponse> membros = mapper.readValue(res.body(), new TypeReference<List<EquipaMembroResponse>>() {});
                    Platform.runLater(() -> {
                        membrosList.clear();
                        membrosList.addAll(membros);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    @FXML
    private void onCreateTeam() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create New Team");
        dialog.setHeaderText("Enter the name of the new team:");
        dialog.setContentText("Name:");

        dialog.showAndWait().ifPresent(name -> {
            if (!name.isBlank()) {
                Thread thread = new Thread(() -> {
                    try {
                        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                        String json = mapper.writeValueAsString(Map.of("designacao", name));
                        java.net.http.HttpResponse<String> res = RequestLauncher.newRequest("equipas", "POST", json);
                        if (res != null && res.statusCode() == 201) {
                            Platform.runLater(this::loadEquipas);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                thread.start();
            }
        });
    }

    @FXML
    private void onAddMember() {
        EquipaResponse selected = teamsListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/wmsdsktp/forms/add-team-member-modal.fxml"));
            Parent root = loader.load();

            AddTeamMemberController controller = loader.getController();
            controller.setEquipa(selected, this);

            Stage stage = new Stage();
            stage.setTitle("Add Member");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeMember(EquipaMembroResponse membro) {
        EquipaResponse selected = teamsListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Thread thread = new Thread(() -> {
            try {
                java.net.http.HttpResponse<String> res = RequestLauncher.newRequest("equipas/" + selected.id() + "/membros/" + membro.utilizadorId(), "DELETE", "");
                if (res != null && res.statusCode() == 200) {
                    Platform.runLater(() -> loadMembers(selected.id()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }
}
