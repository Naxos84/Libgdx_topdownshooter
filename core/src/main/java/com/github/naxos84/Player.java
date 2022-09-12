package com.github.naxos84;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Player {
    float movementSpeed = 5f; // pixels per frame
    Vector2 position = new Vector2();
    float width;
    float height;

    Rectangle collider = new Rectangle();
    public float rotation;

    public Player() {
    }

    public Player(float width, float height) {
        this.width = width;
        this.height = height;
        float size = Math.max(width, height);
        this.collider = new Rectangle(0, 0, size, size);
    }

    public float getPlayerWidth() {
        return width; // this.collider.getWidth();
    }

    public float getPlayerHeight() {
        return height; // this.collider.getHeight();
    }

    public float getXPosition() {
        return position.x;
    }

    public float getYPosition() {
        return position.y;
    }

    // TODO add delta time to movement to make it frame independent

    public void move(Vector2 v) {
        this.position.add(v.setLength(movementSpeed));
        this.collider.x = this.position.x;
        this.collider.y = this.position.y;
    }

    public void moveLeft(float distance) {
        this.position.set(this.position.x - distance * movementSpeed, this.position.y);
        this.collider.x = this.position.x;
        this.collider.y = this.position.y;
    }

    public void moveRight(float distance) {
        this.position.set(this.position.x + distance * movementSpeed, this.position.y);
        this.collider.x = this.position.x;
        this.collider.y = this.position.y;
    }

    public void moveDown(float distance) {
        this.position.set(this.position.x, this.position.y - distance * movementSpeed);
        this.collider.x = this.position.x;
        this.collider.y = this.position.y;
    }

    public void moveUp(float distance) {
        this.position.set(this.position.x, this.position.y + distance * movementSpeed);
        this.collider.x = this.position.x;
        this.collider.y = this.position.y;
    }
}
