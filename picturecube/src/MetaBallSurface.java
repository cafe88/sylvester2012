import ixagon.SurfaceMapper.SuperSurface;

import java.awt.Rectangle;
import java.util.Random;

import processing.core.PApplet;
import processing.core.PGraphics;
import codeanticode.glgraphics.GLTexture;

public class MetaBallSurface implements ISurface {
	GLTexture metaBallTex;
	final int numBlobs = 2;

	int[] blogPx;
	int[] blogPy;

	// Movement vector for each blob
	int[] blogDx;
	int[] blogDy;

	PGraphics pgMetaBall;
	int[][] vy, vx;
	
	PApplet parent;
	SuperSurface ss;
	
	public MetaBallSurface(PApplet parent) {
		this.parent = parent;
		
		metaBallTex = new GLTexture(parent);
	}

	@Override
	public void draw() {
		if(ss != null) {
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
					vx[i][x] = (int) (parent.sq(blogPx[i] - x));
				}
	
				for (int y = 0; y < pgMetaBall.height; y++) {
					vy[i][y] = (int) (parent.sq(blogPy[i] - y));
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
						m += 25000 / (vy[i][y] + vx[i][x] + 1);
					}
					pgMetaBall.pixels[x + y * pgMetaBall.width] = parent.color(2*m, m + x,
							(x + m + y) / 2);
				}
			}
			//pgMetaBall.updatePixels();
			pgMetaBall.endDraw();
	
			// Display the results
			// image(pg, 0, 0, width, height);
			metaBallTex.putImage(pgMetaBall);
		}	
	}

	@Override
	public int getID() {
		return ISurface.METABALLS;
	}

	@Override
	public GLTexture getTexture() {
		return metaBallTex;
	}

	@Override
	public void setSS(SuperSurface ss) {
		if(this.ss != ss) {
			this.ss = ss;
			
			Rectangle r = MyProcessingSketch.getSSRect(ss);
			int xD = r.width;
			int yD = r.height;
			
			pgMetaBall = parent.createGraphics(xD/4, yD/4, parent.P2D);
			// blob position
			blogPx = new int[numBlobs];
			blogPy = new int[numBlobs];
			// movement vector
			blogDx = new int[numBlobs];
			blogDy = new int[numBlobs];
			Random rand = new Random();
			for (int i = 0; i < numBlobs; i++) {
				blogDx[i] = 1;
				blogDy[i] = 1;
				blogPx[i] = (int) (rand.nextDouble() * pgMetaBall.width);
				blogPy[i] = (int) (rand.nextDouble() * pgMetaBall.height);
			}

			vy = new int[numBlobs][pgMetaBall.height];
			vx = new int[numBlobs][pgMetaBall.width];
		}	
	}
}
