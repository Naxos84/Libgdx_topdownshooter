package com.github.naxos84.ai;

import com.badlogic.gdx.ai.pfa.Connection;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(fromTile).append("->").append(toTile);

        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof AiTileConnection) {
            AiTileConnection connection = (AiTileConnection) obj;
            boolean result = connection.fromTile == fromTile && connection.toTile == toTile;

            return result;
        } else {
            return false;
        }
    }

}
