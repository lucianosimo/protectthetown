package com.lucianosimo.protectthetown.pools;

import org.andengine.engine.camera.Camera;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.pool.GenericPool;

import com.lucianosimo.protectthetown.object.SmallRock;

public class SmallRockPool extends GenericPool<SmallRock>{

	private VertexBufferObjectManager vbom;
	private Camera camera;
	private PhysicsWorld physicsWorld;
	
    public SmallRockPool(VertexBufferObjectManager vbom, Camera camera, PhysicsWorld physicsWorld) {
        this.vbom = vbom;
        this.camera = camera;
        this.physicsWorld = physicsWorld;
    }
 
    @Override
    protected SmallRock onAllocatePoolItem() {
    	final SmallRock smallRock = new SmallRock(0, 0, vbom, camera, physicsWorld); 
        return smallRock;
    }
 
    protected void onHandleRecycleItem(final SmallRock smallRock) {
    	smallRock.getSmallRockBody().setActive(false);
    	smallRock.clearEntityModifiers();
    	smallRock.clearUpdateHandlers();
    	smallRock.setVisible(false);
    	smallRock.detachSelf();
    	smallRock.reset();
    }
    
    @Override
    protected void onHandleObtainItem(SmallRock smallRock) {
    	smallRock.getSmallRockBody().setActive(true);
    	smallRock.reset();
    }
}
