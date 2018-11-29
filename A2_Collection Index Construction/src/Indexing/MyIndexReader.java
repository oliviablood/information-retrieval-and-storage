package Indexing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import Classes.Path;


public class MyIndexReader {
	//you are suggested to write very efficient code here, otherwise, your memory cannot hold our corpus...
    
	private ArrayList<String> docNoNames;
	// mapping String docNo to Integer docId
	private HashMap<String, Integer> docIdMap;
	
	/**
	 * Key: term
	 * Value: Array List contains the document index and the term frequency in this document
	 */
    private HashMap<String, ArrayList<Integer>> termMap;
    private String type;
    private BufferedReader br;
    
	public MyIndexReader( String type ) throws IOException {
		this.type = type;
		//read the index files you generated in task 1
		//remember to close them when you finish using them
		//use appropriate structure to store your index
//		
//		File file = null;
//		if(type.equals("trectext")) 
//			file = new File(Path.DataTextDir);
//		else
//			file = new File(Path.DataWebDir);
//		br = new BufferedReader(new FileReader(file));
		
		// map docNo to docID
		docNoNames = new ArrayList<String>();
		docIdMap = new HashMap<String, Integer>();
		int docId = 0;
		PreProcessedCorpusReader pr = new PreProcessedCorpusReader(type);
		Map<String, String> docMap = null;
		while((docMap = pr.NextDocument()) != null) {
			String docNo = docMap.get("DOCNO");
			docNoNames.add(docNo); 
			docIdMap.put(docNo, docId);
			docId++;
		}
//		br.close();
		termMap = new HashMap<>();			
	}
	
	//get the non-negative integer dociId for the requested docNo
	//If the requested docno does not exist in the index, return -1
	public int GetDocid( String docno ) {
		if(!docIdMap.containsKey(docno))
			return -1;
		return docIdMap.get(docno);
	}

	// Retrieve the docno for the integer docid
	public String GetDocno( int docid ) {
		if(docNoNames.get(docid) != null)
			return docNoNames.get(docid);
		return null;		
	}
	
	/**
	 * Get the posting list for the requested token.
	 * 
	 * The posting list records the documents' docids the token appears and corresponding frequencies of the term, such as:
	 *  
	 *  [docid]		[freq]
	 *  1			3
	 *  5			7
	 *  9			1
	 *  13			9
	 * 
	 * ...
	 * 
	 * In the returned 2-dimension array, the first dimension is for each document, and the second dimension records the docid and frequency.
	 * 
	 * For example:
	 * array[0][0] records the docid of the first document the token appears.
	 * array[0][1] records the frequency of the token in the documents with docid = array[0][0]
	 * ...
	 * 
	 * NOTE that the returned posting list array should be ranked by docid from the smallest to the largest. 
	 * 
	 * @param token
	 * @return
	 */
	
	/*
	 * retrieve the docid and term frequency based on the token
	 */
	private void MapToken(String token) throws NumberFormatException, IOException{
		File file = null;
		if(type.equals("trectext")) 
			file = new File(Path.IndexTextDir + "indexTrecText.txt");
		else
			file = new File(Path.IndexWebDir + "indexTrecWeb.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		ArrayList<Integer> docFreList = new ArrayList<>();
		String line = null;
		while((line = br.readLine()) != null) {
			String[] words = line.split("\\s+");
			if(!words[0].equals(token))
				continue;

			if(termMap.containsKey(token)) {
				docFreList = termMap.get(token);
			}
			else {
				docFreList = new ArrayList<>();
				termMap.put(token, docFreList);
			}
			for(int i = 1; i < words.length; i ++) {
				docFreList.add(Integer.parseInt(words[i]));
			}

		}
		br.close();
	}
	public int[][] GetPostingList( String token ) throws IOException {
		if(!termMap.containsKey(token))
			MapToken(token);
		if(!termMap.containsKey(token))
			return null;
		
		ArrayList<Integer> docFre = termMap.get(token);
		int n = docFre.size();
		int[][] postingList = new int[n/2][2];
		for(int i = 0; i < n; i+=2) {
			postingList[i/2][0] = docFre.get(i);
			postingList[i/2][1] = docFre.get(i+1);
		}
		return postingList;
	}

	// Return the number of documents that contains the token.
	public int GetDocFreq( String token ) throws IOException {
		// if there is no token in the termMap, call MapToken method to retrieve corresponding docid and term frequency.
		if(!termMap.containsKey(token))
			MapToken(token);
		if(!termMap.containsKey(token))
			return 0;
		ArrayList<Integer> docFre = termMap.get(token);
		return docFre.size()/2;

	}
	
	// Return the total number of times the token appears in the collection.
	public long GetCollectionFreq( String token ) throws IOException {
		if(!termMap.containsKey(token))
			MapToken(token);
		if(!termMap.containsKey(token))
			return 0;
		
		int sum = 0;
		ArrayList<Integer> docFre = termMap.get(token);
		for(int i = 0; i < docFre.size(); i+=2) {
			sum += docFre.get(i+1);
		}
		return sum;
	}
	
	public void Close() throws IOException {
		termMap = null;
		docIdMap = null;
		docNoNames = null;
		
	}
	
//	public static void main(String[] args) throws IOException {
//		long startTime=System.currentTimeMillis();
//		MyIndexReader ixreader=new MyIndexReader("trecweb");
//		// conduct retrieval
//		String token = "acow";
//		int df = ixreader.GetDocFreq(token);
//		long ctf = ixreader.GetCollectionFreq(token);
//		System.out.println(" >> the token \""+token+"\" appeared in "+df+" documents and "+ctf+" times in total");
//		if(df>0){
//			int[][] posting = ixreader.GetPostingList(token);
//			for(int ix=0;ix<posting.length;ix++){
//				int docid = posting[ix][0];
//				int freq = posting[ix][1];
//				String docno = ixreader.GetDocno(docid);
//				System.out.printf("    %20s    %6d    %6d\n", docno, docid, freq);
//			}
//		}
//		ixreader.Close();
//		long endTime=System.currentTimeMillis();
//		System.out.println("load index & retrieve running time: "+(endTime-startTime)/60000.0+" min");
//	}
	
}