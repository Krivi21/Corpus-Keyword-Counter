package directory_crawler;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DirectoryCrawlerImpl implements DirectoryCrawler {
	
	private List<File> enteredDirs;	//main ubacuje u njega, dirCrawlerWorker cita iz njega
	
	private ReadWriteLock lock = new ReentrantReadWriteLock();
	
	public DirectoryCrawlerImpl () {
		enteredDirs = new ArrayList<>();
	}
	
	@Override
	public void addMainDir(File dir) {
		lock.writeLock().lock();
		enteredDirs.add(dir);
		lock.writeLock().unlock();
	}

	@Override
	public List<File> getEnteredDirs() {
		lock.writeLock().lock();
		lock.writeLock().unlock();
		return enteredDirs;
		
	}
	
	
}
