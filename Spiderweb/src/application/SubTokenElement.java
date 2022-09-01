package application;

/*
 * represents the elements of the first and second level synsets of a word
 * will need to hold onto an IFV value, then have an effective IFV value
 */


public class SubTokenElement {
	
	final private String word;
	private Double IFV;
	private Double effectiveIFV;
		
	public SubTokenElement(String word) {
		this.word = word;
	}

	public Double getEffectiveIFV() {
		return effectiveIFV;
	}

	public void setEffectiveIFV(Double effectiveIFV) {
		this.effectiveIFV = effectiveIFV;
	}

	public Double getIFV() {
		return IFV;
	}

	public void setIFV(Double iFV) {
		IFV = iFV;
	}

	public String getWord() {
		return word;
	}

}
