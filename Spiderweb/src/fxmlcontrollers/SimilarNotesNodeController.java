package fxmlcontrollers;

import application.MasterReference;
import handlers.NoteChooserHandler.Note;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class SimilarNotesNodeController {
	
	private MasterReference mR;
	private TreeItem<Note> treeItem;
	
	@FXML
	AnchorPane root;
	@FXML
	ImageView imageView;
	@FXML
	Label ratingNumberLabel;
	@FXML
	Label titleOfNoteLabel;
	
	
	public void setMasterReference(MasterReference mR) {
		this.mR = mR;
	}
	
	public void setTreeItem(TreeItem<Note> treeItem) {
		this.treeItem = treeItem;
	}
	
	//has to take root as an argument because can only access fxml components from initialize me
	public void setSizeAndPosition(AnchorPane root) {
		
		SplitPane bottomSplit = mR.getMainClassController().getBottomSplit();
		
		root.maxHeightProperty().bind(bottomSplit.heightProperty());
		root.minHeightProperty().bind(bottomSplit.heightProperty());

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
