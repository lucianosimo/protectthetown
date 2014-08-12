package com.lucianosimo.protectthetown.object;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.util.Log;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.lucianosimo.protectthetown.manager.ResourcesManager;

public class Rock extends Sprite{

	private float xVel = 1;
	private float yVel = 1;
	
	private VertexBufferObjectManager vbom;
	private Camera camera;
	private PhysicsWorld physicsWorld;
	
	private boolean destroyed = false;
	
	private Body body;
	
	public Rock(float pX, float pY, VertexBufferObjectManager vbom, Camera camera, PhysicsWorld physicsWorld) {
		super(pX, pY, ResourcesManager.getInstance().game_rock_region.deepCopy(), vbom);
		this.vbom = vbom;
		this.camera = camera;
		this.physicsWorld = physicsWorld;
		createPhysics(camera, physicsWorld);
		//camera.setChaseEntity(this);
	}
	
	private void createPhysics(final Camera camera, PhysicsWorld physicsWorld) {
		body = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(0, 0, 0));
		body.setUserData("rock");
		body.setFixedRotation(true);
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false) {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				super.onUpdate(pSecondsElapsed);
				camera.onUpdate(0.1f);
				body.setLinearVelocity(xVel, yVel);
			}
		});
	}
	
	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		if (pSceneTouchEvent.isActionDown()) {
			Log.i("protect", "touched");
			this.setVisible(false);
			body.setActive(false);
			destroyed = true;
		}
		return true;
	}
	
	public void unDestroyRock() {
		destroyed = false;
	}
	
	public boolean isDestroyed() {
		return destroyed;
	}
	
	public void moveRock(float x, float y) {
		body.setTransform(x / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, y / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, body.getAngle());
	}
	
	public void setDirection(float x, float y) {
		xVel = x;
		yVel = y;
	}
	
	public Body getRockBody() {
		return body;
	}
}
