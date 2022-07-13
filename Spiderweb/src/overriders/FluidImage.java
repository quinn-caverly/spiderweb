package overriders;

import javafx.beans.value.ObservableValue;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class FluidImage extends ImageView {
	
	public FluidImage() {		
	}
	
	public void bindWithWrapper() {
		
		this.fitWidthProperty().bind(((AnchorPane) this.getParent()).widthProperty().subtract(2));
		this.fitHeightProperty().bind(((AnchorPane) this.getParent()).heightProperty().subtract(2));
		
		this.setLayoutX(1);
		this.setLayoutY(1);
		
	}

}
