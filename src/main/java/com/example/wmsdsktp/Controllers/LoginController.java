package com.example.wmsdsktp.Controllers;

import com.example.wmsdsktp.Responses.LoginResponse;
import com.example.wmsdsktp.auth.UserSession;
import com.example.wmsdsktp.util.RequestLauncher;
import com.example.wmsdsktp.util.SceneManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;
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
    public void initialize(){
        // [DEBUG]
        usernameField.setText("admin");
        passwordField.setText("admin123");
    }

    @FXML
    protected void onLoginButtonClick(ActionEvent event) {
        String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();

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

        if (password.length() < 8) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Password must be at least 8 characters");
            return;
        }

        String json;
        try {
            json = new ObjectMapper().writeValueAsString(Map.of(
                    "username", username,
                    "password", password
            ));
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to build login request");
            return;
        }
        HttpResponse<String> loginOutcome = RequestLauncher.newRequest("users/login", "POST", json);

        if (loginOutcome == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Unable to reach authentication service");
            return;
        }

        if (loginOutcome.statusCode() == 200) {

            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(loginOutcome.body());

                LoginResponse data = new LoginResponse();
                data.setId(root.path("id").asLong());
                data.setUsername(root.path("username").asText(""));
                data.setToken(root.path("token").asText(""));
                data.setAdmin(root.path("admin").asBoolean(root.path("isAdmin").asBoolean(false)));
                data.setGestorRotas(root.path("gestorRotas").asBoolean(root.path("isGestorRotas").asBoolean(false)));
                data.setGestor(root.path("gestor").asBoolean(root.path("isGestor").asBoolean(false)));
                data.setLoja(root.path("loja").asBoolean(root.path("isLoja").asBoolean(false)));
                data.setArmazem(root.path("armazem").asBoolean(root.path("isArmazem").asBoolean(false)));
                data.setDn(parseDateNode(root.get("dn")));

                UserSession loggedUser = new UserSession(
                        data.getId(),
                        data.getUsername(),
                        data.getDn(),
                        data.isAdmin(),
                        data.isGestorRotas(),
                        data.isGestor(),
                        data.isLoja(),
                        data.isArmazem(),
                        data.getToken()
                );
                UserSession.setUser(loggedUser);
                SceneManager.switchTo("/com/example/wmsdsktp/layouts/Dashboard.fxml");
                showAlert(Alert.AlertType.INFORMATION, "Success", "Login successful!");

            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to parse login response: " + ex.getMessage());
            }
        } else {
            String errorMessage = loginOutcome.body() == null || loginOutcome.body().isBlank()
                    ? "Invalid username or password"
                    : loginOutcome.body();
            showAlert(Alert.AlertType.ERROR, "Error", errorMessage);
        }
    }

    @FXML
    protected void onRegisterButtonClick() {
        SceneManager.switchTo("/com/example/wmsdsktp/auth/register-view.fxml");
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Date parseDateNode(JsonNode dateNode) {
        if (dateNode == null || dateNode.isNull()) {
            return null;
        }

        if (dateNode.isNumber()) {
            return new Date(dateNode.asLong());
        }

        String raw = dateNode.asText();
        if (raw == null || raw.isBlank()) {
            return null;
        }

        try {
            return Date.from(OffsetDateTime.parse(raw).toInstant());
        } catch (Exception ignored) {
            // try date-only format
        }

        try {
            return Date.from(LocalDate.parse(raw).atStartOfDay(ZoneId.systemDefault()).toInstant());
        } catch (Exception ignored) {
            return null;
        }
    }
}
