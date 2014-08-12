package com.lucianosimo.protectthetown.scene;

import java.util.Iterator;
import java.util.Random;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.scene.background.ParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
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
import com.lucianosimo.protectthetown.object.House;

public class GameScene extends BaseScene{
	
	//Scene indicators
	private HUD gameHud;
	
	//Physics world variable
	private PhysicsWorld physicsWorld;
	
	//Constants	
	private float screenWidth;
	private float screenHeight;
	
	//Instances
	private Sprite smallRock;
	private Sprite rock;
	private House house;
	
	//Sprites
	private Sprite floor;
	
	//Bodies
	private Body floor_body;
	private Body small_rock_body;
	private Body rock_body;
	
	//Booleans

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
		createRock();
		createHouses();
		
		//Sprites
		floor = new Sprite(RIGHT_MARGIN/2, 50, resourcesManager.game_floor_region, vbom);
		
		//Bodies
		floor_body = PhysicsFactory.createBoxBody(physicsWorld, floor, BodyType.StaticBody, PhysicsFactory.createFixtureDef(0, 0, 0));
		floor_body.setUserData("floor");
		
		floor.setCullingEnabled(true);
		GameScene.this.attachChild(floor);
	}
	
	public void createRock() {
		//n = rand.nextInt(max - min + 1) + min;
		Random rand = new Random();
		final int x = rand.nextInt(ROCK_MAX_RANDOM_X) + ROCK_MIN_RANDOM_X;
		final float yVel = -(rand.nextInt(15) + 7);
		
		rock = new Sprite(x, 700, resourcesManager.game_rock_region, vbom){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY) {
				final float x = this.getX();
				final float y = this.getY();
				final Sprite rockRef = this;
				engine.runOnUpdateThread(new Runnable() {
					
					@Override
					public void run() {
						createSmallRock(x, y);
						rockRef.setVisible(false);
						rock_body.setActive(false);
					}
				});
				return true;
			}
		};
		rock_body = PhysicsFactory.createBoxBody(physicsWorld, rock, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(0, 0, 0));
		rock_body.setUserData("rock");
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(rock, rock_body, true, false) {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				super.onUpdate(pSecondsElapsed);
				camera.onUpdate(0.1f);
				if (x > RIGHT_MARGIN/2) {
					rock_body.setLinearVelocity(ROCK_POSITIVE_VEL_X, yVel);
				} else {
					rock_body.setLinearVelocity(ROCK_NEGATIVE_VEL_X, yVel);
				}
			}
		});
	
		//rock_body.setTransform(x / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 700 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, rock_body.getAngle());
		
		
		rock.setCullingEnabled(true);
		GameScene.this.attachChild(rock);
		GameScene.this.registerTouchArea(rock);
	}
	
	public void createSmallRock(final float x, float y) {
		smallRock = new Sprite(x, y, resourcesManager.game_small_rock_region, vbom);
		small_rock_body = PhysicsFactory.createBoxBody(physicsWorld, smallRock, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(0, 0, 0));
		small_rock_body.setUserData("small_rock");
		
		Random rand = new Random();
		final float yVel = -(rand.nextInt(15) + 7);
		
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(smallRock, small_rock_body, true, false) {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				super.onUpdate(pSecondsElapsed);
				camera.onUpdate(0.1f);
				if (x > RIGHT_MARGIN/2) {
					small_rock_body.setLinearVelocity(ROCK_POSITIVE_VEL_X, yVel);
				} else {
					small_rock_body.setLinearVelocity(ROCK_NEGATIVE_VEL_X, yVel);
				}
			}
		});
		
		smallRock.setCullingEnabled(true);
		GameScene.this.attachChild(smallRock);
		GameScene.this.registerTouchArea(smallRock);
	}
	
	public void createHouses() {
		house = new House(600, 200, vbom, camera, physicsWorld);
		GameScene.this.attachChild(house);
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
							rock.setVisible(false);
							rock_body.setActive(false);
							createRock();
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("rock") && x2.getBody().getUserData().equals("house")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							house.setVisible(false);
							house.getHouseBody().setActive(false);
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
