package fxmlcontrollers.notetypes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.MasterReference;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import overriders.FluidImageWrapper;

public class StandardTypeNoteController implements Initializable{
	
    private final Double goldenRatio = (double) (1+Math.sqrt(5))/2;
    private final Integer heightOfTitleArea = 10;
    private final Integer heightOfSeparator = 8;
	
	@FXML
	private SplitPane noteSplit;
	@FXML
	private VBox leftVBox;
	@FXML
	private VBox rightVBox;
	@FXML
	private Pane textSectionPane;
	@FXML
	private VBox textSectionVBox;
	@FXML
	private TextArea titleArea;
	@FXML
	private AnchorPane mainSeparator;
	@FXML
	private TextArea mainTextArea;
	@FXML
	private Button balanceImagesButton;
	@FXML
	private AnchorPane marginKeeper;
	@FXML
	private AnchorPane imagePool;
	@FXML
	private AnchorPane parentOfTextSectionPane;
	@FXML
	private AnchorPane standardTypeRoot;
	@FXML
	private AnchorPane parentOfMarginKeeper;
	@FXML
	private TabPane standardTypeTabPane;
	@FXML
	private Button imagesButton;
	@FXML
	private Button pyramidButton;
	@FXML
	private Button pinnedButton;
	@FXML
	private Tab imagesTab;
	@FXML
	private Tab pyramidTab;
	@FXML
	private Tab pinnedTab;
	@FXML
	private VBox collectorVBox;
	@FXML
	private AnchorPane parentOfScrollPane;
	@FXML
	private AnchorPane pyramidMarginKeeper;	
	
	private MasterReference mR;
	
	public void setMasterReference(MasterReference mR) {
		this.mR = mR;
	}
	
    @FXML
    //makes the mouse icon of a dragged file change to appear as if it is accepting information
    private void dragOverDroppableIndicator(DragEvent event) {
    	if(event.getDragboard().hasFiles()) {
    	event.acceptTransferModes(TransferMode.ANY);
    	}
    }
    
    @FXML
    private void handleFileDropIntoImagePool(DragEvent event) throws FileNotFoundException {
    	//reads the file that was dropped in as an image, throws an error if not an image
    	List<File> files = event.getDragboard().getFiles();
		Image img = new Image(new FileInputStream(files.get(0)));
		
		//needs to create a new imageView for that image
		FluidImageWrapper fluidImageWrapper = new FluidImageWrapper(imagePool, img);
		
		fluidImageWrapper.initialPlace(event);			
		imagePool.getChildren().add(fluidImageWrapper);		
    }
    
    
    //this now returns only the text in the text area
    //this is because the titles of otherwise empty notes were giving high values due to their uniqueness
    public String returnTextForClassifier() {
    	return mainTextArea.getText();
    }
	
	
	

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		parentOfMarginKeeper.maxHeightProperty().bind(standardTypeRoot.heightProperty());
		parentOfMarginKeeper.minHeightProperty().bind(standardTypeRoot.heightProperty());
		
		//has to bind the marginKeeper's heights and widths because this cannot be done easily in SceneBuilder
		pyramidMarginKeeper.minWidthProperty().bind(parentOfScrollPane.widthProperty());
		pyramidMarginKeeper.maxWidthProperty().bind(parentOfScrollPane.widthProperty());
		
		//the margin keeper height is bounded to its child, or the collector vbox, this makes it so that the scrollpane is activated as the anchorpane becomes larger than the area
		pyramidMarginKeeper.minHeightProperty().bind(collectorVBox.heightProperty());
		pyramidMarginKeeper.maxHeightProperty().bind(collectorVBox.heightProperty());
		
			
		titleArea.setPrefRowCount(1);
		titleArea.setWrapText(true);
		mainTextArea.setWrapText(true);
		
		
		initializeImagePool();
		
	}
	
	//has to be called after the note has been opened by ReadAndWriteHandler
	public void chooseInitiallySelectedTab() {
		//the priority if there is content in all 3 goes... images -> pyramid -> pinned
		
		if (imagePool.getChildren().size() != 0) {
			imagesButtonPushed();
		}
		
		else if (collectorVBox.getChildren().size() != 0) {
			pyramidButtonPushed();
		}
	}
	
	//must be called by ReadAndWriteHandler
	public void addStarsToTabsWithContents() {
	
		
		if (imagePool.getChildren().size() != 0) {
			imagesButton.setGraphic(mR.getButtonIconImageView());
		}
		
		if (collectorVBox.getChildren().size() != 0) {
			pyramidButton.setGraphic(mR.getButtonIconImageView());
		}
	}
	
	
	//called by fxml button
	public void imagesButtonPushed() {
		enableAllButtons();
		imagesButton.setDisable(true);
		
		standardTypeTabPane.getSelectionModel().select(imagesTab);
	}
	
	//called by fxml button
	public void pyramidButtonPushed() {
		enableAllButtons();
		pyramidButton.setDisable(true);

		standardTypeTabPane.getSelectionModel().select(pyramidTab);
	}
	
	//called by fxml button
	public void pinnedButtonPushed() {
		enableAllButtons();
		pinnedButton.setDisable(true);

		standardTypeTabPane.getSelectionModel().select(pinnedTab);
	}
	
	private void enableAllButtons() {
		imagesButton.setDisable(false);
		pyramidButton.setDisable(false);
		pinnedButton.setDisable(false);
	}
	
	
	
	public TextArea addLevelButtonPushed() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXMLs/StandardTypeSubFXMLs/PyramidLevel.fxml"));

		HBox mainHBox;
		try {		
			mainHBox = fxmlLoader.load();
			
			VBox mainVBox = (VBox) mainHBox.getChildren().get(1);
			TextArea textArea = (TextArea) mainVBox.getChildren().get(1);
			
			//adds the anchor to the collection and once again binds the dimensions
			collectorVBox.getChildren().add(mainHBox);

			mainHBox.minWidthProperty().bind(collectorVBox.widthProperty());
			mainHBox.maxWidthProperty().bind(collectorVBox.widthProperty());

			mainHBox.setMaxHeight(150);
			mainHBox.setMinHeight(150);
			
			//handles the close button
			AnchorPane buttonAnchor = (AnchorPane) mainHBox.getChildren().get(3);
			GridPane buttonGrid = (GridPane) buttonAnchor.getChildren().get(0);
			Button removeNodeButton = (Button) buttonGrid.getChildren().get(0);
			
			handleCloseButtonListener(removeNodeButton, mainHBox);
			
			setLevelMarginWidths();
			
			//returns the textArea so that when note is being created, node can be created then text edited by ReadAndWriteHandler
			return textArea;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	private void handleCloseButtonListener(Button button, HBox mainHBox) {
        button.setOnAction(new EventHandler<ActionEvent>() { 
		@Override
		public void handle(ActionEvent event) {

			collectorVBox.getChildren().remove(mainHBox);
			
			try {
				mR.saveCurrentNote();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}});
	}
	
	//this is what will make it look like a pyramid. The ones on top with half the largest margin widths, which makes text area smaller
	//the default is 25
	private void setLevelMarginWidths() {
		
		Integer numberOfLevels = collectorVBox.getChildren().size();
		//this starts as the width of the margins of the highest/smallest level
		Integer currentWidth = numberOfLevels * 50 + 25;
		
		for (Node level: collectorVBox.getChildren()) {
			HBox currentHBox = (HBox) level;
			
			AnchorPane leftMargin = (AnchorPane) currentHBox.getChildren().get(0);
			AnchorPane rightMargin = (AnchorPane) currentHBox.getChildren().get(2);
			
			leftMargin.setMaxWidth(currentWidth);
			leftMargin.setMinWidth(currentWidth);
			
			rightMargin.setMaxWidth(currentWidth);
			rightMargin.setMinWidth(currentWidth);

			currentWidth -= 50;
			
		}
	}
	
	
	
	private void initializeImagePool() {
		parentOfMarginKeeper.getChildren().clear();
		
		parentOfMarginKeeper.getChildren().add(marginKeeper);
		
		imagePool.prefWidthProperty().bind(marginKeeper.widthProperty());
		imagePool.prefHeightProperty().bind(marginKeeper.heightProperty());
			
		imagePool.maxWidthProperty().bind(marginKeeper.heightProperty().divide(goldenRatio));
		imagePool.maxHeightProperty().bind(marginKeeper.widthProperty().multiply(goldenRatio));
			
		//centers the imagePool by taking (width of parent - width of child) / 2
		imagePool.translateXProperty()
			.bind(((AnchorPane) imagePool.getParent()).widthProperty().subtract(imagePool.widthProperty())
					.divide(2));
	    
		imagePool.translateYProperty()
			.bind(((AnchorPane) imagePool.getParent()).heightProperty().subtract(imagePool.heightProperty())
					.divide(2));
	}

	
	

	public AnchorPane getStandardTypeRoot() {
		return standardTypeRoot;
	}

	public Double getGoldenRatio() {
		return goldenRatio;
	}

	public Integer getHeightOfTitleArea() {
		return heightOfTitleArea;
	}

	public Integer getHeightOfSeparator() {
		return heightOfSeparator;
	}

	public SplitPane getNoteSplit() {
		return noteSplit;
	}

	public VBox getLeftVBox() {
		return leftVBox;
	}

	public VBox getRightVBox() {
		return rightVBox;
	}

	public Pane getTextSectionPane() {
		return textSectionPane;
	}

	public VBox getTextSectionVBox() {
		return textSectionVBox;
	}

	public TextArea getTitleArea() {
		return titleArea;
	}

	public AnchorPane getMainSeparator() {
		return mainSeparator;
	}

	public TextArea getMainTextArea() {
		return mainTextArea;
	}

	public Button getBalanceImagesButton() {
		return balanceImagesButton;
	}

	public AnchorPane getMarginKeeper() {
		return marginKeeper;
	}

	public AnchorPane getImagePool() {
		return imagePool;
	}

	public AnchorPane getParentOfTextSectionPane() {
		return parentOfTextSectionPane;
	}

	public MasterReference getmR() {
		return mR;
	}

	public VBox getCollectorVBox() {
		return collectorVBox;
	}
}
