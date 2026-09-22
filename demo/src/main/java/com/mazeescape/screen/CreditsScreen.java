package com.mazeescape.screen;

import com.mazeescape.manager.ScreenManager;
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
        setStyle(
                "-fx-background-color: linear-gradient("
                + "to bottom, #050008, #12051a, #050008);"
        );

        // =========================
        // TITLE
        // =========================
        Label title = new Label("CREDITS");

        title.setStyle(
                "-fx-text-fill: white;"
                + "-fx-font-size: 42px;"
                + "-fx-font-weight: bold;"
                + "-fx-letter-spacing: 5px;"
                + "-fx-effect: dropshadow(gaussian, #b84cff, 20, 0.6, 0, 0);"
        );

        Label subtitle = new Label(
                "MAZE ESCAPE • HAUNTED HOUSE"
        );

        subtitle.setStyle(
                "-fx-text-fill: #b99acb;"
                + "-fx-font-size: 13px;"
                + "-fx-letter-spacing: 2px;"
        );

        // =========================
        // GAME INFORMATION
        // =========================
        Label gameTitle = new Label("MAZE ESCAPE");

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

        content.setMaxWidth(600);

        getChildren().add(content);
    }

    // =========================
    // BUTTON
    // =========================
    private Button createButton(String text) {

        Button button = new Button(text);
        button.setOnMouseClicked(event -> ScreenManager.getActive().getAudioManager().playClick());

        button.setPrefWidth(280);
        button.setPrefHeight(46);

        button.setStyle(
                "-fx-background-color: #170d22;"
                + "-fx-border-color: #6d3a91;"
                + "-fx-border-width: 1.5px;"
                + "-fx-border-radius: 8px;"
                + "-fx-background-radius: 8px;"
                + "-fx-text-fill: white;"
                + "-fx-font-size: 13px;"
                + "-fx-font-weight: bold;"
                + "-fx-letter-spacing: 2px;"
                + "-fx-cursor: hand;"
        );

        button.setOnMouseEntered(event -> {

            button.setStyle(
                    "-fx-background-color: #35104a;"
                    + "-fx-border-color: #b84cff;"
                    + "-fx-border-width: 2px;"
                    + "-fx-border-radius: 8px;"
                    + "-fx-background-radius: 8px;"
                    + "-fx-text-fill: white;"
                    + "-fx-font-size: 13px;"
                    + "-fx-font-weight: bold;"
                    + "-fx-letter-spacing: 2px;"
                    + "-fx-cursor: hand;"
                    + "-fx-effect: dropshadow(gaussian, #b84cff, 15, 0.5, 0, 0);"
            );
        });

        button.setOnMouseExited(event -> {

            button.setStyle(
                    "-fx-background-color: #170d22;"
                    + "-fx-border-color: #6d3a91;"
                    + "-fx-border-width: 1.5px;"
                    + "-fx-border-radius: 8px;"
                    + "-fx-background-radius: 8px;"
                    + "-fx-text-fill: white;"
                    + "-fx-font-size: 13px;"
                    + "-fx-font-weight: bold;"
                    + "-fx-letter-spacing: 2px;"
                    + "-fx-cursor: hand;"
            );
        });

        return button;
    }
}
