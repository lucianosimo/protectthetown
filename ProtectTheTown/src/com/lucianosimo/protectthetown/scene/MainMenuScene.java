package com.lucianosimo.protectthetown.scene;

import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.lucianosimo.protectthetown.base.BaseScene;
import com.lucianosimo.protectthetown.manager.SceneManager;
import com.lucianosimo.protectthetown.manager.SceneManager.SceneType;

public class MainMenuScene extends BaseScene implements IOnMenuItemClickListener{
	
	private MenuScene menuChildScene;
	private float screenWidth;
	private float screenHeight;
	
	private Text highScoreText;
	private int highScore;
	
	private final int MENU_PLAY = 0;
	private final int MENU_RATEUS = 1;
	private final int MENU_QUIT = 2;
	private final int MENU_GLOBAL_SCORES = 3;

	@Override
	public void createScene() {
		screenWidth = resourcesManager.camera.getWidth();
		screenHeight = resourcesManager.camera.getHeight();
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		int played = sharedPreferences.getInt("played", 0);
		//Rated: 0 = no, 1 = yes, 2 = no and don't want to rate
		int rated = sharedPreferences.getInt("rated", 0);
		Log.i("protect", "played: " + played);
		Log.i("protect", "rated: " + rated);
		if (rated == 0) {
			if (played == 5 || played == 20 || played == 50) {
				displayRateUsWindow();
			}
		}
		createBackground();
		createMenuChildScene();
	}

	@Override
	public void onBackKeyPressed() {
		System.exit(0);
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_MENU;
	}

	@Override
	public void disposeScene() {
	}
	
	public void createBackground() {
		AutoParallaxBackground background = new AutoParallaxBackground(0, 0, 0, 12);
		background.attachParallaxEntity(new ParallaxEntity(0, new Sprite(screenWidth/2, screenHeight/2, resourcesManager.menu_background_region, vbom)));
		this.setBackground(background);
	}
	
	private void createMenuChildScene() {
		
		menuChildScene = new MenuScene(camera);
		menuChildScene.setPosition(screenWidth/2, screenHeight/2);
		
		loadHighScore();
		
		highScoreText = new Text(0, -300, resourcesManager.highScoreFont, "High Score: 123456789", new TextOptions(HorizontalAlign.CENTER), vbom);
		highScoreText.setText("High Score: " + highScore);
		highScoreText.setColor(Color.BLACK_ARGB_PACKED_INT);
		
		Sprite play_button_background = new Sprite(0, -10, resourcesManager.menu_play_button_background_region, vbom);
		play_button_background.registerEntityModifier(new LoopEntityModifier(new RotationModifier(60, 0, -(4 * 180))));
		
		Sprite rateus_button_background = new Sprite(-500, -225, resourcesManager.menu_rateus_button_background_region, vbom);
		rateus_button_background.registerEntityModifier(new LoopEntityModifier(new RotationModifier(60, 0, 3 * 180)));
		
		Sprite global_scores_button_background = new Sprite(500, -225, resourcesManager.menu_rateus_button_background_region, vbom);
		global_scores_button_background.registerEntityModifier(new LoopEntityModifier(new RotationModifier(60, 0, 3 * 180)));
		
		menuChildScene.attachChild(play_button_background);
		menuChildScene.attachChild(rateus_button_background);
		menuChildScene.attachChild(global_scores_button_background);
		menuChildScene.attachChild(highScoreText);
		
		final IMenuItem playMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_PLAY, resourcesManager.menu_play_button_region, vbom), 1.2f, 1);
		final IMenuItem rateusMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_RATEUS, resourcesManager.menu_rateus_button_region, vbom), 1.2f, 1);
		final IMenuItem globalScoresMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_GLOBAL_SCORES, resourcesManager.menu_global_scores_button_region, vbom), 1.2f, 1);
		final IMenuItem quitMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_QUIT, resourcesManager.menu_quit_button_region, vbom), 1.2f, 1);

		menuChildScene.addMenuItem(playMenuItem);
		menuChildScene.addMenuItem(rateusMenuItem);
		menuChildScene.addMenuItem(globalScoresMenuItem);
		menuChildScene.addMenuItem(quitMenuItem);
		
		menuChildScene.buildAnimations();
		menuChildScene.setBackgroundEnabled(false);
		
		playMenuItem.setPosition(0, -10);
		rateusMenuItem.setPosition(-500, -225);
		globalScoresMenuItem.setPosition(500, -225);
		quitMenuItem.setPosition(570, 290);
		
		menuChildScene.setOnMenuItemClickListener(this);
		setChildScene(menuChildScene);
	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,	float pMenuItemLocalX, float pMenuItemLocalY) {
		switch (pMenuItem.getID()) {
			case MENU_PLAY:
				SceneManager.getInstance().loadGameScene(engine, this);
				return true;
			case MENU_RATEUS:
				activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.lucianosimo.protectthetown")));
				SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
				int rated = sharedPreferences.getInt("rated", 0);
				Editor editor = sharedPreferences.edit();
				rated = 1;
				editor.putInt("rated", rated);
				editor.commit();
				return true;
			case MENU_GLOBAL_SCORES:
				activity.showLeaderboard();
				return true;
			case MENU_QUIT:
				System.exit(0);
				return true;
			default:
				return false;
		}
	}

	@Override
	public void handleOnPause() {
		// TODO Auto-generated method stub
		
	}
	
	private void loadHighScore() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		highScore = sharedPreferences.getInt("highScore", 0);
	}
	
	private void displayRateUsWindow() {
		MainMenuScene.this.activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				new AlertDialog.Builder(MainMenuScene.this.activity)
				.setMessage("Do you want to rate us")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

				    public void onClick(DialogInterface dialog, int whichButton) {
				    	activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.lucianosimo.protectthetown")));
				    	SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
						int rated = sharedPreferences.getInt("rated", 0);
						Editor editor = sharedPreferences.edit();
						rated = 1;
						editor.putInt("rated", rated);
						editor.commit();
				    }})
				.setNegativeButton("Never", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
						int rated = sharedPreferences.getInt("rated", 0);
						Editor editor = sharedPreferences.edit();
						rated = 2;
						editor.putInt("rated", rated);
						editor.commit();						
					}
				})
				.setNeutralButton("Maybe later", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
						int played = sharedPreferences.getInt("played", 0);
						Editor editor = sharedPreferences.edit();
						played++;
						editor.putInt("played", played);
						editor.commit();						
					}
				})
				.show();
			}
		});
	}

}
