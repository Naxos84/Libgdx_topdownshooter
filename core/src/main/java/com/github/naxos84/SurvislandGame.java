package com.github.naxos84;

import com.badlogic.gdx.Game;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all
 * platforms.
 */
public class SurvislandGame extends Game {
	@Override
	public void create() {

		setScreen(new FirstScreen());
	}

}