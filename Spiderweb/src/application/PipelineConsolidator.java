package application;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import application.NoteChooserHandler.Note;
import application.NoteChooserHandler.TypeTab;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class PipelineConsolidator {
	
	MasterReference mR;
	SortByScore sortByScore;
	
	public PipelineConsolidator(MasterReference mR) {
		this.mR = mR;
		sortByScore = new SortByScore();
	}
	
	/*
	 * 2/1/2023-8:40PM --- TODO incomplete, Refactoring so that we deal with Notes directly instead of TreeItems
	 * 
	 * when a note is opened or a new tab is selected in the tabPane
	 * this is essentially the branch to the classification pipeline
	 * 
	 * needs to take each treeItem in the tree, then compare each of their 
	 * maps to the map of the currentItem
	 * 
	 */
	public void newNoteOpenedProcedure() {
		
		TypeTab typeTab = (TypeTab) mR.getMainClassController().getNoteTabPane().getSelectionModel().getSelectedItem();
		
		Note openedNote;
		if (typeTab.getTreeItem()!=null) {
			TreeItem<Note> openedItem = typeTab.getTreeItem();
			openedNote = openedItem.getValue();
		}
		else {
			openedNote = typeTab.getScroll();
		}
		
		ArrayList<TreeItem<Note>> listOfTreeItems = createListOfTreeItems();
		ArrayList<Note> listOfNotes = new ArrayList<Note>();
		listOfNotes.addAll(mR.getMainClassController().getDailyPageList().getItems());
		
		//at this point each tree item already has a matrix for its words and their values
		
		for (TreeItem<Note> currentItem : listOfTreeItems) {
			listOfNotes.add(currentItem.getValue());
		}
		
		for (Note currentNote: listOfNotes) {
			compareItemToCurrentItem(openedNote, currentNote);
		}
		
		ArrayList<Note> sortedListOfNotes = classifyAndSortNotes(listOfNotes, openedNote);
		
		mR.getMainClassController().getSimilarNotesHBox().getChildren().clear();
		addItemsToSimilarNotesHBox(sortedListOfNotes);
	}
	
	/*
	 * takes all of the treeItems "under" the root node
	 */
	public ArrayList<TreeItem<Note>> createListOfTreeItemsWithRootNode() {
		TreeItem<Note> ultimateEncapsulatingTreeItem = mR.getMainClassController().getNoteChooser().getRoot();
		
		ArrayList<TreeItem<Note>> masterList = new ArrayList<TreeItem<Note>>();
				
		for (TreeItem<Note> currentItem: ultimateEncapsulatingTreeItem.getChildren()) {
			createListOfTreeItemsHelper(currentItem, masterList);
		}
		
		masterList.add(ultimateEncapsulatingTreeItem);
		
		return masterList;
	}
	
	/*
	 * takes all of the treeItems "under" the root node
	 */
	public ArrayList<TreeItem<Note>> createListOfTreeItems() {
		TreeItem<Note> ultimateEncapsulatingTreeItem = mR.getMainClassController().getNoteChooser().getRoot();
		
		ArrayList<TreeItem<Note>> masterList = new ArrayList<TreeItem<Note>>();
				
		for (TreeItem<Note> currentItem: ultimateEncapsulatingTreeItem.getChildren()) {
			createListOfTreeItemsHelper(currentItem, masterList);
		}
		return masterList;
	}
	
	/*
	 * using recursion to search through the tree, adding elements to the list
	 */
	private void createListOfTreeItemsHelper(TreeItem<Note> currentItem, ArrayList<TreeItem<Note>> masterList) {
		
		masterList.add(currentItem);
		
		if (currentItem.getChildren().isEmpty() == false) {			
			for (TreeItem<Note> childNote: currentItem.getChildren()) {
				createListOfTreeItemsHelper(childNote, masterList);
			}
		}
	}
	
	
	private ArrayList<Note> classifyAndSortNotes(ArrayList<Note> listOfAllNotes, Note openedNote) {
		listOfAllNotes.remove(openedNote); //removed so it is not compared to itself
		
		for (Note currentNote: listOfAllNotes) {
			compareItemToCurrentItem(openedNote, currentNote);
		}
		
		Collections.sort(listOfAllNotes, sortByScore.reversed());
		return new ArrayList<Note>(listOfAllNotes.subList(0, 50));
	}
	
	/*
	 * easy way to sort the arraylist of treeItems by decreasing score with note being classified
	 */
	class SortByScore implements Comparator<Note> {
		@Override
		public int compare(Note o1, Note o2) {
			return o1.getScoreWithNoteBeingClassified().compareTo(o2.getScoreWithNoteBeingClassified());
		}
	}
	
	
	/*
	 * compares by iterating through the elements of the opened item
	 * 
	 * in each map, there will only be one entry of each word, if there were multiple entries of that word
	 * in the token list, then the value will change depending on the IFV and which level the token was from
	 * 
	 * the total score that a comparing note can get for a word is the value in the opened note,
	 * ex: if the first word is alligator and it has score 10,000 in the opened note,
	 * if the comparing note has alligator and score 20,000, there is a flat cutoff and it receives 10,000 score for that word
	 */
	private void compareItemToCurrentItem(Note openedNote, Note currentNote) {
		
		TreeMap<String, Double> mapOfOpenedItem = openedNote.getClassifierMap();
		TreeMap<String, Double> mapOfCurrentItem = currentNote.getClassifierMap();

		Double score = 0.0;
		for (Map.Entry<String, Double> entry : mapOfOpenedItem.entrySet()) {
			
			/*
			 * this takes the lower value no matter what
			 * lower value to take the value of the current comparing note or 
			 * lower value if there are too many points for the current comparing note
			 */
			if (mapOfCurrentItem.containsKey(entry.getKey())) {
				
				if (entry.getValue() > mapOfCurrentItem.get(entry.getKey())) {
					score += mapOfCurrentItem.get(entry.getKey());
				}
				else {
					score += entry.getValue();
				}
			}
		}
		
		currentNote.setScoreWithNoteBeingClassified(score);
	}
	
	/*
	 * 2/1/2023-9:23PM --- 2/5
	 * 
	 * refactored so that this is compatible with daily notes as well and it got pretty sloppy
	 */
	private void addItemsToSimilarNotesHBox(ArrayList<Note> listOfNotes) {
		
		TreeItem<Note> rootNote = mR.getMainClassController().getNoteChooser().getRoot();
		
		
		
		for (Note currentNote : listOfNotes) {
	
			if (currentNote.getTreeItem()!=null) {
				TreeItem<Note> currentTreeItem = currentNote.getTreeItem();
							
				//if it is a directory, there will only be one button
	    		if (currentTreeItem.getParent() == rootNote) {
	    			
	    			FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXMLs/SimilarNotesNodes/1.fxml"));
	
	            	try {
	            		AnchorPane root = loader.load();
	            	            		
	            		HBox mainHBox = (HBox) root.getChildren().get(0);
	            		VBox mainVBox = (VBox) mainHBox.getChildren().get(1);
	            		
	            		Button buttonOne = (Button) mainVBox.getChildren().get(1);
	            		
	            		buttonOne.setText(currentNote.getName());
	            		buttonOne.setStyle("-fx-font-size: "+mR.getSimilarNotesButtonFontSize().toString()+";");
	            		
	            		setButtonIcon(buttonOne, currentTreeItem);
	
	        			EventHandler<ActionEvent> onButtonOnePressed = (new EventHandler<ActionEvent>() { 
	        				@Override
	        				public void handle(ActionEvent arg0) {
	        					try {
	        						mR.openNote(currentTreeItem);
								} catch (IOException e) {
									e.printStackTrace();
								}						
	        				}});
	        			buttonOne.setOnAction(onButtonOnePressed);
	
	            		mR.getMainClassController().getSimilarNotesHBox().getChildren().add(root);
	    			} catch (IOException e) {
	    				e.printStackTrace();
	    			}
	    		}
	    		
				//if the second parent is the root, then there will only be 2 buttons
	    		else if (currentTreeItem.getParent().getParent() == mR.getMainClassController().getNoteChooser().getRoot()) {
	    			
	    			FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXMLs/SimilarNotesNodes/2.fxml"));
	    			
	    			TreeItem<Note> parent = currentTreeItem.getParent();
	    			Note parentNote = parent.getValue();
	
	            	try {
	            		AnchorPane root = loader.load();
	            	            		
	            		HBox mainHBox = (HBox) root.getChildren().get(0);
	            		VBox mainVBox = (VBox) mainHBox.getChildren().get(1);
	            		
	            		
	            		//handles button one, which is the parent
	            		Button buttonOne = (Button) mainVBox.getChildren().get(1);
	            		buttonOne.setPrefWidth(1000);
	            		buttonOne.setMaxWidth(1000);
	
	            		buttonOne.setStyle("-fx-font-size: "+mR.getSimilarNotesButtonFontSize().toString()+";");
	            		buttonOne.setText(parentNote.getName());
	            		setButtonIcon(buttonOne, parent);
	
	        			EventHandler<ActionEvent> onButtonOnePressed = (new EventHandler<ActionEvent>() { 
	        				@Override
	        				public void handle(ActionEvent arg0) {
	        					try {
	        						mR.openNote(parent);
								} catch (IOException e) {
									e.printStackTrace();
								}						
	        				}});
	        			buttonOne.setOnAction(onButtonOnePressed);
	        			
	        			//handles button two, which is the child
	        			Button buttonTwo = (Button) mainVBox.getChildren().get(3);
	            		buttonTwo.setPrefWidth(1000);
	            		buttonTwo.setMaxWidth(1000);
	            		
	        			buttonTwo.setStyle("-fx-font-size: "+mR.getSimilarNotesButtonFontSize().toString()+";");
	            		buttonTwo.setText(currentNote.getName());
	            		setButtonIcon(buttonTwo, currentTreeItem);
	        			EventHandler<ActionEvent> onButtonTwoPressed = (new EventHandler<ActionEvent>() { 
	        				@Override
	        				public void handle(ActionEvent arg0) {
	        					try {
	        						mR.openNote(currentTreeItem);
								} catch (IOException e) {
									e.printStackTrace();
								}						
	        				}});
	        			buttonTwo.setOnAction(onButtonTwoPressed);
	
	
	            		mR.getMainClassController().getSimilarNotesHBox().getChildren().add(root);
	    			} catch (IOException e) {
	    				e.printStackTrace();
	    			}
	    		}
	    		
	    		//maxes out at 3 notes, 2 parents and the note which is being classified for
	    		else {
	    			
	    			FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXMLs/SimilarNotesNodes/3.fxml"));
	    			
	    			TreeItem<Note> parent = currentTreeItem.getParent();
	    			Note parentNote = parent.getValue();
	    			
	    			TreeItem<Note> superParent = parent.getParent();
	    			Note superParentNote = superParent.getValue();
	
	            	try {
	            		AnchorPane root = loader.load();
	            	            		
	            		HBox mainHBox = (HBox) root.getChildren().get(0);
	            		VBox mainVBox = (VBox) mainHBox.getChildren().get(1);
	            		
	            		
	            		//handles button one, which is the super parent
	            		Button buttonOne = (Button) mainVBox.getChildren().get(1);
	            		buttonOne.setPrefWidth(1000);
	            		buttonOne.setMaxWidth(1000);
	
	            		buttonOne.setStyle("-fx-font-size: "+mR.getSimilarNotesButtonFontSize().toString()+";");
	            		buttonOne.setText(superParentNote.getName());
	            		setButtonIcon(buttonOne, superParent);
	
	        			EventHandler<ActionEvent> onButtonOnePressed = (new EventHandler<ActionEvent>() { 
	        				@Override
	        				public void handle(ActionEvent arg0) {
	        					try {
	        						mR.openNote(superParent);
								} catch (IOException e) {
									e.printStackTrace();
								}						
	        				}});
	        			buttonOne.setOnAction(onButtonOnePressed);
	        			
	        			//handles button two, which is the parent
	        			Button buttonTwo = (Button) mainVBox.getChildren().get(3);
	            		buttonTwo.setPrefWidth(1000);
	            		buttonTwo.setMaxWidth(1000);
	            		
	            		buttonTwo.setStyle("-fx-font-size: "+mR.getSimilarNotesButtonFontSize().toString()+";");
	            		buttonTwo.setText(parentNote.getName());
	            		setButtonIcon(buttonTwo, parent);
	        			EventHandler<ActionEvent> onButtonTwoPressed = (new EventHandler<ActionEvent>() { 
	        				@Override
	        				public void handle(ActionEvent arg0) {
	        					try {
	        						mR.openNote(parent);
								} catch (IOException e) {
									e.printStackTrace();
								}						
	        				}});
	        			buttonTwo.setOnAction(onButtonTwoPressed);
	        			
	        			
	        			//handles button three, which is the child
	        			Button buttonThree = (Button) mainVBox.getChildren().get(5);
	        			buttonThree.setPrefWidth(1000);
	            		buttonThree.setMaxWidth(1000);
	            		
	            		buttonThree.setStyle("-fx-font-size: "+mR.getSimilarNotesButtonFontSize().toString()+";");
	            		buttonThree.setText(currentNote.getName());
	            		setButtonIcon(buttonThree, currentTreeItem);
	        			EventHandler<ActionEvent> onButtonThreePressed = (new EventHandler<ActionEvent>() { 
	        				@Override
	        				public void handle(ActionEvent arg0) {
	        					try {
	        						mR.openNote(currentTreeItem);
								} catch (IOException e) {
									e.printStackTrace();
								}						
	        				}});
	        			buttonThree.setOnAction(onButtonThreePressed);
	
	            		mR.getMainClassController().getSimilarNotesHBox().getChildren().add(root);
	    			} catch (IOException e) {
	    				e.printStackTrace();
	    			}
	    		}
			}
			else { //for daily scrolls, TODO incomplete
				
    			FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXMLs/SimilarNotesNodes/1.fxml"));
    			
            	try {
            		AnchorPane root = loader.load();
            	            		
            		HBox mainHBox = (HBox) root.getChildren().get(0);
            		VBox mainVBox = (VBox) mainHBox.getChildren().get(1);
            		
            		Button buttonOne = (Button) mainVBox.getChildren().get(1);
            		
            		buttonOne.setText(currentNote.getName());
            		buttonOne.setStyle("-fx-font-size: "+mR.getSimilarNotesButtonFontSize().toString()+";");
            		
            		//setButtonIcon(buttonOne, currentTreeItem);

        			EventHandler<ActionEvent> onButtonOnePressed = (new EventHandler<ActionEvent>() { 
        				@Override
        				public void handle(ActionEvent arg0) {
    						mR.openNote(currentNote);					
        				}});
        			buttonOne.setOnAction(onButtonOnePressed);

            		mR.getMainClassController().getSimilarNotesHBox().getChildren().add(root);
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
            	
			}
		}
	}
	
	//TODO, incomplete, needs to be updated if want branch w/ leaf
	public void setButtonIcon(Button button, TreeItem<Note> buttonTreeItem) {
		
		String pathToImage = "src/images/";
		
		Note note = buttonTreeItem.getValue();
		
		//gets the first part of directory based on type of the note
		
		if (note.getTypeOfNote() == "Standard") {
			pathToImage += "StandardNote/";
		}
		
		else if (note.getTypeOfNote() == "Reading") {
			pathToImage += "ReadingNote/";
		}

		else if (note.getTypeOfNote() == "Topic") {
			pathToImage += "TopicNote/";
		}
		
		else if (note.getTypeOfNote() == "Daily") {
			pathToImage += "DailyNote/";
		}
		
		//gets second part of the note based on whether it is full saved and whether it has children
		
		if (buttonTreeItem.getChildren().size() == 0) {
			// if no children, it is a leaf
			pathToImage += "leaf.png";
		}
		
		else {
			
			if (note.isFullSaved()) {
				// if children and contents, then a branch with a leaf
				pathToImage += "branchwithleaf.png";
			}
			
			else {
				//otherwise, just a branch
				pathToImage += "branch.png";
			}
		}
		
		
		InputStream is;
		try {
			is = new FileInputStream(pathToImage);
			Image saveIcon = new Image(is);
			
			ImageView iconView = new ImageView(saveIcon);
			
			button.setGraphic(iconView);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	

}
