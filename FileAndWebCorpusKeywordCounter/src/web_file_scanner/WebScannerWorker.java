package web_file_scanner;

import java.util.Map;
import java.util.concurrent.Callable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import job_queue.JobBlockingQueue;
import job_queue.ScanType;
import job_queue.ScanningJobImpl;

public class WebScannerWorker implements Callable<Map<String, Integer>>{
	
	String link;
	int hopCount;
	JobBlockingQueue jobQueue;
	WebScannerService webScannerService;
	
	public WebScannerWorker(String link, int hopCount, JobBlockingQueue jobQueue, WebScannerService webScannerService) {
		this.link = link;
		this.hopCount = hopCount;
		this.jobQueue = jobQueue;
		this.webScannerService = webScannerService;
	}

	@Override
	public Map<String, Integer> call() throws Exception {
		
		
		
		Document doc = Jsoup.connect(link).get();
		if(doc == null) {
			return null;
		}
		Elements links = doc.select("a[href]");
		
		System.out.println("Scanning of link: " + this.link + "(Current hop count = " + this.hopCount);
		
		
		
		if(hopCount > 0) {
			for (Element link : links) {
	            //dodaj u jobqueue novi posao
				jobQueue.addJobToQueue(new ScanningJobImpl(ScanType.WEB, link.attr("abs:href"), hopCount-1, webScannerService));
	        }
		}
		
		return null;
	}
	
	private static String trim(String s, int width) {
        if (s.length() > width)
            return s.substring(0, width-1) + ".";
        else
            return s;
    }

}
