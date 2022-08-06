package application;
	
import java.io.FileInputStream;
import java.io.InputStream;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;


public class Main extends Application {	
	
	@Override
	public void start(Stage primaryStage) {
		try {
	        primaryStage.initStyle(StageStyle.DECORATED);
	        
			Parent root = FXMLLoader.load(getClass().getResource("/FXMLs/MainScreenRework.fxml"));
			Scene scene = new Scene(root);
			
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
	        scene.setFill(Color.TRANSPARENT);
			
			//name and icon of the application
			
			String pathToImage = "src/images/StandardNote/branchwithleaf.png";
			
			InputStream is;
			is = new FileInputStream(pathToImage);
			Image iconImage = new Image(is);
			
			primaryStage.getIcons().add(iconImage);
			
	        //primaryStage.initStyle(StageStyle.TRANSPARENT); //removes the basic top window bar
			
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
