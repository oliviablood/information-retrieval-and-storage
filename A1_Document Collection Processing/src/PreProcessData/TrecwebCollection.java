package PreProcessData;

import java.io.IOException;
import java.util.*;
import java.io.*;
import Classes.Path;

/**
 * This is for INFSCI 2140 in 2018
 *
 */
public class TrecwebCollection implements DocumentCollection {
	// Essential private methods or variables can be added.
	private BufferedReader bufferreader = null;
	private String keyStartFlag = "<DOCNO>";
	private String keyEndFlag = "</DOCNO>";
	private String textStartFlag = "</DOCHDR>";
	private String docEndFlag = "</DOC>";

	// YOU SHOULD IMPLEMENT THIS METHOD.
	public TrecwebCollection() throws IOException {
		// 1. Open the file in Path.DataWebDir.
		// 2. Make preparation for function nextDocument().
		// NT: you cannot load the whole corpus into memory!!
		bufferreader = new BufferedReader(new BufferedReader(new InputStreamReader(new FileInputStream(Path.DataWebDir))));
	}
	
	// YOU SHOULD IMPLEMENT THIS METHOD.
	public Map<String, Object> nextDocument() throws IOException {
		// 1. When called, this API processes one document from corpus, and returns its doc number and content.
		// 2. When no document left, return null, and close the file.
		// 3. the HTML tags should be removed in document content.
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

			// read the line until between </DOCHDR> and </DOC>
			while (!line.equals(textStartFlag)) {
				line = bufferreader.readLine();
			}
			line = bufferreader.readLine();
			int flag = 0;
			while (!line.equals(docEndFlag)) {
				int tagIndex;
				for(tagIndex = 0;tagIndex < line.length(); tagIndex++){
					char c = line.charAt(tagIndex);
					if( c == '<' )
						flag++;
					else if( c == '>')
						flag --;
					else if (flag == 0)
						mainContent.append(c);
				}
//				mainContent.append(line);
				mainContent.append(' ');
				line = bufferreader.readLine();
			}
			if (key != null || mainContent != null) {
				textMap.put(key, mainContent.toString().toCharArray());
			}
			else{
				continue;
			}
			return textMap;
		}
		bufferreader.close();
		return null;

	}

//	public static void main(String[] args) throws Exception {
//	TrecwebCollection corpus = new TrecwebCollection();
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
