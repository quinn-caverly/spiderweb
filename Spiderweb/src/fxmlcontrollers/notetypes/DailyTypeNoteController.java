package fxmlcontrollers.notetypes;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

public class DailyTypeNoteController implements Initializable {
	
	@FXML
	private AnchorPane dailyTypeRoot;
	
	@FXML
	private TextArea brainstormingSection;
	
	@FXML
	private TextArea toDoSection;
	
	@FXML
	private TextArea calendarSection;
	

    public String returnTextForClassifier() {
    	return brainstormingSection.getText() + " " + toDoSection.getText() + " " + calendarSection.getText();
    }


	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
	}


	public AnchorPane getDailyTypeRoot() {
		return dailyTypeRoot;
	}


	public TextArea getBrainstormingSection() {
		return brainstormingSection;
	}


	public TextArea getToDoSection() {
		return toDoSection;
	}


	public TextArea getCalendarSection() {
		return calendarSection;
	}
}
