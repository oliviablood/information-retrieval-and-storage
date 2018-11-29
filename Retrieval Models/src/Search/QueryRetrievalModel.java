package Search;

import java.io.IOException;
import java.util.*;

import Classes.Query;
import Classes.Document;
import IndexingLucene.MyIndexReader;

public class QueryRetrievalModel {
	
	protected MyIndexReader indexReader;
	private double miu;
	private long collectionLength;
	private long docNum = 503473 ;
	
	public QueryRetrievalModel(MyIndexReader ixreader) throws IOException {

		indexReader = ixreader;

		getCollectionLength();
		//initialize the miu as the average length of document
		miu = (double)(collectionLength/docNum);
	}

    // calculate the length of the whole collection
    private void getCollectionLength() throws IOException {
        collectionLength = 0;
        for (int i = 0; i < docNum; i++)
            collectionLength += indexReader.docLength(i);
        //System.out.println("number of docs is " + numberOfDocs + " collection length is " + collectionLength);
    }
	
	/**
	 * Search for the topic information. 
	 * The returned results (retrieved documents) should be ranked by the score (from the most relevant to the least).
	 * TopN specifies the maximum number of results to be returned.
	 * 
	 * @param aQuery The query to be searched for.
	 * @param TopN The maximum number of returned document
	 * @return
	 */
	
	public List<Document> retrieveQuery( Query aQuery, int TopN ) throws IOException {
		// NT: you will find our IndexingLucene.Myindexreader provides method: docLength()
		// implement your retrieval model here, and for each input query, return the topN retrieved documents
		// sort the docs based on their relevance score, from high to low

		String[] tokensInQuery = aQuery.GetQueryContent().split(" ");
		// term frequency in whole collection
		Map<String, Integer> tokenCollectionFreq = new HashMap<>();

		//map the docid to term frequency map
		Map<Integer, Map<String,Integer>> docPosting = new HashMap<>();


		for(String token: tokensInQuery){
			if(tokenCollectionFreq.containsKey(token))
				continue;

			//retrieve the term frequency in relevant document
			int[][] postingList = indexReader.getPostingList(token);
			if(postingList == null || postingList.length == 0)
				continue;

            // put the term frequency in whole collection into tokenCollectionFreq map
            tokenCollectionFreq.put(token, (int)indexReader.CollectionFreq(token));

			for(int[] docTerm : postingList){
				int docid = docTerm[0];
				int termFreq = docTerm[1];

				if(!docPosting.containsKey(docid)){
					Map<String, Integer> tokenDocFreq = new HashMap<>();
					tokenDocFreq.put(token, termFreq);
					docPosting.put(docid, tokenDocFreq);
				}
				else{
					Map<String, Integer> tokenDocFreq = docPosting.get(docid);
					tokenDocFreq.put(token, termFreq);
				}
			}
		}

		//compare the document scores
		Comparator<Document> documentComparator = new Comparator<Document>() {
			@Override
			public int compare(Document o1, Document o2) {
				if(o1.score() < o2.score())
					return -1;
				else if(o1.score() > o2.score())
					return 1;
				return 0;
			}
		};


        PriorityQueue<Document> topNDocs = new PriorityQueue<>(TopN, documentComparator);

        // score the relevant documents and put them into topN priority queue
        for(Map.Entry<Integer, Map<String, Integer>> entry : docPosting.entrySet()){
            int docid = entry.getKey();
            Map<String, Integer> termFreq = entry.getValue();
            double score = 1.0;

            for(String token: tokensInQuery){
                if(!tokenCollectionFreq.containsKey(token))
                    continue;
                //calculate the probability of term in the collection
                double probTermCollection = (double) tokenCollectionFreq.get(token)/(double) collectionLength;
                double termInDoc = (double)termFreq.getOrDefault(token, 0);
                score *= (termInDoc + miu * probTermCollection) / (indexReader.docLength(docid) + miu);
            }
            Document doc = new Document(Integer.toString(docid), indexReader.getDocno(docid), score);
            if(topNDocs.size() < TopN){
                topNDocs.add(doc);
            }
            else if(score > topNDocs.peek().score()){
                topNDocs.poll();
                topNDocs.add(doc);
            }

        }

        List<Document> docList = new ArrayList<>();
        while(!topNDocs.isEmpty()){
            docList.add(0, topNDocs.poll());
        }
        if(docList.size() == 0)
            return null;
        return docList;
	}
	
}