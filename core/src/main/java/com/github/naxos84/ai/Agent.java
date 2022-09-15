package com.github.naxos84.ai;

import java.util.Date;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Queue;

public class Agent {

    private AiGraph aiTileGraph;

    public float x;
    public float y;

    private float movementSpeed;
    Random random = new Random();

    private Vector2 direction = new Vector2();

    private AiTile previousAiTile;
    private Queue<AiTile> pathQueue = new Queue<>();

    private List<AiTile> graphPath;
    public boolean isIdle = true;

    private long startTime;
    private float wanderingTime = 0f;
    private AiTile currentGoal;

    public Agent(AiGraph aiTileGraph, int startX, int startY) {
        this.aiTileGraph = aiTileGraph;
        AiTile start = aiTileGraph.findTileByGridPosition(startX, startY);
        this.x = start.x;
        this.y = start.y;
        this.previousAiTile = start;
        this.movementSpeed = random.nextFloat(70, 130);
    }

    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1f, 0f, 0f, 1);
        shapeRenderer.circle(x, y, 5);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0, 0, 0, 1);
        shapeRenderer.circle(x, y, 5);
        shapeRenderer.end();
    }

    public void step(float delta) {
        wanderingTime += delta;
        if (isLost()) {
            wanderingTime = 0;
            setSpeedToNextAiTile();
            ;
        }
        x += direction.x * movementSpeed * delta;
        y += direction.y * movementSpeed * delta;
        checkCollision();
    }

    private boolean isLost() {
        return wanderingTime > 4;
    }

    /**
     * Set the goal tile, calculate a path, and start moving.
     */
    public void setGoal(AiTile goal) {
        isIdle = false;
        this.currentGoal = goal;
        startTime = new Date().getTime();
        if (goal == null) {
            return;
        }
        recalculatePath();

        for (int i = 1; i < graphPath.size(); i++) {
            pathQueue.addLast(graphPath.get(i));
        }
        setSpeedToNextAiTile();
    }

    public void recalculatePath() {
        if (pathQueue.size != 0) {
            try {
                AiTile first = pathQueue.first();
                this.graphPath = aiTileGraph.findPath(first, currentGoal);
                pathQueue.clear();
                pathQueue.addFirst(first);
            } catch (IllegalArgumentException e) {
                System.out.println("Error case A");
                reachDestination();
            }
        } else {
            try {
                this.graphPath = aiTileGraph.findPath(previousAiTile, currentGoal);
                pathQueue.clear();
            } catch (IllegalArgumentException e) {
                System.out.println("Error case B");
            }
        }
        setSpeedToNextAiTile();
    }

    /**
     * Check whether Agent has reached the next tile in its path.
     */
    private void checkCollision() {
        if (pathQueue.size > 0) {
            AiTile targetAiTile = pathQueue.first();
            if (Vector2.dst(x, y, targetAiTile.x, targetAiTile.y) < 5) {
                reachNextAiTile();
            }
        }
    }

    /**
     * Agent has collided with the next tile in its path.
     */
    private void reachNextAiTile() {
        wanderingTime = 0;

        AiTile nextAiTile = pathQueue.first();

        // Set the position to keep the Agent in the middle of the path

        this.previousAiTile = nextAiTile;
        pathQueue.removeFirst();

        if (pathQueue.size == 0) {
            reachDestination();
        } else {
            setSpeedToNextAiTile();
        }
    }

    /**
     * Set xSpeed and ySpeed to move towards next tile on path.
     */
    private void setSpeedToNextAiTile() {
        if (pathQueue.isEmpty()) {
            this.isIdle = true;
            return;
        }
        AiTile nextAiTile = pathQueue.first();
        direction.x = nextAiTile.x - this.x;
        direction.y = nextAiTile.y - this.y;
        direction = direction.nor();
    }

    /**
     * Agent has reached the goal tile.
     */
    private void reachDestination() {
        direction.set(0, 0);
        this.isIdle = true;
        long now = new Date().getTime();
    }

    public List<AiTile> getCurrentPath() {
        return graphPath;
    }
}
