import ixagon.SurfaceMapper.SuperSurface;
import codeanticode.glgraphics.GLTexture;

public interface ISurface {
	public static final int PICTURE = 0;
	public static final int METABALLS = 1;
	public static final int COUNTDOWN = 10;
	
	public void draw();
	public int getID();
	public GLTexture getTexture();
	public void setSS(SuperSurface ss);
}
