import processing.core.PApplet;
import processing.core.PImage;
import codeanticode.glgraphics.GLGraphicsOffScreen;
import codeanticode.glgraphics.GLTexture;
import codeanticode.glgraphics.GLTextureFilter;

public class TextureSurface {

	GLTexture orginTexture;
	GLTexture oldTexture;
	GLTexture shownTexture;
	LayerBlend filter;

	float fadeSteps;
	float drawCounter;
	float drawCounterSteps;
	TextureSurface fadeFrom;
	
	PApplet parent;

	public TextureSurface(PApplet parent, GLTexture orginTexture, GLTexture oldTexture,
			GLTexture shownTexture, LayerBlend filter) {
		this.orginTexture = orginTexture;
		this.shownTexture = shownTexture;
		this.oldTexture = oldTexture;
		
		this.filter = filter;
		fadeSteps = 10;
		drawCounterSteps = 10;
		drawCounter = 0;
		this.parent = parent;
	}

	public TextureSurface(PApplet parent, GLTexture shownTexture, GLTexture orginTexture,
			LayerBlend filter) {
		this.shownTexture = shownTexture;
		this.orginTexture = orginTexture;
		this.filter = filter;
		oldTexture = null;
		fadeSteps = 10;
		drawCounterSteps = 50;
		drawCounter = 0;
		this.parent = parent;
	}

	public void draw() {
		if (drawCounter < drawCounterSteps * fadeSteps) {
			drawCounter++;

			if (oldTexture != null && orginTexture != null) {
				//System.out.println("drawcounter: "+drawCounter+" drawcountersteps: "+ drawCounterSteps+" scheise modulo: "+
				//		drawCounter % drawCounterSteps);
				//if (drawCounter % drawCounterSteps == 0) {
					//System.out.println("its fading!!");
					float f = (float) (drawCounter / drawCounterSteps / fadeSteps);
					filter.filter.setParameterValue("Opacity", f);
					
					//CROPPING
					int sy = (int)((shownTexture.height - 600) * f);
					filter.filter.setCrop(0, sy, shownTexture.width, 600);
					
					
					//System.out.println(f);
					// filter.filter.setParameterValue("Opacity", 0.4f);
					filter.apply(oldTexture, orginTexture, shownTexture);
				//}

			}
		}

	}

	public GLTexture getTexture() {
		return shownTexture;
	}
	
	public void setOldTexture(GLTexture oldTexture) {
		this.oldTexture = oldTexture;
	}

	public void setOrginTexture(GLTexture orginTexture) {
		this.orginTexture = orginTexture;
	}

	public void resetCounter() {
		drawCounter = 0;
	}

	/**
	 * @return the orginTexture
	 */
	public GLTexture getOrginTexture() {
		return orginTexture;
	}

	/**
	 * @return the oldTexture
	 */
	public GLTexture getOldTexture() {
		return oldTexture;
	}
}
