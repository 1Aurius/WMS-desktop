package com.example.wmsdsktp.Controllers;

import com.example.wmsdsktp.auth.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SidebarController extends VBox {

    @FXML private Button btnLogout;
    @FXML private Button navDashboard;
    @FXML private Button navStock;
    @FXML private Button navProduct;
    @FXML private Button navRequisitions;
    @FXML private Button navUsers;

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
        clearActive();
        switch (userData.toString()) {
            case "dashboard" -> {
                dashboardController.loadPage("/com/example/wmsdsktp/pages/DashboardOverview.fxml");
                navDashboard.getStyleClass().add("nav-active");
            }
            case "stock"     -> {
                dashboardController.loadPage("/com/example/wmsdsktp/pages/StockPage.fxml");
                 navStock.getStyleClass().add("nav-active");
            }
            case "product"   -> {
                dashboardController.loadPage("/com/example/wmsdsktp/pages/ProductPage.fxml");
                navProduct.getStyleClass().add("nav-active");
            }
            case "requisition"   -> {
                dashboardController.loadPage("/com/example/wmsdsktp/pages/RequisitionsPage.fxml");
                navRequisitions.getStyleClass().add("nav-active");
            }
            case "users"   -> {
                dashboardController.loadPage("/com/example/wmsdsktp/pages/UsersPage.fxml");
                navUsers.getStyleClass().add("nav-active");
            }
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

    private void clearActive() {
        navDashboard.getStyleClass().remove("nav-active");
        navStock.getStyleClass().remove("nav-active");
        navProduct.getStyleClass().remove("nav-active");
        navUsers.getStyleClass().remove("nav-active");
        navRequisitions.getStyleClass().remove("nav-active");
    }


}
