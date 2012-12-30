import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.tools.ant.taskdefs.Sleep;

import processing.core.PApplet;

import codeanticode.glgraphics.GLTexture;

public class PictureChooser implements Runnable {

	ArrayList<File> files;
	long startTime[] = {4000,8000,12000,16000,20000};
	File[] showList;
	UpdateFilelist filelList;
	MyProcessingSketch parent;
	int picCount;
	public static final long PIC_SHOW_TIME = 18000;
	final long PIC_FADE_TIME = 2000;
	final long SLEEP_TIME = 200;

	/**
	 * Inits the Object.
	 * 
	 * @param picCount
	 *            how many pictures should be choosen
	 * @param location
	 *            the location from wich the pictures will be choosen
	 */
	public PictureChooser(MyProcessingSketch parent, int picCount, String location) {
		this.parent = parent;
		this.picCount = picCount;
		showList = new File[picCount];
		files = new ArrayList<>();

		try {
			filelList = new UpdateFilelist(files, location);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < picCount; i++) {
			File random = randomizeFile();
			showList[i] = random;
		}

	
	}
/**
 * should maybe check if we already have one pi
 * 
 * cture in the show array
 * @return
 */
	public void runChooser(){
		Thread thisThread = new Thread(this);
		thisThread.start();
		Thread newFilesThread = new Thread(filelList);
		newFilesThread.start();
	}
	public File randomizeFile() {
		int i = (int) (Math.random() * files.size());
		return files.get(i);
	}

	public ArrayList<File> getFileList(){
		return files;
	}
	
	public File getFile(int i) {
		synchronized (this) {
			return showList[i];
		}
	}
	/**
	 * switches the pictures to a new one
	 * @param index
	 * @param newPic
	 */
	public void switchPictures(int index,File newPic){
		parent.switchPicture(index, newPic);
	}
	
	public void run() {

		long[] shownOnScreen = new long[picCount];
		// same startpoint
//		Arrays.fill(shownOnScreen, System.currentTimeMillis());
		//different startpoint
		for(int i =0;i<shownOnScreen.length;i++){
			//shownOnScreen[i]= System.currentTimeMillis()+(long)(Math.random()*(PIC_SHOW_TIME));
		  	shownOnScreen[i] = System.currentTimeMillis() -  startTime[i];
		}

		while (true) {
			
			for (int i = 0; i < picCount; i++) {
				// grep new image if the current image is longer on the screen
				// the show time
				if (System.currentTimeMillis() - shownOnScreen[i] > (PIC_SHOW_TIME)) {

					// first check if we got new shit rolling and show it
					boolean isEmpty = filelList.getNewFiles().isEmpty();

					if (!isEmpty) {
						synchronized (this) {
							switchPictures(i, filelList.getNewFiles().get(0));
						}
						// remove the image from the newlist and add it to
						files.add(filelList.getNewFiles().get(0));
						filelList.getNewFiles().remove(0);
						// if wo dont have anything new we will show other
						// shit
					} else {
						File random = randomizeFile();
						System.out.println("new File at:  "+i +" Name: "+ random.getName());
						synchronized (this) {
							switchPictures(i, random);
						}
					}
					shownOnScreen[i] = System.currentTimeMillis();
				}
			}
			
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (Exception e) {
				return;
			}
		}
	}
}
