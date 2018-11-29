package PreProcessData;

import java.util.ArrayList;
import java.util.List;

/**
 * This is for INFSCI 2140 in 2018
 * 
 * TextTokenizer can split a sequence of text into individual word tokens.
 */
public class WordTokenizer {
	// Essential private methods or variables can be added.
	private List<String> words = new ArrayList<>();
	int pos = 0;
	// YOU MUST IMPLEMENT THIS METHOD.
	public WordTokenizer( char[] texts ) {
		// Tokenize the input texts.
		String s = new String(texts);
		//replace all characters that are not numbers or letters to blank space
		s = s.replaceAll("[^a-zA-Z0-9]"," ");
		String[] text = s.split("\\s+");
		for(String word : text){
			words.add(word);
		}

	}
	
	// YOU MUST IMPLEMENT THIS METHOD.
	public char[] nextWord() {
		// Return the next word in the document.
		// Return null, if it is the end of the document.
		char[] currentWord = null;
		if(pos < words.size()){
			pos++;
			currentWord = words.get(pos - 1).toCharArray();
			return currentWord;
		}
		return null;
	}
	
}
