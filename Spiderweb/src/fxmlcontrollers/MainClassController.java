package fxmlcontrollers;

import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

import application.MasterReference;
import fxmlcontrollers.notetypes.ReadingTypeNoteController;
import handlers.NoteChooserHandler;
import handlers.ClassifierHandler;
import handlers.NoteChooserHandler.Note;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    private SplitPane bottomSplit;
    @FXML
    private VBox leftVBoxOfMainSplit;
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
    private Button pinnedNotesButton;
    @FXML
    private Button similarNotesButton;
    
    private HBox pinnedNotesHBox;

    private ReadingTypeNoteController readingTypeNoteController;
    
    @FXML
   
    private NoteChooserHandler noteChooserHandler;
    private ClassifierHandler classifierHandler;
    
    //saves the current quote text and author so it will stay consistent when quotes toggled on and off
    private String currentQuoteText;
    private String currentQuoteAuthor;
    
    
    public void handlePinnedNotesButton() {
    	similarNotesButton.setDisable(false);
    	
		switchingScrollPane.setContent(pinnedNotesHBox);

    	pinnedNotesButton.setDisable(true);
    }
    
    public void handleSimilarNotesButton() {
    	pinnedNotesButton.setDisable(false);
    	
		switchingScrollPane.setContent(similarNotesHBox);

    	similarNotesButton.setDisable(true);
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
	
	//this adds / removes the large poe quote near the top of the window
	public void toggleEdgar() {
		
		//if no parent, then it is currently not on the window
		if (edgarQuote.getParent() == null) {
			
			mainVBox.getChildren().add(1, edgarAnchor0);
			mainVBox.getChildren().add(1, edgarQuote);
			mainVBox.getChildren().add(1, edgarAnchor1);
		}
		
		else {
			mainVBox.getChildren().remove(edgarAnchor0);
			mainVBox.getChildren().remove(edgarQuote);
			mainVBox.getChildren().remove(edgarAnchor1);
		}
	}
	
	
	public void toggleQuotes() {
		//if it has no text, then it is "off"
		if (bottomBarMiddleLabel.getText() != "") {
			
			bottomBarMiddleLabel.setText("");
			bottomBarRightLabel.setText("");
			
			//also needs to disable the new quote button
			newQuoteButton.setDisable(true);
			
			
		}
		//if off then turn on
		else {
			//enables new quote button
			newQuoteButton.setDisable(false);

			bottomBarMiddleLabel.setText(currentQuoteText);
			bottomBarRightLabel.setText(currentQuoteAuthor);
		}
	}
	
	
	
	public void setQuote() {
		String[][] interestingQuotes = new String[][] { {"Those who dream by day are cognizant of many things which escape those who dream only by night.", "Edgar Allan Poe"}, 
			{"There is nothing noble in being superior to your fellow man; true nobility is being superior to your former self.", "Ernest Hemingway"},
			{"The world is a fine place and worth the fighting for, and I hate very much to leave it.", "Ernest Hemingway"},
			{"We are what we repeatedly do. Excellence then is not an act, but a habit.", "Aristotle"}, 
			{"Among other evils which being unarmed brings you, it causes you to be despised.", "Machiavelli"},
			{"Better to reign in hell than serve in heav'n.", "John Milton"},
			{"Let the boy win his spurs.", "Edward III, Battle of Crecy 1345"},
			{"I am the King of Rome, and above grammar.", "Sigismund"},
			{"He withdrew, himself wounded, and was compelled to return home inglorious, weeping - he who had once vainly hoped for the glory of a triumph.", "William of Apulia"},
			{"You can't wake a person who is pretending to be asleep.", "Navajo proverb"},
			{"When he judged himself- that was his supreme moment; let not the exalted one relapse again into his low estate!", "Nietzche"}
		};
			
	    int rnd = new Random().nextInt(interestingQuotes.length);
	    String[] chosenArray = interestingQuotes[rnd];
	    
	    bottomBarMiddleLabel.setText(chosenArray[0]);
	    bottomBarRightLabel.setText("-" + chosenArray[1] + " ");
	    
	    currentQuoteText = chosenArray[0];
	    currentQuoteAuthor = chosenArray[1];
	}
    

    
    
	EventHandler<MouseEvent> onMouseClicked = (new EventHandler<MouseEvent>() { 
		   public void handle(MouseEvent event) { 
			   onMouseClicked(event);
			   }});
	
	
	public void onMouseClicked(MouseEvent event) {		

	}
	
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		mR = new MasterReference(this);
		mR.setMainClassController(this);
		
		classifierHandler = mR.getClassifierHandler();
		
		pinnedNotesHBox = new HBox();
		
		//sets the default open bottom thing as the similar notes and not pinned notes
		handleSimilarNotesButton();
		
		//handles the quotes/author on the bottom bar
		setQuote();
	}
	
	//getters
	

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

	public ReadingTypeNoteController getReadingTypeNoteController() {
		return readingTypeNoteController;
	}

	public NoteChooserHandler getNoteChooserHandler() {
		return noteChooserHandler;
	}

	public ClassifierHandler getClassifierHandler() {
		return classifierHandler;
	}

	public EventHandler<MouseEvent> getOnMouseClicked() {
		return onMouseClicked;
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

	public SplitPane getBottomSplit() {
		return bottomSplit;
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

	public Button getPinnedNotesButton() {
		return pinnedNotesButton;
	}

	public Button getSimilarNotesButton() {
		return similarNotesButton;
	}
	
	
}
