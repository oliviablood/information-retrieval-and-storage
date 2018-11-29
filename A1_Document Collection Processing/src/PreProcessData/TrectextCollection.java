package PreProcessData;

import java.io.*;
import java.util.Map;
import java.util.HashMap;
import Classes.Path;

/**
 * This is for INFSCI 2140 in 2017
 *
 */
public class TrectextCollection implements DocumentCollection {

	//you can add essential private methods or variables
	private BufferedReader bufferreader = null;
	private String keyStartFlag = "<DOCNO>";
	private String keyEndFlag = "</DOCNO>";
	private String docEndFlag = "</DOC>";
	private String textStartFlag = "<TEXT>";
	private String textEndFlag = "</TEXT>";

	// YOU SHOULD IMPLEMENT THIS METHOD
	public TrectextCollection() throws IOException {
		// This constructor should open the file in Path.DataTextDir
		// and also should make preparation for function nextDocument()
		// you cannot load the whole corpus into memory here!!

		//read the file in Path.DataTextDir
		bufferreader = new BufferedReader(new BufferedReader(new InputStreamReader(new FileInputStream(Path.DataTextDir))));
	}

	// YOU SHOULD IMPLEMENT THIS METHOD
	public Map<String, Object> nextDocument() throws IOException {
		// this method should load one document from the corpus, and return this document's number and content.
		// the returned document should never be returned again.
		// when no document left, return null
		// NTT: remember to close the file that you opened, when you do not use it any more

		Map<String, Object> textMap = new HashMap<>();
		String key = "";
		StringBuffer mainContent = new StringBuffer();
		String line = new String(); // read the first line;

		while ((line = bufferreader.readLine()) != null) {
			// if this line doesn't contain "<DOCNO>", read the next line;
//			int flag = 0;
			while (line.indexOf(keyStartFlag) < 0) {
				line = bufferreader.readLine();
				if (line == null){
					//flag = 1;
					break;
				}
			}
			if(line == null){
				break;
			}
//			if(flag == 1)
//				break;
			// retrieve the unique identifier for each document;
			while (line.indexOf(keyStartFlag) >= 0) {
				key = line.replace(keyStartFlag, "").replace(keyEndFlag, "").trim();
				line = bufferreader.readLine();
			}
			// read the line until between <text> and </text>
			while (!line.equals(textStartFlag)) {
				line = bufferreader.readLine();
			}
			line = bufferreader.readLine();
			while (!line.equals(textEndFlag)) {
				mainContent.append(line);
				mainContent.append(' ');
				line = bufferreader.readLine();
			}

			textMap.put(key, mainContent.toString().toCharArray());
			return textMap;
		}
		bufferreader.close();
		return null;
	}


//public static void main(String[] args) throws Exception {
//	TrectextCollection corpus = new TrectextCollection();
//
//
//	Map<String, Object> doc = null;
//	int count = 0;
//	while ((doc = corpus.nextDocument()) != null) { //
//		count++;
//		String docno = doc.keySet().iterator().next();
//
//		// Load document content.
//		char[] content = (char[]) doc.get(docno);
//		System.out.println(docno);
////		for (int i = 0; i < content.length; i++) {
////			System.out.print(content[i]);
//
////		}
//		System.out.println("");
//	}
////	System.out.println("cnt=" + count);
//}
}






