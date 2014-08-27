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

import com.lucianosimo.protectthetown.base.BaseScene;
import com.lucianosimo.protectthetown.manager.SceneManager;
import com.lucianosimo.protectthetown.manager.SceneManager.SceneType;

public class MainMenuScene extends BaseScene implements IOnMenuItemClickListener{
	
	private MenuScene menuChildScene;
	private float screenWidth;
	private float screenHeight;
	
	private final int MENU_PLAY = 0;

	@Override
	public void createScene() {
		screenWidth = resourcesManager.camera.getWidth();
		screenHeight = resourcesManager.camera.getHeight();
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
		
		Sprite play_button_background = new Sprite(0, -10, resourcesManager.menu_play_button_background_region, vbom);
		//play_button_background.setRotation(2);
		play_button_background.registerEntityModifier(new LoopEntityModifier(new RotationModifier(60, 0, -(4 * 180))));
		menuChildScene.attachChild(play_button_background);
		
		final IMenuItem playMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_PLAY, resourcesManager.menu_play_button_region, vbom), 1.2f, 1);

		menuChildScene.addMenuItem(playMenuItem);
		
		menuChildScene.buildAnimations();
		menuChildScene.setBackgroundEnabled(false);
		
		playMenuItem.setPosition(0, -10);
		
		menuChildScene.setOnMenuItemClickListener(this);
		setChildScene(menuChildScene);
	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,	float pMenuItemLocalX, float pMenuItemLocalY) {
		switch (pMenuItem.getID()) {
			case MENU_PLAY:
				SceneManager.getInstance().loadGameScene(engine, this);
				return true;
			default:
				return false;
		}
	}

	@Override
	public void handleOnPause() {
		// TODO Auto-generated method stub
		
	}

}
