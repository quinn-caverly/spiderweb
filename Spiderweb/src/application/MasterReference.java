package application;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import application.NoteChooserHandler.Note;
import application.NoteChooserHandler.TypeTab;
import fxmlcontrollers.MainClassController;
import fxmlcontrollers.notetypes.DailyScrollController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MasterReference {
	
    private final Double goldenRatio = (double) (1+Math.sqrt(5))/2;
    private Double similarNotesButtonFontSize = (double) 8;
	
	private final MainClassController mCC;
	
	private final NoteChooserHandler noteChooserHandler;
	private final PinnedNotesHandler pinnedNotesHandler;	
		
	private final PipelineNLP pipeline;
	private final PipelineConsolidator pipelineConsolidator;
		
	private final String dataFilePath = "lib/data/directories.txt";
	private final String lastUsedIDFilePath = "lib/data/lastUsedID.txt";
	
	private ImageView buttonIconImageView;
	
	private Boolean bottomSectionEnabled = true;
	//if the bottom section is disabled then re-enabled while the same note is still open,
	//then it should not start the procedure because it will cause slight lag
	//this will be set to false when a new note opens, set to true when the bottom section is disabled, if there is
	//a note which is currently open
	private Boolean bottomSectionShouldKeepContents = false;

	
	//mR has to take mCC so that it is connected to the program
	public MasterReference(MainClassController mCC) {
		this.mCC = mCC;
		
		//creates a buttonIconImageView for use in more than 1 classes
		String pathToImage = "src/images/star.png";
		InputStream is;
		try {
			is = new FileInputStream(pathToImage);
			Image buttonIconImage = new Image(is);
			buttonIconImageView = new ImageView(buttonIconImage);
			
			buttonIconImageView.setFitWidth(10);
			buttonIconImageView.setFitHeight(10);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
		//initializes the handlers which are not location specific
		noteChooserHandler = new NoteChooserHandler(this);
		pinnedNotesHandler = new PinnedNotesHandler(this);
		
		
		pipeline = new PipelineNLP();
		pipelineConsolidator = new PipelineConsolidator(this);
		
		noteChooserHandler.initialize();		
		
		/*
		 * this is the catalyst for the changing of the
		 * pinned note and similar notes hboxes
		 */
		TabPane noteTabPane = mCC.getNoteTabPane();
		noteTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldVal, newVal)->{
			
			//if note changes to anything even a null value this should be set false
			bottomSectionShouldKeepContents = false;
			
			//if the newtab is null, just needs to clear the hBoxes
			if (newVal == null) {
				mCC.getPinnedNotesHBox().getChildren().clear();
				mCC.getSimilarNotesHBox().getChildren().clear();
			}
			
			
			//a new note has been selected by the tabPane
			else if (oldVal != newVal) {
				
				if (bottomSectionEnabled == true) {
					//changes the values in the similarNotesHBox
					pipelineConsolidator.newNoteOpenedProcedure();
					//changes the values in the pinnedNotesHBox
					pinnedNotesHandler.newNoteOpenedProcedure();
				}
			}
		});
		
		
		mCC.getLeftVBoxOfMainSplit().maxWidthProperty().bind((mCC.getMainSplitPane()).widthProperty().divide(goldenRatio*2));
		mCC.getLeftVBoxOfMainSplit().prefWidthProperty().bind((mCC.getMainSplitPane()).widthProperty().divide(goldenRatio*2));
		
		
		

		//TODO this code should be moved to an actual location will be necessary if I ever decide to make 
		//the jar exportable 
		//DatabaseHandler.initializeDatabase();
				
		//DatabaseHandler.startSaveProtocol(this);
		
		DatabaseHandler.startLoadProtocol(this);
		
    	for (TreeItem<Note> treeItem : pipelineConsolidator.createListOfTreeItems()) {
    		pipeline.runThroughPipeline(treeItem.getValue());
    	}
    	
		//this is called after the tree nodes already exist, it goes through and makes their style in the treeView align with the type of that note
		setTreeCellStyles();
	}

	
	
	//opens a note which can be selected in multiple different ways
	//eventually adds the note to the noteTabPane
	public void openNote(TreeItem<Note> treeItem) throws IOException {
		
		//expands all of the note's parents
		TreeItem<Note> currentTreeItem = treeItem;
		
		while (currentTreeItem.getParent() != mCC.getNoteChooser().getRoot()) {
			
			currentTreeItem = currentTreeItem.getParent();
			
			currentTreeItem.setExpanded(true);
		}
		
		//selects the note which was just opened in the treeView
		mCC.getNoteChooser().getSelectionModel().select(treeItem);
		
	   	Note note = treeItem.getValue();
    	AnchorPane root = note.getRoot();
    	
    	root.maxWidthProperty().bind(mCC.getNoteTabPane().widthProperty());
    	root.minWidthProperty().bind(mCC.getNoteTabPane().widthProperty());
    	
    	root.maxHeightProperty().bind(mCC.getNoteTabPane().heightProperty());
    	root.minHeightProperty().bind(mCC.getNoteTabPane().heightProperty());
    	
    	TypeTab newTab = noteChooserHandler.new TypeTab(note.getName(), root, treeItem);
    	
    	mCC.getNoteTabPane().getTabs().add(newTab);
    	
		mCC.getNoteTabPane().getSelectionModel().select(newTab);
	}
	
	
	/*
	 * a separate but similar method is needed because scrolls are not treeItems
	 */
	public void openNote(Note dailyScroll) {
		
		mCC.getDailyPageList().getSelectionModel().select(dailyScroll);
		
		AnchorPane root = dailyScroll.getRoot();
		
    	root.maxWidthProperty().bind(mCC.getNoteTabPane().widthProperty());
    	root.minWidthProperty().bind(mCC.getNoteTabPane().widthProperty());
    	
    	root.maxHeightProperty().bind(mCC.getNoteTabPane().heightProperty());
    	root.minHeightProperty().bind(mCC.getNoteTabPane().heightProperty());
    	
    	TypeTab newTab = noteChooserHandler.new TypeTab(dailyScroll.getName(), root, dailyScroll);
    	
    	DailyScrollController dsc = (DailyScrollController) dailyScroll.getController();
    	dsc.setMasterReference(this);
    	dsc.loadFromBookDeskDatabase();

    	mCC.getNoteTabPane().getTabs().add(newTab);
    	
		mCC.getNoteTabPane().getSelectionModel().select(newTab);
	}
	
	
	public void closeNote(TreeItem<Note> treeItem) throws IOException {
		
		TabPane noteTabPane = mCC.getNoteTabPane();
		
		ArrayList<TreeItem<Note>> treeItemList = new ArrayList<TreeItem<Note>>();
		
		for (Tab tab : noteTabPane.getTabs()) {
			TypeTab typeTab = (TypeTab) tab;
			
			TreeItem<Note> openedTreeItem = typeTab.getTreeItem();
			
			treeItemList.add(openedTreeItem);
		}
		
		if (treeItemList.contains(treeItem)) {
			for (Tab tab : noteTabPane.getTabs()) {
				
				TypeTab typeTab = (TypeTab) tab;
				
				if (treeItem == typeTab.getTreeItem()) {
					noteTabPane.getTabs().remove(typeTab);
				}
			}	
		}
	}
	
	
	public void renameTab(TreeItem<Note> treeItem, String newName) throws IOException {
		//need to check if the note is already opened
		
		TabPane noteTabPane = mCC.getNoteTabPane();
		
		ArrayList<TreeItem<Note>> treeItemList = new ArrayList<TreeItem<Note>>();
		
		for (Tab tab : noteTabPane.getTabs()) {
			TypeTab typeTab = (TypeTab) tab;
			
			TreeItem<Note> openedTreeItem = typeTab.getTreeItem();
			
			treeItemList.add(openedTreeItem);
		}
		
		//if the note is already opened in the tabPane
		//it sets the tab which was tried to be opened twice to the tabpane's currently selected tab
		
		if (treeItemList.contains(treeItem)) {
			for (Tab tab : noteTabPane.getTabs()) {
				
				TypeTab typeTab = (TypeTab) tab;
				
				if (treeItem == typeTab.getTreeItem()) {
					
					TypeTab newTab = noteChooserHandler.new TypeTab(newName, tab.getContent(), treeItem);
					
					noteTabPane.getTabs().remove(tab);
					noteTabPane.getTabs().add(newTab);
				}
			}	
		}
	}
	
	
	//for removing a treeItem from the tabPane
	public void removeTab(TreeItem<Note> treeItem) {
		//need to check if the note is actually opened
		
		TabPane noteTabPane = mCC.getNoteTabPane();
		
		ArrayList<TreeItem<Note>> treeItemList = new ArrayList<TreeItem<Note>>();
		
		for (Tab tab : noteTabPane.getTabs()) {
			TypeTab typeTab = (TypeTab) tab;
			
			TreeItem<Note> openedTreeItem = typeTab.getTreeItem();
			
			treeItemList.add(openedTreeItem);
		}
		
		//if the note is already opened in the tabPane
		//it sets the tab which was tried to be opened twice to the tabpane's currently selected tab
		
		if (treeItemList.contains(treeItem)) {
			for (Tab tab : noteTabPane.getTabs()) {
				
				TypeTab typeTab = (TypeTab) tab;
				
				if (treeItem == typeTab.getTreeItem()) {
					noteTabPane.getTabs().remove(tab);
				}
			}	
		}
	}
	
	/*
	 * 1/26/2023-4:28PM --- 5/5
	 * 
	 * only saves notes which are currently opened in the tabview, if not opened in the tabview then it is very unlikely
	 * that there have been updates which need to be saved
	 */
	public void saveAllNotes() throws IOException {
		TabPane noteTabPane = mCC.getNoteTabPane();
		
		for (Tab currentTab: noteTabPane.getTabs()) {
			TypeTab typeTab = (TypeTab) currentTab;
			saveTypeTab(typeTab);
		}
	}
	
	/*
	 * 1/26/2023-4:32PM --- 2/5 read below
	 * 
	 * checks if the selectedTab is null because the buttons are still pressable even when none are selected
	 * TODO, maybe make it impossible to push save and saveAllNotes buttons when they don't point to anything
	 */
	public void saveCurrentNote() throws IOException {
		TabPane noteTabPane = mCC.getNoteTabPane();
		Tab selectedTab = noteTabPane.getSelectionModel().getSelectedItem();
		
		if (selectedTab!=null) {
			TypeTab typeTab = (TypeTab) selectedTab;
			saveTypeTab(typeTab);
		}
	}
	
	/*
	 * 1/26/2023-4:30PM --- 5/5
	 * 
	 * method for saving a typetab to avoid code repetitiveness
	 */
	public void saveTypeTab(TypeTab typeTab) {
		if (typeTab.getTreeItem() != null) {
			try {
				saveNote(typeTab.getTreeItem());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if (typeTab.getScroll() != null) {
			saveScroll(typeTab.getScroll());
		}
	}
	
	/*
	 * both saveCurrentNote() and saveAllNotes() lead to this function
	 * also serves the purpose of reassigning the classifierMap of the note
	 */
	public void saveNote(TreeItem<Note> treeItem) throws IOException {
		DatabaseHandler.savePageToDatabase(treeItem.getValue());
		
		pipeline.runThroughPipeline(treeItem.getValue());
	}
	
	/*
	 * 1/26/2023-1:38PM --- TODO incomplete
	 * 
	 * diff from saveNote because of fundamentally diff structure
	 */
	public void saveScroll(Note scroll) {
		DatabaseHandler.saveDailyScrollToDatabase(scroll);
		
		
	}
	
	public void renameCurrentNote() throws IOException {
		//there could be no tabs in the tabPane
		TabPane noteTabPane = mCC.getNoteTabPane();

		Tab selectedTab = noteTabPane.getSelectionModel().getSelectedItem();
		
		if (selectedTab!=null) {
			
			TypeTab typeTab = (TypeTab) selectedTab;
			
			renameNote(typeTab.getTreeItem());
		}
	}
	

	public void renameNote(TreeItem<Note> treeItem) {
		
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXMLs/RenameNote.fxml"));
        
		try {
			VBox root = (VBox) fxmlLoader.load();
			
			AnchorPane textFieldAnchor = (AnchorPane) root.getChildren().get(0);
			TextField textField = (TextField) textFieldAnchor.getChildren().get(0);
			
			
			VBox buttonsVBox = (VBox) root.getChildren().get(1);
			HBox buttonHBox = (HBox) buttonsVBox.getChildren().get(1);
			Button cancelButton = (Button) buttonHBox.getChildren().get(1);
			Button confirmButton = (Button) buttonHBox.getChildren().get(3);

			
			mCC.getFunctionBox().getChildren().clear();
			mCC.getFunctionBox().getChildren().add(root);
			
			mCC.getFunctionBox().setMinHeight(105);
			mCC.getFunctionBox().setMaxHeight(105);
			
			
			EventHandler<ActionEvent> onCancelPressed = (new EventHandler<ActionEvent>() { 
				@Override
				public void handle(ActionEvent arg0) {
					noteChooserHandler.cancelFunctionBoxOperation();						
				}});
			cancelButton.setOnAction(onCancelPressed);

			

			EventHandler<ActionEvent> onConfirmPressed = (new EventHandler<ActionEvent>() { 
				@Override
				public void handle(ActionEvent arg0) {
					//TODO incomplete
				}});
			confirmButton.setOnAction(onConfirmPressed);
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public void deleteCurrentNote() {
		//there could be no tabs in the tabPane
		TabPane noteTabPane = mCC.getNoteTabPane();

		Tab selectedTab = noteTabPane.getSelectionModel().getSelectedItem();
		
		if (selectedTab!=null) {
			
			TypeTab typeTab = (TypeTab) selectedTab;
			
			deleteNote(typeTab.getTreeItem());
		}
	}
	
	public void deleteNote(TreeItem<Note> treeItem) {
		
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXMLs/DeleteNote.fxml"));
		try {
			//the button will be disabled if a specific note is not selected
			TreeItem<Note> selectedTreeItem = mCC.getNoteChooser().getSelectionModel().getSelectedItem();
			
			Note selectedNote = selectedTreeItem.getValue();
			
			VBox newNoteName = fxmlLoader.load();
				        				        			
			mCC.getFunctionBox().getChildren().clear();
			mCC.getFunctionBox().getChildren().add(newNoteName);
			
			mCC.getFunctionBox().setMinHeight(105);
			mCC.getFunctionBox().setPrefHeight(105);
			
			AnchorPane labelAnchor = (AnchorPane) newNoteName.getChildren().get(0);
			Label label = (Label) labelAnchor.getChildren().get(0);
			
			label.setText("Delete (" + selectedNote.getName() + ")");
			
			VBox subVBox = (VBox) newNoteName.getChildren().get(1);
			HBox subHBox = (HBox) subVBox.getChildren().get(1);
			
			Button cancelButton = (Button) subHBox.getChildren().get(1);
			Button confirmButton = (Button) subHBox.getChildren().get(3);
			
			EventHandler<ActionEvent> onCancelPressed = (new EventHandler<ActionEvent>() { 
				@Override
				public void handle(ActionEvent arg0) {
					noteChooserHandler.cancelFunctionBoxOperation();					
				}});
			cancelButton.setOnAction(onCancelPressed);
			
			EventHandler<ActionEvent> onConfirmPressed = (new EventHandler<ActionEvent>() { 
				@Override
				public void handle(ActionEvent arg0) {
					
					noteChooserHandler.cancelFunctionBoxOperation();
					
					//then removes from the tabPane
					removeTab(treeItem);
					
					//deletes the files on local
					try {
						DatabaseHandler.deletePageFromDatabase(treeItem.getValue());
						
						//then removes from the treeView
						treeItem.getParent().getChildren().remove(treeItem);
						
						//because the treeview structure has changed, sends a call to the database handler
						DatabaseHandler.startTreeViewSaveProtocol(MasterReference.this);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}});
			confirmButton.setOnAction(onConfirmPressed);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	//this sets the currently opened note to the selected note in the treeView
	//this is to prevent confusion when renaming, deleting, etc. notes from the buttons
	public void setCurrentToSelected() {
		
		//finds the selected note
		TabPane noteTabPane = mCC.getNoteTabPane();

		Tab selectedTab = noteTabPane.getSelectionModel().getSelectedItem();
		
		if (selectedTab!=null) {
			
			TypeTab tab = (TypeTab) selectedTab;
			
			mCC.getNoteChooser().getSelectionModel().select(tab.getTreeItem());
		}
	}
	
	
	
	//this is a function that will handle the button and the contextmenu item for pinning a note
	//it gets the currently opened note in the tab pane and it takes the selected note in the treeView
	public void pinCurrentToOpened() {
		TabPane noteTabPane = mCC.getNoteTabPane();
		Tab selectedTab = noteTabPane.getSelectionModel().getSelectedItem();
		
		//needs to make sure that there is a currently opened note
		if (selectedTab!=null) {
			TypeTab typeTab = (TypeTab) selectedTab;
			
			TreeItem<Note> currentlyOpenedItem = typeTab.getTreeItem();
			Note currentlyOpenedNote = currentlyOpenedItem.getValue();
			
			
			TreeItem<Note> selectedItem = mCC.getNoteChooser().getSelectionModel().getSelectedItem();
			
			//needs to make sure that there is a note selected in treeView
			if (selectedItem != null) {
				//adds the id to the note's list of pinned ids
				currentlyOpenedNote.getListOfPinnedIDs().add(selectedItem.getValue().getId());
				//also refreshes the pinned menu so the results are shown immediately
				pinnedNotesHandler.newNoteOpenedProcedure();
				//saves the current note so that the pinned notes are written to file
				try {
					saveCurrentNote();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	

	//this method has to be called after the whole tree has been created
	//this should also be created when a new note is created and treecells are modified generally because the styles of those cells may change
	public void setTreeCellStyles() {
		
		ArrayList<TreeItem<Note>> listOfTreeItems = pipelineConsolidator.createListOfTreeItems();
		
		
		for (TreeItem<Note> treeItem : listOfTreeItems) {
			String pathToImage = "src/images/";
			
			Note note = treeItem.getValue();
			
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
			
			if (treeItem.getChildren().size() == 0) {
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
						
			HBox loadedHBox = note.getTreeViewHBox();
			
			ImageView iconView = (ImageView) loadedHBox.getChildren().get(0);

			
			InputStream is;
			try {
				is = new FileInputStream(pathToImage);
				Image saveIcon = new Image(is);
				
				iconView.setImage(saveIcon);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} 
		}
	}
	
	
	/*
	 * this method is for if the bottom section was disabled and has become enabled it needs to establish itself
	 */
	public void establishBottomSplit() {
		
		//if there is a currently selected note, must handle this
		//the listener defined above will automatically handle when a new note is opened
		if ((TypeTab) mCC.getNoteTabPane().getSelectionModel().getSelectedItem() != null) {
			//changes the values in the similarNotesHBox
			pipelineConsolidator.newNoteOpenedProcedure();
			//changes the values in the pinnedNotesHBox
			pinnedNotesHandler.newNoteOpenedProcedure();
		}
	}
	
	
	public MainClassController getMainClassController() {
		return mCC;
	}

	public NoteChooserHandler getNoteChooserHandler() {
		return noteChooserHandler;
	}

	public String getDataFilePath() {
		return dataFilePath;
	}

	public String getLastUsedIDFilePath() {
		return lastUsedIDFilePath;
	}

	public Double getSimilarNotesButtonFontSize() {
		return similarNotesButtonFontSize;
	}

	public void setSimilarNotesButtonFontSize(Double similarNotesButtonFontSize) {
		this.similarNotesButtonFontSize = similarNotesButtonFontSize;
	}
	
	public ImageView getButtonIconImageView() {
		return buttonIconImageView;
	}

	public PipelineNLP getPipeline() {
		return pipeline;
	}

	public PipelineConsolidator getPipelineConsolidator() {
		return pipelineConsolidator;
	}

	public Boolean getBottomSectionEnabled() {
		return bottomSectionEnabled;
	}

	public void setBottomSectionEnabled(Boolean bottomSectionEnabled) {
		this.bottomSectionEnabled = bottomSectionEnabled;
	}

	public Boolean getBottomSectionShouldKeepContents() {
		return bottomSectionShouldKeepContents;
	}

	public void setBottomSectionShouldKeepContents(Boolean bottomSectionShouldKeepContents) {
		this.bottomSectionShouldKeepContents = bottomSectionShouldKeepContents;
	}
}
