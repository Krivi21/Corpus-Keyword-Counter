package directory_crawler;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.html.HTMLDocument.Iterator;

import job_queue.JobBlockingQueue;
import job_queue.ScanType;
import job_queue.ScanningJobImpl;
import web_file_scanner.FileScannerService;

public class DirectoryCrawlerWorker implements Runnable {
	
	JobBlockingQueue jobQueue;
	FileScannerService fileScannerService;

	private DirectoryCrawler dirCrawler;
	private Map<File, File> prefixDirs;	//	file / u kom je direktorijumu
	private Map<File, Long> lastModified; //	file / kad je modifikovan
	private List<File> validDirs;	// dirovi sa prefiksom ---- zbog uslova na liniji 76
	private String dir_corpus_prefix;
	private int sleepTime;
	
	private List<File> enteredDirs = new ArrayList<>();
	
	
	public DirectoryCrawlerWorker(DirectoryCrawler dirCrawler, String dir_corpus_prefix, JobBlockingQueue jobQueue, FileScannerService fileScannerService, int sleepTime) {
		this.dirCrawler = dirCrawler;
		prefixDirs = new HashMap<>();
		lastModified = new HashMap<>();
		validDirs = new ArrayList<>();
		this.dir_corpus_prefix = dir_corpus_prefix;
		this.sleepTime = sleepTime;
		
		this.jobQueue = jobQueue;
		this.fileScannerService = fileScannerService;
	}
	
	@Override
	public void run() {
		
		while(true) {
			
			//scan dirs (and make jobs)
			enteredDirs = dirCrawler.getEnteredDirs();
			for(File dir : enteredDirs) {
				scanDir(dir, false);
			}
			
			//TEST TEST
//			System.out.println("prefixDirs mapa:");
//			System.out.println("________________");
//			for (Map.Entry mapElement : prefixDirs.entrySet()) { 
//	            File key = (File) mapElement.getKey(); 
//	  
//	            // Add some bonus marks 
//	            // to all the students and print it 
//	            File value = (File) mapElement.getValue();
//	  
//	            System.out.println(key + " : " + value); 
//	        } 
			
//			System.out.println("lastModified mapa:");
//			System.out.println("__________________");
//			for (Map.Entry mapElement : lastModified.entrySet()) { 
//	            File key = (File) mapElement.getKey(); 
//	  
//	            // Add some bonus marks 
//	            // to all the students and print it 
//	            Long value = (Long) mapElement.getValue();
//	  
//	            System.out.println(key + " : " + value); 
//	        } 
			
			
			try {
				Thread.sleep(this.sleepTime);	//iz konf datoteke koliko spava
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
	}
	
	public void scanDir(File dir, boolean inPrefixDir) {
			//System.out.println("Trenutno u: " + dir);
			File[] fList = dir.listFiles();
			if(fList != null) {
				for(File file : fList) {
					if(file.isDirectory() && file.getName().startsWith(dir_corpus_prefix)) { //ako je dir sa prefixom
						if(!prefixDirs.containsValue(file) && !validDirs.contains(file)) { //ako je dir nov
							//System.out.println(file);
							validDirs.add(file);
							//NAPRAVI NOVI POSAO
							System.out.println("Pravim novi posao za dir: " + file);
							jobQueue.addJobToQueue(new ScanningJobImpl(ScanType.FILE, file, fileScannerService));
							scanDir(file, true); 
						} else {	//ako dir nije nov (postoji vec u prefixDir mapi)
							System.out.println("Scanning old dir: " + file);
							//System.out.println("Proveravamo opet: " + file);
							for (Map.Entry prefixDirsElement : prefixDirs.entrySet()) { 
								if(prefixDirsElement.getValue().equals(file)) {	//ili dir?
//									System.out.println(prefixDirsElement.getValue() + " and " + file + " are same.");
									//File currFile = (File) prefixDirsElement.getKey();
									for (Map.Entry lastModifiedElement : lastModified.entrySet()) { 
										//File currFile1 = (File) lastModifiedElement.getKey();
										if(lastModifiedElement.getKey().equals(prefixDirsElement.getKey())) {
											if(!lastModifiedElement.getValue().equals(((File) prefixDirsElement.getKey()).lastModified()) ) {
												//NAPRAVI NOVI POSAO
												System.out.println("Pravim novi posao za dir: " + file);
												jobQueue.addJobToQueue(new ScanningJobImpl(ScanType.FILE, file, fileScannerService));
												scanDir(file, true);
											}
										}
									}
								}
					
					        } 
							
						}
					} else if (file.isDirectory()) {	//ako je dir bez prefiksa
						scanDir(file, false);
					} else if (file.isFile() && inPrefixDir){	//ako je fajl u dir sa prefiksom
						prefixDirs.put(file, dir);
						lastModified.put(file, file.lastModified());
					}
				}
			}
		
	}

}
