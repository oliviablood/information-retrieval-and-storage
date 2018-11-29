package PseudoRFSearch;

import java.util.*;

import Classes.Document;
import Classes.Query;
import IndexingLucene.MyIndexReader;
import SearchLucene.QueryRetrievalModel;

public class PseudoRFRetrievalModel {

	MyIndexReader ixreader;
	List<Document> feedbackDocs;
	Set<Integer> docId;
	final int collectionLen=142062976;
	final int docNum=503473;
	final double miu=(double)collectionLen/(double)docNum;

	public PseudoRFRetrievalModel(MyIndexReader ixreader)
	{
		this.ixreader=ixreader;
	}

	/**
	 * Search for the topic with pseudo relevance feedback in 2017 spring assignment 4.
	 * The returned results (retrieved documents) should be ranked by the score (from the most relevant to the least).
	 *
	 * @param aQuery The query to be searched for.
	 * @param TopN The maximum number of returned document
	 * @param TopK The count of feedback documents
	 * @param alpha parameter of relevance feedback model
	 * @return TopN most relevant document, in List structure
	 */
	public List<Document> RetrieveQuery( Query aQuery, int TopN, int TopK, double alpha) throws Exception {
		// this method will return the retrieval result of the given Query, and this result is enhanced with pseudo relevance feedback
		// (1) you should first use the original retrieval model to get TopK documents, which will be regarded as feedback documents
		// (2) implement GetTokenRFScore to get each query token's P(token|feedback model) in feedback documents
		// (3) implement the relevance feedback model for each token: combine the each query token's original retrieval score P(token|document) with its score in feedback documents P(token|feedback model)
		// (4) for each document, use the query likelihood language model to get the whole query's new score, P(Q|document)=P(token_1|document')*P(token_2|document')*...*P(token_n|document')

		//get feedback documents
		QueryRetrievalModel model = new QueryRetrievalModel(ixreader);
		feedbackDocs = model.retrieveQuery(aQuery, TopK);

		docId = new HashSet<Integer>();

		//get P(token|feedback documents)
		HashMap<String,Double> TokenRFScore=GetTokenRFScore(aQuery,TopK);

		//map the docid to token frequency map
		//key: docId; value: token and token frequency
		Map<Integer, Map<String, Integer>> docPosting = new HashMap<>();

		String[] token = aQuery.GetQueryContent().split(" ");

		for(int i = 0; i < token.length; i++) {
			if(!TokenRFScore.containsKey(token[i])) continue;
			int[][] posting = ixreader.getPostingList(token[i]);

			for(int[] freq: posting) {
				int docTempId = freq[0];
				int tokenFreq = freq[1];
				if(!docId.contains(docTempId)) continue;
				if(docPosting.containsKey(docTempId)) {
					Map<String, Integer> map = docPosting.get(docTempId);
					map.put(token[i], tokenFreq);
				}
				else {
					Map<String, Integer> map = new HashMap<>();
					map.put(token[i], tokenFreq);
					docPosting.put(docTempId, map);
				}
			}
		}

		//using priority queue to sort the document scores
		PriorityQueue<Document> topNDocs = new PriorityQueue<>(TopN, new Comparator<Document>() {
			@Override
			public int compare(Document d1, Document d2) {
				if (d1.score() < d2.score())
					return -1;
				else if (d1.score() > d2.score())
					return 1;
				else return 0;
			}
		});


		//calculate the query's new score by using query likelihood language model
		for(int id: docId) {
			double score = 1.0;
			for(int i = 0; i < token.length; i++) {
				if(!TokenRFScore.containsKey(token[i])) continue;
				//calculate original probability P(qi | D)
				double originalScore = 0.0;
				originalScore = (double)docPosting.get(id).getOrDefault(token[i], 0)/(double)ixreader.docLength(id);
				score = score * alpha * originalScore + (1.0 - alpha)*TokenRFScore.get(token[i]);
			}

			//push doc into priority queue
			Document doc = new Document(Integer.toString(id),ixreader.getDocno(id), score);
			if(topNDocs.size() < TopN) {
				topNDocs.add(doc);
			}
			else {
				if(score > topNDocs.peek().score()){
					topNDocs.poll();
					topNDocs.add(doc);
				}
			}
		}

		// sort all retrieved documents from most relevant to least, and return TopN
		List<Document> results = new ArrayList<Document>();
		while(!topNDocs.isEmpty()){
			results.add(0, topNDocs.poll());
		}
		if(results.size() == 0)
			return null;
		return results;
	}

	public HashMap<String,Double> GetTokenRFScore(Query aQuery,  int TopK) throws Exception
	{
		// for each token in the query, you should calculate token's score in feedback documents: P(token|feedback documents)
		// use Dirichlet smoothing
		// save <token, score> in HashMap TokenRFScore, and return it
		HashMap<String,Double> TokenRFScore=new HashMap<String,Double>();

		int docLen = 0;
		//get the feedback document len
		for(Document feedbackDoc: feedbackDocs) {
			String docid = feedbackDoc.docid();
			docId.add(Integer.parseInt(docid));
			docLen += ixreader.docLength(Integer.parseInt(docid));
		}

		String[] token = aQuery.GetQueryContent().split(" ");
		for(int i = 0; i < token.length; i++) {
			int tokenFreq = 0;
			int[][] posting = ixreader.getPostingList(token[i]);
			if(posting == null) continue;
			for(int[] freq: posting) {
				if(docId.contains(freq[0]))
					tokenFreq += freq[1];
			}

			int collectionFreq = (int)ixreader.CollectionFreq(token[i]);
			if(tokenFreq == 0 && collectionFreq == 0) continue;

			//use Dirichlet smoothing
			double score = 0.0;
			score = ((double)tokenFreq + miu * (double)collectionFreq/(double)collectionLen) / ((double)docLen+ miu);
			TokenRFScore.put(token[i], score);
//			double num = (double)tokenFreq + miu*(double)collectionFreq/(double)collectionLen;
//			double denom = (double)docLen + miu;
//			TokenRFScore.put(token[i], num/denom);

		}

		return TokenRFScore;
	}


}