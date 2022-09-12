package com.github.naxos84.ai;

import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.math.Vector2;

public class AiTileHeuristic implements Heuristic<AiTile> {

    @Override
    public float estimate(AiTile currentTile, AiTile targetTile) {
        return Vector2.dst(currentTile.x, currentTile.y, targetTile.x, targetTile.y);
    }

}
