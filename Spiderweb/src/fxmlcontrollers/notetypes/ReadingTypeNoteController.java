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
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import overriders.AnchorForReadingType;

public class ReadingTypeNoteController implements Initializable {
	
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
	
	//subtracted from the bound width in order to prevent bottom scrollbar from being necessary due to
	//the right-side scrollbar eating up space
	private static Integer scrollbarBuffer = 10;
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
			
		//has to bind the marginKeeper's heights and widths because this cannot be done easily in SceneBuilder
		marginKeeper.minWidthProperty().bind(parentOfScrollPane.widthProperty());
		marginKeeper.maxWidthProperty().bind(parentOfScrollPane.widthProperty());
		
		//the margin keeper height is bounded to its child, or the collector vbox, this makes it so that the scrollpane is activated as the anchorpane becomes larger than the area
		marginKeeper.minHeightProperty().bind(collectorVBox.heightProperty());
		marginKeeper.maxHeightProperty().bind(collectorVBox.heightProperty());
		
		
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
	}
	
	private void handleCloseButtonListener(Button button, AnchorPane anchor) {
        button.setOnAction(new EventHandler<ActionEvent>() { 
		@Override
		public void handle(ActionEvent event) {

			collectorVBox.getChildren().remove(anchor);
			
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
			
			mainHBox.minWidthProperty().bind(anchor.widthProperty());
			mainHBox.maxWidthProperty().bind(anchor.widthProperty());
			
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
			Button removeNodeButton = (Button) buttonAnchor.getChildren().get(0);
			
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
			
			mainHBox.minWidthProperty().bind(anchor.widthProperty());
			mainHBox.maxWidthProperty().bind(anchor.widthProperty());
			
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
			Button removeNodeButton = (Button) buttonAnchor.getChildren().get(0);
			
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
			
			mainHBox.minWidthProperty().bind(anchor.widthProperty());
			mainHBox.maxWidthProperty().bind(anchor.widthProperty());
			
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

			//handles the close button
			AnchorPane buttonAnchor = (AnchorPane) mainHBox.getChildren().get(4);
			Button removeNodeButton = (Button) buttonAnchor.getChildren().get(0);
			
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
	
	
	//this needs to go through and find simply concatenate all of the text in the various components and then return it
	public String returnTextForClassifier() {
		
		//will iterate through and add to this string as it goes on
		String sumContents = "";
		
		
		//the following code is very similar to the saveReadingNote method in ReadAndWriteHandler
		
		for (Node node :getCollectorVBox().getChildren()) {
			
			//each anchor is actually a AnchorForReadingType, after it is cast we can see what type it is, Quote, Reading, Both
			AnchorPane anchor = (AnchorPane) node;
			AnchorForReadingType anchorSpecial = (AnchorForReadingType) anchor;
			
			String type = anchorSpecial.getType();
			
			//this is called here to avoid redundancy
			HBox mainHBox = (HBox) anchorSpecial.getChildren().get(0);
			VBox mainVBox = (VBox) mainHBox.getChildren().get(1);
			
			if (type == "Analysis") {
				
				TextArea analysisTextArea = (TextArea) mainVBox.getChildren().get(1);
				
			   sumContents += analysisTextArea.getText() + " ";
			}
			
			else if (type == "Quote") {
				
				TextArea quoteTextArea = (TextArea) mainVBox.getChildren().get(1);
				
				sumContents += quoteTextArea.getText() + " ";
			}
			
			//this will be if the type is quote, can only be 3 types, hard else clause for principle
			else {
				
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
