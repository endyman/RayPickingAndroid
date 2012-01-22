package de.subaqueous.raypicking.gl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Sample GLSurfaceView demonstrating ray picking of open GL Objects on android
 * 
 * @author Nils Domrose
 *
 */
public class RayPickerSurfaceView extends GLSurfaceView {

	private static final String LCAT = "RayPickerSurfaceView";
	private final RayPickerRenderer mRenderer;

	
	/**
	 * Public constructor creating and setting a new Renderer
	 * @param context
	 */
	public RayPickerSurfaceView(Context context) {
		super(context);
		mRenderer = new RayPickerRenderer();
		setRenderer(mRenderer);
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
	}
	
	/* (non-Javadoc)
	 * @see android.opengl.GLSurfaceView#onPause()
	 */
	public void onPause() {
		Log.d(LCAT, "onPause");
	}

	/* (non-Javadoc)
	 * @see android.view.View#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent e) {
		// forward touch events to renderer
		mRenderer.handleTouch(e);
		return true;
	}

}
