package com.example.wmsdsktp.Controllers;

import com.example.wmsdsktp.util.RequestLauncher;
import com.example.wmsdsktp.util.SceneManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.http.HttpResponse;
import java.util.Map;

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
        String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String email = emailField.getText() == null ? "" : emailField.getText().trim();
        if (dnField.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Date of birth is required");
            return;
        }
        String dn = dnField.getValue().toString();
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

        // Email is shown in UI but is not yet stored by backend.
        // Keep a light format check only when user provides one.
        if (!email.isBlank() && !email.contains("@")) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter a valid email");
            return;
        }

        if (password.isBlank()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Password is required");
            return;
        }

        if (password.length() < 8) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Password must be at least 8 characters");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Passwords do not match");
            return;
        }
        HttpResponse<String> registerOutcome;
        try {
            String json = new ObjectMapper().writeValueAsString(Map.of(
                    "nome", username,
                    "password", password,
                    "dn", dn
            ));
            registerOutcome = RequestLauncher.newRequest("users/register", "POST", json);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to build registration request");
            return;
        }

        if (registerOutcome == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Unable to reach registration service");
            return;
        }

        if (registerOutcome.statusCode() >= 200 && registerOutcome.statusCode() < 300) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Registration successful!");

            SceneManager.switchTo("/com/example/wmsdsktp/auth/login-view.fxml");

        } else {
            String errorMessage = registerOutcome.body() == null || registerOutcome.body().isBlank()
                    ? "Registration failed"
                    : registerOutcome.body();
            showAlert(Alert.AlertType.ERROR, "Error", errorMessage);
        }
    }

    @FXML
    protected void onBackToLoginButtonClick() {
        SceneManager.switchTo("/com/example/wmsdsktp/auth/login-view.fxml");
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
