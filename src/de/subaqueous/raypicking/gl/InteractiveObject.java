package de.subaqueous.raypicking.gl;

import java.nio.FloatBuffer;
import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

/**
 * sample implementation of a GLSurfaceView Renderer that supports ray picking of objects
 * based on ios code from: http://blog.nova-box.com/2010/05/iphone-ray-picking-glunproject-sample.html
 * 
 * @author Nils Domrose
 *
 */
class InteractiveObject{
	
	private final static String LCAT = "InteractiveObject";

	private final static float INTERACTIVE_OBJECT_SIZE = 0.2f;
	private final static float INTERACTIVE_OBJECT_DISTANCE = 0.8f;
	
	private int textureId;
	private float pan;
	private float tilt;
	protected float x;
	protected float y;
	protected float z;
	private float rotation;
	private float alpha;
	private float scaleValue;
	private float [] size = new float[2];
	
    private FloatBuffer   mVertexBuffer;
    private FloatBuffer   mCoordsBuffer;

	
    public InteractiveObject(float pan, float tilt,float scale, int textureId ){
    	this.textureId = textureId;
    	this.pan = pan;
    	this.tilt = tilt;
    	this.size[0] = INTERACTIVE_OBJECT_SIZE;
    	this.size[1] = INTERACTIVE_OBJECT_SIZE;
    	this.scaleValue = scale;
    	this.alpha = 1;
    	this.rotation = this.pan + 90;
    	processTransformation();
    	
       	float width = this.size[0];
    	float height = this.size[0];
    	
    	float vertices[] = {
        		width/2, -height/2,	0,           
        		-width/2,-height/2,	0,            		
        		-width/2, height/2,	0,         
        		width/2,  height/2,	0,        
        };
    	mVertexBuffer = Helper.createFloatBuffer(vertices);
        
        float textureCoords[] = {
        				0.0f, 1.0f,     
        				1.0f, 1.0f,     
        				1.0f, 0.0f,     
        				0.0f, 0.0f,      
        };
        mCoordsBuffer = Helper.createFloatBuffer(textureCoords);


    }
    
    private void processTransformation(){
    	float distanceCoeff = INTERACTIVE_OBJECT_DISTANCE;
    	
    	this.x = (float) (Math.cos(Helper.degToRadians(this.pan)) * distanceCoeff);   	
    	this.y = (float) (Math.sin(Helper.degToRadians(this.tilt)) * distanceCoeff);   	
    	this.z = (float) (-Math.sin(Helper.degToRadians(this.pan)) * distanceCoeff);
    	//Log.d(LCAT, "TextureId: " + textureId + " x: " + this.x + " y: " + this.y + " z: " + this.z);
    }

    public void draw(GL10 gl){
    	
    	processTransformation();
    	
    	gl.glEnable(GL10.GL_TEXTURE_2D);	

        gl.glLoadIdentity();
    	gl.glColor4f(alpha, alpha, alpha, alpha);
    	
    	gl.glEnable(GL10.GL_BLEND);
    	gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
    	
    	gl.glTranslatef(this.x, this.y, this.z);
    	gl.glRotatef(this.rotation, 0, 1, 0);
    	gl.glScalef(scaleValue, scaleValue, scaleValue);
    	
    	gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
    	gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
    	
    	gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
    	gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mCoordsBuffer);
    	
    	
    	gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
    	gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 4);
    	
    	gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);	
    	gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
    	gl.glDisable(GL10.GL_BLEND);


    }
    
    public void update(){
    	this.rotation = ((int)(this.rotation + 2 )) % 360;
    	//Log.d(LCAT, "rotation:  " + this.rotation);
    }
    
    public void fireAction(){
    	Log.d(LCAT, "Touched");
    	if(this.alpha == 1f){
        	this.alpha = 0.5f;    		
    	}else{
    		this.alpha = 1f;
    	}
    }


}
