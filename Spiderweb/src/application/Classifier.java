package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Pointer;
import fxmlcontrollers.notetypes.DailyTypeNoteController;
import fxmlcontrollers.notetypes.ReadingTypeNoteController;
import fxmlcontrollers.notetypes.StandardTypeNoteController;
import handlers.NoteChooserHandler.Note;
import javafx.scene.control.TreeItem;

public class Classifier {
	
	private MasterReference mR;
	
	private PipelineNLP pipeline;
	
	private Map<String, Double> scoreDictionary;
	private Map<String, ArrayList<Double>> tunedDictionary;
	
	public Classifier(MasterReference mR) {
		this.mR = mR;
		pipeline = new PipelineNLP();
		
		File csvFile = new File("src/Data/unigram_freq.csv");
		scoreDictionary = new HashMap<String, Double>();
		
		try {
			Scanner inputStream = new Scanner(csvFile);			

			Integer numberOfNonCountingWords = 100;		
			
			
			//splits the csv file up into tenths, ignoring the first 100 words
			//the score for each word is the tenth that they are present in IE (1 for most common, 2 for second most common, etc.)
			//to the power of 2, so 1st tenth = 1**1 = 1, 2nd = 2**2 = 4
			
			
			while (inputStream.hasNext()) {
				//excludes the first however many words from being added
				//this is useful because words like "the" being included are not useful
				if (numberOfNonCountingWords != 0) {
					numberOfNonCountingWords -= 1;
					inputStream.next();
				}
				else {
					String line = inputStream.next();
					String[] lst = line.split(",");
					
					scoreDictionary.put(lst[0], Math.pow(10, 10) * 1/Double.valueOf(lst[1]));
					}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
	
	public void tuneClassifier(TreeItem<Note> currentItem) {
		//creates a dictionary of the unique words in the currently opened note and saves it in the class instance
		
		//in the double arraylist, the first value is the score of the word, the second value is repetitions
		
		tunedDictionary =  new HashMap<String, ArrayList<Double>>();
		
		Note currentNote = currentItem.getValue();
		String contents = getContents(currentNote);
				
		List<String> listOfWords = convertContentsToList(contents);
		
		for (String word : listOfWords) {
			
			//essentially if the word is a valid word
			if (scoreDictionary.containsKey(word)) {
				
				//if the word is already in the tunedDictionary, increases value by one
				if (tunedDictionary.containsKey(word)) {
					ArrayList<Double> currentValue = tunedDictionary.get(word);
					currentValue.set(1, currentValue.get(1)+1);
				}
				else {
					ArrayList<Double> newArray = new ArrayList<Double>();
					
					newArray.add(scoreDictionary.get(word));
					newArray.add((double) 1);
					
					tunedDictionary.put(word, newArray);
				}
			}
		}
	}

	
	public void evaluateAndSortTreeItems(ArrayList<TreeItem<Note>> listOfTreeItems) {
				
		for (TreeItem<Note> treeItem : listOfTreeItems) {
						
			Note currentNote = treeItem.getValue();
			String contents = getContents(currentNote);
			
			List<String> listOfWords = convertContentsToList(contents);
			

			//creates a new dictionary which is a copy of the tunedDictionary as it will be modified 
			Map<String, ArrayList<Double>> availableWordsToMatchDictionary = new HashMap<String, ArrayList<Double>>();
	        for (Map.Entry<String, ArrayList<Double>> entry : tunedDictionary.entrySet()) {
	        	availableWordsToMatchDictionary.put(entry.getKey(), entry.getValue());
	        }
			
			//this is the cumulative score over every word that will be set in the note class's setter
			Double score = (double) 0;
			for (String word : listOfWords) {
				
				if (availableWordsToMatchDictionary.containsKey(word)) {
					
					ArrayList<Double> currentValue = availableWordsToMatchDictionary.get(word);
					Double availableMatches = currentValue.get(1);
					
					if (availableMatches > 0) {
						availableMatches -= 1;
						
						//adds the value to the cumulative score
						score += currentValue.get(0);
					}
					
					ArrayList<Double> newValue = new ArrayList<Double>();
					newValue.add(currentValue.get(0));
					newValue.add(availableMatches);

					availableWordsToMatchDictionary.replace(word, newValue);
				}
			}

			currentNote.setScoreWithNoteBeingClassified(score);
			
		}		
						
		Collections.sort(listOfTreeItems, new SortByScore());
		
		//reverses the list, so the most relevant values will be on the left
		Collections.reverse(listOfTreeItems);
	}
	
	
	public List<String> convertContentsToList(String contents) {
			
		contents = contents.toLowerCase();
		
		contents = contents.replace("/n", " ");
		contents = contents.replace("\n", " ");
		contents = contents.replace(".", " ");
		
	    BreakIterator breakIterator = BreakIterator.getWordInstance();
		
	    List<String> listOfWords = new ArrayList<String>();
	    
	    breakIterator.setText(contents);
	    int lastIndex = breakIterator.first();
	    
	    while (BreakIterator.DONE != lastIndex) {
	        int firstIndex = lastIndex;
	        lastIndex = breakIterator.next();
	        if (lastIndex != BreakIterator.DONE && Character.isLetterOrDigit(contents.charAt(firstIndex))) {
	        	listOfWords.add(contents.substring(firstIndex, lastIndex));
	        }
	    }
		
		return listOfWords;
	}
	
	
	//this is the equivalent of convertContentsToList but using a StanfordNLP pipeline
	public void runThroughPipeline(String contents) {
		
		//pipeline.separateSentences(contents);
		
		pipeline.createListOfTokenElements(pipeline.separateTokens(pipeline.separateSentences(contents)));
		
		List<String> listOfWords = runTokenization(contents);
		
		Map<String, Integer> wordCountDic = countWords(listOfWords);
		
		//at this point, if there are no entries in the wordCountDic, the note should be excluded
		
		if (!wordCountDic.isEmpty()) {
			
			//System.out.println(wordCountDic);

		}
	}
	
	
	
	
	//the tokenization step turns the raw string contents into a list of words 
	public List<String> runTokenization(String contents) {
		
		//edits contents
		contents = contents.toLowerCase();
		
		contents = contents.replace("/n", " ");
		contents = contents.replace("\n", " ");
		contents = contents.replace(".", " ");
		
		
		//converts to list
	    BreakIterator breakIterator = BreakIterator.getWordInstance();
		
	    List<String> listOfWords = new ArrayList<String>();
	    
	    breakIterator.setText(contents);
	    int lastIndex = breakIterator.first();
	    
	    while (BreakIterator.DONE != lastIndex) {
	        int firstIndex = lastIndex;
	        lastIndex = breakIterator.next();
	        if (lastIndex != BreakIterator.DONE && Character.isLetterOrDigit(contents.charAt(firstIndex))) {
	        	listOfWords.add(contents.substring(firstIndex, lastIndex));
	        }
	    }
		
		return listOfWords;
	}
	
	
	//returns a dictionary which contains a key for each word in the original list and the value is how many times that word appeared
	public Map<String, Integer> countWords(List<String> listOfOriginals) {
		
		Map<String, Integer> wordCountDic = new HashMap<String, Integer>();
		
		for (String word : listOfOriginals) {
			
			//if dic contains the word, ups value by 1
			if (wordCountDic.containsKey(word)) {
				wordCountDic.replace(word, wordCountDic.get(word) + 1);
			}
			
			//if not present, adds it with value 1
			else {
				wordCountDic.put(word, 1);
			}
			
		}
		
		return wordCountDic;
	}
	
	
	
	public void testWordNet() {
		
	    String path = "lib/wordnetdict";

	    URL url = null;
	    
	    try{ url = new URL("file", null, path); } 
	    catch(MalformedURLException e){ e.printStackTrace(); }
	    
	    if(url == null) return;
	    
	    
	    ArrayList<String> listOfFirstLevelSimilars = new ArrayList<String>();
	    ArrayList<String> listOfSecondLevelSimilars = new ArrayList<String>();
	    
	    // construct the dictionary object and open it
	    IDictionary dict = new Dictionary(url);
	    try {
			dict.open();
			
			IIndexWord idxWord = dict.getIndexWord("war", POS.NOUN);
			IWordID wordID = idxWord.getWordIDs().get(0); //1st meaning
			IWord word = dict.getWord(wordID);
			ISynset synset = word.getSynset();
			
			//these are the related words
			for (IWord synsetWord :synset.getWords()) {
				listOfFirstLevelSimilars.add(synsetWord.getLemma());
			}
			
			//these are the sets of the related words to the original synset
			for (ISynsetID relatedSynsetID : synset.getRelatedSynsets()) {
				
				ISynset relatedSynset = dict.getSynset(relatedSynsetID);
				
				for (IWord w : relatedSynset.getWords()) {
					listOfSecondLevelSimilars.add(w.getLemma());
				}				
				
				
			
								
			}
			
		    ArrayList<String> listOfSecondLevelSimilarsNoUnderscores = new ArrayList<String>();

		    for (String secondLevelWord : listOfSecondLevelSimilars) {
		    	
		    	if (!secondLevelWord.contains("_")) {
		    		listOfSecondLevelSimilarsNoUnderscores.add(secondLevelWord);
		    	}
		    	
		    }
			
			System.out.println(listOfFirstLevelSimilars);
			System.out.println(listOfSecondLevelSimilars);

			

			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	//a comparator for finding the notes with the highest score
	class SortByScore implements Comparator<TreeItem<Note>> {

		@Override
		public int compare(TreeItem<Note> o1, TreeItem<Note> o2) {
			return o1.getValue().getScoreWithNoteBeingClassified().compareTo(o2.getValue().getScoreWithNoteBeingClassified());
		}
		
	}
	
	
	//this is a method to avoid redundancy, takes the note and calls returnTextForClassifier on the correct controller type
	public String getContents(Note currentNote) {
		//in case something goes wrong, it will stay as "" for type safety
		String contents = "";
		
		String type = currentNote.getTypeOfNote();
		
		if (type == "Standard") {
			StandardTypeNoteController stnc = (StandardTypeNoteController) currentNote.getController();
			
			contents = stnc.returnTextForClassifier();
		}
		
		else if (type == "Reading") {
			ReadingTypeNoteController rtnc = (ReadingTypeNoteController) currentNote.getController();
			
			contents = rtnc.returnTextForClassifier();
		}
		
		else if (type == "Daily") {
			DailyTypeNoteController dtnc = (DailyTypeNoteController) currentNote.getController();
			
			contents = dtnc.returnTextForClassifier();
		}
		
		
		return contents;
	}
			
}
