package fxmlcontrollers.notetypes;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import fxmlcontrollers.notetypes.subtypes.DailyScrollToDoSectionNodeController;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DailyScrollController implements Initializable {
	
	private static Integer toDoVBoxDefaultHeight = 200;
	private static Integer heightOfReflectionSection = 70;
	private static Integer heightOfClosedToDoSectionNode = 50;
	private Boolean toDoSectionInExpandedMode = false;
	
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
	private AnchorPane shortTermGoalSection;
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
	private Button toDoSectionTempButton;
	@FXML
	private Button shortTermGoalSectionButton;
	@FXML
	private Button longTermGoalSectionButton;
		
	
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
		
		/*
		 * short term goal section
		 */
		shortTermGoalSection.minWidthProperty().bind(parentOfLeftScrollPane.widthProperty().subtract(40));
		shortTermGoalSection.maxWidthProperty().bind(parentOfLeftScrollPane.widthProperty().subtract(40));

				
		
		
	}
	
	/*
	 * this button disables the right side, then switches the two buttons with one larger button
	 */
	public void dailyScrollTopLeftButtonPushed() {
		
		parentOfScrollPanes.getChildren().remove(1);
		
		topButtonHolder.getChildren().clear();
		topButtonHolder.getChildren().add(whenNeededButton);
		
		
		
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
			
			handleDeleteButtonListener(deleteButton, loadedNode);
			
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
	
	private void handleDeleteButtonListener(Button button, AnchorPane anchor) {
        button.setOnAction(new EventHandler<ActionEvent>() { 
		@Override
		public void handle(ActionEvent event) {
			toDoVBox.getChildren().remove(anchor);
			
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
	
}
