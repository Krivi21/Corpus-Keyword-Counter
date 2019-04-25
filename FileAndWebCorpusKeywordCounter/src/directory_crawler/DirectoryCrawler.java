package directory_crawler;

import java.io.File;
import java.util.List;

public interface DirectoryCrawler {
	
	void addMainDir(File f);
	List<File> getEnteredDirs();
	
}
