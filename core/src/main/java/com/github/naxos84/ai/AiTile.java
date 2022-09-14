package com.github.naxos84.ai;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class AiTile {

    float x;
    float y;
    float width;
    float height;

    public String name;
    int index;
    boolean selected;

    public AiTile(float x, float y, float width, float height, String name) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.name = name;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public boolean isSelected() {
        return selected;
    }

    public void toggleSelection() {
        this.selected = !this.selected;
    }

    public void render(ShapeRenderer shapeRenderer, boolean inPath) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (inPath && !selected) {
            // green
            shapeRenderer.setColor(.57f, .76f, .48f, 1);
        } else if (selected) {
            shapeRenderer.setColor(0.57f, 0f, 0f, 1f);
        } else {
            // blue
            shapeRenderer.setColor(.8f, .88f, .95f, 1);
        }
        shapeRenderer.circle(x, y, 4);
        shapeRenderer.end();

    }
}
