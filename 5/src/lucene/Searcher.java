package lucene;

//package lia.meetlucene;

/**
 * Copyright Manning Publications Co.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific lan      
*/

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;
import org.apache.lucene.index.*;
import org.apache.lucene.search.ScoreDoc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import jdk.internal.org.objectweb.asm.tree.analysis.Analyzer;

// From chapter 1

/**
 * This code was originally written for
 * Erik's Lucene intro java.net article
 */
@SuppressWarnings("unused")
public class Searcher {

//orisma pleon pernaw mono to Index kai oxi thn errwthsh afoy ginontai apo dw parse oles
//orisma "index"
  public static void main(String[] args) throws IllegalArgumentException,
        IOException, ParseException {
    if (args.length != 1) {
      throw new IllegalArgumentException("Usage: java " + Searcher.class.getName()
        + " <index dir>");
    }
    
    String indexDir = args[0];               //1 
    //String q = args[1];						//2   
    
    ArrayList<Document> returnedDocs =new ArrayList<Document>();
    
    //parsing cranqrel
    ArrayList<ArrayList<Integer>> AllrelevantDocs=new ArrayList<ArrayList<Integer>>();
    ArrayList<question> questions=new ArrayList<question>();
    ArrayList<Integer> totalRel=new ArrayList<Integer>();  
    
    AllrelevantDocs.add(new ArrayList<Integer>());
    totalRel.add(0);
    for (int i=1;i<226;i++){
    	AllrelevantDocs.add(new ArrayList<Integer>());
    	totalRel.add(0);
    }  
    questions.add(new question(0,AllrelevantDocs.get(0),totalRel.get(0)));
    for (int i=1;i<226;i++){
    	questions.add(new question(i,AllrelevantDocs.get(i),totalRel.get(i)));
    	questions.get(i).findrelevant();
    }

    
    //parsing cran    
    String inputFile="cran\\cran.qry"; 
    StringBuilder value = new StringBuilder();
    StringBuilder value2 = new StringBuilder();
	String line=null;
	BufferedReader br = null;
	br = new BufferedReader(new FileReader(new File(inputFile)));
	int questionID=0;
	String[] strs= null;  
	
	line = br.readLine();
	while(line!= null){	
		if(line.startsWith(".I")){
			strs = line.trim().split("\\s+");
			value.append(strs[1]);			  			   
			questionID=Integer.parseInt(value.toString().replaceAll("^0+", ""));
		}
		line = br.readLine();
		if(line.startsWith(".W")){
			line = br.readLine();
			while( !(line.startsWith(".I")) && line!=null ){
				line = line.replaceAll("[-+.^:,/()!><?;~*=_&%$#@]"," ");
				value2.append(line);
				value2.append(" ");
				line = br.readLine();
			} 			
		}	
	if(questionID==AllrelevantDocs.size())
	break;
	
	//search results for each question   
    returnedDocs=search(indexDir, value2.toString());
    int returnedRelevant=0;
   
    for(int i=0;i<returnedDocs.size();i++){
    	for(int j=0;j<questions.get(questionID).getTotalRelevant();j++){
    		if(Integer.parseInt(returnedDocs.get(i).get("ID"))==questions.get(questionID).getRelevantDocs().get(j)){
    			returnedRelevant++;
    		}
    	}
    }
    
    double Precision=0;
    double Recall=0;
    
    Precision=(double)returnedRelevant/(double)returnedDocs.size();
    Recall=(double)returnedRelevant/(double)questions.get(questionID).getTotalRelevant();
    
    
    System.out.println("Precision for question " + questionID +" = " + Precision);
   	System.out.println("Recall for question " + questionID +" = " + Recall);
    
    value = new StringBuilder();
    value2 = new StringBuilder();
    strs= null;
	questionID=0;
    
	}
    
    br.close();
    
    
  }

  public static ArrayList<Document> search(String indexDir, String q)
    throws IOException, ParseException {
    File ff=new File(indexDir);
    Directory dir = FSDirectory.open(ff.toPath()); //3
    IndexReader reader = DirectoryReader.open(dir);
    IndexSearcher is = new IndexSearcher(reader);
    ArrayList<Document> returnedDocs =new ArrayList<Document>();
   
    
    CharArraySet stopSet = CharArraySet.copy(StandardAnalyzer.STOP_WORDS_SET);
    String[] stopwords = {"a", "as", "able", "about", "above", "according", "accordingly", "across", "actually", "after", "afterwards", "again", "against", "aint", "all", "allow", "allows", "almost", "alone", "along", "already", "also", "although", "always", "am", "among", "amongst", "an", "and", "another", "any", "anybody", "anyhow", "anyone", "anything", "anyway", "anyways", "anywhere", "apart", "appear", "appreciate", "appropriate", "are", "arent", "around", "as", "aside", "ask", "asking", "associated", "at", "available", "away", "awfully", "be", "became", "because", "become", "becomes", "becoming", "been", "before", "beforehand", "behind", "being", "believe", "below", "beside", "besides", "best", "better", "between", "beyond", "both", "brief", "but", "by", "cmon", "cs", "came", "can", "cant", "cannot", "cant", "cause", "causes", "certain", "certainly", "changes", "clearly", "co", "com", "come", "comes", "concerning", "consequently", "consider", "considering", "contain", "containing", "contains", "corresponding", "could", "couldnt", "course", "currently", "definitely", "described", "despite", "did", "didnt", "different", "do", "does", "doesnt", "doing", "dont", "done", "down", "downwards", "during", "each", "edu", "eg", "eight", "either", "else", "elsewhere", "enough", "entirely", "especially", "et", "etc", "even", "ever", "every", "everybody", "everyone", "everything", "everywhere", "ex", "exactly", "example", "except", "far", "few", "ff", "fifth", "first", "five", "followed", "following", "follows", "for", "former", "formerly", "forth", "four", "from", "further", "furthermore", "get", "gets", "getting", "given", "gives", "go", "goes", "going", "gone", "got", "gotten", "greetings", "had", "hadnt", "happens", "hardly", "has", "hasnt", "have", "havent", "having", "he", "hes", "hello", "help", "hence", "her", "here", "heres", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "hi", "him", "himself", "his", "hither", "hopefully", "how", "howbeit", "however", "i", "id", "ill", "im", "ive", "ie", "if", "ignored", "immediate", "in", "inasmuch", "inc", "indeed", "indicate", "indicated", "indicates", "inner", "insofar", "instead", "into", "inward", "is", "isnt", "it", "itd", "itll", "its", "its", "itself", "just", "keep", "keeps", "kept", "know", "knows", "known", "last", "lately", "later", "latter", "latterly", "least", "less", "lest", "let", "lets", "like", "liked", "likely", "little", "look", "looking", "looks", "ltd", "mainly", "many", "may", "maybe", "me", "mean", "meanwhile", "merely", "might", "more", "moreover", "most", "mostly", "much", "must", "my", "myself", "name", "namely", "nd", "near", "nearly", "necessary", "need", "needs", "neither", "never", "nevertheless", "new", "next", "nine", "no", "nobody", "non", "none", "noone", "nor", "normally", "not", "nothing", "novel", "now", "nowhere", "obviously", "of", "off", "often", "oh", "ok", "okay", "old", "on", "once", "one", "ones", "only", "onto", "or", "other", "others", "otherwise", "ought", "our", "ours", "ourselves", "out", "outside", "over", "overall", "own", "particular", "particularly", "per", "perhaps", "placed", "please", "plus", "possible", "presumably", "probably", "provides", "que", "quite", "qv", "rather", "rd", "re", "really", "reasonably", "regarding", "regardless", "regards", "relatively", "respectively", "right", "said", "same", "saw", "say", "saying", "says", "second", "secondly", "see", "seeing", "seem", "seemed", "seeming", "seems", "seen", "self", "selves", "sensible", "sent", "serious", "seriously", "seven", "several", "shall", "she", "should", "shouldnt", "since", "six", "so", "some", "somebody", "somehow", "someone", "something", "sometime", "sometimes", "somewhat", "somewhere", "soon", "sorry", "specified", "specify", "specifying", "still", "sub", "such", "sup", "sure", "ts", "take", "taken", "tell", "tends", "th", "than", "thank", "thanks", "thanx", "that", "thats", "thats", "the", "their", "theirs", "them", "themselves", "then", "thence", "there", "theres", "thereafter", "thereby", "therefore", "therein", "theres", "thereupon", "these", "they", "theyd", "theyll", "theyre", "theyve", "think", "third", "this", "thorough", "thoroughly", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "took", "toward", "towards", "tried", "tries", "truly", "try", "trying", "twice", "two", "un", "under", "unfortunately", "unless", "unlikely", "until", "unto", "up", "upon", "us", "use", "used", "useful", "uses", "using", "usually", "value", "various", "very", "via", "viz", "vs", "want", "wants", "was", "wasnt", "way", "we", "wed", "well", "were", "weve", "welcome", "well", "went", "were", "werent", "what", "whats", "whatever", "when", "whence", "whenever", "where", "wheres", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whos", "whoever", "whole", "whom", "whose", "why", "will", "willing", "wish", "with", "within", "without", "wont", "wonder", "would", "would", "wouldnt", "yes", "yet", "you", "youd", "youll", "youre", "youve", "your", "yours", "yourself", "yourselves", "zero"};
    for (int i=0;i<stopwords.length;i++){
    	stopSet.add(stopwords[i]);
    }
    
    StopAnalyzer analyzer = new StopAnalyzer(stopSet);
    //StandardAnalyzer analyzer = new StandardAnalyzer();
    //System.out.println(analyzer.getStopwordSet());
    
    MultiFieldQueryParser parser = new MultiFieldQueryParser(
            new String[] {"abstract", "title"},
            analyzer);
    
    
    //QueryParser parser = new QueryParser("contents",  //4
                    // new StandardAnalyzer());  //4
    Query query = parser.parse(q);              //4   
    long start = System.currentTimeMillis();
    TopDocs hits = is.search(query, 1400); //5
    long end = System.currentTimeMillis();
    
    //System.err.println("Found " + hits.totalHits +   " document(s) (in " + (end - start) +        " milliseconds) that matched query '" +      q + "':");                    //6               
    
    for(ScoreDoc scoreDoc : hits.scoreDocs) {
      Document doc = is.doc(scoreDoc.doc);               //7      
      returnedDocs.add(doc);
      //System.out.println(doc.get("fullpath"));//8 
    }
    
   reader.close();  					  //9
   return returnedDocs;                              
  }
}

/*
#1 Parse provided index directory
#2 Parse provided query string
#3 Open index
#4 Parse query
#5 Search index
#6 Write search stats
#7 Retrieve matching document
#8 Display filename
#9 Close IndexSearcher
*/
