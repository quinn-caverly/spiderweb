package handlers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import application.MasterReference;
import fxmlcontrollers.notetypes.DailyTypeNoteController;
import fxmlcontrollers.notetypes.ReadingTypeNoteController;
import fxmlcontrollers.notetypes.StandardTypeNoteController;
import handlers.NoteChooserHandler.Note;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import overriders.AnchorForReadingType;
import overriders.FluidImageWrapper;
import overriders.TypeTab;

//for handling operations in which information needs to be read and wrote to files from the local machine
public class ReadAndWriteHandler {
	MasterReference mR;
	
	public ReadAndWriteHandler(MasterReference mR) {
		this.mR = mR;
	}
	
	//has to formost check what the note type is
	//then send the note to its specific type for the process of saving it to the file
	public void startSaveToLocal(TreeItem<Note> treeItem) throws IOException {
		Note note = treeItem.getValue();
		note.setFullSaved(true);
		String type = note.getTypeOfNote();
				
		
		//saves the note parts that are specific to the type of the note
		
		if (type == "Standard") {
			saveStandardNote(treeItem);
		}
		
		else if (type == "Daily") {
			saveDailyNote(treeItem);
		}
		
		else if (type == "Reading") {
			saveReadingNote(treeItem);
		}
		
		//will write to the pinned notes file, this is the same regardless of the type
		
    	//needs to grab the current file path because the file or its parents could have been renamed at this point
    	String currentFilePath = note.getCurrentFilePath();
    	String idFilePath = currentFilePath + "/pinned.txt";
    	
    	ArrayList<Integer> listOfIDs = note.getListOfPinnedIDs();
    	
    	//the syntax for the id.txt file is a comma separated string of ids with no spaces
    	String stringList = "";
    	
    	for (Integer id : listOfIDs) {
    		stringList += id.toString() + ",";
    	}
    	
    	//removes the extra comma that will be created by the above for loop, if there are any contents
    	
    	if (stringList != "") {
        	stringList = stringList.substring(0, stringList.length()-1);
        	
        	Files.writeString(Paths.get(idFilePath), stringList);
    	}
	}
	
	
	
    //for saving the standard type notes to the local machine
    public void saveStandardNote(TreeItem<Note> currentTreeItem) throws IOException {
    	
		Note note = currentTreeItem.getValue();
    	StandardTypeNoteController sntc = (StandardTypeNoteController) note.getController();
    	
    	AnchorPane imagePool = sntc.getImagePool();
    	TextArea mainTextArea = sntc.getMainTextArea();
    	VBox collectorVBox = sntc.getCollectorVBox();
				
		String directory = note.getCurrentFilePath();
		String title = note.getName();
	
		//directory is the exact location to save the note
		//with the title of the note included
	
		Path highestPath = Paths.get(directory);
	    new File(highestPath.toString()).mkdir();
		    	
		    	
    	//creates the note save file and the children save file
    	Path secondHighestPath = Paths.get(highestPath.toString() + "/Standard");
    	new File(secondHighestPath.toString()).mkdir();
    	
    	Path childrenFilePath = Paths.get(highestPath.toString() + "/children");
    	new File(childrenFilePath.toString()).mkdir();
    	
    	
    	//Handles the text, which will be a normal txt file
    	
    	Path txtFilePath = Paths.get(secondHighestPath + "//" + title + ".txt");
    	(new File(txtFilePath.toString())).createNewFile();
    	Files.writeString(txtFilePath, mainTextArea.getText());	    	
    	
    	
    	//Handles the images
    	
    	//creates a directory for the images and their data
    	String pathToImagesDirectory = secondHighestPath + "//" + "Images";
    	
    	File imagesDirectory = new File(pathToImagesDirectory.toString());
    	
    	//resets the file every save to avoid overlap
    	if (imagesDirectory.exists()) {
    		deleteDirectory(imagesDirectory);
    	}
    	imagesDirectory.mkdir();
    	
    	//The X and Y coordinates of the placement event
    	//The actual image
    	
    	Integer index = 0;
    	String name = "image";
    	
    	for (Node node : imagePool.getChildren()) {
    		//creates a new directory for each image and their data
    		
    		String pathToCurrentImageFile = pathToImagesDirectory + "//"+ name + index.toString();
	    	new File(pathToCurrentImageFile).mkdir();
    		
    		FluidImageWrapper fiw = (FluidImageWrapper) node;
  
    		
    		//writes the location data to a txt file
	    	Files.writeString(Paths.get(pathToCurrentImageFile + "//" + "data.txt"), fiw.createStringForDataFile());
	    	
	    	//handes the image portion
	    	
	    	Image img = fiw.img;
	    	File imageFile = new File(pathToCurrentImageFile + "//" + name + ".png");
	    	ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", imageFile);
	    	
	    	index += 1;
	    	}
    	
    	
    	//Handles the Pyramid
    	
    	//creates a directory for the pyramid levels and their data
    	String pathToPyramidDirectory = secondHighestPath + "//" + "Pyramid";
    	
    	File pyramidDirectory = new File(pathToPyramidDirectory.toString());
    	
    	//resets the file every save to avoid overlap
    	if (pyramidDirectory.exists()) {
    		deleteDirectory(pyramidDirectory);
    	}
    	pyramidDirectory.mkdir();
    	
    	index = 0;
    	name = "level";
    	
    	for (Node node : collectorVBox.getChildren()) {
    		
    		HBox currentLevel = (HBox) node;
    		
			VBox mainVBox = (VBox) currentLevel.getChildren().get(1);
			TextArea textArea = (TextArea) mainVBox.getChildren().get(1);
	    	
	    	//writes context of textarea to the path
	    	Files.writeString(Paths.get(pyramidDirectory + "//" + name + index.toString() + ".txt"), textArea.getText());

    		index+=1;
    	}
    }
    
    
    
    //for saving the daily type notes to the local machine
    public void saveDailyNote(TreeItem<Note> currentTreeItem) throws IOException {
		Note note = currentTreeItem.getValue();
		DailyTypeNoteController dtnc = (DailyTypeNoteController) note.getController();
    	
    	TextArea calendarSection = dtnc.getCalendarSection();
    	TextArea brainstormingSection = dtnc.getBrainstormingSection();
    	TextArea toDoSection = dtnc.getToDoSection();
    	
    				
		String directory = note.getCurrentFilePath();
    	
    	//directory is the exact location to save the note
    	//with the title of the note included
    	
    	Path highestPath = Paths.get(directory);
		new File(highestPath.toString()).mkdir();
		    	
		    	
		//creates the note save file and the children save file
		Path secondHighestPath = Paths.get(highestPath.toString() + "/Daily");
		new File(secondHighestPath.toString()).mkdir();
		    	
		Path childrenFilePath = Paths.get(highestPath.toString() + "/children");
		new File(childrenFilePath.toString()).mkdir();
		    	
		//handles the text sections
		    	
	    Path brainstormingPath = Paths.get(secondHighestPath + "//" + "brainstorming.txt");
		(new File(brainstormingPath.toString())).createNewFile();
		Files.writeString(brainstormingPath, brainstormingSection.getText());	    	
		
	    Path calendarPath = Paths.get(secondHighestPath + "//" + "calendar.txt");
		(new File(calendarPath.toString())).createNewFile();
		Files.writeString(calendarPath, calendarSection.getText());	   
		
	    Path toDoPath = Paths.get(secondHighestPath + "//" + "toDo.txt");
		(new File(toDoPath.toString())).createNewFile();
		Files.writeString(toDoPath, toDoSection.getText());	   
    }
    
    
    //for saving the daily type notes to the local machine
    public void saveReadingNote(TreeItem<Note> currentTreeItem) throws IOException {
		Note note = currentTreeItem.getValue();
		ReadingTypeNoteController rtnc = (ReadingTypeNoteController) note.getController();	
		
		String directory = note.getCurrentFilePath();
		String pathToReading = directory + "/Reading";
		
    	//resets the file every save to avoid overlap
    	File readingDirectory = new File(pathToReading.toString());
    	if (readingDirectory.exists()) {
    		deleteDirectory(readingDirectory);
    	}
    	readingDirectory.mkdir();
		
    	
		//each node will have a type once it is cast, depending on the type, slightly different save procedures will be run
    	//in order, the nodes in the area will start at 0 then count up by integers. 
    		//analysis file will just have contents of analysis.txt
    		//quote will have quote.txt
    		//both file will have both text files contained
		
    	Integer index = 0;
    	
		for (Node node : rtnc.getCollectorVBox().getChildren()) {
			
			//each anchor is actually a AnchorForReadingType, after it is cast we can see what type it is, Quote, Reading, Both
			AnchorPane anchor = (AnchorPane) node;
			AnchorForReadingType anchorSpecial = (AnchorForReadingType) anchor;
			
			String type = anchorSpecial.getType();
			
			HBox mainHBox = (HBox) anchorSpecial.getChildren().get(0);
			
			String pathToCurrent = readingDirectory + "/" + index.toString();
			new File(pathToCurrent).mkdir();
			
			index += 1;
			
			if (type == "Analysis") {
				
				VBox mainVBox = (VBox) mainHBox.getChildren().get(1);
				TextArea analysisTextArea = (TextArea) mainVBox.getChildren().get(1);
				
			    Path pathToAnalysisTxT = Paths.get(pathToCurrent + "/" + "analysis.txt");
				(new File(pathToAnalysisTxT.toString())).createNewFile();
				Files.writeString(pathToAnalysisTxT, analysisTextArea.getText());	 
			}
			
			else if (type == "Quote") {
				
				VBox mainVBox = (VBox) mainHBox.getChildren().get(1);
				TextArea quoteTextArea = (TextArea) mainVBox.getChildren().get(1);
				
			    Path pathToQuoteTxt = Paths.get(pathToCurrent + "/" + "quote.txt");
				(new File(pathToQuoteTxt.toString())).createNewFile();
				Files.writeString(pathToQuoteTxt, quoteTextArea.getText());	 
			}
			
			//this will be if the type is quote, can only be 3 types, hard else clause for principle
			else {
				
				VBox quoteVBox = (VBox) mainHBox.getChildren().get(1);
				TextArea quoteTextArea = (TextArea) quoteVBox.getChildren().get(1);
				
				VBox analysisVBox = (VBox) mainHBox.getChildren().get(3);
				TextArea analysisTextArea = (TextArea) analysisVBox.getChildren().get(1);
				
			    Path pathToQuoteTxt = Paths.get(pathToCurrent + "/" + "quote.txt");
				(new File(pathToQuoteTxt.toString())).createNewFile();
				Files.writeString(pathToQuoteTxt, quoteTextArea.getText());	 
				
			    Path pathToAnalysisTxT = Paths.get(pathToCurrent + "/" + "analysis.txt");
				(new File(pathToAnalysisTxT.toString())).createNewFile();
				Files.writeString(pathToAnalysisTxT, analysisTextArea.getText());	 
			}
		}
    }
    
    
    
    
    
    
    
    
    /*
     * this method is called by mR, creates the reference to the actually contents of note so it can be opened quickly 
     * also has the task of populating the map of each note with the output from the pipeline
     */
    public void initializeAllNotes() {
    	ArrayList<TreeItem<Note>> listOfTreeItems = mR.getClassifierHandler().createListOfTreeItems();
    	
    	for (TreeItem<Note> treeItem : listOfTreeItems) {
    	
    		initializeNote(treeItem);
    		
    		mR.getPipeline().runThroughPipeline(treeItem);
    	}

    }
    
    public void initializeNote(TreeItem<Note> treeItem) {
    	
    	Note note = treeItem.getValue();
    	note.setInitialized(true);
    	String type = note.getTypeOfNote();
    	    	
    	if (type == "Standard") {
    		try {
				openStandardNote(treeItem);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	
    	else if (type == "Daily") {
    		try {
				openDailyNote(treeItem);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	
    	else if (type == "Reading") {    		
    		try {
				openReadingNote(treeItem);
			} catch (IOException e) {
				e.printStackTrace();
			}
    		
    		
    	}
    }
    
    
    
    
    //has methods for the different types of notes
    //the different methods will assign the values of the components of the scene
    //adds the scene to the tabPane
    public void startOpenFromLocal(TreeItem<Note> treeItem) {
    	Note note = treeItem.getValue();
    	AnchorPane root = note.getRoot();
    	
    	if (note.isInitialized() == false) {
        	//this must be called again because if a newly created note is opened, the initialization process will not have been run
        	initializeNote(treeItem);
    	}

    	//we do not need to actually open the note or read from file, because all notes are already read from file on initialization
    	//all this does in practice (after the modification) is add the pre-loaded note to the tabpane
    	//(notes had to be pre-initialized for the purpose of the text preview in the pinned notes feature) :)
    	
    	TabPane noteTabPane = mR.getMainClassController().getNoteTabPane();
    	
    	
    	root.maxWidthProperty().bind(noteTabPane.widthProperty());
    	root.minWidthProperty().bind(noteTabPane.widthProperty());
    	
    	root.maxHeightProperty().bind(noteTabPane.heightProperty());
    	root.minHeightProperty().bind(noteTabPane.heightProperty());
    	
    	TypeTab newTab = new TypeTab(note.getName(), root, treeItem);
    	
    	
    	noteTabPane.getTabs().add(newTab);
    }
    
    //reads the local file if the note isFullSaved
    public void openStandardNote(TreeItem<Note> treeItem) throws IOException {      	
    	    	
		Note note = treeItem.getValue();
				
		StandardTypeNoteController sntc = (StandardTypeNoteController) note.getController();
		
    	AnchorPane imagePool = sntc.getImagePool();
    	TextArea mainTextArea = sntc.getMainTextArea();
    	TextArea titleArea = sntc.getTitleArea();
    	
		//will need to read info from system
		if (note.isFullSaved()) {
			String directory = note.getCurrentFilePath();
			String title = note.getName();
			
			String pathToSelf = directory + "/Standard";
			
			String pathToTxt = pathToSelf + "/" + title + ".txt";
			String contentsOfTxt = Files.readString(Paths.get(pathToTxt));
			
			String pathToImagesDirectory = pathToSelf + "/Images";
			File imagesDirectory = new File(pathToImagesDirectory);
						
			//handles the imagePool
			imagePool.getChildren().clear();
			File[] listOfImageFiles = imagesDirectory.listFiles();
			
			if (listOfImageFiles != null) {				
				for (File currentImage: listOfImageFiles) {
					String currentImageDirectory = currentImage.getAbsolutePath();
										
					File dataFile = new File(currentImageDirectory + "/data.txt");
					File imageFile = new File(currentImageDirectory + "/image.png");
					
					String data = Files.readString(Paths.get(dataFile.getAbsolutePath()));
					Image image = new Image(imageFile.toURI().toString());
					
					FluidImageWrapper fiw = new FluidImageWrapper(imagePool, image);
					
					fiw.placeFromDataFile(data);
				}
			}
			
			//handles the pyramid
			
			String pathToPyramid = pathToSelf + "/Pyramid";
			File pyramidDirectory = new File(pathToPyramid);
			
			if (pyramidDirectory.exists()) {
									
				File[] listOfPyramidFiles = pyramidDirectory.listFiles();
				
				Integer numberOfLevels = listOfPyramidFiles.length;
				
				Integer index = 0;
				String name = "level";
								
				while (index < numberOfLevels) {
					
					String currentFileName = name + index.toString() + ".txt";
										
					for (File file : listOfPyramidFiles) {

						if (file.getName().toString().equals(currentFileName.toString())) {
														
							TextArea currentLevelTextArea = sntc.addLevelButtonPushed();
							
							currentLevelTextArea.setText(Files.readString(Paths.get(file.getAbsolutePath())));	
						}
					}
					index += 1;
				}
			}
			
			mainTextArea.setText(contentsOfTxt);
			titleArea.setText(title);
			
			
			sntc.chooseInitiallySelectedTab();
			sntc.addStarsToTabsWithContents();
		}
		
		//if the note is not full saved, then the only thing that happens is the titleArea is set to the title of the note
		else {

		titleArea.setText(note.getName());
		
		}
    }
    
    public void openDailyNote(TreeItem<Note> treeItem) throws IOException {      	
    	
		Note note = treeItem.getValue();
				
		DailyTypeNoteController dtnc = (DailyTypeNoteController) note.getController();
		
    	TextArea calendarSection = dtnc.getCalendarSection();
    	TextArea brainstormingSection = dtnc.getBrainstormingSection();
    	TextArea toDoSection = dtnc.getToDoSection();
    	
		//will need to read info from system
		if (note.isFullSaved()) {
			String directory = note.getCurrentFilePath();
			
			String pathToSelf = directory + "/Daily";
			
			
			String pathToCalendar = pathToSelf + "/" + "calendar.txt";
			String pathToToDo = pathToSelf + "/" + "toDo.txt";
			String pathToBrainstorming = pathToSelf + "/" + "brainstorming.txt";
			
			String contentsOfCalendar = Files.readString(Paths.get(pathToCalendar));
			String contentsOfToDo = Files.readString(Paths.get(pathToToDo));
			String contentsOfBrainstorming = Files.readString(Paths.get(pathToBrainstorming));

			
			calendarSection.setText(contentsOfCalendar);
			toDoSection.setText(contentsOfToDo);
			brainstormingSection.setText(contentsOfBrainstorming);
		}
		//if the note is not full saved, nothing needs to happen, sections all start empty
    }
    
    
    
    public void openReadingNote(TreeItem<Note> treeItem) throws IOException {
		Note note = treeItem.getValue();
		ReadingTypeNoteController rtnc = (ReadingTypeNoteController) note.getController();
		
		if (note.isFullSaved()) {
			
			String pathToReading = note.getCurrentFilePath() + "/Reading";
			
			File readingFile = new File(pathToReading);
			File[] listOfIndexedFiles = readingFile.listFiles();
			
			for (File file : listOfIndexedFiles) {
				
				File[] listOfTxtFiles = file.listFiles();
				ArrayList<String> listOfTxTNames = new ArrayList<String>();
				
				for (File txtFile : listOfTxtFiles) {
					listOfTxTNames.add(txtFile.getName());
				}
				
				
				
				//if it is a both type node
				if (listOfTxTNames.contains("analysis.txt") && listOfTxTNames.contains("quote.txt")) {
					
					String pathToAnalysisTxT = file.getPath() + "/analysis.txt";
					String contentsOfAnalysis = Files.readString(Paths.get(pathToAnalysisTxT));
					
					String pathToQuoteTxT = file.getPath() + "/quote.txt";
					String contentsOfQuote = Files.readString(Paths.get(pathToQuoteTxT));
					
					//this adds a new both node and returns the textArea references for convenience here
					ArrayList<TextArea> textAreaList = rtnc.pushBothButton();

					//the indexes are hard-coded here
					TextArea quoteTextArea = textAreaList.get(0);
					TextArea analysisTextArea = textAreaList.get(1);
					
					quoteTextArea.setText(contentsOfQuote);
					analysisTextArea.setText(contentsOfAnalysis);
				}
				
				else if (listOfTxTNames.contains("analysis.txt")) {
					
					String pathToAnalysisTxT = file.getPath() + "/analysis.txt";
					String contentsOfAnalysis = Files.readString(Paths.get(pathToAnalysisTxT));
					
					TextArea analysisTextArea = rtnc.pushAnalysisButton();
					
					analysisTextArea.setText(contentsOfAnalysis);
				}
				
				//then it must be a quote type
				else {
					
					String pathToQuoteTxT = file.getPath() + "/quote.txt";
					String contentsOfQuote = Files.readString(Paths.get(pathToQuoteTxT));
					
					TextArea quoteTextArea = rtnc.pushQuoteButton();
					
					quoteTextArea.setText(contentsOfQuote);
				}
			}
		}
    	//nothing needs to happen if it is not full saved
    }
    
    
    
    public void startRenameNote(TreeItem<Note> treeItem, TextField textField) {
    	
		Note noteToBeRenamed = treeItem.getValue();
		
		//encapsulatingFilePath includes the \ at the end
		Integer titleLength = noteToBeRenamed.getName().length();
		String encapsulatingFilePath = noteToBeRenamed.getCurrentFilePath().substring(0, noteToBeRenamed.getCurrentFilePath().length() - titleLength);
		
		
		File newPotentialFile = new File(encapsulatingFilePath + textField.getText());
		
		//handles two cases which would cause system to fail
		
		boolean failed = false;

		
		if (newPotentialFile.exists()) {	
			textField.clear();
			textField.setPromptText("Note Already Exists");
			failed = true;
		}
		
		String[] strArray = textField.getText().split("");
		
		for (String str: strArray) {
			str = str.toLowerCase();
			if (mR.getNoteChooserHandler().getAllowedcharacters().contains(str) == false) {
				failed = true;
				break;
			}
		}
		if (failed == true) {
			textField.clear();
			textField.setPromptText("No Illegal Characters");
		}
		
		else {
			
		File currentFile = new File(noteToBeRenamed.getCurrentFilePath());
		currentFile.renameTo(newPotentialFile);
		
		if (noteToBeRenamed.getTypeOfNote() == "Standard" || noteToBeRenamed.isFullSaved()) {
			File txtFileToBeRenamed = new File(noteToBeRenamed.getCurrentFilePath() + "/self/" + noteToBeRenamed.getName() + ".txt");
			File txtFileToRenameInto = new File(noteToBeRenamed.getCurrentFilePath() + "/self/" + textField.getText() + ".txt");
			
			txtFileToBeRenamed.renameTo(txtFileToRenameInto);
		}

		
		//changes the treeItem to match the new value
		treeItem.setValue(mR.getNoteChooserHandler().new Note(textField.getText(), encapsulatingFilePath + textField.getText(), noteToBeRenamed.getTypeOfNote()));
		
		//reopens the note so that the title and text area now are consistent
		try {
			mR.openNote(treeItem);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//removes the function box
		mR.getNoteChooserHandler().resetFunctionBox();
		
		//applies the set styles so new note conforms
		//(inefficient)
		mR.setTreeCellStyles();
		
		//opens and closes so that everything readjusts
		
		//for standard notes, adjusts the title area
		if (treeItem.getValue().getTypeOfNote() == "Standard") {
			
			StandardTypeNoteController stnc = (StandardTypeNoteController) treeItem.getValue().getController();
			
			stnc.getTitleArea().setText(textField.getText());
		}
		}
    }
    
    
	
	
	public void deleteNoteOnLocal(TreeItem<Note> treeItem) {
		
		File file = new File(treeItem.getValue().getCurrentFilePath());
		
		if (file.exists()) {
			
			deleteDirectory(file);
			
		}
	}
    
    
	//directories cannot be deleted if they contain files
	//this is a recursive workaround
	public static void deleteDirectory(File file) {
		//deletes children
		if (file.isDirectory()) {
			File[] children = file.listFiles();
			if (children != null) {
				for (File entry: children) {
					deleteDirectory(entry);
				}
			}
		}
		//deletes the file
		file.delete();
	}
}
