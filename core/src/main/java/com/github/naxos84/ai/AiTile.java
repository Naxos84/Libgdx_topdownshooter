package com.github.naxos84.ai;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import squidpony.squidmath.Coord;

public class AiTile extends Coord {

    int gridX;
    int gridY;

    public boolean canBeRemoved = true;

    public AiTile(float x, float y, int gridX, int gridY) {
        super(Math.round(x), Math.round(y));
        this.gridX = gridX;
        this.gridY = gridY;
    }

    public void render(ShapeRenderer shapeRenderer, boolean inPath) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (inPath) {
            // green
            shapeRenderer.setColor(.57f, .76f, .48f, 1);
        } else {
            // blue
            shapeRenderer.setColor(.8f, .88f, .95f, 1);
        }
        shapeRenderer.circle(x, y, 4);
        shapeRenderer.end();

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(x).append(":").append(y).append("(").append(gridX).append(":").append(gridY).append(")");
        return sb.toString();
    }
}
