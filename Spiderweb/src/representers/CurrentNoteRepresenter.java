package representers;

import application.MasterReference;
import handlers.NoteChooserHandler.Note;
import javafx.scene.control.TreeItem;

//is called by multiple different classes
//is a representation of which note is the "current" note
//IE the note that is currently opened by the user and can be edited
//and the note which will have actions performed on it

public class CurrentNoteRepresenter {
	MasterReference mR;
	
	TreeItem<Note> currentNote;
	
	public CurrentNoteRepresenter(MasterReference mR) {
		this.mR = mR;
	}
	
	public TreeItem<Note> getCurrentNote() {
		return currentNote;
	}

	public void setCurrentNote(TreeItem<Note> currentNote) {
		this.currentNote = currentNote;
	}
}