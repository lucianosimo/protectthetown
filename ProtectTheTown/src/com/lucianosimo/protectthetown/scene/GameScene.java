package com.lucianosimo.protectthetown.scene;

import java.util.Iterator;
import java.util.Random;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.background.ParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.extension.debugdraw.DebugRenderer;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;
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
import com.lucianosimo.protectthetown.object.Floor;
import com.lucianosimo.protectthetown.object.House;
import com.lucianosimo.protectthetown.object.LargeHouse;
import com.lucianosimo.protectthetown.object.LargeRock;
import com.lucianosimo.protectthetown.object.Rock;
import com.lucianosimo.protectthetown.object.SmallHouse;
import com.lucianosimo.protectthetown.object.SmallRock;

public class GameScene extends BaseScene{
	
	//Scene indicators
	private HUD gameHud;
	
	//Physics world variable
	private PhysicsWorld physicsWorld;
	
	//Texts
	private Text scoreText;
	private Text countdownText;
	private Text gameOverText;
	
	//Constants	
	private float screenWidth;
	private float screenHeight;
	
	//Instances
	private SmallHouse smallHouse;
	private House house;
	private LargeHouse largeHouse;
	
	//Booleans
	
	//Integers
	private int score = 0;

	//Variables	
	private static final int ROCK_POSITIVE_VEL_X = 2;
	private static final int ROCK_NEGATIVE_VEL_X = -2;
	private static final int ROCK_INITIAL_Y = 800;
	
	private static final int LARGE_ROCK_MAX_RANDOM_Y_VEL = 8;
	private static final int LARGE_ROCK_MIN_RANDOM_Y_VEL = 5;
	
	private static final int ROCK_MAX_RANDOM_Y_VEL = 8;
	private static final int ROCK_MIN_RANDOM_Y_VEL = 5;
	
	private static final int SMALL_ROCK_MAX_RANDOM_Y_VEL = 10;
	private static final int SMALL_ROCK_MIN_RANDOM_Y_VEL = 5;
	
	private static final int ROCK_MAX_RANDOM_X = 1000;
	private static final int ROCK_MIN_RANDOM_X = 100;
	
	private static final int LARGE_ROCK_SCORE = 100;
	private static final int ROCK_SCORE = 250;
	private static final int SMALL_ROCK_SCORE = 500;
	
	private static final int UPDATES = 200;

	@Override
	public void createScene() {
		screenWidth = resourcesManager.camera.getWidth();
		screenHeight = resourcesManager.camera.getHeight();
		createBackground();
		createPhysics();
		createHud();
		initializeGame();		
		DebugRenderer debug = new DebugRenderer(physicsWorld, vbom);
        GameScene.this.attachChild(debug);
	}
	
	private void initializeGame() {
		createFloor();
		createHouses();	
		engine.registerUpdateHandler(new IUpdateHandler() {
			private int updates = 0;
			
			@Override
			public void reset() {

			}
			
			@Override
			public void onUpdate(float pSecondsElapsed) {
				updates++;
				if (updates == 50) {
					countdownText.setText("2");
				}
				if (updates == 100) {
					countdownText.setText("1");
				}
				if (updates == 150) {
					countdownText.setPosition(screenWidth/2 - 250, screenHeight/2);
					countdownText.setText("Protect!!!");
				}
				if (updates > UPDATES) {
					createLargeRock();
					gameHud.detachChild(countdownText);
					engine.unregisterUpdateHandler(this);
				}
			}
		});
		//createRock();
		//createSmallRock();
	}
	
	/*
	 * Creates a new large rock
	 */
	private void createLargeRock() {
		//n = rand.nextInt(max - min + 1) + min;
		Random rand = new Random();
		final int x = rand.nextInt(ROCK_MAX_RANDOM_X) + ROCK_MIN_RANDOM_X;
		final float yVel = -(rand.nextInt(LARGE_ROCK_MAX_RANDOM_Y_VEL) + LARGE_ROCK_MIN_RANDOM_Y_VEL);
		
		LargeRock largeRock = new LargeRock(x, ROCK_INITIAL_Y, vbom, camera, physicsWorld){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY) {
				final LargeRock largeRockRef = this;
				engine.runOnUpdateThread(new Runnable() {
					
					@Override
					public void run() {
						if (largeRockRef.getLargeRockBody().isActive()) {
							createRockFromLargeRock(largeRockRef.getX() + 5, largeRockRef.getY(), ROCK_POSITIVE_VEL_X);
							createRockFromLargeRock(largeRockRef.getX() - 5, largeRockRef.getY(), ROCK_NEGATIVE_VEL_X);
							addScore(LARGE_ROCK_SCORE);
						}
						largeRockRef.setVisible(false);
						largeRockRef.getLargeRockBody().setActive(false);
					}
				});
				return true;
			}
		};
		
		setRockDirection(x, largeRock.getLargeRockBody(), yVel);

		largeRock.setCullingEnabled(true);
		GameScene.this.attachChild(largeRock);
		GameScene.this.registerTouchArea(largeRock);
	}
	
	/*
	 * Creates a new medium rock
	 */
	private void createRock() {
		//n = rand.nextInt(max - min + 1) + min;
		Random rand = new Random();
		final int x = rand.nextInt(ROCK_MAX_RANDOM_X) + ROCK_MIN_RANDOM_X;
		final float yVel = -(rand.nextInt(ROCK_MAX_RANDOM_Y_VEL) + ROCK_MIN_RANDOM_Y_VEL);
		
		Rock rock = new Rock(x, ROCK_INITIAL_Y, vbom, camera, physicsWorld){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY) {
				final Rock rockRef = this;
				engine.runOnUpdateThread(new Runnable() {
					
					@Override
					public void run() {
						if (rockRef.getRockBody().isActive()) {
							createSmallRockFromRock(rockRef.getX() + 5, rockRef.getY(), ROCK_POSITIVE_VEL_X);
							createSmallRockFromRock(rockRef.getX() - 5, rockRef.getY(), ROCK_NEGATIVE_VEL_X);
							addScore(ROCK_SCORE);
						}											
			 			rockRef.setVisible(false);
						rockRef.getRockBody().setActive(false);
					}
				});
				return true;
			}
		};
		
		setRockDirection(x, rock.getRockBody(), yVel);

		rock.setCullingEnabled(true);
		GameScene.this.attachChild(rock);
		GameScene.this.registerTouchArea(rock);
	}
	
	/*
	 * Creates a new rock when a large rock is destroyed
	 */
	private void createRockFromLargeRock(float x, float y, float xVel) {
		Random rand = new Random();
		final float yVel = -(rand.nextInt(ROCK_MAX_RANDOM_Y_VEL) + ROCK_MIN_RANDOM_Y_VEL);
		final float velocityMultiplier = rand.nextInt(3) + 3;
		
		Rock rock = new Rock(x, y, vbom, camera, physicsWorld) {
			
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY) {
				final Rock rockRef = this;
				engine.runOnUpdateThread(new Runnable() {
					
					@Override
					public void run() {
						if (rockRef.getRockBody().isActive()) {
							createSmallRockFromRock(rockRef.getX() + 5, rockRef.getY(), ROCK_POSITIVE_VEL_X);
							createSmallRockFromRock(rockRef.getX() - 5, rockRef.getY(), ROCK_NEGATIVE_VEL_X);
							addScore(ROCK_SCORE);
						}
						rockRef.setVisible(false);
						rockRef.getRockBody().setActive(false);			
					}
				});				
				return true;
			}
			
		};

		rock.setRockDirection(velocityMultiplier * xVel, yVel);
				
		rock.setCullingEnabled(true);
		GameScene.this.attachChild(rock);
		GameScene.this.registerTouchArea(rock);		
	}
		
	/*
	 * Creates a new small rock
	 */
	private void createSmallRock() {
		Random rand = new Random();
		final int x = rand.nextInt(ROCK_MAX_RANDOM_X) + ROCK_MIN_RANDOM_X;
		final float yVel = -(rand.nextInt(SMALL_ROCK_MAX_RANDOM_Y_VEL) + SMALL_ROCK_MIN_RANDOM_Y_VEL);
		
		SmallRock smallRock = new SmallRock(x, ROCK_INITIAL_Y, vbom, camera, physicsWorld) {
			
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY) {
				final SmallRock smallRockRef = this;
				engine.runOnUpdateThread(new Runnable() {
					
					@Override
					public void run() {
						if (smallRockRef.getSmallRockBody().isActive()) {
							addScore(SMALL_ROCK_SCORE);
						}						
						smallRockRef.setVisible(false);
						smallRockRef.getSmallRockBody().setActive(false);			
					}
				});
				return true;
			}
			
		};
		
		setRockDirection(x, smallRock.getSmallRockBody(), yVel);
				
		smallRock.setCullingEnabled(true);
		GameScene.this.attachChild(smallRock);
		GameScene.this.registerTouchArea(smallRock);		
	}
	
	/*
	 * Creates a new small rock when a medium rock is destroyed
	 */
	private void createSmallRockFromRock(float x, float y, float xVel) {
		Random rand = new Random();
		final float yVel = -(rand.nextInt(SMALL_ROCK_MAX_RANDOM_Y_VEL) + SMALL_ROCK_MIN_RANDOM_Y_VEL);
		final float velocityMultiplier = rand.nextInt(3) + 3;
		
		SmallRock smallRock = new SmallRock(x, y, vbom, camera, physicsWorld) {
			
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY) {
				final SmallRock smallRockRef = this;
				engine.runOnUpdateThread(new Runnable() {
					
					@Override
					public void run() {
						if (smallRockRef.getSmallRockBody().isActive()) {
							addScore(SMALL_ROCK_SCORE);
						}
						smallRockRef.setVisible(false);
						smallRockRef.getSmallRockBody().setActive(false);			
					}
				});				
				return true;
			}
			
		};

		smallRock.setSmallRockDirection(velocityMultiplier * xVel, yVel);
				
		smallRock.setCullingEnabled(true);
		GameScene.this.attachChild(smallRock);
		GameScene.this.registerTouchArea(smallRock);		
	}
	
	/*
	 * Used to regenerate the rocks when they touch the floor.
	 */
	private void regenerateRocks(Body rockBody) {
		Random rand = new Random();
		final int x = rand.nextInt(ROCK_MAX_RANDOM_X) + ROCK_MIN_RANDOM_X;
		final float yVel = -(rand.nextInt(ROCK_MAX_RANDOM_Y_VEL) + ROCK_MIN_RANDOM_Y_VEL);
		
		rockBody.setTransform(x / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, ROCK_INITIAL_Y / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, rockBody.getAngle());
		if (x > screenWidth/2) {
			rockBody.setLinearVelocity(ROCK_POSITIVE_VEL_X, yVel);
		} else {
			rockBody.setLinearVelocity(ROCK_NEGATIVE_VEL_X, yVel);
		}
	}
	
	/*
	 * Creates houses on level generation
	 */
	private void createHouses() {
		final int housesInitialHeight = 400;
		final int healthBarWidth = 100;
		
		final Rectangle houseHealthBarBackground = new Rectangle(50, 250, healthBarWidth, 10, vbom);
		final Rectangle houseHealthBar = new Rectangle(50, 250, healthBarWidth, 10, vbom);
		final Rectangle smallHouseHealthBarBackground = new Rectangle(50, 150, healthBarWidth, 10, vbom);
		final Rectangle smallHouseHealthBar = new Rectangle(50, 150, healthBarWidth, 10, vbom);
		final Rectangle largeHouseHealthBarBackground = new Rectangle(50, 350, 102, 10, vbom);
		final Rectangle largeHouseHealthBar = new Rectangle(50, 350, 102, 10, vbom);
		
		houseHealthBarBackground.setColor(Color.RED_ARGB_PACKED_INT);
		houseHealthBar.setColor(Color.GREEN_ARGB_PACKED_INT);
		smallHouseHealthBarBackground.setColor(Color.RED_ARGB_PACKED_INT);
		smallHouseHealthBar.setColor(Color.GREEN_ARGB_PACKED_INT);
		largeHouseHealthBarBackground.setColor(Color.RED_ARGB_PACKED_INT);
		largeHouseHealthBar.setColor(Color.GREEN_ARGB_PACKED_INT);
		
		house = new House(600, housesInitialHeight, vbom, camera, physicsWorld) {
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				super.onManagedUpdate(pSecondsElapsed);
				float energyWidthFactor = healthBarWidth / this.getMaxEnergy();
				if (this.isHouseDestroyed() && this.getHouseBody().isActive()) {
					this.setVisible(false);
					this.getHouseBody().setActive(false);
				}
				houseHealthBar.setSize(this.getHouseEnergy() * energyWidthFactor, houseHealthBar.getHeight());
				houseHealthBar.setPosition((this.getHouseEnergy() * energyWidthFactor) / 2, houseHealthBar.getY());
			}

			@Override
			public void onDie() {
				if (this.isHouseDestroyed() && smallHouse.isSmallHouseDestroyed() && largeHouse.isLargeHouseDestroyed()) {
					gameOver();
				}
				
			}
		};
		
		smallHouse = new SmallHouse(300, housesInitialHeight, vbom, camera, physicsWorld) {
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				super.onManagedUpdate(pSecondsElapsed);
				float energyWidthFactor = healthBarWidth / this.getMaxEnergy();
				if (this.isSmallHouseDestroyed() && this.getSmallHouseBody().isActive()) {
					this.setVisible(false);
					this.getSmallHouseBody().setActive(false);
				}
				smallHouseHealthBar.setSize(this.getSmallHouseEnergy() * energyWidthFactor, smallHouseHealthBar.getHeight());
				smallHouseHealthBar.setPosition((this.getSmallHouseEnergy() * energyWidthFactor) / 2, smallHouseHealthBar.getY());
			}

			@Override
			public void onDie() {
				if (this.isSmallHouseDestroyed() && house.isHouseDestroyed() && largeHouse.isLargeHouseDestroyed()) {
					gameOver();
				}
			}
		};
		
		largeHouse = new LargeHouse(900, housesInitialHeight, vbom, camera, physicsWorld) {
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				super.onManagedUpdate(pSecondsElapsed);
				float energyWidthFactor = 102 / this.getMaxEnergy();
				if (this.isLargeHouseDestroyed() && this.getLargeHouseBody().isActive()) {
					this.setVisible(false);
					this.getLargeHouseBody().setActive(false);
				}
				largeHouseHealthBar.setSize(this.getLargeHouseEnergy() * energyWidthFactor, largeHouseHealthBar.getHeight());
				largeHouseHealthBar.setPosition((this.getLargeHouseEnergy() * energyWidthFactor) / 2, largeHouseHealthBar.getY());
			}

			@Override
			public void onDie() {
				if (this.isLargeHouseDestroyed() && house.isHouseDestroyed() && smallHouse.isSmallHouseDestroyed()) {
					gameOver();
				}
			}
		};
		
		house.attachChild(houseHealthBarBackground);
		house.attachChild(houseHealthBar);
		smallHouse.attachChild(smallHouseHealthBarBackground);
		smallHouse.attachChild(smallHouseHealthBar);
		largeHouse.attachChild(largeHouseHealthBarBackground);
		largeHouse.attachChild(largeHouseHealthBar);
		GameScene.this.attachChild(house);
		GameScene.this.attachChild(smallHouse);
		GameScene.this.attachChild(largeHouse);
	}
	
	/*
	 * Creates floor on level generation
	 */
	private void createFloor() {
		int[] floor_positions = {80, 241, 404, 567, 730, 896, 1055, 1220};
		Floor[] floor = new Floor[8];
 		
		Sprite base_floor = new Sprite(screenWidth/2, 25, resourcesManager.game_base_floor_region, vbom);
		Body base_floor_body = PhysicsFactory.createBoxBody(physicsWorld, base_floor, BodyType.StaticBody, PhysicsFactory.createFixtureDef(0, 0, 0));
		base_floor_body.setUserData("base_floor");
		base_floor.setCullingEnabled(true);
		GameScene.this.attachChild(base_floor);
		
		for (int i = 0; i < 8; i++) {
			floor[i] = new Floor(floor_positions[i], 100 + 10 * i, vbom, camera, physicsWorld);
			floor[i].setCullingEnabled(true);
			GameScene.this.attachChild(floor[i]);
		}
		
	}
	
	private void setRockDirection(float x, Body body, float yVel) {
		if (x > screenWidth/2) {
			body.setLinearVelocity(ROCK_POSITIVE_VEL_X, yVel);
		} else {
			body.setLinearVelocity(ROCK_NEGATIVE_VEL_X, yVel);
		}
	}
	
	private void createBackground() {
		ParallaxBackground background = new ParallaxBackground(0, 0, 0);
		background.attachParallaxEntity(new ParallaxEntity(0, new Sprite(screenWidth/2, screenHeight/2, resourcesManager.game_background_region, vbom)));
		this.setBackground(background);
	}
	
	private void createHud() {
		gameHud = new HUD();	
		
		scoreText = new Text(50, 650, resourcesManager.scoreFont, "Score: 0123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
		countdownText = new Text(screenWidth/2, screenHeight/2, resourcesManager.countdownFont, "321Protect!", new TextOptions(HorizontalAlign.CENTER), vbom);
		
		scoreText.setAnchorCenter(0, 0);
		countdownText.setAnchorCenter(0, 0);
		
		scoreText.setText("Score: " + score);
		countdownText.setText("3");
		
		scoreText.setColor(Color.WHITE_ARGB_PACKED_INT);
		countdownText.setColor(Color.RED_ARGB_PACKED_INT);

		gameHud.attachChild(scoreText);
		gameHud.attachChild(countdownText);

		camera.setHUD(gameHud);
	}
		
	private void createPhysics() {
		physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, -3), false);
		physicsWorld.setContactListener(contactListener());
		registerUpdateHandler(physicsWorld);
	}
	
	private void addScore(int score) {
		this.score += score;
		scoreText.setText("Score: " + this.score);
	}
	
	public void gameOver() {
		engine.runOnUpdateThread(new Runnable() {
			
			@Override
			public void run() {
				GameScene.this.setIgnoreUpdate(true);
		        camera.setChaseEntity(null);
		        gameOverText = new Text(screenWidth/2 - 425, screenHeight/2, resourcesManager.gameOverFont, "GameOver! Yourscore:123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
				gameOverText.setAnchorCenter(0, 0);
				gameOverText.setColor(Color.RED_ARGB_PACKED_INT);
				gameOverText.setText("Game Over!!! Your score: " + score);
				GameScene.this.attachChild(gameOverText);
			}
		});
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
				
				if (x1.getBody().getUserData().equals("large_rock") && x2.getBody().getUserData().equals("floor")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							regenerateRocks(x1.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("floor") && x2.getBody().getUserData().equals("large_rock")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							regenerateRocks(x2.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("rock") && x2.getBody().getUserData().equals("floor")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							regenerateRocks(x1.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("floor") && x2.getBody().getUserData().equals("rock")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							regenerateRocks(x2.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("small_rock") && x2.getBody().getUserData().equals("floor")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							regenerateRocks(x1.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("floor") && x2.getBody().getUserData().equals("small_rock")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							regenerateRocks(x2.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("large_rock") && x2.getBody().getUserData().equals("house")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							house.destroyHouse();
							regenerateRocks(x1.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("house") && x2.getBody().getUserData().equals("large_rock")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							house.destroyHouse();
							regenerateRocks(x2.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("large_rock") && x2.getBody().getUserData().equals("small_house")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							smallHouse.destroySmallHouse();
							regenerateRocks(x1.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("small_house") && x2.getBody().getUserData().equals("large_rock")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							smallHouse.destroySmallHouse();
							regenerateRocks(x2.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("large_rock") && x2.getBody().getUserData().equals("large_house")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							largeHouse.damageLargeHouse();
							largeHouse.damageLargeHouse();
							regenerateRocks(x1.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("large_house") && x2.getBody().getUserData().equals("large_rock")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							largeHouse.damageLargeHouse();
							largeHouse.damageLargeHouse();
							regenerateRocks(x2.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("rock") && x2.getBody().getUserData().equals("house")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							house.damageHouse();
							house.damageHouse();
							regenerateRocks(x1.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("house") && x2.getBody().getUserData().equals("rock")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							house.damageHouse();
							house.damageHouse();
							regenerateRocks(x2.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("rock") && x2.getBody().getUserData().equals("small_house")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							smallHouse.damageSmallHouse();
							smallHouse.damageSmallHouse();
							regenerateRocks(x1.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("large_house") && x2.getBody().getUserData().equals("rock")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							largeHouse.damageLargeHouse();
							largeHouse.damageLargeHouse();
							regenerateRocks(x2.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("rock") && x2.getBody().getUserData().equals("large_house")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							largeHouse.damageLargeHouse();
							largeHouse.damageLargeHouse();
							regenerateRocks(x1.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("small_house") && x2.getBody().getUserData().equals("rock")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							smallHouse.damageSmallHouse();
							smallHouse.damageSmallHouse();
							regenerateRocks(x2.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("small_rock") && x2.getBody().getUserData().equals("house")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							house.damageHouse();
							regenerateRocks(x1.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("house") && x2.getBody().getUserData().equals("small_rock")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							house.damageHouse();
							regenerateRocks(x2.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("small_rock") && x2.getBody().getUserData().equals("small_house")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							smallHouse.damageSmallHouse();
							regenerateRocks(x1.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("small_house") && x2.getBody().getUserData().equals("small_rock")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							smallHouse.damageSmallHouse();
							regenerateRocks(x2.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("small_rock") && x2.getBody().getUserData().equals("large_house")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							largeHouse.damageLargeHouse();
							regenerateRocks(x1.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("large_house") && x2.getBody().getUserData().equals("small_rock")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							largeHouse.damageLargeHouse();
							regenerateRocks(x2.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("house") && x2.getBody().getUserData().equals("floor")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							house.getHouseBody().setType(BodyType.StaticBody);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("floor") && x2.getBody().getUserData().equals("house")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							house.getHouseBody().setType(BodyType.StaticBody);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("small_house") && x2.getBody().getUserData().equals("floor")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							smallHouse.getSmallHouseBody().setType(BodyType.StaticBody);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("floor") && x2.getBody().getUserData().equals("small_house")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							smallHouse.getSmallHouseBody().setType(BodyType.StaticBody);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("large_house") && x2.getBody().getUserData().equals("floor")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							largeHouse.getLargeHouseBody().setType(BodyType.StaticBody);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("floor") && x2.getBody().getUserData().equals("large_house")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							largeHouse.getLargeHouseBody().setType(BodyType.StaticBody);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("floor") && x2.getBody().getUserData().equals("base_floor")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							x1.getBody().setType(BodyType.StaticBody);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("base_floor") && x2.getBody().getUserData().equals("floor")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							x2.getBody().setType(BodyType.StaticBody);
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
