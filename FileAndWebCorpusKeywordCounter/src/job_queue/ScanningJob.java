package job_queue;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Future;

public interface ScanningJob {
	
	ScanType getType();
	
	String getQuery();
	
	void initiate();
	
}
