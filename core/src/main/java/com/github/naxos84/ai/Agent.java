package com.github.naxos84.ai;

import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Queue;

public class Agent {

    AiTileGraph aiTileGraph;

    float x;
    float y;

    float movementSpeed = 100f;

    Vector2 direction = new Vector2();

    AiTile previousAiTile;
    Queue<AiTile> pathQueue = new Queue<>();

    private GraphPath<AiTile> graphPath;

    public Agent(AiTileGraph aiTileGraph, int startX, int startY) {
        this.aiTileGraph = aiTileGraph;
        AiTile start = aiTileGraph.findTileByGridPosition(startX, startY);
        this.x = start.x;
        this.y = start.y;
        this.previousAiTile = start;
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
        x += direction.x * movementSpeed * delta;
        y += direction.y * movementSpeed * delta;
        checkCollision();
    }

    /**
     * Set the goal tile, calculate a path, and start moving.
     */
    public void setGoal(AiTile goal) {
        if (goal == null) {
            return;
        }

        if (pathQueue.size != 0) {
            AiTile first = pathQueue.first();
            this.graphPath = aiTileGraph.findPath(first, goal);
            pathQueue.clear();
            pathQueue.addFirst(first);
        } else {
            this.graphPath = aiTileGraph.findPath(previousAiTile, goal);
            pathQueue.clear();
        }

        for (int i = 1; i < graphPath.getCount(); i++) {
            pathQueue.addLast(graphPath.get(i));
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
    }

    public GraphPath<AiTile> getCurrentPath() {
        return graphPath;
    }
}
