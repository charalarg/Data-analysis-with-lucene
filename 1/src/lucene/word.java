package lucene;

public class word {
	
	String name;
	int freq;

	public word(String name, int freq) {
		super();
		this.name = name;
		this.freq = freq;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getFreq() {
		return freq;
	}
	public void setFreq(int freq) {
		this.freq = freq;
	}
	public void addFreq(){
		this.freq++;
	}
	
	
}
