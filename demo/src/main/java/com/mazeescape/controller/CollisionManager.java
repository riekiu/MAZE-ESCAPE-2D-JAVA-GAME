package com.mazeescape.controller;

import com.mazeescape.model.Maze;
import com.mazeescape.model.Player;
import com.mazeescape.model.Tile;

public class CollisionManager {

    private static final int TILE_SIZE = 64;

    // =========================
    // WALL COLLISION
    // =========================
    public boolean canMove(
            Player player,
            Maze maze,
            double newX,
            double newY
    ) {

        double width = player.getWidth();
        double height = player.getHeight();

        // Check all four corners of the player.
        return !isWallAt(maze, newX, newY)
                && !isWallAt(maze, newX + width - 0.01, newY)
                && !isWallAt(maze, newX, newY + height - 0.01)
                && !isWallAt(
                        maze,
                        newX + width - 0.01,
                        newY + height - 0.01
                );
    }

    // =========================
    // CHECK WALL
    // =========================
    private boolean isWallAt(
            Maze maze,
            double x,
            double y
    ) {

        int column = (int) (x / TILE_SIZE);
        int row = (int) (y / TILE_SIZE);

        if (!maze.isValidPosition(row, column)) {
            return true;
        }

        Tile tile = maze.getTile(row, column);

        return tile != null && tile.isWall();
    }

    // =========================
    // RECTANGLE COLLISION
    // =========================
    public boolean intersects(
            double x1,
            double y1,
            double width1,
            double height1,
            double x2,
            double y2,
            double width2,
            double height2
    ) {

        return x1 < x2 + width2
                && x1 + width1 > x2
                && y1 < y2 + height2
                && y1 + height1 > y2;
    }

    // =========================
    // PLAYER / KEY
    // =========================
    public boolean playerTouchesKey(Player player, double keyX, double keyY, double keySize) {

        return intersects(
                player.getX(),
                player.getY(),
                player.getWidth(),
                player.getHeight(),
                keyX,
                keyY,
                keySize,
                keySize
        );
    }

    // =========================
    // PLAYER / DOOR
    // =========================
    public boolean playerTouchesDoor(
            Player player,
            double doorX,
            double doorY,
            double doorWidth,
            double doorHeight
    ) {

        return intersects(
                player.getX(),
                player.getY(),
                player.getWidth(),
                player.getHeight(),
                doorX,
                doorY,
                doorWidth,
                doorHeight
        );
    }

    // =========================
    // PLAYER / EXIT
    // =========================
    public boolean playerTouchesExit(
            Player player,
            double exitX,
            double exitY,
            double exitWidth,
            double exitHeight
    ) {

        return intersects(
                player.getX(),
                player.getY(),
                player.getWidth(),
                player.getHeight(),
                exitX,
                exitY,
                exitWidth,
                exitHeight
        );
    }
}
