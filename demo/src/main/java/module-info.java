module com.mazeescape {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.desktop;
    requires static org.junit.jupiter.api;
    requires static org.junit.platform.commons;

    exports com.mazeescape;
    exports com.mazeescape.controller;
    exports com.mazeescape.manager;
    exports com.mazeescape.model;
    exports com.mazeescape.screen;

    opens com.mazeescape to org.junit.platform.commons;
}
