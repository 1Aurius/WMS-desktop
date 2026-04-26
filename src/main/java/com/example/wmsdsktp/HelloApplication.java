package com.example.wmsdsktp;

import com.example.wmsdsktp.util.SceneManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        SceneManager.setStage(stage);
        SceneManager.switchTo("/com/example/wmsdsktp/auth/login-view.fxml");
        stage.setTitle("Hermes Logistics");
        stage.show();
    }
}
