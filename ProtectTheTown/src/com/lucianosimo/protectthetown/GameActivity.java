package com.lucianosimo.protectthetown;

import java.io.IOException;

import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.BaseGameActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.lucianosimo.protectthetown.manager.ResourcesManager;
import com.lucianosimo.protectthetown.manager.SceneManager;
import com.swarmconnect.Swarm;
import com.swarmconnect.SwarmLeaderboard;

public class GameActivity extends BaseGameActivity {

	private BoundCamera camera;
	//private ScoreNinjaAdapter scoreNinjaAdapter;
	public static float mGravityX = 0;
	private int score = 0;
	
	private final static float SPLASH_DURATION = 5f;	
	//private final static String APP_ID = "protectthetownscoreboard";
	//private final static String PRIVATE_KEY = "36DC8F51102932FAC652EFE2BDE14DF5";
	//private Chartboost cb;
	
	private final static int SWARM_APP_ID = 12987;
	private final static String SWARM_APP_KEY = "27b45b3507f2daea1c39203e523c00cf";
	private final static int SWARM_LEADERBOARD_ID = 17629;
	
	@Override
	protected void onCreate(Bundle pSavedInstanceState) {
		super.onCreate(pSavedInstanceState);
		/*this.cb = Chartboost.sharedChartboost();
		String appId = "53c57c8289b0bb3697c25124";
		String appSignature = "3f0a28521b32648044a33f149570570df81c89c6";
		this.cb.onCreate(this, appId, appSignature, this.chartBoostDelegate);
		CBPreferences.getInstance().setAnimationsOff(true);
		CBPreferences.getInstance().setOrientation(CBOrientation.PORTRAIT);*/
		//scoreNinjaAdapter = new ScoreNinjaAdapter(this, APP_ID, PRIVATE_KEY);
		Swarm.setActive(this);
		Swarm.preload(this, SWARM_APP_ID, SWARM_APP_KEY);
	}
	
    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      scoreNinjaAdapter.onActivityResult(requestCode, resultCode, data);
    }
    
    public void onGameOver() {
      this.runOnUiThread(new Runnable() {
		
		@Override
		public void run() {
			//scoreNinjaAdapter.show(score);
			scoreNinjaAdapter.show();
			
		}
	});
      
    }*/
    
	public void showLeaderboard() {
		Swarm.setAllowGuests(true);
		if (!Swarm.isInitialized() ) {
    		Swarm.init(this, SWARM_APP_ID, SWARM_APP_KEY);
        }
		this.runOnUiThread(new Runnable() {
    		@Override
    		public void run() {
    			//SwarmLeaderboard.submitScoreAndShowLeaderboard(SWARM_LEADERBOARD_ID, score);
    			SwarmLeaderboard.showLeaderboard(SWARM_LEADERBOARD_ID);
    		}
    	});
	}
	
    public void submitScore(int submitScore) {
    	score = submitScore;
    	Swarm.setAllowGuests(true);
    	if (!Swarm.isInitialized() ) {
    		Swarm.init(this, SWARM_APP_ID, SWARM_APP_KEY);
        }
    	this.runOnUiThread(new Runnable() {
    		@Override
    		public void run() {
    			//SwarmLeaderboard.submitScoreAndShowLeaderboard(SWARM_LEADERBOARD_ID, score);
    			SwarmLeaderboard.submitScore(SWARM_LEADERBOARD_ID, score);
    		}
    	});
    }
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		camera = new BoundCamera(0, 0, 1280, 720);
		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(), this.camera);
		engineOptions.getAudioOptions().setNeedsMusic(true).setNeedsSound(true);
		engineOptions.getRenderOptions().setDithering(true);
		engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
		return engineOptions;
	}
	
	
	@Override
	protected void onPause() {
		super.onPause();
		SceneManager.getInstance().getCurrentScene().handleOnPause();
		Swarm.setInactive(this);
		mEngine.getSoundManager().setMasterVolume(0);
		mEngine.getMusicManager().setMasterVolume(0);
	}
	
	@Override
	protected synchronized void onResume() {
		super.onResume();
		Swarm.setActive(this);
		mEngine.getSoundManager().setMasterVolume(1);
		mEngine.getMusicManager().setMasterVolume(1);
	}

	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback)	throws IOException {
		ResourcesManager.prepareManager(mEngine, this, camera, getVertexBufferObjectManager());
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)	throws IOException {
		SceneManager.getInstance().createSplashScene(pOnCreateSceneCallback);		
	}

	@Override
	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws IOException {
		mEngine.registerUpdateHandler(new TimerHandler(SPLASH_DURATION, new ITimerCallback() {
			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				mEngine.unregisterUpdateHandler(pTimerHandler);
				SceneManager.getInstance().createMenuScene();
			}
		}));
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
	
	@Override
	public Engine onCreateEngine(EngineOptions pEngineOptions) {
		return new LimitedFPSEngine(pEngineOptions, 60);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//this.cb.onDestroy(this);
		System.exit(0);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			SceneManager.getInstance().getCurrentScene().onBackKeyPressed();
			//dimSoftButtonsIfPossible();
		}
		return false;
	}
	
	public void tweetScore(Intent intent) {
		startActivity(Intent.createChooser(intent, "Protect the town"));
	}
	
	/*@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (this.cb.onBackPressed())
		        return false;
		    else
			SceneManager.getInstance().getCurrentScene().onBackKeyPressed();
		}
		return false;
	}*/

    /*@Override
	protected void onStart() {
		super.onStart();
		this.cb.onStart(this);
		this.cb.cacheInterstitial();
	}*/

	/*@Override
	protected void onStop() {
		super.onStop();
		this.cb.onStop(this);
	}*/
	
	/*public void showAd() {
		this.cb.showInterstitial(); 
	}*/
	
	/*private ChartboostDelegate chartBoostDelegate = new ChartboostDelegate() {

		@Override
		public boolean shouldDisplayInterstitial(String location) {
			//Log.i("parachute", "SHOULD DISPLAY INTERSTITIAL '"+location+ "'?");
			return true;
		}

		@Override
		public boolean shouldRequestInterstitial(String location) {
			//Log.i("parachute", "SHOULD REQUEST INSTERSTITIAL '"+location+ "'?");
			return true;
		}

		@Override
		public void didCacheInterstitial(String location) {
			Log.i("parachute", "INTERSTITIAL '"+location+"' CACHED");
		}

		@Override
		public void didFailToLoadInterstitial(String location, CBImpressionError error) {
		    // Show a house ad or do something else when a chartboost interstitial fails to load
			
			Log.i("parachute", "INTERSTITIAL '"+location+"' REQUEST FAILED - " + error.name());
			//Toast.makeText(GameActivity.this, "Interstitial '"+location+"' Load Failed",	Toast.LENGTH_SHORT).show();
		}

		@Override
		public void didDismissInterstitial(String location) {
			
			// Immediately re-caches an interstitial
			cb.cacheInterstitial(location);
			
			//Log.i("parachute", "INTERSTITIAL '"+location+"' DISMISSED");
			//Toast.makeText(GameActivity.this, "Dismissed Interstitial '"+location+"'",	Toast.LENGTH_SHORT).show();
		}

		@Override
		public void didCloseInterstitial(String location) {
			//Log.i("parachute", "INSTERSTITIAL '"+location+"' CLOSED");
			//Toast.makeText(GameActivity.this, "Closed Interstitial '"+location+"'", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void didClickInterstitial(String location) {
			//Log.i("parachute", "DID CLICK INTERSTITIAL '"+location+"'");
			//Toast.makeText(GameActivity.this, "Clicked Interstitial '"+location+"'",Toast.LENGTH_SHORT).show();
		}

		@Override
		public void didShowInterstitial(String location) {
			//Log.i("parachute", "INTERSTITIAL '" + location + "' SHOWN");
		}

		@Override
		public void didFailToRecordClick(String uri, CBClickError error) {

			//Log.i("parachute", "FAILED TO RECORD CLICK " + (uri != null ? uri : "null") + ", error: " + error.name());
			//Toast.makeText(GameActivity.this, "URL '"+uri+"' Click Failed",
					//Toast.LENGTH_SHORT).show();
		}

		@Override
		public boolean shouldDisplayLoadingViewForMoreApps() {
			return true;
		}

		@Override
		public boolean shouldRequestMoreApps() {

			return true;
		}

		@Override
		public boolean shouldDisplayMoreApps() {
			//Log.i("parachute", "SHOULD DISPLAY MORE APPS?");
			return true;
		}

		@Override
		public void didFailToLoadMoreApps(CBImpressionError error) {
			//Log.i("parachute", "MORE APPS REQUEST FAILED - " + error.name());
			//Toast.makeText(GameActivity.this, "More Apps Load Failed",
					//Toast.LENGTH_SHORT).show();
			
		}

		@Override
		public void didCacheMoreApps() {
			//Log.i("parachute", "MORE APPS CACHED");
		}

		@Override
		public void didDismissMoreApps() {
			//Log.i("parachute", "MORE APPS DISMISSED");
			//Toast.makeText(GameActivity.this, "Dismissed More Apps",
					//Toast.LENGTH_SHORT).show();
		}

		@Override
		public void didCloseMoreApps() {
			//Log.i("parachute", "MORE APPS CLOSED");
			//Toast.makeText(GameActivity.this, "Closed More Apps",
					//Toast.LENGTH_SHORT).show();
		}

		@Override
		public void didClickMoreApps() {
			//Log.i("parachute", "MORE APPS CLICKED");
			//Toast.makeText(GameActivity.this, "Clicked More Apps",
					//Toast.LENGTH_SHORT).show();
		}

		@Override
		public void didShowMoreApps() {
			//Log.i("parachute", "MORE APPS SHOWED");
		}

		@Override
		public boolean shouldRequestInterstitialsInFirstSession() {
			return true;
		}

		@Override
		public boolean shouldPauseClickForConfirmation(
				CBAgeGateConfirmation callback) {
			return false;
		}
	};*/

}
