package com.example.wmsdsktp;

import com.example.wmsdsktp.auth.UserSession;
import com.example.wmsdsktp.util.RequestLauncher;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.Console;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Map;

public class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    @FXML
    protected void onLoginButtonClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isBlank()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Username is required");
            return;
        }

        if (username.length() < 3) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Username must be at least 3 characters");
            return;
        }

        if (password.isBlank()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Password is required");
            return;
        }

        if (password.length() < 6) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Password must be at least 6 characters");
            return;
        }
        String json = """
        {
            "nome": "%s",
            "password": "%s",
        }
        """.formatted(username, password);
        System.out.println(json);
        HttpResponse loginOutcome = RequestLauncher.newRequest("users/login","POST",json);


        if(loginOutcome.statusCode() != 200){
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid username or password");
            return;
        }else{
            ObjectMapper mapper = new ObjectMapper();
            try {
                LoginResponse data = mapper.readValue(loginOutcome.body().toString(), LoginResponse.class);

                UserSession.getInstance().setNome(data.nome);
                UserSession.getInstance().setId(data.id);
            }catch (JsonProcessingException e){
                e.printStackTrace();
                return;
            }

        }

        showAlert(Alert.AlertType.INFORMATION, "Success", "Login successful!");
    }

    @FXML
    protected void onRegisterButtonClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("register-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 400, 500);
            Stage stage = (Stage) registerButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Register");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load register form");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public class LoginResponse {
        public String nome;
        public String dn;
        public long id;
    }
}
