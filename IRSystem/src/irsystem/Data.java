package irsystem;

public class Data {
	
	// variables	
	public String content;
	public String title;	    
	public int docId;
	public float score;
	
	// Default constructor
	public Data() {}

	// Constructor used by search 
	public Data(String content, String ttl, int d, float s) {
		this.content = content;
		title = ttl;
		docId = d;
		score = s;
	}

	// Getters
	public String getTitle() {
		return this.title;
	}

	public String getContent() {
		return this.content;
	}

	public int getDocId() {
		return this.docId;
	}

	public String getResultString() {
		return "Score: "+this.score+"\n"
				+ "Id:    "+this.docId+"\n"
				+ "Title: "+this.title+"\n";
	}

	public String getFormatedString() {
		return this.docId + ", ";
	}

	// Setters
	public void setTitle(String title) {
		this.title = title;
	}

	public void setContent(String text) {
		this.content = text;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}


}
