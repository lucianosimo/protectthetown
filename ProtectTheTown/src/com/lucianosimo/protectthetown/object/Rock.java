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

public class Rock extends Sprite{

	private Body body;
	private FixtureDef fixture;
	
	public Rock(float pX, float pY, VertexBufferObjectManager vbom, Camera camera, PhysicsWorld physicsWorld) {
		super(pX, pY, ResourcesManager.getInstance().game_rock_region.deepCopy(), vbom);
		createPhysics(camera, physicsWorld);
		//camera.setChaseEntity(this);
	}
	
	private void createPhysics(final Camera camera, PhysicsWorld physicsWorld) {
		//n = rand.nextInt(max - min + 1) + min;
		Random rand = new Random();
		final int random = rand.nextInt(2) + 3;
		final float omega = random;
		final float width = 85 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		final float height = 95 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;		
		final Vector2[] v = {
			new Vector2(-0.36471f*width, -0.31579f*height),
			new Vector2(+0.03529f*width, -0.48421f*height),
			new Vector2(+0.37647f*width, -0.30526f*height),
			new Vector2(+0.51765f*width, -0.02105f*height),
			new Vector2(+0.37647f*width, +0.42105f*height),
			new Vector2(-0.08235f*width, +0.55789f*height),
			new Vector2(-0.55294f*width, +0.14737f*height),
		};		
		this.setUserData("rock");
		fixture = PhysicsFactory.createFixtureDef(0, 0, 0);
		fixture.filter.groupIndex = -1;
		//body = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.DynamicBody, fixture);
		body = PhysicsFactory.createPolygonBody(physicsWorld, this, v, BodyType.DynamicBody, fixture);
		body.setUserData("rock");
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
	
	public void setRockDirection(float x, float y) {
		body.setLinearVelocity(x, y);
	}
	
	public Body getRockBody() {
		return body;
	}
	
	public float getRockXVel() {
		return body.getLinearVelocity().x;
	}
}
