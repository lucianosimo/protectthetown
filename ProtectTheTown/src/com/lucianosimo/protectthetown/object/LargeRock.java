package com.lucianosimo.protectthetown.object;

import java.util.Random;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.lucianosimo.protectthetown.manager.ResourcesManager;

public class LargeRock extends Sprite{

	private Body body;
	private FixtureDef fixture;
	
	public LargeRock(float pX, float pY, VertexBufferObjectManager vbom, Camera camera, PhysicsWorld physicsWorld) {
		super(pX, pY, ResourcesManager.getInstance().game_large_rock_region.deepCopy(), vbom);
		createPhysics(camera, physicsWorld);
		//camera.setChaseEntity(this);
	}
	
	private void createPhysics(final Camera camera, PhysicsWorld physicsWorld) {
		//n = rand.nextInt(max - min + 1) + min;
		Random rand = new Random();
		final int random = rand.nextInt(2) + 1;
		final float omega = random;
		final float width = 160 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		final float height = 166 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		final Vector2[] v = {
			new Vector2(-0.23125f*width, -0.43373f*height),
			new Vector2(+0.05000f*width, -0.50602f*height),
			new Vector2(+0.36250f*width, -0.40361f*height),
			new Vector2(+0.53750f*width, +0.15060f*height),
			new Vector2(+0.14375f*width, +0.51807f*height),
			new Vector2(-0.36250f*width, +0.43976f*height),
			new Vector2(-0.51250f*width, -0.06627f*height),
		};
		
		this.setUserData("large_rock");
		fixture = PhysicsFactory.createFixtureDef(0, 0, 0);
		fixture.filter.groupIndex = -1;
		//body = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.DynamicBody, fixture);
		body = PhysicsFactory.createPolygonBody(physicsWorld, this, v, BodyType.DynamicBody, fixture);
		body.setUserData("large_rock");
		body.setFixedRotation(true);
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false) {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				super.onUpdate(pSecondsElapsed);
				camera.onUpdate(0.1f);
				body.setAngularVelocity(omega);
			}
		});
		this.registerEntityModifier(new LoopEntityModifier(new RotationModifier(5, 0, -(omega * 180))));
	}
	
	public void setLargeRockDirection(float x, float y) {
		body.setLinearVelocity(x, y);
	}
	
	public Body getLargeRockBody() {
		return body;
	}
	
	public float getLargeRockXVel() {
		return body.getLinearVelocity().x;
	}

}
