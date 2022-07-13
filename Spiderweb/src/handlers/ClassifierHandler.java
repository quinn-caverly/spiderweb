package handlers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import application.MasterReference;
import fxmlcontrollers.MainClassController;
import handlers.NoteChooserHandler.Note;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import overriders.TypeTab;
import representers.CurrentNoteRepresenter;

public class ClassifierHandler {
	
	MasterReference mR;
	
	private class ClassifierSettingsHandler {
		
		AnchorPane classifierFunctionBox;
		
		ClassifierSettingsHandler(AnchorPane classifierFunctionBox) {
			this.classifierFunctionBox = classifierFunctionBox;
		}
	}
	
	private class ClassifierDisplayHandler {
		
		AnchorPane classifierDisplayAnchor;
		
		ClassifierDisplayHandler(AnchorPane classifierDisplayAnchor) {
			this.classifierDisplayAnchor = classifierDisplayAnchor;
		}
	}
	
	
	ClassifierSettingsHandler classifierSettingsHandler;
	ClassifierDisplayHandler classifierDisplayHandler;
	
	NoteChooserHandler noteChooserHandler;
	CurrentNoteRepresenter currentNoteRepresenter;
	TreeView<Note> treeView;
	
	TextArea mainTextArea;
	
	MainClassController mCC;
		
	public ClassifierHandler(MasterReference mR) {
		this.mR = mR;
	}
	
	
	public void initialize() {
		mCC = mR.getMainClassController();
		noteChooserHandler = mCC.getNoteChooserHandler();
		this.noteChooserHandler = mR.getNoteChooserHandler();
		this.treeView = noteChooserHandler.getTreeView();
		this.mainTextArea = mCC.getMainTextArea();

		this.classifierSettingsHandler = new ClassifierSettingsHandler(mCC.getClassifierFunctionBox());
		this.classifierDisplayHandler = new ClassifierDisplayHandler(mCC.getClassifierDisplayAnchor());
		
		this.noteChooserHandler = mR.getNoteChooserHandler();
		this.treeView = noteChooserHandler.getTreeView();
		
		this.mainTextArea = mCC.getMainTextArea();
	}
	
	
	public void newNoteOpenedProcedure() {
		//can get the reference to the treeView and currentlyOpenedNote from the noteChooserHandler
		
		//has to be reassigned because currentNoteRepresenter is sometimes null
		
		TypeTab typeTab = (TypeTab) mCC.getNoteTabPane().getSelectionModel().getSelectedItem();
		TreeItem<Note> currentItem = typeTab.getTreeItem();
		
				
		ArrayList<TreeItem<Note>> listOfNotes = createListOfTreeItems();
		
		mR.getClassifier().tuneClassifier(currentItem);
		mR.getClassifier().evaluateAndSortTreeItems(listOfNotes);

		mCC.getSimilarNotesHBox().getChildren().clear();
		addItemsToSimilarNotesHBox(listOfNotes, currentItem);
	}
	
	//this provides the list of potentially similar notes (in the form of the treeItem)
	//takes the ultimate encapsulating note
	//if no item is provided, it defaults to the rootItem of the treeView
	public ArrayList<TreeItem<Note>> createListOfTreeItems() {			
		return createListOfTreeItems(treeView.getRoot());
	}
	
	public ArrayList<TreeItem<Note>> createListOfTreeItems(TreeItem<Note> ultimateEncapsulatingItem) {
		//does not include the rootItem in the list
		
		ArrayList<TreeItem<Note>> masterList = new ArrayList<TreeItem<Note>>();
				
		for (TreeItem<Note> currentItem: ultimateEncapsulatingItem.getChildren()) {
			createListOfTreeItemsHelper(currentItem, masterList);
		}
		return masterList;
	}
	
	//recursive helper
	private void createListOfTreeItemsHelper(TreeItem<Note> currentItem, ArrayList<TreeItem<Note>> masterList) {
		
		masterList.add(currentItem);
		
		if (currentItem.getChildren().isEmpty() == false) {			
			for (TreeItem<Note> childNote: currentItem.getChildren()) {
				createListOfTreeItemsHelper(childNote, masterList);
			}
		}	
	}
	
	
	
	private void addItemsToSimilarNotesHBox(ArrayList<TreeItem<Note>> listOfNotes, TreeItem<Note> openedItem) {
		
		//removes the opened note so it is not displayed in the HBox
		listOfNotes.remove(openedItem);
				
		//score logic, 10 point scale
		
		//if a note has 50% of the entire possible score (the score determined by classifying the note by itself)
		//then it is given a 10, 45% a 9, etc.
		
		//if more than 50%, given a 10
		
		
		
		for (TreeItem<Note> currentTreeItem : listOfNotes) {
			Note note = currentTreeItem.getValue();
			
			//if it is a directory, there will only be one button
    		if (currentTreeItem.getParent() == mR.getMainClassController().getNoteChooser().getRoot()) {
    			
    			FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXMLs/SimilarNotesNodes/1.fxml"));

            	try {
            		AnchorPane root = loader.load();
            	            		
            		HBox mainHBox = (HBox) root.getChildren().get(0);
            		VBox mainVBox = (VBox) mainHBox.getChildren().get(1);
            		
            		Button buttonOne = (Button) mainVBox.getChildren().get(1);
            		
            		buttonOne.setText(note.getName());
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

            		mCC.getSimilarNotesHBox().getChildren().add(root);
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
            	
            		Scene scene = root.getScene();
            		
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
            		buttonTwo.setText(note.getName());
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


            		mCC.getSimilarNotesHBox().getChildren().add(root);
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
            		buttonThree.setText(note.getName());
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

            		mCC.getSimilarNotesHBox().getChildren().add(root);
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    		}
		}
		
	}
	
	
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
