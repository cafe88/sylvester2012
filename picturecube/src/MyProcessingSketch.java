import ixagon.SurfaceMapper.SuperSurface;
import ixagon.SurfaceMapper.SurfaceMapper;

import java.awt.Dimension;
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
	TextureSurface[] shownTextures;
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
	GLTexture metaBallTex;
	final int numBlobs = 5;

	int[] blogPx;
	int[] blogPy;

	// Movement vector for each blob
	int[] blogDx;
	int[] blogDy;

	PGraphics pgMetaBall;
	int[][] vy, vx;

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
		picChosser = new PictureChooser(this, pictureSurfacesIndex.length,
				picLocation);
		shownTextures = new TextureSurface[pictureSurfacesIndex.length];

		for (int i = 0; i < shownTextures.length; i++) {
			String filePath = picChosser.randomizeFile().getAbsolutePath();
			shownTextures[i] = new TextureSurface(this, new GLTexture(this,
					filePath), new GLTexture(this, filePath), new GLTexture(
					this), BlendModes.get(16));
		}

		newPicturesFiles = new File[pictureSurfacesIndex.length];
		sm = new SurfaceMapper(this, width, height);

		picChosser.runChooser();

		// start the show imediatly
		if (startImediatly) {
			sm.load("bla.xml");
			sm.toggleCalibration();
		}

		// vars which will be used for effects

		// ----MetaBall-------
		metaBallTex = new GLTexture(this);
		pgMetaBall = createGraphics(160, 90, P2D);
		// blob position
		blogPx = new int[numBlobs];
		blogPy = new int[numBlobs];
		// movement vector
		blogDx = new int[numBlobs];
		blogDy = new int[numBlobs];
		Random r = new Random();
		for (int i = 0; i < numBlobs; i++) {
			blogDx[i] = 1;
			blogDy[i] = 1;
			blogPx[i] = (int) (r.nextDouble() * pgMetaBall.width);
			blogPy[i] = (int) (r.nextDouble() * pgMetaBall.height);
		}

		vy = new int[numBlobs][pgMetaBall.height];
		vx = new int[numBlobs][pgMetaBall.width];
		// ----MetaBall--------
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
			renderSurfaces();
			metaBalldraw();
		}
		glos.getTexture().render(0, 0, width, height);
	}

	// draws all the surfaces
	private void renderSurfaces() {
		// render pictures
		updateTextures();
		int j = 0;
		for (int i : pictureSurfacesIndex) {
			SuperSurface sS = sm.getSurfaceById(i);
			shownTextures[j].setSS(sS);
			shownTextures[j].draw();
			sS.render(glos, shownTextures[j].getTexture());
			j++;
		}
	}

	private void updateTextures() {
		if (!newPicTure)
			return;
		synchronized (newPicturesFiles) {
			int i = 0;
			for (File file : newPicturesFiles) {
				if (file != null) {
					PictureSwitcher pswitch = new PictureSwitcher(this,
							shownTextures[i], file.getAbsolutePath());
					new Thread(pswitch).start();
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
		for (SuperSurface ss : sm.getSurfaces()) {
			System.out.println("Supersurfaceid: " + ss.getId());
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

	//draw routin for the metaballs
	private void metaBalldraw() {
		for (int i = 0; i < numBlobs; ++i) {
			blogPx[i] += blogDx[i];
			blogPy[i] += blogDy[i];

			// bounce across screen
			if (blogPx[i] < 0) {
				blogDx[i] = 1;
			}
			if (blogPx[i] > pgMetaBall.width) {
				blogDx[i] = -1;
			}
			if (blogPy[i] < 0) {
				blogDy[i] = 1;
			}
			if (blogPy[i] > pgMetaBall.height) {
				blogDy[i] = -1;
			}

			for (int x = 0; x < pgMetaBall.width; x++) {
				vx[i][x] = (int) (sq(blogPx[i] - x));
			}

			for (int y = 0; y < pgMetaBall.height; y++) {
				vy[i][y] = (int) (sq(blogPy[i] - y));
			}
		}

		// Output into a buffered image for reuse
		pgMetaBall.beginDraw();
		pgMetaBall.loadPixels();
		for (int y = 0; y < pgMetaBall.height; y++) {
			for (int x = 0; x < pgMetaBall.width; x++) {
				int m = 30;
				for (int i = 0; i < numBlobs; i++) {
					// Increase this number to make your blobs bigger
					m += 10000 / (vy[i][y] + vx[i][x] + 1);
				}
				pgMetaBall.pixels[x + y * pgMetaBall.width] = color(0, m + x,
						(x + m + y) / 2);
			}
		}
		pgMetaBall.updatePixels();
		pgMetaBall.endDraw();

		// Display the results
		// image(pg, 0, 0, width, height);
		metaBallTex.putImage(pgMetaBall);
		sm.getSurfaceById(EFFECT_INDEX_METABALL).render(glos, metaBallTex);
	}
}