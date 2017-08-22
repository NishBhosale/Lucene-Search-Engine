package irsystem;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LegacyIntField;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

@SuppressWarnings("deprecation")
public class Index {
	private String pathToIndex = "";
	private IndexWriter indexWriter = null;
	static long startTime;
	static long endTime;

	public Index(String pathToIndex) {
		this.pathToIndex = pathToIndex;
	}

	public static void createIndex() {
		//Start timer for createindex
		startTime = System.currentTimeMillis();
		//Create the object for writing
		Index writer = new Index("indexDir");
		try {			
			//Check if Index directory can be open
			if (writer.openIndex()) {
				//get a buffered reader handle to the file
				BufferedReader breader = new BufferedReader(Home.getFile());
				String value = null;
				Data data = new Data();
				// keep track of the documents to store a "docId"
				int docId = 1;
				// continue till the file has data
				while ((value = breader.readLine()) != null) {
					// Get the Title
					if (value.startsWith(".I")) {
						String id = "";
						// Grab the lines of the abstract to store
						while ((value = breader.readLine()) != null) {
							// Stop when you see %Text%
							if (!value.startsWith(".")) {
								id += "\n" + value;
							}
							// Get the Title and add the document to the index
							else if (value.startsWith(".T")) {
								String title = "";
								// Grab the lines of the abstract to store
								while ((value = breader.readLine()) != null) {
									// Stop when you see an ".I" token
									if (!value.startsWith(".")) {
										title += "\n" + value;
									} else {
										data.setTitle(title);
										break;
									}
								}
							}
							else {
								
								break;
							}
						}
					} 
					else if (value.startsWith(".T")) {
						String title = "";
						// Grab the lines of the abstract to store
						while ((value = breader.readLine()) != null) {
							// Stop when you see an ".I" token
							if (!value.startsWith(".")) {
								title += "\n" + value;
							} else {
								data.setTitle(title);
								break;
							}
						}
					}
					//Get the text/content
					else if (value.startsWith(".W")) {
						String content = "";
						// Grab the lines of the abstract to store
						while ((value = breader.readLine()) != null) {
							// Stop when you see an ".I" token
							if (!value.startsWith(".")) {
								content += "\n" + value;
							} else {
								data.setContent(content);
								data.setDocId(docId++);
								writer.addDoc(data);								
								break;
							}
						}
					}
				}

			} else {
				System.out.println("Cannot open the directory for writing");
			}
		} catch (Exception e) {
			System.out.println("Some error occured " + e.getClass() + " :: " + e.getMessage());
		} finally {
			//close out the index and release the lock on the file
			writer.finish();
		}
	}

	// Method - To Open the index for writing
	public boolean openIndex() {
		try {
			//Open the directory for the index writer
			Directory dir = FSDirectory.open(new File(pathToIndex).toPath());			
			// Use the Standard Analyzer
			Analyzer analyzer = new StandardAnalyzer();
			//Configure the indexer
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
			//set the indexer to overwrite even if an index already exists
			iwc.setOpenMode(OpenMode.CREATE);
			//instantiate the indexer (open the index)
			indexWriter = new IndexWriter(dir, iwc);
			return true;
		} catch (Exception e) {
			System.out.println("There was problem opening the index for writing: " + e.getClass() + " :: " + e.getMessage());
			return false;
		}
	}

	// Method- To Add a document to the index
	public void addDoc(Data data) {
		Document doc = new Document();        
		Field id= new LegacyIntField("id",data.getDocId(),Field.Store.YES);
		Field content = new TextField("content", data.getContent(), Field.Store.YES);
		Field title = new TextField("title", data.getTitle(), Field.Store.YES); 

		//boost precision by increasing the score of various fields
		content.setBoost((float) 1.0);
		title.setBoost((float) 1.0);

		// Adding Fields to the document 
		doc.add(id);
		doc.add(title);
		doc.add(content);
		try {
			// add the document to the index
			indexWriter.addDocument(doc);
		} catch (IOException ex) {
			System.out.println("An exception ocuured while trying to add the doc: " + ex.getClass() + " :: " + ex.getMessage());
		}
	}



	// Method - To close the index writer	 
	public void finish() {
		endTime = System.currentTimeMillis();
		System.out.println("Done creating index. Took "+(endTime - startTime) + " ms");
		try {
			// commit the document to the index
			indexWriter.commit();
			System.out.println("File indexed :" +indexWriter.numDocs());
			// close the index writer
			indexWriter.close();

		} catch (IOException ex) {
			System.out.println("We had a problem closing the index: " + ex.getClass() + " :: " + ex.getLocalizedMessage());
		}
	}


}
