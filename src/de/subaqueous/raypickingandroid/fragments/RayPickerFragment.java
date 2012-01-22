package de.subaqueous.raypickingandroid.fragments;

import de.subaqueous.raypicking.gl.RayPickerSurfaceView;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RayPickerFragment extends Fragment {

    private GLSurfaceView mGLSurfaceView;

    /**
     * Create a new instance of RayPickerFragment
     * a
     */
    public static RayPickerFragment newInstance() {
    	RayPickerFragment f = new RayPickerFragment();

        Bundle args = new Bundle();
        f.setArguments(args);
        return f;
    }

    /**
     * When creating
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    /**
     * The Fragment's UI 
     * 
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    		mGLSurfaceView = new RayPickerSurfaceView(getActivity());
    		mGLSurfaceView.requestFocus();
    		mGLSurfaceView.setFocusableInTouchMode(true);
        return mGLSurfaceView;
    }
    
    @Override
    public void onPause() {
    	mGLSurfaceView.onPause();
    	super.onPause();
    }
}
