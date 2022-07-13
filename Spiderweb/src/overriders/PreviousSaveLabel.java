package overriders;

import javafx.scene.control.Label;

//this is the label which tells the user where the currently opened file will be saved to
//It is a subclass of Label so that it can have an attribute
//it not only notifies the user, but also notifies the other programs where the note should be saved to
//this is so that methods only have to alter the state of one object (this one)
public class PreviousSaveLabel {
	
	private Label label;
	//directory and state always change at the same time
	
	//directory without ending "//"
	private String directory;
	
	private DirectoryTreeItem directoryTreeItem;
	
	//there are only a few values state can have
	private String state;
	
		// "uninitialized" //for when no directory has been set
		// "noneSelected" //for when directory has been set but nothing has been selected\
		// "selected" //for when the note has not been saved
		// "concreteSelected" //for when the note has already been saved in a location and cannot be changed
	
	public PreviousSaveLabel(Label label) {
		this.label = label;
		state = "unitialized";
	}
	
	public void changeState(String state) {
		this.state = state;
		
		
		//can be called from:
		//DirectoryChooserHandler.handleWhenNoDirectoryHasBeenInitialized()
		if (state == "uninitialized") {
			directory = "Create a directory to save a note";
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
		
		if (this.state == "concreteSelected") {
			//cannot change the save location because it already has a home
		}
		else {
			
			this.state = state;
			this.directory = directoryTreeItem.getPath();
			
			this.directoryTreeItem = directoryTreeItem;
			
			label.setText("Will save to: " + directory);
		}
	}

	public String getDirectory() {
		return directory;
	}
	
	public void setDirectory(String directory) {
		this.directory = directory;
	}
	
	public DirectoryTreeItem getDirectoryTreeItem() {
		return directoryTreeItem;
	}

	public String getState() {
		return state;
	}
	
	//resets the attributes to default values
	public void reset() {
		directory = null;
		state = "uninitialized";
	}
}
