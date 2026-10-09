package com.disorient.screen;

import com.disorient.manager.ScreenManager;
import com.disorient.model.CampaignMap;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class CampaignMapCompleteScreen extends StackPane {

    public CampaignMapCompleteScreen(CampaignMap completedMap, ScreenManager screenManager) {
        setStyle("-fx-background-color: linear-gradient(to bottom, #050008, #16051f, #050008);");
        boolean campaignComplete = screenManager.getGameManager().isCampaignCompleted();
        Label title = new Label(campaignComplete ? "CAMPAIGN COMPLETE" : "LOCATION CLEARED");
        title.setStyle("-fx-text-fill: white;-fx-font-size: 40px;-fx-font-weight: bold;"
                + "-fx-letter-spacing: 4px;-fx-effect: dropshadow(gaussian, #8b2cff, 20, 0.6, 0, 0);");
        Label location = new Label(completedMap.getDisplayName().toUpperCase() + "  •  ESCAPED");
        location.setStyle("-fx-text-fill: #e8c98a;-fx-font-size: 17px;-fx-font-weight: bold;"
                + "-fx-letter-spacing: 2px;");
        CampaignMap next = completedMap.ordinal() + 1 < CampaignMap.values().length
                ? CampaignMap.values()[completedMap.ordinal() + 1] : null;
        Label message = new Label(campaignComplete
                ? "You survived every location. The nightmare is over."
                : "UNLOCKED: " + next.getDisplayName().toUpperCase());
        message.setStyle("-fx-text-fill: #c9a8d8;-fx-font-size: 15px;");

        Button maps = createButton("RETURN TO CAMPAIGN MAP", screenManager);
        maps.setOnAction(event -> screenManager.showCampaignMaps());
        Button menu = createButton("MAIN MENU", screenManager);
        menu.setOnAction(event -> screenManager.showMainMenu());
        VBox content = new VBox(18, title, location, message, maps, menu);
        content.setAlignment(Pos.CENTER);
        getChildren().add(content);
    }

    private Button createButton(String text, ScreenManager screenManager) {
        Button button = new Button(text);
        button.setPrefWidth(320);
        button.setPrefHeight(48);
        button.setStyle("-fx-background-color: #170d22;-fx-border-color: #6d3a91;"
                + "-fx-border-width: 1.5px;-fx-border-radius: 8px;-fx-background-radius: 8px;"
                + "-fx-text-fill: white;-fx-font-size: 13px;-fx-font-weight: bold;"
                + "-fx-letter-spacing: 2px;-fx-cursor: hand;");
        button.setOnMousePressed(event -> screenManager.getAudioManager().playClick());
        return button;
    }
}
