package job_queue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class JobBlockingQueueImpl implements JobBlockingQueue{

	private List<ScanningJob> jobQueue = new ArrayList<>();
	
	@Override
	public void addJobToQueue(ScanningJob job) {
		synchronized (this){
			jobQueue.add(job);
			System.out.println("Job has been added to jobQueue");
			notifyAll();	//budim threadove koji cekaju da se pojavi job u queue
		}
		
	}
	@Override
	public ScanningJob getJobFromQueue() {
		ScanningJob job = null;
		
		synchronized (this) {
			while (jobQueue.size() == 0) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			job = jobQueue.get(0);
			jobQueue.remove(0);
			System.out.println("Job has been removed from jobQueue");
			//notifyAll(); - ne treba mi
			
		}
		
		return job;
	}
	
	
	
	
}
