import processing.core.PApplet;
import processing.core.PImage;
import codeanticode.glgraphics.GLTexture;

public class PictureSwitcher implements Runnable {
	PApplet parent;
	PictureSurface ss;
	String file;
	
	public PictureSwitcher(PApplet parent, PictureSurface ss, String file) {
		this.parent = parent;
		this.ss = ss;
		this.file = file;
	}
	
	@Override
	public void run() {
		//ss.setOldTexture(ss.getOrginTexture());
		ss.setOrginImage(file);
		//ss.setOrginTexture(new GLTexture(parent, file));
		//ss.resetCounter();
	}

}
