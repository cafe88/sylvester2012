import java.io.File;
import java.util.ArrayList;

import codeanticode.glgraphics.GLTexture;

public class PictureChooser implements Runnable {

	ArrayList<GLTexture> pictures;
	int picCount;
	File filepath;

	/**
	 * Inits the Object.
	 * 
	 * @param picCount
	 *            how many pictures should be choosen
	 * @param location
	 *            the locattion fro wich the pictures will be choosen
	 */
	public PictureChooser(int picCount, String location) {
		this.picCount = picCount;
		pictures = new ArrayList<>();
		try {
			filepath = new File(location);
			if (!filepath.exists() || !filepath.canRead())
				throw new Exception();
		} catch (Exception e) {
			System.err.println("Files can not be loaded");
			e.printStackTrace();
		}
	}

	@Override
	public void run() {

	}

	public static void main(String[] args) {
		PictureChooser test = new PictureChooser(5, "\\\\CLAUDANDUS\\Games");
	}
}
