package com.github.naxos84.ai;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class AiTile {

    float x;
    float y;
    float width;
    float height;

    public String name;
    int index;

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

    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch, BitmapFont font, boolean inPath) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (inPath) {
            // green
            shapeRenderer.setColor(.57f, .76f, .48f, 1);
        } else {
            // blue
            shapeRenderer.setColor(.8f, .88f, .95f, 1);
        }
        shapeRenderer.circle(x, y, 20);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0, 0, 0, 1);
        shapeRenderer.circle(x, y, 20);
        shapeRenderer.end();

        batch.begin();
        font.draw(batch, name, x - 5, y + 5);
        batch.end();
    }
}
