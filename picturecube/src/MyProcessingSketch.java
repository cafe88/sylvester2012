import ixagon.SurfaceMapper.Point3D;
import ixagon.SurfaceMapper.SuperSurface;
import ixagon.SurfaceMapper.SurfaceMapper;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import antlr.collections.List;

import processing.core.PApplet;
import processing.core.PGraphics;
import codeanticode.glgraphics.GLConstants;
import codeanticode.glgraphics.GLGraphicsOffScreen;
import codeanticode.glgraphics.GLTexture;

public class MyProcessingSketch extends PApplet {

	private boolean newPicTure = false;

	String picLocation = "src\\testPictures";

	// doorbellserver Vars
	final int PORT_DOOR = 4242;
	boolean doorBellRinging = false;
	int doorBellOpacity = 0;
	int doorSteps = 20;
	int doorFadeSteps = 10;

	ArrayList<LayerBlend> BlendModes; // will be an arraylist of LayerBlends
	ISurface[] shownTextures;
	File[] newPicturesFiles;
	PictureChooser picChosser;
	GLGraphicsOffScreen glos;
	SurfaceMapper sm;

	GLTexture doorbellTexture;
	GLTexture fadeDoorbellTexture;

	// starts in rendermode
	final boolean startImediatly = true;

	// index of the surfaces where we want to put a picture
	final int[] pictureSurfacesIndex = { 0, 1, 2 };

	// vars for the effects
	// --------------------------MetaBall--------------------
	final int EFFECT_INDEX_METABALL = 3;

	// -------------------------------------------------------
	
	static final double[] arSurface = {1.12, 1.28, 1.4, 0.99, 0.88, 0.72, 0.92, 0.84, 0.6};
	
	ArrayList<PictureSurface> picSurf;

	static public void main(String args[]) {
		PApplet.main(new String[] { "--display=1", "--present",
				"MyProcessingSketch" });
	}

	public void setup() {
		Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();
		size(scr.width, scr.height, GLConstants.GLGRAPHICS);

		frameRate(20);

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

		// texture for the doorbellring
		doorbellTexture = new GLTexture(this, "data\\doorbell.jpg");
		fadeDoorbellTexture = new GLTexture(this);
		Thread door = new Thread(new DoorListener(PORT_DOOR, this));
		door.start();
		
		picSurf = new ArrayList(4);

		// start the show imediatly
		if (startImediatly) {
			sm.load("bla.xml");
			sm.toggleCalibration();
			initSurfaces();
		}
	}

	public static Rectangle getSSRect(SuperSurface ss) {
		Point3D[] cP = ss.getCornerPoints();
		
		ArrayList<Point3D> p3d = new ArrayList(4);
		p3d.add(cP[0]);
		for (int i = 1; i < cP.length; i++) {
			for (int j = 0; j < p3d.size(); j++) {
				if(cP[i].x < p3d.get(j).x) {
					p3d.add(j, cP[i]);
					break;
				}
			}
			
			if(!p3d.contains(cP[i])) {
				p3d.add(cP[i]);
			}
		}
		
		Point3D ul = p3d.get(0);
		Point3D ll = p3d.get(1);
		Point3D ur = p3d.get(2);
		Point3D lr = p3d.get(3);
		
		if(p3d.get(0).y < p3d.get(1).y) {
			ul = p3d.get(1);
			ll = p3d.get(0);
		}
		
		if(p3d.get(2).y < p3d.get(3).y) {
			ur = p3d.get(3);
			lr = p3d.get(2);
		}
		
//		int xM1 = (int) ((cP[0].x + cP[3].x) / 2);
//		int xM2 = (int) ((cP[1].x + cP[2].x) / 2);
//		int yM1 = (int) ((cP[0].y + cP[1].y) / 2);
//		int yM2 = (int) ((cP[2].y + cP[3].y) / 2);
//
//		int xD = Math.abs(xM2 - xM1);
//		int yD = Math.abs(yM2 - yM1);
		
		int xD = (int)Math.sqrt(Math.pow((ur.x - ul.x), 2) + Math.pow((ur.y - ul.y), 2));
		int yD = (int)Math.sqrt(Math.pow((ur.x - lr.x), 2) + Math.pow((ur.y - lr.y), 2));
		
		return new Rectangle((int)ul.x, (int)ul.y, xD, yD);
	}
	
	public static double getSSRealAR(SuperSurface ss) {
		return arSurface[ss.getId()];
	}

	public void initSurfaces() {
		int num = sm.getSurfaces().size();

		shownTextures = new ISurface[num];

		shownTextures[1] = new CountdownSurface(this, CountdownSurface.HOURS);
		shownTextures[5] = new CountdownSurface(this, CountdownSurface.MINUTES);
		shownTextures[6] = new CountdownSurface(this, CountdownSurface.SECONDS);
		shownTextures[0] = new FFTSurface(this);
		shownTextures[2] = new MetaBallSurface(this);
		
		int files = 0;
		
		picSurf.clear();
		
		for (int i = 0; i < shownTextures.length; i++) {
			// String filePath = picChosser.randomizeFile().getAbsolutePath();
			if(shownTextures[i] == null) {
				shownTextures[i] = new PictureSurface(this, new GLTexture(this),
						new GLTexture(this), new GLTexture(this),
						BlendModes.get(16));
				picSurf.add((PictureSurface)shownTextures[i]);
				files++;
			}	
		}
		
		picChosser = new PictureChooser(this, files, picLocation);

		newPicturesFiles = new File[files];
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
			// long start = System.currentTimeMillis();
			renderSurfaces();
			// System.out.println("rendering all Surfaces takes: "
			// + (System.currentTimeMillis() - start));
			// metaBalldraw();
		}
		glos.getTexture().render(0, 0, width, height);
	}

	// draws all the surfaces
	private void renderSurfaces() {
		// render pictures
		updateTextures(); // schnell
		int j = 0;
		for (ISurface i : shownTextures) {
			SuperSurface sS = sm.getSurfaceById(j++);

			i.setSS(sS);
			i.draw();

			synchronized (doorbellTexture) {
				// if you dont want to fade the surface when the bell is ringing
				// put an if i ==shouldNotRing or something like that and render it directly
				sS.render(glos, doorBellFade(i));
			}
		}
	}

	private GLTexture doorBellFade(ISurface surfaceTexture) {
		if(!doorBellRinging){
			if(doorBellOpacity <=0) return surfaceTexture.getTexture();
			doorBellOpacity--;
		}else{
			if(doorBellOpacity >=(doorSteps*doorFadeSteps)) return doorbellTexture;
			doorBellOpacity++;
		}
		int i = doorBellOpacity/doorSteps;
		float o=(float)(1f/doorFadeSteps*i);
		BlendModes.get(16).filter.setParameterValue("Opacity",
				o);
		BlendModes.get(16).apply(surfaceTexture.getTexture(),doorbellTexture,
				 fadeDoorbellTexture);
		return fadeDoorbellTexture;
	}

	private void updateTextures() {
		synchronized (newPicturesFiles) {
			if (!newPicTure)
				return;

			int i = 0;
			for (File file : newPicturesFiles) {
				if (file != null) {
					picSurf.get(i).setOrginImage(file.getAbsolutePath());
					// PictureSwitcher pswitch = new PictureSwitcher(this,
					// (PictureSurface) shownTextures[i],
					// file.getAbsolutePath());
					// new Thread(pswitch).start();
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
		if (key == 'c') {
			sm.toggleCalibration();
			initSurfaces();}
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
			{
			sm.load("bla.xml");
			for (SuperSurface ss : sm.getSurfaces()) {
				System.out.println("Supersurfaceid: " + ss.getId());
			}
			initSurfaces();
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

	public void setDoorBell(boolean door) {
		synchronized (doorbellTexture) {
			doorBellRinging = door;
		}
	}
}
