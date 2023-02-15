package fxmlcontrollers.notetypes;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ReadingTypeNoteController implements Initializable {
	
	public class AnchorForReadingType extends AnchorPane {
		
		String type;
		
		public AnchorForReadingType(String type) {
			this.type = type;		
		}

		public String getType() {
			return type;
		}
		
		public void setType(String type) {
			this.type = type;
		}
	}

	
	@FXML
	private Button addQuoteButton;
	@FXML
	private Button addAnalysisButton;
	@FXML
	private Button addBothButton;
	@FXML
	private AnchorPane marginKeeper;
	@FXML
	private VBox collectorVBox;
	@FXML
	private AnchorPane parentOfScrollPane;
	@FXML
	private Button addChapterButton;
	@FXML
	private Button addSectionButton;
	
	//subtracted from the bound width in order to prevent bottom scrollbar from being necessary due to
	//the right-side scrollbar eating up space
	private static Integer scrollbarBuffer = 10;
	
	private static Integer subtractFromCollectorVBoxChild = 0;
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
			
		//has to bind the marginKeeper's heights and widths because this cannot be done easily in SceneBuilder
		marginKeeper.minWidthProperty().bind(parentOfScrollPane.widthProperty().subtract(25));
		marginKeeper.maxWidthProperty().bind(parentOfScrollPane.widthProperty().subtract(25));
		
		//the margin keeper height is bounded to its child, or the collector vbox, this makes it so that the scrollpane is activated as the anchorpane becomes larger than the area
		marginKeeper.minHeightProperty().bind(collectorVBox.heightProperty().add(10));
		marginKeeper.maxHeightProperty().bind(collectorVBox.heightProperty().add(10));
		
		
        //When the Add Analysis Button is Pushed
        addAnalysisButton.setOnAction(new EventHandler<ActionEvent>() { 
		@Override
		public void handle(ActionEvent event) {
			pushAnalysisButton();
			}
        });
        
        
        //When the add Quote Button is pressed
        
        ////the quote and analysis fxmls are actually the exact same but quote just needs a different background color
        addQuoteButton.setOnAction(new EventHandler<ActionEvent>() { 
		@Override
		public void handle(ActionEvent event) {
			pushQuoteButton();
			}
        });
        
        
        ////the quote and analysis fxmls are actually the exact same but quote just needs a different background color
        addBothButton.setOnAction(new EventHandler<ActionEvent>() { 
		@Override
		public void handle(ActionEvent event) {
			pushBothButton();
			}
        });
        
        addChapterButton.setOnAction(new EventHandler<ActionEvent>() { 
		@Override
		public void handle(ActionEvent event) {
			pushChapterButton();
			}
        });
        
        addSectionButton.setOnAction(new EventHandler<ActionEvent>() { 
    		@Override
    		public void handle(ActionEvent event) {
    			pushSectionButton();
    		}
        });
	}
	
	private void handleCloseButtonListener(Button button, AnchorPane anchor) {
        button.setOnAction(new EventHandler<ActionEvent>() { 
		@Override
		public void handle(ActionEvent event) {
			collectorVBox.getChildren().remove(anchor);
		}});
	}
	
	private void handleUpButtonListener(Button button, AnchorPane anchor) {
        button.setOnAction(new EventHandler<ActionEvent>() { 
		@Override
		public void handle(ActionEvent event) {
			//first, find the index of the node which is being pushed up
			Integer counter = 0;
			for (Node node: collectorVBox.getChildren()) {
				if (((AnchorPane) node).equals(anchor)) {
					break;
				}
				counter += 1;
			}
			
			if (counter != 0) { //if it equals 0, can't go up
				collectorVBox.getChildren().remove(anchor);
				
				collectorVBox.getChildren().add(counter-1, anchor);
			}
		}});
	}
	
	private void handleDownButtonListener(Button button, AnchorPane anchor) {
        button.setOnAction(new EventHandler<ActionEvent>() { 
		@Override
		public void handle(ActionEvent event) {
			//first, find the index of the node which is being pushed up
			Integer counter = 0;
			for (Node node: collectorVBox.getChildren()) {
				if (((AnchorPane) node).equals(anchor)) {
					break;
				}
				counter += 1;
			}
			
			if (counter != collectorVBox.getChildren().size()-1) { //if it is the last element, can't go down
				collectorVBox.getChildren().remove(anchor);
				
				collectorVBox.getChildren().add(counter+1, anchor);
			}
		}});
	}

	
	public TextArea pushAnalysisButton() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXMLs/ReadingTypeSubFXMLs/Analysis.fxml"));

		HBox mainHBox;
		try {
			//this class type is essentially a normal anchorpane but it holds a type attribute
			AnchorForReadingType anchor = new AnchorForReadingType("Analysis");
			
			mainHBox = fxmlLoader.load();
			
			VBox mainVBox = (VBox) mainHBox.getChildren().get(1);
			TextArea textArea = (TextArea) mainVBox.getChildren().get(1);
			
			//adds the HBox to the anchor and binds the hbox to the dimensions
			anchor.getChildren().add(mainHBox);
			
			mainHBox.minWidthProperty().bind(anchor.widthProperty().subtract(subtractFromCollectorVBoxChild));
			mainHBox.maxWidthProperty().bind(anchor.widthProperty().subtract(subtractFromCollectorVBoxChild));
			
			mainHBox.minHeightProperty().bind(anchor.heightProperty());
			mainHBox.maxHeightProperty().bind(anchor.heightProperty());

			
			//adds the anchor to the collection and once again binds the dimensions
			collectorVBox.getChildren().add(anchor);

			anchor.minWidthProperty().bind(collectorVBox.widthProperty().subtract(scrollbarBuffer));
			anchor.maxWidthProperty().bind(collectorVBox.widthProperty().subtract(scrollbarBuffer));

			anchor.setMaxHeight(150);
			anchor.setMinHeight(150);
			
			//handles the close button
			AnchorPane buttonAnchor = (AnchorPane) mainHBox.getChildren().get(2);
			
			VBox directionVBox = (VBox) buttonAnchor.getChildren().get(0);
			Button upButton = (Button) directionVBox.getChildren().get(0);
			Button downButton = (Button) directionVBox.getChildren().get(2);
			
			handleUpButtonListener(upButton, anchor);
			handleDownButtonListener(downButton, anchor);
			
			Button removeNodeButton = (Button) buttonAnchor.getChildren().get(1);
			
			handleCloseButtonListener(removeNodeButton, anchor);
			
			return textArea;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public TextArea pushQuoteButton() {
		
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXMLs/ReadingTypeSubFXMLs/Analysis.fxml"));

		HBox mainHBox;
		try {
			//this class type is essentially a normal anchorpane but it holds a type attribute
			AnchorForReadingType anchor = new AnchorForReadingType("Quote");
			
			mainHBox = fxmlLoader.load();
			
			VBox mainVBox = (VBox) mainHBox.getChildren().get(1);
			TextArea textArea = (TextArea) mainVBox.getChildren().get(1);
			
			
			//adds the HBox to the anchor and binds the hbox to the dimensions
			anchor.getChildren().add(mainHBox);
			
			mainHBox.minWidthProperty().bind(anchor.widthProperty().subtract(subtractFromCollectorVBoxChild));
			mainHBox.maxWidthProperty().bind(anchor.widthProperty().subtract(subtractFromCollectorVBoxChild));
			
			mainHBox.minHeightProperty().bind(anchor.heightProperty());
			mainHBox.maxHeightProperty().bind(anchor.heightProperty());

			
			//adds the anchor to the collection and once again binds the dimensions
			collectorVBox.getChildren().add(anchor);

			anchor.minWidthProperty().bind(collectorVBox.widthProperty().subtract(scrollbarBuffer));
			anchor.maxWidthProperty().bind(collectorVBox.widthProperty().subtract(scrollbarBuffer));

			anchor.setMaxHeight(150);
			anchor.setMinHeight(150);
			
			//color has to be set here instead of CSS because analysis and quote boxes do not have unique ids as they share the same fxml
			textArea.setStyle("-fx-background-color: rgba(40, 40, 40, 0.95);");
			
			//handles the close button
			AnchorPane buttonAnchor = (AnchorPane) mainHBox.getChildren().get(2);
			
			VBox directionVBox = (VBox) buttonAnchor.getChildren().get(0);
			Button upButton = (Button) directionVBox.getChildren().get(0);
			Button downButton = (Button) directionVBox.getChildren().get(2);
			
			handleUpButtonListener(upButton, anchor);
			handleDownButtonListener(downButton, anchor);
			
			Button removeNodeButton = (Button) buttonAnchor.getChildren().get(1);
			
			handleCloseButtonListener(removeNodeButton, anchor);
			
			
			return textArea;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public ArrayList<TextArea> pushBothButton() {
		
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXMLs/ReadingTypeSubFXMLs/Both.fxml"));

		HBox mainHBox;
		try {
			//this class type is essentially a normal anchorpane but it holds a type attribute
			AnchorForReadingType anchor = new AnchorForReadingType("Both");
			
			mainHBox = fxmlLoader.load();
			
			VBox leftVBox = (VBox) mainHBox.getChildren().get(1);
			TextArea quoteSide = (TextArea) leftVBox.getChildren().get(1);
			
			VBox rightVBox = (VBox) mainHBox.getChildren().get(3);
			TextArea analysisSide = (TextArea) rightVBox.getChildren().get(1);
			
			
			//adds the HBox to the anchor and binds the hbox to the dimensions
			anchor.getChildren().add(mainHBox);
			
			mainHBox.minWidthProperty().bind(anchor.widthProperty().subtract(subtractFromCollectorVBoxChild));
			mainHBox.maxWidthProperty().bind(anchor.widthProperty().subtract(subtractFromCollectorVBoxChild));
			
			mainHBox.minHeightProperty().bind(anchor.heightProperty());
			mainHBox.maxHeightProperty().bind(anchor.heightProperty());

			
			//adds the anchor to the collection and once again binds the dimensions
			collectorVBox.getChildren().add(anchor);

			anchor.minWidthProperty().bind(collectorVBox.widthProperty().subtract(scrollbarBuffer));
			anchor.maxWidthProperty().bind(collectorVBox.widthProperty().subtract(scrollbarBuffer));

			//the both box will be slightly larger to compensate for being half the width 
			anchor.setMaxHeight(200);
			anchor.setMinHeight(200);
			
			//color has to be set here instead of CSS because analysis and quote boxes do not have unique ids as they share the same fxml
			quoteSide.setStyle("-fx-background-color: rgba(40, 40, 40, 0.95);");

			AnchorPane buttonAnchor = (AnchorPane) mainHBox.getChildren().get(4);
			
			VBox directionVBox = (VBox) buttonAnchor.getChildren().get(0);
			Button upButton = (Button) directionVBox.getChildren().get(0);
			Button downButton = (Button) directionVBox.getChildren().get(2);
			
			handleUpButtonListener(upButton, anchor);
			handleDownButtonListener(downButton, anchor);
			
			Button removeNodeButton = (Button) buttonAnchor.getChildren().get(1);
			
			handleCloseButtonListener(removeNodeButton, anchor);
			
			
			ArrayList<TextArea> toReturn = new ArrayList<TextArea>();
			toReturn.add(quoteSide);
			toReturn.add(analysisSide);
			
			return toReturn;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void pushChapterButton() {
		
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXMLs/ReadingTypeSubFXMLs/Divider.fxml"));
        
        try {
			AnchorForReadingType anchor = new AnchorForReadingType("Unverified Chapter");
			HBox mainHBox = fxmlLoader.load();
			
			//adds the HBox to the anchor and binds the hbox to the dimensions
			anchor.getChildren().add(mainHBox);
			
			mainHBox.minWidthProperty().bind(anchor.widthProperty().subtract(subtractFromCollectorVBoxChild));
			mainHBox.maxWidthProperty().bind(anchor.widthProperty().subtract(subtractFromCollectorVBoxChild));
			
			mainHBox.minHeightProperty().bind(anchor.heightProperty());
			mainHBox.maxHeightProperty().bind(anchor.heightProperty());
						
			collectorVBox.getChildren().add(anchor);
			
			anchor.minWidthProperty().bind(collectorVBox.widthProperty().subtract(scrollbarBuffer));
			anchor.maxWidthProperty().bind(collectorVBox.widthProperty().subtract(scrollbarBuffer));

			//the both box will be slightly larger to compensate for being half the width 
			anchor.setMaxHeight(85);
			anchor.setMinHeight(85);
			
			AnchorPane buttonAnchor = (AnchorPane) mainHBox.getChildren().get(2);
			
			VBox directionVBox = (VBox) buttonAnchor.getChildren().get(0);
			Button upButton = (Button) directionVBox.getChildren().get(0);
			Button downButton = (Button) directionVBox.getChildren().get(2);
			
			handleUpButtonListener(upButton, anchor);
			handleDownButtonListener(downButton, anchor);
			
			Button removeNodeButton = (Button) buttonAnchor.getChildren().get(1);
			
			handleCloseButtonListener(removeNodeButton, anchor);
			
			BorderPane borderPane = (BorderPane) mainHBox.getChildren().get(1);
			
			AnchorPane actionButtonAnchor = (AnchorPane) borderPane.getLeft();
			Button actionButton = (Button) actionButtonAnchor.getChildren().get(0);
			TextField descriptionField = (TextField) borderPane.getCenter();
			
			actionButton.setOnAction(new EventHandler<ActionEvent>() { 
			@Override
			public void handle(ActionEvent event) {
				Button roleButton = new Button();
				roleButton.setText(descriptionField.getText());
				roleButton.setId("chapterDescriptionButton");
				
				borderPane.getChildren().clear();
				borderPane.setCenter(roleButton);
				
				anchor.setType("Chapter");
			}
	        });
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void createChapter(String name) {
		
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXMLs/ReadingTypeSubFXMLs/Divider.fxml"));
        
        try {
			AnchorForReadingType anchor = new AnchorForReadingType("Chapter");
			HBox mainHBox = fxmlLoader.load();
			
			//adds the HBox to the anchor and binds the hbox to the dimensions
			anchor.getChildren().add(mainHBox);
			
			mainHBox.minWidthProperty().bind(anchor.widthProperty().subtract(subtractFromCollectorVBoxChild));
			mainHBox.maxWidthProperty().bind(anchor.widthProperty().subtract(subtractFromCollectorVBoxChild));
			
			mainHBox.minHeightProperty().bind(anchor.heightProperty());
			mainHBox.maxHeightProperty().bind(anchor.heightProperty());
						
			collectorVBox.getChildren().add(anchor);
			
			anchor.minWidthProperty().bind(collectorVBox.widthProperty().subtract(scrollbarBuffer));
			anchor.maxWidthProperty().bind(collectorVBox.widthProperty().subtract(scrollbarBuffer));

			anchor.setMaxHeight(85);
			anchor.setMinHeight(85);
			
			AnchorPane buttonAnchor = (AnchorPane) mainHBox.getChildren().get(2);
			
			VBox directionVBox = (VBox) buttonAnchor.getChildren().get(0);
			Button upButton = (Button) directionVBox.getChildren().get(0);
			Button downButton = (Button) directionVBox.getChildren().get(2);
			
			handleUpButtonListener(upButton, anchor);
			handleDownButtonListener(downButton, anchor);
			
			Button removeNodeButton = (Button) buttonAnchor.getChildren().get(1);
			
			handleCloseButtonListener(removeNodeButton, anchor);
			
			BorderPane borderPane = (BorderPane) mainHBox.getChildren().get(1);
	
			Button roleButton = new Button();
			roleButton.setText(name);
			roleButton.setId("chapterDescriptionButton");
			
			borderPane.getChildren().clear();
			borderPane.setCenter(roleButton);
							
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void pushSectionButton() {
		
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXMLs/ReadingTypeSubFXMLs/Divider.fxml"));
        
        try {
			AnchorForReadingType anchor = new AnchorForReadingType("Unverified Section");
			HBox mainHBox = fxmlLoader.load();
			
			//adds the HBox to the anchor and binds the hbox to the dimensions
			anchor.getChildren().add(mainHBox);
			
			mainHBox.minWidthProperty().bind(anchor.widthProperty().subtract(subtractFromCollectorVBoxChild));
			mainHBox.maxWidthProperty().bind(anchor.widthProperty().subtract(subtractFromCollectorVBoxChild));
			
			mainHBox.minHeightProperty().bind(anchor.heightProperty());
			mainHBox.maxHeightProperty().bind(anchor.heightProperty());
						
			collectorVBox.getChildren().add(anchor);
			
			anchor.minWidthProperty().bind(collectorVBox.widthProperty().subtract(scrollbarBuffer));
			anchor.maxWidthProperty().bind(collectorVBox.widthProperty().subtract(scrollbarBuffer));

			//the both box will be slightly larger to compensate for being half the width 
			anchor.setMaxHeight(85);
			anchor.setMinHeight(85);
			
			AnchorPane buttonAnchor = (AnchorPane) mainHBox.getChildren().get(2);
			
			VBox directionVBox = (VBox) buttonAnchor.getChildren().get(0);
			Button upButton = (Button) directionVBox.getChildren().get(0);
			Button downButton = (Button) directionVBox.getChildren().get(2);
			
			handleUpButtonListener(upButton, anchor);
			handleDownButtonListener(downButton, anchor);
			
			Button removeNodeButton = (Button) buttonAnchor.getChildren().get(1);
			
			handleCloseButtonListener(removeNodeButton, anchor);
			
			BorderPane borderPane = (BorderPane) mainHBox.getChildren().get(1);
			
			AnchorPane actionButtonAnchor = (AnchorPane) borderPane.getLeft();
			Button actionButton = (Button) actionButtonAnchor.getChildren().get(0);
			TextField descriptionField = (TextField) borderPane.getCenter();
			
			actionButton.setOnAction(new EventHandler<ActionEvent>() { 
			@Override
			public void handle(ActionEvent event) {
				Button roleButton = new Button();
				roleButton.setText(descriptionField.getText());
				roleButton.setId("sectionDescriptionButton");
				
				borderPane.getChildren().clear();
				borderPane.setCenter(roleButton);
				
				anchor.setType("Section");
			}
	        });
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void createSection(String name) {
		
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXMLs/ReadingTypeSubFXMLs/Divider.fxml"));
        
        try {
			AnchorForReadingType anchor = new AnchorForReadingType("Section");
			HBox mainHBox = fxmlLoader.load();
			
			//adds the HBox to the anchor and binds the hbox to the dimensions
			anchor.getChildren().add(mainHBox);
			
			mainHBox.minWidthProperty().bind(anchor.widthProperty().subtract(subtractFromCollectorVBoxChild));
			mainHBox.maxWidthProperty().bind(anchor.widthProperty().subtract(subtractFromCollectorVBoxChild));
			
			mainHBox.minHeightProperty().bind(anchor.heightProperty());
			mainHBox.maxHeightProperty().bind(anchor.heightProperty());
						
			collectorVBox.getChildren().add(anchor);
			
			anchor.minWidthProperty().bind(collectorVBox.widthProperty().subtract(scrollbarBuffer));
			anchor.maxWidthProperty().bind(collectorVBox.widthProperty().subtract(scrollbarBuffer));

			anchor.setMaxHeight(85);
			anchor.setMinHeight(85);
			
			AnchorPane buttonAnchor = (AnchorPane) mainHBox.getChildren().get(2);
			
			VBox directionVBox = (VBox) buttonAnchor.getChildren().get(0);
			Button upButton = (Button) directionVBox.getChildren().get(0);
			Button downButton = (Button) directionVBox.getChildren().get(2);
			
			handleUpButtonListener(upButton, anchor);
			handleDownButtonListener(downButton, anchor);
			
			Button removeNodeButton = (Button) buttonAnchor.getChildren().get(1);
			
			handleCloseButtonListener(removeNodeButton, anchor);
			
			BorderPane borderPane = (BorderPane) mainHBox.getChildren().get(1);
	
			Button roleButton = new Button();
			roleButton.setText(name);
			roleButton.setId("sectionDescriptionButton");
			
			borderPane.getChildren().clear();
			borderPane.setCenter(roleButton);
							
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Concatenates all of the contents into a single string and returns it
	 * 
	 * the chapter and section nodes are excluded from this, they are simply glossed over in the iteration
	 */
	public String returnTextForClassifier() {
		
		//will iterate through and add to this string as it goes on
		String sumContents = "";
				
		for (Node node :getCollectorVBox().getChildren()) {
			
			//each anchor is actually a AnchorForReadingType, after it is cast we can see what type it is, Quote, Reading, Both
			AnchorPane anchor = (AnchorPane) node;
			AnchorForReadingType anchorSpecial = (AnchorForReadingType) anchor;
			
			String type = anchorSpecial.getType();
			
			//this is called here to avoid redundancy
			HBox mainHBox = (HBox) anchorSpecial.getChildren().get(0);
			
			if (type == "Analysis") {
				
				VBox mainVBox = (VBox) mainHBox.getChildren().get(1);
				TextArea analysisTextArea = (TextArea) mainVBox.getChildren().get(1);
				
			   sumContents += analysisTextArea.getText() + " ";
			}
			
			else if (type == "Quote") {
				
				VBox mainVBox = (VBox) mainHBox.getChildren().get(1);
				TextArea quoteTextArea = (TextArea) mainVBox.getChildren().get(1);
				
				sumContents += quoteTextArea.getText() + " ";
			}
			
			//this will be if the type is quote, can only be 3 types, hard else clause for principle
			else if (type == "Both") {
				
				VBox quoteVBox = (VBox) mainHBox.getChildren().get(1);
				TextArea quoteTextArea = (TextArea) quoteVBox.getChildren().get(1);
				
				VBox analysisVBox = (VBox) mainHBox.getChildren().get(3);
				TextArea analysisTextArea = (TextArea) analysisVBox.getChildren().get(1);
				
				sumContents += quoteTextArea.getText() + " " + analysisTextArea.getText() + " ";
			}
		}
		return sumContents;
	}

	public VBox getCollectorVBox() {
		return collectorVBox;
	}
}
