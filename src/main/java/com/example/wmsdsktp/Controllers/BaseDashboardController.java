package com.example.wmsdsktp.Controllers;

import com.example.wmsdsktp.HelloApplication;
import com.example.wmsdsktp.auth.UserSession;
import com.example.wmsdsktp.util.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class BaseDashboardController {
    private static BaseDashboardController instance;

    @FXML
    private StackPane pageContent;

    // debug display
    @FXML private Label usernameLabel;
    @FXML private Label roleLabel;


    @FXML
    public void initialize() {
        instance = this;
    }

    public void loadPage(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource(fxmlPath));
            Node page = loader.load();
            pageContent.getChildren().setAll(page);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onLogout(ActionEvent actionEvent) {
        UserSession.getInstance().logout();
        SceneManager.switchTo("/com/example/wmsdsktp/auth/login-view.fxml");
    }

    public static BaseDashboardController getInstance() {
        return instance;
    }
}
