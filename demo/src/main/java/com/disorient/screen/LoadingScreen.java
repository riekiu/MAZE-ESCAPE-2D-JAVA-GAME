package com.disorient.screen;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;

public class LoadingScreen extends StackPane {

    private final List<Animation> animations = new ArrayList<>();

    public LoadingScreen(Runnable onFinished) {
        setStyle("-fx-background-color: radial-gradient(center 50% 43%, radius 65%, #281237, #100817 55%, #050308);");

        Circle moteOne = createMote(3.5, 0.82);
        Circle moteTwo = createMote(2.5, 0.55);
        Circle moteThree = createMote(2, 0.68);
        moteOne.setTranslateX(-260);
        moteOne.setTranslateY(-142);
        moteTwo.setTranslateX(238);
        moteTwo.setTranslateY(-82);
        moteThree.setTranslateX(184);
        moteThree.setTranslateY(178);

        StackPane portal = createPortal();
        VBox titleBlock = createTitleBlock();
        Label status = new Label("THE HOUSE IS WAKING");
        status.setStyle("-fx-text-fill: #dfb9ed;-fx-font-size: 10px;-fx-font-weight: bold;"
                + "-fx-letter-spacing: 2.5px;");
        HBox loadingDots = createLoadingDots();
        VBox content = new VBox(18, portal, titleBlock, status, loadingDots);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(20));

        Label footer = new Label("A MAZE IS WAITING IN THE DARK");
        footer.setStyle("-fx-text-fill: #776283;-fx-font-size: 9px;-fx-font-weight: bold;"
                + "-fx-letter-spacing: 2px;");
        StackPane.setAlignment(footer, Pos.BOTTOM_CENTER);
        StackPane.setMargin(footer, new Insets(0, 0, 30, 0));

        getChildren().addAll(moteOne, moteTwo, moteThree, content, footer);

        FadeTransition intro = new FadeTransition(Duration.millis(850), content);
        intro.setFromValue(0);
        intro.setToValue(1);
        intro.play();

        Timeline statusCycle = new Timeline(
                new KeyFrame(Duration.ZERO, event -> status.setText("THE HOUSE IS WAKING")),
                new KeyFrame(Duration.millis(700), event -> status.setText("THE FOG IS GATHERING")),
                new KeyFrame(Duration.millis(1400), event -> status.setText("THE SPIRIT IS NEAR")),
                new KeyFrame(Duration.millis(2100), event -> status.setText("FIND THE WAY OUT")));
        statusCycle.setCycleCount(Timeline.INDEFINITE);
        statusCycle.play();

        PauseTransition pause = new PauseTransition(Duration.seconds(2.5));
        pause.setOnFinished(event -> {
            statusCycle.stop();
            intro.stop();
            animations.forEach(Animation::stop);
            if (onFinished != null) {
                onFinished.run();
            }
        });
        pause.play();
    }

    private StackPane createPortal() {
        Ellipse outerRing = new Ellipse(70, 88);
        outerRing.setFill(Color.TRANSPARENT);
        outerRing.setStroke(Color.web("#9d55bf", 0.72));
        outerRing.setStrokeWidth(2);
        outerRing.setEffect(new Glow(0.8));

        Ellipse innerRing = new Ellipse(57, 74);
        innerRing.setFill(Color.TRANSPARENT);
        innerRing.setStroke(Color.web("#d0a0df", 0.4));
        innerRing.setStrokeWidth(1.2);

        Arc arcOne = new Arc(0, 0, 78, 96, 28, 92);
        arcOne.setType(ArcType.OPEN);
        arcOne.setFill(Color.TRANSPARENT);
        arcOne.setStroke(Color.web("#e8c98a"));
        arcOne.setStrokeWidth(2.2);
        arcOne.setEffect(new Glow(0.65));

        Arc arcTwo = new Arc(0, 0, 78, 96, 205, 54);
        arcTwo.setType(ArcType.OPEN);
        arcTwo.setFill(Color.TRANSPARENT);
        arcTwo.setStroke(Color.web("#bd72dc", 0.88));
        arcTwo.setStrokeWidth(2);

        Group rings = new Group(outerRing, innerRing, arcOne, arcTwo);
        RotateTransition ringMotion = new RotateTransition(Duration.seconds(9), rings);
        ringMotion.setByAngle(360);
        ringMotion.setCycleCount(RotateTransition.INDEFINITE);
        animations.add(ringMotion);
        ringMotion.play();

        SVGPath spiritBody = new SVGPath();
        spiritBody.setContent("M40 8 C20 8 8 22 8 42 L8 67 C8 78 2 87 8 94 L24 84 L40 96 L56 84 L72 96 L88 84 L104 94 C110 86 104 77 104 66 L104 42 C104 22 90 8 72 8 Z");
        spiritBody.setFill(Color.web("#a858c6"));
        spiritBody.setEffect(new Glow(0.6));
        Circle leftEye = new Circle(39, 49, 4, Color.web("#ffe3ff"));
        Circle rightEye = new Circle(73, 49, 4, Color.web("#ffe3ff"));
        Circle mouth = new Circle(56, 66, 3, Color.web("#42164e"));
        Group spirit = new Group(spiritBody, leftEye, rightEye, mouth);
        spirit.setScaleX(0.78);
        spirit.setScaleY(0.78);
        TranslateTransition floatMotion = new TranslateTransition(Duration.seconds(1.3), spirit);
        floatMotion.setFromY(4);
        floatMotion.setToY(-4);
        floatMotion.setAutoReverse(true);
        floatMotion.setCycleCount(TranslateTransition.INDEFINITE);
        animations.add(floatMotion);
        floatMotion.play();

        StackPane portal = new StackPane(rings, spirit);
        portal.setPrefSize(180, 205);
        portal.setMaxSize(180, 205);
        return portal;
    }

    private VBox createTitleBlock() {
        Label eyebrow = new Label("A HAUNTED HOUSE MAZE");
        eyebrow.setStyle("-fx-text-fill: #c58bdd;-fx-font-size: 9px;-fx-font-weight: bold;"
                + "-fx-letter-spacing: 3px;");
        Label title = new Label("DISORIENT");
        title.setStyle("-fx-text-fill: #fff8eb;-fx-font-size: 40px;-fx-font-weight: bold;"
                + "-fx-letter-spacing: 8px;-fx-effect: dropshadow(gaussian, #8b2cff, 20, 0.55, 0, 0);");
        Label subtitle = new Label("FIND THE KEY. ESCAPE THE SPIRIT.");
        subtitle.setStyle("-fx-text-fill: #c8b5d1;-fx-font-size: 11px;-fx-letter-spacing: 2px;");
        VBox titleBlock = new VBox(7, eyebrow, title, subtitle);
        titleBlock.setAlignment(Pos.CENTER);
        return titleBlock;
    }

    private HBox createLoadingDots() {
        HBox dots = new HBox(9);
        dots.setAlignment(Pos.CENTER);
        Color[] colors = {
            Color.web("#8c4ca7"),
            Color.web("#c77dff"),
            Color.web("#f0d18b"),
            Color.web("#c77dff"),
            Color.web("#8c4ca7")
        };
        for (int index = 0; index < colors.length; index++) {
            Circle dot = new Circle(index == 2 ? 3.5 : 2.5, colors[index]);
            FadeTransition pulse = new FadeTransition(Duration.millis(650), dot);
            pulse.setFromValue(0.25);
            pulse.setToValue(1);
            pulse.setAutoReverse(true);
            pulse.setDelay(Duration.millis(index * 100));
            pulse.setCycleCount(FadeTransition.INDEFINITE);
            animations.add(pulse);
            pulse.play();
            dots.getChildren().add(dot);
        }
        return dots;
    }

    private Circle createMote(double radius, double opacity) {
        Circle mote = new Circle(radius, Color.web("#dbaff0", opacity));
        mote.setEffect(new Glow(0.7));
        return mote;
    }
}
