package com.example.wmsdsktp.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class RedirectService {

    public static void redirect(Node node, String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(RedirectService.class.getResource(fxmlPath));
        Parent root = loader.load();

        Stage stage = (Stage) node.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}

