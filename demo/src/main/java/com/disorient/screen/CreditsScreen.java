package com.disorient.screen;

import com.disorient.manager.ScreenManager;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class CreditsScreen extends StackPane {

    public CreditsScreen() {
        this(ScreenManager.getActive());
    }

    public CreditsScreen(ScreenManager screenManager) {

        // =========================
        // BACKGROUND
        // =========================
        HorrorUiTheme.applyBackdrop(this);

        // =========================
        // TITLE
        // =========================
        Label title = new Label("CREDITS");

        title.setStyle(
                "-fx-text-fill: white;"
                + "-fx-font-size: 36px;"
                + "-fx-font-weight: bold;"
                + "-fx-letter-spacing: 5px;"
                + "-fx-font-family: Georgia;"
                + "-fx-effect: dropshadow(gaussian, #b84cff, 20, 0.6, 0, 0);"
        );

        Label subtitle = new Label(
                "DISORIENT • HAUNTED HOUSE"
        );

        subtitle.setStyle(
                "-fx-text-fill: #b99acb;"
                + "-fx-font-size: 13px;"
                + "-fx-letter-spacing: 2px;"
        );

        // =========================
        // GAME INFORMATION
        // =========================
        Label gameTitle = new Label("DISORIENT");

        gameTitle.setStyle(
                "-fx-text-fill: #d88cff;"
                + "-fx-font-size: 24px;"
                + "-fx-font-weight: bold;"
        );

        Label description = new Label(
                """
                A Java-based 2D puzzle adventure game
                set inside a mysterious haunted house.
                """
        );

        description.setAlignment(Pos.CENTER);

        description.setStyle(
                "-fx-text-fill: #d0bfd8;"
                + "-fx-font-size: 14px;"
                + "-fx-line-spacing: 5px;"
        );

        // =========================
        // DEVELOPMENT TEAM
        // =========================
        Label teamTitle = new Label(
                "DEVELOPMENT TEAM"
        );

        teamTitle.setStyle(
                "-fx-text-fill: #c86cff;"
                + "-fx-font-size: 14px;"
                + "-fx-font-weight: bold;"
                + "-fx-letter-spacing: 3px;"
        );

        /*
         * Replace these names with
         * your actual group members.
         */
        Label members = new Label(
                """
                Reibert Lenard P. Leonardo
                Renz Ivan Casais
                Mardie Quiros
                Honneyln Iclabis
                John Cyrus Celistino
                """
        );

        members.setAlignment(Pos.CENTER);

        members.setStyle(
                "-fx-text-fill: white;"
                + "-fx-font-size: 15px;"
                + "-fx-line-spacing: 5px;"
        );

        // =========================
        // COURSE
        // =========================
        Label course = new Label(
                """
                BSCS - 3A
                Java Game Development Project
                """
        );

        course.setAlignment(Pos.CENTER);

        course.setStyle(
                "-fx-text-fill: #a98ab7;"
                + "-fx-font-size: 13px;"
                + "-fx-line-spacing: 4px;"
        );

        // =========================
        // TECHNOLOGY
        // =========================
        Label technology = new Label(
                "Built with Java • JavaFX • Maven"
        );

        technology.setStyle(
                "-fx-text-fill: #7e628c;"
                + "-fx-font-size: 12px;"
        );

        // =========================
        // BACK BUTTON
        // =========================
        Button backButton
                = createButton("BACK");

        backButton.setOnAction(event
                -> screenManager.showMainMenu()
        );

        // =========================
        // CONTENT
        // =========================
        VBox content = new VBox(
                12,
                title,
                subtitle,
                gameTitle,
                description,
                teamTitle,
                members,
                course,
                technology,
                backButton
        );

        content.setAlignment(Pos.CENTER);

        content.setMaxWidth(650);
        content.setPadding(new javafx.geometry.Insets(28, 42, 28, 42));
        content.setStyle("-fx-background-color: rgba(13, 8, 19, 0.94);"
                + "-fx-background-radius: 14px;-fx-border-color: #987452;"
                + "-fx-border-radius: 14px;-fx-border-width: 1.5px;");
        content.setEffect(new javafx.scene.effect.DropShadow(24, javafx.scene.paint.Color.rgb(0, 0, 0, 0.55)));

        getChildren().add(content);
    }

    // =========================
    // BUTTON
    // =========================
    private Button createButton(String text) {

        Button button = new Button(text);
        button.setPrefWidth(280);
        button.setPrefHeight(46);
        HorrorUiTheme.styleButton(button, ScreenManager.getActive().getAudioManager());

        return button;
    }
}
