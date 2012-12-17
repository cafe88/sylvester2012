import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javax.media.opengl.GLContext;
import javax.swing.JApplet;

import processing.core.*;
import ixagon.SurfaceMapper.*;
import processing.opengl.*;
import codeanticode.glgraphics.*;

public class MyProcessingSketch extends PApplet {

	String picLocation = "\\\\CLAUDANDUS\\Users\\Cloudstar\\git\\sylvester2012\\picturecube\\src\\testPictures";
	final int SHOWN_PICTURES = 5;
	final float PIC_FADE_STEPS = 200;

	ArrayList<LayerBlend> BlendModes; // will be an arraylist of LayerBlends

	GLTexture tex;
	GLTexture[] shownPictures;
	float[] fadeSteps;
	GLTexture[] newPictures;
	GLTexture[] oldPics;
	File[] newPicturesFiles;
	GLGraphicsOffScreen glos;
	SurfaceMapper sm;
	PictureChooser picChosser;

	static public void main(String args[]) {
		PApplet.main(new String[] { "--display=1", "--present",
				"MyProcessingSketch" });
	}

	public void setup() {
		Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();
		size(scr.width, scr.height, GLConstants.GLGRAPHICS);

		BlendModes = new ArrayList<>();
		BlendModes.add(new LayerBlend(this, "Color", "BlendColor.xml"));
		BlendModes.add(new LayerBlend(this, "Luminance", "BlendLuminance.xml"));
		BlendModes.add(new LayerBlend(this, "Multiply", "BlendMultiply.xml"));
		BlendModes.add(new LayerBlend(this, "Subtract", "BlendSubtract.xml"));
		BlendModes.add(new LayerBlend(this, "Linear Dodge (Add)",
				"BlendAdd.xml"));
		BlendModes
				.add(new LayerBlend(this, "ColorDodge", "BlendColorDodge.xml"));
		BlendModes.add(new LayerBlend(this, "ColorBurn", "BlendColorBurn.xml"));
		BlendModes.add(new LayerBlend(this, "Darken", "BlendDarken.xml"));
		BlendModes.add(new LayerBlend(this, "Lighten", "BlendLighten.xml"));
		BlendModes
				.add(new LayerBlend(this, "Difference", "BlendDifference.xml"));
		BlendModes.add(new LayerBlend(this, "InverseDifference",
				"BlendInverseDifference.xml"));
		BlendModes.add(new LayerBlend(this, "Exclusion", "BlendExclusion.xml"));
		BlendModes.add(new LayerBlend(this, "Overlay", "BlendOverlay.xml"));
		BlendModes.add(new LayerBlend(this, "Screen", "BlendScreen.xml"));
		BlendModes.add(new LayerBlend(this, "HardLight", "BlendHardLight.xml"));
		BlendModes.add(new LayerBlend(this, "SoftLight", "BlendSoftLight.xml"));
		BlendModes
				.add(new LayerBlend(this,
						"Normal (Unpremultiplied, Photo Mask)",
						"BlendUnmultiplied.xml"));
		BlendModes.add(new LayerBlend(this, "Normal (Premultiplied, CG Alpha)",
				"BlendPremultiplied.xml"));

		glos = new GLGraphicsOffScreen(this, width, height, false);
		tex = new GLTexture(this, "img.jpg");

		picChosser = new PictureChooser(this, SHOWN_PICTURES, picLocation);

		fadeSteps = new float[SHOWN_PICTURES];
		oldPics = new GLTexture[SHOWN_PICTURES];
		newPictures = new GLTexture[SHOWN_PICTURES];
		newPicturesFiles = new File[SHOWN_PICTURES];

		shownPictures = new GLTexture[SHOWN_PICTURES];
		for (int i = 0; i < SHOWN_PICTURES; i++)
			shownPictures[i] = new GLTexture(this, picChosser.getFile(i)
					.getAbsolutePath());

		// Create new instance of SurfaceMapper
		sm = new SurfaceMapper(this, width, height);

	}

	public void draw() {
		background(0);
		glos.beginDraw();
		glos.clear(0);
		glos.endDraw();
		// Updates the shaking of the surfaces in render mode
		sm.shake();

		// render all surfaces in calibration mode
		if (sm.getMode() == sm.MODE_CALIBRATE)
			sm.render(glos);
		// render all surfaces in render mode
		if (sm.getMode() == sm.MODE_RENDER) {

			int i = 0;
			for (SuperSurface ss : sm.getSurfaces()) {
				if(newPicturesFiles[i]!=null && newPictures[i]==null){
					newPictures[i] = new GLTexture(this,newPicturesFiles[i].getAbsolutePath());
					newPicturesFiles[i]= null;
				}
				// fading and changing
				if (newPictures[i] != null) {
					System.out.println(BlendModes.get(16).name);
					if(fadeSteps[i] % 20 != 0){
						ss.render(glos, shownPictures[i]);
						fadeSteps[i]--;
						continue;
					}
					System.out.println("fading with! "+ (float) (fadeSteps[i] / PIC_FADE_STEPS));
					BlendModes.get(16).filter.setParameterValue("Opacity",
							(float) (fadeSteps[i] / PIC_FADE_STEPS));
				
						
					BlendModes.get(16).apply( newPictures[i],oldPics[i],
								shownPictures[i]);
						
						if (fadeSteps[i] == 0) {
							newPictures[i] = null;
							oldPics[i] = null;
							fadeSteps[i] = (float)PIC_FADE_STEPS;
						} else
							fadeSteps[i]--;
			
				}
				ss.render(glos, shownPictures[i]);
				// picChosser.randomizeFile().getAbsolutePath()));
				i++;
			}

		}
		// display the GLOS to screen
		image(glos.getTexture(), 0, 0, width, height);
	}

	public void keyPressed() {
		// create a new QUAD surface at mouse pos
		if (key == 'a')
			sm.createQuadSurface(3, mouseX, mouseY);
		// create new BEZIER surface at mouse pos
		if (key == 'z')
			sm.createBezierSurface(3, mouseX, mouseY);
		// switch between calibration and render mode
		if (key == 'c')
			sm.toggleCalibration();
		// increase subdivision of surface
		if (key == 'p') {
			for (SuperSurface ss : sm.getSelectedSurfaces()) {
				ss.increaseResolution();
			}
		}
		// deletes the selected surface
		if (key == 'd') {
			sm.removeSelectedSurfaces();
		}
		// decrease subdivision of surface
		if (key == 'o') {
			for (SuperSurface ss : sm.getSelectedSurfaces()) {
				ss.decreaseResolution();
			}
		}
		// save layout to xml
		if (key == 's')
			sm.save("bla.xml");
		// load layout from xml
		if (key == 'l')
			sm.load("bla.xml");
		// rotate how the texture is mapped in to the QUAD (clockwise)
		if (key == 'j') {
			for (SuperSurface ss : sm.getSelectedSurfaces()) {
				ss.rotateCornerPoints(0);
			}
		}
		// rotate how the texture is mapped in to the QUAD (counter clockwise)
		if (key == 'k') {
			for (SuperSurface ss : sm.getSelectedSurfaces()) {
				ss.rotateCornerPoints(1);
			}
		}
		// increase the horizontal force on a BEZIER surface
		if (key == 't') {
			for (SuperSurface ss : sm.getSelectedSurfaces()) {
				ss.increaseHorizontalForce();
			}
		}
		// decrease the horizontal force on a BEZIER surface
		if (key == 'y') {
			for (SuperSurface ss : sm.getSelectedSurfaces()) {
				ss.decreaseHorizontalForce();
			}
		}
		// increase the vertical force on a BEZIER surface
		if (key == 'g') {
			for (SuperSurface ss : sm.getSelectedSurfaces()) {
				ss.increaseVerticalForce();
			}
		}
		// decrease the vertical force on a BEZIER surface
		if (key == 'h') {
			for (SuperSurface ss : sm.getSelectedSurfaces()) {
				ss.decreaseVerticalForce();
			}
		}
	}

	public void switchPicture(int i, File file) {
		synchronized (this) {
			newPicturesFiles[i] = file;
			oldPics[i] = shownPictures[i];
		}
	}

	public void fadePictures() {

		float[] fadeSteps = new float[SHOWN_PICTURES];
		System.out.println("thread is running and shit!");
		Arrays.fill(fadeSteps, PIC_FADE_STEPS);
		while (true) {
			for (int i = 0; i < SHOWN_PICTURES; i++) {
				if (newPictures[i] != null) {
					System.out.println("fade-the bitch!! on: " + i);
					BlendModes.get(16).filter.setParameterValue("Opacity",
							(float) (fadeSteps[i] / SHOWN_PICTURES));
					synchronized (this) {
						BlendModes.get(16).apply(oldPics[i], newPictures[i],
								shownPictures[i]);
					}
					if (fadeSteps[i] == 0) {
						newPictures[i] = null;
						oldPics[i] = null;
						fadeSteps[i] = PIC_FADE_STEPS;
					} else
						fadeSteps[i]--;
				}
			}
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				return;
			}

		}
	}

	class LayerBlend {
		String name;
		GLTextureFilter filter;

		LayerBlend(PApplet Parent, String Name, String XmlFile) {
			name = Name;
			filter = new GLTextureFilter(Parent, XmlFile);
		}

		void apply(GLTexture bottomLayer, GLTexture topLayer,
				GLTexture resultLayer) {
			filter.apply(new GLTexture[] { bottomLayer, topLayer }, resultLayer); // all
																					// are
																					// called
																					// the
																					// same
																					// way
		}
	}

}