module quinncaverly.spiderweb {
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;
	requires java.desktop;
	requires javafx.swing;
	requires javafx.base;
	requires jwi;
	requires org.apache.opennlp.tools;
	requires java.sql;

	opens quinncaverly.spiderweb.application to javafx.graphics, javafx.fxml;
	opens quinncaverly.spiderweb.fxmlcontrollers to javafx.graphics, javafx.fxml;
	opens quinncaverly.spiderweb.fxmlcontrollers.notetypes to javafx.fxml;

    exports quinncaverly.spiderweb.application;
	exports quinncaverly.spiderweb.fxmlcontrollers;
}
