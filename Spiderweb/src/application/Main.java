package application;
	
import java.io.FileInputStream;
import java.io.InputStream;

import fxmlcontrollers.notetypes.ReadingTypeNoteController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;


public class Main extends Application {	
	
	@Override
	public void start(Stage primaryStage) {
		try {
	        primaryStage.initStyle(StageStyle.DECORATED);

			Parent root = FXMLLoader.load(getClass().getResource("/FXMLs/MainScreenRework.fxml"));
			Scene scene = new Scene(root);
			
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			//name and icon of the application
			
			String pathToImage = "src/images/StandardNote/branchwithleaf.png";
			
			InputStream is;
			is = new FileInputStream(pathToImage);
			Image iconImage = new Image(is);
			
			primaryStage.getIcons().add(iconImage);
			
			primaryStage.setTitle("SpiderWeb");

			
			primaryStage.setScene(scene);
			primaryStage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
		


	}

}
