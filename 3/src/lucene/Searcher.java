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
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;
import org.apache.lucene.index.*;
import org.apache.lucene.search.ScoreDoc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

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
   
    
    QueryParser parser = new QueryParser("contents",  //4
                     new StandardAnalyzer());  //4
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
