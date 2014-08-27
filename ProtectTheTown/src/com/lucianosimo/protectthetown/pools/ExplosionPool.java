package com.lucianosimo.protectthetown.pools;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.pool.GenericPool;

import android.util.Log;

public class ExplosionPool extends GenericPool<AnimatedSprite>{

	private ITiledTextureRegion iTextureRegion;
	private VertexBufferObjectManager vbom;
	 
    public ExplosionPool(ITiledTextureRegion pTextureRegion, VertexBufferObjectManager vbom) {
        iTextureRegion = pTextureRegion;
        this.vbom = vbom;
    }
 
    @Override
    protected AnimatedSprite onAllocatePoolItem() {
    	Log.i("protect", "new explosion sprite");
        return new AnimatedSprite(0, 0, iTextureRegion.deepCopy(), vbom);
    }
 
    protected void onHandleRecycleItem(final AnimatedSprite explosion) {
    	explosion.clearEntityModifiers();
    	explosion.clearUpdateHandlers();
    	explosion.setVisible(false);
    	explosion.detachSelf();
    	explosion.reset();
    	Log.i("protect", "explosion recycled");
    }
    
    @Override
    protected void onHandleObtainItem(AnimatedSprite explosion) {
    	explosion.reset();
    }
}
