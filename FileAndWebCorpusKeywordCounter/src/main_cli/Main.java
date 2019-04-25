package main_cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import directory_crawler.DirectoryCrawler;
import directory_crawler.DirectoryCrawlerImpl;
import directory_crawler.DirectoryCrawlerWorker;
import job_queue.JobBlockingQueue;
import job_queue.JobBlockingQueueImpl;
import job_queue.JobDispatcherThread;
import job_queue.ScanType;
import job_queue.ScanningJobImpl;
import web_file_scanner.FileScannerService;
import web_file_scanner.WebScannerService;

public class Main {

	public static void main(String[] args) {
		Set<String> keywords = new HashSet<>();
		String file_corpus_prefix = null;
		Integer dir_crawler_sleep_time = null;
		Integer file_scanning_size_limit = null;
		Integer hop_count = null;
		Integer url_refresh_time = null;
		
		//configuration load
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader("app.properties"));
			int lineNumber = 0;
			String line = reader.readLine();
			while(line != null) {
				if(lineNumber % 2 == 1) {
					line = line.substring(line.lastIndexOf("=") + 1);	//substring for after "="
					switch(lineNumber) {
						case 1:
							String[] str = line.split(",");
							for(int i = 0; i < str.length; i++){
					            keywords.add(str[i]);
					        }
							break;
						case 3:
							file_corpus_prefix = line;
							break;
						case 5:
							dir_crawler_sleep_time = Integer.parseInt(line);
							break;
						case 7:
							file_scanning_size_limit = Integer.parseInt(line);
							break;
						case 9:
							hop_count = Integer.parseInt(line);
							break;
						case 11:
							url_refresh_time = Integer.parseInt(line);
							break;
						default:
							break;
					}
				}
				//read next line
				line = reader.readLine();
				lineNumber++;
			}	
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		
		//JOB_QUEUE
		JobBlockingQueue jobQueue = new JobBlockingQueueImpl();
		//JOB_DISPATCHER_THREAD
		Thread jobDispatcherThread = new Thread(new JobDispatcherThread(jobQueue));
		jobDispatcherThread.setDaemon(true);
		jobDispatcherThread.start();
		
		//DIRECTORY_CRAWLER
		boolean firstRun = true;
		DirectoryCrawler dirCrawler = new DirectoryCrawlerImpl();
		
		
		//FILE_SCANNER
		FileScannerService fileScannerService = new FileScannerService(file_scanning_size_limit, keywords);
		
		//WEB_SCANNER
		WebScannerService webScanner = new WebScannerService(jobQueue, keywords);
			
	
		//entering commands ---- ad, aw, stop
		Scanner sc = new Scanner(System.in);
		boolean endProgram = false;
		while(!endProgram) {
			String line = sc.nextLine();
			String command = "";
			int i = 0;
			while(i < line.length()) {
				if(line.charAt(i) == ' ') {
					break;
				}
				command += line.charAt(i);
				i++;
			}
			//System.out.println(command);
			switch(command) {
			case "ad":
				System.out.println("Entered command: ad");
				String directoryName = line.substring(line.lastIndexOf(" ")+1);
				//System.out.println(fileName);
				File dir = new File(directoryName);
				if(!dir.canExecute()) {	//if no such file
					System.out.println("No such directory.");
					break;
				}
				//NAPRAVI POSAO sa directory crawler
				if(firstRun) {
					firstRun = false;
					dirCrawler.addMainDir(dir);
					Thread directoryCrawlerWorker = new Thread(new DirectoryCrawlerWorker(dirCrawler, file_corpus_prefix, jobQueue, fileScannerService, dir_crawler_sleep_time));
					directoryCrawlerWorker.setDaemon(true);
					directoryCrawlerWorker.start();
				} else {
					dirCrawler.addMainDir(dir);
				}
				
				break;
			case "aw":	// jsoup umesto ovoga
				System.out.println("Entered command: aw");
				String myLink = line.substring(line.lastIndexOf(" ")+1);
				try {
				  new URL(myLink).openConnection();
				}catch ( Exception ex ) {
				    System.out.println("Link broken or unable to connect to that website.");
				    break;
				}
				//NOVI POSAO
				System.out.println("Pravim novi posao za web: " + myLink);
				jobQueue.addJobToQueue(new ScanningJobImpl(ScanType.WEB, myLink, hop_count, webScanner));
				break;
			case "stop":
				if(line.length()>4) {
					System.out.println("Wrong command!");
					break;
				}
				System.out.println("Entered command: stop");
				endProgram = true;
				break;
			default:
				System.out.println("Wrong command!");
				break;	
			}
		}
		
		fileScannerService.shutdownThreadPool();
		webScanner.shutdownThreadPool();
		
		
		sc.close();	
		System.out.println("End of program");
		
	}
	
}
