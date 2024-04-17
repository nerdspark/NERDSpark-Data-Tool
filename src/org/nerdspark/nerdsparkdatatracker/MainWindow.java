package org.nerdspark.nerdsparkdatatracker;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

// This program uses JavaFX to create the Nerd Spark Shots Tracker UI.  

// To run this, you must include the following in the VM arguments: (adjust to your own folder structure)
// --module-path C:\Projects\openjfx-21_windows-x64_bin-sdk\javafx-sdk-21\lib  --add-modules=javafx.controls,javafx.fxml

public class MainWindow extends Application {
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("NSShotsTracker.fxml"));
		Scene currentScene = new Scene(root, 1300, 850);
		primaryStage.setTitle("N.E.R.D Spark Shots Tracker");
		primaryStage.setScene(currentScene);
		primaryStage.show();		
	}

	public static void main(String[] args) {
		launch(args);
	}
}
