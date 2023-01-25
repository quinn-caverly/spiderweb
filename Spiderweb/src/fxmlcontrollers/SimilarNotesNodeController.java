package fxmlcontrollers;

import application.MasterReference;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class SimilarNotesNodeController {	
	@FXML
	AnchorPane root;
	@FXML
	ImageView imageView;
	@FXML
	Label ratingNumberLabel;
	@FXML
	Label titleOfNoteLabel;
	
	
	//has to take root as an argument because can only access fxml components from initialize me
	public void setSizeAndPosition(AnchorPane root) {
		root.maxWidthProperty().bind(root.heightProperty());
		root.setMinWidth(200);
	}

	public AnchorPane getRoot() {
		return root;
	}

	public ImageView getImageView() {
		return imageView;
	}

	public Label getRatingNumberLabel() {
		return ratingNumberLabel;
	}

	public Label getTitleOfNoteLabel() {
		return titleOfNoteLabel;
	}
}
