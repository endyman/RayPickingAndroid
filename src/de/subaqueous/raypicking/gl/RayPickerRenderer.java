package de.subaqueous.raypicking.gl;

import static android.opengl.GLES11.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import de.subaqueous.raypickingandroid.App;
import de.subaqueous.raypickingandroid.R;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.Log;
import android.view.MotionEvent;

/**
 * sample implementation of a GLSurfaceView Renderer that supports ray picking of objects
 * based on ios code from: http://blog.nova-box.com/2010/05/iphone-ray-picking-glunproject-sample.html
 * 
 * @author Nils Domrose
 *
 */
public class RayPickerRenderer implements GLSurfaceView.Renderer{
	
	private static final String LCAT = "RayPickerRenderer";
	private static final int INTERACTIVE_TEXTURE = R.drawable.target;
	
	private static final long MOVE_INTERVAL = 100;
	private static final long CLICK_TIMEOUT = 500;

	private static int INTERACTIVE_OBJECT_COUNT = 30;
	private static int RAY_ITERATIONS = 1000;
	private static float COLLISION_RADIUS = 0.1f;
	
	private float mCameraXRotation;
	private float mCameraYRotation;
	private float mCameraXRotationSpeed;
	private float mCameraYRotationSpeed;
	private int mWidth;
	private int mHeight;
	private GLTextures mTextures;
	private long touchStartDate;
	private long moveStartDate;
	
	private float [] mModelview = new float[16];
	private float [] mProjection = new float[16];
	private int [] mViewport = new int[4];
	private int interactiveObjectsCount = 0;
	private List<InteractiveObject> interactiveObjects = new ArrayList<InteractiveObject>();
	
	private Boolean touched = false;
	private boolean moving = false;
	private float [] touchStartPosition = new float[2];

	
	/**
	 * empty public constructor
	 * 
	 */
	public RayPickerRenderer(){
		
	}

	/* (non-Javadoc)
	 * @see android.opengl.GLSurfaceView.Renderer#onDrawFrame(javax.microedition.khronos.opengles.GL10)
	 */
	@Override
	public void onDrawFrame(GL10 gl) {
		if (gl instanceof GL11){
			// update / draw view
			drawView((GL11) gl);
		}
		
	}

	/* (non-Javadoc)
	 * @see android.opengl.GLSurfaceView.Renderer#onSurfaceChanged(javax.microedition.khronos.opengles.GL10, int, int)
	 */
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		
		// set the new width and height
		mWidth = width;
		mHeight = height;
		
		// update the viewport to the current size
		gl.glViewport(0, 0, width, height);
		if (gl instanceof GL11){
			gl.glGetIntegerv(GL11.GL_VIEWPORT, mViewport, 0);
		}
		
		// Select the projection matrix
		gl.glMatrixMode(GL10.GL_PROJECTION);
		
		// reset the Projection Matrix
		gl.glLoadIdentity();
		
		// Calculate the aspect ratio of the window
//		GLU.gluPerspective(gl, 45.0f,
//                (float) width / (float) height,
//                0.1f, 100.0f);
		
		// Select the modelvie matrix
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		
		// Reset the modelview matrix
		gl.glLoadIdentity();
		
		// set inital Frustrum and Field of View
		setupFrustumWithFov(gl, 30.f);	
	}

	/* (non-Javadoc)
	 * @see android.opengl.GLSurfaceView.Renderer#onSurfaceCreated(javax.microedition.khronos.opengles.GL10, javax.microedition.khronos.egl.EGLConfig)
	 */
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// setup view
		setupView(gl);
		
		// create an new GLTextures instance for texture loading convenience
		mTextures = new GLTextures(gl, App.getInstance()); 
		
		// Initialize objects
		initObjects(gl);
	}
	
	
	/**
	 * handel external motion events
	 * @param e {@link MotionEvent}
	 */
	public void handleTouch(MotionEvent e){
		
		switch (e.getAction()){

		case MotionEvent.ACTION_DOWN:
			touchStartDate = System.currentTimeMillis();
			moveStartDate = 0;
			touched = true;
			moving  = false;
			break;
			
		case MotionEvent.ACTION_UP:
			if (!moving || System.currentTimeMillis() - touchStartDate < CLICK_TIMEOUT){
				for (int i=0; i<interactiveObjectsCount;i++){
					Boolean status = checkCollision(e.getX(), e.getY(), interactiveObjects.get(i));
					if(status){
						interactiveObjects.get(i).fireAction();
					}
				}
			}
			touched = false;
			moving = false;
			touchStartDate = 0;
			moveStartDate = 0;
			break;
			
		case MotionEvent.ACTION_MOVE:
			touched = true;
			// Initialize move
			if (!moving && System.currentTimeMillis() - touchStartDate < CLICK_TIMEOUT){
				touchStartPosition[0] = e.getX();
				touchStartPosition[1] = e.getY();
				moving = true;
				if (moveStartDate == 0){
					moveStartDate = System.currentTimeMillis();
				}
			}
			// process subsequent move events
			if (moving && System.currentTimeMillis() - touchStartDate > MOVE_INTERVAL){
				float distanceX = e.getX() - touchStartPosition[0];
				float distanceY = e.getY() - touchStartPosition[1];
				
				long duration = (System.currentTimeMillis() - touchStartDate);
				float touchSpeedX = (distanceX /duration) * 1000;
				float touchSpeedY = (distanceY /duration) * 1000;

				// update to current position and time
				touchStartPosition[0] = e.getX();
				touchStartPosition[1] = e.getY();
				touchStartDate = System.currentTimeMillis();
				
				float factor;
				if (mWidth > mHeight){
					factor = mWidth / mHeight;
				} else{
					factor = mHeight / mWidth;
				}

				// update camera rotation speed
				mCameraYRotationSpeed = -(touchSpeedX * 60.f * factor / mWidth);
				Log.d(LCAT,"Got ACTION_MOVE mCameraXRotationSpeed: " + mCameraXRotationSpeed);
				mCameraXRotationSpeed = -(touchSpeedY * 60.f * factor / mHeight);
				Log.d(LCAT,"Got ACTION_MOVE mCameraYRotationSpeed: " + mCameraYRotationSpeed);
			}
			break;					
		}
	}
	
	/**
	 * implementation of generating vector out of 2d coordinates to check for collisions with
	 * objects based on the blog post and ios sample code from http://blog.nova-box.com/2010/05/iphone-ray-picking-glunproject-sample.html
	 * 
	 * @param x	coordinate of the touch event on the screen
	 * @param y coordinate of the touch event on the screen
	 * @param object to check for collision
	 * @return true if a collision was found, false if not
	 */
	private boolean checkCollision(float x, float y, InteractiveObject object){
		y = mViewport[3] - y;
		
		float [] nearPoint = {0f, 0f, 0f, 0f};
		float [] farPoint = {0f, 0f, 0f, 0f};
		float [] rayVector = {0f, 0f, 0f};
		
		//Retreiving position projected on near plane
		GLU.gluUnProject(x, y, -1f, mModelview, 0, mProjection, 0, mViewport, 0, nearPoint, 0);
		
		//Retreiving position projected on far plane
		GLU.gluUnProject(x, y, 1f, mModelview, 0, mProjection, 0, mViewport, 0, farPoint, 0);
		
		// extract 3d Coordinates put of 4d Coordinates
		nearPoint = Helper.fixW(nearPoint);
		farPoint = Helper.fixW(farPoint);
		
		//Processing ray vector
		rayVector[0] = farPoint[0] - nearPoint[0]; 
		rayVector[1] = farPoint[1] - nearPoint[1]; 
		rayVector[2] = farPoint[2] - nearPoint[2];
		
		// calculate ray vector length
		float rayLength = (float) Math.sqrt((rayVector[0] * rayVector[0]) + (rayVector[1] * rayVector[1]) + (rayVector[2] * rayVector[2]));
				
		//normalizing ray vector
		rayVector[0] /= rayLength;
		rayVector[1] /= rayLength;
		rayVector[2] /= rayLength;
		
		float [] collisionPoint = {0f, 0f, 0f};
		float [] objectCenter = {
				object.x,
				object.y,
				object.z,
		};
		
		//Iterating over ray vector to check for collisions
		for(int i = 0; i < RAY_ITERATIONS; i++){
			collisionPoint[0] = rayVector[0] * rayLength/RAY_ITERATIONS * i;
			collisionPoint[1] = rayVector[1] * rayLength/RAY_ITERATIONS * i;
			collisionPoint[2] = rayVector[2] * rayLength/RAY_ITERATIONS * i;
			
			if (Helper.poinSphereCollision(collisionPoint, objectCenter, COLLISION_RADIUS)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * set inital parameters for the view after creation
	 * 
	 * @param gl
	 */
	private void setupView(GL10 gl){
		
		// set the background color
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		
		// enable smooth shading
		gl.glShadeModel(GL10.GL_SMOOTH);
		
//		// Depth Buffer Setup
//		gl.glClearDepthf(1.0f);
		
//		// enable depth testing
		gl.glEnable(GL10.GL_DEPTH_TEST);
//		// depth testing type
		
//		gl.glDepthFunc(GL10.GL_LEQUAL);
		
//		// perspective calculations
//		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		
		// gl blend Functions
		gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL10.GL_BLEND);

		// Initialize Camera position and speed
		mCameraXRotation = 0;
		mCameraYRotation = 0;
		
		mCameraXRotationSpeed = 0;
		mCameraYRotationSpeed = 0;	
	}
	
	/**
	 * setup the Frustrum for the 3d Space 
	 * 
	 * @param gl
	 * @param fov fieldOfView value
	 */
	private void setupFrustumWithFov(GL10 gl, float fov){
		
		float zNear = 0.1f, zFar = 100.0f, fieldOfView = fov;
		float size;
		
		size =  zNear * (float) Math.tan(Helper.degToRadians(fieldOfView) / 2.0f);
		
		// set frustrum
		gl.glLoadIdentity();
		gl.glFrustumf(-size, size, -size / (mWidth / mHeight), size / ( mWidth / mHeight), zNear, zFar);
	}
	
	/**
	 * initalize objects and load Textures
	 * @param gl
	 */
	private void initObjects(GL10 gl){
		
		// enable 2D textures
    	gl.glEnable(GL_TEXTURE_2D);
    	// add Resource IDs for textures to load
		mTextures.add(INTERACTIVE_TEXTURE);
		
		// load the Textures into the GPU
		mTextures.loadTextures();
		
		// retrieve the Texture id
		int interactiveId = mTextures.getTextureIdforResource(R.drawable.target);
		
		// create random interactive Objects
		interactiveObjectsCount = 0;
		for (int i = 0; i< INTERACTIVE_OBJECT_COUNT; i++){
			float pan = (float) new Random().nextInt(360);
			float tilt = (float) new Random().nextInt(360);
			float scale = (float) new Random().nextInt(15) / 10;
			
			InteractiveObject interactiveObject = new InteractiveObject((pan * -1) + 90, tilt, scale, interactiveId);
			interactiveObjects.add(interactiveObject);
			interactiveObjectsCount++;
		}	
	}
	
	/**
	 * update/draw view
	 * @param gl
	 */
	private void drawView(GL10 gl){
		
		updateCameraVariables();
		
		updateInteractiveObjects();
		
		startDraw(gl);
		
		setupProjection(gl);
		
		drawModels(gl);

		endDraw();
	}
	

	/**
	 * called before drawing the frame to update /set viewport and clear buffers
	 * 
	 * @param gl
	 */
	private void startDraw(GL10 gl){
		gl.glViewport(0, 0, mWidth, mHeight);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
	}
	
	/**
	 * stub method for cleanup code
	 * 
	 */
	private void endDraw(){
		
	}
	
	/**
	 * setup / update the projection
	 * @param gl
	 */
	private void setupProjection(GL10 gl){
		gl.glMatrixMode(GL_PROJECTION);

		float fov = 1.f * 26 + 30;
		setupFrustumWithFov(gl, fov);
		
		gl.glRotatef(mCameraXRotation, 1.f, 0.f, 0.f);
		gl.glRotatef(mCameraYRotation, 0.f, 1.f, 0.f);
		
		gl.glTranslatef(0, 0, 0);
		((GL11) gl).glGetFloatv( GL11.GL_PROJECTION_MATRIX, mProjection, 0);
	}
	
	/**
	 * update the camera rotation and apply limits
	 */
	private void updateCameraVariables() {
		mCameraXRotation += mCameraXRotationSpeed * 1.f/60.f;
		mCameraYRotation += mCameraYRotationSpeed * 1.f/60.f;
		
		// limit camera rotation to 180
//		int min = -90;
//		int max = 90;
		
//		if(mCameraXRotation < min) mCameraXRotation = min;
//		if(mCameraXRotation > max) mCameraXRotation = max;

		if (!touched){
			mCameraXRotation -= mCameraXRotationSpeed /100;
			mCameraYRotation -= mCameraYRotationSpeed /100;
		}
	}
	
	/**
	 * draw the object model
	 * - place calls for the objects draw methods here
	 * @param gl
	 */
	private void drawModels(GL10 gl) {
		gl.glMatrixMode(GL_MODELVIEW);
		gl.glLoadIdentity();
		((GL11) gl).glGetFloatv( GL_MODELVIEW_MATRIX, mModelview , 0 );

		// call convenience method to iterate over Interactive Objects
		drawInteractiveObjects(gl);

	}
	
	/**
	 * iterate over interactive Objects and call its update method
	 */
	private void updateInteractiveObjects(){
		for(int i = 0; i < interactiveObjectsCount; i++)
		{
			interactiveObjects.get(i).update();
		}
	}
	
	/**
	 * iterate over interactive Objects and call its draw method
	 * @param gl
	 */
	private void drawInteractiveObjects(GL10 gl) {
		for(int i = 0; i < interactiveObjectsCount; i++)
		{
			interactiveObjects.get(i).draw(gl);
		}		
	}
}
