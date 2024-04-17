package org.nerdspark.nerdsparkdatatracker;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

// This is a utility class with variables and methods that 
// can be called by either of the controllers.  

public final class NSShotsUtils {

	public static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/uu HH:mm:ss");
	public static String HISTORY_FILE_NAME = "NerdSparkMatchHistory.json";
	public static ArrayList<MatchHistory> alMatchHistory = new ArrayList<MatchHistory>();
		
	// Robot Dimensions
	public static int ROBOT_SIZE = 23;
	public static int HALF_ROBOT_SIZE = ROBOT_SIZE/2;
	
	// These are the boundaries of the field itself
	public static int FIELD_X_MIN = 140;
	public static int FIELD_X_MAX = 895;
	public static int FIELD_Y_MIN = 40;
	public static int FIELD_Y_MAX = 415;

	// These are the limits of where the robot can go inside the field
	public static int FIELD_X_LIMIT_MIN = FIELD_X_MIN + HALF_ROBOT_SIZE;
	public static int FIELD_X_LIMIT_MAX = FIELD_X_MAX - HALF_ROBOT_SIZE;
	public static int FIELD_Y_LIMIT_MIN = FIELD_Y_MIN + HALF_ROBOT_SIZE;
	public static int FIELD_Y_LIMIT_MAX = FIELD_Y_MAX - HALF_ROBOT_SIZE;
	
	// Note dimensions
	public static int NOTE_WIDTH = 37;
	public static int NOTE_HEIGHT = 8;
	public static int HALF_NOTE_WIDTH = NSShotsUtils.NOTE_WIDTH/2;
	public static int HALF_NOTE_HEIGHT = NSShotsUtils.NOTE_HEIGHT/2;
		
	// These are the boundaries of the speaker photo itself 
	public static int SPEAKER_X_MIN = 0;
	public static int SPEAKER_X_MAX = 308;
	public static int SPEAKER_Y_MIN = 0;
	public static int SPEAKER_Y_MAX = 338;

	// These are the coordinates where we accept a "scored" (to be shown as green)
	public static int SPEAKER_SCORE_X_MIN = 110;
	public static int SPEAKER_SCORE_X_MAX = 205;
	public static int SPEAKER_SCORE_Y_MIN = 117;
	public static int SPEAKER_SCORE_Y_MAX = 131;
	
	// These are the coordinates where we accept a being closed to scoring (to be shown as yellow)
	public static int SPEAKER_ALMOST_X_MIN = SPEAKER_SCORE_X_MIN - 20;
	public static int SPEAKER_ALMOST_X_MAX = SPEAKER_SCORE_X_MAX + 20;
	public static int SPEAKER_ALMOST_Y_MIN = SPEAKER_SCORE_Y_MIN - 20;
	public static int SPEAKER_ALMOST_Y_MAX = SPEAKER_SCORE_Y_MAX + 20;
	
	// This is the Y coordinate of where the speaker is relative to the field.
	public static int SPEAKER_Y_COORD_ON_FIELD = 159;
	
	// Conversion rates from pixel to meters or feet
	// This didn't work out right.  May be revisit later.
//	public static double CONVERSION_RATE = 0.071944453;   // calculated from 26.97917 (length of field in Y) / 375 (pixel # for Y)  
//	public static String CONVERSION_UNIT = "feet";
//	public static double CONVERSION_RATE = 0.021893333;   // calculated from = 8.21 (length of field in Y) / 375 (pixel # for Y)  
//	public static String CONVERSION_UNIT = "meters";
	
	
	// These are flip offset values to make sure that the flipped image lines up properly
	public static int FLIP_OFFSET_X = 4;
	public static int FLIP_OFFSET_Y = -2;
	
	// Constants used to identify FIELD or SPEAKER
	public static int FIELD = 1;
	public static int SPEAKER = 2;
	
	// Circle size on field or speaker
	public static int CIRCLE_SIZE = 6;
	
	// Created a private constructor because this is a utils class
	private NSShotsUtils(){}

    public static String getNow() {    	
    	// This method returns a string of the current date and time
		LocalDateTime dt = LocalDateTime.now();
		return dt.format(dtf);
	}
    
    public static void readHistoryFile (String fileName){

        // This method is used to read the JSON file and load the  
		// data into the alMatchHistory ArrayList
		
    	System.out.println("Reading file " + HISTORY_FILE_NAME + " to get history data.");
    	
		JsonArray jsonArray = new JsonArray();
        
        try {
            JsonElement jsonElement = JsonParser.parseReader(new FileReader(fileName));
            jsonArray = jsonElement.getAsJsonArray();
        } catch (FileNotFoundException e) {
           System.out.println("Warning - File not found: " + fileName);
        } catch (@SuppressWarnings("hiding") IOException ioe){
        	System.out.println("Warning - IO Exception: " + fileName);
        }
        
		Gson gson = new Gson();
		Type listType = new TypeToken<List<MatchHistory>>(){}.getType();
		alMatchHistory = gson.fromJson(jsonArray, listType);                
    }

    public static void readHistoryFile (){
    	// Overloaded this method to automatically pass in the 
    	// HISTORY_FILE_NAME by default if no filename was provided 
    	readHistoryFile(HISTORY_FILE_NAME);
    }
    
	public static void writeMatchToFile(String fileName)  {

		// Write the whole alMatchHistory ArrayList to a JSON output file using GSON 
        try (FileWriter writer = new FileWriter(fileName)) { 
    		Gson gson = new Gson();
            gson.toJson(alMatchHistory, writer); 
        } 
        catch (IOException e) { 
        	System.out.println("Unable to write to file: " + fileName);
            e.printStackTrace(); 
        } 
		System.out.println("Updated this match to the match history file: " + fileName);			
	}

	public static void writeMatchToFile()  {
    	// Overloaded this method to automatically pass in the 
    	// HISTORY_FILE_NAME by default if no filename was provided 
		writeMatchToFile(HISTORY_FILE_NAME);
	}
	
	public static void showSimpleDialog(String title, String message) {
		// Use this method to show a simple dialog box with an ok button.
		Dialog<String> dialog = new Dialog<String>();
		dialog.setTitle(title);
		ButtonType type = new ButtonType("Ok", ButtonData.OK_DONE);
		dialog.setContentText(message);
		dialog.getDialogPane().getButtonTypes().add(type);
		dialog.showAndWait();
	}

	public static int calculateRobotX(int x) {
		// Set the boundaries of X within the field
		if (x <= FIELD_X_LIMIT_MIN) return FIELD_X_LIMIT_MIN;
		else if (x >= FIELD_X_LIMIT_MAX) return FIELD_X_LIMIT_MAX;
		return x;
	}
 
	public static int calculateRobotY(int y) {
		// Set the boundaries of Y within the field
		if (y <= FIELD_Y_LIMIT_MIN) return FIELD_Y_LIMIT_MIN;
		else if (y >= FIELD_Y_LIMIT_MAX) return FIELD_Y_LIMIT_MAX;
		return y;
	}
	
	public static int getFlippedValueX(boolean isFlipped, int x) {
		// If the map is flipped, then flip the x values too
		if (isFlipped) {
			return (FIELD_X_LIMIT_MAX + FIELD_X_LIMIT_MIN + FLIP_OFFSET_X - x);
		} else {
			return x;
		}
	}

	public static int getFlippedValueY(boolean isFlipped, int y) {
		// If the map is flipped, then flip the y values too
		if (isFlipped) {
			return (FIELD_Y_LIMIT_MAX + FIELD_Y_LIMIT_MIN + FLIP_OFFSET_Y - y);
		} else {
			return y;
		}
	}
}
