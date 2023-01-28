package fxmlcontrollers.notetypes;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Set;

import application.DatabaseHandler;
import application.MasterReference;
import application.NoteChooserHandler;
import application.NoteChooserHandler.Note;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DailyScrollController implements Initializable {
	@FXML
	private AnchorPane dailyScrollRoot;
	@FXML
	private AnchorPane leftMarginKeeper;
	@FXML
	private AnchorPane rightMarginKeeper;
	@FXML
	private HBox parentOfScrollPanes;
	@FXML
	private VBox rightCollectorVBox;
	@FXML
	private VBox leftCollectorVBox;
	@FXML
	private ScrollPane rightScrollPane;
	@FXML
	private ScrollPane leftScrollPane;
	@FXML
	private AnchorPane parentOfLeftScrollPane;
	@FXML
	private AnchorPane parentOfRightScrollPane;
	@FXML
	private AnchorPane dailyScrollToDoSection;
	@FXML
	private AnchorPane longTermGoalSection;
	@FXML
	private AnchorPane weeklyGoalSection;
	@FXML
	private VBox toDoVBox;
	@FXML
	private Button dailyScrollTopLeftButton;
	@FXML
	private Button dailyScrollTopRightButton;
	@FXML
	private Button whenNeededButton;
	@FXML
	private HBox topButtonHolder;
	@FXML
	private AnchorPane topButtonMarginMaker;
	@FXML
	private Button toDoSectionButton;
	@FXML
	private Button weeklyGoalSectionButton;
	@FXML
	private Button longTermGoalSectionButton;
	@FXML
	private VBox longTermGoalSectionVBox;
	@FXML
	private VBox shortTermGoalSectionVBox;
	@FXML
	private AnchorPane bookSection;
	@FXML
	private AnchorPane weeklyGoalResetButtonAnchor;
	@FXML
	private AnchorPane weeklyGoalMainSectionAnchor;
	@FXML
	private Button weeklyGoalResetButton;
	@FXML
	private VBox weeklyGoalSectionVBox;
	@FXML
	private VBox weeklyGoalSectionLeftSideVBox;
	@FXML
	private VBox weeklyGoalSectionRightSideVBox;
	@FXML
	private AnchorPane weeklyGoalLineUpButtonHolder;
	@FXML
	private Button weeklyGoalLineUpButton;
	@FXML
	private HBox weeklyGoalSectionHBox;
	@FXML
	private AnchorPane weeklyGoalLeftSectionBorderAnchor;
	@FXML
	private AnchorPane weeklyGoalRightSectionBorderAnchor;
	@FXML
	private AnchorPane weeklyGoalSectionLeftSideSpacer;
	@FXML
	private AnchorPane weeklyGoalSectionRightSideSpacer;
	@FXML
	private Button weeklyGoalSectionCompletedButton;
	@FXML
	private Label weeklyGoalSectionNodeLabel;
	@FXML
	private HBox bookSectionHBox;
	@FXML
	private AnchorPane bookSectionMainButtonHolder;
	@FXML
	private AnchorPane bookSectionBookshelfAnchor;
	@FXML
	private HBox bookshelfHBox;
	@FXML
	private Button bookSectionMainButton;
	@FXML
	private VBox firstBookshelf;
	@FXML
	private VBox secondBookshelf;
	@FXML
	private VBox thirdBookshelf;
	@FXML
	private ScrollPane bookshelfScrollPane;
	@FXML
	private ScrollPane firstBookshelfScrollPane;
	@FXML
	private ScrollPane secondBookshelfScrollPane;	
	@FXML
	private ScrollPane thirdBookshelfScrollPane;
	@FXML
	private AnchorPane firstBookshelfAnchor;
	@FXML
	private AnchorPane secondBookshelfAnchor;
	@FXML
	private AnchorPane thirdBookshelfAnchor;
	@FXML
	private BorderPane bookshelfSpotOne;
	@FXML
	private BorderPane bookshelfSpotTwo;
	@FXML
	private BorderPane bookshelfSpotThree;
	@FXML
	private ScrollPane weeklyGoalLeftSideScrollPane;
	@FXML
	private ScrollPane weeklyGoalRightSideScrollPane;
	@FXML
	private AnchorPane leftTextSection;
	@FXML
	private AnchorPane rightTextSection;
	@FXML
	private AnchorPane dailyScrollDoneSection;
	@FXML
	private ScrollPane dailyScrollDoneSectionScrollPane;
	@FXML
	private VBox dailyScrollDoneSectionVBox;
	@FXML
	private TextArea rightTextSectionTextArea;
	@FXML
	private TextArea leftTextSectionTextArea;
	@FXML
	private VBox dailyScrollMasterVBox;
	
	
	public void setMasterReference(MasterReference mR) {
		this.mR = mR;
	}
	
	private static Integer heightOfReflectionSection = 70;
	private static Integer heightOfClosedToDoSectionNode = 50;
	private Boolean toDoSectionInExpandedMode = false;
	//starts in expanded mode and is immediately closed on creation in order to efficiently reference elements
	private Boolean weeklyGoalSectionInExpandedMode = true;
	
	private Boolean bookSectionInExpandedMode = true;
	private Boolean bookSectionInBrowseMode = false;
	private Boolean bookSectionLibraryInitialized = false;
	private TreeItem<Note> bookNotesTreeItem;
	private static String bookShelfName = "Book Notes"; //TODO incomplete, make this more legit
	
	ArrayList<TreeItem<Note>> checkoutPile = new ArrayList<TreeItem<Note>>();
	HBox selectedBook;
	
	private static Integer widthOfSpacingOfCardLineupHBox = 5;
	
	private static Double heightOfLongTermGoalSectionNode = 60.0;
	private static Double heightOfToDoSectionNode = 50.0;
	private static Double heightOfDoneSectionNode = 55.0;
	private static Double heightOfExpandedDoneSectionNode = 135.0;
	
	private MasterReference mR;
	
	private Boolean inTimeCapsuleMode = false;
		
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		topButtonHolder.getChildren().remove(whenNeededButton);
		
				
		leftMarginKeeper.minWidthProperty().bind(parentOfLeftScrollPane.widthProperty());
		leftMarginKeeper.maxWidthProperty().bind(parentOfLeftScrollPane.widthProperty());
		
		//the margin keeper height is bounded to its child, or the collector vbox, this makes it so that the scrollpane is activated as the anchorpane becomes larger than the area
		leftMarginKeeper.minHeightProperty().bind(leftCollectorVBox.heightProperty());
		leftMarginKeeper.maxHeightProperty().bind(leftCollectorVBox.heightProperty());
		
		
		rightMarginKeeper.minWidthProperty().bind(parentOfRightScrollPane.widthProperty());
		rightMarginKeeper.maxWidthProperty().bind(parentOfRightScrollPane.widthProperty());
		
		rightMarginKeeper.minHeightProperty().bind(rightCollectorVBox.heightProperty());
		rightMarginKeeper.maxHeightProperty().bind(rightCollectorVBox.heightProperty());
		
		
		/*
		 * to do section
		 */
		dailyScrollToDoSection.minWidthProperty().bind(parentOfRightScrollPane.widthProperty().subtract(40));
		dailyScrollToDoSection.maxWidthProperty().bind(parentOfRightScrollPane.widthProperty().subtract(40));
		
		toDoVBox.setMinHeight(heightOfToDoSectionNode*4);
				
		/*
		 * done section
		 */
		dailyScrollDoneSectionVBox.minWidthProperty().bind(parentOfRightScrollPane.widthProperty().subtract(40));
		dailyScrollDoneSectionVBox.maxWidthProperty().bind(parentOfRightScrollPane.widthProperty().subtract(40));

		dailyScrollDoneSectionVBox.setMinHeight(heightOfDoneSectionNode*2);
		
		/*
		 * long term goal section
		 */
		loadFromLongTermGoalDatabase();

		longTermGoalSection.minWidthProperty().bind(parentOfLeftScrollPane.widthProperty().subtract(40));
		longTermGoalSection.maxWidthProperty().bind(parentOfLeftScrollPane.widthProperty().subtract(40));
		
		longTermGoalSectionVBox.setMinHeight(heightOfLongTermGoalSectionNode*2);

		/*
		 * weekly goal section
		 */
		weeklyGoalSection.minWidthProperty().bind(parentOfLeftScrollPane.widthProperty().subtract(40));
		weeklyGoalSection.maxWidthProperty().bind(parentOfLeftScrollPane.widthProperty().subtract(40));
		
    	changeWeeklyGoalSectionMode();
    	changeBookSectionMode();
    	
    	//subtract 20 to account for the space which is occupied by the vertical scrollbar
    	weeklyGoalSectionLeftSideVBox.prefWidthProperty().bind(weeklyGoalLeftSideScrollPane.widthProperty().subtract(30));
    	weeklyGoalSectionLeftSideVBox.prefHeightProperty().bind(weeklyGoalLeftSideScrollPane.heightProperty().subtract(30));
    	
    	loadFromWeeklyGoalDatabase();

    	
		/*
		 * book section
		 */
		bookSection.minWidthProperty().bind(parentOfLeftScrollPane.widthProperty().subtract(40));
		bookSection.maxWidthProperty().bind(parentOfLeftScrollPane.widthProperty().subtract(40));
		
		bookshelfScrollPane.maxWidthProperty().bind(bookSectionHBox.widthProperty().subtract(25));
		bookshelfScrollPane.minWidthProperty().bind(bookSectionHBox.widthProperty().subtract(25));		
		 
		bookshelfHBox.maxWidthProperty().bind(bookshelfScrollPane.widthProperty().subtract(30)); 
		bookshelfHBox.minWidthProperty().bind(bookshelfScrollPane.widthProperty().subtract(30)); 
		
		bookSectionHBox.getChildren().remove(bookshelfScrollPane);
		
		firstBookshelf.prefWidthProperty().bind(firstBookshelfAnchor.widthProperty());
		secondBookshelf.prefWidthProperty().bind(secondBookshelfAnchor.widthProperty());
		thirdBookshelf.prefWidthProperty().bind(thirdBookshelfAnchor.widthProperty());
		
		bookshelfHBox.setPrefHeight(295);
		
		/*
		 * text sections
		 */
		
		//left
		leftTextSection.minWidthProperty().bind(parentOfLeftScrollPane.widthProperty().subtract(40));
		leftTextSection.maxWidthProperty().bind(parentOfLeftScrollPane.widthProperty().subtract(40));
		
		leftTextSection.setMinHeight(400);
				
		//right
		rightTextSection.minWidthProperty().bind(parentOfRightScrollPane.widthProperty().subtract(40));
		rightTextSection.maxWidthProperty().bind(parentOfRightScrollPane.widthProperty().subtract(40));
		
		rightTextSection.setMinHeight(400);
	}
	
	/*
	 * 1/25/2023-9:36PM --- 5/5
	 * 
	 * because the nodes aren't uniform size and we can't directly access their height, we infer upon their height
	 */
	private void resizeDoneSection() {
		Double newHeight = 0.0;
		for (Node node: dailyScrollDoneSectionVBox.getChildren()) {
			AnchorPane root = (AnchorPane) node;
			AnchorPane marginKeeper = (AnchorPane) root.getChildren().get(0);
			VBox vbox = (VBox) marginKeeper.getChildren().get(0);
			AnchorPane reflectionAreaHolder = (AnchorPane) vbox.getChildren().get(1);
			
			if (reflectionAreaHolder.isVisible()) {
				newHeight+=heightOfExpandedDoneSectionNode;
			}
			else {
				newHeight+=heightOfDoneSectionNode;
			}
		}
		
		if (newHeight <= heightOfDoneSectionNode*2) {
			dailyScrollDoneSectionVBox.setMinHeight(heightOfDoneSectionNode*2);
			dailyScrollDoneSectionVBox.setMaxHeight(heightOfDoneSectionNode*2);			
		}
		else {
			dailyScrollDoneSectionVBox.setMinHeight(newHeight);
			dailyScrollDoneSectionVBox.setMaxHeight(newHeight);			
		}
	}
	
	/*
	 * used for both upsize and downsize, toDoSeciton needs a special method for resizing because it needs to match the height of its child vbox
	 * this child vbox's size changes but does not trigger a resize through javafx, this essentially forces it to change to what it should have been prior
	 */
	private void resizeToDoSection() {
		Double newHeight = toDoVBox.getChildren().size()*heightOfToDoSectionNode;
		if (newHeight <= heightOfToDoSectionNode*4) {
			dailyScrollToDoSection.setMinHeight(heightOfToDoSectionNode*4);
			dailyScrollToDoSection.setMaxHeight(heightOfToDoSectionNode*4);
		}
		else {
			dailyScrollToDoSection.setMinHeight(newHeight);
			dailyScrollToDoSection.setMaxHeight(newHeight);
		}
	}
	
	/*
	 * see comments for above method resizeToDoSection()
	 */
	private void resizeLongTermGoalSection() {
		Double newHeight = longTermGoalSectionVBox.getChildren().size()*heightOfLongTermGoalSectionNode;
		if (newHeight <= heightOfLongTermGoalSectionNode*2) {
			longTermGoalSection.setMinHeight(heightOfLongTermGoalSectionNode*2);
			longTermGoalSection.setMaxHeight(heightOfLongTermGoalSectionNode*2);
		}
		else {
			longTermGoalSection.setMinHeight(newHeight);
			longTermGoalSection.setMaxHeight(newHeight);
		}
	}
	
	
	/*
	 * this must be run after initialization because we need an active reference to mR
	 */
	public void loadFromBookDeskDatabase() {
		//in order to load the books on the "desk", we essentially simulate the user actually adding the books to the specific scroll
		ArrayList<String> titles = DatabaseHandler.loadFromBookDeskTable();
		
		dailyScrollTopLeftButtonPushed();
		bookSectionMainButtonPushed();
				
		ObservableList<Node> observableList = FXCollections.observableArrayList();;
		
		observableList.addAll(firstBookshelf.getChildren());
		observableList.addAll(secondBookshelf.getChildren());
		observableList.addAll(thirdBookshelf.getChildren());
		
		for (Node node : observableList) {
			AnchorPane anchor = (AnchorPane) node;
			BorderPane borderPane = (BorderPane) anchor.getChildren().get(0);
			VBox vbox = (VBox) borderPane.getLeft();
			HBox hbox = (HBox) vbox.getChildren().get(1);
			
			Button selectionButton = (Button) hbox.getChildren().get(3);
						
			if (titles.contains(selectionButton.getText())) {
				selectionButton.fire();
			}
		}
		
		bookSectionMainButtonPushed();
		whenNeededButtonPushed();
	}
	
	/*
	 * this button disables the right side, then switches the two buttons with one larger button
	 */
	public void dailyScrollTopLeftButtonPushed() {
		
		parentOfScrollPanes.getChildren().remove(1);
		
		topButtonHolder.getChildren().clear();
		topButtonHolder.getChildren().add(whenNeededButton);
		
		
		if (inTimeCapsuleMode) {
			parentOfLeftScrollPane.maxWidthProperty().bind(parentOfScrollPanes.widthProperty());
			parentOfLeftScrollPane.minWidthProperty().bind(parentOfScrollPanes.widthProperty());
		    AnchorPane.setRightAnchor(leftTextSection, 10.0);
		}
		else {
			changeWeeklyGoalSectionMode();
			changeBookSectionMode();
		}
	}
	
	/*
	 * this button disables the left side, then switches the two buttons with one larger button
	 * 
	 * this also opens the reflection sections in the ToDo Section
	 * 
	 * needs to adjust the size of each of the nodes, currently the size of the reflectionArea is __, this will be set to a static private variable for 
	 * easy changes as I adjust the cosmetic aspects of the scroll later 
	 */
	public void dailyScrollTopRightButtonPushed() {
		
		parentOfScrollPanes.getChildren().remove(0);

		topButtonHolder.getChildren().clear();
		topButtonHolder.getChildren().add(whenNeededButton);
		
		if (inTimeCapsuleMode) {
			parentOfRightScrollPane.maxWidthProperty().bind(parentOfScrollPanes.widthProperty());
			parentOfRightScrollPane.minWidthProperty().bind(parentOfScrollPanes.widthProperty());
		    AnchorPane.setLeftAnchor(rightTextSection, 10.0);
		}
	}
	
	/*
	 * 1/27/2023-10:35PM --- 4/5
	 * 
	 * this method needed to be adjusted to fit the structure of the TimeCapsule mode
	 */
	public void whenNeededButtonPushed() {
		
		if (parentOfScrollPanes.getChildren().contains(parentOfLeftScrollPane)) {
			parentOfScrollPanes.getChildren().add(parentOfRightScrollPane);
		}
		else {
			parentOfScrollPanes.getChildren().add(0, parentOfLeftScrollPane);
		}
		
		topButtonHolder.getChildren().clear();
		
		topButtonHolder.getChildren().add(dailyScrollTopLeftButton);
		topButtonHolder.getChildren().add(topButtonMarginMaker);
		topButtonHolder.getChildren().add(dailyScrollTopRightButton);
		
		if (!inTimeCapsuleMode) {
			if (toDoSectionInExpandedMode == true) {
				//placeholder, describes relevance of the toDoSectionInExpandedMode variable
			}
			if (weeklyGoalSectionInExpandedMode == true) {
				changeWeeklyGoalSectionMode();
			}
			if (bookSectionInExpandedMode == true) {
				changeBookSectionMode();
				if (bookSectionInBrowseMode == true) {
					bookSectionMainButtonPushed();
				}
			}
		}
		else {
			parentOfLeftScrollPane.maxWidthProperty().bind(parentOfScrollPanes.widthProperty().divide(2));
			parentOfLeftScrollPane.minWidthProperty().bind(parentOfScrollPanes.widthProperty().divide(2));
		    AnchorPane.setRightAnchor(leftTextSection, 5.0);

			parentOfRightScrollPane.maxWidthProperty().bind(parentOfScrollPanes.widthProperty().divide(2));
			parentOfRightScrollPane.minWidthProperty().bind(parentOfScrollPanes.widthProperty().divide(2));
		    AnchorPane.setLeftAnchor(rightTextSection, 5.0);
		}
	}
	
	
	/*
	 * 1/25/2023-8:40PM --- 5/5
	 * 
	 * there needs to be a toDoSectionNode which is a stepping place for the DoneSecitonNode, which is saved permanently
	 * 
	 * returns a textfield so that if the contents of the textfield are already known, the node can be created and textfield value changed
	 */
	public TextField toDoSectionButtonPushed() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXMLs/DailyScrollSubFXMLs/ToDoSectionNode.fxml"));
		try {
			AnchorPane loadedNode = fxmlLoader.load();
						
			toDoVBox.getChildren().add(loadedNode);
			resizeToDoSection();
			
			loadedNode.maxWidthProperty().bind(toDoVBox.widthProperty());
			loadedNode.minWidthProperty().bind(toDoVBox.widthProperty());
			
			Button actionButton = (Button) loadedNode.getChildren().get(0);
			TextField textField = (TextField) loadedNode.getChildren().get(1);
			Button deleteButton = (Button) loadedNode.getChildren().get(2);
			
			handleDeleteButtonListener(deleteButton, loadedNode, toDoVBox);
			
			actionButton.setOnAction(new EventHandler<ActionEvent>() { 
	    		@Override
	    		public void handle(ActionEvent event) {
	    			try {
						directlyCreateDoneSectionNode(textField.getText());
		    			toDoVBox.getChildren().remove(loadedNode);
					} catch (IOException e) {
						e.printStackTrace();
					}
	    		}});
			
			return textField;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/*
	 * 1/26/2023-5:08PM --- 5/5
	 * 
	 * needs to remove toDoSectionNode, add DoneSectionNode with title of the now-remove toDoSectionNode, remove toDoSectionNode from its vBox
	 * 
	 * returns its ReflectionTextArea so that this can be edited from the DatabaseHandler class
	 */
	public TextArea directlyCreateDoneSectionNode(String description) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXMLs/DailyScrollSubFXMLs/DoneSectionNode.fxml"));
		AnchorPane doneSectionRoot = fxmlLoader.load();
		AnchorPane marginKeeper = (AnchorPane) doneSectionRoot.getChildren().get(0);
		VBox vbox = (VBox) marginKeeper.getChildren().get(0);
		BorderPane borderPane = (BorderPane) vbox.getChildren().get(0);
		
		Button mainButton = (Button) borderPane.getCenter();
		mainButton.setMinWidth(100);				
		mainButton.setText(description);
		
		Button deleteButton = (Button) borderPane.getRight();
		deleteButton.setOnAction(new EventHandler<ActionEvent>() {
			/*
			 * 1/25/2023-8:50PM --- TODO incomplete, this needs to call to database
			 * 
			 * removes the DoneSectionNode, sends back to the toDoSection, with the same value for the button / textfield
			 */
    		@Override
    		public void handle(ActionEvent event) {
				dailyScrollDoneSectionVBox.getChildren().remove(doneSectionRoot);
				resizeDoneSection();
				
				TextField textField = toDoSectionButtonPushed();
				textField.setText(mainButton.getText());
    		}});
		
		AnchorPane textAreaHolder = (AnchorPane) vbox.getChildren().get(1);
		TextArea reflectionTextArea = (TextArea) textAreaHolder.getChildren().get(0);
		textAreaHolder.setVisible(false);
		reflectionTextArea.setVisible(false);
		
		mainButton.setOnAction(new EventHandler<ActionEvent>() {
			/*
			 * 1/25/2023-9:25PM --- 
			 * 
			 * toggles between the reflectionTextArea and its holder being visible or not and also must change the size of the node
			 */
    		@Override
    		public void handle(ActionEvent event) {
    			
    			if (reflectionTextArea.isVisible()) {
	    			doneSectionRoot.setPrefHeight(heightOfDoneSectionNode);
	    			
	    			textAreaHolder.setVisible(false);
	    			reflectionTextArea.setVisible(false);
	    			resizeDoneSection();
    			}
    			else {
	    			doneSectionRoot.setPrefHeight(heightOfExpandedDoneSectionNode);
	    			
	    			textAreaHolder.setVisible(true);
	    			reflectionTextArea.setVisible(true);
	    			resizeDoneSection();
    			}				    			
    		}});
		
		doneSectionRoot.maxWidthProperty().bind(dailyScrollDoneSectionVBox.widthProperty());
		doneSectionRoot.minWidthProperty().bind(dailyScrollDoneSectionVBox.widthProperty());
								
		dailyScrollDoneSectionVBox.getChildren().add(doneSectionRoot);
		resizeDoneSection();
		
		return reflectionTextArea;
	}
	
	
	
	//TODO incomplete
	public void changeWeeklyGoalSectionMode() {
		
		if (weeklyGoalSectionInExpandedMode == false) {
			weeklyGoalSectionInExpandedMode = true;
			
			weeklyGoalSection.setMaxHeight(325);
			weeklyGoalSection.setMinHeight(325);
			
			weeklyGoalSectionVBox.getChildren().add(weeklyGoalResetButtonAnchor);
			//weeklyGoalLineUpButtonHolder.getChildren().add(weeklyGoalLineUpButton);
		}
		else { //removes the reset button, button to add more nodes on left side
			weeklyGoalSectionInExpandedMode = false;
			
			weeklyGoalSection.setMaxHeight(285);
			weeklyGoalSection.setMinHeight(285);
			
			weeklyGoalSectionVBox.getChildren().remove(weeklyGoalResetButtonAnchor);
			//weeklyGoalLineUpButtonHolder.getChildren().remove(weeklyGoalLineUpButton);
			
		}
	}
	
	//TODO incomplete
	public void changeBookSectionMode() {
		
		if (bookSectionInExpandedMode == false) {
			bookSectionInExpandedMode = true;
						
			bookSectionHBox.getChildren().add(bookSectionMainButtonHolder);
		}
		else {
			bookSectionInExpandedMode = false;
			
			bookSectionHBox.getChildren().remove(bookSectionMainButtonHolder);
		}
	}
	
	
	private void handleDeleteButtonListener(Button button, AnchorPane anchor, VBox vbox) {
        button.setOnAction(new EventHandler<ActionEvent>() { 
		@Override
		public void handle(ActionEvent event) {
			vbox.getChildren().remove(anchor);
			
			resizeToDoSection();
		}});
	}
	
	
	public void longTermGoalSectionButtonPushed() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXMLs/DailyScrollSubFXMLs/LongTermGoalSectionNode.fxml"));
		try {
			AnchorPane loadedNode = fxmlLoader.load();
									
			longTermGoalSectionVBox.getChildren().add(loadedNode);
			resizeLongTermGoalSection();
			
			Button deleteButton = (Button) loadedNode.getChildren().get(3);
			deleteButton.setOnAction(new EventHandler<ActionEvent>() { 
	    		@Override
	    		public void handle(ActionEvent event) {
	    			longTermGoalSectionVBox.getChildren().remove(loadedNode);
	    			resizeLongTermGoalSection();
	    		}});
			
			AnchorPane parentOfDescriptionTextField = (AnchorPane) loadedNode.getChildren().get(1);
			TextField longTermGoalSectionDescriptionTextField = (TextField) parentOfDescriptionTextField.getChildren().get(0);
			
			AnchorPane parentOfInputTextField = (AnchorPane) loadedNode.getChildren().get(2);
			TextField longTermGoalSectionDaysInputTextField = (TextField) parentOfInputTextField.getChildren().get(0);
			
			longTermGoalSectionDaysInputTextField.textProperty().addListener(new ChangeListener<String>() {
			    @Override
			    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		            if (newValue.length() > 3) {
		            	longTermGoalSectionDaysInputTextField.setText(oldValue);
		            }
		            if ((isInteger(newValue) == false) && (newValue.length() != 0)) {
		            	longTermGoalSectionDaysInputTextField.setText(oldValue);
		            }
			    }
			});
			
			loadedNode.maxWidthProperty().bind(longTermGoalSectionVBox.widthProperty());
			loadedNode.minWidthProperty().bind(longTermGoalSectionVBox.widthProperty());
			
			Button actionButton = (Button) loadedNode.getChildren().get(0);
			
			/*
			 * if successful, this will also save the long term goal to the database, this section is consistent
			 * throughout all scrolls, each will have identical long term goal sections
			 */
			actionButton.setOnAction(new EventHandler<ActionEvent>() { 
	    		@Override
	    		public void handle(ActionEvent event) {
	    			//need to check if there is text in the description text field
	    			//if the description text is good and number is good, then convert to "LongTermGoalSectionNodeLocked.fxml"
    				Boolean valid = true;
	    			
	    			if (longTermGoalSectionDescriptionTextField.getText().length() > 0) {
	    				Set<String> allowedCharacters = NoteChooserHandler.getAllowedcharacters();
	    				
	    				String contents = longTermGoalSectionDescriptionTextField.getText();
	    				Integer counter = 0;
	    				while (counter < contents.length()) {
	    					if ((allowedCharacters.contains(String.valueOf(contents.charAt(counter)).toLowerCase()) == false)
	    					&& (String.valueOf(contents.charAt(counter)).toLowerCase().equals(" ") == false)) {
	    						valid = false;
	    					}
	    					counter += 1;
	    				}
	    			}
	    			
	    			if (longTermGoalSectionDaysInputTextField.getText().length() > 0) {
		    			if (Integer.valueOf(longTermGoalSectionDaysInputTextField.getText()) < 0) {
		    				valid = false;
		    			}
	    			}
	    			else {
	    				valid = false;
	    			}
	    			
	    			//TODO
	    			//for now its okay for nothing to happen if valid == false
	    			if (valid == true) {
	    		        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXMLs/DailyScrollSubFXMLs/LongTermGoalSectionNodeLocked.fxml"));
	    				try {
							AnchorPane lockedNode = fxmlLoader.load();
							AnchorPane marginKeeper = (AnchorPane) lockedNode.getChildren().get(0);
							
							BorderPane parentOfDescriptionButton = (BorderPane) marginKeeper.getChildren().get(0);
							Button descriptionButton = (Button) parentOfDescriptionButton.getLeft();
							
							AnchorPane parentOfInputLabel = (AnchorPane) marginKeeper.getChildren().get(1);
							Label longTermGoalSectionDaysLabel = (Label) parentOfInputLabel.getChildren().get(0);
							
							descriptionButton.setText(longTermGoalSectionDescriptionTextField.getText());
							
							LocalDate date = LocalDate.now();
							LocalDate inTime = date.plusDays(Integer.valueOf(longTermGoalSectionDaysInputTextField.getText()));
							DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
							String stringDate = formatter.format(inTime);
							
							String[] dateArray = stringDate.split("-");
							//converting to int then back to string removes the leading 0's
							String dayString = Integer.valueOf(dateArray[0]).toString();
							String monthString = Integer.valueOf(dateArray[1]).toString();
							String yearString = Integer.valueOf(dateArray[2]).toString();
							
							Integer daysBetween = (int) ChronoUnit.DAYS.between(date, inTime);
							longTermGoalSectionDaysLabel.setText(daysBetween.toString() + " days");
							
							DatabaseHandler.saveToLongTermGoalTable(longTermGoalSectionDescriptionTextField.getText(), dayString, monthString, yearString);
														
							handleLongTermGoalCompleteButton(descriptionButton, longTermGoalSectionDescriptionTextField.getText(), lockedNode);
							
							//need to remove the loadedNode from the vBox, replace it with lockedNode
							Integer indexCounter = 0;
							for (Node node : longTermGoalSectionVBox.getChildren()) {
								if ((AnchorPane) node == loadedNode) {
									longTermGoalSectionVBox.getChildren().remove(node);
									resizeLongTermGoalSection();
									break; //breaks out of for loop, ends counter of index counter on whatever index
								}
								indexCounter += 1;
							}
							
							lockedNode.maxWidthProperty().bind(longTermGoalSectionVBox.widthProperty());
							lockedNode.minWidthProperty().bind(longTermGoalSectionVBox.widthProperty());
							
							longTermGoalSectionVBox.getChildren().add(indexCounter, lockedNode);
							resizeLongTermGoalSection();

						} catch (IOException e) {
							e.printStackTrace();
						}
	    			}
	    		}
	    	});
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	/*
	 * TODO, incomplete the long term goals should be sorted by time until date somehow
	 */
	private void loadFromLongTermGoalDatabase() {
		
		ArrayList<ArrayList<String>> listOfLists = DatabaseHandler.loadFromLongTermGoalTable();
		
		/*
		 * Description, Day, Month, Year
		 */
		Integer count = 0;
		
		for (ArrayList<String> entry : listOfLists) {
						
			try {
				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXMLs/DailyScrollSubFXMLs/LongTermGoalSectionNodeLocked.fxml"));
				AnchorPane lockedNode = fxmlLoader.load();
				AnchorPane marginKeeper = (AnchorPane) lockedNode.getChildren().get(0);
				
				BorderPane parentOfDescriptionButton = (BorderPane) marginKeeper.getChildren().get(0);
				Button descriptionButton = (Button) parentOfDescriptionButton.getLeft();
				
				AnchorPane parentOfInputLabel = (AnchorPane) marginKeeper.getChildren().get(1);
				Label longTermGoalSectionDaysLabel = (Label) parentOfInputLabel.getChildren().get(0);
				
				descriptionButton.setText(entry.get(0));
				
				LocalDate date = LocalDate.now();
				LocalDate entryDate = LocalDate.of(Integer.valueOf(entry.get(3)), Integer.valueOf(entry.get(2)), Integer.valueOf(entry.get(1)));
				
				Integer daysBetween = (int) ChronoUnit.DAYS.between(date, entryDate);
				longTermGoalSectionDaysLabel.setText(daysBetween.toString() + " days");
												
				handleLongTermGoalCompleteButton(descriptionButton, entry.get(0), lockedNode);
				
				lockedNode.maxWidthProperty().bind(longTermGoalSectionVBox.widthProperty());
				lockedNode.minWidthProperty().bind(longTermGoalSectionVBox.widthProperty());
				
				longTermGoalSectionVBox.getChildren().add(lockedNode);
				
				++count;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		resizeLongTermGoalSection();
	}
	
	//TODO finish this when I finalize this with database
	private void handleLongTermGoalCompleteButton(Button button, String description, AnchorPane node) {
		button.setOnAction(new EventHandler<ActionEvent>() { 
    		@Override
    		public void handle(ActionEvent event) {
    			DatabaseHandler.deleteFromLongTermGoalTable(description);
    			
				Integer indexCounter = 0;
				for (Node presentNode : longTermGoalSectionVBox.getChildren()) {
					if (((AnchorPane) presentNode).equals(node)) {
						longTermGoalSectionVBox.getChildren().remove(node);
						resizeLongTermGoalSection();
						break; //breaks out of for loop, ends counter of index counter on whatever index
					}
					indexCounter += 1;
				}
    		}
		});
	}
	
	/*
	 * weeklyGoalSectionCreationDeleteButton, weeklyGoalDescriptionTextField, weeklyGoalQuantityCombobox, weeklyGoalSectionCreationDeleteButton
	 */
	
	public void weeklyGoalLineUpButtonPushed() {
		
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXMLs/DailyScrollSubFXMLs/WeeklyGoalSectionCreationStage.fxml"));
			AnchorPane creationNode = fxmlLoader.load();
			
			Button weeklyGoalCreateButton = (Button) creationNode.getChildren().get(0);
			
			HBox containerHBox = (HBox) creationNode.getChildren().get(1);
			
			AnchorPane parentOfWeeklyGoalDescriptionTextField = (AnchorPane) containerHBox.getChildren().get(0);
			TextField weeklyGoalDescriptionTextField = (TextField) parentOfWeeklyGoalDescriptionTextField.getChildren().get(0);
			
			AnchorPane parentOfWeeklyGoalQuantityCombobox = (AnchorPane) containerHBox.getChildren().get(1);
			ComboBox weeklyGoalQuantityCombobox = (ComboBox) parentOfWeeklyGoalQuantityCombobox.getChildren().get(0);	
			
			weeklyGoalQuantityCombobox.getItems().add(1);
			weeklyGoalQuantityCombobox.getItems().add(2);
			weeklyGoalQuantityCombobox.getItems().add(3);
			weeklyGoalQuantityCombobox.getItems().add(4);
			weeklyGoalQuantityCombobox.getItems().add(5);
			weeklyGoalQuantityCombobox.getItems().add(6);
			weeklyGoalQuantityCombobox.getItems().add(7);
			
			weeklyGoalQuantityCombobox.getSelectionModel().select(0);
			
			Button weeklyGoalSectionCreationDeleteButton = (Button) creationNode.getChildren().get(2);
			
			handleWeeklyGoalSectionCreationDeleteButton(weeklyGoalSectionCreationDeleteButton, creationNode);
			handleWeeklyGoalCreateButton(weeklyGoalCreateButton, weeklyGoalDescriptionTextField, weeklyGoalQuantityCombobox, creationNode);
			
			creationNode.minWidthProperty().bind(weeklyGoalLeftSectionBorderAnchor.widthProperty().subtract(40));
			creationNode.maxWidthProperty().bind(weeklyGoalLeftSectionBorderAnchor.widthProperty().subtract(40));
			
			weeklyGoalSectionLeftSideVBox.getChildren().add(creationNode);
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void handleWeeklyGoalCreateButton(Button button, TextField textField, ComboBox comboBox, AnchorPane originalNode) {
		button.setOnAction(new EventHandler<ActionEvent>() { 
    		@Override
    		public void handle(ActionEvent event) {
				Boolean valid = true;
    			
    			if (textField.getText().length() > 0) {
    				Set<String> allowedCharacters = NoteChooserHandler.getAllowedcharacters();
    				
    				String contents = textField.getText();
    				Integer counter = 0;
    				while (counter < contents.length()) {
    					if ((allowedCharacters.contains(String.valueOf(contents.charAt(counter)).toLowerCase()) == false)
    					&& (String.valueOf(contents.charAt(counter)).toLowerCase().equals(" ") == false)) {
    						valid = false;
    					}
    					counter += 1;
    				}
    			}
    			
    			//TODO
    			//for now its okay for nothing to happen if valid == false
    			if (valid == true) {
    				
    				//first, remove original template node from the vbox
    				weeklyGoalSectionLeftSideVBox.getChildren().remove(originalNode);
    				
    				Integer count = (Integer) comboBox.getSelectionModel().getSelectedItem();
    				
    				DatabaseHandler.saveGoalToWeeklyGoalTable(textField.getText(), count);
    				
    				createWeeklyGoalSectionCreationStageNode(textField.getText(), count);
    			}
    		}
		});
	}
	
	private HBox createWeeklyGoalSectionCreationStageNode(String text, Integer count) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXMLs/DailyScrollSubFXMLs/WeeklyGoalSectionCreatedStage.fxml"));
			AnchorPane createdNode = fxmlLoader.load();
			AnchorPane coloredBackground = (AnchorPane) createdNode.getChildren().get(0);
			
			BorderPane borderPane = (BorderPane) coloredBackground.getChildren().get(0);
			Label descriptionLabel = (Label) borderPane.getCenter();
			
			AnchorPane hboxHolder = (AnchorPane) coloredBackground.getChildren().get(1);
			HBox cardHolderHBox = (HBox) hboxHolder.getChildren().get(0);
			
			
			AnchorPane deleteButtonHolder = (AnchorPane) createdNode.getChildren().get(1);
			Button deleteButton = (Button) deleteButtonHolder.getChildren().get(0);
			
			//nothing will be written to database at this stage, just remove the node from the vBox
			deleteButton.setOnAction(new EventHandler<ActionEvent>() { 
	    		@Override
	    		public void handle(ActionEvent event) {
	    			weeklyGoalSectionLeftSideVBox.getChildren().remove(createdNode);
	    			DatabaseHandler.deleteGoalFromWeeklyGoalTable(text);
	    			
	    			//if we delete a goal, we also need to delete its instances in the right side
	    			
	    			for (Node node: weeklyGoalSectionRightSideVBox.getChildren()) {
	    				AnchorPane root = (AnchorPane) node;
	    				AnchorPane borderPaneAnchor = (AnchorPane) root.getChildren().get(0);
	    				BorderPane borderPane = (BorderPane) borderPaneAnchor.getChildren().get(0);
	    				Button descriptionButton = (Button) borderPane.getLeft();
	    				
	    				if (descriptionButton.getText().equals(text)) {
	    					weeklyGoalSectionRightSideVBox.getChildren().remove(node);
	    					break; //breaks because there will be no duplicates
	    				}
	    			}
    			}
    		});
	
			
			descriptionLabel.setText(text);
			
			createdNode.minWidthProperty().bind(weeklyGoalLeftSectionBorderAnchor.widthProperty().subtract(40));
			createdNode.maxWidthProperty().bind(weeklyGoalLeftSectionBorderAnchor.widthProperty().subtract(40));
			
			weeklyGoalSectionLeftSideVBox.getChildren().add(createdNode);
						
			for (int i = 0; i < count; i++) {
				FXMLLoader subLoader = new FXMLLoader(getClass().getResource("/FXMLs/DailyScrollSubFXMLs/WeeklyGoalLineupNode.fxml"));
				AnchorPane lineupNode = subLoader.load();
				
				Double totalExtraSpace = (double) (widthOfSpacingOfCardLineupHBox * (count - 1));
				Double perEachExtraSpace = (double) totalExtraSpace / count;
				
				lineupNode.maxWidthProperty().bind(cardHolderHBox.widthProperty().divide(count).subtract(perEachExtraSpace));
				lineupNode.minWidthProperty().bind(cardHolderHBox.widthProperty().divide(count).subtract(perEachExtraSpace));
				
				lineupNode.setOnMouseClicked(new EventHandler<MouseEvent>() { 
		    		@Override
		    		public void handle(MouseEvent event) {
		    			lineupNodeClicked(count, perEachExtraSpace, cardHolderHBox, descriptionLabel.getText());
		    			}
		    		});
				
				cardHolderHBox.getChildren().add(lineupNode);
				}
			
			return cardHolderHBox;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void lineupNodeClicked(Integer originalCount, Double perEachExtraSpace, HBox cardHolderHBox, String goalDescription) {
		
		//remove from the hbox, but place a blank anchorpane at the end in order to represent the absence of the node
		
		try {
			
			//first we need to check if node already exists in the queue (can't be added twice)
			Boolean stillOkay = true;
			for (Node node : weeklyGoalSectionRightSideVBox.getChildren()) {
				AnchorPane root = (AnchorPane) node;
				AnchorPane marginKeeper = (AnchorPane) root.getChildren().get(0);
				BorderPane borderPane = (BorderPane) marginKeeper.getChildren().get(0);
				Button actionButton = (Button) borderPane.getLeft();
				
				if (actionButton.getText().equals(goalDescription)) {
					stillOkay = false;
				}
			}
			
			if (stillOkay == true) {
				//remove the first node because this will always have contents
				cardHolderHBox.getChildren().remove(0);
						
				AnchorPane blankAnchor = new AnchorPane();
				blankAnchor.getStyleClass().add("weeklyGoalLineupNodeGone");
				
				blankAnchor.maxWidthProperty().bind(cardHolderHBox.widthProperty().divide(originalCount).subtract(perEachExtraSpace));
				blankAnchor.minWidthProperty().bind(cardHolderHBox.widthProperty().divide(originalCount).subtract(perEachExtraSpace));
				cardHolderHBox.getChildren().add(blankAnchor);
				
				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXMLs/DailyScrollSubFXMLs/WeeklyGoalSectionNodeLocked.fxml"));
				AnchorPane lockedNode = fxmlLoader.load();
				AnchorPane marginKeeper = (AnchorPane) lockedNode.getChildren().get(0);
				
				BorderPane actionButtonBorderPane = (BorderPane) marginKeeper.getChildren().get(0);
				Button actionButton = (Button) actionButtonBorderPane.getLeft();
				
				AnchorPane unqueueButtonHolder = (AnchorPane) marginKeeper.getChildren().get(1);
				Button unqueueButton = (Button) unqueueButtonHolder.getChildren().get(0);
				
				//puts the lockedNode back into the list of lineup cards. does not affect database
				unqueueButton.setOnAction(new EventHandler<ActionEvent>() { 
		    		@Override
		    		public void handle(ActionEvent event) {
		    			weeklyGoalSectionRightSideVBox.getChildren().remove(lockedNode); 
		    			
		    			//will need to remove the left-most blank anchorpane and replace it with a card
		    			
						try {
			    			FXMLLoader subLoader = new FXMLLoader(getClass().getResource("/FXMLs/DailyScrollSubFXMLs/WeeklyGoalLineupNode.fxml"));
	        				AnchorPane lineupNode = subLoader.load();
							
	        				Double totalExtraSpace = (double) (widthOfSpacingOfCardLineupHBox * (originalCount - 1));
	        				Double perEachExtraSpace = (double) totalExtraSpace / originalCount;
	        				
	        				lineupNode.maxWidthProperty().bind(cardHolderHBox.widthProperty().divide(originalCount).subtract(perEachExtraSpace));
	        				lineupNode.minWidthProperty().bind(cardHolderHBox.widthProperty().divide(originalCount).subtract(perEachExtraSpace));
	        				
	        				lineupNode.setOnMouseClicked(new EventHandler<MouseEvent>() { 
					    		@Override
					    		public void handle(MouseEvent event) {
					    			lineupNodeClicked(originalCount, perEachExtraSpace, cardHolderHBox, goalDescription);
					    			}
					    		});
	        				
	        				
	        				//instead of just adding this node, we need to find the first blank anchorpane from the left, remove it and then
	        				//place the lineupNode at the index
	        				for (Node node :cardHolderHBox.getChildren()) {
	        					
	        					AnchorPane root = (AnchorPane) node;
	        						        					
	        					if (root.getChildren().size() == 0) {
	        						Integer index = cardHolderHBox.getChildren().indexOf(node);
	        						
	        						cardHolderHBox.getChildren().remove(node);
	        						
	        						cardHolderHBox.getChildren().add(index, lineupNode);
	        							        						
	        						break; //breaks out of for loop, process only must happen once
	        					}
	        				}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		    		}
				});
								
				actionButton.setText(goalDescription);
				
				actionButton.setOnAction(new EventHandler<ActionEvent>() { 
		    		@Override
		    		public void handle(ActionEvent event) {
		    			weeklyGoalSectionRightSideVBox.getChildren().remove(lockedNode);
		    			
		    			DatabaseHandler.deleteRepetitionFromWeeklyGoalTable(goalDescription);
		    		}
				});
				
				lockedNode.maxWidthProperty().bind(weeklyGoalRightSectionBorderAnchor.widthProperty().subtract(20));
				lockedNode.minWidthProperty().bind(weeklyGoalRightSectionBorderAnchor.widthProperty().subtract(20));
				
				weeklyGoalSectionRightSideVBox.getChildren().add(lockedNode);
			}
			else { //for now it is okay to do nothing on false TODO incomplete
				
			}
			
			
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * because the node won't have been written to database yet, just removes it from the vBox
	 */
	private void handleWeeklyGoalSectionCreationDeleteButton(Button button, AnchorPane creationNode) {
		
		button.setOnAction(new EventHandler<ActionEvent>() { 
    		@Override
    		public void handle(ActionEvent event) {
    			weeklyGoalSectionLeftSideVBox.getChildren().remove(creationNode);
    		}
		});
	}
	
	/*
	 * first, we receive an ArrayList<ArrayList<String>> from the database handler which contains all the info we need
	 * about the weekly goals and their statuses
	 * 
	 * second, recreate each weekly goal with the correct number of lineup nodes present
	 */
	private void loadFromWeeklyGoalDatabase() {
		
		ArrayList<ArrayList<String>> listOfLists = DatabaseHandler.loadFromWeeklyGoalTable();
		
		for (ArrayList<String> entry : listOfLists) {
						
			Integer originalCount = Integer.valueOf(entry.get(1));
			HBox cardHolderHBox = createWeeklyGoalSectionCreationStageNode(entry.get(0), originalCount);
												
			Integer counter = 0;
			Integer difference = originalCount - Integer.valueOf(entry.get(2));
			
			while (counter < difference) {
				Double totalExtraSpace = (double) (widthOfSpacingOfCardLineupHBox * (originalCount - 1));
				Double perEachExtraSpace = (double) totalExtraSpace / originalCount;
				
				cardHolderHBox.getChildren().remove(0);
				
				AnchorPane blankAnchor = new AnchorPane();
				blankAnchor.getStyleClass().add("weeklyGoalLineupNodeGone");
				
				blankAnchor.maxWidthProperty().bind(cardHolderHBox.widthProperty().divide(originalCount).subtract(perEachExtraSpace));
				blankAnchor.minWidthProperty().bind(cardHolderHBox.widthProperty().divide(originalCount).subtract(perEachExtraSpace));
				cardHolderHBox.getChildren().add(blankAnchor);
				
				counter ++;
			}
		}
	}
	
	/*
	 * this needs to reset all the remaining repetitions values in the weeklygoaltable back to the total original count,
	 * needs to remove all of the nodes from the weeklyGoalSectionRightSideVBox, and needs to replace all blank anchors with lineup nodes
	 * (this can be done by just washing everything, then loading from the database again with the new values)
	 */
	public void weeklyGoalResetButtonPushed() {
		
		DatabaseHandler.resetRepetitionsFromWeeklyGoalTable();
		
		weeklyGoalSectionRightSideVBox.getChildren().clear();
		weeklyGoalSectionLeftSideVBox.getChildren().clear();
		
		loadFromWeeklyGoalDatabase();
	}
	
	
	/*
	 * replaces the 3 book indicators with the "library" so that new books can be chosen
	 * 
	 * TODO incomplete
	 */ 
	public void bookSectionMainButtonPushed() {
		if (bookSectionInBrowseMode == false) {
			bookSectionInBrowseMode = true;
			
			bookSection.setMinHeight(300);
			bookSection.setMaxHeight(300);
			
			if (bookSectionLibraryInitialized == false) { //need to add the nodes to the "bookshelves"
				
				//need a list of all the reading type notes from the treeView
		    	for (TreeItem<Note> treeItem : mR.getPipelineConsolidator().createListOfTreeItems()) {
		    		if (treeItem.getValue().getTypeOfNote().equals("Reading") && treeItem.getValue().getName().equals(bookShelfName)) {
		    			bookNotesTreeItem = treeItem;
		    		}
		    	}
		    			    	
		    	ArrayList<TreeItem<Note>> firstList = new ArrayList<TreeItem<Note>>();
		    	ArrayList<TreeItem<Note>> secondList = new ArrayList<TreeItem<Note>>();
		    	ArrayList<TreeItem<Note>> thirdList = new ArrayList<TreeItem<Note>>();
		    	
		    	Integer size = bookNotesTreeItem.getChildren().size();
		    	Double counter = 0.0;
		    			    			    	
		    	for (TreeItem<Note> treeItem : bookNotesTreeItem.getChildren()) {
		    		
		    		if (counter < size/3.0) {
		    			firstList.add(treeItem);
		    		}
		    		else if (counter < 2*(size/3.0)) {
		    			secondList.add(treeItem);
		    		}
		    		else {
		    			thirdList.add(treeItem);
		    		}
		    		++ counter;
		    	}
		    	
		    	for (TreeItem<Note> treeItem : firstList) {
		    		addBookToShelf(treeItem, firstBookshelf);
		    	}
		    	for (TreeItem<Note> treeItem : secondList) {
		    		addBookToShelf(treeItem, secondBookshelf);
		    	}
		    	for (TreeItem<Note> treeItem : thirdList) {
		    		addBookToShelf(treeItem, thirdBookshelf);
		    	}
				
				bookSectionLibraryInitialized = true;
			}
			
			bookSectionHBox.getChildren().remove(bookSectionBookshelfAnchor);
			bookSectionHBox.getChildren().add(0, bookshelfScrollPane);
		}
		else {
			bookSectionInBrowseMode = false;
			
			bookSection.setMinHeight(170);
			bookSection.setMaxHeight(170);
			
			bookSectionHBox.getChildren().remove(bookshelfScrollPane);
			bookSectionHBox.getChildren().add(0, bookSectionBookshelfAnchor);
			
			//we need to add the books in the checkoutPile to the vBoxes in the normal book section mode
			if (checkoutPile.size() == 1) {				
				addBookToDeskSpot(checkoutPile.get(0), bookshelfSpotOne);
			}
			else if (checkoutPile.size() == 2) {
				addBookToDeskSpot(checkoutPile.get(0), bookshelfSpotOne);
				addBookToDeskSpot(checkoutPile.get(1), bookshelfSpotTwo);
			}
			else if (checkoutPile.size() == 3) {
				addBookToDeskSpot(checkoutPile.get(0), bookshelfSpotOne);
				addBookToDeskSpot(checkoutPile.get(1), bookshelfSpotTwo);
				addBookToDeskSpot(checkoutPile.get(2), bookshelfSpotThree);
			}
			
			//we need to write to the database...
			ArrayList<String> titles = new ArrayList<String>();
			
			if (checkoutPile.size() >= 1) {				
				titles.add(checkoutPile.get(0).getValue().getName());
			}
			if (checkoutPile.size() >= 2) {
				titles.add(checkoutPile.get(1).getValue().getName());
			}
			if (checkoutPile.size() >= 3) {
				titles.add(checkoutPile.get(2).getValue().getName());
			}		
			
			DatabaseHandler.saveToBookDeskTable(titles);
		}    		
	}
	
	/*
	 * the method for adding a book to one of the three "shelves" in the book section expanded mode
	 */
	private void addBookToShelf(TreeItem<Note> treeItem, VBox vbox) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXMLs/DailyScrollSubFXMLs/LibraryBook.fxml"));
			AnchorPane root = fxmlLoader.load();
			
			BorderPane borderPane = (BorderPane) root.getChildren().get(0);
			VBox marginVBox = (VBox) borderPane.getLeft();
			HBox buttonsHolder = (HBox) marginVBox.getChildren().get(1);
			
			Button selectButton = (Button) buttonsHolder.getChildren().get(3);
			Button treeItemButton = (Button) buttonsHolder.getChildren().get(1);
			
			selectButton.setText(treeItem.getValue().getName());
			selectButton.setStyle("-fx-font-size: 14;");
			
			mR.getPipelineConsolidator().setButtonIcon(treeItemButton, bookNotesTreeItem);

			treeItemButton.setOnAction(new EventHandler<ActionEvent>() { 
				@Override
				public void handle(ActionEvent arg0) {
					try {
						mR.openNote(treeItem);
					} catch (IOException e) {
						e.printStackTrace();
					}						
				}});
			
			selectButton.setOnAction(new EventHandler<ActionEvent>() { //adds to the checkout ArrayList
				@Override
				public void handle(ActionEvent arg0) {
					if (checkoutPile.contains(treeItem)) { //remove it, set border back to normal
						checkoutPile.remove(treeItem);
						
						marginVBox.setStyle("-fx-border-color: rgba(47, 47, 47, 0.8)");
					}
					else { //add it, only if there are less than 3 items in the arraylist
						
						if (checkoutPile.size() < 3) {
							checkoutPile.add(treeItem);
							
							marginVBox.setStyle("-fx-border-color: rgba(80, 80, 80, 0.8)");
						}
						else { //TODO incomplete, maybe have some indicator that there are already 3 selected...
							
						}
					}
				}});
			
			root.maxWidthProperty().bind(vbox.widthProperty());
			root.minWidthProperty().bind(vbox.widthProperty());
			
			vbox.getChildren().add(root);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/*
	 * the method for adding a node to the "desk" or one of the 3 spots to be actively selected in the normal mode
	 * 
	 * this method is necessary because there is no way to make a deep copy of a javafx node
	 */
	private void addBookToDeskSpot(TreeItem<Note> treeItem, BorderPane selfBorderPane) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXMLs/DailyScrollSubFXMLs/LibraryBook.fxml"));
			AnchorPane root = fxmlLoader.load();
			
			BorderPane borderPane = (BorderPane) root.getChildren().get(0);
			VBox marginVBox = (VBox) borderPane.getLeft();
			HBox buttonsHolder = (HBox) marginVBox.getChildren().get(1);
			
			Button selectButton = (Button) buttonsHolder.getChildren().get(3);
			Button treeItemButton = (Button) buttonsHolder.getChildren().get(1);
			
			selectButton.setText(treeItem.getValue().getName());
			selectButton.setStyle("-fx-font-size: 14;");
			
			mR.getPipelineConsolidator().setButtonIcon(treeItemButton, bookNotesTreeItem);

			treeItemButton.setOnAction(new EventHandler<ActionEvent>() { 
				@Override
				public void handle(ActionEvent arg0) {
					try {
						mR.openNote(treeItem);
					} catch (IOException e) {
						e.printStackTrace();
					}						
				}});
			
			selectButton.setOnAction(new EventHandler<ActionEvent>() { //adds to the checkout ArrayList
				@Override
				public void handle(ActionEvent arg0) {
					
					if (selectedBook == null) {
						selectedBook = buttonsHolder;
						
						selfBorderPane.setStyle("-fx-border-color: rgba(120, 120, 120, 0.5)");
					}
					
					else if (selectedBook == buttonsHolder) {
						selectedBook = null;
						
						selfBorderPane.setStyle("-fx-border-color: rgba(47, 47, 47, 0.8)");
					}
					
					else { //set all of the borderpanes styles to normal, then highlight this borderPane
						selectedBook = buttonsHolder;

						bookshelfSpotOne.setStyle("-fx-border-color: rgba(47, 47, 47, 0.8)");
						bookshelfSpotTwo.setStyle("-fx-border-color: rgba(47, 47, 47, 0.8)");
						bookshelfSpotThree.setStyle("-fx-border-color: rgba(47, 47, 47, 0.8)");

						selfBorderPane.setStyle("-fx-border-color: rgba(120, 120, 120, 0.5)");
					}
				}});
			
			BorderPane treeItemButtonHolder = new BorderPane();
			treeItemButtonHolder.setCenter(treeItemButton);
			
			treeItemButtonHolder.setMinWidth(32.5);
			treeItemButtonHolder.setMaxWidth(32.5);
			
			selfBorderPane.setLeft(treeItemButtonHolder);
			selfBorderPane.setCenter(selectButton);
						
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * 1/27/2023-6:43PM --- TODO incomplete
	 * 
	 * needs to remove all elements besides leftTextArea, rightTextArea, and the DoneSection which will be moved above the hbox
	 */
	public void configureForTimeCapsule() {
		inTimeCapsuleMode = true;
		
		AnchorPane doneSectionNewHolder = new AnchorPane();
		doneSectionNewHolder.getChildren().add(dailyScrollDoneSectionVBox);
		
		doneSectionNewHolder.minWidthProperty().bind(dailyScrollMasterVBox.widthProperty());
		doneSectionNewHolder.maxWidthProperty().bind(dailyScrollMasterVBox.widthProperty());
		
	    AnchorPane.setLeftAnchor(dailyScrollDoneSectionVBox, 10.0);
	    AnchorPane.setRightAnchor(dailyScrollDoneSectionVBox, 10.0);
	    AnchorPane.setTopAnchor(dailyScrollDoneSectionVBox, 10.0);
		
		dailyScrollMasterVBox.getChildren().add(1, doneSectionNewHolder);
		
		parentOfLeftScrollPane.maxWidthProperty().bind(parentOfScrollPanes.widthProperty().divide(2));
		parentOfLeftScrollPane.minWidthProperty().bind(parentOfScrollPanes.widthProperty().divide(2));

		parentOfRightScrollPane.maxWidthProperty().bind(parentOfScrollPanes.widthProperty().divide(2));
		parentOfRightScrollPane.minWidthProperty().bind(parentOfScrollPanes.widthProperty().divide(2));
		
		parentOfLeftScrollPane.getChildren().clear();
		parentOfLeftScrollPane.getChildren().add(leftTextSection);
	    AnchorPane.setLeftAnchor(leftTextSection, 10.0);
	    AnchorPane.setRightAnchor(leftTextSection, 5.0);
	    AnchorPane.setBottomAnchor(leftTextSection, 60.0);
	    AnchorPane.setTopAnchor(leftTextSection, 10.0);
		
		parentOfRightScrollPane.getChildren().clear();
		parentOfRightScrollPane.getChildren().add(rightTextSection);
	    AnchorPane.setLeftAnchor(rightTextSection, 5.0);
	    AnchorPane.setRightAnchor(rightTextSection, 10.0);
	    AnchorPane.setBottomAnchor(rightTextSection, 60.0);
	    AnchorPane.setTopAnchor(rightTextSection, 10.0);		
	}
	
	/*
	 * https://stackoverflow.com/questions/237159/whats-the-best-way-to-check-if-a-string-represents-an-integer-in-java?page=1&tab=scoredesc#tab-top
	 * 
	 * leaving as public because might be useful in other classes?
	 */
	public static boolean isInteger(String str) {
	    if (str == null) {
	        return false;
	    }
	    int length = str.length();
	    if (length == 0) {
	        return true;
	    }
	    int i = 0;
	    if (str.charAt(0) == '-') {
	        if (length == 1) {
	            return false;
	        }
	        i = 1;
	    }
	    for (; i < length; i++) {
	        char c = str.charAt(i);
	        if (c < '0' || c > '9') {
	            return false;
	        }
	    }
	    return true;
	}
	
	public VBox getDailyScrollDoneSectionVBox() {
		return dailyScrollDoneSectionVBox;
	}
	
	public TextArea getRightTextSectionTextArea() {
		return rightTextSectionTextArea;
	}
	
	public TextArea getLeftTextSectionTextArea() {
		return leftTextSectionTextArea;
	}
}
