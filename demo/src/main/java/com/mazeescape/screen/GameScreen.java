package com.mazeescape.screen;

import com.mazeescape.controller.CollisionManager;
import com.mazeescape.manager.LifeManager;
import com.mazeescape.manager.ScreenManager;
import com.mazeescape.manager.ScoreManager;
import com.mazeescape.manager.TimerManager;
import com.mazeescape.model.Exit;
import com.mazeescape.model.Key;
import com.mazeescape.model.LevelData;
import com.mazeescape.model.Maze;
import com.mazeescape.model.Player;
import com.mazeescape.model.Tile;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.util.Duration;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class GameScreen extends BorderPane {

    private static final int TILE_SIZE = 64;
    private static final int VIEWPORT_WIDTH = 880;
    private static final int VIEWPORT_HEIGHT = 480;
    private static final int TARGET_FRAME_RATE = 60;
    private static final double TARGET_FRAME_TIME = 1.0 / TARGET_FRAME_RATE;
    private static final double PLAYER_RENDER_WIDTH = 76;
    private static final double PLAYER_RENDER_HEIGHT = 88;
    private static final Color FOG_COLOR = Color.rgb(10, 10, 16);
    private static final double LIGHT_FOG_OPACITY = 0.48;
    private static final double HEAVY_FOG_OPACITY = 0.98;
    private static final double BASE_SIGHT_RADIUS = 165;
    private static final double SIGHT_RADIUS_DECREASE_PER_LEVEL = 5;
    private static final double MIN_SIGHT_RADIUS = 70;
    private static final double SIGHT_EDGE_FEATHER = 45;

    private final ScreenManager screenManager;
    private final Player player = new Player(TILE_SIZE + 4, TILE_SIZE + 4);
    private final LevelData levelData;
    private final Key key;
    private final Exit exit;
    private final TimerManager timerManager = new TimerManager();
    private final LifeManager lifeManager = new LifeManager();
    private final ScoreManager scoreManager = new ScoreManager();
    private final CollisionManager collisionManager = new CollisionManager();
    private final Set<KeyCode> keysPressed = new HashSet<>();
    private Maze maze;
    private int rows;
    private int columns;
    private final int level;
    private final Image[][] playerFrames = {
            {
                    loadImage("/img/player/player_down_frame_1.png"),
                    loadImage("/img/player/player_down_frame_2.png"),
                    loadImage("/img/player/player_down_frame_3.png"),
                    loadImage("/img/player/player_down_frame_4.png")
            },
            {
                    loadImage("/img/player/player_left_frame_1.png"),
                    loadImage("/img/player/player_left_frame_2.png"),
                    loadImage("/img/player/player_left_frame_3.png")
            },
            {
                    loadImage("/img/player/player_right_frame_1.png"),
                    loadImage("/img/player/player_right_frame_2.png"),
                    loadImage("/img/player/player_right_frame_3.png")
            },
            {
                    loadImage("/img/player/player_up_frame_1.png"),
                    loadImage("/img/player/player_up_frame_2.png"),
                    loadImage("/img/player/player_up_frame_3.png"),
                    loadImage("/img/player/player_up_frame_4.png")
            }
    };
    private Pane mazePane;
    private Group fogLayer;
    private StackPane gameArea;
    private ScrollPane mazeViewport;
    private StackPane mazeContent;
    private ImageView playerShape;
    private Group keyShape;
    private Group exitShape;
    private Label timerLabel;
    private Label livesLabel;
    private Label scoreLabel;
    private Label objectiveLabel;
    private Pane miniMapPane;
    private Canvas miniMapFogLayer;
    private boolean[][] miniMapExplored;
    private int lastMiniMapPlayerRow = -1;
    private int lastMiniMapPlayerColumn = -1;
    private ImageView miniMapPlayer;
    private Label fieldNotesObjectiveLabel;
    private AnimationTimer gameLoop;
    private boolean paused;
    private PauseMenu pauseMenu;
    private GameSettingsScreen gameSettingsOverlay;
    private double animationTime;
    private double walkingAnimationTime;
    private double footstepDistance;
    private boolean playerMoving;
    private int playerDirectionRow;
    private int playerAnimationFrame;
    private boolean exitMoved;
    private int displayedLives;
    private double lastFogCenterX = Double.NaN;
    private double lastFogCenterY = Double.NaN;

    public GameScreen(int level) {
        this(level, ScreenManager.getActive());
    }

    public GameScreen(int level, ScreenManager screenManager) {
        this.level = level;
        this.screenManager = screenManager;
        this.levelData = new LevelData(level);
        this.rows = levelData.getGridSize();
        this.columns = levelData.getGridSize();
        generateMaze();
        int[] keyPosition = findRandomKeyFloor(1, 1);
        this.key = new Key(TILE_SIZE * keyPosition[1] + 20,
                TILE_SIZE * keyPosition[0] + 20);
        this.exit = new Exit(TILE_SIZE * (columns - 2) + 18,
                TILE_SIZE * (rows - 2) + 14);
        lifeManager.setLives(screenManager.getGameManager().getLives());
        displayedLives = lifeManager.getLives();
        createMazeModel();
        createInterface();
        setupInput();
        syncAudioSettings();
        startGame();
    }

    private void syncAudioSettings() {
        screenManager.getAudioManager().syncVolumes(
                screenManager.getSettingsManager().getMasterVolume(),
                screenManager.getSettingsManager().getMusicVolume(),
                screenManager.getSettingsManager().getSfxVolume());
    }

    private void generateMaze() {
        boolean[][] walls = new boolean[rows][columns];
        for (int row = 0; row < rows; row++) {
            java.util.Arrays.fill(walls[row], true);
        }
        Random random = new Random(levelData.getGenerationSeed());
        List<int[]> stack = new ArrayList<>();
        stack.add(new int[]{1, 1});
        walls[1][1] = false;
        while (!stack.isEmpty()) {
            int[] current = stack.get(stack.size() - 1);
            List<int[]> directions = new ArrayList<>(List.of(
                    new int[]{0, 2}, new int[]{0, -2},
                    new int[]{2, 0}, new int[]{-2, 0}));
            Collections.shuffle(directions, random);
            boolean carved = false;
            for (int[] direction : directions) {
                int nextRow = current[0] + direction[0];
                int nextColumn = current[1] + direction[1];
                if (nextRow > 0 && nextRow < rows - 1
                        && nextColumn > 0 && nextColumn < columns - 1
                        && walls[nextRow][nextColumn]) {
                    walls[current[0] + direction[0] / 2][current[1] + direction[1] / 2] = false;
                    walls[nextRow][nextColumn] = false;
                    stack.add(new int[]{nextRow, nextColumn});
                    carved = true;
                    break;
                }
            }
            if (!carved) {
                stack.remove(stack.size() - 1);
            }
        }
        carveExtraRoutes(walls, random);
        walls[1][1] = false;
        walls[rows - 2][columns - 2] = false;
        if ((rows & 1) == 0) {
            walls[rows - 2][columns - 3] = false;
        }
        if ((columns & 1) == 0) {
            walls[rows - 3][columns - 2] = false;
        }
        maze = new Maze(rows, columns);
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                maze.setTile(row, column, walls[row][column]
                        ? Tile.Type.WALL : Tile.Type.FLOOR);
            }
        }
    }

    /** Opens selected interior dividers in the spanning-tree maze to create loops. */
    private void carveExtraRoutes(boolean[][] walls, Random random) {
        List<int[]> possibleOpenings = new ArrayList<>();
        for (int row = 1; row < rows - 1; row++) {
            for (int column = 1; column < columns - 1; column++) {
                if (!walls[row][column]) continue;
                boolean joinsHorizontalFloors = column > 1 && column < columns - 2
                        && !walls[row][column - 1] && !walls[row][column + 1];
                boolean joinsVerticalFloors = row > 1 && row < rows - 2
                        && !walls[row - 1][column] && !walls[row + 1][column];
                if (joinsHorizontalFloors || joinsVerticalFloors) {
                    possibleOpenings.add(new int[]{row, column});
                }
            }
        }

        Collections.shuffle(possibleOpenings, random);
        double loopRate = 0.18 + 0.004 * (Math.max(1, Math.min(50, level)) - 1);
        int openings = (int) Math.round(possibleOpenings.size() * loopRate);
        for (int index = 0; index < openings; index++) {
            int[] opening = possibleOpenings.get(index);
            walls[opening[0]][opening[1]] = false;
        }
    }

    private void createMazeModel() {
        if (maze == null) {
            generateMaze();
        }
    }

    private int[] findRandomKeyFloor(int startRow, int startColumn) {
        int[][] distances = calculateFloorDistances(startRow, startColumn);
        int exitRow = rows - 2;
        int exitColumn = columns - 2;
        int greatestDistance = 0;

        for (int row = 1; row < rows - 1; row++) {
            for (int column = 1; column < columns - 1; column++) {
                if (distances[row][column] >= 0
                        && (row != exitRow || column != exitColumn)) {
                    greatestDistance = Math.max(greatestDistance, distances[row][column]);
                }
            }
        }

        // Randomize among reachable, relatively distant tiles so the key still takes
        // exploration without repeatedly spawning right beside the player.
        int minimumDistance = Math.max(3, (int) Math.ceil(greatestDistance * 0.65));
        List<int[]> candidates = new ArrayList<>();
        for (int row = 1; row < rows - 1; row++) {
            for (int column = 1; column < columns - 1; column++) {
                if (distances[row][column] >= minimumDistance
                        && (row != startRow || column != startColumn)
                        && (row != exitRow || column != exitColumn)) {
                    candidates.add(new int[]{row, column});
                }
            }
        }

        if (candidates.isEmpty()) {
            for (int row = 1; row < rows - 1; row++) {
                for (int column = 1; column < columns - 1; column++) {
                    if (distances[row][column] > 0
                            && (row != exitRow || column != exitColumn)) {
                        candidates.add(new int[]{row, column});
                    }
                }
            }
        }
        return candidates.get(new Random().nextInt(candidates.size()));
    }

    private void moveExitAfterKeyCollection() {
        int[] newExitPosition = findRandomExitFloor();
        exit.setPosition(
                TILE_SIZE * newExitPosition[1] + 18,
                TILE_SIZE * newExitPosition[0] + 14);
        exitMoved = true;
        updateVisualPositions();
    }

    private int[] findRandomExitFloor() {
        int playerRow = (int) (player.getY() / TILE_SIZE);
        int playerColumn = (int) (player.getX() / TILE_SIZE);
        int keyRow = (int) (key.getY() / TILE_SIZE);
        int keyColumn = (int) (key.getX() / TILE_SIZE);
        int currentExitRow = (int) (exit.getY() / TILE_SIZE);
        int currentExitColumn = (int) (exit.getX() / TILE_SIZE);
        int minimumDistance = 4;
        int[][] distances = calculateFloorDistances(keyRow, keyColumn);

        List<int[]> candidates = new ArrayList<>();
        List<int[]> distantCandidates = new ArrayList<>();
        for (int row = 1; row < rows - 1; row++) {
            for (int column = 1; column < columns - 1; column++) {
                if (!maze.isWall(row, column)
                        && (row != 1 || column != 1)
                        && (row != playerRow || column != playerColumn)
                        && (row != keyRow || column != keyColumn)
                        && (row != currentExitRow || column != currentExitColumn)) {
                    int[] candidate = {row, column};
                    candidates.add(candidate);
                    if (distances[row][column] >= minimumDistance) {
                        distantCandidates.add(candidate);
                    }
                }
            }
        }

        if (candidates.isEmpty()) {
            throw new IllegalStateException("No valid floor tile is available for the exit.");
        }

        List<int[]> selection = distantCandidates.isEmpty() ? candidates : distantCandidates;
        return selection.get(new Random().nextInt(selection.size()));
    }

    private int[][] calculateFloorDistances(int startRow, int startColumn) {
        int[][] distances = new int[rows][columns];
        for (int row = 0; row < rows; row++) {
            java.util.Arrays.fill(distances[row], -1);
        }

        List<int[]> queue = new ArrayList<>();
        queue.add(new int[]{startRow, startColumn});
        distances[startRow][startColumn] = 0;

        for (int index = 0; index < queue.size(); index++) {
            int[] current = queue.get(index);
            for (int[] direction : new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
                int nextRow = current[0] + direction[0];
                int nextColumn = current[1] + direction[1];
                if (nextRow >= 0 && nextRow < rows
                        && nextColumn >= 0 && nextColumn < columns
                        && distances[nextRow][nextColumn] == -1
                        && !maze.isWall(nextRow, nextColumn)) {
                    distances[nextRow][nextColumn] = distances[current[0]][current[1]] + 1;
                    queue.add(new int[]{nextRow, nextColumn});
                }
            }
        }
        return distances;
    }

    private void createInterface() {
        configureGameBackground();
        Label levelLabel = new Label(String.format("LEVEL %02d  •  %s  •  %dx%d",
                level, levelData.getDifficulty(), rows, columns));
        timerLabel = new Label();
        livesLabel = new Label();
        scoreLabel = new Label();
        objectiveLabel = new Label();
        ImageView titleLogo = new ImageView(loadImage("/img/menu/landscape_logo.png"));
        titleLogo.setFitWidth(118);
        titleLogo.setFitHeight(62);
        titleLogo.setPreserveRatio(true);
        titleLogo.setSmooth(true);
        titleLogo.setEffect(new DropShadow(10, Color.rgb(139, 44, 255, 0.45)));
        HBox hud = new HBox(
                8,
                titleLogo,
                createHudPanel("LEVEL", levelLabel, createHudIcon("level")),
                createHudPanel("TIME", timerLabel, createHudIcon("timer")),
                createHudPanel("LIVES", livesLabel, createHudIcon("heart")),
                createHudPanel("SCORE", scoreLabel, createHudIcon("score")),
                createHudPanel("OBJECTIVE", objectiveLabel, createHudIcon("key")),
                createPauseButton()
        );
        hud.setPadding(new javafx.geometry.Insets(10, 18, 10, 18));
        hud.setAlignment(Pos.CENTER);
        hud.setPrefHeight(82);
        hud.setStyle("-fx-background-color: rgba(8, 4, 16, 0.97);"
                + "-fx-border-color: #b08d57;-fx-border-width: 0 0 1px 0;");
        setTop(hud);

        mazePane = new Pane();
        mazePane.setPrefSize(columns * TILE_SIZE, rows * TILE_SIZE);
        mazePane.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        mazePane.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        createMazeVisuals();
        mazeContent = new StackPane(mazePane);
        mazeContent.setPrefSize(
                Math.max(columns * TILE_SIZE, VIEWPORT_WIDTH),
                Math.max(rows * TILE_SIZE, VIEWPORT_HEIGHT));
        mazeContent.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        mazeContent.setAlignment(Pos.CENTER);
        mazeViewport = new ScrollPane(mazeContent);
        mazeViewport.setPrefViewportWidth(VIEWPORT_WIDTH);
        mazeViewport.setPrefViewportHeight(VIEWPORT_HEIGHT);
        mazeViewport.setMinViewportWidth(0);
        mazeViewport.setMinViewportHeight(0);
        mazeViewport.setMaxWidth(VIEWPORT_WIDTH);
        mazeViewport.setMaxHeight(VIEWPORT_HEIGHT);
        mazeViewport.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mazeViewport.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mazeViewport.setPannable(false);
        mazeViewport.setFitToWidth(false);
        mazeViewport.setFitToHeight(false);
        mazeViewport.setStyle("-fx-background: #08040f;-fx-background-color: #08040f;"
                + "-fx-border-color: #5c3474;-fx-border-width: 1px;");
        StackPane mazeFrame = createHauntedFrame(mazeViewport);
        HBox playfield = new HBox(16, mazeFrame, createLegendPanel());
        playfield.setAlignment(Pos.CENTER);
        playfield.setPadding(new javafx.geometry.Insets(12, 18, 12, 18));
        gameArea = new StackPane(playfield);
        gameArea.setAlignment(Pos.CENTER);
        setCenter(gameArea);
        Label controlsLabel = new Label("WASD / ARROWS: MOVE    |    ESC: PAUSE    |    COLLECT THE KEY, THEN REACH THE EXIT");
        controlsLabel.setMaxWidth(Double.MAX_VALUE);
        controlsLabel.setAlignment(Pos.CENTER);
        controlsLabel.setPadding(new javafx.geometry.Insets(12));
        controlsLabel.setStyle("-fx-background-color: rgba(8, 4, 16, 0.98);"
                + "-fx-border-color: #b08d57;-fx-border-width: 1px 0 0 0;"
                + "-fx-text-fill: #d8c2e4;-fx-font-size: 12px;"
                + "-fx-font-weight: bold;-fx-letter-spacing: 1px;");
        setBottom(controlsLabel);
        updateCamera();
        updateHUD();
    }

    private StackPane createHauntedFrame(ScrollPane viewport) {
        StackPane frame = new StackPane(viewport);
        frame.setPadding(new javafx.geometry.Insets(34, 22, 30, 22));
        frame.setStyle("-fx-background-color: linear-gradient(to bottom, #180b23, #08050d);"
                + "-fx-border-color: #c49a5a;-fx-border-width: 2px;-fx-border-radius: 14px;"
                + "-fx-background-radius: 14px;");
        frame.setBorder(new Border(new BorderStroke(
                Color.web("#b08d57"),
                javafx.scene.layout.BorderStrokeStyle.SOLID,
                new CornerRadii(14),
                new BorderWidths(2)
        )));
        frame.setEffect(new DropShadow(30, Color.rgb(0, 0, 0, 0.82)));

        Label title = new Label("MAZE ESCAPE");
        title.setStyle("-fx-text-fill: #f4d58d;-fx-font-size: 18px;-fx-font-weight: bold;"
                + "-fx-letter-spacing: 4px;-fx-effect: dropshadow(gaussian, #8b2cff, 10, 0.4, 0, 0);");
        StackPane.setAlignment(title, Pos.TOP_CENTER);
        StackPane.setMargin(title, new javafx.geometry.Insets(7, 0, 0, 0));

        Label leftMarker = new Label("◆");
        leftMarker.setStyle("-fx-text-fill: #9b6bb5;-fx-font-size: 24px;");
        StackPane.setAlignment(leftMarker, Pos.TOP_LEFT);
        StackPane.setMargin(leftMarker, new javafx.geometry.Insets(4, 0, 0, 5));

        Label rightMarker = new Label("◆");
        rightMarker.setStyle("-fx-text-fill: #9b6bb5;-fx-font-size: 24px;");
        StackPane.setAlignment(rightMarker, Pos.TOP_RIGHT);
        StackPane.setMargin(rightMarker, new javafx.geometry.Insets(4, 5, 0, 0));

        frame.getChildren().addAll(title, leftMarker, rightMarker);
        return frame;
    }

    private HBox createHudPanel(String heading, Label value, Node icon) {
        Label title = new Label(heading);
        title.setStyle("-fx-text-fill: #a985bd;-fx-font-size: 9px;-fx-font-weight: bold;"
                + "-fx-letter-spacing: 1.5px;");
        value.setStyle("-fx-text-fill: #f5e5b0;-fx-font-size: 13px;-fx-font-weight: bold;"
                + "-fx-letter-spacing: 0.7px;");
        if ("LEVEL".equals(heading)) {
            value.setStyle("-fx-text-fill: " + difficultyColorCss()
                    + ";-fx-font-size: 13px;-fx-font-weight: bold;-fx-letter-spacing: 0.7px;");
        }
        VBox text = new VBox(2, title, value);
        HBox panel = new HBox(8, icon, text);
        panel.setAlignment(Pos.CENTER_LEFT);
        panel.setPadding(new javafx.geometry.Insets(7, 10, 7, 10));
        panel.setStyle("-fx-background-color: rgba(31, 14, 45, 0.92);"
                + "-fx-border-color: #76518d;-fx-border-width: 1px;"
                + "-fx-border-radius: 5px;-fx-background-radius: 5px;");
        return panel;
    }

    private String difficultyColorCss() {
        Color color;
        if (level <= 6) color = Color.web("#78e08f");
        else if (level <= 10) color = Color.web("#a8e6a3");
        else if (level <= 16) color = Color.web("#ffd166");
        else if (level <= 20) color = Color.web("#f5bd54");
        else if (level <= 30) color = Color.web("#ff9f43");
        else if (level <= 40) color = Color.web("#ff5c64");
        else color = Color.web("#c77dff");
        return String.format("#%02X%02X%02X", (int) Math.round(color.getRed() * 255),
                (int) Math.round(color.getGreen() * 255), (int) Math.round(color.getBlue() * 255));
    }

    private void styleHudTitle(Label title) {
        title.setStyle("-fx-text-fill: #f4d58d;-fx-font-size: 18px;-fx-font-weight: bold;"
                + "-fx-letter-spacing: 3px;-fx-effect: dropshadow(gaussian, #8b2cff, 12, 0.45, 0, 0);");
        title.setPadding(new javafx.geometry.Insets(0, 10, 0, 0));
    }

    private Node createHudIcon(String type) {
        Color gold = Color.web("#f4d58d");
        Color purple = Color.web("#b84cff");
        Group icon = new Group();
        if ("heart".equals(type)) {
            Circle left = new Circle(5, 6, 4, purple);
            Circle right = new Circle(11, 6, 4, purple);
            Line point = new Line(2, 8, 8, 15);
            Line pointEnd = new Line(8, 15, 14, 8);
            point.setStroke(purple);
            pointEnd.setStroke(purple);
            icon.getChildren().addAll(left, right, point, pointEnd);
        } else if ("timer".equals(type)) {
            Circle face = new Circle(8, 9, 6, Color.TRANSPARENT);
            face.setStroke(gold);
            face.setStrokeWidth(1.5);
            Line hand = new Line(8, 9, 8, 5);
            Line handRight = new Line(8, 9, 11, 11);
            hand.setStroke(gold);
            handRight.setStroke(gold);
            icon.getChildren().addAll(face, hand, handRight);
        } else if ("score".equals(type)) {
            Rectangle badge = new Rectangle(2, 4, 12, 10);
            badge.setArcWidth(3);
            badge.setArcHeight(3);
            badge.setFill(Color.TRANSPARENT);
            badge.setStroke(gold);
            Line mark = new Line(5, 9, 11, 9);
            mark.setStroke(gold);
            icon.getChildren().addAll(badge, mark);
        } else if ("key".equals(type)) {
            icon.getChildren().add(createKeyVisual());
            icon.setScaleX(0.55);
            icon.setScaleY(0.55);
        } else {
            Rectangle levelMark = new Rectangle(3, 3, 10, 11);
            levelMark.setFill(Color.TRANSPARENT);
            levelMark.setStroke(gold);
            levelMark.setStrokeWidth(1.5);
            icon.getChildren().add(levelMark);
        }
        return icon;
    }

    private VBox createLegendPanel() {
        Label heading = new Label("FIELD NOTES");
        heading.setStyle("-fx-text-fill: #f4d58d;-fx-font-size: 14px;-fx-font-weight: bold;"
                + "-fx-letter-spacing: 2px;");
        HBox headingRow = new HBox(9, createFieldNotesIcon(), heading);
        headingRow.setAlignment(Pos.CENTER_LEFT);
        Label subheading = new Label(String.format("HAUNTED HOUSE   •   LEVEL %02d", level));
        subheading.setStyle("-fx-text-fill: #9e7cad;-fx-font-size: 9px;-fx-letter-spacing: 1.4px;");
        VBox map = createMiniMap();
        Label objectiveHeading = new Label("CURRENT OBJECTIVE");
        objectiveHeading.setStyle("-fx-text-fill: #f4d58d;-fx-font-size: 11px;-fx-font-weight: bold;"
                + "-fx-letter-spacing: 1.2px;");
        fieldNotesObjectiveLabel = new Label("FIND THE KEY");
        fieldNotesObjectiveLabel.setStyle("-fx-text-fill: #f4d58d;-fx-font-size: 13px;"
                + "-fx-font-weight: bold;-fx-letter-spacing: 0.8px;");
        Circle objectiveIcon = new Circle(7, Color.TRANSPARENT);
        objectiveIcon.setStroke(Color.web("#f4d58d"));
        objectiveIcon.setStrokeWidth(1.5);
        objectiveIcon.setEffect(new Glow(0.3));
        HBox objectiveValue = new HBox(10, objectiveIcon, fieldNotesObjectiveLabel);
        objectiveValue.setAlignment(Pos.CENTER_LEFT);
        VBox objective = new VBox(8, objectiveHeading, objectiveValue);
        objective.setPadding(new javafx.geometry.Insets(10, 11, 10, 11));
        objective.setStyle("-fx-background-color: rgba(21, 11, 34, 0.92);"
                + "-fx-border-color: #8c65a4;-fx-border-width: 1px;"
                + "-fx-border-radius: 6px;-fx-background-radius: 6px;");

        Label legendHeading = new Label("LEGEND");
        legendHeading.setStyle("-fx-text-fill: #f4d58d;-fx-font-size: 12px;-fx-font-weight: bold;"
                + "-fx-letter-spacing: 1.5px;");
        VBox legend = new VBox(
                3,
                createLegendRow("WALL", createWallLegendIcon()),
                createLegendRow("FLOOR (EXPLORED)", createExploredFloorLegendIcon()),
                createLegendRow("FLOOR (UNEXPLORED)", createUnexploredFloorLegendIcon()),
                createLegendRow("PLAYER", createLegendImage(playerFrames[0][0], 1.4))
        );
        VBox legendCard = new VBox(6, legendHeading, legend, createFieldNotesDivider());
        legendCard.setPadding(new javafx.geometry.Insets(7, 10, 7, 10));
        legendCard.setStyle("-fx-background-color: rgba(14, 8, 25, 0.88);"
                + "-fx-border-color: #75528e;-fx-border-width: 1px;"
                + "-fx-border-radius: 6px;-fx-background-radius: 6px;");
        VBox panel = new VBox(6, headingRow, subheading, createFieldNotesDivider(), map, objective, legendCard);
        panel.setPrefWidth(254);
        panel.setMinWidth(254);
        panel.setMaxWidth(254);
        panel.setPadding(new javafx.geometry.Insets(8, 16, 8, 16));
        panel.setStyle("-fx-background-color: rgba(15, 7, 24, 0.96);"
                + "-fx-border-color: #9d6cb7;-fx-border-width: 1px;"
                + "-fx-border-radius: 10px;-fx-background-radius: 10px;");
        panel.setEffect(new DropShadow(20, Color.rgb(105, 43, 145, 0.32)));
        return panel;
    }

    private VBox createMiniMap() {
        miniMapPane = new Pane();
        miniMapPane.setPrefSize(220, 220);
        miniMapPane.setMinSize(220, 220);
        miniMapPane.setMaxSize(220, 220);
        Rectangle miniMapClip = new Rectangle(220, 220);
        miniMapPane.setClip(miniMapClip);
        miniMapPane.setStyle("-fx-background-color: #090611;-fx-border-color: #b08d57;-fx-border-width: 1.5px;");
        renderMiniMap();
        VBox wrapper = new VBox(miniMapPane);
        wrapper.setPadding(new javafx.geometry.Insets(8, 0, 0, 0));
        return wrapper;
    }

    private void renderMiniMap() {
        miniMapPane.getChildren().clear();
        double cellSize = Math.min(216.0 / columns, 216.0 / rows);
        double mapWidth = columns * cellSize;
        double mapHeight = rows * cellSize;
        double offsetX = (220 - mapWidth) / 2;
        double offsetY = (220 - mapHeight) / 2;

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                Rectangle tile = new Rectangle(cellSize, cellSize);
                tile.setLayoutX(offsetX + column * cellSize);
                tile.setLayoutY(offsetY + row * cellSize);
                boolean wall = maze.isWall(row, column);
                tile.setFill(wall ? Color.web("#694783") : Color.web("#292333"));
                tile.setStroke(wall ? Color.web("#a77bc4") : Color.web("#44394f"));
                tile.setStrokeWidth(Math.max(0.35, cellSize * 0.06));
                miniMapPane.getChildren().add(tile);
            }
        }

        miniMapFogLayer = new Canvas(220, 220);
        miniMapFogLayer.setMouseTransparent(true);
        miniMapExplored = new boolean[rows][columns];
        miniMapPane.getChildren().add(miniMapFogLayer);

        miniMapPlayer = createMiniMapImage(playerFrames[0][0]);
        miniMapPane.getChildren().add(miniMapPlayer);
        updateMiniMapMarkers(offsetX, offsetY, cellSize);
    }

    private ImageView createMiniMapImage(Image image) {
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setEffect(new Glow(0.35));
        return imageView;
    }

    private void updateMiniMapMarkers(double offsetX, double offsetY, double cellSize) {
        if (miniMapPane == null) {
            return;
        }
        double markerSize = Math.max(9, cellSize * 1.15);
        miniMapPlayer.setFitWidth(markerSize);
        miniMapPlayer.setFitHeight(markerSize);
        miniMapPlayer.setLayoutX(offsetX + (player.getX() / TILE_SIZE + 0.5) * cellSize - markerSize / 2);
        miniMapPlayer.setLayoutY(offsetY + (player.getY() / TILE_SIZE + 0.5) * cellSize - markerSize / 2);
        updateMiniMapFog(offsetX, offsetY, cellSize);
    }

    private void updateMiniMapFog(double offsetX, double offsetY, double cellSize) {
        if (miniMapFogLayer == null) {
            return;
        }
        int playerRow = Math.max(0, Math.min(rows - 1, (int) (player.getY() / TILE_SIZE)));
        int playerColumn = Math.max(0, Math.min(columns - 1, (int) (player.getX() / TILE_SIZE)));
        if (playerRow == lastMiniMapPlayerRow && playerColumn == lastMiniMapPlayerColumn) {
            return;
        }
        lastMiniMapPlayerRow = playerRow;
        lastMiniMapPlayerColumn = playerColumn;

        double centerX = offsetX + (player.getX() + player.getWidth() / 2) / TILE_SIZE * cellSize;
        double centerY = offsetY + (player.getY() + player.getHeight() / 2) / TILE_SIZE * cellSize;
        double sightRadius = getSightRadius() / TILE_SIZE * cellSize;
        GraphicsContext graphics = miniMapFogLayer.getGraphicsContext2D();
        graphics.clearRect(0, 0, miniMapFogLayer.getWidth(), miniMapFogLayer.getHeight());

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                double tileX = offsetX + (column + 0.5) * cellSize;
                double tileY = offsetY + (row + 0.5) * cellSize;
                boolean currentlyVisible = Math.hypot(tileX - centerX, tileY - centerY) <= sightRadius;
                if (currentlyVisible) {
                    miniMapExplored[row][column] = true;
                } else if (miniMapExplored[row][column]) {
                    graphics.setFill(Color.rgb(5, 4, 9, 0.48));
                    graphics.fillRect(offsetX + column * cellSize, offsetY + row * cellSize,
                            cellSize + 0.25, cellSize + 0.25);
                } else {
                    graphics.setFill(Color.rgb(3, 2, 6, 0.91));
                    graphics.fillRect(offsetX + column * cellSize, offsetY + row * cellSize,
                            cellSize + 0.25, cellSize + 0.25);
                }
            }
        }
        miniMapPlayer.toFront();
    }

    private void updateMiniMapMarkers() {
        double cellSize = Math.min(216.0 / columns, 216.0 / rows);
        updateMiniMapMarkers(
                (220 - columns * cellSize) / 2,
                (220 - rows * cellSize) / 2,
                cellSize);
    }

    private HBox createLegendRow(String label, Node icon) {
        Label text = new Label(label);
        text.setStyle("-fx-text-fill: #c9b8d4;-fx-font-size: 9px;-fx-font-weight: bold;"
                + "-fx-letter-spacing: 0.45px;");
        HBox row = new HBox(8, icon, text);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private Node createFieldNotesIcon() {
        Group icon = new Group();
        Rectangle page = new Rectangle(3, 2, 17, 20);
        page.setArcWidth(3);
        page.setArcHeight(3);
        page.setFill(Color.web("#21132f"));
        page.setStroke(Color.web("#f4d58d"));
        page.setStrokeWidth(1.3);
        Line binding = new Line(7, 2, 7, 22);
        binding.setStroke(Color.web("#b48acb"));
        Line lineOne = new Line(10, 8, 17, 8);
        Line lineTwo = new Line(10, 12, 17, 12);
        Line lineThree = new Line(10, 16, 15, 16);
        for (Line line : List.of(lineOne, lineTwo, lineThree)) {
            line.setStroke(Color.web("#c9b8d4"));
            line.setStrokeWidth(1);
        }
        icon.getChildren().addAll(page, binding, lineOne, lineTwo, lineThree);
        return icon;
    }

    private Region createFieldNotesDivider() {
        Region divider = new Region();
        divider.setPrefHeight(1);
        divider.setMinHeight(1);
        divider.setMaxHeight(1);
        divider.setStyle("-fx-background-color: linear-gradient(to right, #6d477f, #b08d57, #6d477f);");
        return divider;
    }

    private ImageView createLegendImage(Image image) {
        return createLegendImage(image, 1.0);
    }

    private ImageView createLegendImage(Image image, double scale) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(24 * scale);
        imageView.setFitHeight(24 * scale);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setEffect(new Glow(0.35));
        return imageView;
    }

    private Rectangle createWallLegendIcon() {
        Rectangle wall = new Rectangle(24, 20);
        wall.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#4a285d")),
                new Stop(0.45, Color.web("#24152f")),
                new Stop(1, Color.web("#120b1b"))));
        wall.setStroke(Color.web("#9b6bb5"));
        wall.setStrokeWidth(1.2);
        wall.setArcWidth(3);
        wall.setArcHeight(3);
        return wall;
    }

    private Rectangle createExploredFloorLegendIcon() {
        Rectangle floor = new Rectangle(24, 20, Color.web("#292333"));
        floor.setStroke(Color.web("#6b5a79"));
        floor.setStrokeWidth(1.2);
        return floor;
    }

    private Rectangle createUnexploredFloorLegendIcon() {
        Rectangle floor = new Rectangle(24, 20, Color.web("#07050c"));
        floor.setStroke(Color.web("#44394f"));
        floor.setStrokeWidth(1.2);
        return floor;
    }

    private Circle createMapMarker(Color color, boolean glow) {
        Circle marker = new Circle(6, color);
        if (glow) {
            marker.setEffect(new Glow(0.75));
        }
        return marker;
    }

    private void configureGameBackground() {
        setStyle("-fx-background-color: linear-gradient(to bottom, #05020a, #10061a 55%, #050208);");
    }

    private Button createPauseButton() {
        Button button = new Button("Ⅱ  PAUSE");
        button.setOnMousePressed(event -> screenManager.getAudioManager().playClick());
        button.setFocusTraversable(false);
        button.setPrefHeight(48);
        button.setStyle("-fx-background-color: #170d22;-fx-border-color: #b08d57;"
                + "-fx-border-width: 1px;-fx-border-radius: 5px;-fx-background-radius: 5px;"
                + "-fx-text-fill: #f4d58d;-fx-font-size: 11px;-fx-font-weight: bold;"
                + "-fx-letter-spacing: 1px;-fx-cursor: hand;");
        button.setOnAction(event -> {
            if (!paused) {
                pauseGame();
            }
        });
        return button;
    }

    private void styleHudLabel(Label label) {
        label.setStyle("-fx-text-fill: white;-fx-font-size: 16px;-fx-font-weight: bold;-fx-letter-spacing: 1px;");
    }

    private void createMazeVisuals() {
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                Rectangle tile = new Rectangle(TILE_SIZE, TILE_SIZE);
                tile.setLayoutX(column * TILE_SIZE);
                tile.setLayoutY(row * TILE_SIZE);
                boolean wall = maze.isWall(row, column);
                tile.setFill(wall ? hauntedWallPaint(row, column) : hauntedFloorPaint(row, column));
                tile.setStroke(wall ? Color.web("#9b6bb5") : Color.web("#352941"));
                tile.setStrokeWidth(wall ? 2 : 1);
                if (wall) {
                    tile.setArcWidth(6);
                    tile.setArcHeight(6);
                }
                addTileDetail(tile, row, column, wall);
                mazePane.getChildren().add(tile);
            }
        }

        fogLayer = new Group();
        fogLayer.setMouseTransparent(true);

        playerShape = new ImageView(playerFrames[0][0]);
        playerShape.setFitWidth(PLAYER_RENDER_WIDTH);
        playerShape.setFitHeight(PLAYER_RENDER_HEIGHT);
        playerShape.setPreserveRatio(true);
        playerShape.setSmooth(true);
        playerShape.setCache(true);
        keyShape = createKeyVisual();
        exitShape = createExitVisual(false);
        mazePane.getChildren().addAll(playerShape, keyShape, exitShape);
        mazePane.getChildren().add(fogLayer);
        playerShape.toFront();
        updateVisualPositions();
    }

    private void updateFogLayer() {
        if (fogLayer == null) {
            return;
        }

        double centerX = player.getX() + player.getWidth() / 2;
        double centerY = player.getY() + player.getHeight() / 2;
        if (Math.abs(centerX - lastFogCenterX) < 1
                && Math.abs(centerY - lastFogCenterY) < 1) {
            return;
        }
        lastFogCenterX = centerX;
        lastFogCenterY = centerY;
        double sightRadius = getSightRadius();
        double outerRadius = sightRadius + SIGHT_EDGE_FEATHER;

        if (fogLayer.getChildren().isEmpty()) {
            double mazeWidth = columns * TILE_SIZE;
            double mazeHeight = rows * TILE_SIZE;
            double fogExtent = Math.max(mazeWidth, mazeHeight) + outerRadius * 2;
            Shape fogWithSightOpening = Shape.subtract(
                    new Rectangle(-fogExtent, -fogExtent, fogExtent * 3, fogExtent * 3),
                    new Circle(0, 0, outerRadius)
            );
            fogWithSightOpening.setFill(FOG_COLOR.deriveColor(
                        0,
                        1,
                        1,
                        getFogOpacity()
            ));

            Shape featheredEdge = Shape.subtract(
                        new Circle(0, 0, outerRadius),
                        new Circle(0, 0, sightRadius)
            );
            featheredEdge.setFill(new RadialGradient(
                    0,
                    0,
                    0,
                    0,
                    outerRadius,
                    false,
                    CycleMethod.NO_CYCLE,
                    new Stop(sightRadius / outerRadius, Color.TRANSPARENT),
                    new Stop(0.9, FOG_COLOR.deriveColor(0, 1, 1, getFogOpacity() * 0.55)),
                    new Stop(1, FOG_COLOR.deriveColor(0, 1, 1, getFogOpacity()))
            ));
            fogLayer.getChildren().setAll(fogWithSightOpening, featheredEdge);
        }

        fogLayer.setTranslateX(centerX);
        fogLayer.setTranslateY(centerY);
    }

    private double getSightRadius() {
        return Math.max(MIN_SIGHT_RADIUS,
                BASE_SIGHT_RADIUS - (level - 1) * SIGHT_RADIUS_DECREASE_PER_LEVEL);
    }

    private double getFogOpacity() {
        double levelProgress = (Math.max(1, Math.min(50, level)) - 1) / 49.0;
        return LIGHT_FOG_OPACITY + (HEAVY_FOG_OPACITY - LIGHT_FOG_OPACITY) * levelProgress;
    }

    private LinearGradient hauntedWallPaint(int row, int column) {
        return new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#4a285d")),
                new Stop(0.45, Color.web("#24152f")),
                new Stop(1, Color.web("#120b1b")));
    }

    private LinearGradient hauntedFloorPaint(int row, int column) {
        Color base = ((row + column) & 1) == 0
                ? Color.web("#201724") : Color.web("#19121d");
        return new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, base.brighter()),
                new Stop(1, base.darker()));
    }

    private void addTileDetail(Rectangle tile, int row, int column, boolean wall) {
        if (!wall) {
            tile.setStroke(Color.web("#3d2b42"));
            return;
        }
        tile.setEffect(new DropShadow(8, Color.rgb(0, 0, 0, 0.45)));
    }

    private Group createKeyVisual() {
        Circle ring = new Circle(8, 8, 5, Color.web("#f4d58d"));
        ring.setStroke(Color.web("#fff4c7"));
        ring.setStrokeWidth(1.5);
        Circle hole = new Circle(8, 8, 2, Color.web("#3a2350"));
        Line shaft = new Line(11.5, 11.5, 21, 21);
        shaft.setStroke(Color.web("#f4d58d"));
        shaft.setStrokeWidth(3);
        Line toothOne = new Line(16, 16, 18, 14);
        Line toothTwo = new Line(18.5, 18.5, 20.5, 16.5);
        toothOne.setStroke(Color.web("#f4d58d"));
        toothTwo.setStroke(Color.web("#f4d58d"));
        toothOne.setStrokeWidth(3);
        toothTwo.setStrokeWidth(3);
        return new Group(ring, hole, shaft, toothOne, toothTwo);
    }

    private Group createExitVisual(boolean open) {
        Color border = open ? Color.web("#79f2c0") : Color.web("#f4d58d");
        Color panel = open ? Color.web("#102d31") : Color.web("#542b6f");
        Group visual = new Group();
        Rectangle frame = new Rectangle(2, 2, 28, 36);
        frame.setArcWidth(6);
        frame.setArcHeight(6);
        frame.setFill(Color.web("#241332"));
        frame.setStroke(border);
        frame.setStrokeWidth(2);
        Rectangle door = new Rectangle(6, 6, 20, 28);
        door.setArcWidth(4);
        door.setArcHeight(4);
        door.setFill(panel);
        visual.getChildren().addAll(frame, door);
        for (int y = 10; y <= 30; y += 5) {
            Line stripe = new Line(9, y, 23, y);
            stripe.setStroke(open ? Color.web("#286b6a") : Color.web("#8e55aa"));
            stripe.setStrokeWidth(1.5);
            visual.getChildren().add(stripe);
        }
        if (open) {
            Line center = new Line(16, 8, 16, 32);
            Line arrow = new Line(11, 20, 20, 20);
            Line arrowTop = new Line(16, 16, 20, 20);
            Line arrowBottom = new Line(16, 24, 20, 20);
            center.setStroke(border);
            arrow.setStroke(border);
            arrowTop.setStroke(border);
            arrowBottom.setStroke(border);
            center.setStrokeWidth(2);
            arrow.setStrokeWidth(2);
            arrowTop.setStrokeWidth(2);
            arrowBottom.setStrokeWidth(2);
            visual.getChildren().addAll(center, arrow, arrowTop, arrowBottom);
        } else {
            Circle handle = new Circle(22, 21, 2, border);
            visual.getChildren().add(handle);
        }
        Line base = new Line(4, 36, 28, 36);
        base.setStroke(border);
        base.setStrokeWidth(2);
        visual.getChildren().add(base);
        return visual;
    }

    private void setupInput() {
        setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                togglePause();
            } else {
                keysPressed.add(event.getCode());
            }
        });
        setOnKeyReleased(event -> keysPressed.remove(event.getCode()));
        setFocusTraversable(true);
        sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                requestFocus();
            }
        });
    }

    private void startGame() {
        timerManager.start(levelData.getTimeLimit());
        gameLoop = new AnimationTimer() {
            private long previousTime;
            private double accumulatedTime;

            @Override
            public void handle(long now) {
                if (previousTime == 0) {
                    previousTime = now;
                    return;
                }
                double elapsed = (now - previousTime) / 1_000_000_000.0;
                previousTime = now;
                if (paused) {
                    accumulatedTime = 0;
                    return;
                }
                accumulatedTime += Math.min(elapsed, 0.1);
                while (accumulatedTime >= TARGET_FRAME_TIME) {
                    update(TARGET_FRAME_TIME);
                    accumulatedTime -= TARGET_FRAME_TIME;
                }
            }
        };
        gameLoop.start();
    }

    private void update(double delta) {
        timerManager.update(delta);
        updatePlayer(delta);
        checkKeyCollision();
        checkExitCollision();
        updateHUD();
        updateVisualPositions();
        updateCamera();
        if (timerManager.isTimeUp()) {
            lifeManager.loseLife();
            displayedLives = lifeManager.getLives();
            screenManager.getGameManager().setLives(displayedLives);
            livesLabel.setText(createLivesText(displayedLives));
            stopGame();
            screenManager.getAudioManager().playGameOver();
            final int remainingLives = displayedLives;
            PauseTransition gameOverDelay = new PauseTransition(Duration.seconds(2));
            gameOverDelay.setOnFinished(event -> screenManager.gameOver(level, remainingLives));
            gameOverDelay.play();
        }
    }

    private void updatePlayer(double delta) {
        double movement = player.getSpeed() * delta;
        double dx = 0;
        double dy = 0;
        if (keysPressed.contains(KeyCode.W) || keysPressed.contains(KeyCode.UP)) dy -= movement;
        if (keysPressed.contains(KeyCode.S) || keysPressed.contains(KeyCode.DOWN)) dy += movement;
        if (keysPressed.contains(KeyCode.A) || keysPressed.contains(KeyCode.LEFT)) dx -= movement;
        if (keysPressed.contains(KeyCode.D) || keysPressed.contains(KeyCode.RIGHT)) dx += movement;
        if (dx != 0 && dy != 0) {
            double diagonalScale = 1 / Math.sqrt(2);
            dx *= diagonalScale;
            dy *= diagonalScale;
        }
        updatePlayerDirection(dx, dy);
        double previousX = player.getX();
        double previousY = player.getY();
        movePlayer(dx, dy);
        double movedDistance = Math.hypot(player.getX() - previousX, player.getY() - previousY);
        playerMoving = movedDistance > 0;
        if (playerMoving) {
            animationTime += delta * 10;
            walkingAnimationTime += delta;
            if (walkingAnimationTime >= 0.12) {
                playerAnimationFrame =
                        (playerAnimationFrame + 1) % playerFrames[playerDirectionRow].length;
                walkingAnimationTime = 0;
                updatePlayerFrame();
            }
            footstepDistance += movedDistance;
            if (footstepDistance >= 68) {
                screenManager.getAudioManager().playFootstep();
                footstepDistance = 0;
            }
        } else {
            animationTime = 0;
            walkingAnimationTime = 0;
            playerAnimationFrame = 0;
            updatePlayerFrame();
            footstepDistance = 0;
        }
    }

    private void updatePlayerDirection(double dx, double dy) {
        if (dx < 0) {
            playerDirectionRow = 1;
        } else if (dx > 0) {
            playerDirectionRow = 2;
        } else if (dy < 0) {
            playerDirectionRow = 3;
        } else if (dy > 0) {
            playerDirectionRow = 0;
        }
        if (playerAnimationFrame >= playerFrames[playerDirectionRow].length) {
            playerAnimationFrame = 0;
        }
        updatePlayerFrame();
    }

    private void updatePlayerFrame() {
        if (playerShape != null) {
            playerShape.setImage(playerFrames[playerDirectionRow][playerAnimationFrame]);
        }
    }

    private void movePlayer(double dx, double dy) {
        double newX = player.getX() + dx;
        double newY = player.getY() + dy;
        if (collisionManager.canMove(player, maze, newX, player.getY())) {
            player.setX(newX);
        }
        if (collisionManager.canMove(player, maze, player.getX(), newY)) {
            player.setY(newY);
        }
    }

    private void checkKeyCollision() {
        if (!key.isCollected() && collisionManager.playerTouchesKey(
                player, key.getX(), key.getY(), key.getWidth())) {
            key.collect();
            player.collectKey();
            moveExitAfterKeyCollection();
            exit.activate(player.hasKey());
            screenManager.getAudioManager().playKeyPickup();
            screenManager.getAudioManager().playExitUnlock();
            Group openExit = createExitVisual(true);
            int exitIndex = mazePane.getChildren().indexOf(exitShape);
            mazePane.getChildren().set(exitIndex, openExit);
            exitShape = openExit;
            DropShadow glow = new DropShadow(24, Color.web("#79f2c0"));
            exitShape.setEffect(glow);
            FadeTransition unlockFlash = new FadeTransition(Duration.millis(500), exitShape);
            unlockFlash.setFromValue(0.35);
            unlockFlash.setToValue(1.0);
            unlockFlash.setCycleCount(4);
            unlockFlash.setAutoReverse(true);
            unlockFlash.play();
            keyShape.setVisible(false);
        }
    }

    private void checkExitCollision() {
        if (!exit.isReached() && exit.isActive()
                && collisionManager.playerTouchesExit(player, exit.getX(), exit.getY(),
                exit.getWidth(), exit.getHeight())) {
            exit.reach();
            int score = scoreManager.calculateLevelScore(
                    timerManager.getRemainingSeconds(), lifeManager.getLives());
            stopGame();
            screenManager.getAudioManager().playLevelComplete();
            screenManager.completeLevel(level, score, timerManager.getRemainingSeconds(), lifeManager.getLives());
        }
    }

    private void updateVisualPositions() {
        playerShape.setLayoutX(player.getX() + (player.getWidth() - playerShape.getFitWidth()) / 2);
        double bob = playerMoving ? Math.sin(animationTime) * 1.5 : 0;
        playerShape.setLayoutY(player.getY() + (player.getHeight() - playerShape.getFitHeight()) / 2 + bob);
        keyShape.setLayoutX(key.getX());
        keyShape.setLayoutY(key.getY());
        exitShape.setLayoutX(exit.getX());
        exitShape.setLayoutY(exit.getY());
        updateFogLayer();
        updateMiniMapMarkers();
    }

    private void updateCamera() {
        if (mazeViewport == null || mazePane == null) {
            return;
        }
        double viewportWidth = mazeViewport.getViewportBounds().getWidth();
        double viewportHeight = mazeViewport.getViewportBounds().getHeight();
        double contentWidth = mazePane.getPrefWidth();
        double contentHeight = mazePane.getPrefHeight();
        if (contentWidth <= viewportWidth && contentHeight <= viewportHeight) {
            mazeViewport.setHvalue(0);
            mazeViewport.setVvalue(0);
            return;
        }
        double targetX = player.getX() + player.getWidth() / 2 - viewportWidth / 2;
        double targetY = player.getY() + player.getHeight() / 2 - viewportHeight / 2;
        double maxX = Math.max(0, contentWidth - viewportWidth);
        double maxY = Math.max(0, contentHeight - viewportHeight);
        mazeViewport.setHvalue(maxX == 0 ? 0 : clamp(targetX / maxX));
        mazeViewport.setVvalue(maxY == 0 ? 0 : clamp(targetY / maxY));
    }

    private double clamp(double value) {
        return Math.max(0, Math.min(1, value));
    }

    private static Image loadImage(String... resourcePaths) {
        for (String resourcePath : resourcePaths) {
            var resource = GameScreen.class.getResource(resourcePath);
            if (resource != null) {
                return new Image(resource.toExternalForm(), false);
            }
        }
        throw new IllegalStateException("Missing image resources: " + String.join(", ", resourcePaths));
    }

    private void updateHUD() {
        timerLabel.setText("TIME: " + timerManager.getFormattedTime());
        timerLabel.setVisible(screenManager.getSettingsManager().isShowTimer());
        if (fieldNotesObjectiveLabel != null) {
            boolean hasKey = key.isCollected();
            fieldNotesObjectiveLabel.setText(hasKey ? "REACH THE EXIT" : "FIND THE KEY");
            fieldNotesObjectiveLabel.setTextFill(hasKey
                    ? Color.web("#79f2c0") : Color.web("#f4d58d"));
        }
        livesLabel.setText(createLivesText());
        scoreManager.calculateLevelScore(
                timerManager.getRemainingSeconds(),
                lifeManager.getLives());
        scoreLabel.setText(String.format("SCORE: %04d", scoreManager.getCurrentLevelScore()));
        if (!exit.isActive()) {
            objectiveLabel.setText("FIND THE KEY");
            objectiveLabel.setTextFill(Color.web("#f4d58d"));
        } else if (exitMoved && !exit.isReached()) {
            objectiveLabel.setText("THE EXIT MOVED");
            objectiveLabel.setTextFill(Color.web("#ff9f68"));
        } else {
            objectiveLabel.setText("EXIT: OPEN");
            objectiveLabel.setTextFill(Color.web("#79f2c0"));
        }
    }

    private String createLivesText() {
        return createLivesText(displayedLives);
    }

    private String createLivesText(int remainingLives) {
        StringBuilder text = new StringBuilder();
        text.append("LIVES  ");
        for (int i = 0; i < 3; i++) {
            if (i > 0) {
                text.append(' ');
            }
            text.append(i < remainingLives ? '♥' : '♡');
        }
        return text.toString();
    }

    private void togglePause() {
        if (paused) {
            resumeGame();
        } else {
            pauseGame();
        }
    }

    private void pauseGame() {
        paused = true;
        timerManager.pause();
        keysPressed.clear();
        screenManager.getAudioManager().pauseBackgroundMusic();
        pauseMenu = new PauseMenu(this::resumeGame,
                this::openGameSettings,
                () -> screenManager.retryLevel(level), screenManager);
        gameArea.getChildren().add(pauseMenu);
    }

    private void openGameSettings() {
        if (gameSettingsOverlay != null) {
            return;
        }
        gameSettingsOverlay = new GameSettingsScreen(screenManager, this::closeGameSettings);
        gameArea.getChildren().add(gameSettingsOverlay);
    }

    private void closeGameSettings() {
        if (gameSettingsOverlay == null) {
            return;
        }
        gameArea.getChildren().remove(gameSettingsOverlay);
        gameSettingsOverlay = null;
        resumeGame();
    }

    private void resumeGame() {
        if (!paused) return;
        paused = false;
        timerManager.resume();
        screenManager.getAudioManager().resumeBackgroundMusic();
        if (pauseMenu != null) {
            gameArea.getChildren().remove(pauseMenu);
            pauseMenu = null;
        }
        requestFocus();
    }

    public void pauseForSettings() {
        paused = true;
        timerManager.pause();
        keysPressed.clear();
        screenManager.getAudioManager().pauseBackgroundMusic();
    }

    public void restoreAfterSettings() {
        if (paused && pauseMenu == null) {
            pauseMenu = new PauseMenu(this::resumeGame,
                    this::openGameSettings,
                    () -> screenManager.retryLevel(level), screenManager);
            gameArea.getChildren().add(pauseMenu);
        }
        screenManager.getAudioManager().pauseBackgroundMusic();
        requestFocus();
    }

    public int getLives() {
        return lifeManager.getLives();
    }

    public void stopGame() {
        timerManager.stop();
        screenManager.getAudioManager().stopBackgroundMusic();
        if (gameLoop != null) {
            gameLoop.stop();
        }
    }
}
