package overriders;

import java.net.URL;
import java.util.ResourceBundle;

import handlers.NoteChooserHandler.Note;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;

public class TypeTab extends Tab implements Initializable {
	
	private TreeItem<Note> treeItem;
	
	public TypeTab(String name, Node node, TreeItem<Note> treeItem) {
		
		super(name, node);
		this.treeItem = treeItem;
	}

	public TreeItem<Note> getTreeItem() {
		return treeItem;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {		
	}
}
