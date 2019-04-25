package web_file_scanner;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;


public class FileScannerService {
	
	private Integer file_scanning_size_limit;
	public static Set<String> keywords;
	private ForkJoinPool pool = new ForkJoinPool();
	
	public FileScannerService(Integer file_scanning_size_limit, Set<String> keywords) {
		this.file_scanning_size_limit = file_scanning_size_limit;
		this.pool = new ForkJoinPool();
		this.keywords = keywords;
	}
	
	public void startDirScan(File dir) {
		System.out.println("Starting file scanning for: " + dir);
		
		Future<Map<String, Integer>> result = pool.submit(new FileScannerWorker(dir, file_scanning_size_limit, 0, 0, 0));
		
		try {
			
			Map<String, Integer> finishedResult = result.get();
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void shutdownThreadPool() {
		pool.shutdown();
	}
}
