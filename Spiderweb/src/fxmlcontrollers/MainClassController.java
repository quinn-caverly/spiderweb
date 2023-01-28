package fxmlcontrollers;

import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

import application.MasterReference;
import application.NoteChooserHandler.Note;
import application.NoteChooserHandler.TypeTab;
import fxmlcontrollers.notetypes.ReadingTypeNoteController;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class MainClassController implements Initializable {
	//master reference
	private MasterReference mR;

	//fxml components
    @FXML
    private TextArea mainTextArea;
    @FXML
    private TextArea titleArea;
    @FXML
    private SplitPane rightSideSplitPane;
    @FXML
    private SplitPane mainSplitPane;
    @FXML
    private AnchorPane mainSplitLeftAnchor;
    @FXML
    private AnchorPane mainSplitMiddleAnchor;
    @FXML
    private VBox mainSplitRightVBox;
    @FXML
    private AnchorPane imagePool;
    @FXML
    private AnchorPane marginKeeper;
    @FXML
    private Pane textSectionPane;
    @FXML
    private VBox textSectionVBox;
    @FXML
    private VBox mainVBox;
    @FXML
    private Button saveButton;
    @FXML
    private HBox bottomHBox;
    @FXML
    private Label bottomBarLeftLabel;
    @FXML
    private Label bottomBarMiddleLabel;
    @FXML
    private Label bottomBarRightLabel;
    @FXML
    private TreeView<Note> noteChooser;
    @FXML
    private Button renameButton;
    @FXML
    private HBox functionBox;
    @FXML
    private AnchorPane classifierFunctionBox;
    @FXML
    private AnchorPane classifierDisplayAnchor;
    @FXML
    private Button balanceImagesButton;
    @FXML
    private AnchorPane mainSeparator;
    @FXML 
    private TabPane noteTabPane;
    @FXML
    private VBox parentOfTreeView;
    @FXML
    private HBox superParentOfTreeView;
    @FXML
    private HBox similarNotesHBox;
    @FXML
    private HBox classifierHBox;
    @FXML
    private VBox bottomSplitVBox;
    @FXML
    private VBox leftVBoxOfMainSplit;
    @FXML
    private HBox leftHBoxOfMainSplit;
    @FXML
    private SplitPane verticalSplitPane;
    @FXML
    private ScrollPane switchingScrollPane;
    @FXML
    private Button pinNoteButton;
    @FXML
    private Button toggleQuotesButton;
    @FXML
    private Button newQuoteButton;
    @FXML
    private Button edgarButton;
    @FXML
    private Label edgarQuote;
    @FXML
    private AnchorPane edgarAnchor0;
    @FXML
    private AnchorPane edgarAnchor1;
    @FXML
    private Button exitButton;
    @FXML
    private Button refreshSimilarsButton;
    @FXML
    private Button switchTreeButton;
    @FXML
    private TabPane treeViewTabPane;
    @FXML
    private ListView<Note> dailyPageList;
    @FXML
    private Button bottomSplitButton;
	@FXML
    private AnchorPane classifierHoldingAnchor;
    @FXML
    private AnchorPane treeHoldingAnchor;
    @FXML
    private Button buttonBelowNoteChooser;
    @FXML
    private Button newDailyPageButton;
    @FXML
    private ScrollPane dailyPageListScrollPane;
    @FXML
    private VBox vBoxHolderOfDailyPageList;
    
    private HBox pinnedNotesHBox;
    
    
    public void handleCloseButtonAction() {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }
    
    private Boolean similarNotesEnabled = true;
    
    public void handleBottomRightButton() {
    	
    	if (similarNotesEnabled == true) {
    		switchingScrollPane.setContent(pinnedNotesHBox);
    		similarNotesEnabled = false;
    	}
    	else {
    		switchingScrollPane.setContent(similarNotesHBox);
    		similarNotesEnabled = true;
    	}
    }
    
    public void pinNoteInMR() {
    	mR.pinCurrentToOpened();
    }

	
	public void setSaveButtonToSaved() {
		//indicates that the current opened note was just changed
		saveButton.setStyle("-fx-border-color: green");
	}
	
	public void setSaveButtonToAntiSaved() {
		saveButton.setStyle("-fx-border-color: red");
	}
	
	public void renameFromButton() {
		//this method to avoid confusion, see mR
		mR.setCurrentToSelected();

		try {
			mR.renameCurrentNote();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteFromButton() {
		//this method to avoid confusion, see mR
		mR.setCurrentToSelected();

		mR.deleteCurrentNote();
	}
	
	public void saveNoteInMR() throws IOException {
		mR.saveCurrentNote();
	}
	
	public void saveAllInMR() throws IOException {
		mR.saveAllNotes();
	}
	
	public void renameNoteInMR() throws IOException {
		mR.renameCurrentNote();
	}
	
	//switches between legacy tree view and the newer date-ranked note structure view
	public void switchTreeViewTabPane() {
		
		//if index 0 is selected, we select 1
		if (treeViewTabPane.getSelectionModel().getSelectedIndex() == 0) {
			treeViewTabPane.getSelectionModel().select(1);
			//generates the most recent version of the recency list
			mR.getNoteChooserHandler().createRecencyList();
			
			//because this corresponds to the legacy treeView we change the message
			switchTreeButton.setText("legacy tree");
		}
		else {
			treeViewTabPane.getSelectionModel().select(0);
			switchTreeButton.setText("recency tree");
		}		
	}
	
	public void buttonBelowNoteChooserPushed() {
		if (treeViewTabPane.getSelectionModel().getSelectedIndex() == 0) {
			treeViewTabPane.getSelectionModel().select(1);
		}
		else {
			treeViewTabPane.getSelectionModel().select(0);
		}
	}
	
	
	public void leftButtonPushed() {
		
		if (leftHBoxOfMainSplit.getChildren().contains(leftVBoxOfMainSplit)) { //then need to remove it
			leftHBoxOfMainSplit.getChildren().remove(leftVBoxOfMainSplit);
			
			//this leaves just enough space for the button
			treeHoldingAnchor.setMaxWidth(30);

			mainSplitPane.setDividerPosition(0, 0);
		}
		else { //then need to add it
			leftHBoxOfMainSplit.getChildren().add(1, leftVBoxOfMainSplit);
			
			treeHoldingAnchor.setMaxWidth(600); //the max size for this is arbitrary

			mainSplitPane.setDividerPosition(0, 0.225);
		}
	}
	
	
	public void bottomSplitButtonPushed() {
		
		if (bottomSplitVBox.getChildren().contains(classifierHBox)) { //then need to remove it
			bottomSplitVBox.getChildren().remove(classifierHBox);
			mR.setBottomSectionEnabled(false);
			verticalSplitPane.setDividerPosition(0, 1);
			
			//this leaves just enough space for the button
			classifierHoldingAnchor.setMaxHeight(30);
			
			if ((TypeTab) noteTabPane.getSelectionModel().getSelectedItem() != null) {
				mR.setBottomSectionShouldKeepContents(true);
			}
		}
		else { //then need to add it
			classifierHoldingAnchor.setMaxHeight(270); //button anchor is 30, anchor holding classifier is 240...
			
			bottomSplitVBox.getChildren().add(0, classifierHBox); //it will always be the last element so no index needed
			mR.setBottomSectionEnabled(true);
			
			//this is intentionally very large, because the bottom section has a maximum height, this essentially pulls it up to the maximum height
			//it will not actually go to halfway of the window
			verticalSplitPane.setDividerPosition(0, 0.5);
			
			if (((TypeTab) noteTabPane.getSelectionModel().getSelectedItem() != null) && mR.getBottomSectionShouldKeepContents() == false) {
				//changes the values in the similarNotesHBox
				mR.getPipelineConsolidator().newNoteOpenedProcedure();
				//changes the values in the pinnedNotesHBox
				mR.getPipelineConsolidator().newNoteOpenedProcedure();
			}
		}
	}
	
	public void newDailyPageButtonPushed() {
		Note newDailyScroll = mR.getNoteChooserHandler().new Note("temporary", "DailyScroll"); //name is overridden with the date later
				
		dailyPageList.getItems().add(newDailyScroll);
		
		mR.openNote(newDailyScroll);
	}
	

	/*
	 * called from the mainScreen, refreshes the similars, which essentially means
	 * calling the function which is called when a note changes
	 * 
	 * it is necessary to save the note first because the pipeline uses the saved state of the note, 
	 * not what it currently displayed in the text area
	 */
	public void refreshSimilars() throws IOException {
		mR.saveCurrentNote();
		mR.getPipelineConsolidator().newNoteOpenedProcedure();
	}
	
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		mR = new MasterReference(this);
								
		pinnedNotesHBox = new HBox();
		
		//this effectively disables the Spiderweb top label
		mainVBox.getChildren().remove(0);
		
		bottomSplitButtonPushed();
		
		
		dailyPageListScrollPane.maxWidthProperty().bind(vBoxHolderOfDailyPageList.widthProperty());
		dailyPageListScrollPane.minWidthProperty().bind(vBoxHolderOfDailyPageList.widthProperty());
		
		dailyPageListScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);		
		//leftVBoxOfMainSplit
		
		vBoxHolderOfDailyPageList.maxWidthProperty().bind(leftVBoxOfMainSplit.widthProperty());
		vBoxHolderOfDailyPageList.minWidthProperty().bind(leftVBoxOfMainSplit.widthProperty());
		
	}
		

	public TextArea getMainTextArea() {
		return mainTextArea;
	}

	public TextArea getTitleArea() {
		return titleArea;
	}

	public SplitPane getRightSideSplitPane() {
		return rightSideSplitPane;
	}

	public SplitPane getMainSplitPane() {
		return mainSplitPane;
	}

	public AnchorPane getMainSplitLeftAnchor() {
		return mainSplitLeftAnchor;
	}

	public AnchorPane getMainSplitMiddleAnchor() {
		return mainSplitMiddleAnchor;
	}

	public VBox getMainSplitRightVBox() {
		return mainSplitRightVBox;
	}

	public AnchorPane getImagePool() {
		return imagePool;
	}

	public AnchorPane getMarginKeeper() {
		return marginKeeper;
	}

	public Pane getTextSectionPane() {
		return textSectionPane;
	}

	public VBox getTextSectionVBox() {
		return textSectionVBox;
	}

	public VBox getMainVBox() {
		return mainVBox;
	}

	public Button getSaveButton() {
		return saveButton;
	}

	public HBox getBottomHBox() {
		return bottomHBox;
	}

	public Label getBottomBarLeftLabel() {
		return bottomBarLeftLabel;
	}

	public Label getBottomBarMiddleLabel() {
		return bottomBarMiddleLabel;
	}

	public Label getBottomBarRightLabel() {
		return bottomBarRightLabel;
	}

	public TreeView<Note> getNoteChooser() {
		return noteChooser;
	}

	public Button getRenameButton() {
		return renameButton;
	}

	public HBox getFunctionBox() {
		return functionBox;
	}

	public AnchorPane getClassifierFunctionBox() {
		return classifierFunctionBox;
	}

	public AnchorPane getClassifierDisplayAnchor() {
		return classifierDisplayAnchor;
	}

	public Button getBalanceImagesButton() {
		return balanceImagesButton;
	}
	
	public TabPane getNoteTabPane() {
		return noteTabPane;
	}

	public VBox getParentOfTreeView() {
		return parentOfTreeView;
	}

	public HBox getSuperParentOfTreeView() {
		return superParentOfTreeView;
	}

	public HBox getSimilarNotesHBox() {
		return similarNotesHBox;
	}

	public VBox getLeftVBoxOfMainSplit() {
		return leftVBoxOfMainSplit;
	}

	public SplitPane getVerticalSplitPane() {
		return verticalSplitPane;
	}

	public HBox getPinnedNotesHBox() {
		return pinnedNotesHBox;
	}
	
    public ListView<Note> getDailyPageList() {
		return dailyPageList;
	}

}
