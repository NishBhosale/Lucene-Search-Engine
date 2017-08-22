package irsystem;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Search {

	// Method - takes a querystring as input and returns a list of data objects based on score for input query
	public ArrayList<Data> getDocument(String queryString) {
		// create the index searcher 
		IndexSearcher indexSearcher = createIndexSearcher();
		ArrayList<Data> docList = new ArrayList<Data>();
		Analyzer analyzer = new StandardAnalyzer();
				
		String fields[];
		fields = new String[]{"content", "title"};	
		MultiFieldQueryParser mfqp= new MultiFieldQueryParser(fields, analyzer);
				try {
					Query query = mfqp.parse(queryString);
					TopScoreDocCollector results = TopScoreDocCollector.create(10);
					indexSearcher.search(query, results);
					ScoreDoc[] hits = results.topDocs().scoreDocs; 
					// loop through the results and store them as Data objects to be returned to the users
					for (int i = 0; i < hits.length; i++) {
						Document doc = indexSearcher.doc(hits[i].doc);
						Data result = new Data(
								doc.getField("content").stringValue(),
								doc.getField("title").stringValue(),
								Integer.parseInt(doc.getField("id").stringValue()),
								hits[i].score
								);
						docList.add(result);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				}
		return docList;
	}


	// Method - creates and return the index searcher object
	public IndexSearcher createIndexSearcher() {
		IndexReader indexReader = null;
		IndexSearcher indexSearcher = null;
		try {
			Path indexDirFile = new File("indexDir").toPath();
			Directory dir = FSDirectory.open(indexDirFile);
			indexReader = DirectoryReader.open(dir);
			indexSearcher = new IndexSearcher(indexReader);

		} catch (IOException ex) {
			return null;
		}
		return indexSearcher;
	}
}
