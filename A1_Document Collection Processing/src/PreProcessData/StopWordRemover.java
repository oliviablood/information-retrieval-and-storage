package PreProcessData;
import Classes.*;

import java.io.BufferedReader;
import java.io.*;
import java.util.*;

public class StopWordRemover {
	// Essential private methods or variables can be added.
	private HashSet<String> set = new HashSet<>();
	private BufferedReader br = null;
	// YOU SHOULD IMPLEMENT THIS METHOD.
	public StopWordRemover( ) throws IOException {
		// Load and store the stop words from the fileinputstream with appropriate data structure.
		// NT: address of stopword.txt is Path.StopwordDir
		try{
			br = new BufferedReader(new BufferedReader(new InputStreamReader(new FileInputStream(Path.StopwordDir))));
			String line = null;
			while ((line = br.readLine()) != null){
				set.add(line.trim());
			}
			br.close();
		}catch (FileNotFoundException e){
			System.out.println("File not found!");
		}

	}
	// YOU SHOULD IMPLEMENT THIS METHOD.
	public boolean isStopword( char[] word ) {
		// Return true if the input word is a stopword, or false if not.
		if(set.contains(new String(word)))
			return true;
		return false;
	}
}
