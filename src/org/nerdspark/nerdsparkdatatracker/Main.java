package org.nerdspark.nerdsparkdatatracker;

import javafx.application.Application;

// This Main class was added to call the MainWindow class because we ran into JavaFX errors
// when running the executable jar with the MainWindow as the starting class.  Adding this
// class on top seems to alleviate the issue.

// To run this, you must: 
// 1. Have java 17 or above installed.  (I used eclipse for my IDE.)
// 2. Have JavaFX 17.0.10 jdk or above installed.  (I used e(fx)clipse for JavaFX.) 
// 3. Include the following in the VM arguments: (adjust to your own folder structure)
// --module-path C:\Projects\openjfx-21_windows-x64_bin-sdk\javafx-sdk-21\lib  --add-modules=javafx.controls,javafx.fxml

// Mac or Linux: 
// If you created an executable jar with this code, and want to run it on a Mac or Linux machine, do this:
// 1. Install java 17 jdk onto the machine.  
//      https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html
// 2. Download the javafx 17.0.10 jdk onto the same directory where you have the NerdSparkDataTracker.jar
//      https://gluonhq.com/products/javafx/
// 3. To run it, go to your terminal, navigate to that directory where you have your jar and javafx sdk, 
//      then type in the following to run it (changing the path to your own path):
//        java --module-path <location of javafx is installed>/javafx-sdk-17.0.10/lib --add-modules javafx.controls,javafx.fxml -jar NerdSparkDataTracker.jar
//      Alternatively, you can create a shell script with the above so you don't have to retype this.

// Windows: 
// If you created an executable jar with this code, and want to run it on a Windows machine, first,  
//   you need to make sure the machine has java and javafx installed (steps 1 and 2 in the Mac directions  
//   above).  Then try one or more of the following:  (only 3 and 4 below worked for me. I prefer 4.) 
//     1. Try double-clicking on the jar file and see if it loads.  If it does, you are all set.  
//     2. Try following the same steps for Mac above.  If it works, you are all set
//     3. Under your JavaFX folder structure, you should see a bin directory.  
//          a) Copy and paste the whole bin directory in the same directory where you have the 
//               NerdSparkDataTracker.jar file.
//          b) Now double-click on the NerdSparkDataTracker.jar file to run it.  If it works, you're all set.
//     4. Under your JavaFX folder structure, you should see a bin directory.
//          a) Open the NerdSparkDataTracker.jar archive file with a file archiver app like 7-zip.
//          b) Select all the contents of the JavaFX bin folder, and add it into the root of the  
//               NerdSparkDataTracker.jar archive file. 
//          c) Now double-click on the NerdSparkDataTracker.jar file to run it.  If it works, you're all set.
//     > If you followed 3 or 4 above, and the app opens, but the images are missing:
//          a) Copy all the .png files from the src folder, and add it to the same folder of the 
//               NerdSparkDataTracker.jar file
//          b) Open the NerdSparkDataTracker.jar archive file with a file archiver app like 7-zip.
//               and add all the png files from the src folder into the archive file.

public class Main {

	public static void main(String[] args) {
        Application.launch(MainWindow.class);
	}

}  
