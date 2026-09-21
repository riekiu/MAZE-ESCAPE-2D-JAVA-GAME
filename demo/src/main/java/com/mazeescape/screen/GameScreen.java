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
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.Group;
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
    private static final int VIEWPORT_WIDTH = 1160;
    private static final int VIEWPORT_HEIGHT = 560;
    private static final double TARGET_FRAME_TIME = 1.0 / 60.0;

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
    private final Image playerDownImage = loadImage("/img/player/character_down.png", "/img/player/player_down.png");
    private final Image playerLeftImage = loadImage("/img/player/character_left.png", "/img/player/player_left.png");
    private final Image playerRightImage = loadImage("/img/player/character_right.png", "/img/player/player_right.png");
    private final Image playerUpImage = loadImage("/img/player/character_up.png", "/img/player/player_up.png");
    private Pane mazePane;
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
    private AnimationTimer gameLoop;
    private boolean paused;
    private PauseMenu pauseMenu;
    private double animationTime;
    private boolean playerMoving;
    private boolean exitMoved;

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
        int[] keyPosition = findFarthestFloor(1, 1);
        this.key = new Key(TILE_SIZE * keyPosition[1] + 20,
                TILE_SIZE * keyPosition[0] + 20);
        this.exit = new Exit(TILE_SIZE * (columns - 2) + 18,
                TILE_SIZE * (rows - 2) + 14);
        lifeManager.setLives(screenManager.getGameManager().getLives());
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

    private void createMazeModel() {
        if (maze == null) {
            generateMaze();
        }
    }

    private int[] findFarthestFloor(int startRow, int startColumn) {
        int[][] distance = new int[rows][columns];
        for (int row = 0; row < rows; row++) {
            java.util.Arrays.fill(distance[row], -1);
        }
        List<int[]> queue = new ArrayList<>();
        queue.add(new int[]{startRow, startColumn});
        distance[startRow][startColumn] = 0;
        int[] farthest = {startRow, startColumn};
        for (int index = 0; index < queue.size(); index++) {
            int[] current = queue.get(index);
            if ((current[0] != rows - 2 || current[1] != columns - 2)
                    && distance[current[0]][current[1]] > distance[farthest[0]][farthest[1]]) {
                farthest = current;
            }
            for (int[] direction : new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
                int nextRow = current[0] + direction[0];
                int nextColumn = current[1] + direction[1];
                if (nextRow >= 0 && nextRow < rows && nextColumn >= 0 && nextColumn < columns
                        && distance[nextRow][nextColumn] == -1 && !maze.isWall(nextRow, nextColumn)) {
                    distance[nextRow][nextColumn] = distance[current[0]][current[1]] + 1;
                    queue.add(new int[]{nextRow, nextColumn});
                }
            }
        }
        return farthest;
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
        styleHudLabel(levelLabel);
        styleHudLabel(timerLabel);
        styleHudLabel(livesLabel);
        styleHudLabel(scoreLabel);
        styleHudLabel(objectiveLabel);
        livesLabel.setStyle("-fx-text-fill: #ff8fa3;-fx-font-size: 17px;-fx-font-weight: bold;"
                + "-fx-letter-spacing: 1px;");
        HBox hud = new HBox(28, levelLabel, timerLabel, livesLabel, scoreLabel, objectiveLabel);
        hud.setAlignment(Pos.CENTER);
        hud.setPrefHeight(70);
        hud.setStyle("-fx-background-color: #100817;-fx-border-color: #44245a;-fx-border-width: 0 0 1px 0;");
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
        mazeViewport.setMinViewportWidth(VIEWPORT_WIDTH);
        mazeViewport.setMinViewportHeight(VIEWPORT_HEIGHT);
        mazeViewport.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mazeViewport.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mazeViewport.setPannable(false);
        mazeViewport.setFitToWidth(false);
        mazeViewport.setFitToHeight(false);
        mazeViewport.setStyle("-fx-background: #0b0710;-fx-background-color: #0b0710;");
        StackPane mazeFrame = createHauntedFrame(mazeViewport);
        mazeFrame.setPadding(new javafx.geometry.Insets(10));
        mazeFrame.setBorder(new Border(new BorderStroke(
                Color.web("#b08d57"),
                javafx.scene.layout.BorderStrokeStyle.SOLID,
                new CornerRadii(6),
                new BorderWidths(2)
        )));
        mazeFrame.setEffect(new DropShadow(24, Color.rgb(0, 0, 0, 0.75)));

        Button pauseButton = createPauseButton();
        gameArea = new StackPane(mazeFrame, pauseButton);
        gameArea.setAlignment(Pos.CENTER);
        StackPane.setAlignment(pauseButton, Pos.TOP_RIGHT);
        StackPane.setMargin(pauseButton, new javafx.geometry.Insets(14));
        setCenter(gameArea);
        Label controlsLabel = new Label("WASD / ARROWS: MOVE    |    ESC: PAUSE    |    COLLECT THE KEY, THEN REACH THE EXIT");
        controlsLabel.setMaxWidth(Double.MAX_VALUE);
        controlsLabel.setAlignment(Pos.CENTER);
        controlsLabel.setPadding(new javafx.geometry.Insets(10));
        controlsLabel.setStyle("-fx-background-color: #100817;-fx-border-color: #44245a;"
                + "-fx-border-width: 1px 0 0 0;-fx-text-fill: #cdb9d8;"
                + "-fx-font-size: 12px;-fx-font-weight: bold;-fx-letter-spacing: 0.8px;");
        setBottom(controlsLabel);
        updateCamera();
        updateHUD();
    }

    private StackPane createHauntedFrame(ScrollPane viewport) {
        StackPane frame = new StackPane(viewport);
        frame.setPadding(new javafx.geometry.Insets(34, 22, 30, 22));
        frame.setStyle("-fx-background-color: linear-gradient(to bottom, #160d20, #08050d);"
                + "-fx-border-color: #c49a5a;-fx-border-width: 3px;-fx-border-radius: 12px;"
                + "-fx-background-radius: 12px;");
        frame.setEffect(new DropShadow(28, Color.rgb(0, 0, 0, 0.8)));

        Label title = new Label("MAZE ESCAPE");
        title.setStyle("-fx-text-fill: #f4d58d;-fx-font-size: 18px;-fx-font-weight: bold;"
                + "-fx-letter-spacing: 4px;-fx-effect: dropshadow(gaussian, #8b2cff, 10, 0.4, 0, 0);");
        StackPane.setAlignment(title, Pos.TOP_CENTER);
        StackPane.setMargin(title, new javafx.geometry.Insets(7, 0, 0, 0));

        Label leftMarker = new Label("☾");
        leftMarker.setStyle("-fx-text-fill: #9b6bb5;-fx-font-size: 24px;");
        StackPane.setAlignment(leftMarker, Pos.TOP_LEFT);
        StackPane.setMargin(leftMarker, new javafx.geometry.Insets(4, 0, 0, 5));

        Label rightMarker = new Label("☽");
        rightMarker.setStyle("-fx-text-fill: #9b6bb5;-fx-font-size: 24px;");
        StackPane.setAlignment(rightMarker, Pos.TOP_RIGHT);
        StackPane.setMargin(rightMarker, new javafx.geometry.Insets(4, 5, 0, 0));

        Label footer = new Label("M A Z E   E S C A P E   •   E X P L O R A T I O N   Z O N E");
        footer.setStyle("-fx-text-fill: #846e91;-fx-font-size: 10px;-fx-font-weight: bold;"
                + "-fx-letter-spacing: 2px;");
        StackPane.setAlignment(footer, Pos.BOTTOM_CENTER);
        StackPane.setMargin(footer, new javafx.geometry.Insets(0, 0, 7, 0));
        frame.getChildren().addAll(title, leftMarker, rightMarker, footer);
        return frame;
    }

    private void configureGameBackground() {
        var resource = getClass().getResource("/img/menu/background.png");
        if (resource == null) {
            resource = getClass().getResource("/img/menu/background.jpg");
        }
        if (resource != null) {
            setBackground(new Background(new BackgroundImage(
                    new Image(resource.toExternalForm(), false),
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(100, 100, true, true, false, true)
            )));
        } else {
            setStyle("-fx-background-color: #050008;");
        }
    }

    private Button createPauseButton() {
        Button button = new Button("II");
        button.setOnMousePressed(event -> screenManager.getAudioManager().playClick());
        button.setFocusTraversable(false);
        button.setPrefSize(44, 36);
        button.setStyle("-fx-background-color: #170d22;-fx-border-color: #b08d57;"
                + "-fx-border-width: 1.5px;-fx-border-radius: 6px;-fx-background-radius: 6px;"
                + "-fx-text-fill: #f4d58d;-fx-font-size: 14px;-fx-font-weight: bold;-fx-cursor: hand;");
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

        playerShape = new ImageView(playerDownImage);
        playerShape.setFitWidth(48);
        playerShape.setFitHeight(56);
        playerShape.setPreserveRatio(true);
        playerShape.setSmooth(true);
        playerShape.setCache(true);
        keyShape = createKeyVisual();
        exitShape = createExitVisual(false);
        mazePane.getChildren().addAll(playerShape, keyShape, exitShape);
        updateVisualPositions();
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
            screenManager.getGameManager().setLives(lifeManager.getLives());
            stopGame();
            screenManager.getAudioManager().playGameOver();
            screenManager.gameOver(level);
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
        playerMoving = dx != 0 || dy != 0;
        if (playerMoving) {
            animationTime += delta * 10;
        } else {
            animationTime = 0;
        }
        if (dx != 0 && dy != 0) {
            double diagonalScale = 1 / Math.sqrt(2);
            dx *= diagonalScale;
            dy *= diagonalScale;
        }
        updatePlayerDirection(dx, dy);
        movePlayer(dx, dy);
    }

    private void updatePlayerDirection(double dx, double dy) {
        if (dx < 0) {
            playerShape.setImage(playerLeftImage);
        } else if (dx > 0) {
            playerShape.setImage(playerRightImage);
        } else if (dy < 0) {
            playerShape.setImage(playerUpImage);
        } else if (dy > 0) {
            playerShape.setImage(playerDownImage);
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
        livesLabel.setText(createLivesText());
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
        StringBuilder text = new StringBuilder();
        int remainingLives = lifeManager.getLives();
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
        pauseMenu = new PauseMenu(this::resumeGame, screenManager::showSettings,
                () -> screenManager.retryLevel(level), screenManager);
        gameArea.getChildren().add(pauseMenu);
    }

    private void resumeGame() {
        if (!paused) return;
        paused = false;
        timerManager.resume();
        if (pauseMenu != null) {
            gameArea.getChildren().remove(pauseMenu);
            pauseMenu = null;
        }
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
