package com.github.naxos84.map;

import com.github.naxos84.ai.AiTile;

public class MapTile {

    protected AiTile tile;

    public MapTile(AiTile tile) {
        this.tile = tile;
    }

    public AiTile getTile() {
        return this.tile;
    }

    public void setTile(AiTile tile) {
        this.tile = tile;
    }
}
