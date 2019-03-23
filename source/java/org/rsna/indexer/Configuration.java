package org.rsna.indexer;

import java.io.File;
import org.rsna.ui.PropertiesFile;

/**
 * A singleton PropertiesFile
 */
public class Configuration extends PropertiesFile {

	static Configuration instance = null;
	static String filename = "DicomImageIndexer.properties";

	public static Configuration getInstance() {
		if (instance == null) {
			instance = new Configuration(new File(filename));
		}
		return instance;
	}

	protected Configuration(File file) {
		super(file);
	}

}
