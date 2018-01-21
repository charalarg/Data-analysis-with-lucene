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
import java.util.Comparator;

// From chapter 1

/**
 * This code was originally written for
 * Erik's Lucene intro java.net article
 */
@SuppressWarnings("unused")
public class Indexer {

//orisma "index" "newcran"	

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

    //System.out.println("Indexing " + numIndexed + " files took " + (end - start) + " milliseconds");

 
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
	   return doc;
	  }

  private void indexFile(File f) throws Exception {
    //System.out.println("Indexing " + f.getCanonicalPath());
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
        ArrayList<word> words = new ArrayList<word>(); 
		br = new BufferedReader(new FileReader(new File(inputFile)));
		
		
         String line=null;
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
                		String tempname= trim.split("\\s+")[i];
                		if(containsName(words,trim.split("\\s+")[i])){
                			words.stream().filter(o -> o.getName().equals(tempname)).findFirst().get().addFreq();;
                		}             			
                		else
                			words.add(new word(trim.split("\\s+")[i],1));
                	}
                }
            	}
                      
            }
			br.close();

			words.sort(Comparator.comparing(word::getFreq));
			System.out.println("synolo leksewn: " +wordCount);
			System.out.println("synolo monadikwn leksewn: " +words.size());
			System.out.println("");
			System.out.println("oi 10 pio syxnes lekseis sthn syllogh: ");
			System.out.println("");
			for(int i=words.size()-1;i>words.size()-10;i--){
				System.out.println(words.get(i).getName() +" syxnothta " +words.get(i).getFreq());
			}
			System.out.println("");
			System.out.println("oi 10 pio syxnes lekseis sthn syllogh pou ksekinan me 's': ");
			System.out.println("");
			int count=0;
			int j=words.size()-1;
			while (count<10){
					if(words.get(j).getName().startsWith("s")||words.get(j).getName().startsWith("S")){
						System.out.println(words.get(j).getName() +" syxnothta " +words.get(j).getFreq());
						count++;
					}
				j--;
			}
	}

public static boolean containsName( ArrayList<word> list, String name){
    return list.stream().filter(o -> o.getName().equals(name)).findFirst().isPresent();
}


}





