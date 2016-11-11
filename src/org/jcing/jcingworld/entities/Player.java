package org.jcing.jcingworld.entities;

import org.jcing.jcingworld.io.KeyBoard;
import org.jcing.jcingworld.io.Mouse;
import org.jcing.jcingworld.main.MainGameLoop;
import org.jcing.jcingworld.models.TexturedModel;
import org.jcing.jcingworld.renderengine.DisplayManager;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Vector3f;

public class Player extends Entity {

	public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
		// TODO Auto-generated constructor stub
	}

	private static final float SPEED = 17.5f;
	private static final float TURN = 7.5f;
	private static final float SPEEDMOD = 7.5f;
	private static final float HEADOFFSET = 3.5f;

	private static final float GRAVITY = -70;
	private static final float JUMP_POWER = 40;

	private float currSpeed = 0;

	private boolean flying = false;

	private boolean inAir = false;

	private float forwardSpeed = 0;
	private float upwardsSpeed = 0;
	private float rightSpeed = 0;

	public void move() {

		flying = KeyBoard.toggled(GLFW.GLFW_KEY_F);

		// SPEED
		if (KeyBoard.key(GLFW.GLFW_KEY_LEFT_SHIFT)) {
			currSpeed = SPEED * SPEEDMOD;
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
			}
		}

		// ROTATION Horizontal
		this.increaseRotation(0, (float) (Mouse.deltaX * TURN * DisplayManager.getFrameTimeSeconds()), 0);

		// Vertical
		if ((float) (Mouse.deltaY * TURN * DisplayManager.getFrameTimeSeconds()) + getRotX() > -90
				&& (float) (Mouse.deltaY * TURN * DisplayManager.getFrameTimeSeconds()) + getRotX() < 90) {

			this.increaseRotation((float) (Mouse.deltaY * TURN * DisplayManager.getFrameTimeSeconds()), 0, 0);
		}

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
			distanceUp = (float) (distanceForward * Math.sin(Math.toRadians(getRotX())) - speedUp * Math.sin(Math.toRadians(getRotX() - 90)));
			distanceForward = (float) (distanceForward * Math.cos(Math.toRadians(getRotX())) - speedUp * Math.cos(Math.toRadians(getRotX() - 90)));
			// distanceRight = (float) (distanceRight *
			// Math.cos(Math.toRadians(getRotX()))
			// - speedUp * Math.cos(Math.toRadians(getRotX() - 90)));

			distanceX = (float) (distanceForward * Math.sin(Math.toRadians(-getRotY())) + distanceRight * Math.sin(Math.toRadians(-getRotY() + 90)));
			distanceZ = (float) (distanceForward * Math.cos(Math.toRadians(-getRotY())) + distanceRight * Math.cos(Math.toRadians(-getRotY() + 90)));
			distanceY = distanceUp;

		} else {
			// NOT FLYING
			if (getPosition().y + speedUp < MainGameLoop.getTerrainHeight(getPosition().x, getPosition().z)) {
				inAir = false;
				upwardsSpeed = 0;
				speedUp = 0;
				getPosition().y = MainGameLoop.getTerrainHeight(getPosition().x, getPosition().z);
			}
			distanceY = speedUp;
			distanceX = (float) (distanceForward * Math.sin(Math.toRadians(-getRotY())) + distanceRight * Math.sin(Math.toRadians(-getRotY() + 90)));
			distanceZ = (float) (distanceForward * Math.cos(Math.toRadians(-getRotY())) + distanceRight * Math.cos(Math.toRadians(-getRotY() + 90)));

		}

		// MOVE
		increasePosition(distanceX, distanceY, distanceZ);
	}

	private void jump() {
		if (!inAir) {
			this.upwardsSpeed = JUMP_POWER;
		}
		inAir = true;
	}

	public void moveCamera(Camera cam) {
		// System.out.println("Y: " + getPosition().y);
		cam.setPosition(new Vector3f(getPosition().x, getPosition().y + HEADOFFSET, getPosition().z));

		cam.setPitch(getRotX());
		cam.setYaw(getRotY());
	}

}
