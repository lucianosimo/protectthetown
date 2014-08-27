package com.lucianosimo.protectthetown.pools;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.pool.GenericPool;

import android.util.Log;

import com.lucianosimo.protectthetown.scene.GameScene;

public class ExplosionPool extends GenericPool<AnimatedSprite>{

	private ITiledTextureRegion iTextureRegion;
	private VertexBufferObjectManager vbom;
	private GameScene game;
	private int newExplosionsCreated = 0;
	private int explosionsRecycled = 0;
	private int explosionsObtained = 0;
	 
    public ExplosionPool(ITiledTextureRegion pTextureRegion, VertexBufferObjectManager vbom, GameScene gameLocal) {
        iTextureRegion = pTextureRegion;
        this.vbom = vbom;
        game = gameLocal;
    }
 
    @Override
    protected AnimatedSprite onAllocatePoolItem() {
    	newExplosionsCreated++;
    	Log.i("protect", "new new explosion sprite");
    	Log.i("protect", "********************************");
    	Log.i("protect", "newExplosionsCreated: " + newExplosionsCreated);
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
    	explosionsRecycled++;
    	Log.i("protect", "------------------------------");
    	Log.i("protect", "explosion recycled");
    	Log.i("protect", "------------------------------");
    	Log.i("protect", "********************************");
    	Log.i("protect", "explosionsRecycled: " + explosionsRecycled);
    	Log.i("protect", "********************************");
    }
    
    @Override
    protected void onHandleObtainItem(AnimatedSprite explosion) {
    	Log.i("protect", "new recycled explosion sprite");
    	Log.i("protect", "available: " + this.getAvailableItemCount());
    	Log.i("protect", "unrecycled: " + this.getUnrecycledItemCount());
    	Log.i("protect", "------------------------------");
    	explosionsObtained++;
    	Log.i("protect", "********************************");
    	Log.i("protect", "explosionsObtained: " + explosionsObtained);
    	Log.i("protect", "********************************");
    	explosion.reset();
    	explosion.setVisible(true);
    	if (!explosion.hasParent()) {
    		game.attachChild(explosion);
    	}
    	
    }
}
