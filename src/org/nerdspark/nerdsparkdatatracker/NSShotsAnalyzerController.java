package org.nerdspark.nerdsparkdatatracker;

import java.net.URL;
//import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

// This is the controller class for the NERD Spark Shots Analyzer.  
// Most of the logic is in this class.  

public class NSShotsAnalyzerController implements Initializable {
	// Declare all variables	
	@FXML private ImageView imgMap;
	@FXML private Pane pMapPane;
	@FXML private ImageView imgSpeaker;
	@FXML private Pane pSpeakerPane;
	@FXML private TextField txtMatchName;
	@FXML private ComboBox<String> comboboxMatchName;

	@FXML private ToggleGroup tgrDisplay;
	@FXML private RadioButton rbtnDisplayAll;
	@FXML private RadioButton rbtnDisplayAuton;
	@FXML private RadioButton rbtnDisplayTeleop;
	@FXML private RadioButton rbtnDisplayCurrent;
	
	@FXML private CheckBox cboxDoNotEraseShots;
	@FXML private TextField txtMatchTime;
	@FXML private Label lblMatchTime;
	@FXML private Label lblMatchName;
	@FXML private TextField txtFieldXCoord;
	@FXML private TextField txtFieldYCoord;
	@FXML private Label lblShotTime;
	@FXML private Label lblShotNumber;
	@FXML private TextField txtShotTime;
	@FXML private TextField txtShotNumber;
	@FXML private ToggleGroup tgrOutcome;
	@FXML private RadioButton rbtnTooHigh;
	@FXML private RadioButton rbtnScored;
	@FXML private RadioButton rbtnInAndOut;
	@FXML private RadioButton rbtnTooLow;
	@FXML private RadioButton rbtnOther;
	@FXML private TextField txtSpeakerXCoord;
	@FXML private TextField txtSpeakerYCoord;
	@FXML private Label lblSpeakerXCoord;
	@FXML private Label lblSpeakerYCoord;
	@FXML private TableView<Shot> tblShots;
	@FXML private ChoiceBox<String> cboxTeam;
	@FXML private ChoiceBox<String> cboxMode;
	@FXML private Label lblMode;
	@FXML private Label lblAmpShot;
	@FXML private TextField txtAmpShots;
	@FXML private TextField txtAutonSummary;
	@FXML private TextField txtTeleopSummary;

	private ArrayList<Shot> alShots = new ArrayList<Shot>();
	private int iShotIndex = 0;
	private int iMatchIndex = 0;
	private Circle marker;
	private Circle marker2;
	private Rectangle map_marker;
	private Rectangle speaker_marker;
	private DropShadow dropShadowWhite = new DropShadow();
	private DropShadow dropShadowLime = new DropShadow(); 

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		// These are things to do when the program initializes
		System.out.println("Initializing NERD Spark Shots Analyzer!");

		// Populate Team drop down
		cboxTeam.getItems().add("Red");
		cboxTeam.getItems().add("Blue");
		cboxTeam.getSelectionModel().selectFirst();
		
		// Populate Mode drop down
		cboxMode.getItems().add("Auton");
		cboxMode.getItems().add("Teleop");
		cboxMode.getSelectionModel().selectFirst();
		
		// Setting the drop shadow settings for the circles
		dropShadowWhite.setBlurType(BlurType.GAUSSIAN);
		dropShadowWhite.setColor(Color.WHITE); 
	    dropShadowWhite.setRadius(2);  
	    dropShadowWhite.setSpread(3);  
		dropShadowLime.setBlurType(BlurType.GAUSSIAN);
		dropShadowLime.setColor(Color.LIME); 
	    dropShadowLime.setRadius(2);  
	    dropShadowLime.setSpread(3); 
		
		// Load the matches to the match name dropdown		
		NSShotsUtils.alMatchHistory.forEach( match -> comboboxMatchName.getItems().add( match.getName() + " - " + match.getDate()) );
				
		// Add listener to the match name dropdown and populate the 
		// input fields on the screen with the info from the selected match
		comboboxMatchName.setOnAction((event) -> {

			// If "Don't Erase Shots" is not checked, then clear the shots on screen
			if (!cboxDoNotEraseShots.isSelected()) clearAllShots();
			
			// Get Match info based on drop down selection
		    System.out.println("Analyzing match : " + comboboxMatchName.getValue());
		    iMatchIndex = comboboxMatchName.getSelectionModel().getSelectedIndex();
		    iShotIndex = 0;
		    MatchHistory currentMatch = NSShotsUtils.alMatchHistory.get(iMatchIndex);
		    alShots = currentMatch.getShots();
		    
		    //Populate the screen with the corresponding data
		    txtMatchName.setText(currentMatch.getName());
		    txtMatchTime.setText(currentMatch.getDate());
		    cboxTeam.getSelectionModel().select(currentMatch.getTeam());
		    if (currentMatch.getAmpShots() == null) {
		    	 txtAmpShots.setText("0");
		    } else {
		    	txtAmpShots.setText(currentMatch.getAmpShots().toString());
		    }
		    populateShotsOnScreen();
		    populateShotsOnTableView();
			setRowFocus();
			
			// Show shots on screen
			if (rbtnDisplayAll.isSelected())	showAllShots();
			else if (rbtnDisplayAuton.isSelected())	{
				showAllAuton();
			}
			else if (rbtnDisplayTeleop.isSelected()) {
				showAllTelop();
			}
		});

		// Add a Listener to the Show Shots radio button to display or clear
		// the shot circles on the field image
		rbtnDisplayAll.selectedProperty().addListener(new ChangeListener<Boolean>() {
		    @Override
		    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		    	if (rbtnDisplayAll.isSelected()) showAllShots();
		    	else if (!cboxDoNotEraseShots.isSelected()) clearAllShots();
		    }
		});

		// Add a Listener to the Show All Auton Shots radio button to display or clear
		// the shot circles on the field image
		rbtnDisplayAuton.selectedProperty().addListener(new ChangeListener<Boolean>() {
		    @Override
		    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		    	if (rbtnDisplayAuton.isSelected()) showAllAuton();
		    	else if (!cboxDoNotEraseShots.isSelected()) clearAllShots();
		    }
		});

		// Add a Listener to the Show All Teleop Shots radio button to display or clear
		// the shot circles on the field image
		rbtnDisplayTeleop.selectedProperty().addListener(new ChangeListener<Boolean>() {
		    @Override
		    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		    	if (rbtnDisplayTeleop.isSelected()) showAllTelop();
		    	else if (!cboxDoNotEraseShots.isSelected()) clearAllShots();
		    }
		});
		
		// Add a Listener to the Show Current Shot radio button to display or clear
		// the shot circles on the field image
		rbtnDisplayCurrent.selectedProperty().addListener(new ChangeListener<Boolean>() {
		    @Override
		    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (rbtnDisplayCurrent.isSelected()) {
					if (!cboxDoNotEraseShots.isSelected()) clearAllShots();
					showShot(alShots.get(iShotIndex));
				}
			}
		});

		// Add a listener to the table view so that we can change the
		// input field values based on which shot was clicked on
		tblShots.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
		    if (newSelection != null) {
		        iShotIndex = tblShots.getSelectionModel().getSelectedIndex();
				populateShotsOnScreen();	
				setRowFocus();
		    }
		});
		
        // Add a listener to the field image to capture the x and y coordinate
        imgMap.setPickOnBounds(true);
        imgMap.setOnMouseClicked(e -> {        	
        	// Record the x and y coordinates in the fields
        	txtFieldXCoord.setText(String.valueOf(NSShotsUtils.calculateRobotX((int)e.getX())));
        	txtFieldYCoord.setText(String.valueOf(NSShotsUtils.calculateRobotY((int)e.getY())));

        	// Record the time that the shot was recorded
    		txtShotTime.setText(NSShotsUtils.getNow());
    		
        	// Put a red marker on the spot with those coordinates  
        	addRectMarkerOnField(Integer.parseInt(txtFieldXCoord.getText()), Integer.parseInt(txtFieldYCoord.getText()));	
        });

        // Add a listener to the speaker image to capture the x and y coordinate
        imgSpeaker.setPickOnBounds(true);
        imgSpeaker.setOnMouseClicked(e -> {
        	
        	// Record the x and y coordinates in the speaker
        	txtSpeakerXCoord.setText(String.valueOf((int)e.getX()));
        	txtSpeakerYCoord.setText(String.valueOf((int)e.getY()));
   		
        	// Put a red marker on the spot with those coordinates 
        	addRectMarkerOnSpeaker(Integer.parseInt(txtSpeakerXCoord.getText()), Integer.parseInt(txtSpeakerYCoord.getText()));

        	// Update the Outcome according to where on speaker was clicked 
        	if (e.getY() < NSShotsUtils.SPEAKER_SCORE_Y_MAX && e.getY() > NSShotsUtils.SPEAKER_SCORE_Y_MIN) rbtnScored.setSelected(true);
        	else if (e.getY() <= NSShotsUtils.SPEAKER_SCORE_Y_MIN) rbtnTooHigh.setSelected(true);
        	else if (e.getY() >= NSShotsUtils.SPEAKER_SCORE_Y_MAX) rbtnTooLow.setSelected(true);
        	else rbtnOther.setSelected(true);
        	if (e.getX() < NSShotsUtils.SPEAKER_SCORE_X_MIN || e.getX() > NSShotsUtils.SPEAKER_SCORE_X_MAX) rbtnOther.setSelected(true);       	        	
        });        
	}

	private void addRectMarker(int p, int x, int y, boolean remove, Color c) {

    	// Put a marker on the spot with the provided coordinates 
		// And remove the old one if prompted
		if (p == NSShotsUtils.FIELD) {
	    	if (map_marker != null && remove) pMapPane.getChildren().remove(map_marker);
	    	// Subtract half the robot size to center the rectangle 
			map_marker = new Rectangle(x - NSShotsUtils.HALF_ROBOT_SIZE, y - NSShotsUtils.HALF_ROBOT_SIZE, NSShotsUtils.ROBOT_SIZE, NSShotsUtils.ROBOT_SIZE);
			map_marker.setFill(c);
	    	pMapPane.getChildren().add(map_marker);
		} else if (p == NSShotsUtils.SPEAKER) {
	    	if (speaker_marker != null && remove) pSpeakerPane.getChildren().remove(speaker_marker);
			speaker_marker = new Rectangle(x - NSShotsUtils.HALF_NOTE_WIDTH, y - NSShotsUtils.HALF_NOTE_HEIGHT, NSShotsUtils.NOTE_WIDTH, NSShotsUtils.NOTE_HEIGHT);
			speaker_marker.setFill(c);
			pSpeakerPane.getChildren().add(speaker_marker);
		}    	
	}
	
	private void addRectMarkerOnField(int x, int y) {
		// Given x and y coordinate, this method puts a shot circle on the field image for this shot.
		addRectMarker(NSShotsUtils.FIELD, x, y, true, Color.LAWNGREEN);
	}
	
	private void addRectMarkerOnSpeaker(int x, int y) {
		// Given x and y coordinate, this method puts a shot circle on the speaker image for this shot.
		// Determine the color of the shot based on accuracy
		if (
			y < NSShotsUtils.SPEAKER_SCORE_Y_MAX && 	//bottom
			y > NSShotsUtils.SPEAKER_SCORE_Y_MIN &&	//top	
			x < NSShotsUtils.SPEAKER_SCORE_X_MAX && 	//left
			x > NSShotsUtils.SPEAKER_SCORE_X_MIN) {	//right
	   		addRectMarker(NSShotsUtils.SPEAKER, x, y, true, Color.GREEN);
		} else if (
			y < NSShotsUtils.SPEAKER_ALMOST_Y_MAX && 	//bottom
			y > NSShotsUtils.SPEAKER_ALMOST_Y_MIN &&	//top
			x < NSShotsUtils.SPEAKER_ALMOST_X_MAX &&	//left
			x > NSShotsUtils.SPEAKER_ALMOST_X_MIN) {	//right
    		addRectMarker(NSShotsUtils.SPEAKER, x, y, true, Color.YELLOW);
		} else { 
    		addRectMarker(NSShotsUtils.SPEAKER, x, y, true, Color.RED);
		}        	
	}
	
	private void addCirMarker(int p, int x, int y, boolean remove, Color c, DropShadow d, String sTooltipText) {

    	// Put a marker on the spot with those coordinates and remove the old one if prompted		
		Tooltip t = new Tooltip(sTooltipText);		
		
		if (p == NSShotsUtils.FIELD) {
	    	if (x == 0 && y == 0) {
	    		// Do nothing if x and y are both 0
	    	} else {
	    		marker = new Circle(x, y, NSShotsUtils.CIRCLE_SIZE, c);
	    		marker.setEffect(d); 
	    		if (sTooltipText != "") Tooltip.install(marker, t); 
	    		pMapPane.getChildren().add(marker);
	    	}
		} else if (p == NSShotsUtils.SPEAKER) {
	    	if (marker2 != null && remove) pSpeakerPane.getChildren().remove(marker2);        	
	    	if (x == 0 && y == 0) {
	    		// Do nothing if x and y are both 0
	    	} else {			marker2 = new Circle(x, y, NSShotsUtils.CIRCLE_SIZE, c);
				marker2.setEffect(d); 
				if (sTooltipText != "") Tooltip.install(marker2, t);
				pSpeakerPane.getChildren().add(marker2);
	    	}
		}    	
	}

	private void populateShotsOnScreen() {
	    
    	// Clear the old markers on the map too
    	if (map_marker != null) pMapPane.getChildren().remove(map_marker);
		if (speaker_marker != null) pSpeakerPane.getChildren().remove(speaker_marker);
		
	    //Populate the Shots-related input fields with the corresponding data

		cboxMode.getSelectionModel().select(alShots.get(iShotIndex).getMode());
	    txtShotNumber.setText(alShots.get(iShotIndex).getShotNumber().toString());
		txtShotTime.setText(alShots.get(iShotIndex).getShotDate().toString());
    	txtFieldXCoord.setText(alShots.get(iShotIndex).getFieldXCoord().toString());
    	txtFieldYCoord.setText(alShots.get(iShotIndex).getFieldYCoord().toString());
    	txtSpeakerXCoord.setText(alShots.get(iShotIndex).getSpeakerXCoord().toString());
    	txtSpeakerYCoord.setText(alShots.get(iShotIndex).getSpeakerYCoord().toString());
    	getOutcomeRadioButton(alShots.get(iShotIndex).getOutcome()).setSelected(true);

		if (rbtnDisplayCurrent.isSelected())
		{
			showShot(alShots.get(iShotIndex));
		}
		
		updateSummaryField();
	}

	private void populateShotsOnTableView() {
	    
	    //Populate the TableView with the shots info from the current match

		// First, clear the table view and columns
		tblShots.getColumns().clear();
		tblShots.getItems().clear();
		
        // Create the table view and columns
        TableColumn<Shot, Integer> col1 = new TableColumn<> ("Shot #");
        TableColumn<Shot, String> col2 = new TableColumn<> ("Date");
        TableColumn<Shot, Integer> col3 = new TableColumn<> ("Field X");
        TableColumn<Shot, Integer> col4 = new TableColumn<> ("Field Y");
        TableColumn<Shot, Integer> col5 = new TableColumn<> ("Speaker X");
        TableColumn<Shot, Integer> col6 = new TableColumn<> ("Speaker Y");
        TableColumn<Shot, String> col7 = new TableColumn<> ("Outcome");
        TableColumn<Shot, String> col8 = new TableColumn<> ("Mode");
        col1.setCellValueFactory(new PropertyValueFactory<Shot, Integer>("shotNumber"));
        col2.setCellValueFactory(new PropertyValueFactory<Shot, String>("shotDate"));
        col3.setCellValueFactory(new PropertyValueFactory<Shot, Integer>("fieldXCoord"));
        col4.setCellValueFactory(new PropertyValueFactory<Shot, Integer>("fieldYCoord"));
        col5.setCellValueFactory(new PropertyValueFactory<Shot, Integer>("speakerXCoord"));
        col6.setCellValueFactory(new PropertyValueFactory<Shot, Integer>("speakerYCoord"));
        col7.setCellValueFactory(new PropertyValueFactory<Shot, String>("outcome"));
        col8.setCellValueFactory(new PropertyValueFactory<Shot, String>("mode"));
        tblShots.getColumns().add(col8);
        tblShots.getColumns().add(col1);
        tblShots.getColumns().add(col7);
        tblShots.getColumns().add(col2);
        tblShots.getColumns().add(col3);
        tblShots.getColumns().add(col4);
        tblShots.getColumns().add(col5);
        tblShots.getColumns().add(col6);
        
        // Add data to the table view and columns from the alShots array list
		for (int i = 0; i < alShots.size(); i++) {		 
			tblShots.getItems().add(alShots.get(i));
		}		
	}

	public RadioButton getOutcomeRadioButton(String name) {
    	switch (name) {
			case "Too High": return rbtnTooHigh;
			case "Too Low": return rbtnTooLow;
			case "Scored": return rbtnScored;
			case "In and Out": return rbtnInAndOut;
			case "Other": return rbtnOther;
			default: return rbtnOther;
		}
	}
	
	public void previousClick() {
		// This method is called when the user clicks on the  
		// Prev button to see the previous shot info

		// Confirm there is a previous shot, and if so, populate the data on screen
		if (iShotIndex > 0) {
			iShotIndex--; 
			populateShotsOnScreen();			
		}	
		setRowFocus();
	}
	
	public void nextClick() {
		// This method is called when the user clicks on the  
		// Prev button to see the previous shot info
		
		// Confirm there is a next shot, and if so, populate the data on screen
		if (iShotIndex >= 0 && iShotIndex < (alShots.size()-1)) {
			iShotIndex++; 
			populateShotsOnScreen();	
		}
		setRowFocus();
	}

	public void setRowFocus() {
		// This method sets the row focus on the TableView on the right.
		tblShots.requestFocus();
		tblShots.getSelectionModel().select(iShotIndex);
		tblShots.getFocusModel().focus(iShotIndex);		
	}


	public void updateMatchClick() {
		// This method is called when the user clicks on the
		// Update button next to the match info.
		System.out.println("Updating the match:" + txtMatchName.getText() + " - " + txtMatchTime.getText());
		
		// Add the match history data to the Match History array list first
		NSShotsUtils.alMatchHistory.get(iMatchIndex).setName(txtMatchName.getText()); 
		NSShotsUtils.alMatchHistory.get(iMatchIndex).setDate(txtMatchTime.getText());
		NSShotsUtils.alMatchHistory.get(iMatchIndex).setTeam(cboxTeam.getValue());
		NSShotsUtils.alMatchHistory.get(iMatchIndex).setAmpShots(Integer.parseInt(txtAmpShots.getText()));
		    
		// Write the match data to file
		NSShotsUtils.writeMatchToFile();
		
		// Update the Match Name drop down with the newly update info
		comboboxMatchName.getItems().set(iMatchIndex, txtMatchName.getText() + " - " + txtMatchTime.getText());
		comboboxMatchName.getSelectionModel().select(iMatchIndex);
		
		// Update summary field
		updateSummaryField();
	}

	public void updateShotClick() {
		// This method is called when the user clicks on the
		// Update button next to the match info.
		System.out.println("Updating the shot: " + txtShotNumber.getText());
		
		// Create a new Shot object based on the data from the user entry fields
		Shot s = new Shot(
				Integer.parseInt(txtShotNumber.getText()),
				txtShotTime.getText(),
				Integer.parseInt(txtFieldXCoord.getText()),
				Integer.parseInt(txtFieldYCoord.getText()),
				Integer.parseInt(txtSpeakerXCoord.getText()),
				Integer.parseInt(txtSpeakerYCoord.getText()),
				((RadioButton)tgrOutcome.getSelectedToggle()).getText(),
				cboxMode.getValue()
				);
		
		// Use this Shot object to replace the data in the corresponding alShots and alMatchHistory ArrayLists  
		alShots.set(iShotIndex, s);
		NSShotsUtils.alMatchHistory.get(iMatchIndex).getShots().set(iShotIndex, s);
		    
		// Now write this data to file
		NSShotsUtils.writeMatchToFile();
		
		populateShotsOnTableView();
		setRowFocus();
	}

	public void addShotBeforeClick() {
		// This method is used to add a new shot before the current shot.

		// Create a blank shot first, and add it to the end of the array list
		Shot s = new Shot( alShots.size()+1, "",0,0,0,0,"Other",cboxMode.getValue());		
		alShots.add(s);

		// Now start moving all the data down one number		
		for (int i = alShots.size()-1; i > iShotIndex; i--) {	

			s = new Shot(
					alShots.get(i).getShotNumber(),
					alShots.get(i-1).getShotDate(),
					alShots.get(i-1).getFieldXCoord(),
					alShots.get(i-1).getFieldYCoord(),
					alShots.get(i-1).getSpeakerXCoord(),
					alShots.get(i-1).getSpeakerYCoord(),
					alShots.get(i-1).getOutcome(),
					alShots.get(i-1).getMode()
					);
			
			// Use this Shot object to replace the data in the corresponding alShots and alMatchHistory ArrayLists  
			alShots.set(i, s);
			NSShotsUtils.alMatchHistory.get(iMatchIndex).getShots().set(i, s);
		}		
		
		// Create a new blank Shot object
		s = new Shot( (iShotIndex+1), "",0,0,0,0,"Other",cboxMode.getValue());	
		
		// Use this Shot object to replace the data in the corresponding alShots and alMatchHistory ArrayLists  
		alShots.set(iShotIndex, s);
		NSShotsUtils.alMatchHistory.get(iMatchIndex).getShots().set(iShotIndex, s);
		    
		// Now write this data to file
		NSShotsUtils.writeMatchToFile();
		
		populateShotsOnTableView();
		setRowFocus();
	}
	

	public void addShotAfterClick() {
		// This method is used to add a new shot after the current shot.

		// Create a blank shot first, and add it to the end of the array list
		Shot s = new Shot( alShots.size()+1, "",0,0,0,0,"Other",cboxMode.getValue());		
		alShots.add(s);
		
		// Now start moving all the data down one number
		
		for (int i = alShots.size()-1; i > iShotIndex; i--) {	
			
			s = new Shot(
					alShots.get(i).getShotNumber(),
					alShots.get(i-1).getShotDate(),
					alShots.get(i-1).getFieldXCoord(),
					alShots.get(i-1).getFieldYCoord(),
					alShots.get(i-1).getSpeakerXCoord(),
					alShots.get(i-1).getSpeakerYCoord(),
					alShots.get(i-1).getOutcome(),
					alShots.get(i-1).getMode()
					);
			
			// Use this Shot object to replace the data in the corresponding alShots and alMatchHistory ArrayLists  
			alShots.set(i, s);
			NSShotsUtils.alMatchHistory.get(iMatchIndex).getShots().set(i, s);
		}		
		
		// Now increment ishot index
		iShotIndex = iShotIndex + 1;
		
		// Create a new blank Shot object
		s = new Shot( (iShotIndex+1), "",0,0,0,0,"Other",cboxMode.getValue());	
		
		// Use this Shot object to replace the data in the corresponding alShots and alMatchHistory ArrayLists  
		alShots.set(iShotIndex, s);
		NSShotsUtils.alMatchHistory.get(iMatchIndex).getShots().set(iShotIndex, s);
		    
		// Now write this data to file
		NSShotsUtils.writeMatchToFile();
		
		populateShotsOnTableView();
		setRowFocus();
	}

	public void deleteShotClick() {
		// This method is used to delete the current shot

		Shot s;
		// Now start moving all the data up one number		
		for (int i = iShotIndex; i < alShots.size()-1; i++) {	

			System.out.println("i = " + i);
			s = new Shot(
					alShots.get(i).getShotNumber(),
					alShots.get(i+1).getShotDate(),
					alShots.get(i+1).getFieldXCoord(),
					alShots.get(i+1).getFieldYCoord(),
					alShots.get(i+1).getSpeakerXCoord(),
					alShots.get(i+1).getSpeakerYCoord(),
					alShots.get(i+1).getOutcome(),
					alShots.get(i+1).getMode()
					);
			
			System.out.println(""+ s.getShotNumber()+s.getShotDate());
			
			// Use this Shot object to replace the data in the corresponding alShots and alMatchHistory ArrayLists  
			alShots.set(i, s);
			NSShotsUtils.alMatchHistory.get(iMatchIndex).getShots().set(i, s);
		}		
		
		// Delete the last shot
		alShots.remove(alShots.size()-1);
		    
		// Now write this data to file
		NSShotsUtils.writeMatchToFile();
		
		populateShotsOnTableView();
		setRowFocus();
	}
	
	public void showShot(Shot s) {
		// Given a Shot object, this method puts a shot circle on the field image for this shot.

		// If Show All Shots and Don't Erase Shots are not checked, then clear the old shots on screen
		if (!cboxDoNotEraseShots.isSelected() && !rbtnDisplayAll.isSelected() && !rbtnDisplayAuton.isSelected() && !rbtnDisplayTeleop.isSelected()) {
					clearAllShots();
		}
		
		// Calculate robot's direct distance from speaker 
        // Tried out converting it to feet or meters, but the ratio didn't work out, so abandoned.  Maybe revisit again later
//		int iRobotDistanceX;
//		int iRobotDistanceY;
//		double iRobotDistanceDirect;
//		double iRobotDistanceDirectConverted;
//		if (NSShotsUtils.alMatchHistory.get(iMatchIndex).getTeam().equals("Red")) {
//			iRobotDistanceX = NSShotsUtils.FIELD_X_MAX - s.getFieldXCoord();
//		} else {
//			iRobotDistanceX = s.getFieldXCoord() - NSShotsUtils.FIELD_X_MIN;
//		}
//        iRobotDistanceY = NSShotsUtils.SPEAKER_Y_COORD_ON_FIELD - s.getFieldXCoord();
//        iRobotDistanceDirect = Math.sqrt((Math.pow(iRobotDistanceX, 2))+(Math.pow(iRobotDistanceY, 2)));
//        iRobotDistanceDirectConverted = iRobotDistanceDirect * NSShotsUtils.CONVERSION_RATE;

		// Create tool tip text       
        String sToolTipText = "Shot #" + s.getShotNumber() + " - " + s.getOutcome();

        // Tried out converting it to feet or meters, but the ratio didn't work out, so abandoned.  Maybe revisit again later
//        DecimalFormat df = new DecimalFormat("#.0000");  
//        String sToolTipText = "Shot #" + s.getShotNumber() + " - " + s.getOutcome() + " - " + df.format(iRobotDistanceDirectConverted) + " " + NSShotsUtils.CONVERSION_UNIT + " from speaker";

		// Determine the color of the shot based on accuracy
		if (s.getOutcome().equals("Scored")) {
    		addCirMarker(NSShotsUtils.FIELD, s.getFieldXCoord(), s.getFieldYCoord(), false, Color.GREEN, getDropShadow(s), sToolTipText);
    		addCirMarker(NSShotsUtils.SPEAKER, s.getSpeakerXCoord(), s.getSpeakerYCoord(), false, Color.GREEN, getDropShadow(s), sToolTipText);			
		} else if (s.getOutcome().equals("In and Out")) {
       		addCirMarker(NSShotsUtils.FIELD, s.getFieldXCoord(), s.getFieldYCoord(), false, Color.ORANGE, getDropShadow(s), sToolTipText);
       		addCirMarker(NSShotsUtils.SPEAKER, s.getSpeakerXCoord(), s.getSpeakerYCoord(), false, Color.ORANGE, getDropShadow(s), sToolTipText);
		} else if (s.getOutcome().equals("Too High")) {
			if (
				s.getSpeakerYCoord() < NSShotsUtils.SPEAKER_ALMOST_Y_MAX && //bottom
				s.getSpeakerYCoord() > NSShotsUtils.SPEAKER_ALMOST_Y_MIN &&	//top
				s.getSpeakerXCoord() < NSShotsUtils.SPEAKER_ALMOST_X_MAX &&	//left
				s.getSpeakerXCoord() > NSShotsUtils.SPEAKER_ALMOST_X_MIN	//right
			) {
				addCirMarker(NSShotsUtils.FIELD, s.getFieldXCoord(), s.getFieldYCoord(), false, Color.PINK, getDropShadow(s), sToolTipText);
				addCirMarker(NSShotsUtils.SPEAKER, s.getSpeakerXCoord(), s.getSpeakerYCoord(), false, Color.PINK, getDropShadow(s), sToolTipText);
			} else {
				addCirMarker(NSShotsUtils.FIELD, s.getFieldXCoord(), s.getFieldYCoord(), false, Color.RED, getDropShadow(s), sToolTipText);
				addCirMarker(NSShotsUtils.SPEAKER, s.getSpeakerXCoord(), s.getSpeakerYCoord(), false, Color.RED, getDropShadow(s), sToolTipText);				
			}
		} else if (s.getOutcome().equals("Too Low")) {
			if (
					s.getSpeakerYCoord() < NSShotsUtils.SPEAKER_ALMOST_Y_MAX && //bottom
					s.getSpeakerYCoord() > NSShotsUtils.SPEAKER_ALMOST_Y_MIN &&	//top
					s.getSpeakerXCoord() < NSShotsUtils.SPEAKER_ALMOST_X_MAX &&	//left
					s.getSpeakerXCoord() > NSShotsUtils.SPEAKER_ALMOST_X_MIN	//right
				) {				
				addCirMarker(NSShotsUtils.FIELD, s.getFieldXCoord(), s.getFieldYCoord(), false, Color.CYAN, getDropShadow(s), sToolTipText);
				addCirMarker(NSShotsUtils.SPEAKER, s.getSpeakerXCoord(), s.getSpeakerYCoord(), false, Color.CYAN, getDropShadow(s), sToolTipText);
			} else {
				addCirMarker(NSShotsUtils.FIELD, s.getFieldXCoord(), s.getFieldYCoord(), false, Color.MEDIUMBLUE, getDropShadow(s), sToolTipText);
				addCirMarker(NSShotsUtils.SPEAKER, s.getSpeakerXCoord(), s.getSpeakerYCoord(), false, Color.MEDIUMBLUE, getDropShadow(s), sToolTipText);				
			}
		} else { 
    		addCirMarker(NSShotsUtils.FIELD, s.getFieldXCoord(), s.getFieldYCoord(), false, Color.PURPLE, getDropShadow(s), sToolTipText);
    		addCirMarker(NSShotsUtils.SPEAKER, s.getSpeakerXCoord(), s.getSpeakerYCoord(), false, Color.PURPLE, getDropShadow(s), sToolTipText);
		}
        	
	}
	
	private DropShadow getDropShadow(Shot s) {
		// Determine the color of the drop shadow based on mode
		if (s.getMode().equals("Auton")) return dropShadowLime;
		else return dropShadowWhite;
	}

	public void showAllShots() {
		// This method add a shot circle on top of the field image for every shot in this match.		
		// Loop through all shots and display it on the screen
		rbtnDisplayAuton.setSelected(false);
		rbtnDisplayTeleop.setSelected(false);
		alShots.forEach(shot->{
			showShot(shot);
			});
	}

	public void showAllAuton() {
		// This method add a shot circle on top of the field image for every auton shot in this match.		
		// Loop through all shots and display it on the screen
		rbtnDisplayAll.setSelected(false);
		rbtnDisplayTeleop.setSelected(false);
		alShots.forEach(shot->{
			if (shot.getMode().equals("Auton")) showShot(shot);
		});
	}

	public void showAllTelop() {
		// This method add a shot circle on top of the field image for every teleop shot in this match.		
		// Loop through all shots and display it on the screen
		rbtnDisplayAll.setSelected(false);
		rbtnDisplayAuton.setSelected(false);
		alShots.forEach(shot->{
			if (shot.getMode().equals("Teleop")) showShot(shot);
		});
	}

	public void clearAllShots() {
		// This method clear all shot circles on top of the field image.
		pMapPane.getChildren().removeIf(Circle.class::isInstance);
		pSpeakerPane.getChildren().removeIf(Circle.class::isInstance);				
	}

	public void updateSummaryField() {
		// This method updates the summary field with the info about this match
		int shotsScoredTeleop = 0;
		int shotsScoredAuton = 0;
		int shotsTotalTeleop = 0;
		int shotsTotalAuton = 0;
		Shot s;
		Iterator<Shot> it = alShots.iterator();
		while (it.hasNext()) {
			s = it.next();
			if (s.getMode().equals("Auton")) {
				if (s.getOutcome().equals("Scored")) shotsScoredAuton++; 
				shotsTotalAuton++;
			} else {
				if (s.getOutcome().equals("Scored")) shotsScoredTeleop++; 
				shotsTotalTeleop++;
			}			
		}
		txtAutonSummary.setText(txtMatchName.getText() + " AUTON: " + shotsScoredAuton + "/" + shotsTotalAuton + " speaker");			
		txtTeleopSummary.setText(txtMatchName.getText() + " TELEOP: " + shotsScoredTeleop + "/" + shotsTotalTeleop + " speaker + " + txtAmpShots.getText() + " amp");			
	}

	public void goToShotsTrackerClick() {
		// This method just closes the Shots Analyzer window and 
		// take the user back to the shot tracker window
		Stage thisStage = (Stage) txtMatchName.getScene().getWindow();
		thisStage.close();
	}
	
}
