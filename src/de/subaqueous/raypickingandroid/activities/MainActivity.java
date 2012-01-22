package de.subaqueous.raypickingandroid.activities;

import de.subaqueous.raypickingandroid.R;
import de.subaqueous.raypickingandroid.fragments.RayPickerFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

/**
 * Main Avtivity class for this sample application
 * @author nd
 *
 */
public class MainActivity extends FragmentActivity {
	
    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        if (savedInstanceState == null) {
            // Do first time initialization -- add the RayPicker fragment.
            Fragment newFragment = RayPickerFragment.newInstance();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.fragment_placeholder, newFragment).commit();
        }
    }
     
}