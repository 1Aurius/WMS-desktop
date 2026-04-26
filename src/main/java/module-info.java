module com.example.wmsdsktp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;


    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires java.net.http;
    requires java.security.jgss;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;

    opens com.example.wmsdsktp to javafx.fxml;
    opens com.example.wmsdsktp.auth to javafx.fxml;
    opens com.example.wmsdsktp.layouts to javafx.fxml;
    opens com.example.wmsdsktp.pages to javafx.fxml;
    opens com.example.wmsdsktp.components to javafx.fxml;
    opens com.example.wmsdsktp.Responses to com.fasterxml.jackson.databind, javafx.base;
    exports com.example.wmsdsktp;
    exports com.example.wmsdsktp.Controllers;
    opens com.example.wmsdsktp.Controllers to javafx.fxml;
}