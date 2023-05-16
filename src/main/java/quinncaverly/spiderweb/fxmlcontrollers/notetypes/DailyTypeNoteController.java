package quinncaverly.spiderweb.fxmlcontrollers.notetypes;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

public class DailyTypeNoteController{

	@FXML
	private AnchorPane dailyTypeRoot;
	@FXML
	private TextArea brainstormingSection;
	@FXML
	private TextArea toDoSection;
	@FXML
	private TextArea calendarSection;

	//this is the main function of this class, returns the 3 main sections for the classifier. (this is also used for the preview in pinned notes)
	public String returnTextForClassifier() {
    	return brainstormingSection.getText() + " " + toDoSection.getText() + " " + calendarSection.getText();
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
