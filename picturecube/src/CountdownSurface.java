import ixagon.SurfaceMapper.SuperSurface;

import java.awt.Rectangle;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import processing.core.PGraphics;
import codeanticode.glgraphics.GLTexture;


public class CountdownSurface implements ISurface {
	GLTexture texture;
	PApplet parent;
	PFont font;
	PGraphics g;
	SuperSurface ss;
	int xD, yD;
	Date end;
	static final int HOURS = 0;
	static final int MINUTES = 1;
	static final int SECONDS = 2;
	
	//Millis
	static final int X = 0;
	static final int Y = 1;
	static final int KEEPALIVE = 2;
	static final int ALX = 3;
	static final int ALY = 4;
	
	int what;
	
	static final int fontSize = 100;
	
	static final int[][] alignment = {{PConstants.LEFT, PConstants.TOP},{PConstants.CENTER, PConstants.CENTER},{PConstants.RIGHT, PConstants.BOTTOM}};
	
	int[][] millis;
	Random r;
	
	public CountdownSurface(PApplet parent, int what) {
		this.parent = parent;
		
		texture = new GLTexture(parent);
		
		font = parent.createFont("Arial", fontSize);
		
		g = parent.createGraphics(500, 500, parent.P2D);
		
		//GregorianCalendar cal = new GregorianCalendar(2013, 0, 1, 0, 0, 0);
		GregorianCalendar cal = new GregorianCalendar(2012, 11, 30, 15, 59, 00);
		end = cal.getTime();
		
		this.what = what;
		
		millis = new int[4][5];
		r = new Random();
	}

	@Override
	public void draw() {
		Date current = GregorianCalendar.getInstance().getTime();
		
		long millisBetween = end.getTime() - current.getTime();
		
		g.beginDraw();
		g.background(0);
		g.textFont(font);
		g.noStroke();
		
		long neg = millisBetween % 1000;
		if(millisBetween < 0) {
			neg = -neg;
		}
		
		//Draw Millis
		for (int i = 0; i < millis.length; i++) {
			if(millis[i][KEEPALIVE] <= 0) {
				millis[i][KEEPALIVE] = 128-r.nextInt(50);
				millis[i][X] = 50+r.nextInt(xD-100);
				millis[i][Y] = 50+r.nextInt(yD-100);
				millis[i][ALX] = alignment[r.nextInt(3)][0];
				millis[i][ALY] = alignment[r.nextInt(3)][1];
			}
			
			millis[i][KEEPALIVE] -= 2;
			
			if(millis[i][KEEPALIVE] > 0) {
				g.textSize(128-millis[i][KEEPALIVE]);
				g.fill(millis[i][KEEPALIVE]);
				g.textAlign(millis[i][ALX], millis[i][ALY]);
				g.text(""+neg, millis[i][X], millis[i][Y]);
			}
		}
		//End Millis
		
		g.textAlign(parent.CENTER, parent.CENTER);
		g.textSize(fontSize);
		g.fill(255);
		
		int t;
		String tstr;
		
		switch(what) {
			case CountdownSurface.HOURS:
				if(millisBetween < 0) {
					t = GregorianCalendar.getInstance().get(Calendar.HOUR_OF_DAY);
				} else {
					t = (int)(millisBetween / (1000 * 60 * 60)) % 24;
				}
				
				if(t < 10) {
					tstr = "0" + t;
				} else tstr = "" + t;
				
				g.text(tstr, xD/2, yD/2);
				break;
			
			case CountdownSurface.MINUTES:
				if(millisBetween < 0) {
					t = GregorianCalendar.getInstance().get(Calendar.MINUTE);
				} else {
					t = (int)(millisBetween / (1000 * 60)) % 60;
				}
				
				if(t < 10) {
					tstr = "0" + t;
				} else tstr = "" + t;
				
				g.text(tstr, xD/2, yD/2);
				break;
				
			case CountdownSurface.SECONDS:
				if(millisBetween < 0) {
					t = GregorianCalendar.getInstance().get(Calendar.SECOND);
				} else {
					t = (int)(millisBetween / (1000)) % 60;
				}
				
				if(t < 10) {
					tstr = "0" + t;
				} else tstr = "" + t;
				
				g.text(tstr, xD/2, yD/2);
				break;
		}
		
		g.endDraw();
	}

	@Override
	public int getID() {
		return ISurface.COUNTDOWN;
	}

	@Override
	public GLTexture getTexture() {
		texture.putImage(g);
		return texture;
	}

	@Override
	public void setSS(SuperSurface ss) {
		this.ss = ss;
		
		Rectangle r = MyProcessingSketch.getSSRect(ss);
		xD = r.width;
		yD = r.height;
		
		g.setSize(xD, yD);
		texture.init(xD, yD);
	}
}
