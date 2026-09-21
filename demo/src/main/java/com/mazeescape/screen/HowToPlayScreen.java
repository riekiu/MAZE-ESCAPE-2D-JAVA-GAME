package com.mazeescape.screen;

import com.mazeescape.manager.ScreenManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class HowToPlayScreen extends StackPane {

    public HowToPlayScreen() {
        this(ScreenManager.getActive());
    }

    public HowToPlayScreen(ScreenManager screenManager) {
        setStyle("-fx-background-color: linear-gradient(to bottom, #050008, #160b20, #050008);");

        Label title = new Label("HOW TO PLAY");
        title.setStyle("-fx-text-fill: white;-fx-font-size: 42px;-fx-font-weight: bold;"
                + "-fx-letter-spacing: 5px;"
                + "-fx-effect: dropshadow(gaussian, #b84cff, 20, 0.6, 0, 0);");

        Label subtitle = new Label("ENTER THE MAZE. FIND THE KEY. ESCAPE THE HOUSE.");
        subtitle.setStyle("-fx-text-fill: #c7a8d4;-fx-font-size: 14px;-fx-letter-spacing: 2px;");

        Label controls = createSection("CONTROLS",
                "W / A / S / D or ARROW KEYS     Move through the maze\n"
                        + "ESC                              Pause the game");
        Label objective = createSection("MAIN OBJECTIVE",
                "1. Explore the maze and find the golden key.\n"
                        + "2. Collecting the key activates the exit.\n"
                        + "3. Reach the open exit before the timer runs out.");
        Label features = createSection("HAUNTED HOUSE FEATURES",
                "• 20 progressively larger mazes from 9x9 to 28x28.\n"
                        + "• Every level uses a different generated maze.\n"
                        + "• The camera follows you through larger levels.\n"
                        + "• The exit moves after you collect the key.\n"
                        + "• Timer, lives, score, music, and sound effects are active.\n"
                        + "• Difficulty rises from EASY to NIGHTMARE.");
        Label advice = createSection("SURVIVAL TIPS",
                "Watch the HUD objective, remember explored paths, and do not assume\n"
                        + "the first door you see is the final escape route.");

        Button back = new Button("BACK TO MAIN MENU");
        back.setPrefWidth(280);
        back.setPrefHeight(44);
        back.setStyle("-fx-background-color: #170d22;-fx-border-color: #6d3a91;"
                + "-fx-border-width: 1.5px;-fx-border-radius: 8px;-fx-background-radius: 8px;"
                + "-fx-text-fill: white;-fx-font-size: 13px;-fx-font-weight: bold;"
                + "-fx-letter-spacing: 1.5px;-fx-cursor: hand;");
        back.setOnMousePressed(event -> screenManager.getAudioManager().playClick());
        back.setOnAction(event -> screenManager.showMainMenu());

        VBox card = new VBox(13, title, subtitle, controls, objective, features, advice, back);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(28, 46, 28, 46));
        card.setMaxWidth(900);
        card.setBorder(new Border(new BorderStroke(
                Color.web("#9a6ab2"),
                javafx.scene.layout.BorderStrokeStyle.SOLID,
                new CornerRadii(12),
                new BorderWidths(2))));
        card.setStyle("-fx-background-color: rgba(13, 7, 20, 0.96);"
                + "-fx-background-radius: 12px;-fx-border-radius: 12px;");
        getChildren().add(card);
    }

    private Label createSection(String heading, String body) {
        Label section = new Label(heading + "\n" + body);
        section.setWrapText(true);
        section.setAlignment(Pos.CENTER);
        section.setMaxWidth(780);
        section.setStyle("-fx-text-fill: #eadff0;-fx-font-size: 14px;-fx-line-spacing: 4px;"
                + "-fx-background-color: #1b1025;-fx-background-radius: 8px;"
                + "-fx-border-color: #44245a;-fx-border-radius: 8px;"
                + "-fx-padding: 11px;");
        return section;
    }
}
