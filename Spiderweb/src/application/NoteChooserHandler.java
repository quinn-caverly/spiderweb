package application;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;
import java.util.TreeMap;

import application.NoteChooserHandler.Note;
import fxmlcontrollers.TreeViewCellController;
import fxmlcontrollers.notetypes.DailyScrollController;
import fxmlcontrollers.notetypes.DailyTypeNoteController;
import fxmlcontrollers.notetypes.ReadingTypeNoteController;
import fxmlcontrollers.notetypes.StandardTypeNoteController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class NoteChooserHandler {	
	//mainReference
	MasterReference mR;
	
	private TreeView<Note> treeView;
	private ListView<Note> recencyList;
	private HBox functionBox;
	
	private static final Set<String> ALLOWEDCHARACTERS = Set.of(
			"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"
			, "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"
			, "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "(", ")", "-", "_", ".", "!", ":", ";"
		);


	public NoteChooserHandler(MasterReference mR) {
		this.mR = mR;
		
		this.treeView = mR.getMainClassController().getNoteChooser();
				
		this.functionBox = mR.getMainClassController().getFunctionBox();
	}
	
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

	public class Note {
		
		//an id that uniquely identifies the note because the filepath cannot uniquely identify the note because it changes
		private Integer id;
		//an Integer[] of the ids which are pinned to this note
		private ArrayList<Integer> listOfPinnedIDs;
		//this is the root which is loaded from fxml which is added to the tabPane on open
		private AnchorPane root;
		private Object controller;
		private TreeViewCellController cellController;
		private HBox treeViewHBox;
		private AnchorPane listViewAnchor;
	    private String name;
	    private final String typeOfNote;
	    private String childrenFilePath;
	    private String selfPath;
	    private Double scoreWithNoteBeingClassified;
	    private boolean isFullSaved = false;
	    //so that the note is not initialized twice
	    private boolean isInitialized = false;
	    private Integer databaseId;
	    private ArrayList<String> childrenIds;
	    private TreeMap<String, Double> classifierMap;
	    private TreeItem<Note> treeItem;
	    
	    
	    public Note(String name, String typeOfNote){	    	
	        this.name = name;
	        this.typeOfNote = typeOfNote;
	        
	        listOfPinnedIDs = new ArrayList<Integer>();
	        	        
	        //sets style in the treeView
	        if (typeOfNote != "DailyScroll") { //TODO static reference
	            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXMLs/TreeViewCell.fxml"));
		    	try {
					HBox loadedHBox = fxmlLoader.load();
					
					this.treeViewHBox = loadedHBox;
	
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
	        }
	        else { //sets style in the listView
	            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXMLs/ListViewCell.fxml"));
		    	try {
					AnchorPane listViewAnchor = fxmlLoader.load();
					
					this.listViewAnchor = listViewAnchor;
					
					Button button = (Button) listViewAnchor.getChildren().get(0);
					button.setOnAction(new EventHandler<ActionEvent>() { 
						@Override
						public void handle(ActionEvent event) {
							mR.openNote(Note.this);	
						}});
											
					if (getName().equals("temporary")) {
						LocalDate date = LocalDate.now();
						
						String day = String.valueOf(date.getDayOfMonth());
						String month = date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
						String year = String.valueOf(date.getYear());
						
						setName(day + " " + month + " " + year);
					}
					
					button.setStyle("-fx-font-size: 16px;");
					button.setText(getName());

					listViewAnchor.setMaxWidth(100);
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
	        		

	        if (typeOfNote == "Standard") {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXMLs/NoteTypes/StandardType.fxml"));

	        	try {
					root = loader.load();
		        	StandardTypeNoteController stnc = loader.getController();		        	
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
	        
	        else if (typeOfNote == "DailyScroll") {
	        		        	
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXMLs/NoteTypes/DailyScroll.fxml"));

	        	try {
					root = loader.load();
					
					DailyScrollController dsc = loader.getController();
							        	
		        	controller = dsc;
		        			        	
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
	        
	    }
	    

	    public String getName() {
	        return name;
	    }
	    
	    public void setName(String name) {
	    	this.name = name;
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

		public HBox getTreeViewHBox() {
			return treeViewHBox;
		}
		
		public AnchorPane getListViewAnchor() {
			return listViewAnchor;
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

		public TreeMap<String, Double> getClassifierMap() {
			return classifierMap;
		}

		public void setClassifierMap(TreeMap<String, Double> classifierMap) {
			this.classifierMap = classifierMap;
		}

		public Integer getDatabaseId() {
			return databaseId;
		}

		public void setDatabaseId(Integer databaseId) {
			this.databaseId = databaseId;
		}


		public ArrayList<String> getChildrenIds() {
			return childrenIds;
		}


		public void setChildrenIds(ArrayList<String> childrenIds) {
			this.childrenIds = childrenIds;
		}


		public TreeItem<Note> getTreeItem() {
			return treeItem;
		}


		public void setTreeItem(TreeItem<Note> treeItem) {
			this.treeItem = treeItem;
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
	
	
	public class RecencyListCell extends ListCell<Note>{

		public RecencyListCell() {
		}
		
		    @Override
		    protected void updateItem(Note note, boolean empty) {
		        super.updateItem(note, empty);
		        if (note == null || empty) {
		            setGraphic(null);
		        } else {	        	
		        	setGraphic(note.getListViewAnchor());
		        }
		    }
		    
	        @Override
	        public void startEdit() {
	            super.startEdit();
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
	        	setGraphic(note.getTreeViewHBox());
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
	
	
	//the default function for whenever the cancel button is pushed in a functionBox menu
	public void cancelFunctionBoxOperation() {
			
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
		
		
		Note newNote = new Note(title, typeOfNote);
		TreeItem<Note> newTreeItem = new TreeItem<Note>(newNote);
		newNote.setTreeItem(newTreeItem);
		
		mR.saveNote(newTreeItem);
		
		//adds to treeView structure
		noteTreeItem.getChildren().add(newTreeItem);
				
		/* TODO incomplete
		noteTreeItem.getChildren().sort(Comparator.comparing(i->((TreeItem<Note>) i).getValue().getTypeOfNote())
				.thenComparing(i->((TreeItem<Note>) i).getValue().getName()));
		*/
		
		//because the treeview structure has changed, sends a call to the database handler
		DatabaseHandler.startTreeViewSaveProtocol(mR);
		
		if (typeOfNote.equals("DailyScroll")) {
			mR.openNote(newNote);
		}
		else {
			mR.openNote(newTreeItem);
		}
				
		resetFunctionBox();
		
		mR.setTreeCellStyles();
	}
	
	
	//this will be run once we can already generate the list of tree items through the PipelineConsolidator
	public void createRecencyList() {
		ArrayList<TreeItem<Note>> listOfTreeItems = mR.getPipelineConsolidator().createListOfTreeItems();
				
    	for (TreeItem<Note> treeItem : listOfTreeItems) {
    		    		
    		recencyList.getItems().add(treeItem.getValue());
    		        	    		
    	}
	}
	
	public void initialize() {
		
		/*
		 * handles the classic treeView initialization,
		 * properties which could not be determined by Scene Builder in fxml
		 */
        treeView.setShowRoot(false);		
		treeView.setEditable(true);
				
        treeView.setCellFactory(lv -> new NoteTreeCell());
        
        /*
         * handles the recency tree initialization
         */
        mR.getMainClassController().getDailyPageList().setCellFactory(lv -> new RecencyListCell());
	}



	public static Set<String> getAllowedcharacters() {
		return ALLOWEDCHARACTERS;
	}
}
