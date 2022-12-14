package fxmlcontrollers.notetypes.subtypes;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

public class DailyScrollToDoSectionNodeController implements Initializable {


	private TextArea reflectionTextArea;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		setReflectionTextArea(new TextArea());
		
	}

	public TextArea getReflectionTextArea() {
		return reflectionTextArea;
	}

	public void setReflectionTextArea(TextArea reflectionTextArea) {
		this.reflectionTextArea = reflectionTextArea;
	}
}
