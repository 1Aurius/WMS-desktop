module com.example.wmsdsktp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires eu.hansolo.tilesfx;
    requires java.net.http;
    requires java.security.jgss;
    requires com.fasterxml.jackson.databind;

    opens com.example.wmsdsktp to javafx.fxml;
    exports com.example.wmsdsktp;
}