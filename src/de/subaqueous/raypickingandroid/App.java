package de.subaqueous.raypickingandroid;

import android.app.Application;

/**
 * extended Application Class including convenience methods
 * @author Nils Domrose
 *
 */
public class App extends Application {
	
	private static App instance;
	
	/* (non-Javadoc)
	 * @see android.app.Application#onCreate()
	 */
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		instance = this;
	}
	
	/**
	 * get the instance of this application
	 * @return {@link App} - instance of this application
	 */
	public static App getInstance(){
		return instance;
	}


}
