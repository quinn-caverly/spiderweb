package handlers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import application.MasterReference;
import fxmlcontrollers.notetypes.DailyTypeNoteController;
import fxmlcontrollers.notetypes.ReadingTypeNoteController;
import fxmlcontrollers.notetypes.StandardTypeNoteController;
import handlers.NoteChooserHandler.Note;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import overriders.AnchorForReadingType;

/*
 * DatabaseHandler will have only static methods in order to create an implementation
 * which is easiest to modify and simplest to understand abstractly
 */
public final class DatabaseHandler {
	
	//this is used to create an instance of the Connection class,
	//does not make a final version of connection because this may cause problems
	//connection should be created and closed as queries to the database are needed
	private static final String urlForConnection = "jdbc:derby:derbydb;create=true";

	/*
	 * this will be the method which implements the schema design of the db
	 * the schema is defined by the draw.io file, spiderwebSchema.drawio
	 * 
	 * every other method will reference the schema designed here for methods,
	 * other methods will not have to be changed here because core implementation should not change
	 */
	public static void initializeDatabase() {
		
		try {
			Connection connection = DriverManager.getConnection(urlForConnection);
		    Statement statement = connection.createStatement();
		    
		    //creates Page table
		    statement.executeUpdate("CREATE TABLE Page (Id INT NOT NULL GENERATED ALWAYS AS IDENTITY, Type VARCHAR(20), Timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
		    
		    //creates StandardPage table and the associated PyramidContainer table
		    statement.executeUpdate("CREATE TABLE StandardPage (Id INT, Title VARCHAR(1000), BoxContents VARCHAR(30000),"
		    		+ " PyramidSequence VARCHAR(1000))");
		    
		    statement.executeUpdate("CREATE TABLE PyramidContainer (Id INT, Contents VARCHAR(10000))");
		    
		    //creates LegacyDailyPage table, is legacy because I intend to replace the daily page with a different format page
		    statement.executeUpdate("CREATE TABLE LegacyDailyPage (Id INT, Title VARCHAR(1000), LeftBoxContents VARCHAR(30000),"
		    		+ " TopRightBoxContents VARCHAR(30000), BottomRightBoxContents VARCHAR(30000))");
		    
		    //creates ReadingPage table and associated AnalysisContainer, QuoteContainer, AnalysisAndQuoteContainer
		    statement.executeUpdate("CREATE TABLE ReadingPage (Id INT, Title VARCHAR(1000), ContainerSequence VARCHAR(1000))");
		    //the ids of the containers will contain a letter depending on which table it corresponds to
		    //the id will have to be cut out before searching that table for the id
		    statement.executeUpdate("CREATE TABLE AnalysisContainer (Id INT NOT NULL GENERATED ALWAYS AS IDENTITY, Contents VARCHAR(30000))");// A
		    statement.executeUpdate("CREATE TABLE QuoteContainer (Id INT NOT NULL GENERATED ALWAYS AS IDENTITY, Contents VARCHAR(30000))");// Q
		    statement.executeUpdate("CREATE TABLE AnalysisAndQuoteContainer (Id INT NOT NULL GENERATED ALWAYS AS IDENTITY, AnalysisContents VARCHAR(30000), QuoteContents VARCHAR(30000))");// B
		    statement.executeUpdate("CREATE TABLE SectionContainer (Id INT NOT NULL GENERATED ALWAYS AS IDENTITY, Name VARCHAR(1000))");// S
		    statement.executeUpdate("CREATE TABLE ChapterContainer (Id INT NOT NULL GENERATED ALWAYS AS IDENTITY, Name VARCHAR(1000))");// C

		    
		    //for saving the treeView structure
		    statement.executeUpdate("CREATE TABLE TreeViewStructure (Id INT, ChildrenSequence VARCHAR(1000), IsRoot CHAR(1))");
		    
		    statement.executeUpdate("CREATE TABLE DailyScroll (Id INT NOT NULL GENERATED ALWAYS AS IDENTITY, Date VARCHAR(10))");
		    
		    statement.executeUpdate("CREATE TABLE LongTermGoalTable (Description VARCHAR(1000), Day INT, Month INT, CalendarYear INT)");

		    statement.executeUpdate("CREATE TABLE BookDeskTable (Title VARCHAR(1000))");
		    
		    statement.executeUpdate("CREATE TABLE WeeklyGoalTable (Title VARCHAR(1000), Repetitions Int, Remaining Int)");
		    
		    connection.close();
		    
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	public static void startSaveProtocol(MasterReference mR) {
		
		ArrayList<TreeItem<Note>> treeList = mR.getPipelineConsolidator().createListOfTreeItemsWithRootNode();
		
		/*
		 * this will save each page in the treeView to the database, and also gives each page an Id
		 */
		for (TreeItem<Note> treeItem : treeList) {
			DatabaseHandler.savePageToDatabase(treeItem.getValue());
		}
		
		startTreeViewSaveProtocol(mR);
		
	}
	
	/*
	 * first saves all of the individual pages which are present in the treeView
	 * then, it saves the structure of the treeView
	 */
	public static void startTreeViewSaveProtocol(MasterReference mR) {
		
		ArrayList<TreeItem<Note>> treeList = mR.getPipelineConsolidator().createListOfTreeItemsWithRootNode();
		
		/*
		 * once each treeView page is saved, we use a breadth first search style implementation
		 * in order to save the treeView structure in a table
		 * 
		 * start with root note, its children will be saved, then we do its children, the children's children will be saved, etc.
		 * CREATE TABLE TreeViewStructure (Id INT, ChildrenSequence VARCHAR(1000), IsRoot BIT)
		 * 
		 * the ChildrenSequence will be a list of the notes children, unordered
		 */
		
		Connection connection;
		try {
			connection = DriverManager.getConnection(urlForConnection);
		    Statement statement = connection.createStatement();
		    
		    //creates a new table instead of just dropping old one incase something goes wrong
		    statement.execute("CREATE TABLE NewTreeViewStructure (Id INT, ChildrenSequence VARCHAR(1000), IsRoot SMALLINT)");
		    
			TreeItem<Note> rootNode = mR.getMainClassController().getNoteChooser().getRoot();
					    
			for (TreeItem<Note> treeItem : treeList) {
				
				String bitValue;
				if (treeItem.equals(rootNode)) {
					bitValue = "1";
				}
				else {
					bitValue = "0";
				}
				
								
				String childrenSequence = "";
				
				for (TreeItem<Note> childTreeItem : treeItem.getChildren()) {
					childrenSequence += childTreeItem.getValue().getDatabaseId() + ",";
				}
				//removes the last character because there will be an extra ","
				if (childrenSequence.length() > 0) {
					childrenSequence = childrenSequence.substring(0, childrenSequence.length() - 1);
				}
				
				//if it has no children, just lets that value be null in the database
				if (childrenSequence == "") {
				    statement.execute("INSERT INTO NewTreeViewStructure (Id, IsRoot) VAlUES ("
				    + treeItem.getValue().getDatabaseId().toString() + ", " + bitValue + ")");
				}
				else {
				    statement.execute("INSERT INTO NewTreeViewStructure (Id, ChildrenSequence, IsRoot) VAlUES ("
				    + treeItem.getValue().getDatabaseId().toString() + ", '" + childrenSequence + "', " + bitValue + ")");
				}
			}
			
			statement.execute("DROP TABLE TreeViewStructure");
			statement.execute("RENAME TABLE NewTreeViewStructure TO TreeViewStructure");
			
			connection.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * all pages have the same general format and will be saved to the Page table first
	 * then will be sent to different methods for type specific saving
	 * 
	 * if a page has an id, then that means that it has already been saved to database,
	 * and therefore already has a time value so the time value will not be overridden
	 * in the case of a page already having an id, it will only be saved to the type specific table
	 */
	public static void savePageToDatabase(Note page) {
						
		Boolean failed = false;
		//this will run if it needs to be saved to the general Page table
		if (page.getDatabaseId() == null) {
			
			try {
				Connection connection = DriverManager.getConnection(urlForConnection);
			    PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Page (Type) VAlUES ('" + page.getTypeOfNote() + "')", Statement.RETURN_GENERATED_KEYS);
			    
			    preparedStatement.executeUpdate();
			    
			    //will have to query the database to see which id was generated for the page
			    
		        try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
		            if (generatedKeys.next()) {
		                page.setDatabaseId(generatedKeys.getInt(1));
		            }
		            else {
		                throw new SQLException("Creating page failed, no ID obtained.");
		            }
		        }
			    
		        connection.close();

			} catch (SQLException e) {
				failed = true; //this is just for safety, should never be run as the database will embedded and should never be unable to connect
				e.printStackTrace();
			}
		}
		
		//now it saves to the specific type database, if a page did not have an id previously, it does now
		if (failed == false) {
			
			if (page.getTypeOfNote() == "Standard") {
				saveToStandardPage(page);
			}
			else if (page.getTypeOfNote() == "Daily") {
				saveToLegacyDailyPage(page);
			}
			else if (page.getTypeOfNote() == "Reading") {
				saveToReadingPage(page);
			}
		}
		
	}
	
	/*
	 * needs values; (Id INT, Title VARCHAR(1000), BoxContents VARCHAR(30000), PyramidSequence VARCHAR(1000))
	 * 
	 * will need to account for possibility of the note already being saved in the database,
	 * then it will overwrite the values where the Id matches...
	 * 
	 * UNFINISHED: will do PyramidSequence later, probably at same time that I do readingPage DELETE THIS COMMENT
	 * DELTE THIS DELTE THIS DELTE THIS DELTE THIS DELTE THIS DELTE THIS DELTE THIS DELTE THIS 
	 */
	private static void saveToStandardPage(Note standardPage) {
				
		try {
			Connection connection = DriverManager.getConnection(urlForConnection);
		    Statement statement = connection.createStatement();
		    
		    StandardTypeNoteController stnc = (StandardTypeNoteController) standardPage.getController();
		    TextArea mainTextArea = stnc.getMainTextArea();
		    
		    String BoxContents = prepareStringForSQL(mainTextArea.getText());
		    
		    //first we will check if the entry with the id already exists in the database
		    Boolean existsInDatabase = false; 
		    
		    ResultSet resultSet = statement.executeQuery("SELECT COUNT(1) FROM StandardPage WHERE Id = " + standardPage.getDatabaseId().toString());
		    while (resultSet.next()) {
		        Integer count = resultSet.getInt(1);
		        if (count == 1) { existsInDatabase = true; }
		    }
		    
		    //if it exists in database, then we update the value
		    if (existsInDatabase == true) {
		    	statement.executeUpdate("UPDATE StandardPage SET Title = '" + prepareStringForSQL(standardPage.getName())
		    	+ "', BoxContents = '" + BoxContents + "' WHERE Id = " + standardPage.getDatabaseId().toString());
		    }
		    
		    //if it does not exist in database, just add the row as normal
		    else {
		    	try {
					statement.executeUpdate("INSERT INTO StandardPage (Id, Title, BoxContents) VALUES ("
					+ standardPage.getDatabaseId() + ", '" + prepareStringForSQL(standardPage.getName()) + "', '" + BoxContents + "')");
		    	}
		    	catch (SQLException exception) {
		    		exception.printStackTrace();
		    	}
		    }
		    
			connection.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * needs values; (Id INT, Title VARCHAR(1000), LeftBoxContents VARCHAR(30000),
	 * TopRightBoxContents VARCHAR(30000), BottomRightBoxContents VARCHAR(30000))
	 * 
	 * will need to account for possibility of the note already being saved in the database,
	 * then it will overwrite the values where the Id matches...
	 */
	private static void saveToLegacyDailyPage(Note legacyDailyPage) {
				
		try {
			Connection connection = DriverManager.getConnection(urlForConnection);
		    Statement statement = connection.createStatement();
		    
		    DailyTypeNoteController dtnc = (DailyTypeNoteController) legacyDailyPage.getController();

		    //the contents boxes correspond to the various sections, proved via FXML
	    	String LeftBoxContents = prepareStringForSQL(dtnc.getBrainstormingSection().getText());
	    	String TopRightBoxContents = prepareStringForSQL(dtnc.getToDoSection().getText());
	    	String BottomRightBoxContents = prepareStringForSQL(dtnc.getCalendarSection().getText());   	
	    		    		    			    
		    //first we will check if the entry with the id already exists in the database
		    Boolean existsInDatabase = false; 
		    
		    ResultSet resultSet = statement.executeQuery("SELECT COUNT(1) FROM LegacyDailyPage WHERE Id = " + legacyDailyPage.getDatabaseId().toString());
		    while (resultSet.next()) {
		        Integer count = resultSet.getInt(1);
		        if (count == 1) { existsInDatabase = true; }
		    }
		    
		    //if it exists in database, then we update the value
		    if (existsInDatabase == true) {
		    	statement.executeUpdate("UPDATE LegacyDailyPage SET Title = '" + prepareStringForSQL(legacyDailyPage.getName())
		    	+ "', TopRightBoxContents = '" + TopRightBoxContents + "', BottomRightBoxContents = '" + BottomRightBoxContents
		    	+ "', LeftBoxContents = '" + LeftBoxContents + "' WHERE Id = " + legacyDailyPage.getDatabaseId().toString());
		    }
		    
		    //if it does not exist in database, just add the row as normal
		    else {
		    	try {
					statement.executeUpdate("INSERT INTO LegacyDailyPage (Id, Title, LeftBoxContents, TopRightBoxContents, BottomRightBoxContents) VALUES ("
					+ legacyDailyPage.getDatabaseId() + ", '" + prepareStringForSQL(legacyDailyPage.getName()) + "', '" + LeftBoxContents
					+ "', '" + TopRightBoxContents + "', '" + BottomRightBoxContents +"')");
		    	}
		    	catch (SQLException exception) {
		    		exception.printStackTrace();
		    	}
		    }
		    
			connection.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * needs values; ReadingPage (Id INT, Title VARCHAR(1000), ContainerSequence VARCHAR(1000))
	 * 
	 * will need to account for possibility of the note already being saved in the database,
	 * then it will overwrite the values where the Id matches...
	 * 
	 * the most logic needed in this function will be the creation of the container sequence
	 * the containers will be added to their individual tables, the id will be saved
	 * then the one letter indicators will be added to the front of the ids
	 * the ids will then be turned into an ordered list which will be saved as container sequence
	 */
	private static void saveToReadingPage(Note readingPage) {
				
		try {
			Connection connection = DriverManager.getConnection(urlForConnection);
		    Statement statement = connection.createStatement();
		    	    		    		    			    
		    //first we will check if the entry with the id already exists in the database
		    Boolean existsInDatabase = false; 
		    
		    ResultSet resultSet = statement.executeQuery("SELECT COUNT(1) FROM ReadingPage WHERE Id = " + readingPage.getDatabaseId().toString());
		    while (resultSet.next()) {
		        Integer count = resultSet.getInt(1);
		        if (count == 1) { existsInDatabase = true; }
		    }
		    
		    /*
		     * if it already exists in the database, then we need to fetch the previously defined container sequence
		     * then we delete the previously defined containers in their respective tables
		     * then we update the character sequence with the newly generated one
		     */
		    if (existsInDatabase == true) {
		    	
		    	/*
		    	 * first we need to delete the previously generated rows in the Respective tables
		    	 */
			    ResultSet resSet = statement.executeQuery("SELECT * FROM ReadingPage WHERE Id = " + readingPage.getDatabaseId().toString());
			    
			    String containerSequenceString = null; //this being null is fine because if this throws a type it would be best to see it clearly
			    while (resSet.next()) {
			    	containerSequenceString = resSet.getString("ContainerSequence");
			    }
			    
				ArrayList<ArrayList<String>> containerSequence = decryptContainerSequence(containerSequenceString);

			    for (ArrayList<String> innerList : containerSequence) {
			    	deleteRowInRespectiveReadingPageTable(innerList);
			    }
			    
			    /*
			     * now we need to generate a new container sequence
			     * then we update the row in ReadingPage table, with it keeping the same Id
			     */
				
				ArrayList<ArrayList<String>> newContainerSequence = saveToRespectiveReadingPageTables(readingPage);
				String newContainerSequenceString = encryptContainerSequence(newContainerSequence);
				
		    	statement.executeUpdate("UPDATE ReadingPage SET Title = '" + prepareStringForSQL(readingPage.getName())
		    	+ "', ContainerSequence = '" + newContainerSequenceString + "' WHERE Id = " + readingPage.getDatabaseId().toString());
		    }
		    
		    /*
		     * if it does not exist in database, first we add the containers to their respective tables
		     * this gives us the container sequence, we decrypt the container sequence so SQL understands it
		     * then we add the information to the readingPage table
		     */
		    else {
		    	
				ArrayList<ArrayList<String>> containerSequence = saveToRespectiveReadingPageTables(readingPage);
				String containerSequenceString = encryptContainerSequence(containerSequence);
				
				statement.executeUpdate("INSERT INTO ReadingPage (Id, Title, ContainerSequence) VALUES ("
				+ readingPage.getDatabaseId().toString() + ", '" + prepareStringForSQL(readingPage.getName()) + "', '" + containerSequenceString + "')");
		    }
		    
			connection.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * this function will handle the saving of the containers to their respective tables
	 * then it will return the container sequence to be saved in the main table
	 */
	private static ArrayList<ArrayList<String>> saveToRespectiveReadingPageTables(Note readingPage) throws SQLException {
		
		Connection connection = DriverManager.getConnection(urlForConnection);

	    ReadingTypeNoteController rtnc = (ReadingTypeNoteController) readingPage.getController(); 	
	    
	    //for each container, it will add a row to its respective table
	    //then it will add its id with its respective initial letter to this arraylist as a String
	    ArrayList<ArrayList<String>> containerSequence = new ArrayList<ArrayList<String>>();
		
		for (Node node : rtnc.getCollectorVBox().getChildren()) {
			
			//each anchor is actually a AnchorForReadingType, after it is cast we can see what type it is, Quote, Reading, Both
			AnchorPane anchor = (AnchorPane) node;
			AnchorForReadingType anchorSpecial = (AnchorForReadingType) anchor;
			
			HBox mainHBox = (HBox) anchorSpecial.getChildren().get(0);
			
			/*
			 * ("CREATE TABLE AnalysisContainer (Id INT NOT NULL GENERATED ALWAYS AS IDENTITY, Contents VARCHAR(30000))");// A
			 */
			if (anchorSpecial.getType().equals("Analysis")) {
				
				VBox mainVBox = (VBox) mainHBox.getChildren().get(1);
				TextArea analysisTextArea = (TextArea) mainVBox.getChildren().get(1);
				String analysisString = prepareStringForSQL(analysisTextArea.getText());
				
				
				try {
				    PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO AnalysisContainer (Contents) VALUES ('" + analysisString + "')", Statement.RETURN_GENERATED_KEYS);
				    
				    preparedStatement.executeUpdate();
				    
				    
				    //will have to query the database to see which id was generated for the container
			        try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
			            if (generatedKeys.next()) { //this is in an iteration loop but will only run once because there will only be 1 insert
						    String generatedId = generatedKeys.getString(1);
			            	
			            	ArrayList<String> arrayListToAdd = new ArrayList<String>();
			            	arrayListToAdd.add("A"); //A because this is an analysis container
			            	arrayListToAdd.add(generatedId);
			            	containerSequence.add(arrayListToAdd);
			            }
			            else {
			                throw new SQLException("Inserting failed, no Analysis Id obtained");
			            }
			            
			        }
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			/*
			 * ("CREATE TABLE QuoteContainer (Id INT NOT NULL GENERATED ALWAYS AS IDENTITY, Contents VARCHAR(30000))");// Q
			 */
			else if (anchorSpecial.getType().equals("Quote")) {
								
				VBox mainVBox = (VBox) mainHBox.getChildren().get(1);
				TextArea quoteTextArea = (TextArea) mainVBox.getChildren().get(1);
				String quoteString = prepareStringForSQL(quoteTextArea.getText());

				try {
				    PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO QuoteContainer (Contents) VALUES ('" + quoteString + "')", Statement.RETURN_GENERATED_KEYS);
				    
				    preparedStatement.executeUpdate();
				    
				    //will have to query the database to see which id was generated for the container
			        try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
			            if (generatedKeys.next()) { //this is in an iteration loop but will only run once because there will only be 1 insert
						    String generatedId = generatedKeys.getString(1);
			            	
			            	ArrayList<String> arrayListToAdd = new ArrayList<String>();
			            	arrayListToAdd.add("Q"); //Q because this is a quote container
			            	arrayListToAdd.add(generatedId);
			            	containerSequence.add(arrayListToAdd);
			            }
			            else {
			                throw new SQLException("Inserting failed, no Quote Id obtained");
			            }
			        }
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			/*
			 * ("CREATE TABLE AnalysisAndQuoteContainer (Id INT NOT NULL GENERATED ALWAYS AS IDENTITY, AnalysisContents VARCHAR(30000), QuoteContents VARCHAR(30000))");// B
			 */
			//both is the legacy name which will be kept, before I had the idea of adding more types,
			//which would make the type name of "Both" confusing, but nevertheless it is kept
			else if (anchorSpecial.getType().equals("Both")) {
				
				VBox quoteVBox = (VBox) mainHBox.getChildren().get(1);
				TextArea quoteTextArea = (TextArea) quoteVBox.getChildren().get(1);
				String quoteString = prepareStringForSQL(quoteTextArea.getText());

				VBox analysisVBox = (VBox) mainHBox.getChildren().get(3);
				TextArea analysisTextArea = (TextArea) analysisVBox.getChildren().get(1);
				String analysisString = prepareStringForSQL(analysisTextArea.getText());

				try {
				    PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO AnalysisAndQuoteContainer (AnalysisContents, QuoteContents)"
				    + " VALUES ('" + analysisString + "', '" + quoteString + "')", Statement.RETURN_GENERATED_KEYS);
				    
				    preparedStatement.executeUpdate();
				    
				    //will have to query the database to see which id was generated for the container
			        try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
			            if (generatedKeys.next()) { //this is in an iteration loop but will only run once because there will only be 1 insert
						    String generatedId = generatedKeys.getString(1);
			            	
			            	ArrayList<String> arrayListToAdd = new ArrayList<String>();
			            	arrayListToAdd.add("B"); //B because this is a quote container
			            	arrayListToAdd.add(generatedId);
			            	containerSequence.add(arrayListToAdd);
			            }
			            else {
			                throw new SQLException("Inserting failed, no Analysis and Quote Id obtained");
			            }
			        }
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			else if (anchorSpecial.getType().equals("Section")) {
				
				BorderPane borderPane = (BorderPane) mainHBox.getChildren().get(1);
				Button nameButton = (Button) borderPane.getCenter();
				String name = prepareStringForSQL(nameButton.getText());

				try {
				    PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO SectionContainer (Name) VALUES ('" + name + "')", Statement.RETURN_GENERATED_KEYS);
				    
				    preparedStatement.executeUpdate();
				    
				    //will have to query the database to see which id was generated for the container
			        try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
			            if (generatedKeys.next()) { //this is in an iteration loop but will only run once because there will only be 1 insert
						    String generatedId = generatedKeys.getString(1);
			            	
			            	ArrayList<String> arrayListToAdd = new ArrayList<String>();
			            	arrayListToAdd.add("S"); //Q because this is a quote container
			            	arrayListToAdd.add(generatedId);
			            	containerSequence.add(arrayListToAdd);
			            }
			            else {
			                throw new SQLException("Inserting failed, no Quote Id obtained");
			            }
			        }
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			else if (anchorSpecial.getType().equals("Chapter")) {
				BorderPane borderPane = (BorderPane) mainHBox.getChildren().get(1);
				Button nameButton = (Button) borderPane.getCenter();
				String name = prepareStringForSQL(nameButton.getText());

				try {
				    PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO ChapterContainer (Name) VALUES ('" + name + "')", Statement.RETURN_GENERATED_KEYS);
				    
				    preparedStatement.executeUpdate();
				    
				    //will have to query the database to see which id was generated for the container
			        try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
			            if (generatedKeys.next()) { //this is in an iteration loop but will only run once because there will only be 1 insert
						    String generatedId = generatedKeys.getString(1);
			            	
			            	ArrayList<String> arrayListToAdd = new ArrayList<String>();
			            	arrayListToAdd.add("C"); //Q because this is a quote container
			            	arrayListToAdd.add(generatedId);
			            	containerSequence.add(arrayListToAdd);
			            }
			            else {
			                throw new SQLException("Inserting failed, no Quote Id obtained");
			            }
			        }
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
        connection.close();
		
		return containerSequence;
	}
	
	/*
	 * this will define the format of the container sequence
	 * 
	 * The outer ArrayList will be separated by commas ,
	 * The inner ArrayList will be separated by hyphens -
	 */
	private static String encryptContainerSequence(ArrayList<ArrayList<String>> containerSequence) {
		
		String containerSequenceString = "";
		
		for (ArrayList<String> stringList : containerSequence) {
			
			containerSequenceString += stringList.get(0); //this is the letter
			containerSequenceString += "-";
			containerSequenceString += stringList.get(1); //this is the id

			containerSequenceString += ",";
		}
		//removes the last character because there will be an extra ","
		if (containerSequenceString.length() > 0) {
			containerSequenceString = containerSequenceString.substring(0, containerSequenceString.length() - 1);
		}
		
		return containerSequenceString;
	}
	
	private static ArrayList<ArrayList<String>> decryptContainerSequence(String containerSequenceString) {
		
		ArrayList<ArrayList<String>> containerSequence = new ArrayList<ArrayList<String>>();
		
		if (containerSequenceString.length() != 0) {
			String firstSplit[] = containerSequenceString.split(",");
			
			for (String innerString : firstSplit) {
				
				String secondSplit[] = innerString.split("-");
				
				ArrayList<String> innerList = new ArrayList<String>();
				innerList.add(secondSplit[0]);
				innerList.add(secondSplit[1]);

				containerSequence.add(innerList);
			}
		}
		
		return containerSequence;
	}
	
	/*
	 * the first element in the list is the character which determines the table
	 * the second element is the id in the respective table
	 */
	private static void deleteRowInRespectiveReadingPageTable(ArrayList<String> innerList) throws SQLException {
		
		Connection connection = DriverManager.getConnection(urlForConnection);
	    Statement statement = connection.createStatement();
		
		/*
		 * ("CREATE TABLE AnalysisContainer (Id INT NOT NULL GENERATED ALWAYS AS IDENTITY, Contents VARCHAR(30000))");// A
		 */
		if (innerList.get(0).equals("A")) {
			statement.executeUpdate("DELETE FROM AnalysisContainer WHERE Id = " + innerList.get(1));
		}
		
		/*
		 * ("CREATE TABLE QuoteContainer (Id INT NOT NULL GENERATED ALWAYS AS IDENTITY, Contents VARCHAR(30000))");// Q
		 */
		else if (innerList.get(0).equals("Q")) {
			statement.executeUpdate("DELETE FROM QuoteContainer WHERE Id = " + innerList.get(1));
		}
		
		/*
		 * ("CREATE TABLE AnalysisAndQuoteContainer (Id INT NOT NULL GENERATED ALWAYS AS IDENTITY, AnalysisContents VARCHAR(30000), QuoteContents VARCHAR(30000))");// B
		 */
		else if (innerList.get(0).equals("B")) {
			statement.executeUpdate("DELETE FROM AnalysisAndQuoteContainer WHERE Id = " + innerList.get(1));
		}
		
		else if (innerList.get(0).equals("C")) {
			statement.executeUpdate("DELETE FROM ChapterContainer WHERE Id = " + innerList.get(1));
		}
		
		else if (innerList.get(0).equals("S")) {
			statement.executeUpdate("DELETE FROM SectionContainer WHERE Id = " + innerList.get(1));
		}
		
		else {
			throw new SQLException("Something went wrong, table indicator for Respective Reading Page Table is no good: " + innerList.get(0));
		}
	}
	
	/*
	 * Because I use single quotations, ', for wrapping strings for SQL, if the string contains a single quotation, it will cause a syntax error
	 * This method replaces single quotations with two single quotations, which is interpreted as just one single quotation when
	 * queried back from SQL
	 */
	private static String prepareStringForSQL(String string) {
		return string.replaceAll("\'", "\''");
	}
	
	
	
	public static void startLoadProtocol(MasterReference mR) {
		
		//TODO place temporary db updates here
		
		/*
		try {
			Connection connection = DriverManager.getConnection(urlForConnection);
		    Statement statement = connection.createStatement();
		    statement.executeUpdate("CREATE TABLE SectionContainer (Id INT NOT NULL GENERATED ALWAYS AS IDENTITY, Name VARCHAR(1000))");// S
		    statement.executeUpdate("CREATE TABLE ChapterContainer (Id INT NOT NULL GENERATED ALWAYS AS IDENTITY, Name VARCHAR(1000))");// C

		} catch (SQLException e) {
			e.printStackTrace();
		}
		*/
		
		startTreeViewLoadProtocol(mR);
		
	}
	
	/*
	 * first, build all of the TreeItem<Note>
	 * then, assign children by going through through CREATE TABLE TreeViewStructure (Id INT, ChildrenSequence VARCHAR(1000), IsRoot CHAR(1))
	 * make note of the root during previous step, set root at the end
	 * 
	 * the ChildrenSequence is a comma-separated list of the notes children, unordered
	 */
	private static void startTreeViewLoadProtocol(MasterReference mR) {
		
		try {
			Connection connection = DriverManager.getConnection(urlForConnection);
		    Statement statement = connection.createStatement();
			
			ArrayList<TreeItem<Note>> treeList = new ArrayList<TreeItem<Note>>();
					
			loadFromReadingPage(treeList, connection, mR);
			loadFromLegacyDailyPage(treeList, connection, mR);
			loadFromStandardPage(treeList, connection, mR);
						
			TreeItem<Note> rootItem = null;
			
		    ResultSet resultSet = statement.executeQuery("SELECT * FROM TreeViewStructure");
		    while (resultSet.next()) {
		    	
		    	String childrenSequenceString = resultSet.getString("ChildrenSequence");
		    	
		    	ArrayList<String> childrenIds = new ArrayList<String>();
		    	if (childrenSequenceString != null) {
		    		for (String childId : childrenSequenceString.split(",")) {
		    			childrenIds.add(childId);
		    		}
		    	}
		    	
		    	TreeItem<Note> parentItem = null;
		    	Integer id = resultSet.getInt("Id");
		    		    		
		    	for (TreeItem<Note> treeItem : treeList) {		    		
		    		if (treeItem.getValue().getDatabaseId().equals(id)) {
		    			parentItem = treeItem;
		    			break;
		    		}
		    	}
		    	
		    	if (parentItem == null) {
		    		throw new Exception("An id which existed in one of the type pages does not exist in the main database.");
		    	}
		    	
		    	for (TreeItem<Note> treeItem : treeList) {
		    		if (childrenIds.contains(treeItem.getValue().getDatabaseId().toString())) {
		    			parentItem.getChildren().add(treeItem);
		    		}
		    	}
		    			    	
		    	if (resultSet.getString("IsRoot").equals("1")) {
		    		rootItem = parentItem;
		    	}
		    }
		    		    
		    mR.getMainClassController().getNoteChooser().setRoot(rootItem);
			
			connection.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * (Id INT, Title VARCHAR(1000), BoxContents VARCHAR(30000), PyramidSequence VARCHAR(1000))
	 * 
	 * the pyramidsequence implementation in Derby is currently unfinished
	 */
	private static void loadFromStandardPage(ArrayList<TreeItem<Note>> treeList, Connection connection, MasterReference mR) throws SQLException {
		
	    Statement statement = connection.createStatement();
	    ResultSet resultSet = statement.executeQuery("SELECT * FROM StandardPage");
	    
	    while (resultSet.next()) {
	    		    	
	    	Note standardNote = mR.getNoteChooserHandler().new Note(resultSet.getString("Title"),"Standard");
			standardNote.setDatabaseId(resultSet.getInt("Id"));

			StandardTypeNoteController stnc = (StandardTypeNoteController) standardNote.getController();
			
			stnc.getMainTextArea().setText(resultSet.getString("BoxContents"));
			stnc.getTitleArea().setText(resultSet.getString("Title"));
						
			treeList.add(new TreeItem<Note>(standardNote));
	    }
	}
	
	/*
	 * CREATE TABLE LegacyDailyPage (Id INT, Title VARCHAR(1000), LeftBoxContents VARCHAR(30000),
	 * TopRightBoxContents VARCHAR(30000), BottomRightBoxContents VARCHAR(30000))
	 * 
	 * String LeftBoxContents = prepareStringForSQL(dtnc.getBrainstormingSection().getText());
	 * String TopRightBoxContents = prepareStringForSQL(dtnc.getToDoSection().getText());
	 * String BottomRightBoxContents = prepareStringForSQL(dtnc.getCalendarSection().getText());   
	 */
	private static void loadFromLegacyDailyPage(ArrayList<TreeItem<Note>> treeList, Connection connection, MasterReference mR) throws SQLException {
	    
	    Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery("SELECT * FROM LegacyDailyPage");
	    
		while (resultSet.next()) {
				    	
	    	Note legacyDailyNote = mR.getNoteChooserHandler().new Note(resultSet.getString("Title"),"Daily");
	    	legacyDailyNote.setDatabaseId(resultSet.getInt("Id"));

			DailyTypeNoteController dtnc = (DailyTypeNoteController) legacyDailyNote.getController();
			
			dtnc.getBrainstormingSection().setText(resultSet.getString("LeftBoxContents"));
			dtnc.getToDoSection().setText(resultSet.getString("TopRightBoxContents"));
			dtnc.getCalendarSection().setText(resultSet.getString("BottomRightBoxContents"));
			
			treeList.add(new TreeItem<Note>(legacyDailyNote));
	    }
	}
	
	/*
	 *CREATE TABLE ReadingPage (Id INT, Title VARCHAR(1000), ContainerSequence VARCHAR(1000))
	 *
	 *CREATE TABLE AnalysisContainer (Id INT NOT NULL GENERATED ALWAYS AS IDENTITY, Contents VARCHAR(30000)) A
	 *CREATE TABLE QuoteContainer (Id INT NOT NULL GENERATED ALWAYS AS IDENTITY, Contents VARCHAR(30000)) Q
	 *CREATE TABLE AnalysisAndQuoteContainer (Id INT NOT NULL GENERATED ALWAYS AS IDENTITY, AnalysisContents VARCHAR(30000), QuoteContents VARCHAR(30000)) B
	 *
	 *first we will iterate through the resSet as normal, then there will be helper methods depending on which type the container is
	 *
	 */
	private static void loadFromReadingPage(ArrayList<TreeItem<Note>> treeList, Connection connection, MasterReference mR) throws SQLException {
		
	    Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery("SELECT * FROM ReadingPage");
			    
		while (resultSet.next()) {
				    	
	    	Note readingNote = mR.getNoteChooserHandler().new Note(resultSet.getString("Title"),"Reading");
	    	readingNote.setDatabaseId(resultSet.getInt("Id"));

			ReadingTypeNoteController rtnc = (ReadingTypeNoteController) readingNote.getController();
						
			ArrayList<ArrayList<String>> containerSequence = decryptContainerSequence(resultSet.getString("ContainerSequence"));
						
			for (ArrayList<String> typeAndIdPair : containerSequence) {
				
				String type = typeAndIdPair.get(0);
				String id = typeAndIdPair.get(1);

				if (type.equals("A")) { //Analysis
										
					//having multiple statements with different queries can cause errors
					Statement subStatement = connection.createStatement();
				    ResultSet resSet = subStatement.executeQuery("SELECT * FROM AnalysisContainer WHERE Id = " + id);
				    
				    if (resSet.next()) {
						TextArea analysisTextArea = rtnc.pushAnalysisButton();

						analysisTextArea.setText(resSet.getString("Contents"));
				    }
				}
				else if (type.equals("Q")) { //Quote
					
					Statement subStatement = connection.createStatement();
				    ResultSet resSet = subStatement.executeQuery("SELECT * FROM QuoteContainer WHERE Id = " + id);
				    
				    if (resSet.next()) {
						TextArea quoteTextArea = rtnc.pushQuoteButton();

						quoteTextArea.setText(resSet.getString("Contents"));
				    }
				}
				else if (type.equals("B")) { //Both Analysis and Quote
					
					Statement subStatement = connection.createStatement();
				    ResultSet resSet = subStatement.executeQuery("SELECT * FROM AnalysisAndQuoteContainer WHERE Id = " + id);
				    
				    if (resSet.next()) {
						ArrayList<TextArea> textAreaList = rtnc.pushBothButton();

						//the indexes are hard-coded here
						TextArea quoteTextArea = textAreaList.get(0);
						TextArea analysisTextArea = textAreaList.get(1);
						
						quoteTextArea.setText(resSet.getString("QuoteContents"));
						analysisTextArea.setText(resSet.getString("AnalysisContents"));
				    }
				}
			    else if (type.equals("S")) { //Section
			    	
					Statement subStatement = connection.createStatement();
				    ResultSet resSet = subStatement.executeQuery("SELECT * FROM SectionContainer WHERE Id = " + id);
				    
				    if (resSet.next()) {
						rtnc.createSection(resSet.getString("Name"));
				    }
			    }
			    else if (type.equals("C")) { //Section
			    	
					Statement subStatement = connection.createStatement();
				    ResultSet resSet = subStatement.executeQuery("SELECT * FROM ChapterContainer WHERE Id = " + id);
				    
				    if (resSet.next()) {
						rtnc.createChapter(resSet.getString("Name"));
				    }
			    }
				else {
					throw new SQLException("Something went wrong, Reading Page Container Type is not A, Q, or B");
				}	
			}
			
			treeList.add(new TreeItem<Note>(readingNote));
	    }
	}
	
	public static void deletePageFromDatabase(Note note) throws Exception {
		
		//if it is null, then it does not yet exist in the database
		if (note.getDatabaseId() != null) {
			
			Connection connection = DriverManager.getConnection(urlForConnection);
		    Statement statement = connection.createStatement();
		    
		    statement.execute("DELETE FROM Page WHERE Id = " + note.getDatabaseId().toString());
			
			if (note.getTypeOfNote() == "Standard") {
			    statement.execute("DELETE FROM StandardPage WHERE Id = " + note.getDatabaseId().toString());
			}
			else if (note.getTypeOfNote() == "Daily") {
			    statement.execute("DELETE FROM LegacyDailyPage WHERE Id = " + note.getDatabaseId().toString());
			}
			//if it is a reading note then it contains sub tables which need to be handled first
			else if (note.getTypeOfNote() == "Reading") {
				ResultSet resultSet = statement.executeQuery("SELECT * FROM ReadingPage WHERE Id =" + note.getDatabaseId().toString());

				while (resultSet.next()) { //this will only run once unless there is a problem
								
					ArrayList<ArrayList<String>> containerSequence = decryptContainerSequence(resultSet.getString("ContainerSequence"));
								
					for (ArrayList<String> typeAndIdPair : containerSequence) {
						
						String type = typeAndIdPair.get(0);
						String id = typeAndIdPair.get(1);

						if (type.equals("A")) { //Analysis				
							Statement subStatement = connection.createStatement();
						    subStatement.execute("DELETE FROM AnalysisContainer WHERE Id = " + id);
						}
						else if (type.equals("Q")) { //Quote
							Statement subStatement = connection.createStatement();
						    subStatement.execute("DELETE FROM QuoteContainer WHERE Id = " + id);
						}
						else if (type.equals("B")) { //Both Analysis and Quote
							Statement subStatement = connection.createStatement();
						    subStatement.execute("DELETE FROM AnalysisAndQuoteContainer WHERE Id = " + id);
						}
						else {
							throw new SQLException("Something went wrong, Reading Page Container Type is not A, Q, or B");
						}	
					}
			    }
				
				statement.execute("DELETE FROM ReadingPage WHERE Id = " + note.getDatabaseId().toString());
			}
			
			else {
				throw new Exception("note is not one of the valid types when deleting from database.");
			}
		}
	}
	
	/*
	 * CREATE TABLE LongTermGoalTable (Description VARCHAR(1000), Day INT, Month INT, CalendarYear INT)
	 * 
	 * this method will only be run on creation of new element, elements won't be updated
	 */
	public static void saveToLongTermGoalTable(String description, String day, String month, String calendarYear) {
		
		try {
			Connection connection = DriverManager.getConnection(urlForConnection);
		    Statement statement = connection.createStatement();
		    
			statement.executeUpdate("INSERT INTO LongTermGoalTable (Description, Day, Month, CalendarYear) VALUES ('"
			+ prepareStringForSQL(description) + "', " + day + ", " + month +  ", " + calendarYear + ")");

			connection.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * goals can not be edited before they are deleted from the table
	 * this method will find the element in table where the description matches, then deletes it
	 */
	public static void deleteFromLongTermGoalTable(String description) {
		
		try {
			Connection connection = DriverManager.getConnection(urlForConnection);
		    Statement statement = connection.createStatement();
			
			statement.execute("DELETE FROM LongTermGoalTable WHERE Description = '" + prepareStringForSQL(description) + "'");

			connection.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}	
	
	/*
	 * will return info to be handled by the DailyScrollController on initialization
	 */
	public static ArrayList<ArrayList<String>> loadFromLongTermGoalTable() {
				
		Connection connection;
		try {
			connection = DriverManager.getConnection(urlForConnection);
		    Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM LongTermGoalTable");
			
			ArrayList<ArrayList<String>> listOfLists = new ArrayList<ArrayList<String>>();
			
			while (resultSet.next()) {
								
				ArrayList<String> currentList = new ArrayList<String>();
				
				currentList.add(resultSet.getString("Description"));
				currentList.add(resultSet.getString("Day"));
				currentList.add(resultSet.getString("Month"));
				currentList.add(resultSet.getString("CalendarYear"));
				
				listOfLists.add(currentList);
			}
			
			connection.close();
			
			return listOfLists;
		    
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/*
	 * BookDeskTable (Title VARCHAR(1000))
	 * 
	 * TODO this save structure only works if the books have unique titles
	 */
	public static void saveToBookDeskTable(ArrayList<String> titles) {

		try {
			Connection connection = DriverManager.getConnection(urlForConnection);
		    Statement statement = connection.createStatement();
		    
		    statement.execute("DELETE FROM BookDeskTable"); //this deletes all rows of the table
		    
		    for (String title : titles) {
				statement.executeUpdate("INSERT INTO BookDeskTable (Title) VALUES ('"
				+ prepareStringForSQL(title) + "')");
		    }

			connection.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static ArrayList<String> loadFromBookDeskTable() {
		
		ArrayList<String> returnList = new ArrayList<String>();

		try {
			Connection connection = DriverManager.getConnection(urlForConnection);
		    Statement statement = connection.createStatement();
		    
		    ResultSet resSet = statement.executeQuery("SELECT * FROM BookDeskTable");
		    
		    while (resSet.next()) {
		    	returnList.add(resSet.getString("Title"));
		    }

			connection.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return returnList;
	}
	
	/*
	 * CREATE TABLE WeeklyGoalTable (Title VARCHAR(1000), Repetitions Int, Remaining Int)
	 * 
	 * this is run when a new weekly goal is created, this will be consistent for every instance of new daily type note
	 */
	public static void saveGoalToWeeklyGoalTable(String description, Integer repetitions) {
		
		try {
			Connection connection = DriverManager.getConnection(urlForConnection);
		    Statement statement = connection.createStatement();
		    
			statement.executeUpdate("INSERT INTO WeeklyGoalTable (Title, Repetitions, Remaining) VALUES ('"
			+ prepareStringForSQL(description) + "', " + repetitions.toString() + ", " + repetitions.toString() + ")");

			connection.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * removes the goal from the cycle, not to be confused with the deleteRepetition function
	 */
	public static void deleteGoalFromWeeklyGoalTable(String description) {
		
		try {
			Connection connection = DriverManager.getConnection(urlForConnection);
		    Statement statement = connection.createStatement();
			
			statement.execute("DELETE FROM WeeklyGoalTable WHERE Title = '" + prepareStringForSQL(description) + "'");

			connection.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * this essentially just lowers the value in the repetition column by one
	 * 
	 * it should be impossible for the value present to be <= 0 when pressed, so will not handle that case
	 */
	public static void deleteRepetitionFromWeeklyGoalTable(String description) {
		
		try {
			Connection connection = DriverManager.getConnection(urlForConnection);
		    Statement statement = connection.createStatement();
		    
		    //this process will have an uncaught null addition error if something goes wrong, this is okay 
		    Integer remainingRepetitions = null;
		    ResultSet resultSet = statement.executeQuery("SELECT * FROM WeeklyGoalTable WHERE Title = '" + prepareStringForSQL(description) + "'");
		    while (resultSet.next()) {
		    	remainingRepetitions = resultSet.getInt(3);
		    }
		    		    			
	    	statement.executeUpdate("UPDATE WeeklyGoalTable SET Remaining = " + Integer.toString(remainingRepetitions - 1) +
	    	" Where Title = '" + prepareStringForSQL(description) + "'");

			connection.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * sets the remaining value to be equal to the repetitions value
	 */
	public static void resetRepetitionsFromWeeklyGoalTable() {
		try {
			Connection connection = DriverManager.getConnection(urlForConnection);
		    Statement statement = connection.createStatement();
		    
		    //this process will have an uncaught null addition error if something goes wrong, this is okay 
		    ResultSet resultSet = statement.executeQuery("SELECT * FROM WeeklyGoalTable");
		    while (resultSet.next()) {
		    	if (resultSet.getString(2).equals(resultSet.getString(3)) == false) { //only sets them same if they are different, more efficient
			    	statement.executeUpdate("UPDATE WeeklyGoalTable SET Remaining = " + resultSet.getString(2) +
			    	    	" Where Title = '" + prepareStringForSQL(resultSet.getString(1)) + "'");
		    	}
		    }

			connection.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static ArrayList<ArrayList<String>> loadFromWeeklyGoalTable() {
		
		Connection connection;
		try {
			connection = DriverManager.getConnection(urlForConnection);
		    Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM WeeklyGoalTable");
			
			ArrayList<ArrayList<String>> listOfLists = new ArrayList<ArrayList<String>>();
			
			while (resultSet.next()) {
								
				ArrayList<String> currentList = new ArrayList<String>();
				
				currentList.add(resultSet.getString("Title"));
				currentList.add(resultSet.getString("Repetitions"));
				currentList.add(resultSet.getString("Remaining"));
				
				listOfLists.add(currentList);
			}
			
			connection.close();
			
			return listOfLists;
		    
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
