import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.media.opengl.GLContext;
import javax.swing.JApplet;

import processing.core.*;
import ixagon.SurfaceMapper.*;
import processing.opengl.*;
import codeanticode.glgraphics.*;

public class MyProcessingSketch extends PApplet {

	String picLocation = "\\\\CLAUDANDUS\\Users\\Cloudstar\\git\\sylvester2012\\picturecube\\src\\testPictures";
	final int SHOWN_PICTURES = 5;

	ArrayList<LayerBlend> BlendModes; // will be an arraylist of LayerBlends

	
	TextureSurface[] shownTextures;

	File[] newPicturesFiles;
	GLTexture bouncingBalls;
	
	GLGraphicsOffScreen glos;
	SurfaceMapper sm;
	PictureChooser picChosser;
	private boolean newPicTure = false;

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

		picChosser = new PictureChooser(this, SHOWN_PICTURES, picLocation);

		

		shownTextures = new TextureSurface[SHOWN_PICTURES];

		for (int i = 0; i < shownTextures.length; i++) {
			String filePath = picChosser.randomizeFile().getAbsolutePath();
			shownTextures[i] = new TextureSurface(
					new GLTexture(this, filePath),
					new GLTexture(this, filePath),
					new GLTexture(this, filePath), BlendModes.get(16));
		}

		bouncingBalls = new GLTexture(this, 640	, 360);
		
		newPicturesFiles = new File[SHOWN_PICTURES];
		// Create new instance of SurfaceMapper
		sm = new SurfaceMapper(this, width, height);

		picChosser.runChooser();

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
				if(i==0){
					bouncingBalls.
					
					ss.render(glos,bouncingBalls);
					i++;
					continue;
				}
				
				
				
				updateTextures();
				shownTextures[i].draw();
				ss.render(glos, shownTextures[i].getTexture());

				// picChosser.randomizeFile().getAbsolutePath()));
				i++;
			}

		}
		// display the GLOS to screen
		image(glos.getTexture(), 0, 0, width, height);
	}

	private void updateTextures() {
		if (!newPicTure)
			return;
		synchronized (newPicturesFiles) {
			int i = 0;
			for (File file : newPicturesFiles) {
				if (file != null) {
					shownTextures[i].setOldTexture(shownTextures[i]
							.getOrginTexture());
					shownTextures[i].setOrginTexture(new GLTexture(this, file.getAbsolutePath()));
					shownTextures[i].resetCounter();
					newPicturesFiles[i] = null;
				}
				i++;
			}
			newPicTure = false;
		}
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
		synchronized (newPicturesFiles) {
			newPicturesFiles[i] = file;
			newPicTure = true;
		}
	}
}