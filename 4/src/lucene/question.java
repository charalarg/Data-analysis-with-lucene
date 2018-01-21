package lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class question {

	private int numb;
	private ArrayList<Integer> relevantDocs=new ArrayList<Integer>();
	private int totalRelevant;
	
	public question(int numb, ArrayList<Integer> relevantDocs, int totalRelevant) {
		this.numb = numb;
		this.relevantDocs = relevantDocs;
		this.totalRelevant = totalRelevant;
	}
	
	
	public int getNumb() {
		return numb;
	}
	public void setNumb(int numb) {
		this.numb = numb;
	}
	public ArrayList<Integer> getRelevantDocs() {
		return relevantDocs;
	}
	public void setRelevantDocs(ArrayList<Integer> relevantDocs) {
		this.relevantDocs=relevantDocs;
	}
	public int getTotalRelevant() {
		return totalRelevant;
	}
	public void setTotalRelevant(int totalRelevant) {
		this.totalRelevant = totalRelevant;
	}
	
	
	public void findrelevant() throws IOException{
		
		String inputFile="cran\\cranqrel.txt";
		BufferedReader br = null;
		br = new BufferedReader(new FileReader(new File(inputFile)));
		String line=null;
		this.setNumb(this.numb);
		while((line = br.readLine()) != null){
			if(line.startsWith(String.valueOf(this.numb)+" ")){
				String[] strs = line.trim().split("\\s+");
				this.relevantDocs.add(Integer.parseInt(strs[1]));
				totalRelevant++;
        	}
		}
		this.setTotalRelevant(totalRelevant);
		br.close();	
	}
	
	
	
}
