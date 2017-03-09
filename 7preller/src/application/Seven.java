package application;

// The Seven Swabians

import javafx.application.*;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.stage.*;
import javafx.scene.layout.*;


public class Seven extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	public void start(Stage myStage) {

		// Setup the stage
		myStage.setTitle("The Seven Swabians");

		// Setup the graphical elements
		SevenCanvas observationCanvas = new SevenCanvas();
		SevenCanvas discoveryCanvas = new SevenCanvas();
		SevenControllerPane sevenPane = new SevenControllerPane(observationCanvas, discoveryCanvas);

		// Create the HBox. 
		HBox rootNode = new HBox(10);

		rootNode.setPadding(new Insets(10, 10, 10, 10));

		rootNode.getChildren().addAll(sevenPane, observationCanvas, discoveryCanvas);

		// Create a scene. 
		Scene myScene = new Scene(rootNode, rootNode.getMinWidth(), rootNode.getMinHeight());

		// Set the scene on the stage.  
		myStage.setScene(myScene);
		myStage.setResizable(false);

		// Show the stage and its scene.
		myStage.show();

	}
	
}
