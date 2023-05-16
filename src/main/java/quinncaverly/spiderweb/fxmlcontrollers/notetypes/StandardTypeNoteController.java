package quinncaverly.spiderweb.fxmlcontrollers.notetypes;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

public class StandardTypeNoteController implements Initializable{


	@FXML
	private AnchorPane standardTypeRoot;
	@FXML
	private TextArea standardTypeMainTextArea;
	@FXML
	private Button standardTypeNoteTitleButton;

    //this now returns only the text in the text area
    //this is because the titles of otherwise empty notes were giving high values due to their uniqueness
    public String returnTextForClassifier() {
    	return standardTypeMainTextArea.getText();
    }

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		standardTypeMainTextArea.setWrapText(true);
	}

	public AnchorPane getStandardTypeRoot() {
		return standardTypeRoot;
	}

	public TextArea getStandardTypeMainTextArea() {
		return standardTypeMainTextArea;
	}

	public Button getStandardTypeNoteTitleButton() {
		return standardTypeNoteTitleButton;
	}
}
