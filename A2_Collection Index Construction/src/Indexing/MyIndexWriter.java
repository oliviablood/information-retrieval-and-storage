package Indexing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.text.html.HTMLDocument.Iterator;

import Classes.Path;

public class MyIndexWriter {
	// I suggest you to write very efficient code here, otherwise, your memory
	// cannot hold our corpus...

	// store all docNo names in the arraylist
	private ArrayList<String> docNoNames = new ArrayList<>();
	private HashMap<String, ArrayList<Integer>> termMap = new HashMap<>();

	private File outputIndFile;
	private int docNum;
	private FileWriter fw;

	public MyIndexWriter(String type) throws IOException {
		// This constructor should initiate the FileWriter to output your index files
		// remember to close files if you finish writing the index
		if (type.equals("trecweb"))
			this.outputIndFile = new File(Path.IndexWebDir + "indexTrecWeb.txt");
		else if (type.equals("trectext"))
			this.outputIndFile = new File(Path.IndexTextDir + "indexTrecText.txt");
		fw = new FileWriter(outputIndFile);
		docNum = 0;
	}

	public void IndexADocument(String docno, String content) throws IOException {
		// you are strongly suggested to build the index by installments
		// you need to assign the new non-negative integer docId to each document, which will be used in MyIndexReader

		//generate non-negative integer docId
		int docId = docNoNames.size();
		docNoNames.add(docno);
		
		String[] contents = content.split(" ");
		//unique content in the contents
		Set<String> set = new HashSet<String>();
		for(String term: contents) {
			//if term exists in the set for 1 time, just plus one on the basis of original term frequency in the array list
			if(set.contains(term)) {
				ArrayList<Integer> docFreList = termMap.get(term);
				int index = docFreList.size() - 1;
				docFreList.set(index, docFreList.get(index) + 1);
			}
			
			//if term is already in the map, but not in the set
			else if(termMap.containsKey(term)) {
				ArrayList<Integer> docFreList = termMap.get(term);
				docFreList.add(docId);
				docFreList.add(1);
				set.add(term);
			}
			//if term neither exists in termMap nor set
			else {
				ArrayList<Integer> docFreList = new ArrayList<>();
				docFreList.add(docId);
				docFreList.add(1);
				termMap.put(term, docFreList);
				set.add(term);
			}
		}
		docNum++;
		// write into the file after preprocessing terms in every 20000 documents. 
		if(docNum % 20000 == 0) {
			fileWrite();
			termMap = new HashMap<String,ArrayList<Integer>>();
		}

	}
	
	//write term dictionary to the file
	private void fileWrite() throws IOException {
		for(HashMap.Entry<String, ArrayList<Integer>> entry: termMap.entrySet()) {
			String term = entry.getKey();
			String docFre = entry.getValue().toString();
			docFre = docFre.replaceAll("[^0-9]", " ").trim();
			fw.append(term);
			fw.append(" ");
			fw.append(docFre);
			fw.append("\n");
		}
	}

	public void Close() throws IOException {
		// close the index writer, and you should output all the buffered content (if
		// any).
		// if you write your index into several files, you need to fuse them here.
		fileWrite();
		termMap = null;
		fw.close();
		docNoNames = null;	
	}
	
//	public static void main(String[] args) throws IOException {
//		long startTime=System.currentTimeMillis();
//		PreProcessedCorpusReader corpus=new PreProcessedCorpusReader("trecweb");
//		MyIndexWriter output=new MyIndexWriter("trecweb");
//		// initiate a doc object, which will hold document number and document content
//		Map<String, String> doc = null;
//
//		int count=0;
//		// build index of corpus document by document
//		while ((doc = corpus.NextDocument()) != null) {
//			// load document number and content of the document
//			String docno = doc.get("DOCNO"); 
//			String content = doc.get("CONTENT");			
//			
//			// index this document
//			output.IndexADocument(docno, content); 
//			
//			count++;
//			if(count%30000==0)
//				System.out.println("finish "+count+" docs");
//		}
//		System.out.println("totaly document count:  "+count);
//		output.Close();
//		long endTime=System.currentTimeMillis();
//		System.out.println("index web corpus running time: "+(endTime-startTime)/60000.0+" min"); 
//	}
}
