module zzzz {
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;
	requires java.desktop;
	requires javafx.swing;
	requires javafx.base;
	requires edu.mit.jwi;
	
	opens application to javafx.graphics, javafx.fxml;
	opens fxmlcontrollers to javafx.graphics, javafx.fxml;
	opens handlers to javafx.graphics, javafx.fxml;
	opens fxmlcontrollers.notetypes to javafx.fxml;
	opens fxmlcontrollers.notetypes.subtypes to javafx.fxml;
	
}
