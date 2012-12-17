import java.io.File;

import processing.core.PApplet;
import codeanticode.glgraphics.GLTexture;

class UpdateStruct {

	public GLTexture from;
	public GLTexture to;
	boolean locked = true;
	boolean alreadyShown= false;
	boolean lastFade = false;

	public UpdateStruct(PApplet parent, File from, File to) {
		this.from = new GLTexture(parent, from.getAbsolutePath());
		this.to = new GLTexture(parent, to.getAbsolutePath());

	}

	public void lock() {
		synchronized (this) {
			locked = true;
		}
	}

	public void unlock() {
		synchronized (this) {
			locked = false;
		}
	}

	public boolean isLocked() {
		synchronized (this) {
			return locked;
		}
	}
	public  void alreadyShown(){
		synchronized (this) {
			alreadyShown = true;
		}
	}
	public void fade(int step) {
		alreadyShown = false;
		unlock();
		lastFade = true;
	}
}

public class PictureFader  implements Runnable {

	PApplet parent;
	PictureChooser chooser;
	UpdateStruct[] textures;
	long fadeTime = 0;

	public PictureFader(PApplet parent, int picCount, long fadeTime) {
		this.parent = parent;
		this.chooser = chooser;
		textures = new UpdateStruct[picCount];
		this.fadeTime = fadeTime;

		Thread t = new Thread(this);
		t.start();
	}

	public GLTexture hasNewTex(int index) {
		if (textures[index] != null && !textures[index].isLocked()){
			UpdateStruct buff = textures[index];
			if(textures[index].lastFade)textures[index] = null;
			return buff.to;
			}
		return null;
	}

	public void fadePictures(int index, File from, File to) {
		// mal gucken ob ich das brauche
		if (textures[index] != null) {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				return;
			}
		}
		textures[index] = new UpdateStruct(parent, from, to);
	}

	@Override
	public void run() {
		while (true) {
			for (UpdateStruct texture : textures) {
				if (texture == null)
					continue;
				
				texture.fade(2);
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				return;
			}
		}
	}

}
