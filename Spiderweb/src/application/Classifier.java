package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import fxmlcontrollers.notetypes.DailyTypeNoteController;
import fxmlcontrollers.notetypes.ReadingTypeNoteController;
import fxmlcontrollers.notetypes.StandardTypeNoteController;
import handlers.NoteChooserHandler.Note;
import javafx.scene.control.TreeItem;

public class Classifier {
	
	private MasterReference mR;
	
	private Map<String, Double> scoreDictionary;
	private Map<String, ArrayList<Double>> tunedDictionary;
	
	public Classifier(MasterReference mR) {
		this.mR = mR;
		
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
		
		//System.out.println(scoreDictionary.get("nietzche"));
		//System.out.println(scoreDictionary.get("than"));
		//System.out.println(scoreDictionary.get("find"));

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
		
		/*
        for (Map.Entry<String, ArrayList<Double>> entry : tunedDictionary.entrySet()) {
        	System.out.println(entry.getKey() + " " + entry.getValue());
        }
		*/
		
		
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
			
			System.out.println(currentNote.getName() + " : " + score.toString());
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
