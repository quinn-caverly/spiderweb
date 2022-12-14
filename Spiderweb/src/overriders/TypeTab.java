package overriders;

import handlers.NoteChooserHandler.Note;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;

public class TypeTab extends Tab {
	
	private TreeItem<Note> treeItem;
	private Note scroll;
	
	public TypeTab(String name, Node node, TreeItem<Note> treeItem) {
		super(name, node);
		this.treeItem = treeItem;
	}
	
	/*
	 * this is needed because a scroll is not contained in a treeItem
	 * this could be refactored so that everything is just a note and not treeItem
	 * if I ever do a large refactor of code
	 * 
	 * TODO
	 */
	public TypeTab(String name, Node node, Note scroll) {
		super(name, node);
		this.scroll = scroll;
	}

	public TreeItem<Note> getTreeItem() {
		return treeItem;
	}

	public Note getScroll() {
		return scroll;
	}
}
