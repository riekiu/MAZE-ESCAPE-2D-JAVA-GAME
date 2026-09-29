package com.disorient.screen;

import com.disorient.controller.CollisionManager;
import com.disorient.manager.LifeManager;
import com.disorient.manager.ScreenManager;
import com.disorient.manager.ScoreManager;
import com.disorient.manager.TimerManager;
import com.disorient.model.Exit;
import com.disorient.model.Key;
import com.disorient.model.LevelData;
import com.disorient.model.Maze;
import com.disorient.model.Player;
import com.disorient.model.Tile;
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
import javafx.scene.shape.SVGPath;
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
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private static final int[][] MAZE_DIRECTIONS = {{0, 2}, {0, -2}, {2, 0}, {-2, 0}};
    private static final Pattern SVG_PATH_DATA = Pattern.compile("<path\\b[^>]*\\bd=\"([^\"]+)\"");

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
    // Direction order: front/down, left, right, back/up.
    private final Image[] playerIdleFrames = {
            loadImage("/img/player/sprites/idle/idle_front.png"),
            loadImage("/img/player/sprites/idle/idle_left.png"),
            loadImage("/img/player/sprites/idle/idle_right.png"),
            loadImage("/img/player/sprites/idle/idle_back.png")
    };
    private final Image[][] playerFrames = {
            {
                    loadImage("/img/player/sprites/walk/front/walk_front_1.png"),
                    loadImage("/img/player/sprites/walk/front/walk_front_2.png"),
                    loadImage("/img/player/sprites/walk/front/walk_front_3.png")
            },
            {
                    loadImage("/img/player/sprites/walk/left/walk_left_1.png"),
                    loadImage("/img/player/sprites/walk/left/walk_left_2.png"),
                    loadImage("/img/player/sprites/walk/left/walk_left_3.png")
            },
            {
                    loadImage("/img/player/sprites/walk/right/walk_right_1.png"),
                    loadImage("/img/player/sprites/walk/right/walk_right_2.png"),
                    loadImage("/img/player/sprites/walk/right/walk_right_3.png")
            },
            {
                    loadImage("/img/player/sprites/walk/back/walk_back_1.png"),
                    loadImage("/img/player/sprites/walk/back/walk_back_2.png"),
                    loadImage("/img/player/sprites/walk/back/walk_back_3.png")
            }
    };
    private Pane mazePane;
    private Group fogLayer;
    private StackPane gameArea;
    private ScrollPane mazeViewport;
    private StackPane mazeContent;
    private ImageView playerShape;
    private Group ghostShape;
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
    private int mazeLayoutIndex;
    private boolean ghostActive;
    private double ghostX;
    private double ghostY;
    private double ghostTargetX;
    private double ghostTargetY;
    private double ghostBobTime;
    private double ghostHitCooldown;
    private double ghostCaughtMessageTime;
    private int ghostTargetRow = -1;
    private int ghostTargetColumn = -1;
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
        Random random = new Random(levelData.getGenerationSeed() + mazeLayoutIndex * 104729L);
        List<int[]> stack = new ArrayList<>();
        List<int[]> directions = new ArrayList<>(List.of(
                MAZE_DIRECTIONS[0], MAZE_DIRECTIONS[1], MAZE_DIRECTIONS[2], MAZE_DIRECTIONS[3]));
        stack.add(new int[]{1, 1});
        walls[1][1] = false;
        while (!stack.isEmpty()) {
            int[] current = stack.get(stack.size() - 1);
            // Restore the original order before shuffling so the seeded maze and
            // random-number sequence remain identical without per-step allocations.
            for (int index = 0; index < MAZE_DIRECTIONS.length; index++) {
                directions.set(index, MAZE_DIRECTIONS[index]);
            }
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
        Label controlsLabel = new Label("WASD / ARROWS: MOVE    |    ESC: PAUSE    |    FIND THE KEY, ESCAPE THE SPIRIT, REACH THE EXIT");
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

        Label title = new Label("DISORIENT");
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
                createLegendRow("KEY", createHudIcon("key")),
                createLegendRow("PLAYER", createLegendImage(playerIdleFrames[0], 1.4))
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
        double cellSize = Math.min(216.0 / columns, 216.0 / rows);
        double mapWidth = columns * cellSize;
        double mapHeight = rows * cellSize;
        double offsetX = (220 - mapWidth) / 2;
        double offsetY = (220 - mapHeight) / 2;

        Canvas mapTiles = new Canvas(220, 220);
        GraphicsContext mapGraphics = mapTiles.getGraphicsContext2D();
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                boolean wall = maze.isWall(row, column);
                mapGraphics.setFill(wall ? Color.web("#694783") : Color.web("#292333"));
                mapGraphics.fillRect(offsetX + column * cellSize, offsetY + row * cellSize,
                        cellSize, cellSize);
                mapGraphics.setStroke(wall ? Color.web("#a77bc4") : Color.web("#44394f"));
                mapGraphics.setLineWidth(Math.max(0.35, cellSize * 0.06));
                mapGraphics.strokeRect(offsetX + column * cellSize, offsetY + row * cellSize,
                        cellSize, cellSize);
            }
        }
        miniMapPane.getChildren().setAll(mapTiles);

        miniMapFogLayer = new Canvas(220, 220);
        miniMapFogLayer.setMouseTransparent(true);
        miniMapExplored = new boolean[rows][columns];
        miniMapPane.getChildren().add(miniMapFogLayer);

        miniMapPlayer = createMiniMapImage(playerIdleFrames[0]);
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
        Canvas mazeTiles = createMazeTilesCanvas();
        mazePane.getChildren().add(mazeTiles);

        fogLayer = new Group();
        fogLayer.setMouseTransparent(true);

        playerShape = new ImageView(playerIdleFrames[0]);
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

    private Canvas createMazeTilesCanvas() {
        Canvas mazeTiles = new Canvas(columns * TILE_SIZE, rows * TILE_SIZE);
        GraphicsContext graphics = mazeTiles.getGraphicsContext2D();
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                boolean wall = maze.isWall(row, column);
                double x = column * TILE_SIZE;
                double y = row * TILE_SIZE;
                graphics.setFill(wall ? hauntedWallPaint(x, y) : hauntedFloorPaint(row, column, x, y));
                graphics.fillRoundRect(x, y, TILE_SIZE, TILE_SIZE,
                        wall ? 6 : 0, wall ? 6 : 0);
                graphics.setStroke(wall ? Color.web("#9b6bb5") : Color.web("#352941"));
                graphics.setLineWidth(wall ? 2 : 1);
                if (wall) {
                    graphics.strokeRoundRect(x, y, TILE_SIZE, TILE_SIZE, 6, 6);
                } else {
                    graphics.setStroke(Color.web("#3d2b42"));
                    graphics.strokeRect(x, y, TILE_SIZE, TILE_SIZE);
                }
            }
        }
        return mazeTiles;
    }

    /** Builds a fresh post-key layout and guarantees a clear route from the player to the exit. */
    private void rearrangeMazeAfterKeyCollection() {
        int playerRow = Math.max(1, Math.min(rows - 2, (int) (player.getY() / TILE_SIZE)));
        int playerColumn = Math.max(1, Math.min(columns - 2, (int) (player.getX() / TILE_SIZE)));

        mazeLayoutIndex++;
        generateMaze();

        // Leave room for the player sprite, then carve a guaranteed route to the far exit.
        for (int row = Math.max(1, playerRow - 1); row <= Math.min(rows - 2, playerRow + 1); row++) {
            for (int column = Math.max(1, playerColumn - 1);
                 column <= Math.min(columns - 2, playerColumn + 1); column++) {
                maze.setTile(row, column, Tile.Type.FLOOR);
            }
        }
        int exitRow = rows - 2;
        int exitColumn = columns - 2;
        for (int column = Math.min(playerColumn, exitColumn);
             column <= Math.max(playerColumn, exitColumn); column++) {
            maze.setTile(playerRow, column, Tile.Type.FLOOR);
        }
        int step = Integer.compare(exitRow, playerRow);
        for (int row = playerRow; row != exitRow; row += step) {
            maze.setTile(row, exitColumn, Tile.Type.FLOOR);
        }
        maze.setTile(exitRow, exitColumn, Tile.Type.FLOOR);

        // Replace the rendered tiles and reset exploration to match the new floor plan.
        mazePane.getChildren().set(0, createMazeTilesCanvas());
        fogLayer.getChildren().clear();
        lastFogCenterX = Double.NaN;
        lastFogCenterY = Double.NaN;
        if (miniMapPane != null) {
            lastMiniMapPlayerRow = -1;
            lastMiniMapPlayerColumn = -1;
            renderMiniMap();
        }
        updateFogLayer();
        updateVisualPositions();
        updateMiniMapMarkers();
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

    private LinearGradient hauntedWallPaint(double x, double y) {
        return new LinearGradient(x, y, x + TILE_SIZE, y + TILE_SIZE, false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#4a285d")),
                new Stop(0.45, Color.web("#24152f")),
                new Stop(1, Color.web("#120b1b")));
    }

    private LinearGradient hauntedFloorPaint(int row, int column, double x, double y) {
        Color base = ((row + column) & 1) == 0
                ? Color.web("#201724") : Color.web("#19121d");
        return new LinearGradient(x, y, x, y + TILE_SIZE, false, CycleMethod.NO_CYCLE,
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
        Color border = Color.web("#8b7a82");
        Color panel = open ? Color.web("#35151b") : Color.web("#681b24");
        Group visual = new Group();
        // A round crown over a square jamb gives the small marker a gothic doorway silhouette.
        Rectangle frameBase = new Rectangle(2, 13, 28, 24);
        frameBase.setFill(Color.web("#17121c"));
        frameBase.setStroke(Color.web("#63536a"));
        frameBase.setStrokeWidth(1.2);
        Circle frameArch = new Circle(16, 14, 14, Color.web("#17121c"));
        frameArch.setStroke(Color.web("#63536a"));
        frameArch.setStrokeWidth(1.2);
        Rectangle doorBase = new Rectangle(6, 14, 20, 22);
        doorBase.setFill(panel);
        doorBase.setStroke(Color.web("#29151c"));
        doorBase.setStrokeWidth(1);
        Circle doorArch = new Circle(16, 14, 10, panel);
        doorArch.setStroke(Color.web("#a44348"));
        doorArch.setStrokeWidth(1.2);
        visual.getChildren().addAll(frameBase, frameArch, doorBase, doorArch);

        // Planks, recessed panels, worn highlights and iron straps add wood and age cues.
        Line plankLeft = new Line(12.5, 10, 12.5, 34);
        Line plankRight = new Line(19.5, 10, 19.5, 34);
        plankLeft.setStroke(Color.web("#42171e"));
        plankRight.setStroke(Color.web("#42171e"));
        Rectangle upperPanel = new Rectangle(9, 17, 14, 7);
        Rectangle lowerPanel = new Rectangle(9, 26, 14, 7);
        for (Rectangle inset : new Rectangle[]{upperPanel, lowerPanel}) {
            inset.setArcWidth(2);
            inset.setArcHeight(2);
            inset.setFill(Color.TRANSPARENT);
            inset.setStroke(Color.web("#a44348"));
            inset.setStrokeWidth(0.8);
        }
        Line ironBand = new Line(7, 25, 25, 25);
        ironBand.setStroke(Color.web("#302b35"));
        ironBand.setStrokeWidth(1.6);
        Line scratch = new Line(21, 17, 18, 21);
        scratch.setStroke(Color.web("#d27668"));
        scratch.setStrokeWidth(0.8);
        visual.getChildren().addAll(plankLeft, plankRight, upperPanel, lowerPanel, ironBand, scratch);

        // Small rivets and side hinges make the doorway hardware visible at game scale.
        for (int y : new int[]{17, 31}) {
            Rectangle hinge = new Rectangle(5, y, 3, 4);
            hinge.setArcWidth(1);
            hinge.setArcHeight(1);
            hinge.setFill(Color.web("#77717b"));
            visual.getChildren().add(hinge);
        }
        if (open) {
            Rectangle opening = new Rectangle(10, 15, 12, 20);
            opening.setFill(Color.web("#090710"));
            opening.setStroke(Color.web("#090710"));
            Rectangle openDoorLeaf = new Rectangle(16, 16, 7, 18);
            openDoorLeaf.setFill(Color.web("#681b24"));
            openDoorLeaf.setStroke(Color.web("#a44348"));
            openDoorLeaf.setStrokeWidth(0.8);
            openDoorLeaf.setRotate(-14);
            Circle openDoorHandle = new Circle(18, 25, 0.8, Color.web("#d7b56d"));
            visual.getChildren().addAll(opening, openDoorLeaf, openDoorHandle);
        } else {
            Circle backplate = new Circle(22, 25, 2.2, Color.web("#24212b"));
            backplate.setStroke(Color.web("#a99eaa"));
            backplate.setStrokeWidth(0.8);
            Circle handle = new Circle(22, 25, 0.9, Color.web("#d7b56d"));
            visual.getChildren().addAll(backplate, handle);
        }
        Line threshold = new Line(3, 37, 29, 37);
        threshold.setStroke(Color.web("#8b7a82"));
        threshold.setStrokeWidth(1.5);
        visual.getChildren().add(threshold);
        return visual;
    }

    private Group createGhostVisual() {
        SVGPath body = new SVGPath();
        body.setContent(loadSvgPathData("/img/ghost/haunting_spirit.svg"));
        body.setFill(Color.web("#d7e4ed", 0.88));
        body.setStroke(Color.web("#a9bfd1", 0.9));
        body.setStrokeWidth(1.5);
        body.setEffect(new DropShadow(20, Color.rgb(166, 105, 255, 0.8)));

        Circle leftEye = new Circle(23, 28, 3.1, Color.web("#291936"));
        Circle rightEye = new Circle(41, 28, 3.1, Color.web("#291936"));
        Circle mouth = new Circle(32, 39, 2.1, Color.web("#493254"));
        Group ghost = new Group(body, leftEye, rightEye, mouth);
        ghost.setScaleX(0.72);
        ghost.setScaleY(0.72);
        ghost.setMouseTransparent(true);
        return ghost;
    }

    private String loadSvgPathData(String resourcePath) {
        try (InputStream input = GameScreen.class.getResourceAsStream(resourcePath)) {
            if (input == null) {
                throw new IllegalStateException("Missing ghost SVG resource: " + resourcePath);
            }
            String svg = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            Matcher matcher = SVG_PATH_DATA.matcher(svg);
            if (!matcher.find()) {
                throw new IllegalStateException("Ghost SVG has no path data: " + resourcePath);
            }
            return matcher.group(1);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read ghost SVG: " + resourcePath, exception);
        }
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
        updateGhost(delta);
        if (!lifeManager.hasLivesLeft()) {
            return;
        }
        checkExitCollision();
        updateHUD();
        updateVisualPositions();
        updateCamera();
        if (timerManager.isTimeUp()) {
            stopGame();
            screenManager.getAudioManager().playGameOver();
            PauseTransition gameOverDelay = new PauseTransition(Duration.seconds(2));
            gameOverDelay.setOnFinished(event -> screenManager.gameOver(level, displayedLives));
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
            playerShape.setImage(playerMoving
                    ? playerFrames[playerDirectionRow][playerAnimationFrame]
                    : playerIdleFrames[playerDirectionRow]);
            if (miniMapPlayer != null) {
                miniMapPlayer.setImage(playerShape.getImage());
            }
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
            rearrangeMazeAfterKeyCollection();
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
            spawnGhost();
        }
    }

    private void spawnGhost() {
        ghostShape = createGhostVisual();
        placeGhostAtFarthestFloor();
        ghostActive = true;
        int fogIndex = mazePane.getChildren().indexOf(fogLayer);
        mazePane.getChildren().add(Math.max(0, fogIndex), ghostShape);
        playerShape.toFront();
        ghostHitCooldown = 1.0;
    }

    private void placeGhostAtFarthestFloor() {
        int playerRow = Math.max(0, Math.min(rows - 1,
                (int) ((player.getY() + player.getHeight() / 2) / TILE_SIZE)));
        int playerColumn = Math.max(0, Math.min(columns - 1,
                (int) ((player.getX() + player.getWidth() / 2) / TILE_SIZE)));
        int[][] distances = calculateFloorDistances(playerRow, playerColumn);
        int exitRow = Math.max(0, Math.min(rows - 1, (int) (exit.getY() / TILE_SIZE)));
        int exitColumn = Math.max(0, Math.min(columns - 1, (int) (exit.getX() / TILE_SIZE)));
        int farthestDistance = 0;
        List<int[]> candidates = new ArrayList<>();
        for (int row = 1; row < rows - 1; row++) {
            for (int column = 1; column < columns - 1; column++) {
                if (distances[row][column] > 0
                        && (row != exitRow || column != exitColumn)) {
                    if (distances[row][column] > farthestDistance) {
                        farthestDistance = distances[row][column];
                        candidates.clear();
                    }
                    if (distances[row][column] == farthestDistance) {
                        candidates.add(new int[]{row, column});
                    }
                }
            }
        }
        if (candidates.isEmpty()) {
            ghostX = (playerColumn + 0.5) * TILE_SIZE;
            ghostY = (playerRow + 0.5) * TILE_SIZE;
        } else {
            int[] spawn = candidates.get(new Random().nextInt(candidates.size()));
            ghostX = (spawn[1] + 0.5) * TILE_SIZE;
            ghostY = (spawn[0] + 0.5) * TILE_SIZE;
        }
        ghostTargetRow = -1;
        ghostTargetColumn = -1;
    }

    private void updateGhost(double delta) {
        if (!ghostActive || ghostShape == null) {
            return;
        }
        ghostHitCooldown = Math.max(0, ghostHitCooldown - delta);
        ghostCaughtMessageTime = Math.max(0, ghostCaughtMessageTime - delta);
        ghostBobTime += delta;

        int playerRow = Math.max(0, Math.min(rows - 1,
                (int) ((player.getY() + player.getHeight() / 2) / TILE_SIZE)));
        int playerColumn = Math.max(0, Math.min(columns - 1,
                (int) ((player.getX() + player.getWidth() / 2) / TILE_SIZE)));
        int ghostRow = Math.max(0, Math.min(rows - 1, (int) (ghostY / TILE_SIZE)));
        int ghostColumn = Math.max(0, Math.min(columns - 1, (int) (ghostX / TILE_SIZE)));
        double playerCenterX = player.getX() + player.getWidth() / 2;
        double playerCenterY = player.getY() + player.getHeight() / 2;
        double speed = 78 + Math.min(level, 30) * 1.5;

        if (ghostRow == playerRow && ghostColumn == playerColumn) {
            ghostTargetRow = -1;
            moveGhostToward(playerCenterX, playerCenterY, speed * delta);
        } else {
            if (ghostTargetRow >= 0) {
                double targetCenterX = (ghostTargetColumn + 0.5) * TILE_SIZE;
                double targetCenterY = (ghostTargetRow + 0.5) * TILE_SIZE;
                if (Math.hypot(targetCenterX - ghostX, targetCenterY - ghostY) < 2) {
                    ghostX = targetCenterX;
                    ghostY = targetCenterY;
                    ghostTargetRow = -1;
                }
            }
            if (ghostTargetRow < 0) {
                chooseGhostNextStep(ghostRow, ghostColumn, playerRow, playerColumn);
            }
            if (ghostTargetRow >= 0) {
                ghostTargetX = (ghostTargetColumn + 0.5) * TILE_SIZE;
                ghostTargetY = (ghostTargetRow + 0.5) * TILE_SIZE;
                moveGhostToward(ghostTargetX, ghostTargetY, speed * delta);
            }
        }

        if (ghostHitCooldown <= 0
                && Math.hypot(ghostX - playerCenterX, ghostY - playerCenterY) < 34) {
            handleGhostCatch();
        }
    }

    private void moveGhostToward(double targetX, double targetY, double travel) {
        double dx = targetX - ghostX;
        double dy = targetY - ghostY;
        double distance = Math.hypot(dx, dy);
        if (distance <= travel || distance < 1) {
            ghostX = targetX;
            ghostY = targetY;
        } else {
            ghostX += dx / distance * travel;
            ghostY += dy / distance * travel;
        }
    }

    private void chooseGhostNextStep(int ghostRow, int ghostColumn, int playerRow, int playerColumn) {
        int[][] distances = calculateFloorDistances(playerRow, playerColumn);
        int bestDistance = ghostRow >= 0 && ghostRow < rows && ghostColumn >= 0 && ghostColumn < columns
                ? distances[ghostRow][ghostColumn] : -1;
        int bestRow = -1;
        int bestColumn = -1;
        for (int[] direction : new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
            int row = ghostRow + direction[0];
            int column = ghostColumn + direction[1];
            if (row >= 0 && row < rows && column >= 0 && column < columns
                    && distances[row][column] >= 0
                    && (bestDistance < 0 || distances[row][column] < bestDistance)) {
                bestDistance = distances[row][column];
                bestRow = row;
                bestColumn = column;
            }
        }
        ghostTargetRow = bestRow;
        ghostTargetColumn = bestColumn;
    }

    private void handleGhostCatch() {
        lifeManager.loseLife();
        displayedLives = lifeManager.getLives();
        screenManager.getGameManager().setLives(displayedLives);
        ghostCaughtMessageTime = 1.6;
        ghostHitCooldown = 2.8;
        placeGhostAtFarthestFloor();
        updateHUD();

        if (!lifeManager.hasLivesLeft()) {
            stopGame();
            screenManager.getAudioManager().playGameOver();
            int remainingLives = displayedLives;
            PauseTransition gameOverDelay = new PauseTransition(Duration.seconds(2));
            gameOverDelay.setOnFinished(event -> screenManager.gameOver(level, remainingLives));
            gameOverDelay.play();
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
        if (ghostShape != null) {
            ghostShape.setLayoutX(ghostX - 32);
            ghostShape.setLayoutY(ghostY - 36 + Math.sin(ghostBobTime * 2.4) * 4);
            ghostShape.setOpacity(0.78 + Math.sin(ghostBobTime * 1.4) * 0.12);
        }
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
        if (ghostActive && ghostCaughtMessageTime > 0) {
            objectiveLabel.setText("THE SPIRIT CAUGHT YOU");
            objectiveLabel.setTextFill(Color.web("#ff687d"));
        } else if (ghostActive) {
            double distance = Math.hypot(
                    ghostX - (player.getX() + player.getWidth() / 2),
                    ghostY - (player.getY() + player.getHeight() / 2));
            if (distance < TILE_SIZE * 4.5) {
                objectiveLabel.setText("THE SPIRIT IS NEAR");
                objectiveLabel.setTextFill(Color.web("#ff8b6b"));
            }
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
