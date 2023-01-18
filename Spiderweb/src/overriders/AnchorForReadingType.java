package overriders;

import javafx.scene.layout.AnchorPane;

public class AnchorForReadingType extends AnchorPane {
	
	String type;
	
	public AnchorForReadingType(String type) {
		this.type = type;		
	}

	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
}
