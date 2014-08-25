package com.lucianosimo.protectthetown.scene;

import java.util.Iterator;
import java.util.Random;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.background.ParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;
import org.andengine.util.debug.Debug;
import org.andengine.util.modifier.IModifier;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.lucianosimo.protectthetown.base.BaseScene;
import com.lucianosimo.protectthetown.manager.SceneManager;
import com.lucianosimo.protectthetown.manager.SceneManager.SceneType;
import com.lucianosimo.protectthetown.object.Bomb;
import com.lucianosimo.protectthetown.object.Earth;
import com.lucianosimo.protectthetown.object.Floor;
import com.lucianosimo.protectthetown.object.House;
import com.lucianosimo.protectthetown.object.LargeHouse;
import com.lucianosimo.protectthetown.object.LargeRock;
import com.lucianosimo.protectthetown.object.Repair;
import com.lucianosimo.protectthetown.object.Rock;
import com.lucianosimo.protectthetown.object.Satelite;
import com.lucianosimo.protectthetown.object.Shield;
import com.lucianosimo.protectthetown.object.Shot;
import com.lucianosimo.protectthetown.object.SmallHouse;
import com.lucianosimo.protectthetown.object.SmallRock;
import com.lucianosimo.protectthetown.object.Tree;
import com.lucianosimo.protectthetown.object.Ufo;

public class GameScene extends BaseScene{
	
	//Scene indicators
	private HUD gameHud;
	
	//Physics world variable
	private PhysicsWorld physicsWorld;
	
	//Texts
	private Text scoreText;
	private Text countdownText;
	//private Text gameOverText;
	private Text finalScoreText;
	//private Text pauseText;
	
	//Constants	
	private float screenWidth;
	private float screenHeight;
	
	//Instances
	private SmallHouse smallHouse;
	private House house;
	private LargeHouse largeHouse;
	
	//Booleans
	private boolean availablePause = false;
	private boolean gameOver = false;
	private boolean destroyAllEnemies = false;
	private boolean domeActivated = false;
	
	//Integers
	private int score = 0;
	
	//Windows
	private Sprite gameOverWindow;
	private Sprite pauseWindow;
	
	//Buttons
	private Sprite resumeButton;
	private Sprite retryButton;
	private Sprite quitButton;
	private Sprite pauseButton;
	
	//Dome
	private Sprite dome;
	private Sprite shieldBarFrame;
	private Sprite shieldBarLogo;
	
	//Rectangle
	private Rectangle fade;
	private Rectangle shieldBarBackground;
	private Rectangle shieldBar;
	
	//Counters
	private int largeRocksCounter = 0;
	private int rocksCounter = 0;
	private int smallRocksCounter = 0;
	private int ufoCounter = 0;
	private int sateliteCounter = 0;
	
	//Explosions
	private AnimatedSprite explosion;
	private AnimatedSprite small_explosion;

	//Variables	
	private static final int ROCK_POSITIVE_VEL_X = 2;
	private static final int ROCK_NEGATIVE_VEL_X = -2;
	private static final int ROCK_INITIAL_Y = 800;
	private static final int UFO_INITIAL_Y = 600;
	private static final int SATELITE_INITIAL_Y = 1500;
	
	private static final int LARGE_ROCK_MAX_RANDOM_Y_VEL = 5;
	private static final int LARGE_ROCK_MIN_RANDOM_Y_VEL = 3;
	
	private static final int ROCK_MAX_RANDOM_Y_VEL = 6;
	private static final int ROCK_MIN_RANDOM_Y_VEL = 4;
	
	private static final int SMALL_ROCK_MAX_RANDOM_Y_VEL = 7;
	private static final int SMALL_ROCK_MIN_RANDOM_Y_VEL = 5;
	
	private static final int ROCK_MAX_RANDOM_X = 1000;
	private static final int ROCK_MIN_RANDOM_X = 100;
	
	private static final int LARGE_ROCK_SCORE = 100;
	private static final int ROCK_SCORE = 250;
	private static final int SMALL_ROCK_SCORE = 500;
	private static final int UFO_SCORE = 1000;
	private static final int SATELITE_SCORE = 1000;
	
	private static final int START_GAME_UPDATES = 200;
	private static final int LARGE_ROCK_CREATION_UPDATES = 250;
	private static final int UFO_CREATION_UPDATES = 500;
	private static final int SATELITE_CREATION_UPDATES = 750;
	private static final int HELP_BOXES_CREATION_UPDATES = 500;
	
	private static final float SHIELD_DURATION = 10f;

	private static final int LARGE_ROCK_MAX = 2;
	private static final int UFO_MAX = 2;
	private static final int SATELITE_MAX = 1;
	
	//If negative, never collides between groups, if positive yes
	//private static final int GROUP_ENEMY = -1;

	@Override
	public void createScene() {
		screenWidth = resourcesManager.camera.getWidth();
		screenHeight = resourcesManager.camera.getHeight();
		createBackground();
		createPhysics();
		createHud();
		createWindows();
		initializeGame();		
		//DebugRenderer debug = new DebugRenderer(physicsWorld, vbom);
        //GameScene.this.attachChild(debug);
	}
	
	private void initializeGame() {
		createFloor();
		createHouses();
		createDecoration();
		createDome();
		engine.registerUpdateHandler(new IUpdateHandler() {
			private int updates = 0;
			private int difficulty = 0;
			
			@Override
			public void reset() {

			}
			
			@Override
			public void onUpdate(float pSecondsElapsed) {
				Random rand = new Random();
				int box;
				updates++;
				
				if (updates < START_GAME_UPDATES) {
					availablePause = false;
				}
				
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
				if (updates == START_GAME_UPDATES) {
					createShieldBox();
					gameHud.detachChild(countdownText);
					availablePause = true;
				}
				
				if ((((updates % LARGE_ROCK_CREATION_UPDATES) - difficulty) == 0) && (largeRocksCounter <= LARGE_ROCK_MAX) && availablePause) {
					createLargeRock();
					if (difficulty > (LARGE_ROCK_CREATION_UPDATES + 50)) {
						difficulty += 50;
					}					
				}
				if ((((updates % UFO_CREATION_UPDATES) - difficulty) == 0) && (ufoCounter <= UFO_MAX) && availablePause) {
					createUfo();
				}
				
				if (((updates % SATELITE_CREATION_UPDATES) - difficulty) == 0 && (sateliteCounter <= SATELITE_MAX) && availablePause) {
					createSatelite();
				}
				
				if (((updates % HELP_BOXES_CREATION_UPDATES) == 0) && availablePause) {
					//n = rand.nextInt(max - min + 1) + min;
					box = rand.nextInt(3) + 1;
					
					switch (box) {
						case 1:
							createBombBox();
							break;
						case 2:
							createRepairBox();
							break;
						case 3:
							createShieldBox();
							break;
						default:
							break;
					}
				}
				
				if (domeActivated && availablePause) {
					if (shieldBar.getWidth() > 0) {
						shieldBar.setSize(shieldBar.getWidth() - pSecondsElapsed * 40, shieldBar.getHeight());
					}
					shieldBar.setPosition((screenWidth/2 + screenWidth/4 - 450) + shieldBar.getWidth() / 2, shieldBar.getY());
				}
			}
		});
	}
	
	private void createSatelite() {
		//n = rand.nextInt(max - min + 1) + min;
		Random rand = new Random();
		final int whichHouse = rand.nextInt(3) + 1;
		final float initialX;
		
		switch (whichHouse) {
			case 1:
				initialX = smallHouse.getX();
				break;
			case 2:
				initialX = house.getX();
				break;
			case 3:
				initialX = largeHouse.getX();
				break;
			default:
				initialX = screenWidth/2;
				break;
		}
		
		Satelite satelite = new Satelite(initialX, SATELITE_INITIAL_Y, vbom, camera, physicsWorld) {
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				super.onManagedUpdate(pSecondsElapsed);
				if (destroyAllEnemies && this.getSateliteBody().isActive() && availablePause && !gameOver) {
					if (this.getX() > 0 && this.getX() < 1280 && this.getY() > 0 && this.getY() < 720) {
						final Satelite ref = this;
						engine.runOnUpdateThread(new Runnable() {
							
							@Override
							public void run() {
								addScore(SATELITE_SCORE);
								ref.setVisible(false);
								ref.getSateliteBody().setActive(false);
								sateliteCounter--;
								GameScene.this.unregisterTouchArea(ref);
								createExplosion(ref.getX(), ref.getY());
							}
						});
					}
				}
				if (this.collidesWith(dome) && this.getSateliteBody().isActive()) {
					final Satelite ref = this;
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							addScore(SATELITE_SCORE);
							ref.setVisible(false);
							ref.getSateliteBody().setActive(false);
							sateliteCounter--;
							GameScene.this.unregisterTouchArea(ref);
							createExplosion(ref.getX(), ref.getY());
						}
					});
				}
			}
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionDown()) {
					final Satelite satRef = this;
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							if (satRef.getSateliteBody().isActive() && availablePause && !gameOver) {
								addScore(SATELITE_SCORE);
								satRef.setVisible(false);
								satRef.getSateliteBody().setActive(false);
								sateliteCounter--;
								GameScene.this.unregisterTouchArea(satRef);
								createExplosion(satRef.getX(), satRef.getY());
							}						
						}
					});
				}				
				return true;
			}
		};
		
		sateliteCounter++;
		
		satelite.setCullingEnabled(true);
		GameScene.this.attachChild(satelite);
		GameScene.this.registerTouchArea(satelite);
	}
	
	private void createUfo() {
		//n = rand.nextInt(max - min + 1) + min;
		Random rand = new Random();
		final int ufoSpeed = rand.nextInt(3) + 5;
		final int ufoLimit = 200;
		final int appereanceSide = rand.nextInt(2) + 1;
		final int ufo_initial_x;
		final int ufoRandomRegion = rand.nextInt(3) + 1;
		final ITextureRegion ufoRegion;
		
		final Rectangle smallSensor = new Rectangle(smallHouse.getX(), screenHeight/2 , 0.1f, screenHeight, vbom);
		final Rectangle sensor = new Rectangle(house.getX(), screenHeight/2 , 0.1f, screenHeight, vbom);
		final Rectangle largeSensor = new Rectangle(largeHouse.getX(), screenHeight/2 , 0.1f, screenHeight, vbom);
		
		smallSensor.setVisible(false);
		sensor.setVisible(false);
		largeSensor.setVisible(false);
		
		switch (ufoRandomRegion) {
		case 1:
			ufoRegion = resourcesManager.game_ufo_1_region;
			break;
		case 2:
			ufoRegion = resourcesManager.game_ufo_2_region;
			break;
		case 3:
			ufoRegion = resourcesManager.game_ufo_3_region;
			break;
		default:
			ufoRegion = resourcesManager.game_ufo_1_region;
			break;
		}
		
		if (appereanceSide == 1) {
			ufo_initial_x = 1500;
		} else {
			ufo_initial_x = -250;
		}
		
		Ufo ufo = new Ufo(ufo_initial_x, UFO_INITIAL_Y, vbom, camera, physicsWorld, ufoRegion) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionDown()) {
					final Ufo ufoRef = this;
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							if (ufoRef.getUfoBody().isActive() && availablePause && !gameOver) {
								addScore(UFO_SCORE);
								ufoRef.setVisible(false);
								ufoRef.getUfoBody().setActive(false);
								ufoCounter--;
								GameScene.this.unregisterTouchArea(ufoRef);
								createExplosion(ufoRef.getX(), ufoRef.getY());
							}
							
						}
					});
				}
				
				return true;
			}
			
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				super.onManagedUpdate(pSecondsElapsed);
				if (destroyAllEnemies && this.getUfoBody().isActive() && availablePause && !gameOver) {
					if (this.getX() > 0 && this.getX() < 1280 && this.getY() > 0 && this.getY() < 720) {
						final Ufo ref = this;
						engine.runOnUpdateThread(new Runnable() {
							
							@Override
							public void run() {
								addScore(UFO_SCORE);
								ref.setVisible(false);
								ref.getUfoBody().setActive(false);
								sateliteCounter--;
								GameScene.this.unregisterTouchArea(ref);
								createExplosion(ref.getX(), ref.getY());
							}
						});
					}
				}
				
				if (this.collidesWith(dome)) {
					final Ufo ref = this;
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							addScore(UFO_SCORE);
							ref.setVisible(false);
							ref.getUfoBody().setActive(false);
							sateliteCounter--;
							GameScene.this.unregisterTouchArea(ref);
							createExplosion(ref.getX(), ref.getY());
						}
					});
				}
				
				if (this.getX() > (screenWidth + ufoLimit)) {
					this.setUfoVelocityX(-ufoSpeed);
				} else if (this.getX() < (-ufoLimit)) {
					this.setUfoVelocityX(ufoSpeed);
				}
				
				if (this.getY() > UFO_INITIAL_Y + 100) {
					this.setUfoVelocityY(-5);
					smallSensor.setPosition(smallHouse.getX(), screenHeight/2);
					sensor.setPosition(house.getX(), screenHeight/2);
					largeSensor.setPosition(largeHouse.getX(), screenHeight/2);
				} else if (this.getY() < UFO_INITIAL_Y - 100) {
					this.setUfoVelocityY(5);
					smallSensor.setPosition(smallHouse.getX(), screenHeight/2);
					sensor.setPosition(house.getX(), screenHeight/2);
					largeSensor.setPosition(largeHouse.getX(), screenHeight/2);
				}
				
				if (this.collidesWith(smallSensor)) {
					Shot shot = new Shot(this.getX(), this.getY(), vbom, camera, physicsWorld){
						protected void onManagedUpdate(float pSecondsElapsed) {
							if (this.collidesWith(dome)) {
								if (this.getX() > 0 && this.getX() < 1280 && this.getY() > 0 && this.getY() < 720) {
									final Shot ref = this;
									engine.runOnUpdateThread(new Runnable() {
										
										@Override
										public void run() {
											ref.setVisible(false);
											ref.getShotBody().setActive(false);
										}
									});
								}
							} 
						};
					};
					smallSensor.setPosition(10000, 10000);
					GameScene.this.attachChild(shot);
				}
				
				if (this.collidesWith(sensor)) {
					Shot shot = new Shot(this.getX(), this.getY(), vbom, camera, physicsWorld) {
						protected void onManagedUpdate(float pSecondsElapsed) {
							if (this.collidesWith(dome)) {
								if (this.getX() > 0 && this.getX() < 1280 && this.getY() > 0 && this.getY() < 720) {
									final Shot ref = this;
									engine.runOnUpdateThread(new Runnable() {
										
										@Override
										public void run() {
											ref.setVisible(false);
											ref.getShotBody().setActive(false);
										}
									});
								}
							} 
						};
					};
					sensor.setPosition(10000, 10000);
					GameScene.this.attachChild(shot);
				}
				
				if (this.collidesWith(largeSensor)) {
					Shot shot = new Shot(this.getX(), this.getY(), vbom, camera, physicsWorld) {
						protected void onManagedUpdate(float pSecondsElapsed) {
							if (this.collidesWith(dome)) {
								if (this.getX() > 0 && this.getX() < 1280 && this.getY() > 0 && this.getY() < 720) {
									final Shot ref = this;
									engine.runOnUpdateThread(new Runnable() {
										
										@Override
										public void run() {
											ref.setVisible(false);
											ref.getShotBody().setActive(false);
										}
									});
								}
							} 
						};
					};
					largeSensor.setPosition(10000, 10000);
					GameScene.this.attachChild(shot);
				}
				
			}
		};
		
		ufo.setCullingEnabled(true);
		
		ufoCounter++;
		
		GameScene.this.attachChild(smallSensor);
		GameScene.this.attachChild(sensor);
		GameScene.this.attachChild(largeSensor);
		GameScene.this.attachChild(ufo);
		GameScene.this.registerTouchArea(ufo);
	}
	
	/*
	 * Creates a new large rock
	 */
	private void createLargeRock() {
		//n = rand.nextInt(max - min + 1) + min;
		Random rand = new Random();
		final int x = rand.nextInt(ROCK_MAX_RANDOM_X) + ROCK_MIN_RANDOM_X;
		final float yVel = -(rand.nextInt(LARGE_ROCK_MAX_RANDOM_Y_VEL) + LARGE_ROCK_MIN_RANDOM_Y_VEL);
		
		LargeRock largeRock = new LargeRock(x, ROCK_INITIAL_Y, vbom, camera, physicsWorld) {
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				super.onManagedUpdate(pSecondsElapsed);
				if (destroyAllEnemies && this.getLargeRockBody().isActive() && availablePause && !gameOver) {
					if (this.getX() > 0 && this.getX() < 1280 && this.getY() > 0 && this.getY() < 720) {
						final LargeRock ref = this;
						engine.runOnUpdateThread(new Runnable() {
							
							@Override
							public void run() {
								addScore(LARGE_ROCK_SCORE);
								ref.setVisible(false);
								ref.getLargeRockBody().setActive(false);
								largeRocksCounter--;
								GameScene.this.unregisterTouchArea(ref);
								createExplosion(ref.getX(), ref.getY());
							}
						});
					}
				}
				if (this.collidesWith(dome) && this.getLargeRockBody().isActive()) {
					final LargeRock ref = this;
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							addScore(LARGE_ROCK_SCORE);
							ref.setVisible(false);
							ref.getLargeRockBody().setActive(false);
							largeRocksCounter--;
							GameScene.this.unregisterTouchArea(ref);
							createExplosion(ref.getX(), ref.getY());
						}
					});
				}
			}
			
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionDown()) {
					final LargeRock largeRockRef = this;
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							if (largeRockRef.getLargeRockBody().isActive() && availablePause && !gameOver) {
								createRockFromLargeRock(largeRockRef.getX() + 5, largeRockRef.getY(), ROCK_POSITIVE_VEL_X);
								createRockFromLargeRock(largeRockRef.getX() - 5, largeRockRef.getY(), ROCK_NEGATIVE_VEL_X);
								addScore(LARGE_ROCK_SCORE);
								largeRocksCounter--;
								largeRockRef.setVisible(false);
								largeRockRef.getLargeRockBody().setActive(false);
								GameScene.this.unregisterTouchArea(largeRockRef);
								createExplosion(largeRockRef.getX(), largeRockRef.getY());
							}						
						}
					});
				}
				
				return true;
			}
		};
		
		setRockDirection(x, largeRock.getLargeRockBody(), yVel);

		largeRocksCounter++;
		
		largeRock.setCullingEnabled(true);
		GameScene.this.attachChild(largeRock);
		GameScene.this.registerTouchArea(largeRock);
	}
	
	/*
	 * Creates a new medium rock
	 */
	/*private void createRock() {
		//n = rand.nextInt(max - min + 1) + min;
		Random rand = new Random();
		final int x = rand.nextInt(ROCK_MAX_RANDOM_X) + ROCK_MIN_RANDOM_X;
		final float yVel = -(rand.nextInt(ROCK_MAX_RANDOM_Y_VEL) + ROCK_MIN_RANDOM_Y_VEL);
		
		Rock rock = new Rock(x, ROCK_INITIAL_Y, vbom, camera, physicsWorld){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionDown()) {
					final Rock rockRef = this;
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							if (rockRef.getRockBody().isActive() && availablePause && !gameOver) {
								createSmallRockFromRock(rockRef.getX() + 5, rockRef.getY(), ROCK_POSITIVE_VEL_X);
								createSmallRockFromRock(rockRef.getX() - 5, rockRef.getY(), ROCK_NEGATIVE_VEL_X);
								addScore(ROCK_SCORE);
								rocksCounter--;
					 			rockRef.setVisible(false);
								rockRef.getRockBody().setActive(false);
								GameScene.this.unregisterTouchArea(rockRef);
								createExplosion(rockRef.getX(), rockRef.getY());
							}
						}
					});
				}
				
				return true;
			}
		};
		
		setRockDirection(x, rock.getRockBody(), yVel);

		rocksCounter++;
		
		rock.setCullingEnabled(true);
		GameScene.this.attachChild(rock);
		GameScene.this.registerTouchArea(rock);
	}*/
	
	/*
	 * Creates a new rock when a large rock is destroyed
	 */
	private void createRockFromLargeRock(float x, float y, float xVel) {
		Random rand = new Random();
		final float yVel = -(rand.nextInt(ROCK_MAX_RANDOM_Y_VEL) + ROCK_MIN_RANDOM_Y_VEL);
		final float velocityMultiplier = rand.nextInt(3) + 3;
		
		Rock rock = new Rock(x, y, vbom, camera, physicsWorld) {
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				super.onManagedUpdate(pSecondsElapsed);
				if (destroyAllEnemies && this.getRockBody().isActive() && availablePause && !gameOver) {
					if (this.getX() > 0 && this.getX() < 1280 && this.getY() > 0 && this.getY() < 720) {
						final Rock ref = this;
						engine.runOnUpdateThread(new Runnable() {
							
							@Override
							public void run() {
								addScore(ROCK_SCORE);
								ref.setVisible(false);
								ref.getRockBody().setActive(false);
								rocksCounter--;
								GameScene.this.unregisterTouchArea(ref);
								createExplosion(ref.getX(), ref.getY());
							}
						});
					}
				}
				if (this.collidesWith(dome) && this.getRockBody().isActive()) {
					final Rock ref = this;
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							addScore(ROCK_SCORE);
							ref.setVisible(false);
							ref.getRockBody().setActive(false);
							rocksCounter--;
							GameScene.this.unregisterTouchArea(ref);
							createExplosion(ref.getX(), ref.getY());
						}
					});
				}
			}
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionDown()) {
					final Rock rockRef = this;
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							if (rockRef.getRockBody().isActive() && availablePause && !gameOver) {
								createSmallRockFromRock(rockRef.getX() + 5, rockRef.getY(), ROCK_POSITIVE_VEL_X);
								createSmallRockFromRock(rockRef.getX() - 5, rockRef.getY(), ROCK_NEGATIVE_VEL_X);
								addScore(ROCK_SCORE);
								rocksCounter--;
								rockRef.setVisible(false);
								rockRef.getRockBody().setActive(false);
								GameScene.this.unregisterTouchArea(rockRef);
								createExplosion(rockRef.getX(), rockRef.getY());
							}										
						}
					});	
				}
							
				return true;
			}
			
		};

		rock.setRockDirection(velocityMultiplier * xVel, yVel);
		
		rocksCounter++;
				
		rock.setCullingEnabled(true);
		GameScene.this.attachChild(rock);
		GameScene.this.registerTouchArea(rock);		
	}
		
	/*
	 * Creates a new small rock
	 */
	/*private void createSmallRock() {
		Random rand = new Random();
		final int x = rand.nextInt(ROCK_MAX_RANDOM_X) + ROCK_MIN_RANDOM_X;
		final float yVel = -(rand.nextInt(SMALL_ROCK_MAX_RANDOM_Y_VEL) + SMALL_ROCK_MIN_RANDOM_Y_VEL);
		
		SmallRock smallRock = new SmallRock(x, ROCK_INITIAL_Y, vbom, camera, physicsWorld) {
			
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionDown()) {
					final SmallRock smallRockRef = this;
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							if (smallRockRef.getSmallRockBody().isActive() && availablePause && !gameOver) {
								addScore(SMALL_ROCK_SCORE);
								smallRocksCounter--;
								smallRockRef.setVisible(false);
								smallRockRef.getSmallRockBody().setActive(false);
								GameScene.this.unregisterTouchArea(smallRockRef);
								createExplosion(smallRockRef.getX(), smallRockRef.getY());
							}	
						}
					});
				}
				
				return true;
			}
			
		};
		
		setRockDirection(x, smallRock.getSmallRockBody(), yVel);
		
		smallRocksCounter++;
				
		smallRock.setCullingEnabled(true);
		GameScene.this.attachChild(smallRock);
		GameScene.this.registerTouchArea(smallRock);		
	}*/
	
	/*
	 * Creates a new small rock when a medium rock is destroyed
	 */
	private void createSmallRockFromRock(float x, float y, float xVel) {
		Random rand = new Random();
		final float yVel = -(rand.nextInt(SMALL_ROCK_MAX_RANDOM_Y_VEL) + SMALL_ROCK_MIN_RANDOM_Y_VEL);
		final float velocityMultiplier = rand.nextInt(3) + 3;
		
		SmallRock smallRock = new SmallRock(x, y, vbom, camera, physicsWorld) {
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				super.onManagedUpdate(pSecondsElapsed);
				if (destroyAllEnemies && this.getSmallRockBody().isActive() && availablePause && !gameOver) {
					if (this.getX() > 0 && this.getX() < 1280 && this.getY() > 0 && this.getY() < 720) {
						final SmallRock ref = this;
						engine.runOnUpdateThread(new Runnable() {
							
							@Override
							public void run() {
								addScore(SMALL_ROCK_SCORE);
								ref.setVisible(false);
								ref.getSmallRockBody().setActive(false);
								smallRocksCounter--;
								GameScene.this.unregisterTouchArea(ref);
								createExplosion(ref.getX(), ref.getY());
							}
						});
					}
				}
				if (this.collidesWith(dome) && this.getSmallRockBody().isActive()) {
					final SmallRock ref = this;
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							addScore(SMALL_ROCK_SCORE);
							ref.setVisible(false);
							ref.getSmallRockBody().setActive(false);
							smallRocksCounter--;
							GameScene.this.unregisterTouchArea(ref);
							createExplosion(ref.getX(), ref.getY());
						}
					});
				}
			}
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionDown()) {
					final SmallRock smallRockRef = this;
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							if (smallRockRef.getSmallRockBody().isActive() && availablePause && !gameOver) {
								addScore(SMALL_ROCK_SCORE);
								smallRocksCounter--;
								smallRockRef.setVisible(false);
								smallRockRef.getSmallRockBody().setActive(false);
								GameScene.this.unregisterTouchArea(smallRockRef);
								createExplosion(smallRockRef.getX(), smallRockRef.getY());
							}
						}
					});
				}
								
				return true;
			}
			
		};

		smallRock.setSmallRockDirection(velocityMultiplier * xVel, yVel);
		
		smallRocksCounter++;
				
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
		final int housesInitialHeight = 600;
		final int healthBarWidth = 100;
		final int healthBarHeight = 12;
		
		final int smallInitialX = 37;
		final int smallInitialY = 100;
		final int houseInitialX = 37;
		final int houseInitialY = 125;
		final int largeInitialX = 37;
		final int largeInitialY = 175;
		
		final Rectangle smallHouseHealthBarBackground = new Rectangle(smallInitialX, smallInitialY, healthBarWidth, healthBarHeight, vbom);
		final Rectangle smallHouseHealthBar = new Rectangle(smallInitialX, smallInitialY, healthBarWidth, healthBarHeight, vbom);
		final Rectangle houseHealthBarBackground = new Rectangle(houseInitialX, houseInitialY, healthBarWidth, healthBarHeight, vbom);
		final Rectangle houseHealthBar = new Rectangle(houseInitialX, houseInitialY, healthBarWidth, healthBarHeight, vbom);
		final Rectangle largeHouseHealthBarBackground = new Rectangle(largeInitialX, largeInitialY, 102, healthBarHeight, vbom);
		final Rectangle largeHouseHealthBar = new Rectangle(largeInitialX, largeInitialY, 102, healthBarHeight, vbom);
		
		final Sprite smallHealthBarFrame = new Sprite(smallInitialX, smallInitialY, resourcesManager.game_health_bar_frame_region, vbom);
		final Sprite healthBarFrame = new Sprite(houseInitialX, houseInitialY, resourcesManager.game_health_bar_frame_region, vbom);
		final Sprite largeHealthBarFrame = new Sprite(largeInitialX, largeInitialY, resourcesManager.game_health_bar_frame_region, vbom);
		
		houseHealthBarBackground.setColor(Color.RED_ARGB_PACKED_INT);
		houseHealthBar.setColor(Color.GREEN_ARGB_PACKED_INT);
		smallHouseHealthBarBackground.setColor(Color.RED_ARGB_PACKED_INT);
		smallHouseHealthBar.setColor(Color.GREEN_ARGB_PACKED_INT);
		largeHouseHealthBarBackground.setColor(Color.RED_ARGB_PACKED_INT);
		largeHouseHealthBar.setColor(Color.GREEN_ARGB_PACKED_INT);
		
		house = new House(1000, housesInitialHeight, vbom, camera, physicsWorld) {
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				super.onManagedUpdate(pSecondsElapsed);
				float energyWidthFactor = healthBarWidth / this.getMaxEnergy();
				if (this.isHouseDestroyed() && this.getHouseBody().isActive()) {
					this.setVisible(false);
					this.getHouseBody().setActive(false);
					explosion = new AnimatedSprite(0, 0, resourcesManager.game_explosion_region.deepCopy(), vbom);
					explosion.setPosition(this.getX(), this.getY());
					final long[] EXPLOSION_ANIMATE = new long[] {75, 75, 75, 75, 75, 150};
					explosion.animate(EXPLOSION_ANIMATE, 0, 5, false);
					GameScene.this.attachChild(explosion);
				}
				houseHealthBar.setSize(this.getHouseEnergy() * energyWidthFactor, houseHealthBar.getHeight());
				houseHealthBar.setPosition((this.getHouseEnergy() * energyWidthFactor) / 2 - 13, houseHealthBar.getY());
			}

			@Override
			public void onDie() {
				if (this.isHouseDestroyed() && smallHouse.isSmallHouseDestroyed() && largeHouse.isLargeHouseDestroyed()) {
					engine.runOnUpdateThread(new Runnable() {
						public void run() {
							gameOver();
						}
					});					
				}
				
			}
		};
		
		smallHouse = new SmallHouse(240, housesInitialHeight, vbom, camera, physicsWorld) {
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				super.onManagedUpdate(pSecondsElapsed);
				float energyWidthFactor = healthBarWidth / this.getMaxEnergy();
				if (this.isSmallHouseDestroyed() && this.getSmallHouseBody().isActive()) {
					this.setVisible(false);
					this.getSmallHouseBody().setActive(false);
					explosion = new AnimatedSprite(0, 0, resourcesManager.game_explosion_region.deepCopy(), vbom);
					explosion.setPosition(this.getX(), this.getY());
					final long[] EXPLOSION_ANIMATE = new long[] {75, 75, 75, 75, 75, 150};
					explosion.animate(EXPLOSION_ANIMATE, 0, 5, false);
					GameScene.this.attachChild(explosion);
				}
				smallHouseHealthBar.setSize(this.getSmallHouseEnergy() * energyWidthFactor, smallHouseHealthBar.getHeight());
				smallHouseHealthBar.setPosition((this.getSmallHouseEnergy() * energyWidthFactor) / 2 - 13, smallHouseHealthBar.getY());
			}

			@Override
			public void onDie() {
				if (this.isSmallHouseDestroyed() && house.isHouseDestroyed() && largeHouse.isLargeHouseDestroyed()) {
					engine.runOnUpdateThread(new Runnable() {
						public void run() {
							gameOver();
						}
					});
				}
			}
		};
		
		largeHouse = new LargeHouse(600, housesInitialHeight, vbom, camera, physicsWorld) {
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				super.onManagedUpdate(pSecondsElapsed);
				float energyWidthFactor = 102 / this.getMaxEnergy();
				if (this.isLargeHouseDestroyed() && this.getLargeHouseBody().isActive()) {
					this.setVisible(false);
					this.getLargeHouseBody().setActive(false);
					explosion = new AnimatedSprite(0, 0, resourcesManager.game_explosion_region.deepCopy(), vbom);
					explosion.setPosition(this.getX(), this.getY());
					final long[] EXPLOSION_ANIMATE = new long[] {75, 75, 75, 75, 75, 150};
					explosion.animate(EXPLOSION_ANIMATE, 0, 5, false);
					GameScene.this.attachChild(explosion);
				}
				largeHouseHealthBar.setSize(this.getLargeHouseEnergy() * energyWidthFactor, largeHouseHealthBar.getHeight());
				largeHouseHealthBar.setPosition((this.getLargeHouseEnergy() * energyWidthFactor) / 2 - 13, largeHouseHealthBar.getY());
			}

			@Override
			public void onDie() {
				if (this.isLargeHouseDestroyed() && house.isHouseDestroyed() && smallHouse.isSmallHouseDestroyed()) {
					engine.runOnUpdateThread(new Runnable() {
						public void run() {
							gameOver();
						}
					});
				}
			}
		};
		
		house.attachChild(houseHealthBarBackground);
		house.attachChild(houseHealthBar);
		house.attachChild(healthBarFrame);
		smallHouse.attachChild(smallHouseHealthBarBackground);
		smallHouse.attachChild(smallHouseHealthBar);
		smallHouse.attachChild(smallHealthBarFrame);
		largeHouse.attachChild(largeHouseHealthBarBackground);
		largeHouse.attachChild(largeHouseHealthBar);
		largeHouse.attachChild(largeHealthBarFrame);
		GameScene.this.attachChild(house);
		GameScene.this.attachChild(smallHouse);
		GameScene.this.attachChild(largeHouse);
	}
	
	/*
	 * Creates floor on level generation
	 */
	private void createFloor() {
		Random rand = new Random();
		int elevation;
		int[] floor_positions = {80, 240, 400, 560, 720, 880, 1040, 1200};
		Floor[] floor = new Floor[8];
 		
		Sprite base_floor = new Sprite(screenWidth/2, 0, resourcesManager.game_base_floor_region, vbom);
		Body base_floor_body = PhysicsFactory.createBoxBody(physicsWorld, base_floor, BodyType.StaticBody, PhysicsFactory.createFixtureDef(0, 0, 0));
		base_floor_body.setUserData("base_floor");
		base_floor.setCullingEnabled(true);
		GameScene.this.attachChild(base_floor);
		
		for (int i = 0; i < 8; i++) {
			elevation = rand.nextInt(5) + 1;
			floor[i] = new Floor(floor_positions[i], 200, vbom, camera, physicsWorld);
			floor[i].setCullingEnabled(true);
			GameScene.this.attachChild(floor[i]);
			if (elevation == 2) {
				Earth earth = new Earth(floor_positions[i], 50, vbom, camera, physicsWorld);
				GameScene.this.attachChild(earth);
			}
		}
		
	}
	
	private void createDecoration() {
		//n = rand.nextInt(max - min + 1) + min;
		Random rand = new Random();
		int x = rand.nextInt(1281) + 1;
		int y = rand.nextInt(321) + 400;
		int cloudSpeed = -(rand.nextInt(11) + 35);
		int farCloudSpeed = -(rand.nextInt(11) + 10);
		Tree[] trees = new Tree[6];
		int[] trees_positions = {80, 350, 450, 680, 880, 1200};
		ITextureRegion region = resourcesManager.game_trees_1_region;
		
		for (int i = 0; i < 6; i++) {
			switch (i) {
			case 0:
				region = resourcesManager.game_trees_1_region;
				break;
			case 1:
				region = resourcesManager.game_trees_2_region;
				break;
			case 2:
				region = resourcesManager.game_trees_3_region;
				break;
			case 3:
				region = resourcesManager.game_trees_4_region;
				break;
			case 4:
				region = resourcesManager.game_trees_5_region;
				break;
			case 5:
				region = resourcesManager.game_trees_6_region;
				break;
			default:
				break;
			}
			trees[i] = new Tree(trees_positions[i], 600, vbom, camera, physicsWorld, region);
			GameScene.this.attachChild(trees[i]);
		}
		
		Sprite cloud1 = new Sprite(x, y, resourcesManager.game_cloud_1_region, vbom) {
			protected void onManagedUpdate(float pSecondsElapsed) {
				super.onManagedUpdate(pSecondsElapsed);
				if (this.getX() < -200) {
					this.setPosition(1480, this.getY());
				}
			};
		};
		PhysicsHandler handler = new PhysicsHandler(cloud1);
		cloud1.registerUpdateHandler(handler);
		handler.setVelocity(cloudSpeed, 0);
		
		x = rand.nextInt(1281) + 1;
		y = rand.nextInt(321) + 400;
		cloudSpeed = -(rand.nextInt(11) + 35);
		
		Sprite cloud2 = new Sprite(x, y, resourcesManager.game_cloud_2_region, vbom) {
			protected void onManagedUpdate(float pSecondsElapsed) {
				super.onManagedUpdate(pSecondsElapsed);
				if (this.getX() < -200) {
					this.setPosition(1480, this.getY());
				}
			};
		};
		PhysicsHandler handler2 = new PhysicsHandler(cloud2);
		cloud1.registerUpdateHandler(handler2);
		handler2.setVelocity(cloudSpeed, 0);
		
		x = rand.nextInt(1281) + 1;
		y = rand.nextInt(321) + 400;
		
		Sprite farCloud1 = new Sprite(x, y, resourcesManager.game_far_cloud_1_region, vbom) {
			protected void onManagedUpdate(float pSecondsElapsed) {
				super.onManagedUpdate(pSecondsElapsed);
				if (this.getX() < -200) {
					this.setPosition(1480, this.getY());
				}
			};
		};
		PhysicsHandler handler4 = new PhysicsHandler(farCloud1);
		farCloud1.registerUpdateHandler(handler4);
		handler4.setVelocity(farCloudSpeed, 0);
		
		x = rand.nextInt(1281) + 1;
		y = rand.nextInt(321) + 400;
		farCloudSpeed = -(rand.nextInt(11) + 10);
		
		Sprite farCloud2 = new Sprite(x, y, resourcesManager.game_far_cloud_2_region, vbom) {
			protected void onManagedUpdate(float pSecondsElapsed) {
				super.onManagedUpdate(pSecondsElapsed);
				if (this.getX() < -200) {
					this.setPosition(1480, this.getY());
				}
			};
		};
		PhysicsHandler handler5 = new PhysicsHandler(farCloud2);
		farCloud2.registerUpdateHandler(handler5);
		handler5.setVelocity(farCloudSpeed, 0);
		
		GameScene.this.attachChild(cloud1);
		GameScene.this.attachChild(cloud2);
		GameScene.this.attachChild(farCloud1);
		GameScene.this.attachChild(farCloud2);
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
	
	private void createWindows() {
		gameOverWindow = new Sprite(0, 0, resourcesManager.game_over_window_region, vbom);
		pauseWindow = new Sprite(0, 0, resourcesManager.game_pause_window_region, vbom);
		fade = new Rectangle(screenWidth/2, screenHeight/2, screenWidth, screenHeight, vbom);
		fade.setColor(Color.BLACK);
		fade.setAlpha(0.75f);
	}
	
	private void createHud() {
		gameHud = new HUD();	
		
		scoreText = new Text(50, 650, resourcesManager.scoreFont, "Score: 0123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
		countdownText = new Text(screenWidth/2, screenHeight/2, resourcesManager.countdownFont, "321Protect!", new TextOptions(HorizontalAlign.CENTER), vbom);
		pauseButton = new Sprite(1230, 670, resourcesManager.game_pause_button_region, vbom) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				GameScene.this.unregisterTouchArea(pauseButton);
				if (availablePause) {
					displayPauseWindow();
				}
				return true;
			}
		};
		
		scoreText.setAnchorCenter(0, 0);
		countdownText.setAnchorCenter(0, 0);
		
		scoreText.setText("Score: " + score);
		countdownText.setText("3");
		
		//scoreText.setColor(0.596f, 0.596f, 0.6f);
		scoreText.setColor(Color.BLACK_ARGB_PACKED_INT);
		countdownText.setColor(Color.RED_ARGB_PACKED_INT);

		gameHud.attachChild(scoreText);
		gameHud.attachChild(countdownText);
		gameHud.attachChild(pauseButton);
		
		GameScene.this.registerTouchArea(pauseButton);

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
	
	private void displayPauseWindow() {
		availablePause = false;	
		GameScene.this.setIgnoreUpdate(true);		
		
		pauseWindow.setPosition(camera.getCenterX(), camera.getCenterY());
		
	    retryButton = new Sprite(270, 50, resourcesManager.game_retry_button_region, vbom){
	    	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
	    		if (pSceneTouchEvent.isActionDown()) {
	    			gameHud.dispose();
					gameHud.setVisible(false);
					detachChild(gameHud);
					myGarbageCollection();
					SceneManager.getInstance().loadGameScene(engine, GameScene.this);
				}
	    		return true;
	    	};
	    };
	    quitButton = new Sprite(0, 50, resourcesManager.game_quit_button_region, vbom){
	    	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
	    		if (pSceneTouchEvent.isActionDown()) {
	    			gameHud.dispose();
					gameHud.setVisible(false);
					detachChild(gameHud);
					myGarbageCollection();
					SceneManager.getInstance().loadMenuScene(engine, GameScene.this);
	    		}
	    		return true;
	    	};
	    };
	    resumeButton = new Sprite(550, 50, resourcesManager.game_resume_button_region, vbom){
	    	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
	    		if (pSceneTouchEvent.isActionDown()) {
	    			availablePause = true;
					gameHud.setVisible(true);
					GameScene.this.detachChild(fade);
					GameScene.this.detachChild(pauseWindow);
	    			GameScene.this.setIgnoreUpdate(false);
	    			GameScene.this.unregisterTouchArea(this);
	    		    GameScene.this.unregisterTouchArea(retryButton);
	    		    GameScene.this.unregisterTouchArea(quitButton);
	    		    GameScene.this.registerTouchArea(pauseButton);
	    		}
	    		return true;
	    	};
	    };
	    GameScene.this.registerTouchArea(resumeButton);
	    GameScene.this.registerTouchArea(retryButton);
	    GameScene.this.registerTouchArea(quitButton);
	    pauseWindow.attachChild(resumeButton);
	    pauseWindow.attachChild(retryButton);	    
	    pauseWindow.attachChild(quitButton);
		
		GameScene.this.attachChild(fade);
		GameScene.this.attachChild(pauseWindow);
		
		gameHud.setVisible(false);
	}
	
	private void gameOver() {
		gameOverWindow = new Sprite(0, 0, resourcesManager.game_over_window_region, vbom);
		Rectangle fade = new Rectangle(screenWidth/2, screenHeight/2, screenWidth, screenHeight, vbom);
		
		fade.setColor(Color.BLACK);
		fade.setAlpha(0.35f);
		
		availablePause = false;
		gameOver = true;
		
		GameScene.this.setIgnoreUpdate(true);
		gameOverWindow.setPosition(camera.getCenterX(), camera.getCenterY());
		
		finalScoreText = new Text(275, 256, resourcesManager.finalScoreFont, " Yourscore:123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
		
		finalScoreText.setColor(Color.RED_ARGB_PACKED_INT);

		finalScoreText.setText("Your score: " + score);
		
		retryButton = new Sprite(450, 50, resourcesManager.game_retry_button_region, vbom){
	    	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
	    		if (pSceneTouchEvent.isActionDown()) {
	    			gameHud.dispose();
					gameHud.setVisible(false);
					detachChild(gameHud);
					myGarbageCollection();
					SceneManager.getInstance().loadGameScene(engine, GameScene.this);
				}
	    		return true;
	    	};
	    };
	    quitButton = new Sprite(150, 50, resourcesManager.game_quit_button_region, vbom){
	    	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
	    		if (pSceneTouchEvent.isActionDown()) {
	    			gameHud.dispose();
					gameHud.setVisible(false);
					detachChild(gameHud);
					myGarbageCollection();
					SceneManager.getInstance().loadMenuScene(engine, GameScene.this);
	    		}
	    		return true;
	    	};
	    };
		
		gameHud.setVisible(false);
		GameScene.this.attachChild(fade);
		GameScene.this.attachChild(gameOverWindow);
		GameScene.this.registerTouchArea(retryButton);
	    GameScene.this.registerTouchArea(quitButton);
		gameOverWindow.attachChild(retryButton);
		gameOverWindow.attachChild(quitButton);
		gameOverWindow.attachChild(finalScoreText);
	}
	
	private void createExplosion(float x, float y) {
		explosion = new AnimatedSprite(x, y, resourcesManager.game_explosion_region.deepCopy(), vbom);
		final long[] EXPLOSION_ANIMATE = new long[] {75, 75, 75, 75, 75, 150};
		explosion.animate(EXPLOSION_ANIMATE, 0, 5, false);
		GameScene.this.attachChild(explosion);
	}
	
	private void createSmallExplosion(float x, float y) {
		small_explosion = new AnimatedSprite(x, y, resourcesManager.game_small_explosion_region.deepCopy(), vbom);
		final long[] EXPLOSION_ANIMATE = new long[] {75, 75, 75, 75, 75, 150};
		small_explosion.animate(EXPLOSION_ANIMATE, 0, 5, false);
		GameScene.this.attachChild(small_explosion);
	}
	
	private void createBombBox() {
		Random rand = new Random();
		final int x = rand.nextInt(ROCK_MAX_RANDOM_X) + ROCK_MIN_RANDOM_X;
		Bomb bomb = new Bomb(x, ROCK_INITIAL_Y, vbom, camera, physicsWorld) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionDown()) {
					final Bomb ref = this;
					engine.runOnUpdateThread(new Runnable() {
						@Override
						public void run() {
							if (ref.getBombBody().isActive() && availablePause && !gameOver) {
								ref.setVisible(false);
								ref.getBombBody().setActive(false);
								GameScene.this.unregisterTouchArea(ref);
								registerEntityModifier(new DelayModifier(0.1f, new IEntityModifierListener() {
									
									@Override
									public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
										destroyAllEnemies = true;										
									}
									
									@Override
									public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
										destroyAllEnemies = false;
										
									}
								}));
							}
						}
					});
				}
				
				return true;
			}
		};
		GameScene.this.attachChild(bomb);
		GameScene.this.registerTouchArea(bomb);
	}
	
	private void createRepairBox() {
		Random rand = new Random();
		final int x = rand.nextInt(ROCK_MAX_RANDOM_X) + ROCK_MIN_RANDOM_X;
		Repair repair = new Repair(x, ROCK_INITIAL_Y, vbom, camera, physicsWorld) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionDown()) {
					final Repair ref = this;
					engine.runOnUpdateThread(new Runnable() {
						@Override
						public void run() {
							if (ref.getRepairBody().isActive() && availablePause && !gameOver) {
								ref.setVisible(false);
								ref.getRepairBody().setActive(false);
								GameScene.this.unregisterTouchArea(ref);
								if (!smallHouse.isSmallHouseDestroyed()) {
									smallHouse.repairCompleteSmallHouse();
								}
								if (!house.isHouseDestroyed()) {
									house.repairCompleteHouse();
								}
								if (!largeHouse.isLargeHouseDestroyed()) {
									largeHouse.repairCompleteLargeHouse();
								}
							}
						}
					});
				}
				
				return true;
			}
		};
		GameScene.this.attachChild(repair);
		GameScene.this.registerTouchArea(repair);
	}
	
	private void createShieldBox() {
		Random rand = new Random();
		final int x = rand.nextInt(ROCK_MAX_RANDOM_X) + ROCK_MIN_RANDOM_X;
		Shield shield = new Shield(x, ROCK_INITIAL_Y, vbom, camera, physicsWorld) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionDown()) {
					final Shield ref = this;
					engine.runOnUpdateThread(new Runnable() {
						@Override
						public void run() {
							if (ref.getShieldBody().isActive() && availablePause && !gameOver) {
								ref.setVisible(false);
								ref.getShieldBody().setActive(false);
								GameScene.this.unregisterTouchArea(ref);
								registerEntityModifier(new DelayModifier(SHIELD_DURATION, new IEntityModifierListener() {
									
									@Override
									public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
										dome.setPosition(screenWidth/2, 100);
										shieldBarBackground.setWidth(400);
										shieldBar.setWidth(400);
										shieldBarBackground.setVisible(true);
										shieldBar.setVisible(true);
										shieldBarFrame.setVisible(true);
										shieldBarLogo.setVisible(true);
										domeActivated = true;
									}
									
									@Override
									public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
										dome.setPosition(-1500, -1500);
										shieldBarBackground.setVisible(false);
										shieldBar.setVisible(false);
										shieldBarFrame.setVisible(false);
										shieldBarLogo.setVisible(false);
										domeActivated = true;
									}
								}));
							}
						}
					});
				}
				
				return true;
			}
		};
		GameScene.this.attachChild(shield);
		GameScene.this.registerTouchArea(shield);
	}
	
	private void createDome() {
		float shieldBarWidth = 400;
		float shieldBarHeight = 25;
		
		shieldBarBackground = new Rectangle(screenWidth/2 + screenWidth/4 - 250, 665, shieldBarWidth, shieldBarHeight, vbom);
		shieldBar = new Rectangle(screenWidth/2 + screenWidth/4 - 250, 665, shieldBarWidth, shieldBarHeight, vbom);
		shieldBarFrame = new Sprite(screenWidth/2 + screenWidth/4 - 250, 665, resourcesManager.game_shield_bar_frame_region, vbom);
		shieldBarLogo = new Sprite(screenWidth/2 - 170, 670, resourcesManager.game_shield_bar_logo_region, vbom);
		
		dome = new Sprite(-1500, 1500, resourcesManager.game_dome_region, vbom);
		
		shieldBarBackground.setColor(Color.WHITE);
		shieldBar.setColor(0.259f, 0.541f, 0.78f);
		
		shieldBarBackground.setVisible(false);
		shieldBar.setVisible(false);
		shieldBarFrame.setVisible(false);
		shieldBarLogo.setVisible(false);
		
		gameHud.attachChild(shieldBarBackground);
		gameHud.attachChild(shieldBar);
		gameHud.attachChild(shieldBarFrame);
		gameHud.attachChild(shieldBarLogo);
		GameScene.this.attachChild(dome);
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
							if (!house.isHouseDestroyed()) {
								createSmallExplosion(house.getX(), house.getY());
							}							
							regenerateRocks(x1.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("house") && x2.getBody().getUserData().equals("large_rock")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							house.destroyHouse();
							if (!house.isHouseDestroyed()) {
								createSmallExplosion(house.getX(), house.getY());
							}							
							regenerateRocks(x2.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("large_rock") && x2.getBody().getUserData().equals("small_house")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							smallHouse.destroySmallHouse();
							if (!smallHouse.isSmallHouseDestroyed()) {
								createSmallExplosion(smallHouse.getX(), smallHouse.getY());
							}
							regenerateRocks(x1.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("small_house") && x2.getBody().getUserData().equals("large_rock")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							smallHouse.destroySmallHouse();
							if (!smallHouse.isSmallHouseDestroyed()) {
								createSmallExplosion(smallHouse.getX(), smallHouse.getY());
							}
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
							if (!largeHouse.isLargeHouseDestroyed()) {
								createSmallExplosion(largeHouse.getX(), largeHouse.getY());
							}
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
							if (!largeHouse.isLargeHouseDestroyed()) {
								createSmallExplosion(largeHouse.getX(), largeHouse.getY());
							}
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
							if (!house.isHouseDestroyed()) {
								createSmallExplosion(house.getX(), house.getY());
							}
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
							if (!house.isHouseDestroyed()) {
								createSmallExplosion(house.getX(), house.getY());
							}
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
							if (!smallHouse.isSmallHouseDestroyed()) {
								createSmallExplosion(smallHouse.getX(), smallHouse.getY());
							}
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
							if (!largeHouse.isLargeHouseDestroyed()) {
								createSmallExplosion(largeHouse.getX(), largeHouse.getY());
							}
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
							if (!largeHouse.isLargeHouseDestroyed()) {
								createSmallExplosion(largeHouse.getX(), largeHouse.getY());
							}
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
							if (!smallHouse.isSmallHouseDestroyed()) {
								createSmallExplosion(smallHouse.getX(), smallHouse.getY());
							}
							regenerateRocks(x2.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("small_rock") && x2.getBody().getUserData().equals("house")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							house.damageHouse();
							if (!house.isHouseDestroyed()) {
								createSmallExplosion(house.getX(), house.getY());
							}
							regenerateRocks(x1.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("house") && x2.getBody().getUserData().equals("small_rock")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							house.damageHouse();
							if (!house.isHouseDestroyed()) {
								createSmallExplosion(house.getX(), house.getY());
							}
							regenerateRocks(x2.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("small_rock") && x2.getBody().getUserData().equals("small_house")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							smallHouse.damageSmallHouse();
							if (!smallHouse.isSmallHouseDestroyed()) {
								createSmallExplosion(smallHouse.getX(), smallHouse.getY());
							}
							regenerateRocks(x1.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("small_house") && x2.getBody().getUserData().equals("small_rock")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							smallHouse.damageSmallHouse();
							if (!smallHouse.isSmallHouseDestroyed()) {
								createSmallExplosion(smallHouse.getX(), smallHouse.getY());
							}
							regenerateRocks(x2.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("small_rock") && x2.getBody().getUserData().equals("large_house")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							largeHouse.damageLargeHouse();
							if (!largeHouse.isLargeHouseDestroyed()) {
								createSmallExplosion(largeHouse.getX(), largeHouse.getY());
							}
							regenerateRocks(x1.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("large_house") && x2.getBody().getUserData().equals("small_rock")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							largeHouse.damageLargeHouse();
							if (!largeHouse.isLargeHouseDestroyed()) {
								createSmallExplosion(largeHouse.getX(), largeHouse.getY());
							}
							regenerateRocks(x2.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("shot") && x2.getBody().getUserData().equals("large_house")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							largeHouse.damageLargeHouse();
							if (!largeHouse.isLargeHouseDestroyed()) {
								createSmallExplosion(largeHouse.getX(), largeHouse.getY());
							}
							x1.getBody().setTransform(2000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 1000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, x1.getBody().getAngle());
							x1.getBody().setActive(false);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("large_house") && x2.getBody().getUserData().equals("shot")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							largeHouse.damageLargeHouse();
							if (!largeHouse.isLargeHouseDestroyed()) {
								createSmallExplosion(largeHouse.getX(), largeHouse.getY());
							}
							x2.getBody().setTransform(2000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 1000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, x2.getBody().getAngle());
							x2.getBody().setActive(false);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("shot") && x2.getBody().getUserData().equals("house")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							house.damageHouse();
							if (!house.isHouseDestroyed()) {
								createSmallExplosion(house.getX(), house.getY());
							}
							x1.getBody().setTransform(2000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 1000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, x1.getBody().getAngle());
							x1.getBody().setActive(false);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("house") && x2.getBody().getUserData().equals("shot")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							house.damageHouse();
							if (!house.isHouseDestroyed()) {
								createSmallExplosion(house.getX(), house.getY());
							}
							x2.getBody().setTransform(2000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 1000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, x2.getBody().getAngle());
							x2.getBody().setActive(false);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("shot") && x2.getBody().getUserData().equals("small_house")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							smallHouse.damageSmallHouse();
							if (!smallHouse.isSmallHouseDestroyed()) {
								createSmallExplosion(smallHouse.getX(), smallHouse.getY());
							}
							x1.getBody().setTransform(2000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 1000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, x1.getBody().getAngle());
							x1.getBody().setActive(false);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("small_house") && x2.getBody().getUserData().equals("shot")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							smallHouse.damageSmallHouse();
							if (!smallHouse.isSmallHouseDestroyed()) {
								createSmallExplosion(smallHouse.getX(), smallHouse.getY());
							}
							x2.getBody().setTransform(2000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 1000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, x2.getBody().getAngle());
							x2.getBody().setActive(false);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("shot") && x2.getBody().getUserData().equals("floor")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							x1.getBody().setTransform(2000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 1000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, x1.getBody().getAngle());
							x1.getBody().setActive(false);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("floor") && x2.getBody().getUserData().equals("shot")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							x2.getBody().setTransform(2000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 1000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, x2.getBody().getAngle());
							x2.getBody().setActive(false);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("floor") && x2.getBody().getUserData().equals("satelite")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							x2.getBody().setTransform(2000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 1000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, x2.getBody().getAngle());
							x2.getBody().setActive(false);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("satelite") && x2.getBody().getUserData().equals("floor")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							x1.getBody().setTransform(2000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 1000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, x1.getBody().getAngle());
							x1.getBody().setActive(false);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("small_house") && x2.getBody().getUserData().equals("satelite")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							smallHouse.damageSmallHouse();
							if (!smallHouse.isSmallHouseDestroyed()) {
								createSmallExplosion(smallHouse.getX(), smallHouse.getY());
							}
							x2.getBody().setTransform(2000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 1000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, x2.getBody().getAngle());
							x2.getBody().setActive(false);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("satelite") && x2.getBody().getUserData().equals("small_house")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							smallHouse.damageSmallHouse();
							if (!smallHouse.isSmallHouseDestroyed()) {
								createSmallExplosion(smallHouse.getX(), smallHouse.getY());
							}
							x1.getBody().setTransform(2000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 1000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, x1.getBody().getAngle());
							x1.getBody().setActive(false);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("house") && x2.getBody().getUserData().equals("satelite")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							house.damageHouse();
							if (!house.isHouseDestroyed()) {
								createSmallExplosion(house.getX(), house.getY());
							}
							x2.getBody().setTransform(2000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 1000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, x2.getBody().getAngle());
							x2.getBody().setActive(false);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("satelite") && x2.getBody().getUserData().equals("house")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							house.damageHouse();
							if (!house.isHouseDestroyed()) {
								createSmallExplosion(house.getX(), house.getY());
							}
							x1.getBody().setTransform(2000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 1000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, x1.getBody().getAngle());
							x1.getBody().setActive(false);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("large_house") && x2.getBody().getUserData().equals("satelite")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							largeHouse.damageLargeHouse();
							if (!largeHouse.isLargeHouseDestroyed()) {
								createSmallExplosion(largeHouse.getX(), largeHouse.getY());
							}
							x2.getBody().setTransform(2000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 1000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, x2.getBody().getAngle());
							x2.getBody().setActive(false);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("satelite") && x2.getBody().getUserData().equals("large_house")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							largeHouse.damageLargeHouse();
							if (!largeHouse.isLargeHouseDestroyed()) {
								createSmallExplosion(largeHouse.getX(), largeHouse.getY());
							}
							x1.getBody().setTransform(2000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 1000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, x1.getBody().getAngle());
							x1.getBody().setActive(false);
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
				
				if (x1.getBody().getUserData().equals("floor") && x2.getBody().getUserData().equals("earth")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							x1.getBody().setType(BodyType.StaticBody);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("earth") && x2.getBody().getUserData().equals("floor")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							x2.getBody().setType(BodyType.StaticBody);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("floor") && x2.getBody().getUserData().equals("tree")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							x2.getBody().setType(BodyType.StaticBody);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("tree") && x2.getBody().getUserData().equals("floor")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							x1.getBody().setType(BodyType.StaticBody);
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
		engine.runOnUpdateThread(new Runnable() {
			
			@Override
			public void run() {
				if (availablePause) {
					displayPauseWindow();
				} else if (!gameOver){
					availablePause = true;
					gameHud.setVisible(true);
					GameScene.this.detachChild(fade);
					GameScene.this.detachChild(pauseWindow);
	    			GameScene.this.setIgnoreUpdate(false);
	    			GameScene.this.unregisterTouchArea(resumeButton);
	    		    GameScene.this.unregisterTouchArea(retryButton);
	    		    GameScene.this.unregisterTouchArea(quitButton);
	    		    GameScene.this.registerTouchArea(pauseButton);
				} else {
					gameHud.dispose();
					gameHud.setVisible(false);
					detachChild(gameHud);
					myGarbageCollection();
					SceneManager.getInstance().loadMenuScene(engine, GameScene.this);
				}
			}
		});
	}
	
}
