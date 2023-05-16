package quinncaverly.spiderweb.application;

import java.io.IOException;
import java.util.ArrayList;

import quinncaverly.spiderweb.application.NoteChooserHandler.Note;
import quinncaverly.spiderweb.application.NoteChooserHandler.TypeTab;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import quinncaverly.spiderweb.fxmlcontrollers.notetypes.DailyTypeNoteController;
import quinncaverly.spiderweb.fxmlcontrollers.notetypes.ReadingTypeNoteController;
import quinncaverly.spiderweb.fxmlcontrollers.notetypes.StandardTypeNoteController;

public class PinnedNotesHandler {

	MasterReference mR;

	public PinnedNotesHandler(MasterReference mR) {
		this.mR = mR;
	}

	public void newNoteOpenedProcedure() {

		mR.getMainClassController().getPinnedNotesHBox().getChildren().clear();

		//can get the reference to the treeView and currentlyOpenedNote from the noteChooserHandler

		//has to be reassigned because currentNoteRepresenter is sometimes null

		TypeTab typeTab = (TypeTab) mR.getMainClassController().getNoteTabPane().getSelectionModel().getSelectedItem();
		TreeItem<Note> currentItem = typeTab.getTreeItem();

		Note note = currentItem.getValue();

		ArrayList<Integer> listOfPinnedIDs = note.getListOfPinnedIDs();

		ArrayList<TreeItem<Note>> listOfPinnedTreeItems = new ArrayList<TreeItem<Note>>();
		ArrayList<TreeItem<Note>> listOfTreeItems = mR.getPipelineConsolidator().createListOfTreeItems();


		for (TreeItem<Note> treeItem : listOfTreeItems) {
			Note currentNote = treeItem.getValue();

			if (listOfPinnedIDs.contains(currentNote.getId())) {
				listOfPinnedTreeItems.add(treeItem);
			}
		}

		//adds each pinnedtreeitem to the pinnedNotesHBox
		for (TreeItem<Note> currentTreeItem : listOfPinnedTreeItems) {
			Note currentNote = currentTreeItem.getValue();

    			FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXMLs/PinnedNoteNode.fxml"));

            	try {
            		AnchorPane root = loader.load();

            		HBox mainHBox = (HBox) root.getChildren().get(0);
            		VBox mainVBox = (VBox) mainHBox.getChildren().get(1);

            		Button button = (Button) mainVBox.getChildren().get(1);
            		TextArea textArea = (TextArea) mainVBox.getChildren().get(3);

            		button.setText(currentNote.getName());
            		button.setStyle("-fx-font-size: "+mR.getSimilarNotesButtonFontSize().toString()+";");

            		textArea.setStyle("-fx-font-size: "+mR.getSimilarNotesButtonFontSize().toString()+";");

            		//the classifier handler defined this function, could move the function to masterReference for centrality, staying for now
            		mR.getPipelineConsolidator().setButtonIcon(button, currentTreeItem);

        			EventHandler<ActionEvent> onButtonOnePressed = (new EventHandler<ActionEvent>() {
        				@Override
        				public void handle(ActionEvent arg0) {
        					try {
        						mR.openNote(currentTreeItem);
							} catch (IOException e) {
								e.printStackTrace();
							}
        				}});
        			button.setOnAction(onButtonOnePressed);


        			AnchorPane buttonAnchor = (AnchorPane) mainHBox.getChildren().get(2);
        			GridPane buttonGridPane = (GridPane) buttonAnchor.getChildren().get(0);
        			Button closeButton = (Button) buttonGridPane.getChildren().get(0);

        			EventHandler<ActionEvent> onCloseButtonPressed = (new EventHandler<ActionEvent>() {
        				@Override
        				public void handle(ActionEvent arg0) {
        					//will remove the note from the opened note's list of pinned notes
        					Integer idOfNoteToClose = currentNote.getId();
        					note.getListOfPinnedIDs().remove(idOfNoteToClose);

        					//refreshes the pinnedNotesHBox
        					PinnedNotesHandler.this.newNoteOpenedProcedure();

        					//saves the current note so the changes are saved
        					try {
								mR.saveCurrentNote();
							} catch (IOException e) {
								e.printStackTrace();
							}
        				}});
        			closeButton.setOnAction(onCloseButtonPressed);


        			//sets the text in the text area

        			if (currentNote.getTypeOfNote() == "Standard") {
        				StandardTypeNoteController stnc = (StandardTypeNoteController) currentNote.getController();

        				textArea.setText(stnc.getStandardTypeMainTextArea().getText());
        			}

        			else if (currentNote.getTypeOfNote() == "Daily") {
        				DailyTypeNoteController dtnc = (DailyTypeNoteController) currentNote.getController();

        				textArea.setText(dtnc.getBrainstormingSection().getText());
        			}

        			else if (currentNote.getTypeOfNote() == "Reading") {
        				ReadingTypeNoteController rtnc = (ReadingTypeNoteController) currentNote.getController();

        				textArea.setText(rtnc.returnTextForClassifier());
        			}

            		mR.getMainClassController().getPinnedNotesHBox().getChildren().add(root);
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    		}
	}
}
