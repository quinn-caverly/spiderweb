package quinncaverly.spiderweb.fxmlcontrollers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class TreeViewCellController {

	@FXML
	private ImageView iconView;

	@FXML
	private Label cellLabel;

	@FXML
	private HBox root;

	public ImageView getIconView() {
		return iconView;
	}

	public Label getCellLabel() {
		return cellLabel;
	}

	public HBox getRoot() {
		return root;
	}
}
