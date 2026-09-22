package com.mazeescape.screen;

import com.mazeescape.manager.ScreenManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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

        VBox information = new VBox(13,
                createSection("1. STARTING THE GAME",
                        "PLAY loads the saved current level and keeps the saved score, lives, and "
                                + "progress. CAMPAIGN lets you choose an unlocked level. Starting a selected "
                                + "level resets that level's lives to 3. The game contains 20 levels."),
                createSection("2. LEVEL SETUP",
                        "Each level creates a square grid using: grid size = 8 + level number. "
                                + "That produces Level 1 at 9x9 and Level 20 at 28x28. The maze is carved "
                                + "from walls into connected floor paths using a deterministic seed: "
                                + "0x5EED + (level number x 7919). The same level therefore recreates "
                                + "the same maze layout."),
                createSection("3. DIFFICULTY AND TIME LIMITS",
                        "Levels 1-5 are EASY with 60 seconds. Levels 6-8 are NORMAL with 90 seconds. "
                                + "Levels 9-13 are HARD with 120 seconds. Levels 14-17 are NIGHTMARE "
                                + "with 150 seconds. Levels 18-20 are NIGHTMARE with 180 seconds. "
                                + "Every level starts with 3 lives."),
                createSection("4. CONTROLS",
                        "W / A / S / D or ARROW KEYS: move.\n"
                                + "ESC: pause or resume.\n"
                                + "The game updates movement at a fixed 60-step-per-second simulation. "
                                + "Player speed is 200 pixels per second, and diagonal movement is "
                                + "normalized so diagonal travel is not faster."),
                createSection("5. MOVEMENT AND WALL COLLISION",
                        "The player has a 32x32 collision body. Before each horizontal or vertical move, "
                                + "the game checks all four player corners against maze tiles. A move is "
                                + "accepted only when none of the corners is inside a wall or outside the "
                                + "maze. The visible character is larger than the collision body, so the "
                                + "sprite size does not change movement physics."),
                createSection("6. CHARACTER ANIMATION",
                        "The player uses separate directional images from img/player. Down and up each "
                                + "cycle through frames 1-4. Left and right cycle through frames 1-3. "
                                + "A new frame is selected every 0.12 seconds while moving. The animation "
                                + "returns to frame 1 when the player stops, and the selected direction "
                                + "is retained until another direction is pressed."),
                createSection("7. FOG OF WAR AND FIELD NOTES",
                        "A dark fog covers the entire maze and the Field Notes map. The player reveals "
                                + "a circular sight radius of 165 pixels with a 45-pixel soft radial edge. "
                                + "The light center follows the player's collision-body center automatically. "
                                + "The minimap uses the same proportional radius and keeps the player marker "
                                + "visible above the fog."),
                createSection("8. FINDING THE KEY",
                        "The key is placed on a floor tile selected from the maze's farthest reachable "
                                + "floor area from the starting tile. The player collects it by rectangle "
                                + "intersection with the key. Collection marks the key as obtained, hides "
                                + "its visual marker, changes the objective, activates the exit, and may "
                                + "move the exit to another valid floor tile at least four maze steps "
                                + "from the key when possible."),
                createSection("9. REACHING THE EXIT",
                        "The exit begins inactive. It becomes active only after the key is collected. "
                                + "The player wins the level when the player rectangle intersects the "
                                + "active exit rectangle. If the exit has moved, the HUD reports that "
                                + "the exit moved. The final level leads to the victory screen."),
                createSection("10. TIMER AND LIVES",
                        "The timer decreases using elapsed seconds while the game is running. The HUD "
                                + "displays the remaining value as MM:SS using ceiling to whole seconds. "
                                + "Pausing stops timer updates. When the timer reaches zero, it is clamped "
                                + "to zero, one life is lost, the game stops, and the game-over flow opens "
                                + "after a short delay."),
                createSection("11. SCORE CALCULATION",
                        "At level completion:\n"
                                + "Level score = 1,000 base points\n"
                                + "             + remaining seconds x 10\n"
                                + "             + remaining lives x 250.\n"
                                + "Only positive level scores are added to the total score. Finishing "
                                + "with more time and more lives produces a higher score."),
                createSection("12. LEADERBOARD CALCULATION",
                        "The completion time is calculated as level time limit minus remaining seconds, "
                                + "never below zero. Only the best (lowest) completion time is stored for "
                                + "each level. The status rating compares completion time to that level's "
                                + "time limit: 5 - EXCELLENT at 0-20%; 4 - GOOD at 21-40%; "
                                + "3 - AVERAGE at 41-60%; 2 - BELOW AVERAGE at 61-80%; "
                                + "1 - POOR above 80%."),
                createSection("13. LEVEL PROGRESSION",
                        "Completing a level adds its score, records its best time, unlocks the next level, "
                                + "sets the next level as current, and opens the level-complete screen. "
                                + "The level-complete screen can start the next level or retry the current "
                                + "one. The campaign displays the available level buttons."),
                createSection("14. SAVING AND PLAY",
                        "The save file stores current level, highest unlocked level, total score, lives, "
                                + "and best completion times. Progress is saved when a level starts, when "
                                + "the game screen is left, after completion, after game over, and when "
                                + "the application saves progress. PLAY loads the last saved current level "
                                + "instead of automatically starting from Level 1."),
                createSection("15. PAUSE, SETTINGS, AND RETRY",
                        "Use the single PAUSE button in the HUD or ESC. Pausing freezes movement and "
                                + "the timer, clears held movement keys, and opens the pause menu. The "
                                + "pause menu provides resume, settings, retry, and return controls. "
                                + "Retrying a level starts it again with the selected life state."),
                createSection("16. HUD AND FIELD NOTES",
                        "The HUD shows level, difficulty, grid size, timer, lives, score, and objective. "
                                + "FIELD NOTES shows a scaled maze map with player, key, and exit markers. "
                                + "The same fog and player-centered light are applied to that map."),
                createSection("17. AUDIO AND SCREEN FLOW",
                        "The game synchronizes master, music, and sound-effect volume settings. Main-menu "
                                + "music plays on menu screens, haunted music plays during gameplay, and "
                                + "movement, key pickup, exit unlock, level completion, game over, and "
                                + "button actions can trigger sound effects."),
                createSection("18. SURVIVAL TIPS",
                        "Plan routes before moving deep into the fog, use the minimap to orient yourself, "
                                + "watch the objective text after collecting the key, preserve lives for "
                                + "the score bonus, and finish quickly to improve both score and leaderboard "
                                + "rating.")
        );
        information.setAlignment(Pos.CENTER);
        information.setPadding(new Insets(4));

        ScrollPane scroll = new ScrollPane(information);
        scroll.setFitToWidth(true);
        scroll.setPrefViewportWidth(860);
        scroll.setPrefViewportHeight(560);
        scroll.setMaxWidth(900);
        scroll.setStyle("-fx-background: transparent;-fx-background-color: transparent;");

        Button back = new Button("BACK TO MAIN MENU");
        back.setPrefWidth(280);
        back.setPrefHeight(44);
        back.setStyle("-fx-background-color: #170d22;-fx-border-color: #6d3a91;"
                + "-fx-border-width: 1.5px;-fx-border-radius: 8px;-fx-background-radius: 8px;"
                + "-fx-text-fill: white;-fx-font-size: 13px;-fx-font-weight: bold;"
                + "-fx-letter-spacing: 1.5px;-fx-cursor: hand;");
        back.setOnMousePressed(event -> screenManager.getAudioManager().playClick());
        back.setOnAction(event -> screenManager.showMainMenu());

        VBox card = new VBox(13, title, subtitle, scroll, back);
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
