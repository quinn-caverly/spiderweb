package quinncaverly.spiderweb.application;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Objects;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;


public class Main extends Application {

	@Override
	public void start(Stage primaryStage) {
		try {
	        primaryStage.initStyle(StageStyle.DECORATED);

			Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/FXMLs/MainScreen.fxml")));
			Scene scene = new Scene(root);

			scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/application.css")).toExternalForm());
	        scene.setFill(Color.TRANSPARENT);

			//name and icon of the application

			InputStream is;
			is = new FileInputStream("src/main/resources/files/hatBearRoundedCorners.png");
			Image iconImage = new Image(is);

			primaryStage.getIcons().add(iconImage);

			primaryStage.setTitle("Spiderweb");

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
