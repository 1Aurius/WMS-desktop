package com.example.wmsdsktp;

import com.example.wmsdsktp.util.RedirectService;
import com.example.wmsdsktp.util.RequestLauncher;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.http.HttpResponse;

public class RegisterController {
    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private DatePicker dnField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button registerButton;

    @FXML
    private Button backToLoginButton;

    @FXML
    protected void onRegisterButtonClick() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String dn      = dnField.getValue().toString();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isBlank()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Username is required");
            return;
        }

        if (username.length() < 3) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Username must be at least 3 characters");
            return;
        }

        if (email.isBlank()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Email is required");
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

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Passwords do not match");
            return;
        }
        String json = """
        {
            "nome": "%s",
            "password": "%s",
            "dn": "%s"
        }
    """.formatted(username, password, dn);

    HttpResponse loginOutcome = RequestLauncher.newRequest("users/register", "POST", json);

        if(loginOutcome.statusCode() == 200){
             showAlert(Alert.AlertType.INFORMATION, "Success", "Registration successful!");

             try{
                 RedirectService.redirect(backToLoginButton,"com/example/wmsdsktp/login-view.fxml");
             }catch (IOException e){
                 e.printStackTrace();
             }

         }else{
             showAlert(Alert.AlertType.ERROR, "Error", "Registration failed");
         }
    }

    @FXML
    protected void onBackToLoginButtonClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 400, 400);
            Stage stage = (Stage) backToLoginButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Login");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load login form");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
