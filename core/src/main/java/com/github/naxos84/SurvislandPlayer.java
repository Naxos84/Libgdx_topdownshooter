package com.github.naxos84;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class SurvislandPlayer implements Collidable {

    private TextureRegion textureRegion;
    private int width;
    private int height;
    private Rectangle collider;
    private Vector2 position;
    private float rotation;
    private float movementSpeed = 5f;

    public SurvislandPlayer(TextureRegion textureRegion, int width, int height) {
        this.textureRegion = textureRegion;
        this.width = width;
        this.height = height;
        float size = Math.max(width, height);
        this.collider = new Rectangle(0, 0, size, size);
        position = new Vector2();
    }

    public void setPosition(Vector2 position) {
        this.position.set(position);
    }

    public void renderDebug(ShapeRenderer debugRenderer) {
        debugRenderer.rect(this.collider.x, this.collider.y,
                this.collider.width, this.collider.height);
    }

    public void lookAt(Vector2 position) {
        this.rotation = calculateRotation(position);
    }

    public float calculateRotation(Vector2 position) {
        Vector2 playerPosition = this.position.cpy();
        playerPosition.add(collider.width / 2f, collider.height / 2f);

        Vector2 targetPosition = position.cpy();
        targetPosition.sub(playerPosition);

        return targetPosition.angleDeg();
    }

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getRotation() {
        return rotation;
    }

    @Override
    public Rectangle getCollider() {
        return collider;
    }

    public float getSize() {
        return Math.max(getWidth(), getHeight());
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    public void move(Vector2 v) {
        updatePosition(v);
        updateCollider();
    }

    private void updatePosition(Vector2 v) {
        this.position.add(v.setLength(movementSpeed));
    }

    private void updateCollider() {
        this.collider.x = this.position.x;
        this.collider.y = this.position.y;
    }

    public void render(SpriteBatch batch) {
        float halfPlayerWidth = this.getWidth() / 2f; // TODO maybe use collider dimensions
        float halfPlayerHeight = this.getHeight() / 2f; // TODO maybe use collider dimensions

        batch.draw(textureRegion, getX(), getY(),
                halfPlayerWidth, halfPlayerHeight, getWidth(),
                getHeight(), 1f, 1f,
                getRotation());
    }

}
