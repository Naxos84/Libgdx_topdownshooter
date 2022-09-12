package com.github.naxos84;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * First screen of the application. Displayed after the application is created.
 */
public class FirstScreen implements Screen, InputProcessor {

	private final boolean debug = false;

	private OrthographicCamera camera;

	Texture characterTexture;

	TextureRegion zombie;
	SpriteBatch batch;

	ShapeRenderer debugRenderer = new ShapeRenderer();

	private boolean isColliding;
	private SurvislandMap sMap = new SurvislandMap();
	private SurvislandPlayer sPlayer;

	@Override
	public void show() {
		sMap.loadMap("tiled/base.tmx");
		this.camera = new OrthographicCamera(800, 600);

		this.characterTexture = new Texture("characters/spritesheet.png");
		this.zombie = new TextureRegion(characterTexture, 460, 0, 33, 43);
		this.batch = new SpriteBatch();
		sPlayer = new SurvislandPlayer(this.zombie, 33, 43);
		this.spawnPlayer();

		this.updateCamera();

		Gdx.input.setInputProcessor(this);
	}

	private void spawnPlayer() {
		Vector2 playerSpawn = sMap.getPlayerSpawn();
		this.sPlayer.setPosition(playerSpawn);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 0.2f);

		this.sMap.render(this.camera);
		handleInput();
		handleMapBorderCollision();

		Vector2 mousePosition = getMousePosition();
		sPlayer.lookAt(mousePosition);

		this.batch.setProjectionMatrix(this.camera.combined);
		this.batch.begin();
		this.sPlayer.render(batch);

		this.batch.end();

		if (debug) {
			renderDebug();
		}

	}

	private Vector2 getMousePosition() {
		Vector3 unprojectedMousePosition = this.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
		return new Vector2(unprojectedMousePosition.x, unprojectedMousePosition.y);
	}

	private void renderDebug() {
		debugRenderer.setProjectionMatrix(this.camera.combined);

		if (this.isColliding) {
			debugRenderer.setColor(Color.RED);
		} else {
			debugRenderer.setColor(Color.WHITE);
		}
		debugRenderer.begin(ShapeType.Line);

		sPlayer.renderDebug(debugRenderer);
		sMap.renderWallsDebug(debugRenderer);

		debugRenderer.end();
	}

	private void handleMapBorderCollision() {
		Integer mapWidth = sMap.getWidth();
		Integer mapHeight = sMap.getHeight();
		float playerSize = sPlayer.getSize();

		float playerX = MathUtils.clamp(this.sPlayer.getX(), 0, mapWidth - playerSize);
		float playerY = MathUtils.clamp(this.sPlayer.getY(), 0, mapHeight - playerSize);

		this.sPlayer.setPosition(playerX, playerY);
	}

	private void handleInput() {
		boolean hasMoved = false;
		Vector2 horizontalMovement = new Vector2();

		if (Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT)) {
			horizontalMovement.sub(Vector2.X);
		}
		if (Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT)) {
			horizontalMovement.add(Vector2.X);
		}

		this.sPlayer.move(horizontalMovement);
		checkCollision();
		if (this.isColliding) {
			horizontalMovement.rotateDeg(180);
			this.sPlayer.move(horizontalMovement);
		}

		Vector2 verticalMovement = new Vector2();

		if (Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.UP)) {
			verticalMovement.add(Vector2.Y);
		}
		if (Gdx.input.isKeyPressed(Keys.S) || Gdx.input.isKeyPressed(Keys.DOWN)) {
			verticalMovement.sub(Vector2.Y);
		}
		this.sPlayer.move(verticalMovement);
		checkCollision();

		if (this.isColliding) {
			verticalMovement.rotateDeg(180);
			this.sPlayer.move(verticalMovement);
		}

		hasMoved = !horizontalMovement.isZero() || !verticalMovement.isZero();

		if (hasMoved) {
			this.updateCamera();
		}
	}

	private void checkCollision() {
		this.isColliding = sMap.isCollidingWithWall(sPlayer);

	}

	@Override
	public void resize(int width, int height) {
		// Resize your screen here. The parameters represent the new window size.
	}

	@Override
	public void pause() {
		// Invoked when your application is paused.
	}

	@Override
	public void resume() {
		// Invoked when your application is resumed after pause.
	}

	@Override
	public void hide() {
		// This method is called when another screen replaces this one.
	}

	@Override
	public void dispose() {
		batch.dispose();
		characterTexture.dispose();

		debugRenderer.dispose();

		// Destroy screen's assets here.
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Keys.ESCAPE) {
			Gdx.app.exit();
		}
		return false;
	}

	private void updateCamera() {
		Gdx.app.debug("MethodCall", "Updating camera");
		float cX;
		float cY;

		float halfScreenWidth = camera.viewportWidth / 2;
		float halfScreenHeight = camera.viewportHeight / 2;
		float playerXPosition = this.sPlayer.getX();
		float playerYPosition = this.sPlayer.getY();
		int mapWidth = sMap.getWidth();
		int mapHeight = sMap.getHeight();
		if (playerXPosition < halfScreenWidth) {
			cX = halfScreenWidth;
		} else if (playerXPosition > mapWidth - halfScreenWidth) {
			cX = mapWidth - halfScreenWidth;
		} else {
			cX = playerXPosition;
		}

		if (playerYPosition < halfScreenHeight) {
			cY = halfScreenHeight;
		} else if (playerYPosition > mapHeight - halfScreenHeight) {
			cY = mapHeight - halfScreenHeight;
		} else {
			cY = playerYPosition;
		}

		camera.position.set(cX, cY, 0);
		camera.update();
	}

	@Override
	public boolean keyTyped(char character) {
		if (character == 'j') {
			sMap.toggleAllDoors();
		}
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		// TODO Auto-generated method stub
		return false;
	}
}