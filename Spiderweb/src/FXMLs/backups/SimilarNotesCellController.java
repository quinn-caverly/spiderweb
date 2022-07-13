package FXMLs.backups;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
  
public class SimilarNotesCellController implements Initializable{
	
	@FXML
	private AnchorPane similarNotesCell;
	
	@FXML
	private Label testLabel;
	
	public void updateInfo(String text) {				
	}
	
	
	public String returnCell() {
		
		return "Testing";
	}
	
	public ImageView returnGraphic() {		
	    Image image = new Image("/220px-Stussy_Logo.svg.png");
		ImageView iv = new ImageView(image);
		
		return iv;
	}


	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/SimilarNotesCell.fxml"));
		try {
			similarNotesCell = fxmlLoader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		testLabel.setText("Blah");
		
		
	}
	

}
