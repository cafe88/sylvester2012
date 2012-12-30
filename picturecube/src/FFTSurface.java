import ixagon.SurfaceMapper.SuperSurface;

import java.awt.Rectangle;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import codeanticode.glgraphics.GLTexture;
import ddf.minim.AudioInput;
import ddf.minim.Minim;
import ddf.minim.analysis.BartlettHannWindow;
import ddf.minim.analysis.FFT;


public class FFTSurface implements ISurface {
	GLTexture texture;
	PApplet parent;
	PFont font;
	PGraphics g;
	SuperSurface ss;
	int xD, yD;
	
	Minim m;
	AudioInput in;
	FFT fft;
	
	public FFTSurface(PApplet parent) {
		this.parent = parent;
		
		texture = new GLTexture(parent);
		
		font = parent.createFont("Arial", 32);
		
		g = parent.createGraphics(500, 500, parent.P2D);
		
		m = new Minim(parent);
		
		Mixer.Info[] bla = AudioSystem.getMixerInfo();
		
		Mixer mixer = null;
		
		for (int i = 0; i < bla.length; i++) {
			if(bla[i].getName().contains("Eingang")) {
				mixer = AudioSystem.getMixer(bla[i]);
				break;
			}
		}
		
		if(mixer != null) {
			m.setInputMixer(mixer);
		} else throw new RuntimeException("BIG FAIL");
		
		in = m.getLineIn(Minim.STEREO);
		
		fft = new FFT(in.bufferSize(), in.sampleRate());
		//fft.linAverages(32);
		fft.logAverages(50, 5);
		//fft.window(new BartlettHannWindow());
	}

	@Override
	public void draw() {
		g.beginDraw();
		g.background(0);
		g.textFont(font);
		g.stroke(255);
		g.rectMode(parent.CORNERS);
		
		g.textAlign(parent.CENTER, parent.CENTER);
		g.textSize(70);
		g.fill(145, 85, 136);
		
		fft.forward(in.mix);
		
		int bla = 200;
		
		for(int i = 0; i < in.bufferSize() - 1; i+=1) {
			float left = 250-(1+in.left.get(i))*250+50;
			g.stroke(left, (int)((left-50)*0.5), 0);
			//for (int j = 0; j < 10; j++) {
				g.rect(i, yD/2+in.left.get(i)*bla+10, i+1, yD/2+in.left.get(i+1)*bla+10);
			//}
			float right = (in.right.get(i))*250+50;
			g.stroke((int)((right-50)*1.5), right, 0);
			//for (int j = 0; j < 10; j++) {
				g.rect(i, yD-yD/3+in.right.get(i)*bla+10, i+1, yD-yD/3+in.right.get(i+1)*bla+10);
			//}
		}
		
		g.stroke(0);
		int w = (int)(xD/40);
		for(int i = 0; i < fft.avgSize(); i++)
		{
		  g.rect(i*w, yD, i*w + w, (int)(yD - fft.getAvg(i)*(1+0.2*i)), 5, 5, 5, 5);
		}
		
		g.endDraw();
	}

	@Override
	public int getID() {
		return ISurface.FFT;
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
