package job_queue;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Future;

import web_file_scanner.FileScannerService;
import web_file_scanner.WebScannerService;

public class ScanningJobImpl implements ScanningJob {
	
	FileScannerService fileScannerService;
	WebScannerService webScannerService;
	
	ScanType scanType;
	File dir;
	String link;
	int hopCount;
	
	public ScanningJobImpl(ScanType scanType, File dir, FileScannerService fileScannerService) {
		this.fileScannerService = fileScannerService;
		this.scanType = scanType;
		this.dir = dir;
	}
	
	public ScanningJobImpl(ScanType scanType, String link, int hopCount, WebScannerService webScannerService) {
		this.scanType = scanType;
		this.link = link;
		this.hopCount = hopCount;
		this.webScannerService = webScannerService;
	}

	@Override
	public ScanType getType() {
		// TODO Auto-generated method stub
		return scanType;
	}

	//upit sa kojim ce se dohvatati rezultati rada ovog posla preko CLI
	@Override
	public String getQuery() {
		// TODO Auto-generated method stub
		return null;
	}

	//metoda koja startuje job u odgvorajucem thread poolu
	@Override
	public void initiate() {
		if(this.scanType == ScanType.FILE) {
			fileScannerService.startDirScan(this.dir);
		} else {
			//web scanner service start scan
			webScannerService.startWebScanning(link, hopCount);
		}
	}


}
