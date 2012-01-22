package de.subaqueous.raypicking.gl;

import javax.microedition.khronos.opengles.*;

import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.lang.Integer;

import android.opengl.GLES10;
import android.opengl.GLUtils;

/**
 * convenience class to deal with texture loading based on blog post by Tasos Kleisas: http://tkcodesharing.blogspot.com/2008/05/working-with-textures-in-androids.html
 * @author Tasos Kleisas 
 *
 */
public class GLTextures {
	public GLTextures(GL10 gl, Context context) {
		this.gl = gl;
		this.context = context;
		this.textureMap = new java.util.HashMap<Integer, Integer>();
	}

	public void loadTextures() {
		int[] tmp_tex = new int[textureFiles.length];
		gl.glGenTextures(textureFiles.length, tmp_tex, 0);
		textures = tmp_tex;
		for (int i = 0; i < textureFiles.length; i++) {
			Bitmap bmp = BitmapFactory.decodeResource(context.getResources(),
					textureFiles[i]);
			this.textureMap.put(new Integer(textureFiles[i]), new Integer(i));
			int tex = tmp_tex[i];

			gl.glBindTexture(GL10.GL_TEXTURE_2D, tex);
			gl.glTexEnvf(GLES10.GL_TEXTURE_ENV, GLES10.GL_TEXTURE_ENV_MODE, GLES10.GL_MODULATE);
			GLUtils.texImage2D(GLES10.GL_TEXTURE_2D, 0, bmp, 0);
			bmp.recycle();
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
					GL10.GL_LINEAR);
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
					GL10.GL_LINEAR);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T
							,GL10.GL_CLAMP_TO_EDGE);
			gl.glEnable(GL10.GL_TEXTURE_2D);	

		}
	}

	public void setTexture(int id) {
		try {
			int textureid = this.textureMap.get(new Integer(id)).intValue();
			gl.glBindTexture(GL10.GL_TEXTURE_2D, this.textures[textureid]);

		} catch (Exception e) {
			return;
		}
	}
	
	public int getTextureIdforResource(int id) {
		int textureid = this.textureMap.get(new Integer(id)).intValue();
		return this.textures[textureid];
	}

	public void add(int resource) {
		if (textureFiles == null) {
			textureFiles = new int[1];
			textureFiles[0] = resource;
		} else {
			int[] newarray = new int[textureFiles.length + 1];
			for (int i = 0; i < textureFiles.length; i++) {
				newarray[i] = textureFiles[i];
			}
			newarray[textureFiles.length] = resource;
			textureFiles = newarray;
		}  
	}

	private java.util.HashMap<Integer, Integer> textureMap;
	private int[] textureFiles;
	private GL10 gl;
	private Context context;
	private int[] textures;
}