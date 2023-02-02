package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import application.NoteChooserHandler.Note;
import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import fxmlcontrollers.notetypes.DailyScrollController;
import fxmlcontrollers.notetypes.DailyTypeNoteController;
import fxmlcontrollers.notetypes.ReadingTypeNoteController;
import fxmlcontrollers.notetypes.StandardTypeNoteController;
import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

public class PipelineNLP {

	InputStream tokenBinInputStream;
	Tokenizer tokenizer;
	TokenizerModel tokenizerModel;
	
	InputStream sentenceBinInputStream;
	SentenceModel sentenceModel;
	SentenceDetectorME sentenceDetector;
	
	InputStream partofSpeechBinInputStream;
	POSModel PartOfSpeechModel;
	POSTaggerME partOfSpeechTagger;
	
	InputStream lemmatizerDictInputStream;
	DictionaryLemmatizer lemmatizer;
	
	IDictionary dictionaryFromJWI;
	String pathToDict = "lib/wordnetdict";
	
	Map<String, Double> IFVDictionary;
	Double halfValue;
	
	
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
	
	
	
	/*
	 * 2/1/2023-9:03PM --- 5/5
	 * 
	 * references http://www.diva-portal.org/smash/get/diva2:1461669/FULLTEXT01.pdf
	 */
	public PipelineNLP() {
		try {
			tokenBinInputStream = new FileInputStream("lib/en-token.bin");
			tokenizerModel = new TokenizerModel(tokenBinInputStream);
			tokenizer = new TokenizerME(tokenizerModel);
			
			sentenceBinInputStream = new FileInputStream("lib/en-sentence.bin");
			sentenceModel = new SentenceModel(sentenceBinInputStream);
			sentenceDetector = new SentenceDetectorME(sentenceModel);
			
			partofSpeechBinInputStream = new FileInputStream("lib/en-pos-maxent.bin");
			PartOfSpeechModel = new POSModel(partofSpeechBinInputStream);
			partOfSpeechTagger = new POSTaggerME(PartOfSpeechModel);
			
			lemmatizerDictInputStream = new FileInputStream("lib/en-lemmatizer.txt");
			lemmatizer = new DictionaryLemmatizer(lemmatizerDictInputStream);
			
			dictionaryFromJWI = new Dictionary(new URL("file", null, pathToDict));
			dictionaryFromJWI.open();
			
			File csvFile = new File("lib/unigram_freq.csv");
			Scanner csvScanner = new Scanner(csvFile);
			IFVDictionary = new HashMap<String, Double>();
						
			Integer topper = 30000; //only want the first 30,000 values of the dictionary
			while (csvScanner.hasNext() && topper > 0) {
				String[] lst = csvScanner.next().split(",");
				
				IFVDictionary.put(lst[0], Math.pow(10, 10) * 1/Double.valueOf(lst[1]));
				
				topper -= 1;
			}
			csvScanner.close();
			
			halfValue = IFVDictionary.get("supporter");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * the "main" method for this class
	 * has no return, directly modifies the inputed TreeItem<Note>
	 * 
	 * below is an overview of the pipeline
	 * 
	 * separate sentences
	 * tokenize
	 * POS
	 * lemma
	 * first level synset
	 * second level synset
	 * IFV value of original
	 * assign "IFV" values to first and second level synsets
	 * generate classifierMap (matrix)
	 */
	public void runThroughPipeline(Note currentNote) {
		
		String[] separateSentence = sentenceDetector.sentDetect(getContents(currentNote));
		
		ArrayList<String[]> sentencesOfTokens = separateTokens(separateSentence);
		
		ArrayList<TokenElement> tokenElementList = createTokenElementList(sentencesOfTokens);
		
		addSynsets(tokenElementList); //modifies the elements
		
		TreeMap<String, Double> classifierMap = returnClassifierMap(tokenElementList);
		
		currentNote.setClassifierMap(classifierMap);
	}
	
	
	/*
	 * 2/1/2023-9:05PM --- 5/5
	 * 
	 * Tokenized, but still the sentences are kept intact
	 * is effectively an arraylist of sentences, those sentences are a string array of the tokens
	 */
	private ArrayList<String[]> separateTokens(String[] sentences) {
		ArrayList<String[]> sentencesOfTokens = new ArrayList<String[]>();
		
		for (int i=0; i < sentences.length; i++) {
			sentencesOfTokens.add(tokenizer.tokenize((sentences[i])));
		}
		return sentencesOfTokens;
	}
	
	
	/*
	 * this function also tags the POS of the word
	 * the POS of the word is used for taking the Lemma of the word in this method using the OpenNLP
	 * then the POS is converted to the format of the MIT JWI for synset creating later
	 * 
	 * after this step the sentence data is no longer needed, so each token
	 * is now an element independent of its neighbors
	 * 
	 * also filters out unnecessary tokens 
	 */
	private ArrayList<TokenElement> createTokenElementList(ArrayList<String[]> sentencesOfTokens) {
		
		ArrayList<TokenElement> tokenElementList = new ArrayList<TokenElement>();
		
		for (int i=0; i < sentencesOfTokens.size(); i++) {
			
			String tokens[] = sentencesOfTokens.get(i);
			
			String tags[] = partOfSpeechTagger.tag(tokens);
			
			String lemmas[] = lemmatizer.lemmatize(sentencesOfTokens.get(i), tags);
			
			// iterates through each word in the sentence, adding it to the tokenElement list
			
			for (int j=0; j < tags.length; j++) {
				//substring is important here, the OpenNLP POS are often a few letters
				//for the JWI, the POS needs to be just one, the first character takes what we need from the OpenNLP POS
				String partOfSpeechTag = tags[j].substring(0, 1);
				String lemma = lemmas[j];
				String token = tokens[j];
								
				POS POSTag = null;
				/*
				 * the 4 part of speech tags which matter for the JWI are
				 * POS. ...
				 * NOUN, ADJECTIVE, VERB, ADVERB
				 */
				
				/*
				 * also acts as a sort of a filter due to:
				 * 
				 * The parts of speech which are left out include,
				 * pronouns, articles, quantitative and other irrelevant adjectives
				 * punctuation is also excluded
				 */
				if (partOfSpeechTag.equals("J")) {
					POSTag = POS.ADJECTIVE;
				}
				
				else if (partOfSpeechTag.equals("N")) {
					POSTag = POS.NOUN;
				}
				
				else if (partOfSpeechTag.equals("V") || partOfSpeechTag.equals("M")) {
					POSTag = POS.VERB;
				}
				
				else if (partOfSpeechTag.equals("R")) {
					POSTag = POS.ADVERB;
				}
				
				if (POSTag != null) {
					tokenElementList.add(new TokenElement(token, lemma, POSTag)); // token element must use the lemma and not the original word
				}
			}
		}		
		
		return tokenElementList;		
	}
	
	
	/*
	 * adds the first level and second level synset to be matched to the token element
	 * also does duplication management, if a word is in the first level of similar words and the second,
	 * then the first level takes priority and it is removed from the second level
	 */
	private void addSynsets(ArrayList<TokenElement> tokenElementList) {
		
		String tokenToUse;
			
		for (TokenElement tokenElement : tokenElementList) {
			
			if (!tokenElement.getLemma().equals("O")) { //this will output "O" if there is no lemma given
				tokenToUse = tokenElement.getLemma();
			}
			else { //word will be given, but it has to be cleaned first
				//lemmatization implicitly places the words into lowercase
				tokenToUse = tokenElement.getOriginalWord().toLowerCase();
			}
			
			tokenElement.setUsedToken(tokenToUse);

			/*
			 * need to check if the word actually exists in the JWI Dictionary
			 * if it does not exist in the directory, still keeps it but the similars arraylists just stay null,
			 * in this case helper does not need to be run
			 */
			
			try {
				IIndexWord idxLemma = dictionaryFromJWI.getIndexWord(tokenToUse, tokenElement.getPartOfSpeech());
				idxLemma.getWordIDs().get(0); //1st meaning
				
				addSynsetsHelper(tokenElement, tokenToUse);
				
			} catch (NullPointerException e) {
				//helper does not need to be run if this exception is caught 
			}
		}
	}
	
	
	/*
	 * This is run if the word actually has valid synsets
	 * simply adds the reference to the individual token
	 * 
	 * only uses lemmas which do not have underscores
	 * this is because the underscores do not work for the dictionary of IFV's because they are tokenized elements
	 * this should not be a problem because words with many connections tend to have many words with underscores,
	 * words with few connections tend to have few underscores, it should not effect the results significantly
	 */
	private void addSynsetsHelper(TokenElement tokenElement, String tokenToUse) {
		
	    ArrayList<String> listOfFirstLevelSimilars = new ArrayList<String>();
	    ArrayList<String> listOfSecondLevelSimilars = new ArrayList<String>();

		IIndexWord idxLemma = dictionaryFromJWI.getIndexWord(tokenToUse, tokenElement.getPartOfSpeech()); //this method takes a pre-lemmatized word, which has been done in the createTokenElementList method
		IWordID lemmaID = idxLemma.getWordIDs().get(0); //1st meaning
		IWord lemma = dictionaryFromJWI.getWord(lemmaID);
		ISynset synset = lemma.getSynset();
			
		//these are the related words
		for (IWord synsetWord :synset.getWords()) {
			if (!synsetWord.getLemma().contains("_")) {
			listOfFirstLevelSimilars.add(synsetWord.getLemma());
			}
		}
		
		//these are the sets of the related words to the original synset
		for (ISynsetID relatedSynsetID : synset.getRelatedSynsets()) {
			
			ISynset relatedSynset = dictionaryFromJWI.getSynset(relatedSynsetID);
			
			for (IWord synsetWord : relatedSynset.getWords()) {
				if (!synsetWord.getLemma().contains("_")) {
				listOfSecondLevelSimilars.add(synsetWord.getLemma());
				}
			}				
		}
		
		//removes a word from second list if it is in the first 
		for (int i = 0; i < listOfSecondLevelSimilars.size(); i++) {	
			if (listOfFirstLevelSimilars.contains(listOfSecondLevelSimilars.get(i))) {
				listOfSecondLevelSimilars.remove(i);
			}
					
		}
		
		//removes the word from the first level if it is the word itself
		for (int i = 0; i < listOfFirstLevelSimilars.size(); i++) {
			if (listOfFirstLevelSimilars.get(i).equals(tokenToUse)) {
				listOfFirstLevelSimilars.remove(i);
			}
		}
		
		tokenElement.setFirstLevelRelatedWords(listOfFirstLevelSimilars);
		tokenElement.setSecondLevelRelatedWords(listOfSecondLevelSimilars);
	}
	
	/*
	 * adds the IFV to the original word and also to the first and second level synsets
	 * if a word is not in IFV dictionary, given value of supporter, 15,000th word
	 * 
	 * also sorts the map with the highest values at the top
	 */
	
	private TreeMap<String, Double> returnClassifierMap(ArrayList<TokenElement> tokenList) {
		
		TreeMap<String, Double> classifierMap = new TreeMap<String, Double>();
				
		for (TokenElement tokenElement : tokenList) {
			
			
			setIFV(tokenElement);
			
			ArrayList<SubTokenElement> firstLevelList = null;
			ArrayList<SubTokenElement> secondLevelList = null;

						
			//first level words have a sum which matches the IFV of original
			if (tokenElement.getFirstLevelRelatedWords()!= null) {
				firstLevelList = createSubTokenList(tokenElement, tokenElement.getFirstLevelRelatedWords(), tokenElement.getIFV());
			}
			
			//second level words have a sum which matches the IFV of original / 4
			if (tokenElement.getSecondLevelRelatedWords()!= null) {
				secondLevelList = createSubTokenList(tokenElement, tokenElement.getSecondLevelRelatedWords(), tokenElement.getIFV()/4);
			}
			
			/*
			 * if there are multiple entries of a single word, then the value of that word is
			 * IFV of the word if there were only one * repetitions
			 */
			
			if (classifierMap.containsKey(tokenElement.getUsedToken())) {
				classifierMap.put(tokenElement.getUsedToken(), tokenElement.getIFV() + classifierMap.get(tokenElement.getUsedToken()));
			}
			else {
				
			}
			
			classifierMap.put(tokenElement.getUsedToken(), tokenElement.getIFV());
			
			addToMatrix(classifierMap, firstLevelList);
			addToMatrix(classifierMap, secondLevelList);
		}
		
		return classifierMap;
	}
	
	/*
	 * a method which is conceptually apart of addIFV but avoids code repeat
	 */
	private void setIFV(TokenElement tokenElement) {
		// if the word is not in the dictionary, assigned the value of "supporter"
		//this is the 15,000 word in the dictionary, half of the 30,000 
		if (IFVDictionary.get(tokenElement.getUsedToken()) == null) {
			tokenElement.setIFV(halfValue);
		}
		else {
			tokenElement.setIFV(IFVDictionary.get(tokenElement.getUsedToken()));
		}
	}
	
	/*
	 * Same as above but for subTokenElements
	 */
	private Double setAndReturnIFV(SubTokenElement tokenElement, String similarWord) {
		// if the word is not in the dictionary, assigned the value of "supporter"
		//this is the 15,000 word in the dictionary, half of the 30,000 
		if (IFVDictionary.get(similarWord) == null) {
			tokenElement.setIFV(halfValue);
			
			return halfValue;
		}
		else {
			tokenElement.setIFV(IFVDictionary.get(similarWord));
			
			return IFVDictionary.get(similarWord);
		}
	}
	
	private ArrayList<SubTokenElement> createSubTokenList(TokenElement tokenElement, ArrayList<String> listOfLevelStrings, Double totalAvailable) {
		
		Double sumIFV = 0.0;
		ArrayList<SubTokenElement> levelList = new ArrayList<SubTokenElement>();
		
		//first assign IFV then sum up the IFVs for this level
		for (String levelSimilar : listOfLevelStrings) {
			SubTokenElement subTokenForSimilar = new SubTokenElement(levelSimilar);
			levelList.add(subTokenForSimilar);
			
			sumIFV += setAndReturnIFV(subTokenForSimilar, levelSimilar);
		}
		
		/*
		 * then assign % of total IFV for this level
		 * again, this would be the value of the original word * the percent of the first level pool
		 * the effective IFVs in the first level add up to the IFV of the original word
		 */
		for (SubTokenElement subTokenForSimilar: levelList) {
			Double percentOfTotal = subTokenForSimilar.getIFV() / sumIFV;
			subTokenForSimilar.setEffectiveIFV(totalAvailable * percentOfTotal);		
		}
		
		return levelList;
	}
	
	/*
	 * avoids repeating code
	 * also sorts out if the subtokenelement list is empty
	 */
	private void addToMatrix(TreeMap<String, Double> map, ArrayList<SubTokenElement> subTokenList) {
		if (subTokenList != null) {
			for (SubTokenElement subToken : subTokenList) {
				/*
				 * if the token is already in the map, then just adds the values
				 */
				if (map.containsKey(subToken.getWord())) {	
					map.put(subToken.getWord(), subToken.getEffectiveIFV() + map.get(subToken.getWord()));
					
				}
				else {
					map.put(subToken.getWord(), subToken.getEffectiveIFV());

				}
			}
		}
	}
	
	/*
	 * 2/1/2023-2:52AM --- 5/5
	 * 
	 * returns "" if no contents, this is not an issue later
	 */
	private String getContents(Note currentNote) {
		String contents = "";
		String type = currentNote.getTypeOfNote();
		
		if (type.equals("Standard")) {
			StandardTypeNoteController stnc = (StandardTypeNoteController) currentNote.getController();
			contents = stnc.returnTextForClassifier();
		}
		else if (type.equals("Reading")) {
			ReadingTypeNoteController rtnc = (ReadingTypeNoteController) currentNote.getController();
			contents = rtnc.returnTextForClassifier();
		}
		else if (type.equals("Daily")) {
			DailyTypeNoteController dtnc = (DailyTypeNoteController) currentNote.getController();	
			contents = dtnc.returnTextForClassifier();
		}
		else if (type.equals("Daily Scroll")) {
			DailyScrollController dsc = (DailyScrollController) currentNote.getController();
			contents = dsc.returnTextForClassifier();
		}
		return contents;
	}

}
