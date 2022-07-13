package overriders;

import javafx.scene.control.Label;

public class FutureSaveLabel extends Label {
	
	private Label label;
	
	//directory and state always change at the same time
	
	//directory without ending "//"
	private String directory;
	
	private DirectoryTreeItem directoryTreeItem;
	
	//there are only a few values state can have
	private String state;
	
		// "uninitialized" //for when no directory has been set
		// "noneSelected" //for when directory has been set but nothing has been selected\
		// "selected" //for when there is a note selected
	
	public FutureSaveLabel(Label label) {
		this.label = label;
		state = "unitialized";
	}
	
	public void changeState(String state) {
		this.state = state;
		
		
		//can be called from:
		//DirectoryChooserHandler.handleWhenNoDirectoryHasBeenInitialized()
		if (state == "uninitialized") {
			directory = "";
			label.setText(directory);
		}
		//can be called from:
		//DirectoryChooserHandler.openDirectoryChooser()
		else if (state == "noneSelected") {
			directory = "Please select a file to save a note";
			label.setText(directory);
		}
		
	}
	
	public void changeState(String state, DirectoryTreeItem directoryTreeItem) {
		this.state = state;
		this.directory = directoryTreeItem.getPath();
		
		this.directoryTreeItem = directoryTreeItem;
		
		label.setText("Will save to: " + directory);
	}	
	
	
	public String getDirectory() {
		return directory;
	}

	public String getState() {
		return state;
	}
	
	public DirectoryTreeItem getDirectoryTreeItem() {
		return directoryTreeItem;
	}
	
	//resets the attributes to default values
	public void reset() {
		directory = null;
		state = "uninitialized";
	}
}
