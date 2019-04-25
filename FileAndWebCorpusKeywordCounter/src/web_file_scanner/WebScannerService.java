package web_file_scanner;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import job_queue.JobBlockingQueue;
import job_queue.ScanningJob;

public class WebScannerService {
	
	private JobBlockingQueue jobQueue;
	public static Set<String> keywords;
	//private int hopCount;

	ExecutorService threadPool = Executors.newCachedThreadPool();
	CompletionService<Map<String, Integer>> service = new ExecutorCompletionService<>(threadPool);
	
	public WebScannerService(JobBlockingQueue jobQueue, Set<String> keywords) {
		this.jobQueue = jobQueue;
		this.keywords = keywords;
	}
	
	//ova metoda se poziva ili iz JobDispatchera ili iz ScanningJobImpl ili lupam gluposti
	public void startWebScanning(String link, int hopCount) {
		Future<Map<String, Integer>> keywordCount = service.submit(new WebScannerWorker(link, hopCount, jobQueue, this));
	}
	
	public void shutdownThreadPool() {
		threadPool.shutdown();
	}
}
