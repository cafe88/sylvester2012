import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * The program can pull new files which are added into the directory after the
 * start of the program from newFiles
 * 
 * @author Niksa Rasic
 * 
 */
public class UpdateFilelist implements Runnable {

	/**
	 * all files we want to show
	 */
	ArrayList<File> files;
	/**
	 * all files which will be found after the thread is started
	 */
	ArrayList<File> newFiles;
	/**
	 * Location from which we will read the files from
	 */
	File location;
	/**
	 * time before the next update should occure
	 */
	final long SLEEP_CYCLE = 1000;

	/**
	 * 
	 * @param files
	 *            the list we want to fill
	 * @param location
	 *            the dir-location from which we want to get the files
	 * @throws FileNotFoundException
	 *             thrown if we can not reach the dir or get the files from it
	 */
	public UpdateFilelist(ArrayList<File> files, String location)
			throws FileNotFoundException {
		this.files = files;
		newFiles = new ArrayList<>();
		try {
			this.location = new File(location);
			if (!this.location.exists())
				System.out.println("location does not exist");
			if (!this.location.canRead())
				System.out.println("can not read from location");

		} catch (Exception e) {
			e.printStackTrace();
			throw new FileNotFoundException("Can not load the location!");
		}
		// add all Files which are at the start of the application in location
		System.out.println(location);
		for (File file : this.location.listFiles()) {
			if (file.getName().toLowerCase().endsWith(".jpg")) {
				//System.out.println(file);
				files.add(file);
			}
		}
	}

	/**
	 * Returns the the list with the new files found in location.
	 * 
	 * @return the List with the new Files
	 */
	public ArrayList<File> getNewFiles() {
		return newFiles;
	}

	/**
 * 
 */
	public void run() {

		// synch filelist with this as Lock object
		// all operations on the arraylists should sync with this object
		while (true) {
			File[] fileList = location.listFiles();
			//System.out.println("checking filesystem:");
			for (File file : fileList) {
				//System.out.println(file);
			}
			for (File foundFile : fileList) {
				// if the file is not in the filelist we add them to newFie
				if (!files.contains(foundFile)
						&& foundFile.getName().toLowerCase().endsWith(".jpg"))
					synchronized (this) {
						System.out
								.println("------------------Found a new Picture-------------------");
						newFiles.add(foundFile);
					}
				//
				try {
					Thread.sleep(SLEEP_CYCLE);
				} catch (InterruptedException e) {
					// on interrupt we kill everything
					return;
				}
			}
		}
	}

}
