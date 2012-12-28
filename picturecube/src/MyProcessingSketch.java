import ixagon.SurfaceMapper.Point3D;
import ixagon.SurfaceMapper.SuperSurface;
import ixagon.SurfaceMapper.SurfaceMapper;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import processing.core.PApplet;
import processing.core.PGraphics;
import codeanticode.glgraphics.GLConstants;
import codeanticode.glgraphics.GLGraphicsOffScreen;
import codeanticode.glgraphics.GLTexture;

public class MyProcessingSketch extends PApplet {

	private boolean newPicTure = false;

	String picLocation = "src\\testPictures";

	ArrayList<LayerBlend> BlendModes; // will be an arraylist of LayerBlends
	ISurface[] shownTextures;
	File[] newPicturesFiles;
	PictureChooser picChosser;
	GLGraphicsOffScreen glos;
	SurfaceMapper sm;

	// starts in rendermode
	final boolean startImediatly = true;

	// index of the surfaces where we want to put a picture
	final int[] pictureSurfacesIndex = { 0, 1, 2 };

	// vars for the effects
	// --------------------------MetaBall--------------------
	final int EFFECT_INDEX_METABALL = 3;

	// -------------------------------------------------------

	static public void main(String args[]) {
		PApplet.main(new String[] { "--display=1", "--present",
				"MyProcessingSketch" });
	}

	public void setup() {
		Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();
		size(scr.width, scr.height, GLConstants.GLGRAPHICS);

		// frameRate(10);

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

		sm = new SurfaceMapper(this, width, height);

		// start the show imediatly
		if (startImediatly) {
			sm.load("bla.xml");
			sm.toggleCalibration();
			initSurfaces();
		}
	}

	public static Rectangle getSSRect(SuperSurface ss) {
		Point3D[] cP = ss.getCornerPoints();
		int xM1 = (int) ((cP[0].x + cP[3].x) / 2);
		int xM2 = (int) ((cP[1].x + cP[2].x) / 2);
		int yM1 = (int) ((cP[0].y + cP[1].y) / 2);
		int yM2 = (int) ((cP[2].y + cP[3].y) / 2);

		int xD = Math.abs(xM2 - xM1);
		int yD = Math.abs(yM2 - yM1);

		return new Rectangle(xM1, yM2, xD, yD);
	}

	public void initSurfaces() {
		int num = sm.getSurfaces().size();

		picChosser = new PictureChooser(this, num, picLocation);
		shownTextures = new ISurface[num];

		for (int i = 0; i < shownTextures.length; i++) {
			// String filePath = picChosser.randomizeFile().getAbsolutePath();
			shownTextures[i] = new PictureSurface(this, new GLTexture(this),
					new GLTexture(this), new GLTexture(this),
					BlendModes.get(16));
		}

		// shownTextures[shownTextures.length-1] = new MetaBallSurface(this);

		newPicturesFiles = new File[num];
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
			//long start = System.currentTimeMillis();
			renderSurfaces();
			//System.out.println("rendering all Surfaces takes: "
			//		+ (System.currentTimeMillis() - start));
			// metaBalldraw();
		}
		glos.getTexture().render(0, 0, width, height);
	}

	// draws all the surfaces
	private void renderSurfaces() {
		// render pictures
		updateTextures(); //schnell
		int j = 0;
		for (ISurface i : shownTextures) {
			SuperSurface sS = sm.getSurfaceById(j++);

			i.setSS(sS);
			i.draw();
			sS.render(glos, i.getTexture());
		}
	}

	private void updateTextures() {
		synchronized (newPicturesFiles) {
			if (!newPicTure)
				return;

			int i = 0;
			for (File file : newPicturesFiles) {
				if (file != null
						&& shownTextures[i].getID() == ISurface.PICTURE) {
					((PictureSurface)shownTextures[i]).setOrginImage(file.getAbsolutePath());
//					PictureSwitcher pswitch = new PictureSwitcher(this,
//							(PictureSurface) shownTextures[i],
//							file.getAbsolutePath());
//					new Thread(pswitch).start();
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
		initSurfaces();
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
		if (key == 'l') {
			sm.load("bla.xml");
			for (SuperSurface ss : sm.getSurfaces()) {
				System.out.println("Supersurfaceid: " + ss.getId());
			}
		}
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