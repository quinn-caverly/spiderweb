package application;

import java.util.ArrayList;

import edu.mit.jwi.item.POS;

/*
 * Represents the lemma of the individual word and represents information necessary for the classification
 * the POS is in the format of the MIT JWI POS
 */

public class TokenElement {
	
	final private String originalWord;
	final private String lemma;
	private String usedToken; //the word which actually is used, either lemma or original word
	
	final private POS partOfSpeech;
	private ArrayList<String> firstLevelRelatedWords;
	private ArrayList<String> secondLevelRelatedWords;
	
	private double IFV;

	
	public TokenElement(String originalWord, String lemma, POS partOfSpeech) {
		this.originalWord = originalWord;
		this.lemma = lemma;
		this.partOfSpeech = partOfSpeech;
	}
	
	public String getLemma() {
		return lemma;
	}

	public POS getPartOfSpeech() {
		return partOfSpeech;
	}

	public String getOriginalWord() {
		return originalWord;
	}

	public ArrayList<String> getFirstLevelRelatedWords() {
		return firstLevelRelatedWords;
	}

	public void setFirstLevelRelatedWords(ArrayList<String> firstLevelRelatedWords) {
		this.firstLevelRelatedWords = firstLevelRelatedWords;
	}

	public ArrayList<String> getSecondLevelRelatedWords() {
		return secondLevelRelatedWords;
	}

	public void setSecondLevelRelatedWords(ArrayList<String> secondLevelRelatedWords) {
		this.secondLevelRelatedWords = secondLevelRelatedWords;
	}

	public String getUsedToken() {
		return usedToken;
	}

	public void setUsedToken(String usedToken) {
		this.usedToken = usedToken;
	}

	public double getIFV() {
		return IFV;
	}

	public void setIFV(double iFV) {
		IFV = iFV;
	}
	
}
