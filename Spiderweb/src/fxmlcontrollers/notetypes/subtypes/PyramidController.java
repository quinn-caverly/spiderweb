package fxmlcontrollers.notetypes.subtypes;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

public class PyramidController implements Initializable {

	@FXML
	AnchorPane pyramidRoot;

	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		/*
		parentOfButtonVBox.maxWidthProperty().bind(pyramidRoot.widthProperty());
		parentOfButtonVBox.minWidthProperty().bind(pyramidRoot.widthProperty());

		parentOfScrollPane.maxWidthProperty().bind(pyramidRoot.widthProperty());
		parentOfScrollPane.minWidthProperty().bind(pyramidRoot.widthProperty());
		*/
	}


	public AnchorPane getPyramidRoot() {
		return pyramidRoot;
	}
}
