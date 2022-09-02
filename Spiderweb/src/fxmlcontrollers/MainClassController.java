package fxmlcontrollers;

import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

import application.MasterReference;
import fxmlcontrollers.notetypes.ReadingTypeNoteController;
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
    @FXML
    private Button exitButton;
    
    private HBox pinnedNotesHBox;
    
    
    public void handleCloseButtonAction() {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }
    
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


	EventHandler<MouseEvent> onMouseClicked = (new EventHandler<MouseEvent>() { 
		   public void handle(MouseEvent event) { 
			   onMouseClicked(event);
			   }});
	
	
	public void onMouseClicked(MouseEvent event) {		

	}
	
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		mR = new MasterReference(this);
						
		//sets the default open bottom thing as the similar notes and not pinned notes
		handleSimilarNotesButton();
		
		pinnedNotesHBox = new HBox();
		
		//this effectively disables the Spiderweb top label
		mainVBox.getChildren().remove(0);
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
