package fxmlcontrollers.notetypes;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Set;

import handlers.DatabaseHandler;
import handlers.NoteChooserHandler;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DailyScrollController implements Initializable {
	
	private static Integer toDoVBoxDefaultHeight = 200;
	private static Integer heightOfReflectionSection = 70;
	private static Integer heightOfClosedToDoSectionNode = 50;
	private Boolean toDoSectionInExpandedMode = false;
	//starts in expanded mode and is immediately closed on creation in order to efficiently reference elements
	private Boolean weeklyGoalSectionInExpandedMode = true;
	
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
	private Button whenNeededButton; //this is the button in same hbox as 2 above, removed initially, added when needed
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
	
	
		
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
				
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
		
		toDoVBox.setMinHeight(200);
		toDoVBox.setMaxHeight(200);
		dailyScrollToDoSection.setMinHeight(240);
		dailyScrollToDoSection.setMaxHeight(240);
		
		topButtonHolder.getChildren().remove(whenNeededButton);

		/*
		 * long term goal section
		 */
		longTermGoalSection.minWidthProperty().bind(parentOfLeftScrollPane.widthProperty().subtract(40));
		longTermGoalSection.maxWidthProperty().bind(parentOfLeftScrollPane.widthProperty().subtract(40));
		loadFromLongTermGoalDatabase();
		
		/*
		 * weekly goal section
		 */
		weeklyGoalSection.minWidthProperty().bind(parentOfLeftScrollPane.widthProperty().subtract(40));
		weeklyGoalSection.maxWidthProperty().bind(parentOfLeftScrollPane.widthProperty().subtract(40));
		
		//TODO the 77.5 is an arbitrary number and may break if reformatting occurs
		weeklyGoalSectionLeftSideSpacer.minWidthProperty().bind(parentOfLeftScrollPane.widthProperty().subtract(77.5).divide(2));
		weeklyGoalSectionLeftSideSpacer.maxWidthProperty().bind(parentOfLeftScrollPane.widthProperty().subtract(77.5).divide(2));

		weeklyGoalSectionRightSideSpacer.minWidthProperty().bind(parentOfLeftScrollPane.widthProperty().subtract(77.5).divide(2));
		weeklyGoalSectionRightSideSpacer.maxWidthProperty().bind(parentOfLeftScrollPane.widthProperty().subtract(77.5).divide(2));
		
    	changeWeeklyGoalSectionMode();
    	
		/*
		 * book section
		 */
		bookSection.minWidthProperty().bind(parentOfLeftScrollPane.widthProperty().subtract(40));
		bookSection.maxWidthProperty().bind(parentOfLeftScrollPane.widthProperty().subtract(40));
	}
	
	/*
	 * this button disables the right side, then switches the two buttons with one larger button
	 */
	public void dailyScrollTopLeftButtonPushed() {
		
		parentOfScrollPanes.getChildren().remove(1);
		
		topButtonHolder.getChildren().clear();
		topButtonHolder.getChildren().add(whenNeededButton);
		
		changeWeeklyGoalSectionMode();
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
		
		changeToDoSectionMode();
	}
	
	/*
	 * resets the topButtonHolder and left and right sides back to original state
	 */
	public void whenNeededButtonPushed() {
		
		parentOfScrollPanes.getChildren().clear();
		parentOfScrollPanes.getChildren().add(parentOfLeftScrollPane);
		parentOfScrollPanes.getChildren().add(parentOfRightScrollPane);
		
		topButtonHolder.getChildren().clear();
		
		topButtonHolder.getChildren().add(dailyScrollTopLeftButton);
		topButtonHolder.getChildren().add(topButtonMarginMaker);
		topButtonHolder.getChildren().add(dailyScrollTopRightButton);
		
		if (toDoSectionInExpandedMode == true) {
			changeToDoSectionMode();
		}
		if (weeklyGoalSectionInExpandedMode == true) {
			changeWeeklyGoalSectionMode();
		}
	}
	
	
	/*
	 * the anchor for the loaded node will be a class which overrides anchorpane
	 * this way, it can hold node specific attributes without making a reference to a controller
	 */
	public void toDoSectionButtonPushed() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXMLs/DailyScrollSubFXMLs/ToDoSectionNodeExperiment.fxml"));
		try {
			AnchorPane loadedNode = fxmlLoader.load();
						
			toDoVBox.getChildren().add(loadedNode);
			
			loadedNode.maxWidthProperty().bind(dailyScrollToDoSection.widthProperty().subtract(15));
			loadedNode.minWidthProperty().bind(dailyScrollToDoSection.widthProperty().subtract(15));
			
			
			//TODO these are static references and can break if the tree structure of the node changes
			VBox encapsulatingVBox = (VBox) loadedNode.getChildren().get(0);
			AnchorPane originalAnchor = (AnchorPane) encapsulatingVBox.getChildren().get(0);
			HBox rightSideHBox = (HBox) originalAnchor.getChildren().get(1);
			Button deleteButton = (Button) rightSideHBox.getChildren().get(2);
			
			handleDeleteButtonListener(deleteButton, loadedNode, toDoVBox);
			
			HBox leftSideHBox = (HBox) originalAnchor.getChildren().get(0);

			Button toDoSectionNodeReflectButton = (Button) leftSideHBox.getChildren().get(0);
			Button toDoSectionNodeCheckButton = (Button) leftSideHBox.getChildren().get(2);
			Button toDoSectionNodeLockButton = (Button) leftSideHBox.getChildren().get(4);
			
			handleLockButtonListener(toDoSectionNodeLockButton);
			
			AnchorPane secondAnchor = (AnchorPane) encapsulatingVBox.getChildren().get(1);
			AnchorPane nestedAnchor = (AnchorPane) secondAnchor.getChildren().get(0);
			TextArea reflectionTextArea = (TextArea) nestedAnchor.getChildren().get(0);
			
			reflectionTextArea.setVisible(false);
			
			//only one if statement here because the above code is default implementation for if nodes in closed form
			if (toDoSectionInExpandedMode == true) {
				reflectionTextArea.setVisible(true);
				loadedNode.setPrefHeight(heightOfClosedToDoSectionNode + heightOfReflectionSection);
			}
			
			adjustToDoSectionVBoxHeight();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * the default height will be 200, if there are more than 4 children height 50, then add height
	 */
	private void adjustToDoSectionVBoxHeight() {
		
		if (toDoSectionInExpandedMode == false) {
			//sets the height of the vbox back to the default to avoid rewriting unnecessary code, this operation should cause no visible latency
			toDoVBox.setMaxHeight(toDoVBoxDefaultHeight);
			toDoVBox.setMinHeight(toDoVBoxDefaultHeight);
			
			if (toDoVBox.getChildren().size() >= 5) {
				
				Integer numOfChildren = toDoVBox.getChildren().size();
				Integer counter = 4;
				
				//if 4 children or less, already the expected height, for every child after 4, add one unit
				
				while (counter < numOfChildren) {
					toDoVBox.setMaxHeight(toDoVBox.getMaxHeight() + heightOfClosedToDoSectionNode);
					toDoVBox.setMinHeight(toDoVBox.getMinHeight() + heightOfClosedToDoSectionNode);

					dailyScrollToDoSection.setMinHeight(toDoVBox.getMinHeight() + 40);
					dailyScrollToDoSection.setMaxHeight(toDoVBox.getMinHeight() + 40);
					
					counter += 1;
				}
			}
			else {
				dailyScrollToDoSection.setMinHeight(toDoVBox.getMinHeight() + 40);
				dailyScrollToDoSection.setMaxHeight(toDoVBox.getMinHeight() + 40);
			}
		}
		else {
			toDoVBox.setMaxHeight(toDoVBoxDefaultHeight + 2 * heightOfReflectionSection);
			toDoVBox.setMinHeight(toDoVBoxDefaultHeight + 2 * heightOfReflectionSection);
			
			if (toDoVBox.getChildren().size() >= 3) {
				
				Integer numOfChildren = toDoVBox.getChildren().size();
				Integer counter = 2;
				
				//if 4 children or less, already the expected height, for every child after 4, add one unit
				
				while (counter < numOfChildren) {
					toDoVBox.setMaxHeight(toDoVBox.getMaxHeight() + heightOfClosedToDoSectionNode + heightOfReflectionSection);
					toDoVBox.setMinHeight(toDoVBox.getMinHeight() + heightOfClosedToDoSectionNode + heightOfReflectionSection);

					dailyScrollToDoSection.setMinHeight(toDoVBox.getMinHeight() + 40);
					dailyScrollToDoSection.setMaxHeight(toDoVBox.getMinHeight() + 40);
					
					counter += 1;
				}
			}
			else {
				dailyScrollToDoSection.setMinHeight(toDoVBox.getMinHeight() + 40);
				dailyScrollToDoSection.setMaxHeight(toDoVBox.getMinHeight() + 40);
			}
		}
	}
	
	//TODO incomplete
	public void changeWeeklyGoalSectionMode() {
		
		if (weeklyGoalSectionInExpandedMode == false) {
			weeklyGoalSectionInExpandedMode = true;
			
			weeklyGoalSectionVBox.getChildren().add(weeklyGoalResetButtonAnchor);
			weeklyGoalLineUpButtonHolder.getChildren().add(weeklyGoalLineUpButton);
			
			
		}
		else { //removes the reset button, button to add more nodes on left side
			weeklyGoalSectionInExpandedMode = false;
			
			weeklyGoalSectionVBox.getChildren().remove(weeklyGoalResetButtonAnchor); //potentially destructive static reference
			weeklyGoalLineUpButtonHolder.getChildren().remove(weeklyGoalLineUpButton);
			
		}
		
		
	}
	
	public void changeToDoSectionMode() {
		if (toDoSectionInExpandedMode == false) {
			toDoSectionInExpandedMode = true;
						
			for (Node node : toDoVBox.getChildren()) {
				AnchorPane root = (AnchorPane) node;
				
				root.setPrefHeight(heightOfClosedToDoSectionNode + heightOfReflectionSection);
				
				//TODO these are vulnerable static references to indexes of tree structure
				VBox encapsulatingVBox = (VBox) root.getChildren().get(0);
				AnchorPane secondAnchor = (AnchorPane) encapsulatingVBox.getChildren().get(1);
				AnchorPane nestedAnchor = (AnchorPane) secondAnchor.getChildren().get(0);
				TextArea reflectionTextArea = (TextArea) nestedAnchor.getChildren().get(0);
				
				reflectionTextArea.setVisible(true);
			}
			adjustToDoSectionVBoxHeight();
		}
		else {
			toDoSectionInExpandedMode = false;
			
			for (Node node : toDoVBox.getChildren()) {
				AnchorPane root = (AnchorPane) node;
				
				root.setPrefHeight(heightOfClosedToDoSectionNode);
				
				//TODO these are vulnerable static references to indexes of tree structure
				VBox encapsulatingVBox = (VBox) root.getChildren().get(0);
				AnchorPane secondAnchor = (AnchorPane) encapsulatingVBox.getChildren().get(1);
				AnchorPane nestedAnchor = (AnchorPane) secondAnchor.getChildren().get(0);
				TextArea reflectionTextArea = (TextArea) nestedAnchor.getChildren().get(0);
				
				reflectionTextArea.setVisible(false);
			}
			adjustToDoSectionVBoxHeight();
		}
	}
	
	private void handleDeleteButtonListener(Button button, AnchorPane anchor, VBox vbox) {
        button.setOnAction(new EventHandler<ActionEvent>() { 
		@Override
		public void handle(ActionEvent event) {
			vbox.getChildren().remove(anchor);
			
			adjustToDoSectionVBoxHeight();
		}});
	}
	
	/*
	 * when the lock button is pushed, change the color from green to gray, enable the pushing of the 
	 * check button
	 * 
	 * (later will add some functionality here, maybe turn the textfield into a label for cosmetic purposes)
	 */
	private void handleLockButtonListener(Button button) {
        button.setOnAction(new EventHandler<ActionEvent>() { 
		@Override
		public void handle(ActionEvent event) {
			
			System.out.println("lock button pushed");
			
			button.setStyle("-fx-background: rgba(65, 65, 65, 0.9);"
					+ " -fx-hovered-background: rgba(65, 65, 65, 0.95);"
					+ " -fx-pressed-background: rgba(65, 65, 65, 1);");
			
			
			button.getStylesheets().add(getClass().getResource("src/application/application.css").toExternalForm());
			button.getStyleClass().add("test");
		
		}});
	}
	
	
	public void longTermGoalSectionButtonPushed() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXMLs/DailyScrollSubFXMLs/LongTermGoalSectionNode.fxml"));
		try {
			AnchorPane loadedNode = fxmlLoader.load();
									
			longTermGoalSectionVBox.getChildren().add(loadedNode);
			
			Button deleteButton = (Button) loadedNode.getChildren().get(3);
			handleDeleteButtonListener(deleteButton, loadedNode, longTermGoalSectionVBox);
			
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
	    					&& (String.valueOf(contents.charAt(counter)).toLowerCase() != " ")) {
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
							
							AnchorPane parentOfDescriptionLabel = (AnchorPane) lockedNode.getChildren().get(1);
							Label longTermGoalSectionDescriptionLabel = (Label) parentOfDescriptionLabel.getChildren().get(0);
							
							AnchorPane parentOfInputLabel = (AnchorPane) lockedNode.getChildren().get(2);
							Label longTermGoalSectionDaysLabel = (Label) parentOfInputLabel.getChildren().get(0);
							
							longTermGoalSectionDescriptionLabel.setText(longTermGoalSectionDescriptionTextField.getText());
							
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
							
							Button actionButton = (Button) lockedNode.getChildren().get(0);
							
							handleLongTermGoalCompleteButton(actionButton, longTermGoalSectionDescriptionTextField.getText(), lockedNode);
							
							//need to remove the loadedNode from the vBox, replace it with lockedNode
							Integer indexCounter = 0;
							for (Node node : longTermGoalSectionVBox.getChildren()) {
								if ((AnchorPane) node == loadedNode) {
									longTermGoalSectionVBox.getChildren().remove(node);
									break; //breaks out of for loop, ends counter of index counter on whatever index
								}
								indexCounter += 1;
							}
							
							longTermGoalSectionVBox.getChildren().add(indexCounter, lockedNode);

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
	 * TODO the long term goals should be sorted by time until date somehow
	 */
	private void loadFromLongTermGoalDatabase() {
		
		ArrayList<ArrayList<String>> listOfLists = DatabaseHandler.loadFromLongTermGoalTable();
		
		/*
		 * Description, Day, Month, Year
		 */
		
		for (ArrayList<String> entry : listOfLists) {
						
			try {
				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXMLs/DailyScrollSubFXMLs/LongTermGoalSectionNodeLocked.fxml"));
				AnchorPane lockedNode = fxmlLoader.load();
				
				AnchorPane parentOfDescriptionLabel = (AnchorPane) lockedNode.getChildren().get(1);
				Label longTermGoalSectionDescriptionLabel = (Label) parentOfDescriptionLabel.getChildren().get(0);
				
				AnchorPane parentOfInputLabel = (AnchorPane) lockedNode.getChildren().get(2);
				Label longTermGoalSectionDaysLabel = (Label) parentOfInputLabel.getChildren().get(0);
				
				longTermGoalSectionDescriptionLabel.setText(entry.get(0));
				
				LocalDate date = LocalDate.now();
				LocalDate entryDate = LocalDate.of(Integer.valueOf(entry.get(3)), Integer.valueOf(entry.get(2)), Integer.valueOf(entry.get(1)));
				
				Integer daysBetween = (int) ChronoUnit.DAYS.between(date, entryDate);
				longTermGoalSectionDaysLabel.setText(daysBetween.toString() + " days");
								
				Button actionButton = (Button) lockedNode.getChildren().get(0);
				
				handleLongTermGoalCompleteButton(actionButton, entry.get(0), lockedNode);
				
				longTermGoalSectionVBox.getChildren().add(lockedNode);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	
		
	}
	
	//TODO finish this when I finalize this with database
	private void handleLongTermGoalCompleteButton(Button button, String description, AnchorPane node) {
		button.setOnAction(new EventHandler<ActionEvent>() { 
    		@Override
    		public void handle(ActionEvent event) {
    			DatabaseHandler.deleteFromLongTermGoalTable(description);
    			
				Integer indexCounter = 0;
				for (Node node : longTermGoalSectionVBox.getChildren()) {
					if ((AnchorPane) node == node) {
						longTermGoalSectionVBox.getChildren().remove(node);
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
			
			AnchorPane parentOfWeeklyGoalDescriptionTextField = (AnchorPane) creationNode.getChildren().get(1);
			TextField weeklyGoalDescriptionTextField = (TextField) parentOfWeeklyGoalDescriptionTextField.getChildren().get(0);
			
			AnchorPane parentOfWeeklyGoalQuantityCombobox = (AnchorPane) creationNode.getChildren().get(2);
			ComboBox weeklyGoalQuantityCombobox = (ComboBox) parentOfWeeklyGoalQuantityCombobox.getChildren().get(0);	
			
			weeklyGoalQuantityCombobox.getItems().add(1);
			weeklyGoalQuantityCombobox.getItems().add(2);
			weeklyGoalQuantityCombobox.getItems().add(3);
			weeklyGoalQuantityCombobox.getItems().add(4);
			weeklyGoalQuantityCombobox.getItems().add(5);
			weeklyGoalQuantityCombobox.getItems().add(6);
			weeklyGoalQuantityCombobox.getItems().add(7);
			
			weeklyGoalQuantityCombobox.getSelectionModel().select(0);
			
			Button weeklyGoalSectionCreationDeleteButton = (Button) creationNode.getChildren().get(3);
			
			handleWeeklyGoalSectionCreationDeleteButton(weeklyGoalSectionCreationDeleteButton, creationNode);
			handleWeeklyGoalCreateButton(weeklyGoalCreateButton, weeklyGoalDescriptionTextField, weeklyGoalQuantityCombobox);
			
			creationNode.minWidthProperty().bind(weeklyGoalLeftSectionBorderAnchor.widthProperty());
			creationNode.maxWidthProperty().bind(weeklyGoalLeftSectionBorderAnchor.widthProperty());
			
			weeklyGoalSectionLeftSideVBox.getChildren().add(creationNode);
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	//TODO incomplete
	private void handleWeeklyGoalCreateButton(Button button, TextField textField, ComboBox comboBox) {
		button.setOnAction(new EventHandler<ActionEvent>() { 
    		@Override
    		public void handle(ActionEvent event) {
    			//need to check if there is text in the description text field
    			//if the description text is good and number is good, then convert to "LongTermGoalSectionNodeLocked.fxml"
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
    				
    				try {
        				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXMLs/DailyScrollSubFXMLs/WeeklyGoalSectionCreatedStage.fxml"));
						AnchorPane createdNode = fxmlLoader.load();
						
						BorderPane borderPane = (BorderPane) createdNode.getChildren().get(0);
						Label descriptionLabel = (Label) borderPane.getCenter();
						
						AnchorPane hboxHolder = (AnchorPane) createdNode.getChildren().get(1);
						HBox cardHolderHBox = (HBox) hboxHolder.getChildren().get(0);
						
						descriptionLabel.setText(textField.getText());
						
						createdNode.minWidthProperty().bind(weeklyGoalLeftSectionBorderAnchor.widthProperty());
						createdNode.maxWidthProperty().bind(weeklyGoalLeftSectionBorderAnchor.widthProperty());
						
						weeklyGoalSectionLeftSideVBox.getChildren().add(createdNode);
        				Integer count = (Integer) comboBox.getSelectionModel().getSelectedItem();
						
						for (int i = 0; i < count; i++) {
	        				FXMLLoader subLoader = new FXMLLoader(getClass().getResource("/FXMLs/DailyScrollSubFXMLs/WeeklyGoalLineupNode.fxml"));
	        				AnchorPane lineupNode = subLoader.load();
	        				
	        				lineupNode.maxWidthProperty().bind(cardHolderHBox.widthProperty().divide(count));
	        				lineupNode.minWidthProperty().bind(cardHolderHBox.widthProperty().divide(count));
	        				
	        				lineupNode.setOnMouseClicked(new EventHandler<MouseEvent>() { 
					    		@Override
					    		public void handle(MouseEvent event) {
					    			
					    			System.out.println("test");
					    			
					    			}
					    		});
	        				
	        				cardHolderHBox.getChildren().add(lineupNode);
						}
						
						
					} catch (IOException e) {
						e.printStackTrace();
					}
    				
    				
    			}
    		}
		});
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
}
