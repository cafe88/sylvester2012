import ixagon.SurfaceMapper.Point3D;
import ixagon.SurfaceMapper.SuperSurface;
import processing.core.PApplet;
import processing.core.PImage;
import codeanticode.glgraphics.GLTexture;
import codeanticode.glgraphics.GLTextureFilter;

public class TextureSurface {

	GLTexture orginTexture;
	GLTexture oldTexture;
	GLTexture shownTexture;
	
	GLTexture lastCropped;
	GLTexture nextCropped;
	
	SuperSurface ss;
	
	LayerBlend filter;

	float fadeSteps;
	float fadeCounter;
	float drawCounterSteps;
	TextureSurface fadeFrom;
	boolean picFullyShown = false;
	long timeWhenFadingStarted = 0;
	long timeWhenFadingEnded = 0;
	long slideMillis = 0;
	
	PImage newImg;
	boolean updateTex = false;
	
	GLTextureFilter crop;
	
	PApplet parent;

	public TextureSurface(PApplet parent, GLTexture orginTexture, GLTexture oldTexture,
			GLTexture shownTexture, LayerBlend filter) {
		this.orginTexture = orginTexture;
		this.shownTexture = shownTexture;
		this.oldTexture = oldTexture;
		
		this.nextCropped = new GLTexture(parent);
		this.lastCropped = new GLTexture(parent);
		
		crop = new GLTextureFilter(parent, "BlendColor.xml");
		
		this.filter = filter;
		fadeSteps = 10;
		drawCounterSteps = 10;
		fadeCounter = 0;
		this.parent = parent;
	}

	public TextureSurface(PApplet parent, GLTexture shownTexture, GLTexture orginTexture,
			LayerBlend filter) {
		this.shownTexture = shownTexture;
		this.orginTexture = orginTexture;
		
		this.nextCropped = new GLTexture(parent);
		this.lastCropped = new GLTexture(parent);
		
		this.filter = filter;
		oldTexture = null;
		fadeSteps = 10;
		drawCounterSteps = 50;
		fadeCounter = 0;
		this.parent = parent;
	}

	public void draw() {
		if (oldTexture != null && newImg != null && ss != null) {
			if(updateTex) {
				updateTex = false;
				oldTexture.copy(orginTexture);
				orginTexture.putImage(newImg);
				resetCounter();
			}	
			//System.out.println("drawcounter: "+drawCounter+" drawcountersteps: "+ drawCounterSteps+" scheise modulo: "+
			//		drawCounter % drawCounterSteps);
			//if (drawCounter % drawCounterSteps == 0) {
				//System.out.println("its fading!!");
			
				if(picFullyShown) {
					long timeLeft = PictureChooser.PIC_SHOW_TIME - (timeWhenFadingEnded-timeWhenFadingStarted);
					if((timeWhenFadingEnded-timeWhenFadingStarted) < 0) {
						timeLeft = PictureChooser.PIC_SHOW_TIME;
						slideMillis = 0;
					}
					timeLeft -= 1500;
					float slideY = (float)slideMillis / (float)timeLeft;
					if(slideY >= 0.0 && slideY <= 1.0) {						
						Point3D[] cP = ss.getCornerPoints();
						int xM1 = (int) ((cP[0].x + cP[3].x) / 2);
						int xM2 = (int) ((cP[1].x + cP[2].x) / 2);
						int yM1 = (int) ((cP[0].y + cP[1].y) / 2);
						int yM2 = (int) ((cP[2].y + cP[3].y) / 2);
						
						int xD = Math.abs(xM2 - xM1);
						int yD = Math.abs(yM2 - yM1);
						
						shownTexture.init(xD, yD);
						
						float aspectRatioSS = ((float)xD / (float)yD);
						float aspectRatioOld = ((float)oldTexture.width / (float)oldTexture.height);
						float aspectRatioOrgin = ((float)orginTexture.width / (float)orginTexture.height);
						
						float logisticSlide = (float)1.02 / (1 + (float)Math.pow(Math.E, (1.02*(-10)*(slideY-0.5))));
						if(logisticSlide > 1.0) logisticSlide = 1;
						
						if(aspectRatioSS > aspectRatioOld) {
							int heightOld = (int)(oldTexture.width / aspectRatioSS);
							
							//CROPPING -> SLIDING
							int syOld = oldTexture.height - heightOld;
							if(fadeCounter > 0)	syOld = (int)((oldTexture.height - heightOld) * logisticSlide);
							
							crop.setCrop(0, syOld, oldTexture.width, heightOld);
							oldTexture.filter(crop, lastCropped);
						} else {
							int widthOld = (int)(oldTexture.height * aspectRatioSS);
							
							//CROPPING -> SLIDING
							int sxOld = oldTexture.width - widthOld;
							if(fadeCounter > 0)	sxOld = (int)((oldTexture.width - widthOld) * logisticSlide);
							
							crop.setCrop(sxOld, 0, widthOld, oldTexture.height);
							oldTexture.filter(crop, lastCropped);
						}
						
						if(aspectRatioSS > aspectRatioOrgin) {						
							int heightOrgin = (int)(orginTexture.width / aspectRatioSS);
							
							//CROPPING -> SLIDING
							int syOrgin = (int)((orginTexture.height - heightOrgin) * logisticSlide);
							
							crop.setCrop(0, syOrgin, orginTexture.width, heightOrgin);
							orginTexture.filter(crop, nextCropped);
						} else {
							int widthOrgin = (int)(orginTexture.height * aspectRatioSS);
							
							//CROPPING -> SLIDING							
							int sxOrgin = (int)((orginTexture.width - widthOrgin) * logisticSlide);
							
							crop.setCrop(sxOrgin, 0, widthOrgin, orginTexture.height);
							orginTexture.filter(crop, nextCropped);
						}
					}	
				}
			
				float f = (float) (fadeCounter / drawCounterSteps / fadeSteps);
				filter.filter.setParameterValue("Opacity", f);
				filter.apply(lastCropped, nextCropped, shownTexture);
			//}
		}
		
		if (fadeCounter < drawCounterSteps * fadeSteps) {
			fadeCounter++;
			picFullyShown = false;
		} else {
			slideMillis = System.currentTimeMillis() - timeWhenFadingEnded;
			if(picFullyShown == false) timeWhenFadingEnded = System.currentTimeMillis();
			picFullyShown = true;
		}
	}
	
	public void setSS(SuperSurface ss) {
		this.ss = ss;
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
	
	public void setOrginImage(String file) {
		newImg = parent.loadImage(file);
		updateTex = true;
	}

	public void resetCounter() {
		fadeCounter = 0;
		timeWhenFadingStarted = System.currentTimeMillis();
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
