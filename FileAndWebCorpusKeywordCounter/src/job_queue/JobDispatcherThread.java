package job_queue;

public class JobDispatcherThread implements Runnable {

	private JobBlockingQueue jobQueue;
	
	public JobDispatcherThread (JobBlockingQueue jobQueue) {
		this.jobQueue = jobQueue;
	}
	
	@Override
	public void run() {
		while(true) {
			ScanningJob job = jobQueue.getJobFromQueue();	//waitujem ako nema nista u jobQueue, a pravim posao ako ima
			job.initiate(); //pravi novi posao, implementacija u ScanningJobImpl
		}
		
	}

}
