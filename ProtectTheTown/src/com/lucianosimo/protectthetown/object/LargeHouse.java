package com.lucianosimo.protectthetown.object;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.lucianosimo.protectthetown.manager.ResourcesManager;

public abstract class LargeHouse extends Sprite{

	private Body body;
	private int energy;
	
	private final static int MAX_ENERGY = 6;
	
	public abstract void onDie();
	
	public LargeHouse(float pX, float pY, VertexBufferObjectManager vbom, Camera camera, PhysicsWorld physicsWorld) {
		super(pX, pY, ResourcesManager.getInstance().game_large_house_region.deepCopy(), vbom);
		createPhysics(camera, physicsWorld);
		energy = MAX_ENERGY;
		//camera.setChaseEntity(this);
	}
	
	private void createPhysics(final Camera camera, PhysicsWorld physicsWorld) {
		body = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(0, 0, 0));
		body.setUserData("large_house");
		body.setFixedRotation(true);
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false) {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				super.onUpdate(pSecondsElapsed);
				camera.onUpdate(0.1f);
				body.setLinearVelocity(body.getLinearVelocity().x, -7);
				if (isLargeHouseDestroyed()) {
					onDie();
				}
			}
		});
	}
	
	public Body getLargeHouseBody() {
		return body;
	}
	
	public int getMaxEnergy() {
		return MAX_ENERGY;
	}
	
	public void repairCompleteLargeHouse() {
		energy = MAX_ENERGY;
	}
	
	public void repairPartialLargeHouse() {
		if (energy < MAX_ENERGY) {
			energy++;
		}		
	}
	
	public void destroyLargeHouse() {
		energy = 0;
	}
	
	public void damageLargeHouse() {
		energy--;
	}
	
	public int getLargeHouseEnergy() {
		return energy;
	}
	
	public boolean isLargeHouseDestroyed() {
		if (energy > 0) {
			return false;
		} else {
			return true;
		}
	}
	
}
