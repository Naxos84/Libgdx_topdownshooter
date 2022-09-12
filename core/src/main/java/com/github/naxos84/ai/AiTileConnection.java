package com.github.naxos84.ai;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class AiTileConnection implements Connection<AiTile> {

    AiTile fromTile;
    AiTile toTile;
    float cost;

    public AiTileConnection(AiTile fromTile, AiTile toTile) {
        this.fromTile = fromTile;
        this.toTile = toTile;
        cost = Vector2.dst(fromTile.x, fromTile.y, toTile.x, toTile.y);
    }

    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 1);
        shapeRenderer.rectLine(fromTile.x, fromTile.y, toTile.x, toTile.y, 4);
        shapeRenderer.end();
    }

    @Override
    public float getCost() {
        // TODO Auto-generated method stub
        return cost;
    }

    @Override
    public AiTile getFromNode() {
        // TODO Auto-generated method stub
        return fromTile;
    }

    @Override
    public AiTile getToNode() {
        // TODO Auto-generated method stub
        return toTile;
    }

}
