package com.lucianosimo.protectthetown.pools;

import org.andengine.engine.camera.Camera;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.pool.GenericPool;

import com.lucianosimo.protectthetown.object.Rock;

public class RockPool extends GenericPool<Rock>{

	private VertexBufferObjectManager vbom;
	private Camera camera;
	private PhysicsWorld physicsWorld;
	
    public RockPool(VertexBufferObjectManager vbom, Camera camera, PhysicsWorld physicsWorld) {
        this.vbom = vbom;
        this.camera = camera;
        this.physicsWorld = physicsWorld;
    }
 
    @Override
    protected Rock onAllocatePoolItem() {
    	final Rock rock = new Rock(0, 0, vbom, camera, physicsWorld); 
        return rock;
    }
 
    protected void onHandleRecycleItem(final Rock rock) {
    	rock.getRockBody().setActive(false);
    	rock.clearEntityModifiers();
    	rock.clearUpdateHandlers();
    	rock.setVisible(false);
    	rock.detachSelf();
    	rock.reset();
    }
    
    @Override
    protected void onHandleObtainItem(Rock rock) {
    	rock.getRockBody().setActive(true);
    	rock.reset();
    }
}
