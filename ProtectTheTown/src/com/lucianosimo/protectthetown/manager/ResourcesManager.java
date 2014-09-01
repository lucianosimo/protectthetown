package com.lucianosimo.protectthetown.manager;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.color.Color;
import org.andengine.util.debug.Debug;

import com.lucianosimo.protectthetown.GameActivity;

public class ResourcesManager {

	private static final ResourcesManager INSTANCE = new ResourcesManager();
	
	public Engine engine;
	public BoundCamera camera;
	public GameActivity activity;
	public VertexBufferObjectManager vbom;
	
	//Splash items
	public ITextureRegion splash_region;
	private BitmapTextureAtlas splashTextureAtlas;
	
	//Menu fonts
	public Font highScoreFont;
	
	//Menu items
	public ITextureRegion loading_background_region;
	public ITextureRegion menu_background_region;
	public ITextureRegion menu_play_button_region;
	public ITextureRegion menu_play_button_background_region;
	public ITextureRegion menu_rateus_button_region;
	public ITextureRegion menu_rateus_button_background_region;
	public ITextureRegion menu_quit_button_region;
	
	private BuildableBitmapTextureAtlas loadingBackgroundTextureAtlas;
	private BuildableBitmapTextureAtlas menuTextureAtlas;
	private BuildableBitmapTextureAtlas menuBackgroundTextureAtlas;
	
	
	//Game fonts
	public Font scoreFont;
	public Font countdownFont;
	public Font gameOverFont;
	public Font finalScoreFont;
	public Font pauseFont;
	
	//Game HUD
	
	//Objects
	public ITextureRegion game_small_rock_region;
	public ITextureRegion game_rock_region;
	public ITextureRegion game_large_rock_region;
	
	public ITextureRegion game_satelite_region;
	
	public ITextureRegion game_cloud_1_region;
	public ITextureRegion game_cloud_2_region;
	public ITextureRegion game_far_cloud_1_region;
	public ITextureRegion game_far_cloud_2_region;
	
	public ITextureRegion game_trees_1_region;
	public ITextureRegion game_trees_2_region;
	public ITextureRegion game_trees_3_region;
	public ITextureRegion game_trees_4_region;
	public ITextureRegion game_trees_5_region;
	public ITextureRegion game_trees_6_region;
	
	public ITextureRegion game_ufo_1_region;
	public ITextureRegion game_ufo_2_region;
	public ITextureRegion game_ufo_3_region;
	
	public ITextureRegion game_ufo_shot_region;
	
	public ITextureRegion game_bomb_region;
	public ITextureRegion game_repair_region;
	public ITextureRegion game_shield_region;
	
	public ITextureRegion game_dome_region;
		
	public ITextureRegion game_small_house_region;
	public ITextureRegion game_house_region;
	public ITextureRegion game_large_house_region;
	
	public ITextureRegion game_health_bar_frame_region;
	public ITextureRegion game_shield_bar_frame_region;
	public ITextureRegion game_shield_bar_logo_region;
	
	//Platforms
	public ITextureRegion game_floor_region;
	public ITextureRegion game_floor_back_region;
	public ITextureRegion game_earth_region;
	public ITextureRegion game_base_floor_region;
	
	//Backgrounds
	public ITextureRegion game_background_region;
	
	//Animated
	public ITiledTextureRegion game_explosion_region;
	public ITiledTextureRegion game_small_explosion_region;
	
	//Countdown
	public ITextureRegion game_countdown_frame_1_region;
	public ITextureRegion game_countdown_frame_2_region;
	public ITextureRegion game_countdown_frame_3_region;
	public ITextureRegion game_countdown_frame_4_region;
	
	//Windows
	public ITextureRegion game_over_window_region;
	public ITextureRegion game_pause_window_region;
	public ITextureRegion game_new_record_region;
	
	//Buttons
	public ITextureRegion game_resume_button_region;
	public ITextureRegion game_retry_button_region;
	public ITextureRegion game_quit_button_region;
	public ITextureRegion game_pause_button_region;
	public ITextureRegion game_submit_button_region;
	public ITextureRegion game_twitter_button_region;
	
	//Game Textures
	private BuildableBitmapTextureAtlas gameTextureAtlas;
	private BuildableBitmapTextureAtlas gameWindowsTextureAtlas;
	private BuildableBitmapTextureAtlas gameAnimatedTextureAtlas;
	private BuildableBitmapTextureAtlas gameBackgroundTextureAtlas;
	
	//Splash Methods
	public void loadSplashScreen() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		splashTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 1280, 720, TextureOptions.BILINEAR);
		splash_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas, activity, "splash.png", 0, 0);
		splashTextureAtlas.load();
	}
	
	public void unloadSplashScreen() {
		splashTextureAtlas.unload();
		splash_region = null;
	}
	
	//Menu methods
	public void loadMenuResources() {
		//loadLoadingGraphics();
		loadMenuGraphics();
		loadMenuFonts();
		//loadMenuAudio();		
	}
	
	public void unloadMenuResources() {
		unloadMenuTextures();
		unloadMenuFonts();
		//unloadMenuAudio();
	}
	
	/*private void loadLoadingGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
		backgroundLoadingTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 480, 854, TextureOptions.BILINEAR);
		
		loading_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(backgroundLoadingTextureAtlas, activity, "loading_background.png");
		
		try {
			this.backgroundLoadingTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.backgroundLoadingTextureAtlas.load();
		} catch (final TextureAtlasBuilderException e) {
			org.andengine.util.debug.Debug.e(e);
		}
	}*/

	private void loadMenuGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
		menuTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1000, 1000, TextureOptions.BILINEAR);
		menuBackgroundTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1280, 720, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		loadingBackgroundTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1280, 720, TextureOptions.BILINEAR);
		
		loading_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(loadingBackgroundTextureAtlas, activity, "loading_background.png");
		menu_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuBackgroundTextureAtlas, activity, "menu_background.png");
		menu_play_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "menu_play_button.png");
		menu_play_button_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "menu_play_button_background.png");
		menu_rateus_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "menu_rateus_button.png");
		menu_rateus_button_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "menu_rateus_button_background.png");
		menu_quit_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "menu_quit_button.png");
		
		try {
			this.menuTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.menuBackgroundTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.loadingBackgroundTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.menuTextureAtlas.load();
			this.loadingBackgroundTextureAtlas.load();
			this.menuBackgroundTextureAtlas.load();
		} catch (final TextureAtlasBuilderException e) {
			org.andengine.util.debug.Debug.e(e);
		}
	}
	
	private void loadMenuFonts() {
		FontFactory.setAssetBasePath("fonts/menu/");
		final ITexture highScoreTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		highScoreFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), highScoreTexture, activity.getAssets(), "font.ttf", 40, true, Color.WHITE_ARGB_PACKED_INT, 1f, Color.WHITE_ARGB_PACKED_INT);
		highScoreFont.load();
	}
	
	/*private void loadMenuAudio() {
	}
	
	private void loadMenuFonts() {
		FontFactory.setAssetBasePath("font/menu/");
		final ITexture loadingTexture = new BitmapTextureAtlas(activity.getTextureManager(), 64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		loadingFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), loadingTexture, activity.getAssets(), "simple.ttf", 35, true, Color.BLACK_ARGB_PACKED_INT, 0.1f, Color.BLACK_ARGB_PACKED_INT);
		loadingFont.load();
	}*/
	
	private void unloadMenuTextures() {
		this.menuTextureAtlas.unload();
		this.menuBackgroundTextureAtlas.unload();
	}
	
	private void unloadMenuFonts() {
		
	}
	
	/*private void unloadMenuAudio() {
		
	}*/
	
	//Game Methods
	public void loadGameResources() {
		loadGameGraphics();
		//loadGameAudio();
		loadGameFonts();
	}
	
	public void unloadGameResources() {
		unloadGameTextures();
		unloadGameFonts();	
		//unloadGameAudio();
	}
	
	private void loadGameGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
		
		gameTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 2000, 2000, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		gameWindowsTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 2000, 2000, TextureOptions.BILINEAR);
		gameAnimatedTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1500, 1500, TextureOptions.BILINEAR);
		gameBackgroundTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1280, 720, TextureOptions.BILINEAR);
		
		game_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameBackgroundTextureAtlas, activity, "game_background.png");
		
		game_small_rock_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_small_rock.png");
		game_rock_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_rock.png");
		game_large_rock_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_large_rock.png");
		
		game_satelite_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_satelite.png");
		
		game_far_cloud_1_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_far_cloud_1.png");
		game_far_cloud_2_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_far_cloud_2.png");		
		game_cloud_1_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_cloud_1.png");
		game_cloud_2_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_cloud_2.png");
		
		game_trees_1_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_trees_1.png");
		game_trees_2_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_trees_2.png");
		game_trees_3_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_trees_3.png");
		game_trees_4_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_trees_4.png");
		game_trees_5_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_trees_5.png");
		game_trees_6_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_trees_6.png");
		
		game_ufo_1_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_ufo_1.png");
		game_ufo_2_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_ufo_2.png");
		game_ufo_3_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_ufo_3.png");
		
		game_ufo_shot_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_ufo_shot.png");
		
		game_small_house_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_small_house.png");
		game_house_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_house.png");
		game_large_house_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_large_house.png");
		
		game_health_bar_frame_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_health_bar_frame.png");
		game_shield_bar_frame_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_shield_bar_frame.png");
		game_shield_bar_logo_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_shield_bar_logo.png");
		
		game_bomb_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_bomb.png");
		game_repair_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_repair.png");
		game_shield_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_shield.png");
		
		game_dome_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_dome.png");
		
		game_base_floor_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_base_floor.png");
		game_earth_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_earth.png");
		game_floor_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_floor.png");
		game_floor_back_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_floor_back.png");
		
		game_over_window_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameWindowsTextureAtlas, activity, "game_over_window.png");
		game_pause_window_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameWindowsTextureAtlas, activity, "game_pause_window.png");
		game_new_record_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameWindowsTextureAtlas, activity, "game_new_record.png");
		
		game_resume_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameWindowsTextureAtlas, activity, "game_resume_button.png");
		game_retry_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameWindowsTextureAtlas, activity, "game_retry_button.png");
		game_quit_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameWindowsTextureAtlas, activity, "game_quit_button.png");
		game_pause_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameWindowsTextureAtlas, activity, "game_pause_button.png");
		game_submit_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameWindowsTextureAtlas, activity, "game_submit_button.png");
		game_twitter_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameWindowsTextureAtlas, activity, "game_twitter_button.png");
		
		game_countdown_frame_1_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameWindowsTextureAtlas, activity, "game_countdown_frame_1.png");
		game_countdown_frame_2_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameWindowsTextureAtlas, activity, "game_countdown_frame_2.png");
		game_countdown_frame_3_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameWindowsTextureAtlas, activity, "game_countdown_frame_3.png");
		game_countdown_frame_4_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameWindowsTextureAtlas, activity, "game_countdown_frame_4.png");
		
		game_explosion_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameAnimatedTextureAtlas, activity, "game_explosion.png", 6, 1);
		game_small_explosion_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameAnimatedTextureAtlas, activity, "game_small_explosion.png", 6, 1);
		
		try {
			this.gameTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.gameWindowsTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.gameAnimatedTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.gameBackgroundTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.gameTextureAtlas.load();
			this.gameWindowsTextureAtlas.load();
			this.gameAnimatedTextureAtlas.load();
			this.gameBackgroundTextureAtlas.load();
		} catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}
	}

	/*private void loadGameAudio() {
		MusicFactory.setAssetBasePath("music/");
		SoundFactory.setAssetBasePath("sound/");
		try {
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void unloadGameAudio() {
		System.gc();
	}*/
	
	private void loadGameFonts() {
		FontFactory.setAssetBasePath("fonts/game/");
		final ITexture scoreTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		final ITexture countdownTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		final ITexture gameOverTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		final ITexture finalScoreTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		final ITexture pauseTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		scoreFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), scoreTexture, activity.getAssets(), "font.ttf", 35, true, Color.WHITE_ARGB_PACKED_INT, 1f, Color.WHITE_ARGB_PACKED_INT);
		countdownFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), countdownTexture, activity.getAssets(), "font.ttf", 70, true, Color.RED_ARGB_PACKED_INT, 2f, Color.RED_ARGB_PACKED_INT);
		gameOverFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), gameOverTexture, activity.getAssets(), "font.ttf", 45, true, Color.RED_ARGB_PACKED_INT, 2f, Color.RED_ARGB_PACKED_INT);
		finalScoreFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), finalScoreTexture, activity.getAssets(), "font.ttf", 40, true, Color.RED_ARGB_PACKED_INT, 2f, Color.RED_ARGB_PACKED_INT);
		pauseFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), pauseTexture, activity.getAssets(), "font.ttf", 45, true, Color.RED_ARGB_PACKED_INT, 2f, Color.RED_ARGB_PACKED_INT);
		scoreFont.load();
		countdownFont.load();
		gameOverFont.load();
		finalScoreFont.load();
		pauseFont.load();
	}
	
	private void unloadGameTextures() {
		this.gameTextureAtlas.unload();
		this.gameWindowsTextureAtlas.unload();
		this.gameAnimatedTextureAtlas.unload();
		this.gameBackgroundTextureAtlas.unload();
	}
	
	private void unloadGameFonts() {
		scoreFont.unload();
		countdownFont.unload();
		gameOverFont.unload();
		finalScoreFont.unload();
		pauseFont.unload();
	}
	
	
	//Manager Methods
	public static void prepareManager(Engine engine, GameActivity activity, BoundCamera camera, VertexBufferObjectManager vbom) {
		getInstance().engine = engine;
		getInstance().activity = activity;
		getInstance().camera = camera;
		getInstance().vbom = vbom;		
	}
	
	public static ResourcesManager getInstance() {
		return INSTANCE;
	}

}


