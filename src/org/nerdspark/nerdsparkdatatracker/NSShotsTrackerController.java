package org.nerdspark.nerdsparkdatatracker;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

// This is the controller class for the NERD Spark Shots Tracker.  
// Most of the logic is in this class.  

public class NSShotsTrackerController implements Initializable {
	
	// Declare all variables
	@FXML private BorderPane pParent;
	@FXML private Button btnSubmitShot;
	@FXML private Button btnSkipShot;
	@FXML private Button btnStart;
	@FXML private Button btnEnd;
	@FXML private Button btnGoToShotsAnalyzer;
	@FXML private Button btnAddAmpShot;	
	@FXML private ImageView imgMap;
	@FXML private Pane pMapPane;
	@FXML private ImageView imgSpeaker;
	@FXML private Pane pSpeakerPane;
	@FXML private Label lblError;
	@FXML private TextArea txtStatus;
	@FXML private TextField txtMatchName;
	@FXML private TextField txtMatchTime;
	@FXML private Label lblMatchTime;
	@FXML private Label lblMatchName;
	@FXML private TextField txtXCoord;
	@FXML private TextField txtYCoord;
	@FXML private Label lblXCoord;
	@FXML private Label lblYCoord;
	@FXML private Label lblShotTime;
	@FXML private TextField txtShotTime;
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
	@FXML private Label lblTeam;
	@FXML private ChoiceBox<String> cboxTeam;
	@FXML private ChoiceBox<String> cboxMode;
	@FXML private CheckBox cboxAutonMode;
	@FXML private Label lblMode;
	@FXML private CheckBox cboxFlip;
	@FXML private Label lblAmpShot;
	@FXML private TextField txtAmpShots;
	
	
	private ArrayList<Shot> alShots = new ArrayList<Shot>();
	private Rectangle marker3;
	private Rectangle marker4;
	private int iShotCounter = 0;
	private Image imgFlip = new Image(getClass().getResourceAsStream("2024FRCField_flip.png"));
	private Image imgOrig = new Image(getClass().getResourceAsStream("2024FRCField_orig.png"));
	

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		// When the program initializes, display the default items
		System.out.println("Initializing NERD Spark Shots Tracker!");
		displayButtons(false);
		
		// Populate Team drop down
		cboxTeam.getItems().add("Red");
		cboxTeam.getItems().add("Blue");
		cboxTeam.getSelectionModel().selectFirst();

		// Populate Mode drop down
		cboxMode.getItems().add("Auton");
		cboxMode.getItems().add("Teleop");
		cboxMode.getSelectionModel().selectFirst();

		// Also open the match history data file to see if there's any data.  
		// If so, load the data into an ArrayList so we can reference it later.
		NSShotsUtils.readHistoryFile();


		// Add a listener to listen for the right mouse click event and submit the shot when detected
		pParent.sceneProperty().addListener((observable, oldScene, newScene) -> {
	    	if (newScene != null) {
	    		pParent.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
	    			@Override
	    			public void handle(MouseEvent mouseEvent) {
	    				if (mouseEvent.getButton() == MouseButton.SECONDARY) {
	    					submitClick();
	    				}
	    			}
	    		});
	        }
	    });
		
		// Add a Listener to the Auton Mode check box to display or clear 
		// the shot circles on the field image
		cboxAutonMode.selectedProperty().addListener(new ChangeListener<Boolean>() {
		    @Override
		    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		    	if (cboxAutonMode.isSelected()) cboxMode.getSelectionModel().select("Auton");
		    	else cboxMode.getSelectionModel().select("Teleop");
		    }
		});
		
		// Add a Listener to the Flip check box to flip the field if necessary
		cboxFlip.selectedProperty().addListener(new ChangeListener<Boolean>() {
		    @Override
		    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		    	if (cboxFlip.isSelected()) imgMap.setImage(imgFlip);
		    	else imgMap.setImage(imgOrig);
		    }
		});
		
        // Add a listener to the field image to capture the x and y coordinates
        imgMap.setPickOnBounds(true);
        imgMap.setOnMouseClicked(e -> {
        	
        	// Record the x and y coordinates in the input fields
        	txtXCoord.setText(String.valueOf(NSShotsUtils.calculateRobotX((int)e.getX())));
        	txtYCoord.setText(String.valueOf(NSShotsUtils.calculateRobotY((int)e.getY())));

        	// Record the time that the shot was recorded
    		txtShotTime.setText(NSShotsUtils.getNow());
    		
        	// Put a red marker on the spot with those coordinates 
        	addRectMarkerOnField(Integer.parseInt(txtXCoord.getText()), Integer.parseInt(txtYCoord.getText()));
        	
        });

        // Add a listener to the speaker image to capture the x and y coordinates
        imgSpeaker.setPickOnBounds(true);
        imgSpeaker.setOnMouseClicked(e -> {
        	
        	// Record the x and y coordinates in the fields
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
	    	if (marker3 != null && remove) pMapPane.getChildren().remove(marker3);
	    	// Subtract half the robot size to center the rectangle 
			marker3 = new Rectangle(x - NSShotsUtils.HALF_ROBOT_SIZE, y - NSShotsUtils.HALF_ROBOT_SIZE, NSShotsUtils.ROBOT_SIZE, NSShotsUtils.ROBOT_SIZE);
			marker3.setFill(c);
	    	pMapPane.getChildren().add(marker3);
		} else if (p == NSShotsUtils.SPEAKER) {
	    	if (marker4 != null && remove) pSpeakerPane.getChildren().remove(marker4);
			marker4 = new Rectangle(x - NSShotsUtils.HALF_NOTE_WIDTH, y - NSShotsUtils.HALF_NOTE_HEIGHT, NSShotsUtils.NOTE_WIDTH, NSShotsUtils.NOTE_HEIGHT);
			marker4.setFill(c);
			pSpeakerPane.getChildren().add(marker4);		
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
			y > NSShotsUtils.SPEAKER_SCORE_Y_MIN &&		//top	
			x < NSShotsUtils.SPEAKER_SCORE_X_MAX && 	//left
			x > NSShotsUtils.SPEAKER_SCORE_X_MIN) {		//right
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
	
	private void displayButtons(boolean inMatch) {
		// This method is used to change the visibility of the buttons.  

		if (inMatch) {
			// If we are currently in a match...
			btnStart.setVisible(false);
			btnStart.setManaged(false);
			btnGoToShotsAnalyzer.setVisible(false);
			btnGoToShotsAnalyzer.setManaged(false);
			btnSubmitShot.setVisible(true);
			btnSubmitShot.setManaged(true);
			btnSkipShot.setVisible(true);
			btnSkipShot.setManaged(true);
			btnEnd.setVisible(true);
			btnEnd.setManaged(true);
			txtMatchName.setVisible(true);
			txtMatchTime.setVisible(true);
			lblMatchTime.setVisible(true);
			lblMatchName.setVisible(true);
			txtXCoord.setVisible(true);
			txtYCoord.setVisible(true);
			lblXCoord.setVisible(true);
			lblYCoord.setVisible(true);
			txtSpeakerXCoord.setVisible(true);
			txtSpeakerYCoord.setVisible(true);
			lblSpeakerXCoord.setVisible(true);
			lblSpeakerYCoord.setVisible(true);
			lblShotTime.setVisible(true);
			txtShotTime.setVisible(true);
			rbtnTooHigh.setVisible(true);
			rbtnScored.setVisible(true);
			rbtnInAndOut.setVisible(true);
			rbtnTooLow.setVisible(true);
			rbtnOther.setVisible(true);
			imgMap.setVisible(true);
			imgSpeaker.setVisible(true);
			lblTeam.setVisible(true);
			cboxTeam.setVisible(true);
			cboxAutonMode.setVisible(true);
			cboxMode.setVisible(true);
			lblMode.setVisible(true);
			cboxFlip.setVisible(true);
			txtAmpShots.setVisible(true);
			btnAddAmpShot.setVisible(true);
			lblAmpShot.setVisible(true);
		} else {
			// If we are currently NOT in a match...
			btnStart.setVisible(true);
			btnStart.setManaged(true);
			btnGoToShotsAnalyzer.setVisible(true);
			btnGoToShotsAnalyzer.setManaged(true);			
			btnSubmitShot.setVisible(false);
			btnSkipShot.setVisible(false);
			btnSkipShot.setManaged(false);
			btnEnd.setVisible(false);
			btnEnd.setManaged(false);	
			txtMatchName.setVisible(false);
			txtMatchTime.setVisible(false);
			lblMatchTime.setVisible(false);
			lblMatchName.setVisible(false);
			txtXCoord.setVisible(false);
			txtYCoord.setVisible(false);
			lblXCoord.setVisible(false);
			lblYCoord.setVisible(false);
			txtSpeakerXCoord.setVisible(false);
			txtSpeakerYCoord.setVisible(false);
			lblSpeakerXCoord.setVisible(false);
			lblSpeakerYCoord.setVisible(false);
			lblShotTime.setVisible(false);
			txtShotTime.setVisible(false);
			rbtnTooHigh.setVisible(false);
			rbtnScored.setVisible(false);
			rbtnInAndOut.setVisible(false);
			rbtnTooLow.setVisible(false);
			rbtnOther.setVisible(false);
			imgMap.setVisible(false);
			imgSpeaker.setVisible(false);
			lblTeam.setVisible(false);
			cboxTeam.setVisible(false);
			cboxAutonMode.setVisible(false);
			cboxMode.setVisible(false);
			lblMode.setVisible(false);
			cboxFlip.setVisible(false);
			txtAmpShots.setVisible(false);
			btnAddAmpShot.setVisible(false);
			lblAmpShot.setVisible(false);
		}
	}

	public void startClick() {
		// This method is called when the user clicks on the  
		// Start Match button to start a new match.
		System.out.println("Starting new match.");
		
		// Reset screen from previous match and hide/show appropriate buttons
		txtMatchTime.setText(NSShotsUtils.getNow());
		txtMatchName.setText("Quals # ");
		txtStatus.setText("");
		txtAmpShots.setText("0");
		cboxTeam.getSelectionModel().select("Red");
		cboxAutonMode.setSelected(true);
		displayButtons(true);	
		
	}

	private void clearShotFromScreen() {
		
		// This method is called to clear the shot info from the screen

		// Clear the contents of the Shot Time, X and Y Coordinates
		txtShotTime.setText("");
		txtXCoord.setText("");
    	txtYCoord.setText("");
    	txtSpeakerXCoord.setText("");
    	txtSpeakerYCoord.setText("");

    	// Clear the markers on the map too
    	if (marker3!= null) pMapPane.getChildren().remove(marker3);
		if (marker4 != null) pSpeakerPane.getChildren().remove(marker4);
	}

	public void submitClick() {
		// This method is called when the user clicks on the Submit button.
		// This will create an instance of the Shot object, put it in the 
		// array list, then display it to the right pane of the screen.
		
        RadioButton rb = (RadioButton)tgrOutcome.getSelectedToggle(); 

		// Perform some field validation
        if (txtShotTime.getText() == "" || !isInt(txtXCoord.getText()) || 
        		!isInt(txtYCoord.getText()) ||!isInt(txtSpeakerXCoord.getText()) ||
        		!isInt(txtSpeakerYCoord.getText()) || rb.getText() == "") {
        	lblError.setText("Insufficient data");
        	return;
        } else { 
        	lblError.setText("");
        }
		
        // Increase the shot number by 1
        iShotCounter++;
        
        // Create a Shot object and add it to the alShots ArrayList
		Shot currentShot = new Shot(
				iShotCounter,
				txtShotTime.getText(),
				NSShotsUtils.getFlippedValueX(cboxFlip.isSelected(), Integer.parseInt(txtXCoord.getText())), 
				NSShotsUtils.getFlippedValueY(cboxFlip.isSelected(), Integer.parseInt(txtYCoord.getText())), 
				Integer.parseInt(txtSpeakerXCoord.getText()), 
				Integer.parseInt(txtSpeakerYCoord.getText()), 
				rb.getText(), 
				cboxMode.getValue() 
				); 
		alShots.add(currentShot);
		
		// Display the shot info to the right pane of the screen
		txtStatus.appendText(currentShot.toString() + "\n");
		// Reset the input fields to prepare for the next shot
		clearShotFromScreen();
	}
	
	private boolean isInt(String s) {
		// This method is used to test if a string value can be 
		// converted into an int.
		try {
			Integer.parseInt(s);
			return true;			
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public void skipClick() {
		// This method is called when the user clicks on the Skip button.
		// This will create an empty instance of the Shot object, put it in    
		// the ArrayList, then display it to the right pane of the screen.

        //Increase the shot number by 1
        iShotCounter++;
        
        // Create a Shot object and add it to the shots ArrayList
		Shot currentShot = new Shot(iShotCounter, NSShotsUtils.getNow(), 0, 0, 0, 0, "Other", getSkipMode()); 
		alShots.add(currentShot);
		
		// Display the shot info to the right pane of the screen
		txtStatus.appendText(currentShot.toString() + "\n");

		// Reset the input fields to prepare for the next shot
		clearShotFromScreen();
	}
	
	private String getSkipMode() {
		if (cboxAutonMode.isSelected()) return "Auton";
		else return "Teleop";
		
	}
	
	@SuppressWarnings("unchecked")
	public void endClick() {
		// This method is called when the user clicks on the  
		// End Match button to end the match.
		System.out.println("Ending the match.");
		System.out.println(alShots.isEmpty());

		// Confirm there is data to store before proceeding
		if (alShots.isEmpty()) {
			// There are no shots submitted, so abandon the save.
			NSShotsUtils.showSimpleDialog("Submission Error", "No data to submit. Submission aborted and exiting.");			
		} else {
			// add the match history data to the alMatchHistory ArrayList first 
			NSShotsUtils.alMatchHistory.add(
					new MatchHistory(
							txtMatchName.getText(), 
							txtMatchTime.getText(), 
							cboxTeam.getValue(), 
							(ArrayList<Shot>) alShots.clone(),
							Integer.parseInt(txtAmpShots.getText())
					)
				);
			
			// Now write the whole alMatchHistory ArrayList to a JSON output file 
			NSShotsUtils.writeMatchToFile();			
		}		
		
		// Hide/Show appropriate buttons
		displayButtons(false);
		
		// Clear out old data
		alShots.clear();
		if (marker3 != null) pMapPane.getChildren().remove(marker3);
		if (marker4 != null) pSpeakerPane.getChildren().remove(marker4);
		iShotCounter = 0;    	   
	}

	public void addAmpShotClick() {
		// This method is called when the user clicks on the  
		// Add Amp Shot button to increment the amp shot
		

		Integer count = Integer.parseInt(txtAmpShots.getText()) + 1;
		txtAmpShots.setText(count.toString());
	}
	
	public void goToShotsAnalyzerClick() {
		// This is called when the user clicks on "Go to Shots Analyzer".
		// It loads the Shots Analyzer FXML in a new window.
	    try {
	        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("NSShotsAnalyzer.fxml"));
	        Parent root1 = (Parent) fxmlLoader.load();
	        Stage stage = new Stage();
	        stage.initStyle(StageStyle.DECORATED);
	        stage.initModality(Modality.APPLICATION_MODAL);
	        stage.setTitle("N.E.R.D Spark Shots Analyzer");
	        stage.setScene(new Scene(root1, 1300, 850));
	        stage.show();
	    } catch(Exception e) {
	        e.printStackTrace();
	    }
	}

}
