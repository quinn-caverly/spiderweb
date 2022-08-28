package handlers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;
import application.MasterReference;
import fxmlcontrollers.MainClassController;
import fxmlcontrollers.TreeViewCellController;
import fxmlcontrollers.notetypes.DailyTypeNoteController;
import fxmlcontrollers.notetypes.ReadingTypeNoteController;
import fxmlcontrollers.notetypes.StandardTypeNoteController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class NoteChooserHandler {	
	//mainReference
	MasterReference mR;
	MainClassController mCC;
	

	private TreeView<Note> treeView;
	private MainClassController mainClassController;
	private VBox parentOfTreeView;
	private HBox superParentOfTreeView;
	private HBox functionBox;
	private Pane textSectionPane;
	private FunctionBoxRepresenter functionBoxRepresenter;
	
	private static final Set<String> ALLOWEDCHARACTERS = Set.of(
			"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"
			, "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"
			, "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "(", ")", "-", "_", ".", "!"
		);

	public TreeView<Note> getTreeView() {
		return treeView;
	}



	public NoteChooserHandler(MasterReference mR) {
		this.mR = mR;
		mCC = mR.getMainClassController();
	}

	
	
	
	public class Note {
		
		//an id that uniquely identifies the note because the filepath cannot uniquely identify the note because it changes
		private Integer id;
		
		//an Integer[] of the ids which are pinned to this note
		private ArrayList<Integer> listOfPinnedIDs;
		
		//this is the root which is loaded from fxml which is added to the tabPane on open
		private AnchorPane root;
		
		private Object controller;
		
		private TreeViewCellController cellController;
		
		private HBox loadedHBox;

	    private final String name;
	    private final String filePath;
	    private final String typeOfNote;
	    
	    private String childrenFilePath;
	    private String selfPath;
	    
	    private Double scoreWithNoteBeingClassified;
	    
	    private boolean isFullSaved = false;
	    
	    //so that the note is not initialized twice
	    private boolean isInitialized = false;
	    
	    public Note(String name, String filePath, String typeOfNote){	    	
	        this.name = name;
	        this.filePath = filePath;
	        this.typeOfNote = typeOfNote;
	        
	        listOfPinnedIDs = new ArrayList<Integer>();
	        
	        createSystemFiles();	        
	        
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXMLs/TreeViewCell.fxml"));

	    	try {
				HBox loadedHBox = fxmlLoader.load();
				
				this.loadedHBox = loadedHBox;

				ImageView iconView = (ImageView) loadedHBox.getChildren().get(0);
				Label label = (Label) loadedHBox.getChildren().get(1);
				
				label.setText(name);
				
				InputStream is = new FileInputStream("src/images/SaveIcon.png");
				Image saveIcon = new Image(is);
				iconView.setImage(saveIcon);
				
				loadedHBox.setMaxHeight(16);
				loadedHBox.setMaxWidth(100);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
	        		

	        if (typeOfNote == "Standard") {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXMLs/NoteTypes/StandardType.fxml"));

	        	try {
					root = loader.load();
					
		        	StandardTypeNoteController stnc = loader.getController();
		        	stnc.setMasterReference(mR);
		        	
		        	controller = stnc;
		        	
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
	        
	        else if (typeOfNote == "Daily") {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXMLs/NoteTypes/DailyType.fxml"));

	        	try {
					root = loader.load();
					
		        	DailyTypeNoteController dtnc = loader.getController();
		        	
		        	controller = dtnc;
		        	
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
	        
	        else if (typeOfNote == "Reading") {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXMLs/NoteTypes/ReadingType.fxml"));

	        	try {
					root = loader.load();
					
		        	ReadingTypeNoteController rtnc = loader.getController();
		        	
		        	controller = rtnc;
		        	
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
	        
	    }
	    
		public void createSystemFiles() {
			//format for local save:
			//root file
				//note file (title as name)
				//file for children of note
				//file for pinned notes
				//txt file for id
			
	    	File rootFile = new File(filePath);
	    	rootFile.mkdir();
	    		    	
	    	this.childrenFilePath = filePath + "/children";
	    	this.selfPath = filePath + "/self";
	    	
	    	String pinnedPath = filePath + "/pinned.txt";
	    	String idPath = filePath + "/id.txt";
	    	
	    	//if it is already made, essentially nothing happens here
	    	File childrenFile = new File(childrenFilePath);
	    	childrenFile.mkdir();
	    	
	    	File pinnedFile = new File(pinnedPath);
	    	
	    	//creates the pinnedFile if one does not exist
	    	if (pinnedFile.exists() == false) {
	    		try {
					pinnedFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
	    	}
	    	
	    	//reads the ids which are pinned if it does
	    	else {
    			try {
    	    		FileInputStream fis;
        			String contents = "";
        			
					fis = new FileInputStream(pinnedPath);
	    			Scanner in = new Scanner(fis);
	    			while (in.hasNext()) {
	    				contents += in.next();
	    			}
	    			//the ids are formatted as a comma separated list with no spaces on 1 line
	    			if (contents != "") {
	    				String[] listOfIDs = contents.split(",");
	    				for (String id: listOfIDs) {
	    					listOfPinnedIDs.add(Integer.valueOf(id));
	    				}
	    			}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
	    	}
	    		    	
	    	//if there is no id.txt file, needs to create one based on the lastUsedID.txt file in the data folder
	    	File idFile = new File(idPath);
	    	if (idFile.exists() == false) {
	    		try {
		    		FileInputStream fis;
	    			
	    			String lastUsedID = "";
	    			//populates lastUsedID based on the contents of the txt file
	    			fis = new FileInputStream(mR.getLastUsedIDFilePath());
	    			Scanner in = new Scanner(fis);
	    			while (in.hasNext()) {
	    				lastUsedID += in.next();
	    			}
	    				    			
		        	Integer onePlus = Integer.valueOf(lastUsedID) + 1;
	    			
	    			//writes to the id.txt file, the items ID
					idFile.createNewFile();
		        	Files.writeString(Paths.get(idPath), onePlus.toString());
		        	
		        	//updates the value here of what the notes id is
		        	id = onePlus;
		        	
		        	//needs to update the lastUsedID.txt file because the lastUsedID has changed
		        	Files.writeString(Paths.get(mR.getLastUsedIDFilePath()), onePlus.toString());

				} catch (IOException e) {
					e.printStackTrace();
				}
	    	}
	    	
	    	//if the file actually exists, then we just need to save the value here
	    	else {
    			//populates lastUsedID based on the contents of the txt file
    			try {
    	    		FileInputStream fis;
        			String recordedID = "";
        			
					fis = new FileInputStream(idPath);
	    			Scanner in = new Scanner(fis);
	    			while (in.hasNext()) {
	    				recordedID += in.next();
	    			}
	    			
	    			//sets the attribute to be the recorded ID value :)
	    			id = Integer.valueOf(recordedID);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
	    	}
		}
		
		//this will generate the filepath every time the function is called
		//this is to avoid problems which will be caused if a parent item or the item itself is renamed or altered
	    public String getCurrentFilePath() {
	    	
	    	TreeItem<Note> thisTreeItem = null;
	    	
	    	for (TreeItem<Note> treeItem : mR.getClassifierHandler().createListOfTreeItems()) {
	    		
	    		if (treeItem.getValue() == this) {
	    			
	    			thisTreeItem = treeItem;
	    			
	    		}
	    	}
	    	
	    	if (thisTreeItem != null) {
	    		
	    		String filePath = getName();
	    		
	    		while (thisTreeItem.getParent() != mR.getMainClassController().getNoteChooser().getRoot()) {
	    			filePath = thisTreeItem.getParent().getValue().getName() + "/children/" + filePath;
	    			
	    			thisTreeItem = thisTreeItem.getParent();
	    		}
	    		
	    		//once it has reached the root value
	    		
	    		FileInputStream fis;
	    		
				String directoryPath = "";
				
				//populates readInfo
				try {
					fis = new FileInputStream(mR.getDataFilePath());
					
					Scanner in = new Scanner(fis);
					while (in.hasNext()) {
						directoryPath += in.next();
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				filePath = directoryPath + "/" + filePath;
	    	}	    	

	        return filePath;
	    }

	    public String getName() {
	        return name;
	    }

		public boolean isFullSaved() {
			return isFullSaved;
		}

		public void setFullSaved(boolean isFullSaved) {
			this.isFullSaved = isFullSaved;
		}

		public String getChildrenFilePath() {			
			return childrenFilePath;
		}

		public void setChildrenFilePath(String childrenFilePath) {
			this.childrenFilePath = childrenFilePath;
		}

		public String getSelfPath() {
			return selfPath;
		}

		public void setSelfPath(String selfPath) {
			this.selfPath = selfPath;
		}

		public String getTypeOfNote() {
			return typeOfNote;
		}

		public AnchorPane getRoot() {
			return root;
		}
		
		public Object getController() {
			return controller;
		}

		public Double getScoreWithNoteBeingClassified() {
			return scoreWithNoteBeingClassified;
		}

		public void setScoreWithNoteBeingClassified(Double scoreWithNoteBeingClassified) {
			this.scoreWithNoteBeingClassified = scoreWithNoteBeingClassified;
		}

		public TreeViewCellController getCellController() {
			return cellController;
		}

		public void setCellController(TreeViewCellController cellController) {
			this.cellController = cellController;
		}

		public HBox getLoadedHBox() {
			return loadedHBox;
		}

		public String getFilePath() {
			return filePath;
		}

		public Integer getId() {
			return id;
		}

		public ArrayList<Integer> getListOfPinnedIDs() {
			return listOfPinnedIDs;
		}

		public void setListOfPinnedIDs(ArrayList<Integer> listOfPinnedIDs) {
			this.listOfPinnedIDs = listOfPinnedIDs;
		}

		public boolean isInitialized() {
			return isInitialized;
		}

		public void setInitialized(boolean isInitialized) {
			this.isInitialized = isInitialized;
		}
		
		
	}
	
	

	public class NoteControl extends HBox {
		
	    public NoteControl(Note note) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXMLs/TreeViewCell.fxml"));

	    	try {
				HBox loadedHBox = fxmlLoader.load();
				
				TreeViewCellController cellController = fxmlLoader.getController();
				note.setCellController(cellController);
												
				getChildren().clear();
				getChildren().addAll(loadedHBox.getChildren());
				
				ImageView iconView = (ImageView) getChildren().get(0);
				Label nameLabel = (Label) getChildren().get(1);
				
		        nameLabel.setText(note.getName());
		        

				InputStream is = new FileInputStream("src/images/SaveIcon.png");
				Image saveIcon = new Image(is);
				
				iconView.setImage(saveIcon);
				
				this.setMaxHeight(16);
				this.setMaxWidth(100);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	}
	
	
	
	public class NoteTreeCell extends TreeCell<Note>{
		
	    private ContextMenu treeViewContextMenu = new ContextMenu();
		
		public NoteTreeCell() {
			//creates contextMenu format
	        MenuItem addMenuItem = new MenuItem("New Note");
	        treeViewContextMenu.getItems().add(addMenuItem);
	        
	        MenuItem newDirectory = new MenuItem("New Directory");
	        treeViewContextMenu.getItems().add(newDirectory);
	                
	        MenuItem openNote = new MenuItem("Open");
	        treeViewContextMenu.getItems().add(openNote);
	        
	        MenuItem renameNote = new MenuItem("Rename");
	        treeViewContextMenu.getItems().add(renameNote);
	        
	        MenuItem deleteNote = new MenuItem("Delete");
	        treeViewContextMenu.getItems().add(deleteNote);
	        
	        MenuItem pinToOpened = new MenuItem("Pin To Opened");
	        treeViewContextMenu.getItems().add(pinToOpened);
	        
	        
	        //has to start disabled
	        deleteNote.setDisable(true);
	        //a listener for the selection model of the treeView
			treeView.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
				
				if (newValue == null) {
					deleteNote.setDisable(true);
				}
				
				else {
					deleteNote.setDisable(false);
				}
				
				}); 

			
	        //Open Note Menu Item Handler
	        openNote.setOnAction(new EventHandler<ActionEvent>() { 
			@Override
			public void handle(ActionEvent event) {
				TreeItem<Note> selectedTreeItem = treeView.getSelectionModel().getSelectedItem();			
				try {
					mR.openNote(selectedTreeItem);
				} catch (IOException e) {
					e.printStackTrace();
				}		
			}});
	        
			//New Note Menu Item Handler
	        addMenuItem.setOnAction(new EventHandler<ActionEvent>() {
	        	@Override
	            public void handle(ActionEvent event) {
	                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXMLs/NewNoteName.fxml"));
	        		try {
	        			TreeItem<Note> selectedTreeItem = treeView.getSelectionModel().getSelectedItem();
	        			
	        			if (selectedTreeItem == null) {
	        				selectedTreeItem = treeView.getRoot();
	        			}
	        			
	        			Note selectedNote = selectedTreeItem.getValue();
	        			
	        			functionBoxRepresenter = new FunctionBoxRepresenter();
	        			functionBoxRepresenter.newNoteProcess(selectedTreeItem);
	        			
	        			VBox newNoteName = fxmlLoader.load();
	        				        				        			
	        			functionBox.getChildren().clear();
	        			functionBox.getChildren().add(newNoteName);
	        			
	        			functionBox.setMinHeight(145);
	        			
        				AnchorPane comboBoxAnchor = (AnchorPane) newNoteName.getChildren().get(1);
        				
        				ComboBox comboBox = (ComboBox) comboBoxAnchor.getChildren().get(0);
        				
        				comboBox.getItems().add("Standard");
        				comboBox.getItems().add("Reading");
        				comboBox.getItems().add("Topic");
        				comboBox.getItems().add("Daily");
        				
        				AnchorPane textFieldAnchor = (AnchorPane) newNoteName.getChildren().get(0);
        				TextField textField = (TextField) textFieldAnchor.getChildren().get(0);
        					        			
	        			
	        			if (selectedTreeItem.getValue().getTypeOfNote() == "Standard") {
	        				//the note could be assigned to any type
	        				
	        				comboBox.getSelectionModel().select(0);
	        			}
	        			
	        			else if (selectedTreeItem.getValue().getTypeOfNote() == "Reading") {
	        				
	        				comboBox.getSelectionModel().select(1);
	        				
	        				comboBox.setDisable(true);
	        			}
	        			
	        			else if (selectedTreeItem.getValue().getTypeOfNote() == "Topic") {
	        				
	        				comboBox.getSelectionModel().select(2);
	        				
	        				comboBox.setDisable(true);
	        			}
	        			
	        			else if (selectedTreeItem.getValue().getTypeOfNote() == "Daily") {
	        				
	        				comboBox.getSelectionModel().select(3);
	        				
	        				comboBox.setDisable(true);
	        				
	        				//if the type is daily, we want the title to default to today's date
	        				setTitleAsMonth(textField);

	        			}
	        			
	        		
	        			VBox subVBox = (VBox) newNoteName.getChildren().get(2);
	        			HBox subHBox = (HBox) subVBox.getChildren().get(1);
	        			
	        			Button cancelButton = (Button) subHBox.getChildren().get(1);
	        			Button confirmButton = (Button) subHBox.getChildren().get(3);

	        			
	        			EventHandler<ActionEvent> onCancelPressed = (new EventHandler<ActionEvent>() { 
	        				@Override
	        				public void handle(ActionEvent arg0) {
	        					cancelFunctionBoxOperation();						
	        				}});
	        			cancelButton.setOnAction(onCancelPressed);
	        			
	        			EventHandler<ActionEvent> onConfirmPressed = (new EventHandler<ActionEvent>() { 
	        				@Override
	        				public void handle(ActionEvent arg0) {
	        					try {
									newNoteConfirmPushed(newNoteName, (String) comboBox.getSelectionModel().getSelectedItem(), textField.getText(), treeView.getSelectionModel().getSelectedItem());
								} catch (IOException e) {
									e.printStackTrace();
								}						
	        				}});
	        			confirmButton.setOnAction(onConfirmPressed);
	        			
	        			
	        			comboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
	        				
	        				if (newValue == "Daily") {
	        					setTitleAsMonth(textField);
	        				}
	        				
	        				}); 
	        			
	        			
	        		} catch (IOException e) {
	        			e.printStackTrace();
	        		}
	        	}
		        private void setTitleAsMonth(TextField textField) {
    				LocalDate date = LocalDate.now();
    				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    				
    				String theDate = formatter.format(date);
    				
    				String[] dateList = theDate.split("-");
    				
    				String day = dateList[0];
    				String month = dateList[1];
    				String year = dateList[2];
    				
    				if (day.charAt(0) == "0".charAt(0)) {
    					day = String.valueOf(day.charAt(1));
    				}
    				
    				if (month.charAt(0) == "0".charAt(0)) {
    					month = String.valueOf(month.charAt(1));
    				}
    				
    				
    				if (Integer.valueOf(month)== 1) {
    					month = "January";
    				}
    				
    				else if (Integer.valueOf(month)== 2) {
    					month = "February";
    				}
    				
    				else if (Integer.valueOf(month)== 3) {
    					month = "March";
    				}
    				
    				else if (Integer.valueOf(month)== 4) {
    					month = "April";
    				}
    				
    				else if (Integer.valueOf(month)== 5) {
    					month = "May";
    				}
    				
    				else if (Integer.valueOf(month)== 6) {
    					month = "June";
    				}
    				
    				else if (Integer.valueOf(month)== 7) {
    					month = "July";
    				}
    				
    				else if (Integer.valueOf(month)== 8) {
    					month = "August";
    				}
    				
    				else if (Integer.valueOf(month)== 9) {
    					month = "September";
    				}
    				
    				else if (Integer.valueOf(month)== 10) {
    					month = "October";
    				}
    				
    				else if (Integer.valueOf(month)== 11) {
    					month = "November";
    				}
    				
    				else if (Integer.valueOf(month)== 12) {
    					month = "December";
    				}

    				textField.setText(day + " " + month + " " + year);
		        }

	        	});
	        
	        
	        
	        //Same as new note but the current note is the root note
	        newDirectory.setOnAction(new EventHandler<ActionEvent>() { 
			@Override
			public void handle(ActionEvent event) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXMLs/NewNoteName.fxml"));
        		try {
        			TreeItem<Note> rootItem = treeView.getRoot();;        			
        			
        			Note selectedNote = rootItem.getValue();
        			
        			functionBoxRepresenter = new FunctionBoxRepresenter();
        			functionBoxRepresenter.newNoteProcess(rootItem);
        			
        			VBox newNoteName = fxmlLoader.load();
        				        				        			
        			functionBox.getChildren().clear();
        			functionBox.getChildren().add(newNoteName);
        			
        			functionBox.setMinHeight(140);
        			
    				AnchorPane comboBoxAnchor = (AnchorPane) newNoteName.getChildren().get(1);
    				
    				ComboBox comboBox = (ComboBox) comboBoxAnchor.getChildren().get(0);
    				
    				comboBox.getItems().add("Standard");
    				comboBox.getItems().add("Reading");
    				comboBox.getItems().add("Topic");
    				comboBox.getItems().add("Daily");
    				
    				AnchorPane textFieldAnchor = (AnchorPane) newNoteName.getChildren().get(0);
    				TextField textField = (TextField) textFieldAnchor.getChildren().get(0);
    				
        		
        			VBox subVBox = (VBox) newNoteName.getChildren().get(2);
        			HBox subHBox = (HBox) subVBox.getChildren().get(1);
        			
        			Button cancelButton = (Button) subHBox.getChildren().get(1);
        			Button confirmButton = (Button) subHBox.getChildren().get(3);
        			
        			EventHandler<ActionEvent> onCancelPressed = (new EventHandler<ActionEvent>() { 
        				@Override
        				public void handle(ActionEvent arg0) {
        					cancelFunctionBoxOperation();						
        				}});
        			cancelButton.setOnAction(onCancelPressed);
        			
        			EventHandler<ActionEvent> onConfirmPressed = (new EventHandler<ActionEvent>() { 
        				@Override
        				public void handle(ActionEvent arg0) {
        					try {
        						if ((String) comboBox.getSelectionModel().getSelectedItem() == null) {
    								newNoteConfirmPushed(newNoteName, "Standard", textField.getText(), rootItem);
        						}
        						else {
    								newNoteConfirmPushed(newNoteName, (String) comboBox.getSelectionModel().getSelectedItem(), textField.getText(), rootItem);

        						}
							} catch (IOException e) {
								e.printStackTrace();
							}						
        				}});
        			confirmButton.setOnAction(onConfirmPressed);
        		} catch (IOException e) {
        			e.printStackTrace();
        		}
        	}
		}
	        );
	      
	        
	        //Same as new note but the current note is the root note
	        deleteNote.setOnAction(new EventHandler<ActionEvent>() { 
			@Override
			public void handle(ActionEvent event) {
				if (treeView.getSelectionModel().getSelectedItem() != null) {
					mR.deleteNote(treeView.getSelectionModel().getSelectedItem());
				}
        		}
	        }
	        );
	        
	        
	        
	        
	        //Open Note Menu Item Handler
	        renameNote.setOnAction(new EventHandler<ActionEvent>() { 
			@Override
			public void handle(ActionEvent event) {
				TreeItem<Note> selectedTreeItem = treeView.getSelectionModel().getSelectedItem();			
				mR.renameNote(selectedTreeItem);	
			}});
	        
	        
	        
	        //Open Note Menu Item Handler
	        pinToOpened.setOnAction(new EventHandler<ActionEvent>() { 
			@Override
			public void handle(ActionEvent event) {
				mR.pinCurrentToOpened();		
			}});
	        
	        
	        
	        
	        
	        
	        this.setContextMenu(treeViewContextMenu);
		}
		
	    @Override
	    protected void updateItem(Note note, boolean empty) {
	        super.updateItem(note, empty);
	        if (note == null || empty) {
	            setGraphic(null);
	        } else {
	        	
	        	TreeViewCellController cellController = note.getCellController();
	        	
	        	setGraphic(note.getLoadedHBox());
	        	
	        	//NoteControl noteControl = new NoteControl(note);
	            //setGraphic(noteControl);
	        }
	    }
	    
	    
	    //occurs on every double click in the treeView
        @Override
        public void startEdit() {
            super.startEdit();
            
            //if the item is a leaf node, can open the note with a double click
            if (this.getTreeItem().getChildren().isEmpty()) {
            	openCurrentlySelectedNote();
            }
        }
	    
	}
	

	
	
	
	public void openCurrentlySelectedNote() {
		TreeItem<Note> selectedTreeItem = treeView.getSelectionModel().getSelectedItem();			
		try {
			mR.openNote(selectedTreeItem);
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	
	
	
	
	//must be called every time the functionBox changes form
	//stores attributes for the completion of functionBox functionalities
	private class FunctionBoxRepresenter {
		TreeItem<Note> encapsulatingNode;
		
		
		public void newNoteProcess(TreeItem<Note> encapsulatingNode) {
			this.encapsulatingNode = encapsulatingNode;
		}
		public TreeItem<Note> getEncapsulatingNode() {
			return encapsulatingNode;
		}
	}
	
	
	//the default function for whenever the cancel button is pushed in a functionBox menu
	public void cancelFunctionBoxOperation() {
		
		//resets the functionBoxRepresenter to null
		functionBoxRepresenter = null;
			
		resetFunctionBox();
	}
	public void resetFunctionBox() {
		//take a look at this later
			//later could set this to a default screen or menu that looks nice
		functionBox.getChildren().clear();
		functionBox.setMinHeight(0);
	}
	
	
	//for when confirm is pushed on the newNoteName menu
	public void newNoteConfirmPushed(VBox newNoteName, String typeOfNote, String title, TreeItem<Note> noteTreeItem) throws IOException {
	
		//this runs if there is no selected note, defaults it to the root
		if (noteTreeItem == null) {
			noteTreeItem = treeView.getRoot();
		}
		
		
		//creates list of note names in under the encapsulating node
		Note encapsulatingNote = noteTreeItem.getValue();
		ArrayList<String> listOfNoteNames = new ArrayList<String>();
		for (TreeItem<Note> treeItem: noteTreeItem.getChildren()) {
			Note currentNote = treeItem.getValue();
			
			String name = currentNote.getName();
			
			listOfNoteNames.add(name);
		}
				
		//cannot be named either of these
		if (title == noteTreeItem.getValue().getName() || title == "children") {
			title = "";
		}
		
		//will default to New Note
		//finds the lowest number New Note(x) available		
		if (title == "") {
			if (listOfNoteNames.contains("New Note")) {
				Integer num = 1;
				String current = "New Note(1)";
				while (listOfNoteNames.contains(current)) {
					num +=1;
					current = "New Note(" + num.toString() + ")";
				}
				title = current;
			}
			else {
				title = "New Note";
			}
		}
		
		//if there is a duplicate name
		if (listOfNoteNames.contains(title)) {			
			Integer num = 1;
			String current = title + "(1)";
			while (listOfNoteNames.contains(current)) {
				num +=1;
				current = title + "(" + num.toString() + ")";
			}
			title = current;
		}
		
		//creates new note and adds it to the tree
		String pathToNewNote = encapsulatingNote.getFilePath() + "/children/" + title;
		
		TreeItem<Note> newNote = new TreeItem<Note>(new Note(title, pathToNewNote, typeOfNote));
		
		mR.saveNote(newNote);
		
		//adds to treeView structure
		noteTreeItem.getChildren().add(newNote);
		
		mR.openNote(newNote);
		
		resetFunctionBox();
		
		mR.setTreeCellStyles();
	}
	
	
	
	public void createTreeStructureFromLocal(String pathToMaster) {
		TreeItem<Note> rootItem = new TreeItem<Note>(new Note("WebNotesSaveFile", pathToMaster, "Standard"));
        treeView.setRoot(rootItem);
        
		Note rootNote = rootItem.getValue();
		
		File masterFile = new File(rootNote.getChildrenFilePath());
		
		File[] listOfSubFiles = masterFile.listFiles();
		
		for (File file : listOfSubFiles) {
			createTreeStructureFromLocalHelper(file.getPath(), rootItem);
		}
	}
	
	public void createTreeStructureFromLocalHelper(String filePath, TreeItem<Note> parentItem) {
		
		//first needs to create the treeItem for this note
		File currentItemFile = new File(filePath);
		File[] listOfContents = currentItemFile.listFiles();
		
		ArrayList<String> listOfFileNames = new ArrayList<String>();
		for (File subfile : listOfContents) {
			listOfFileNames.add(subfile.getName());
		}
		
		//typeOfNote defaults to the parent's type
		String typeOfNote = parentItem.getValue().getTypeOfNote();
		//defaults to false, if there is no notetype file in the note, it will stay false, and type will default to parent's type
		Boolean fullSaved = false;
		
		if (listOfFileNames.contains("Standard")) {
			typeOfNote = "Standard";
			fullSaved = true;
		}
		
		else if (listOfFileNames.contains("Daily")) {
			typeOfNote = "Daily";
			fullSaved = true;
		}
		
		else if (listOfFileNames.contains("Reading")) {
			typeOfNote = "Reading";
			fullSaved = true;
		}
		
		//creates the currentItem and adds it to the children of the parent
		Note currentNote = new Note(currentItemFile.getName(),filePath, typeOfNote);			
		currentNote.setFullSaved(fullSaved);
		TreeItem<Note> currentItem = new TreeItem<Note>(currentNote);
		parentItem.getChildren().add(currentItem);
		
		//handles the children
		File currentItemChildrenFile = new File(filePath + "/children");
		File[] listOfChildren = currentItemChildrenFile.listFiles();
		
		//for each child of the current note, it runs the same recursive process
		for (File child : listOfChildren) {
			createTreeStructureFromLocalHelper(child.getPath(), currentItem);
		}
	}
	
	
	//run when the directories.txt file is empty
	public void handleWhenNoDirectoryHasBeenInitialized() {
		//removes the TreeView and adds the button to add an initial directory
		superParentOfTreeView.getChildren().clear();
		Button createDirectory = new Button("Add Directory");
		superParentOfTreeView.getChildren().add(createDirectory);
		
		EventHandler<ActionEvent> onButtonPressed = (new EventHandler<ActionEvent>() { 
			@Override
			public void handle(ActionEvent arg0) {
				   try {
					mR.createDirectory();;
				} catch (IOException e) {
					e.printStackTrace();
				}							
			}});
		
		createDirectory.setOnAction(onButtonPressed);
	}
	
	
	public void initialize() {
		
		//because not all classes in mR are created at same time
		//this is run after the instances exist so that they can be referenced

		this.treeView = mCC.getNoteChooser();
		this.functionBox = mCC.getFunctionBox();
		this.textSectionPane = mCC.getTextSectionPane();
		this.mainClassController = mR.getMainClassController();
			
			
        treeView.setShowRoot(false);		
		treeView.setEditable(true);
				
        treeView.setCellFactory(new Callback<TreeView<Note>,TreeCell<Note>>(){
            @Override
            public TreeCell<Note> call(TreeView<Note> p) {
                return new NoteTreeCell();
            }
        });
		
		
		FileInputStream fis;
		try {	
			String readInfo = "";
			
			//populates readInfo
			fis = new FileInputStream(mR.getDataFilePath());
			Scanner in = new Scanner(fis);
			while (in.hasNext()) {
				readInfo += in.next();
			}
											
			//if a directory has not been created
			if (readInfo == "") {					
				handleWhenNoDirectoryHasBeenInitialized();
			}
			
			//if directory has been initialized, reads directories into the TreeView
			else {
				createTreeStructureFromLocal(readInfo);
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}



	public static Set<String> getAllowedcharacters() {
		return ALLOWEDCHARACTERS;
	}
	
	
}
