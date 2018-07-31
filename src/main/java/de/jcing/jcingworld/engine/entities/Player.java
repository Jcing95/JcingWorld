package de.jcing.jcingworld.engine.entities;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Vector3f;

import de.jcing.jcingworld.engine.DisplayManager;
import de.jcing.jcingworld.engine.entities.models.TexturedModel;
import de.jcing.jcingworld.engine.io.KeyBoard;
import de.jcing.jcingworld.engine.io.Mouse;
import de.jcing.jcingworld.terrain.Terrain;

public class Player extends Entity {

	private static final float SPEED = 17.5f;
	private static final float TURN = 7.5f;
	private static final float BOOSTSPEEDMOD = 7.5f;
	private static final float CAMERA_HEADOFFSET = 3.5f;

	private static final float GRAVITY = -70;
	private static final float JUMP_POWER = 40;

	private float currSpeed = 0;

	private boolean flying = false;

	private boolean inAir = false;

	private float forwardSpeed = 0;
	private float upwardsSpeed = 0;
	private float rightSpeed = 0;

	private float xRotDelta;
	private boolean doubleJumpRotation;
	private long lastJump;
	private float doubleJumpRotationDegrees;
	private float doubleJumpRotationSpeed;
	private static final float DOUBLEJUMPROTATIONACCELERATION = 37.5f;
	private static final long JUMPTIMEDELTA = 150;
	private static final float MAXDOUBLEJUMPROTATIONSPEED = 707.5f;
	private static final int MAXDOUBLEJUMPS = 3;

	private Terrain terrain;
	private boolean spaceReleased;
	private float xRotDJDelta;
	private int doubleJumps;

	public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale, Terrain terrain) {
		super(model, position, rotX, rotY, rotZ, scale);
		// TODO: Player Implemention / MOBS n co.
		this.terrain = terrain;
	}

	public String getBiome() {
		switch (terrain.getGenerator().biome(this.getPosition().x, this.getPosition().z)) {
		default:
			return "" + terrain.getGenerator().biome(this.getPosition().x, this.getPosition().z);
		}
	}

	public void move() {

		flying = KeyBoard.toggled(GLFW.GLFW_KEY_F);

		// SPEED
		if (KeyBoard.key(GLFW.GLFW_KEY_LEFT_SHIFT)) {
			currSpeed = SPEED * BOOSTSPEEDMOD;
		} else {
			currSpeed = SPEED;
		}

		if (KeyBoard.key(GLFW.GLFW_KEY_W)) {
			forwardSpeed = -currSpeed;
		} else if (KeyBoard.key(GLFW.GLFW_KEY_S)) {
			forwardSpeed = currSpeed;
		} else {
			forwardSpeed = 0;
		}

		if (KeyBoard.key(GLFW.GLFW_KEY_D)) {
			rightSpeed = currSpeed;
		} else if (KeyBoard.key(GLFW.GLFW_KEY_A)) {
			rightSpeed = -currSpeed;
		} else {
			rightSpeed = 0;
		}

		if (flying) {
			if (KeyBoard.key(GLFW.GLFW_KEY_SPACE)) {
				upwardsSpeed = currSpeed;
			} else if (flying && KeyBoard.key(GLFW.GLFW_KEY_LEFT_CONTROL)) {
				upwardsSpeed = -currSpeed;
			} else {
				upwardsSpeed = 0;
			}
		} else {
			upwardsSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();
			if (KeyBoard.key(GLFW.GLFW_KEY_SPACE)) {
				jump();
				spaceReleased = false;
			} else {
				spaceReleased = true;
			}
		}
		if (doubleJumpRotation) {
			if (doubleJumpRotationSpeed < MAXDOUBLEJUMPROTATIONSPEED)
				doubleJumpRotationSpeed += DOUBLEJUMPROTATIONACCELERATION;

			doubleJumpRotationDegrees += doubleJumpRotationSpeed * DisplayManager.getFrameTimeSeconds();
			;
			if (doubleJumpRotationDegrees >= 360) {
				doubleJumpRotationDegrees = 0;
				doubleJumpRotation = false;
			}
		}
		xRotDJDelta = doubleJumpRotationDegrees;

		// ROTATION Horizontal
		if (doubleJumps > 2)
			this.increaseRotation(0, (float) (Mouse.deltaX * TURN * DisplayManager.getFrameTimeSeconds() + xRotDJDelta), 0);
		else
			this.increaseRotation(0, (float) (Mouse.deltaX * TURN * DisplayManager.getFrameTimeSeconds()), 0);

		// Vertical
		if ((float) (Mouse.deltaY * TURN * DisplayManager.getFrameTimeSeconds()) + getRotX() > -90
				&& (float) (Mouse.deltaY * TURN * DisplayManager.getFrameTimeSeconds()) + getRotX() < 90) {

			xRotDelta += (float) (Mouse.deltaY * TURN * DisplayManager.getFrameTimeSeconds());
		}
		xRotDelta %= 180;
		this.setRotX((xRotDelta + xRotDJDelta) % 360);
		// MOVEMENT

		float distanceUp = 0.0f;
		float distanceX = 0.0f;
		float distanceZ = 0.0f;
		float distanceY = 0.0f;

		float speedUp = upwardsSpeed * DisplayManager.getFrameTimeSeconds();

		float distanceRight = rightSpeed * DisplayManager.getFrameTimeSeconds();
		float distanceForward = forwardSpeed * DisplayManager.getFrameTimeSeconds();

		// FLYING
		if (flying) {
			distanceUp = speedUp;// (float) (distanceForward * Math.sin(Math.toRadians(getRotX())) - speedUp * Math.sin(Math.toRadians(getRotX() -
									// 90)));
			distanceForward = (float) (distanceForward
					* Math.cos(Math.toRadians(getRotX())) /*- speedUp * Math.cos(Math.toRadians(getRotX() - 90))*/);
			// distanceRight = (float) (distanceRight *
			// Math.cos(Math.toRadians(getRotX()))
			// - speedUp * Math.cos(Math.toRadians(getRotX() - 90)));

			distanceX = (float) (distanceForward * Math.sin(Math.toRadians(-getRotY())) + distanceRight * Math.sin(Math.toRadians(-getRotY() + 90)));
			distanceZ = (float) (distanceForward * Math.cos(Math.toRadians(-getRotY())) + distanceRight * Math.cos(Math.toRadians(-getRotY() + 90)));
			distanceY = distanceUp;

		} else {
			// NOT FLYING
			if (getPosition().y + speedUp < terrain.getHeightAt(getPosition().x, getPosition().z)) {
				inAir = false;
				doubleJumps = 0;
				upwardsSpeed = 0;
				speedUp = 0;
				getPosition().y = terrain.getHeightAt(getPosition().x, getPosition().z);
			}
			distanceY = speedUp;
			distanceX = (float) (distanceForward * Math.sin(Math.toRadians(-getRotY())) + distanceRight * Math.sin(Math.toRadians(-getRotY() + 90)));
			distanceZ = (float) (distanceForward * Math.cos(Math.toRadians(-getRotY())) + distanceRight * Math.cos(Math.toRadians(-getRotY() + 90)));

		}

		// MOVE
		increasePosition(distanceX, distanceY, distanceZ);
	}

	public void reset() {
		setPosition(new Vector3f());
		super.setRotX(0);
		super.setRotY(0);
		super.setRotZ(0);
	}

	private void jump() {
		if (!inAir) {
			lastJump = DisplayManager.getLastFrameTime();
			this.upwardsSpeed = JUMP_POWER;
		} else if (!doubleJumpRotation && spaceReleased && doubleJumps < MAXDOUBLEJUMPS
				&& DisplayManager.getLastFrameTime() - lastJump >= JUMPTIMEDELTA) {
			doubleJumps++;
			doubleJumpRotationSpeed = 0;
			this.upwardsSpeed += JUMP_POWER * (doubleJumps + 0.3f);
			doubleJumpRotation = true;
		}
		inAir = true;
	}

	public void moveCamera(Camera cam) {
		// System.out.println("Y: " + getPosition().y);
		cam.setPosition(new Vector3f(getPosition().x, getPosition().y + CAMERA_HEADOFFSET, getPosition().z));

		cam.setPitch(getRotX());
		cam.setYaw(getRotY());
	}

}
