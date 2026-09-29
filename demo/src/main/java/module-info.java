module com.disorient {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.desktop;
    requires static org.junit.jupiter.api;
    requires static org.junit.platform.commons;

    exports com.disorient;
    exports com.disorient.controller;
    exports com.disorient.manager;
    exports com.disorient.model;
    exports com.disorient.screen;

    opens com.disorient to org.junit.platform.commons;
}
