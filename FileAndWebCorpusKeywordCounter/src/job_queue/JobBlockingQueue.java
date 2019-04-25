package job_queue;

public interface JobBlockingQueue {
	
	void addJobToQueue(ScanningJob job);
	ScanningJob getJobFromQueue();
	
}
