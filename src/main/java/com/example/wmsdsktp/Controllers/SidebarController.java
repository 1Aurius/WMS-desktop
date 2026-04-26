package com.example.wmsdsktp.Controllers;

import com.example.wmsdsktp.auth.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SidebarController extends VBox {

    @FXML private Button btnLogout;


    public void onNavigate(ActionEvent actionEvent) {
        if (!(actionEvent.getSource() instanceof Button button)) {
            return;
        }
        Object userData = button.getUserData();
        if (userData == null) {
            return;
        }
        BaseDashboardController dashboardController = BaseDashboardController.getInstance();
        if (dashboardController == null) {
            return;
        }

        switch (userData.toString()) {
            case "dashboard" -> dashboardController.loadPage("/com/example/wmsdsktp/pages/DashboardOverview.fxml");
            case "stock"     -> dashboardController.loadPage("/com/example/wmsdsktp/pages/StockPage.fxml");
            case "product"   -> dashboardController.loadPage("/com/example/wmsdsktp/pages/ProductPage.fxml");
            case "utilizadores"   -> dashboardController.loadPage("/com/example/wmsdsktp/pages/utilizadoresPage.fxml");
            case "requesicao"   -> dashboardController.loadPage("/com/example/wmsdsktp/pages/requesicoesPage.fxml");
            default -> {
                // tbdn
            }
        }
    }
    @FXML
    private void onLogout() {
        UserSession.getInstance().logout();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/wmsdsktp/auth/login-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
