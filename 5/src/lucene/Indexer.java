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

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

// From chapter 1

/**
 * This code was originally written for
 * Erik's Lucene intro java.net article
 */
@SuppressWarnings("unused")
public class Indexer {

//orismata "index" "newcran"	
  public static void main(String[] args) throws Exception {
    if (args.length != 2) {
      throw new IllegalArgumentException("Usage: java " + Indexer.class.getName()
        + " <index dir> <data dir>");
    }
    
    
    SplitText();
    String indexDir = args[0];         //1
    String dataDir = args[1];         //2

    long start = System.currentTimeMillis();
    Indexer indexer = new Indexer(indexDir);
    int numIndexed;
    try {
      numIndexed = indexer.index(dataDir, new TextFilesFilter());
    } finally {
      indexer.close();
    }
    long end = System.currentTimeMillis();

    System.out.println("Indexing " + numIndexed + " files took "
      + (end - start) + " milliseconds");

 
  }



private static IndexWriter writer;

  public Indexer(String indexDir) throws IOException {
	File ff= new File(indexDir);
    Directory dir = FSDirectory.open(ff.toPath());
    IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
    writer = new IndexWriter(dir,config); //3

  }

  public void close() throws IOException {
    writer.close();                             //4
  }

  public int index(String dataDir, FileFilter filter)
    throws Exception {

    File[] files = new File(dataDir).listFiles();

    for (File f: files) {
      if (!f.isDirectory() &&
          !f.isHidden() &&
          f.exists() &&
          f.canRead() &&
          (filter == null || filter.accept(f))) {
        indexFile(f);
        
        
        
        
      }
    }

    return writer.numDocs();                     //5
  }

  private static class TextFilesFilter implements FileFilter {
    public boolean accept(File path) {
      return path.getName().toLowerCase()        //6
             .endsWith(".txt");                  //6
    }
  }

  public Document getDocument(File f) throws Exception {
	  Document doc = new Document();
	  doc.add(new TextField("contents", new FileReader(f)));
	   doc.add(new StringField("filename",  f.getName(), Field.Store.YES));
	   doc.add(new StringField("fullpath", f.getCanonicalPath(), Field.Store.YES));
	   
	   //add id
	   StringBuilder value3 = new StringBuilder();
	   String line3=null;
	   BufferedReader br3 = null;
	   br3 = new BufferedReader(new FileReader(f)); 
	   line3 = br3.readLine();
	   String[] strs = line3.trim().split("\\s+");
	   value3.append(strs[1]);			  			   
	   value3.toString(); 
	   doc.add(new TextField("ID",  value3.toString(),  Field.Store.YES));
	   
	   //add title
	   StringBuilder value = new StringBuilder();
	   String line=null;
	   String last=null;
	   BufferedReader br = null;
	   br = new BufferedReader(new FileReader(f));
	   
	   while((line = br.readLine()) != null){
		   if(line.startsWith(".T")){
			   while(!(line = br.readLine()).startsWith(".A")){
				   value.append(line);
				   value.append(" ");
				   last=line;
			   }
			   value.append(last);
       		}
	   } 
	   value.toString(); 
	   TextField boostedField = new TextField("title",  value.toString(),  Field.Store.YES);
	   boostedField.setBoost(2);
	   doc.add(boostedField);
	   
	   
	   //add Author
	   StringBuilder value1 = new StringBuilder();
	   String line1=null;
	   String last1=null;
	   BufferedReader br1 = null;
	   br1 = new BufferedReader(new FileReader(f));
	   
	   while((line1 = br1.readLine()) != null){
		   if(line1.startsWith(".A")){
			   while(!(line1 = br1.readLine()).startsWith(".B")){
				   value1.append(line1);
			   }
       		}
	   } 
	   value1.toString();  
	   TextField boostedField2 = new TextField("author",  value.toString(),  Field.Store.YES);
	   boostedField2.setBoost(1);
	   doc.add(boostedField2);
	   
	   //add abtract
	   StringBuilder value2 = new StringBuilder();
	   String line2=null;
	   String last2=null;
	   BufferedReader br2 = null;
	   br2 = new BufferedReader(new FileReader(f));
	   
	   while((line2 = br2.readLine()) != null){
		   if(line2.startsWith(".W")){
			   while((line2 = br2.readLine()) != null){
				   value2.append(line2);
				   value2.append(" ");
				   last2=line2;
			   }
			   value2.append(last2);
       		}
	   }
	   value2.toString(); 
	   TextField boostedField3 = new TextField("abstract",  value.toString(),  Field.Store.YES);
	   boostedField3.setBoost(1);
	   doc.add(boostedField3);
	   
	   
	   
	   br3.close();
	   br2.close();
	   br.close();
	   br1.close();
	   return doc;
	  }

  private void indexFile(File f) throws Exception {
    System.out.println("Indexing " + f.getCanonicalPath());
    Document doc = getDocument(f);
    writer.addDocument(doc);                              //10
  }


/*
#1 Create index in this directory
#2 Index *.txt files from this directory
#3 Create Lucene IndexWriter
#4 Close IndexWriter
#5 Return number of documents indexed
#6 Index .txt files only, using FileFilter
#7 Index file content
#8 Index file name
#9 Index file full path
#10 Add document to Lucene index
*/

public static void SplitText() throws IOException{

        String inputFile="cran\\cranall.txt"; 
        BufferedReader br = null;
        ArrayList<String> words = new ArrayList<String>(); 
		br = new BufferedReader(new FileReader(new File(inputFile)));
	
         String line=null;
         int count=0;
         File file = new File("newcran\\DOC_"+count+".txt");
         count++;
         PrintWriter writer = new PrintWriter(file, "UTF-8");
         	int wordCount=0;
            while((line = br.readLine()) != null){
            	//metrish leksewn
            	if(line.startsWith(".")){
            	}
            	else{
            	line = line.replaceAll("[-+.^:,/()!><?;~*=_&%$#@]"," ");
            	String trim = line.trim();
                if (trim.isEmpty()){
                	wordCount = wordCount+0;
                }
                else{
                	wordCount += trim.split("\\s+").length;
                	//metrish monadikwn leksewn
                	for(int i=0;i<trim.split("\\s+").length;i++){
                		if(words.contains(trim.split("\\s+")[i])){}             			
                		else
                			words.add(trim.split("\\s+")[i]);
                	}
                }
            	}
                
                if(line.startsWith(".I")){
                		writer.close();  
                        file = new File("newcran\\DOC_"+count+".txt");
                        count++;     
                        writer = new PrintWriter(file, "UTF-8");
                }
                writer.println(line);      
            }
			br.close();
			writer.close(); 
			Path fileToDeletePath = Paths.get("newcran\\DOC_0.txt");
			Files.delete(fileToDeletePath);
			
			//System.out.println("synolo leksewn: " +wordCount);
			//System.out.println("synolo monadikwn leksewn: " +words.size());
	}


}





