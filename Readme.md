# The N.E.R.D Spark Data Tracker

The N.E.R.D. Spark Data Tracker is our Data Science team's solution to recording, compiling, and analyzing shot accuracy data for the 2024 First Robotics Competition.

Using a JavaFX gui, and a point-and-click interface, users can use the app to record the positional data of shots taken in real-time, and look for patterns where shots land either consistently high, low, or scored. This data is then provided to the programming, strategy, and drive teams to assist them in decisions on how to improve.

Read our [Writeup Paper](./9312_NERD_Spark_Data_Tracker.pdf) for an in-depth review of our process and results!

## Usage

To run the compiled jar file in **Windows**:
1. Install a [Java 17+ JDK](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) onto the machine.  
2. Double click the the file!


To run the compiled jar file in **Mac** or **Linux**:
1. Install a [Java 17+ JDK](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) onto the machine.  
      
2. Download and unzip the [JavaFX 17.0.10+ SDK](https://gluonhq.com/products/javafx/) into the same directory where you have the NerdSparkDataTracker.jar


3. To run it, go to your terminal, navigate to that directory where you have your jar and javafx sdk, then type in the following to run it (changing the path to your own path):
     ```
     java --module-path <location of javafx is installed>/javafx-sdk-<Version>/lib --add-modules javafx.controls,javafx.fxml -jar NerdSparkDataTracker.jar
     ```
     For convenience, you can also pipe this into a `run.sh` script so you don't have to retype this every time. This would look like:
     ```
     "<configured command above>" > run.sh
     ```

## Compiling

The N.E.R.D. Spark Data Tracker is written with Java using the JavaFX library for UI controls. To run this from the source code, you must: 
1. Have Java 17 or above installed.
2. Have JavaFX 17.0.10 jdk or above installed.
3. Include the following in the VM arguments (adjust to your own JavaFX sdk location): 
```
    --module-path <location of javafx is installed>/javafx-sdk-<Version>/lib
    --add-modules=javafx.controls,javafx.fxml
```

