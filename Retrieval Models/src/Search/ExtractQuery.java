package Search;

import Classes.*;

import java.io.*;
import java.util.ArrayList;

import Classes.Path;
import Classes.Query;
import PreProcessData.StopWordRemover;
import PreProcessData.WordNormalizer;
import PreProcessData.WordTokenizer;

public class ExtractQuery {
	
	private BufferedReader br;
	private ArrayList<Query> query = new ArrayList<>();
	private int count = 0;
	
	public ExtractQuery() throws IOException {
		//you should extract the 4 queries from the Path.TopicDir
		//NT: the query content of each topic should be 1) tokenized, 2) to lowercase, 3) remove stop words, 4) stemming.
		//NT: you can simply pick up title only for query, or you can also use title + description + narrative for the query content.
		Query q = new Query();
		br = new BufferedReader(new FileReader(new File(Path.TopicDir)));
		String line = br.readLine();
		while(line != null) {
			if(line.indexOf("<num>") >= 0){
				String topicId = line.split(" ")[2];
				if(!topicId.equals(""))
					q.SetTopicId(topicId);
				line = br.readLine();

				String title = line.split(">")[1];
				if(!title.equals(""))
					q.SetQueryContent(preprocessQuery(title));

				if((!q.GetTopicId().equals("")) && (!q.GetQueryContent().equals(""))){
					query.add(q);
					q = new Query();
				}
			}
			line = br.readLine();
		}
		br.close();
	}
	
	public boolean hasNext()
	{
		if(count == query.size())
			return false;
		else return true;
	}
	
	public Query next()
	{
		Query que = query.get(count++);
		if(que == null)
			return  null;
		else return que;
	}
	
	private String preprocessQuery(String line) throws IOException {
		// Loading stopword, and initiate StopWordRemover.
		StopWordRemover stopwordRemover = new StopWordRemover();
		// Initiate WordNormalizer.
		WordNormalizer normalizer = new WordNormalizer();
		// Initiate the WordTokenizer class.
		WordTokenizer tokenizer = new WordTokenizer(line.toCharArray());
		
		// Initiate a word object, which can hold a word.
		char[] word;
		StringBuffer sb = new StringBuffer();

		// Process the document word by word iteratively.
		while ((word = tokenizer.nextWord()) != null) {
			// Each word is transformed into lowercase.
			word = normalizer.lowercase(word);

			// Only non-stopword will appear in result file.
			if (!stopwordRemover.isStopword(word))
				// Words are stemmed.
				sb.append(normalizer.stem(word) + " ");	
		}
		String preprocessedQuery = new String(sb);
		return preprocessedQuery.trim();
	}

//	public static void main(String[] args) throws Exception {
//	ExtractQuery queries = new ExtractQuery();
//	while (queries.hasNext()) {
//		Query aQuery = queries.next();
//		System.out.println(aQuery.GetTopicId() + "\t" + aQuery.GetQueryContent());
//	}
//	}
}
