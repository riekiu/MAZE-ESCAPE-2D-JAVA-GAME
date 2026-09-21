package com.mazeescape.model;

public class Maze {

    // =========================
    // MAZE DATA
    // =========================
    private final int rows;
    private final int columns;

    private final Tile[][] tiles;

    // =========================
    // CONSTRUCTOR
    // =========================
    public Maze(int rows, int columns) {

        if (rows <= 0 || columns <= 0) {
            throw new IllegalArgumentException(
                    "Maze dimensions must be greater than zero."
            );
        }

        this.rows = rows;
        this.columns = columns;

        this.tiles = new Tile[rows][columns];

        createEmptyMaze();
    }

    // =========================
    // CREATE MAZE
    // =========================
    private void createEmptyMaze() {

        for (int row = 0; row < rows; row++) {

            for (int column = 0; column < columns; column++) {

                tiles[row][column] = new Tile(
                        row,
                        column,
                        Tile.Type.FLOOR
                );
            }
        }
    }

    // =========================
    // GET TILE
    // =========================
    public Tile getTile(int row, int column) {

        if (!isValidPosition(row, column)) {
            return null;
        }

        return tiles[row][column];
    }

    // =========================
    // SET TILE
    // =========================
    public void setTile(
            int row,
            int column,
            Tile.Type type
    ) {

        if (!isValidPosition(row, column)) {
            return;
        }

        tiles[row][column]
                = new Tile(row, column, type);
    }

    // =========================
    // DIMENSIONS
    // =========================
    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    // =========================
    // VALID POSITION
    // =========================
    public boolean isValidPosition(
            int row,
            int column
    ) {

        return row >= 0
                && row < rows
                && column >= 0
                && column < columns;
    }

    // =========================
    // WALL CHECK
    // =========================
    public boolean isWall(int row, int column) {

        Tile tile = getTile(row, column);

        return tile != null
                && tile.getType() == Tile.Type.WALL;
    }

    // =========================
    // RESET
    // =========================
    public void clear() {
        createEmptyMaze();
    }
}
