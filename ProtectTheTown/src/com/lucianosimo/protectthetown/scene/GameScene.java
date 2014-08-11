package com.lucianosimo.protectthetown.scene;

import java.util.Iterator;
import java.util.Random;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.scene.background.ParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.util.debug.Debug;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.lucianosimo.protectthetown.base.BaseScene;
import com.lucianosimo.protectthetown.manager.SceneManager.SceneType;
import com.lucianosimo.protectthetown.object.Rock;
import com.lucianosimo.protectthetown.pools.RockPool;

public class GameScene extends BaseScene{
	
	//Scene indicators
	private HUD gameHud;
	
	//Physics world variable
	private PhysicsWorld physicsWorld;
	
	//Constants	
	private float screenWidth;
	private float screenHeight;
	
	//Instances
	private RockPool rockPool;
	private Rock rock;
	
	//Sprites
	private Sprite floor;
	
	//Bodies
	private Body floor_body;
	
	//Variables
	private static final int LEFT_MARGIN = 0;
	private static final int RIGHT_MARGIN = 1280;
	
	private static final int ROCK_POSITIVE_VEL_X = 2;
	private static final int ROCK_NEGATIVE_VEL_X = -2;
	private static final int ROCK_MAX_RANDOM_X = 1000;
	private static final int ROCK_MIN_RANDOM_X = 100;

	@Override
	public void createScene() {
		screenWidth = resourcesManager.camera.getWidth();
		screenHeight = resourcesManager.camera.getHeight();
		createBackground();
		createPhysics();
		createHud();
		initializeGame();		
		//DebugRenderer debug = new DebugRenderer(physicsWorld, vbom);
        //GameScene.this.attachChild(debug);
	}
	
	private void initializeGame() {
		rockPool = new RockPool(vbom, camera, physicsWorld);
		createRock();
		
		//Sprites
		floor = new Sprite(RIGHT_MARGIN/2, 50, resourcesManager.game_floor_region, vbom);
		
		//Bodies
		floor_body = PhysicsFactory.createBoxBody(physicsWorld, floor, BodyType.StaticBody, PhysicsFactory.createFixtureDef(0, 0, 0));
		floor_body.setUserData("floor");
		
		floor.setCullingEnabled(true);
		GameScene.this.attachChild(floor);
	}
	
	public void createRock() {
		rock = rockPool.obtainPoolItem();
		//n = rand.nextInt(max - min + 1) + min;
		Random rand = new Random();
		int x = rand.nextInt(ROCK_MAX_RANDOM_X) + ROCK_MIN_RANDOM_X;
		float yVel = rand.nextInt(15) + 7;
		yVel = -yVel;
		rock.moveRock(x, 700);
		if (x > RIGHT_MARGIN/2) {
			rock.setDirection(ROCK_POSITIVE_VEL_X, yVel);
		} else {
			rock.setDirection(ROCK_NEGATIVE_VEL_X, yVel);
		}
		rock.setCullingEnabled(true);
		GameScene.this.attachChild(rock);
		GameScene.this.registerTouchArea(rock);
	}
	
	private void createBackground() {
		ParallaxBackground background = new ParallaxBackground(0, 0, 0);
		background.attachParallaxEntity(new ParallaxEntity(0, new Sprite(screenWidth/2, screenHeight/2, resourcesManager.game_background_region, vbom)));
		this.setBackground(background);
	}
	
	private void createHud() {
		gameHud = new HUD();		
		camera.setHUD(gameHud);
	}
		
	private void createPhysics() {
		physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, -3), false);
		physicsWorld.setContactListener(contactListener());
		registerUpdateHandler(physicsWorld);
	}
	
	private ContactListener contactListener() {
		ContactListener contactListener = new ContactListener() {
			
			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
			}
			
			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
	
			}
			
			@Override
			public void endContact(Contact contact) {
				
			}
			
			@Override
			public void beginContact(Contact contact) {
				final Fixture x1 = contact.getFixtureA();
				final Fixture x2 = contact.getFixtureB();
				
				if (x1.getBody().getUserData().equals("rock") && x2.getBody().getUserData().equals("floor")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							rockPool.recyclePoolItem(rock);
							createRock();
						}
					});
				}
				
			}
		};
		return contactListener;
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_GAME;
	}

	@Override
	public void disposeScene() {
		
	}
	
	private void myGarbageCollection() {
		Iterator<Body> allMyBodies = physicsWorld.getBodies();
        while(allMyBodies.hasNext()) {
        	try {
        		final Body myCurrentBody = allMyBodies.next();
                	physicsWorld.destroyBody(myCurrentBody);                
            } catch (Exception e) {
            	Debug.e(e);
            }
        }
               
        this.clearChildScene();
        this.detachChildren();
        this.reset();
        this.detachSelf();
        physicsWorld.clearForces();
        physicsWorld.clearPhysicsConnectors();
        physicsWorld.reset();
 
        System.gc();
	}
	
	@Override
	public void handleOnPause() {

	}
	
	@Override
	public void onBackKeyPressed() {

	}
	
}
