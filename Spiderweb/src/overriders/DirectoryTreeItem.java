package overriders;

import java.io.File;

import javafx.scene.control.TreeItem;

public class DirectoryTreeItem extends TreeItem<Object> {
	
	String path;
	
	//this will be true if the particular item is a file which can be created under
	//false if it is a note which cannot be created under
	Boolean bool = true;
	
	public DirectoryTreeItem(String name, String encapsulatingFilePath) {
		this.setValue(name);
        this.setPath(encapsulatingFilePath + "/" + name);

        //createSystemFile();

		this.setExpanded(true);
	}
	
	public void createSystemFile() {
    	File rootFile = new File(this.getPath());
    	rootFile.mkdir();
	}
	
	
	

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public String getDirectoriesTxtValue() {
		if (bool = true) {
			return (String) this.getValue();
		}
		
		else {
			//the *Note* will denote that this is a note which cannot have a file made under it
			//the * is a character which cannot be used to create a file
			//so this will never be set off by accident
			String temp = (String) this.getValue();
			return temp + "*Note*";
		}
	}
	
	public void setFolder(Boolean bool) {
		this.bool = bool;
	}
	
	
	public Boolean getFolder() {
		return bool;
	}
	
	
	
	
	
}
