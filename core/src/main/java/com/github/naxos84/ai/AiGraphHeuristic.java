package com.github.naxos84.ai;

import com.badlogic.gdx.math.Vector2;

import squidpony.squidai.graph.Heuristic;

public class AiGraphHeuristic implements Heuristic<AiTile> {

    private double d = 1.0;
    private double d2 = Math.sqrt(2.0);

    @Override
    public double estimate(AiTile node, AiTile endNode) {
        return estimateEuclidian(node, endNode);
    }

    // shorter
    private double estimateEuclidian(AiTile node, AiTile endNode) {
        return d * Vector2.dst(node.x, node.y, endNode.x, endNode.y);
    }

    // faster
    private double estimateDiagonal(AiTile node, AiTile endNode) {
        int dx = Math.abs(node.x - endNode.x);
        int dy = Math.abs(node.y - endNode.y);
        return d * (dx + dy) + (d2 - 2 * d) * Math.min(dx, dy);
    }

}
