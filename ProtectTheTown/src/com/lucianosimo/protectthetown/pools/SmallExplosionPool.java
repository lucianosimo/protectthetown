package com.lucianosimo.protectthetown.pools;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.pool.GenericPool;

import android.util.Log;

import com.lucianosimo.protectthetown.scene.GameScene;

public class SmallExplosionPool extends GenericPool<AnimatedSprite>{

	private ITiledTextureRegion iTextureRegion;
	private VertexBufferObjectManager vbom;
	private GameScene game;
	private int newSmallExplosionsCreated = 0;
	private int SmallexplosionsRecycled = 0;
	private int SmallexplosionsObtained = 0;
	 
    public SmallExplosionPool(ITiledTextureRegion pTextureRegion, VertexBufferObjectManager vbom, GameScene gameLocal) {
        iTextureRegion = pTextureRegion;
        this.vbom = vbom;
        game = gameLocal;
    }
 
    @Override
    protected AnimatedSprite onAllocatePoolItem() {
    	newSmallExplosionsCreated++;
    	Log.i("protect", "new new Smallexplosion sprite");
    	Log.i("protect", "********************************");
    	Log.i("protect", "newSmallExplosionsCreated: " + newSmallExplosionsCreated);
    	Log.i("protect", "********************************");
    	AnimatedSprite exp = new AnimatedSprite(0, 0, iTextureRegion.deepCopy(), vbom);
    	//game.attachChild(exp);
        return exp;
        
    }
 
    protected void onHandleRecycleItem(final AnimatedSprite explosion) {
    	explosion.clearEntityModifiers();
    	explosion.clearUpdateHandlers();
    	explosion.setVisible(false);
    	explosion.detachSelf();
    	explosion.reset();
    	SmallexplosionsRecycled++;
    	Log.i("protect", "------------------------------");
    	Log.i("protect", "Smallexplosion recycled");
    	Log.i("protect", "------------------------------");
    	Log.i("protect", "********************************");
    	Log.i("protect", "SmallexplosionsRecycled: " + SmallexplosionsRecycled);
    	Log.i("protect", "********************************");
    }
    
    @Override
    protected void onHandleObtainItem(AnimatedSprite explosion) {
    	Log.i("protect", "new recycled explosion sprite");
    	Log.i("protect", "available: " + this.getAvailableItemCount());
    	Log.i("protect", "unrecycled: " + this.getUnrecycledItemCount());
    	Log.i("protect", "------------------------------");
    	SmallexplosionsObtained++;
    	Log.i("protect", "********************************");
    	Log.i("protect", "SmallexplosionsObtained: " + SmallexplosionsObtained);
    	Log.i("protect", "********************************");
    	explosion.reset();
    	explosion.setVisible(true);
    	if (!explosion.hasParent()) {
    		game.attachChild(explosion);
    	}
    	
    }
}
