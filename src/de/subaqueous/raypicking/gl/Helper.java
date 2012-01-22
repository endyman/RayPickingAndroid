package de.subaqueous.raypicking.gl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Helper {
	
	/**
	 * convert degrees to radians based on ios code from: http://blog.nova-box.com/2010/05/iphone-ray-picking-glunproject-sample.html
	 * @param deg
	 * @return
	 */
	public static double degToRadians(float deg){
		return deg/ 180.0 * Math.PI;	
	}
	
	/**
	 * create a Floatbuffer for a given Array
	 * @param array
	 * @return
	 */
	public static FloatBuffer createFloatBuffer(float[] array)
	{
		final int floatSize = Float.SIZE / 8;
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(array.length * floatSize);
		byteBuffer.order(ByteOrder.nativeOrder());
		FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
		floatBuffer.put(array);
		floatBuffer.position(0);
		return floatBuffer;
	}
	
	/**
	 * Convert the 4D input into 3D space (or something like that, otherwise the gluUnproject values are incorrect)
	 * @param v 4D input
	 * @return 3D output
	 * @author http://stackoverflow.com/users/1029225/mh
	 */
	public static float[] fixW(float[] v) { 
	    float w = v[3];
	    for(int i = 0; i < 4; i++) 
	        v[i] = v[i] / w;
	    return v;
	}
	
	/**
	 * check if a given point in space collides with a given object center  with a given radius
	 * based on ios code from: http://blog.nova-box.com/2010/05/iphone-ray-picking-glunproject-sample.html
	 * @param point point to check for collision with object x,y,z
	 * @param center center of the object x,y,z
	 * @param radius
	 * @return true on collision, false on no collision
	 */
	public static Boolean poinSphereCollision(float [] point, float[] center, float radius){
		
		return ( (point[0] - center[0]) * (point[0] - center[0]) +
				(point[1] - center[1]) * (point[1] - center[1]) +
				(point[2] - center[2]) * (point[2] - center[2]) < (radius * radius));
			}
	
}
