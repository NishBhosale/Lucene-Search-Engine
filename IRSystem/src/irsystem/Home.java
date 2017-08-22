package irsystem;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import org.apache.lucene.search.IndexSearcher;


public class Home {

	// instantiate search class
	private static Search search = new Search();   

	// instantiate keyboard interface
	private static Scanner keyboard = new Scanner(System.in);
	static long startTime;
	static long endTime;

	public static void main(String[] args) {

		IndexSearcher indexSearcher = search.createIndexSearcher();
		if (indexSearcher==null) {
			System.out.println("Creating index...");
			Index.createIndex();    
		} 
		DisplayResult(indexSearcher);   
	}

	// Method - Display the output based on user query.
	public static void DisplayResult(IndexSearcher indexSearcher) {

		String input = "";
		do {
			System.out.println(" Enter your query here (Press q to quit) : ");
			
			input = keyboard.nextLine();
			startTime = System.currentTimeMillis();
			if (!input.equalsIgnoreCase("q") && !input.equals("")) {
				// Call the searcher
				ArrayList<Data> docs = search.getDocument(input);
				if (docs.size() == 0) {
					System.out.println("No result found.");
				} else {
					System.out.println();
					ArrayList<Integer> sortedlist= new ArrayList<Integer>();
					// sorting retrieved docId in ascending order
					for (Data item : docs) {
						sortedlist.add( item.docId);
					}
					Collections.sort(sortedlist);

					for (Integer item : sortedlist) {						
						System.out.print(item+" , "); 
					}
					endTime = System.currentTimeMillis();
					System.out.println("\nTime taken to find document IDs: "+(endTime - startTime) + " ms");
					do {
						System.out.println();
						System.out.println("Enter a document Id # to read the text (Press 's' to search again or 'q' to quit) : ");
						
						input = keyboard.nextLine();	
						
						try {
							if (!input.equalsIgnoreCase("s") && !input.equalsIgnoreCase("q") && !input.isEmpty()) {
								long sTime= System.currentTimeMillis();
								for (Data item : docs) {
									if (item.docId == Integer.parseInt(input)) {
										System.out.println("\n"+item.getContent());
									}
								}
								long eTime= System.currentTimeMillis();
								System.out.println("\nTime taken to fetch the document: "+(eTime - sTime) + " ms");
							}
							
							
						} catch (NumberFormatException e) {
							System.out.println("\n"+e.getMessage() + " is not a number");
						}
					} while (!input.equalsIgnoreCase("s") && !input.equalsIgnoreCase("q"));

				}
			}
		} while (!input.equalsIgnoreCase("q"));
	}

	// Method - Get input datafile/dataset
	public static InputStreamReader getFile() {
		InputStreamReader iStreamReader = null;
		try {
			iStreamReader = new InputStreamReader(new FileInputStream(new File("Dataset/cran.all")), "UTF-8");
		} catch (Exception e) {
			System.out.println("An error occured while opening the file: " + e.getClass() + " :: " + e.getMessage());
		}
		return iStreamReader;
	}


}
