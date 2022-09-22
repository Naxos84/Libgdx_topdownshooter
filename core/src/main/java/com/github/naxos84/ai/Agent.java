package com.github.naxos84.ai;

import java.util.List;
import java.util.Random;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Queue;

public class Agent {

    private AiGraph aiTileGraph;

    private Vector2 position = new Vector2();

    private float movementSpeed;
    Random random = new Random();

    private Vector2 direction = new Vector2();

    private AiTile previousAiTile;
    private Queue<AiTile> pathQueue = new Queue<>();

    private List<AiTile> graphPath;
    public boolean isIdle = true;

    private float wanderingTime = 0f;
    private AiTile currentGoal;
    private TextureRegion textureRegion;

    public Agent(AiGraph aiTileGraph, int startX, int startY, TextureRegion textureRegion) {
        this.aiTileGraph = aiTileGraph;
        AiTile start = aiTileGraph.findTileByGridPosition(startX, startY);
        this.position.set(start.x, start.y);
        this.previousAiTile = start;
        this.movementSpeed = random.nextFloat(70, 130);
        this.textureRegion = textureRegion;
    }

    public void render(SpriteBatch batch) {
        batch.draw(textureRegion, getX() - 21.5f, getY() - 21.5f,
                21.5f, 21.5f, 43,
                43, 1f, 1f,
                getRotation());
    }

    public void renderDebug(ShapeRenderer debugRenderer) {

        debugRenderer.setColor(1f, 0f, 0f, 1);
        debugRenderer.circle(position.x, position.y, 5);
        debugRenderer.line(position.x, position.y,
                position.x + direction.x * 32,
                position.y + direction.y * 32);
    }

    public void step(float delta) {
        wanderingTime += delta;
        if (isLost()) {
            wanderingTime = 0;
            setSpeedToNextAiTile();
        }
        // TODO
        this.position.add(direction.setLength(movementSpeed * delta));
        checkCollision();
    }

    public float getX() {
        return this.position.x;
    }

    public float getY() {
        return this.position.y;
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
            float distance = Vector2.dst(position.x, position.y, targetAiTile.x, targetAiTile.y);
            if (distance < 5) {
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
        calculateRotationTo(new Vector2(nextAiTile.x, nextAiTile.y));
    }

    private float getRotation() {
        return direction.angleDeg();
    }

    private void calculateRotationTo(Vector2 target) {
        Vector2 playerPosition = this.position.cpy();

        direction = target.cpy();
        direction.sub(playerPosition);
        direction.setLength(100);
    }

    /**
     * Agent has reached the goal tile.
     */
    private void reachDestination() {
        direction.set(0, 0);
        this.isIdle = true;
    }

    public List<AiTile> getCurrentPath() {
        return graphPath;
    }
}
