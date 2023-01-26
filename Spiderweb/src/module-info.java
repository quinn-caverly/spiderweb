module quinncaverly {
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;
	requires java.desktop;
	requires javafx.swing;
	requires javafx.base;
	requires edu.mit.jwi;
	requires org.apache.opennlp.tools;
	requires java.sql;
	
	opens application to javafx.graphics, javafx.fxml;
	opens fxmlcontrollers to javafx.graphics, javafx.fxml;
	opens fxmlcontrollers.notetypes to javafx.fxml;
}
