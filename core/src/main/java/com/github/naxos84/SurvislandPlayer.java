package com.github.naxos84;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class SurvislandPlayer implements Collidable {

    private TextureRegion textureRegion;
    private Rectangle collider;
    private Vector2 position;
    private Vector2 direction;
    private float interactionRange = 60f;
    private float movementSpeed = 200f;

    public SurvislandPlayer(TextureRegion textureRegion) {
        this.textureRegion = textureRegion;
        float size = Math.max(textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
        this.collider = new Rectangle(0, 0, size, size);
        position = new Vector2();
    }

    public void setPosition(Vector2 position) {
        this.position.set(position);
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    public void renderDebug(ShapeRenderer debugRenderer) {
        debugRenderer.rect(this.collider.x, this.collider.y,
                this.collider.width, this.collider.height);
        debugRenderer.line(position.x + getHalfPlayerWidth(), position.y + getHalfPlayerHeight(),
                position.x + direction.x + getHalfPlayerWidth(),
                position.y + direction.y + getHalfPlayerHeight());
    }

    /**
     * Returns the direction relative to the players center.
     */
    public Vector2 getRelativeDirection() {
        return direction.cpy().add(getHalfPlayerWidth(), getHalfPlayerHeight());
    }

    /**
     * Returns the direction of the player in World positon
     */
    public Vector2 getAbsoluteDirection() {
        return this.position.cpy().add(getRelativeDirection());
    }

    public void lookAt(Vector2 position) {
        calculateRotationTo(position);
    }

    private void calculateRotationTo(Vector2 position) {
        Vector2 playerPosition = this.position.cpy();
        playerPosition.add(collider.width / 2f, collider.height / 2f);

        direction = position.cpy();
        direction.sub(playerPosition);
        direction.setLength(interactionRange);
    }

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }

    public float getWidth() {
        return this.collider.getWidth();
    }

    public float getHeight() {
        return this.collider.getHeight();
    }

    private float getHalfPlayerWidth() {
        return getWidth() / 2;
    }

    private float getHalfPlayerHeight() {
        return getHeight() / 2;
    }

    public float getRotation() {
        return direction.angleDeg();
    }

    @Override
    public Rectangle getCollider() {
        return collider;
    }

    public float getSize() {
        return Math.max(getWidth(), getHeight());
    }

    public void move(Vector2 v, float delta) {
        updatePosition(v, delta);
        updateCollider();
    }

    private void updatePosition(Vector2 v, float delta) {
        this.position.add(v.setLength(movementSpeed * delta));
    }

    private void updateCollider() {
        this.collider.x = this.position.x;
        this.collider.y = this.position.y;
    }

    public void render(SpriteBatch batch) {
        float halfPlayerWidth = getHalfPlayerWidth(); // TODO maybe use collider
                                                      // dimensions
        float halfPlayerHeight = getHalfPlayerHeight(); // TODO maybe use collider
                                                        // dimensions

        batch.draw(textureRegion, getX(), getY(),
                halfPlayerWidth, halfPlayerHeight, getWidth(),
                getHeight(), 1f, 1f,
                getRotation());

    }

    public boolean canInteractWith(Rectangle interactable) {
        return interactable.contains(direction.x + getHalfPlayerWidth(), direction.y + getHalfPlayerHeight());
    }

}
