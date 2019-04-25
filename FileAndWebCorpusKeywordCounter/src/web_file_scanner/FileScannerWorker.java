package web_file_scanner;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.RecursiveTask;


public class FileScannerWorker extends RecursiveTask<Map<String, Integer>>{

	private static final long serialVersionUID = -122620037217778883L;
	
	private File dir;
	private int file_scanning_size_limit;	//= 1700 bytes
	private int currentSize;
	private int startingElement;
	private int counter;
	
	public FileScannerWorker(File dir, Integer file_scanning_size_limit, int currentSize, int startingElement, int counter) {
		this.dir = dir;
		this.file_scanning_size_limit = file_scanning_size_limit;
		this.currentSize = currentSize;
		this.startingElement = startingElement;
		this.counter = counter;
	}
	
	@Override
	protected Map<String, Integer> compute() {
		Map<String, Integer> toReturn = new HashMap<>();
		
		File[] fList = dir.listFiles();
		
		if(counter == fList.length-1) {	//ako je poslednji fajl u listi
			if(currentSize > file_scanning_size_limit) { //scan pre poslednjeg fajla
				System.out.println();
				System.out.println("Scanning txt files: ");
				System.out.println("-------------------");
				System.out.println("Directory: " + dir);
				for(int i = startingElement; i < counter; i++) {
					System.out.println(fList[i].getName() + " (" + fList[i].length() + " bytes)" + " - Keywords:");
					keywordCounter(fList[i]);
				}
				//i poslednji fajl
				System.out.println();
				System.out.println("Scanning txt file: ");
				System.out.println("------------------");
				System.out.println("Directory: " + dir);
				System.out.println(fList[counter].getName() + " (" + fList[counter].length() + " bytes)" + " - Keywords:");
				keywordCounter(fList[counter]);
			} else { //scan poslednjih fajlova
				System.out.println();
				System.out.println("Scanning txt files: ");
				System.out.println("-------------------");
				System.out.println("Directory: " + dir);
				for(int i = startingElement; i <= counter; i++) {
					System.out.println(fList[i].getName() + " (" + fList[i].length() + " bytes)" + " - Keywords:");
					keywordCounter(fList[i]);
				}	
			}
		} else if(currentSize > file_scanning_size_limit) {		//ako je velicina nakupljenih fajlova veca od limita
			System.out.println();
			System.out.println("Scanning txt files: ");
			System.out.println("-------------------");
			System.out.println("Directory: " + dir);
			for(int i = startingElement; i < counter; i++) {
				System.out.println(fList[i].getName() + " (" + fList[i].length() + " bytes)"  + " - Keywords:");
				keywordCounter(fList[i]);
			}
			
			
			currentSize = 0;
			currentSize += fList[counter].length();
			startingElement=counter;
			counter++;
			FileScannerWorker callTask = new FileScannerWorker(dir, file_scanning_size_limit, currentSize, startingElement, counter);
			Map<String, Integer> forkResult = callTask.compute();
			
			//prodjem kroz forkResult mapu i ako toReturn mapa nema neki kljuc odavde, ubacim ga i value, potom ih izbrisem iz forkResult
//			for(Map.Entry mapElement : forkResult.entrySet()) {
//				if(!toReturn.containsKey(mapElement.getKey())){
//					toReturn.put((String)mapElement.getKey(), (Integer)mapElement.getValue());
//					forkResult.remove(mapElement.getKey());
//				}
//			}
			//prolazim kroz toReturn mapu i ako forkResult i ona imaju isti kljuc, saberem njihove values u toReturn mapu
//			for(Map.Entry mapElement : toReturn.entrySet()) {
//				if(forkResult.containsKey(mapElement.getKey())) {
//					mapElement.setValue((Integer)mapElement.getValue() + forkResult.getOrDefault(mapElement.getKey(), (Integer) mapElement.getValue()));
//				}
//			}
			
		} else {
			currentSize += fList[counter].length();
			counter++;
			//ForkJoinTask<Map<String, Integer>> forkTask = new FileScannerWorker(dir, file_scanning_size_limit, currentSize, startingElement, counter);;
			//forkTask.fork();
			
			FileScannerWorker callTask = new FileScannerWorker(dir, file_scanning_size_limit, currentSize, startingElement, counter);
			Map<String, Integer> forkResult = callTask.compute();
			
			//Map<String,Integer> callResult = forkTask.join();
			
			//toReturn.addAll(forkResult);
			//toReturn.addAll(callResult);
			
			
			//prodjem kroz forkResult mapu i ako toReturn mapa nema neki kljuc odavde, ubacim ga i value, potom ih izbrisem iz forkResult
			for(Map.Entry mapElement : forkResult.entrySet()) {
				if(!toReturn.containsKey(mapElement.getKey())){
					toReturn.put((String)mapElement.getKey(), (int)mapElement.getValue());
					forkResult.remove(mapElement.getKey());
				}
			}
			//prolazim kroz toReturn mapu i ako forkResult i ona imaju isti kljuc, saberem njihove values u toReturn mapu
			for(Map.Entry mapElement : toReturn.entrySet()) {
				if(forkResult.containsKey(mapElement.getKey())) {
					mapElement.setValue((Integer)mapElement.getValue() + forkResult.getOrDefault(mapElement.getKey(), (Integer) mapElement.getValue()));
				}
			}
		}
		
		
		return toReturn;
	}
	
	
	//funkcija kojoj se prosledji txt fajl i ispise koliko kojih keyworda ima, FileScannerService.keywords je static
	public void keywordCounter(File f) {
		
		//popunim hash mapu sa keywordsima i 0 vrednostima
		Map<String, Integer> result = new HashMap<>();
		for (String s : FileScannerService.keywords) {
			//dodajem na pocetak i kraj stringa " " jer sam retardiran
			String customRework = " ";
			customRework += s;
			customRework += " ";
	    	result.put(customRework, 0);
		}
		
		for (Map.Entry mapElement : result.entrySet()) { 
            String keyword = (String) mapElement.getKey();
            
            try {
				BufferedReader reader = new BufferedReader(new FileReader(f));
				String line = reader.readLine();
				while(line != null) {
					String words[] = line.split(" ");
					int count = 0;
					for (int i = 0; i < words.length; i++)  
				    {
						String customRework = " ";
						customRework += words[i];
						customRework += " ";
				    
				    if (keyword.equals(customRework)) 
				        result.put(keyword, result.get(keyword)+1); 
				    } 
					line = reader.readLine();
				}
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				System.out.println("File " + f + " can't be read!");
			}
        } 
		
		

		for (Map.Entry mapElement : result.entrySet()) { 
            String key = (String) mapElement.getKey(); 
  
            Integer value = (Integer) mapElement.getValue();
  
            System.out.println(key + " : " + value); 
        } 
		
		//return result;
		
	}

}
