package Indexing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import Classes.Path;

public class PreProcessedCorpusReader {

	private File file;
	private BufferedReader br;
	
	public PreProcessedCorpusReader(String type) throws IOException {
		// This constructor opens the pre-processed corpus file, Path.ResultHM1 + type
		// You can use your own version, or download from http://crystal.exp.sis.pitt.edu:8080/iris/resource.jsp
		// Close the file when you do not use it any more
		if(type.equals("trectext") || type.equals("trecweb")) {
			this.file = new File(Path.ResultHM1 + type);
			this.br = new BufferedReader(new FileReader(file));	
		}
		else
			throw new IOException("error type");		
	}
	

	public Map<String, String> NextDocument() throws IOException {
		// read a line for docNo, put into the map with <"DOCNO", docNo>
		// read another line for the content , put into the map with <"CONTENT", content>
		String docNo = this.br.readLine();
		Map<String, String> map = new TreeMap<>();
		if(docNo != null) {
			map.put("DOCNO", docNo.trim());
			String content = this.br.readLine();
			map.put("CONTENT", content == null ? "": content.trim());
			return map;
		}
		
		this.br.close();
		return null;
	}

}
