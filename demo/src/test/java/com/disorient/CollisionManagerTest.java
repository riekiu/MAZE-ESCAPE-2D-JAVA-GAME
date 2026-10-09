package com.disorient;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.disorient.controller.CollisionManager;
import com.disorient.model.Maze;
import com.disorient.model.Player;
import com.disorient.model.Tile;
import org.junit.jupiter.api.Test;

class CollisionManagerTest {

    @Test
    void blockingThemeObstacleUsesTheExistingWallCollision() {
        Maze maze = new Maze(3, 3);
        maze.setTile(1, 1, Tile.Type.OBSTACLE);
        Player player = new Player(32, 64);

        assertTrue(maze.isWall(1, 1));
        assertFalse(new CollisionManager().canMove(player, maze, 64, 64));
    }
}
