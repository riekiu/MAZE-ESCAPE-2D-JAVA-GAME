package com.disorient.screen;

import com.disorient.manager.ScreenManager;
import com.disorient.model.CampaignMap;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/** Selects a campaign location independently from the Mansion's existing floors. */
public class CampaignMapScreen extends StackPane {

    private static final String GOLD = "#e8c98a";
    private final ScreenManager screenManager;

    public CampaignMapScreen(ScreenManager screenManager) {
        this.screenManager = screenManager;
        setPadding(new Insets(28, 42, 24, 42));
        setStyle("-fx-background-color: linear-gradient(to bottom right, #08050e, #1c1028, #09060f);");

        Button back = actionButton("←  MAIN MENU", false);
        back.setOnAction(event -> screenManager.showMainMenu());
        Label eyebrow = new Label("SELECT YOUR DESTINATION");
        eyebrow.setStyle("-fx-text-fill: #ad8ac2;-fx-font-size: 11px;-fx-font-weight: bold;"
                + "-fx-letter-spacing: 4px;");
        Label title = new Label("CAMPAIGN MAP");
        title.setStyle("-fx-text-fill: #f4e7ce;-fx-font-size: 38px;-fx-font-weight: bold;"
                + "-fx-letter-spacing: 7px;-fx-effect: dropshadow(gaussian, #8b2cff, 18, 0.45, 0, 0);");
        VBox heading = new VBox(5, eyebrow, title);
        heading.setAlignment(Pos.CENTER);
        HBox header = new HBox(back, heading);
        header.setAlignment(Pos.CENTER);
        HBox.setHgrow(heading, Priority.ALWAYS);

        HBox cards = new HBox(24);
        cards.setAlignment(Pos.CENTER);
        for (CampaignMap map : CampaignMap.values()) {
            cards.getChildren().add(createMapCard(map));
        }
        VBox content = new VBox(30, header, cards);
        content.setAlignment(Pos.CENTER);
        getChildren().add(content);
    }

    private VBox createMapCard(CampaignMap map) {
        boolean unlocked = screenManager.getGameManager().isCampaignMapUnlocked(map);
        boolean completed = map == CampaignMap.HOSPITAL
                && screenManager.getGameManager().isCampaignCompleted();
        Canvas preview = createPreview(map);
        preview.setWidth(340);
        preview.setHeight(176);

        Label name = new Label(map.getDisplayName().toUpperCase());
        name.setStyle("-fx-text-fill: #f4e7ce;-fx-font-size: 22px;-fx-font-weight: bold;"
                + "-fx-letter-spacing: 3px;");
        Label description = new Label(map.getDescription());
        description.setWrapText(true);
        description.setMinHeight(56);
        description.setStyle("-fx-text-fill: #c7b8d0;-fx-font-size: 12px;");
        Label status = new Label(completed ? "CAMPAIGN COMPLETE"
                : unlocked ? "●  UNLOCKED" : "▣  LOCKED");
        status.setStyle("-fx-text-fill: " + (unlocked ? GOLD : "#887f8d")
                + ";-fx-font-size: 10px;-fx-font-weight: bold;-fx-letter-spacing: 2px;");

        Button play = actionButton(unlocked
                ? completed ? "PLAY AGAIN" : "PLAY " + map.getDisplayName().toUpperCase()
                : "LOCKED", true);
        play.setMaxWidth(Double.MAX_VALUE);
        play.setDisable(!unlocked);
        play.setOnAction(event -> screenManager.startCampaignMap(map));

        VBox card = new VBox(12, preview, name, description, status, play);
        card.setAlignment(Pos.TOP_LEFT);
        card.setPadding(new Insets(18));
        card.setPrefWidth(380);
        card.setMaxWidth(380);
        card.setStyle("-fx-background-color: linear-gradient(to bottom, #21162b, #110d18);"
                + "-fx-border-color: " + (unlocked ? "#765987" : "#3b3442")
                + ";-fx-border-width: 1px;-fx-border-radius: 10px;-fx-background-radius: 10px;");
        VBox.setVgrow(description, Priority.ALWAYS);
        preview.setEffect(new javafx.scene.effect.DropShadow(12, Color.rgb(0, 0, 0, 0.5)));
        return card;
    }

    private Canvas createPreview(CampaignMap map) {
        Canvas canvas = new Canvas(340, 176);
        GraphicsContext graphics = canvas.getGraphicsContext2D();
        if (map == CampaignMap.MANSION) {
            drawMansionPreview(graphics);
        } else if (map == CampaignMap.CEMETERY) {
            drawCemeteryPreview(graphics);
        } else {
            drawHospitalPreview(graphics);
        }
        return canvas;
    }

    private void drawMansionPreview(GraphicsContext graphics) {
        graphics.setFill(new javafx.scene.paint.LinearGradient(
                0, 0, 340, 176, false, javafx.scene.paint.CycleMethod.NO_CYCLE,
                new javafx.scene.paint.Stop(0, Color.web("#24182a")),
                new javafx.scene.paint.Stop(1, Color.web("#100d17"))));
        graphics.fillRect(0, 0, 340, 176);
        for (int y = 0; y < 176; y += 22) {
            graphics.setStroke(Color.web("#211713"));
            graphics.strokeLine(0, y, 340, y);
            graphics.setStroke(Color.web("#75513b", 0.42));
            graphics.strokeLine(0, y + 2, 340, y + 2);
            for (int x = (y / 22 % 2 == 0 ? 0 : 38); x < 340; x += 76) {
                graphics.setStroke(Color.web("#171216"));
                graphics.strokeLine(x, y + 3, x, y + 21);
            }
        }
        graphics.setFill(Color.rgb(0, 0, 0, 0.4));
        graphics.fillRoundRect(103, 23, 134, 133, 5, 5);
        graphics.setFill(Color.web("#472736"));
        graphics.fillRoundRect(100, 19, 134, 133, 5, 5);
        graphics.setStroke(Color.web("#b08c5c"));
        graphics.setLineWidth(2);
        graphics.strokeRoundRect(104, 23, 126, 125, 3, 3);
        graphics.setStroke(Color.web("#76513d"));
        graphics.setLineWidth(1);
        graphics.strokeRoundRect(111, 30, 112, 111, 3, 3);
        for (int x : new int[]{57, 283}) {
            graphics.setFill(Color.web("#17121b"));
            graphics.fillRect(x - 18, 31, 36, 52);
            graphics.setStroke(Color.web("#99734f"));
            graphics.strokeRect(x - 21, 28, 42, 58);
            graphics.setFill(Color.web("#695174"));
            graphics.fillOval(x - 10, 39, 20, 20);
            graphics.setFill(Color.web("#211725"));
            graphics.fillOval(x - 5, 48, 3, 3);
            graphics.fillOval(x + 2, 48, 3, 3);
        }
        graphics.setFill(Color.web("#17121a"));
        graphics.fillOval(158, 43, 24, 24);
        graphics.setFill(Color.web("#bc9a62"));
        graphics.fillOval(167, 49, 6, 6);
        graphics.setStroke(Color.web("#b39365"));
        graphics.setLineWidth(1.2);
        graphics.strokeLine(170, 55, 170, 68);
    }

    private void drawCemeteryPreview(GraphicsContext graphics) {
        graphics.setFill(new javafx.scene.paint.LinearGradient(
                0, 0, 0, 176, false, javafx.scene.paint.CycleMethod.NO_CYCLE,
                new javafx.scene.paint.Stop(0, Color.web("#1d3022")),
                new javafx.scene.paint.Stop(1, Color.web("#0e1a12"))));
        graphics.fillRect(0, 0, 340, 176);
        graphics.setFill(Color.web("#adbbc0", 0.1));
        graphics.fillOval(228, 12, 72, 72);
        graphics.setStroke(Color.web("#657461"));
        graphics.setLineWidth(5);
        graphics.beginPath();
        graphics.moveTo(0, 111);
        graphics.bezierCurveTo(80, 100, 63, 66, 143, 77);
        graphics.bezierCurveTo(222, 87, 200, 139, 340, 112);
        graphics.stroke();
        graphics.setStroke(Color.web("#8b8065", 0.68));
        graphics.setLineWidth(15);
        graphics.beginPath();
        graphics.moveTo(0, 111);
        graphics.bezierCurveTo(80, 100, 63, 66, 143, 77);
        graphics.bezierCurveTo(222, 87, 200, 139, 340, 112);
        graphics.stroke();
        graphics.setStroke(Color.web("#514b3d"));
        graphics.setLineWidth(10);
        graphics.beginPath();
        graphics.moveTo(0, 111);
        graphics.bezierCurveTo(80, 100, 63, 66, 143, 77);
        graphics.bezierCurveTo(222, 87, 200, 139, 340, 112);
        graphics.stroke();
        for (int x : new int[]{23, 77, 129, 190, 252, 312}) {
            drawPreviewGravestone(graphics, x, x % 2 == 0 ? 43 : 128);
        }
        drawPreviewDeadTree(graphics, 290, 24);
        drawPreviewDeadTree(graphics, 47, 136);
        graphics.setStroke(Color.web("#606c52"));
        graphics.setLineWidth(2);
        for (int x = 6; x < 334; x += 24) {
            graphics.strokeLine(x, 6, x, 29);
        }
        graphics.strokeLine(2, 14, 338, 14);
        graphics.strokeLine(2, 24, 338, 24);
        graphics.setFill(Color.rgb(168, 190, 182, 0.12));
        graphics.fillOval(3, 28, 116, 42);
        graphics.fillOval(177, 107, 153, 41);
    }

    private void drawHospitalPreview(GraphicsContext graphics) {
        graphics.setFill(new javafx.scene.paint.LinearGradient(
                0, 0, 0, 176, false, javafx.scene.paint.CycleMethod.NO_CYCLE,
                new javafx.scene.paint.Stop(0, Color.web("#465153")),
                new javafx.scene.paint.Stop(1, Color.web("#222b2e"))));
        graphics.fillRect(0, 0, 340, 176);
        for (int x = 0; x < 340; x += 28) {
            for (int y = 0; y < 176; y += 28) {
                graphics.setFill(((x + y) / 28) % 2 == 0
                        ? Color.web("#3c4546") : Color.web("#30393b"));
                graphics.fillRect(x, y, 28, 28);
                graphics.setStroke(Color.web("#1f282a"));
                graphics.strokeRect(x, y, 28, 28);
            }
        }
        graphics.setFill(Color.web("#282e30"));
        graphics.fillRoundRect(0, 66, 340, 48, 4, 4);
        graphics.fillRoundRect(67, 0, 48, 176, 4, 4);
        graphics.fillRoundRect(231, 0, 48, 176, 4, 4);
        graphics.setStroke(Color.web("#91958b", 0.6));
        graphics.setLineWidth(2);
        graphics.strokeLine(8, 72, 330, 72);
        graphics.strokeLine(8, 108, 330, 108);
        graphics.setFill(Color.rgb(0, 0, 0, 0.45));
        for (int[] point : new int[][]{{135, 27}, {177, 27}, {135, 125}, {177, 125}}) {
            graphics.fillOval(point[0] + 2, point[1] + 30, 37, 8);
            graphics.setFill(Color.web("#69716e"));
            graphics.fillRoundRect(point[0], point[1], 37, 30, 3, 3);
            graphics.setFill(Color.web("#b2b2a5"));
            graphics.fillRect(point[0] + 4, point[1] + 4, 12, 9);
            graphics.setFill(Color.web("#77817b"));
            graphics.fillRect(point[0] + 18, point[1] + 4, 15, 21);
            graphics.setFill(Color.rgb(0, 0, 0, 0.45));
        }
        graphics.setFill(Color.web("#8b383e", 0.72));
        graphics.fillOval(202, 78, 24, 15);
        graphics.setFill(Color.web("#a7464c"));
        graphics.fillRect(307, 20, 4, 30);
        graphics.fillRect(294, 33, 30, 4);
        graphics.setFill(Color.web("#f0d39b"));
        graphics.fillOval(85, 20, 12, 5);
        graphics.setEffect(new Glow(0.5));
        graphics.fillOval(85, 20, 12, 5);
        graphics.setEffect(null);
    }

    private void drawPreviewGravestone(GraphicsContext graphics, double x, double y) {
        graphics.setFill(Color.rgb(0, 0, 0, 0.42));
        graphics.fillOval(x - 4, y + 20, 30, 7);
        graphics.setFill(Color.web("#7c8674"));
        graphics.fillRoundRect(x, y, 22, 24, 10, 10);
        graphics.setStroke(Color.web("#c2c2a5"));
        graphics.setLineWidth(1.5);
        graphics.strokeLine(x + 11, y + 5, x + 11, y + 16);
        graphics.strokeLine(x + 6, y + 10, x + 16, y + 10);
    }

    private void drawPreviewDeadTree(GraphicsContext graphics, double x, double y) {
        graphics.setStroke(Color.web("#493f31"));
        graphics.setLineWidth(5);
        graphics.strokeLine(x, y + 39, x + 1, y + 9);
        graphics.setLineWidth(3);
        graphics.strokeLine(x + 1, y + 20, x - 13, y + 3);
        graphics.strokeLine(x + 1, y + 17, x + 18, y + 1);
        graphics.strokeLine(x, y + 30, x - 13, y + 23);
    }

    private Button actionButton(String text, boolean primary) {
        Button button = new Button(text);
        button.setPadding(new Insets(11, 16, 11, 16));
        button.setStyle(primary
                ? "-fx-background-color: #6b3c7e;-fx-border-color: #d7b67a;-fx-border-radius: 6px;"
                    + "-fx-background-radius: 6px;-fx-text-fill: #fff1d4;-fx-font-size: 11px;"
                    + "-fx-font-weight: bold;-fx-letter-spacing: 1px;-fx-cursor: hand;"
                : "-fx-background-color: rgba(28,19,36,0.8);-fx-border-color: #6e5379;"
                    + "-fx-border-radius: 6px;-fx-background-radius: 6px;-fx-text-fill: #d6c6dc;"
                    + "-fx-font-size: 10px;-fx-font-weight: bold;-fx-letter-spacing: 1px;-fx-cursor: hand;");
        button.setOnMousePressed(event -> screenManager.getAudioManager().playClick());
        return button;
    }
}
