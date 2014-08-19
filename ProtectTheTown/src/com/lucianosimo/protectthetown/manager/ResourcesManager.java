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
	
	//Menu items
	public ITextureRegion loading_background_region;
	public ITextureRegion menu_background_region;
	public ITextureRegion menu_play_button_region;
	
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
	
	public ITextureRegion game_ufo_1_region;
	public ITextureRegion game_ufo_2_region;
	public ITextureRegion game_ufo_3_region;
	
	public ITextureRegion game_ufo_shot_region;
		
	public ITextureRegion game_small_house_region;
	public ITextureRegion game_house_region;
	public ITextureRegion game_large_house_region;
	
	public ITextureRegion game_health_bar_frame_region;
	
	//Platforms
	public ITextureRegion game_floor_region;
	public ITextureRegion game_base_floor_region;
	
	//Backgrounds
	public ITextureRegion game_background_region;
	
	//Animated
	
	//Windows
	public ITextureRegion game_window_region;
	
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
		//loadMenuAudio();
		//loadMenuFonts();
	}
	
	public void unloadMenuResources() {
		unloadMenuTextures();
		//unloadMenuFonts();
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
		menuTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 512, 512, TextureOptions.BILINEAR);
		menuBackgroundTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1280, 720, TextureOptions.BILINEAR);
		loadingBackgroundTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1280, 720, TextureOptions.BILINEAR);
		
		loading_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(loadingBackgroundTextureAtlas, activity, "loading_background.png");
		menu_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuBackgroundTextureAtlas, activity, "menu_background.png");
		menu_play_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "menu_play_button.png");
		
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
	
	/*private void unloadMenuFonts() {
		
	}
	
	private void unloadMenuAudio() {
		
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
		
		gameTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1500, 1500, TextureOptions.BILINEAR);
		gameWindowsTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1000, 1000, TextureOptions.BILINEAR);
		gameAnimatedTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1000, 1000, TextureOptions.BILINEAR);
		gameBackgroundTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1280, 720, TextureOptions.BILINEAR);
		
		game_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameBackgroundTextureAtlas, activity, "game_background.png");
		
		game_small_rock_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_small_rock.png");
		game_rock_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_rock.png");
		game_large_rock_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_large_rock.png");
		
		game_satelite_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_satelite.png");
		
		game_ufo_1_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_ufo_1.png");
		game_ufo_2_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_ufo_2.png");
		game_ufo_3_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_ufo_3.png");
		
		game_ufo_shot_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_ufo_shot.png");
		
		game_small_house_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_small_house.png");
		game_house_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_house.png");
		game_large_house_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_large_house.png");
		
		game_health_bar_frame_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_health_bar_frame.png");
		
		game_base_floor_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_base_floor.png");
		game_floor_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_floor.png");
		
		game_window_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameWindowsTextureAtlas, activity, "game_window.png");
		
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
		scoreFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), scoreTexture, activity.getAssets(), "AgentOrange.ttf", 35, true, Color.WHITE_ARGB_PACKED_INT, 1f, Color.WHITE_ARGB_PACKED_INT);
		countdownFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), countdownTexture, activity.getAssets(), "AgentOrange.ttf", 70, true, Color.RED_ARGB_PACKED_INT, 2f, Color.RED_ARGB_PACKED_INT);
		gameOverFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), gameOverTexture, activity.getAssets(), "AgentOrange.ttf", 45, true, Color.RED_ARGB_PACKED_INT, 2f, Color.RED_ARGB_PACKED_INT);
		finalScoreFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), finalScoreTexture, activity.getAssets(), "AgentOrange.ttf", 40, true, Color.RED_ARGB_PACKED_INT, 2f, Color.RED_ARGB_PACKED_INT);
		pauseFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), pauseTexture, activity.getAssets(), "AgentOrange.ttf", 45, true, Color.RED_ARGB_PACKED_INT, 2f, Color.RED_ARGB_PACKED_INT);
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


