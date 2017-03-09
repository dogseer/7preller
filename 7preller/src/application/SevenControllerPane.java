package application;

import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.scene.control.*;
import javafx.event.*;
import javafx.geometry.Insets;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.io.File;
import java.util.concurrent.Semaphore;

import foundation.*;
import ai.AI;

public class SevenControllerPane extends VBox {
	
	public static final int NUMBEROFSWABIANS = 7;

	private Map map = null;
	private int round;
	private int finish;
	private Timeline timeline = null;
	private Position swabian[] = null;
	private AI ai = null;
	
	private Semaphore semaphore = null;
	
	// GUI elements
	private Label lblRound = new Label();
	private Label lblFinish = new Label();
	private Button btnLoad = new Button("Load");
	private Button btnGo = new Button("Go");
	private Button btnStop = new Button("Stop");
	
	// Canvas
	private SevenCanvas observationCanvas = null;
	private SevenCanvas discoveryCanvas = null;

	public SevenControllerPane(SevenCanvas observationCanvas, SevenCanvas discoveryCanvas) {
		super(10);
		this.observationCanvas = observationCanvas;
		this.discoveryCanvas = discoveryCanvas;

		// Set graphical properties

		this.setPadding(new Insets(0, 10, 20, 10));

		// Set up the animation
		timeline = new Timeline(new KeyFrame(Duration.millis(10), ae -> playRound()));
		timeline.setCycleCount(Timeline.INDEFINITE);

		// Set the "Load" button 
		btnLoad.setMinWidth(150);
		btnLoad.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent ae) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Load Map");
				fileChooser.setInitialDirectory(new File(System.getProperty("user.dir"))); 
				fileChooser.getExtensionFilters().addAll(
						new FileChooser.ExtensionFilter("MAP", "*.map"),
						new FileChooser.ExtensionFilter("XML", "*.xml"),
						new FileChooser.ExtensionFilter("All Files", "*.*")
						);
				File file = fileChooser.showOpenDialog(btnLoad.getScene().getWindow());
				if (file != null) {
					map = new Map(file.getAbsolutePath());
					observationCanvas.show(map);
					discoveryCanvas.clear();
					btnGo.setDisable(false);
					lblRound.setText("");
					lblFinish.setText("");
				}
			}
		});

		// Set the "Go" button 
		btnGo.setMinWidth(150);
		btnGo.setDisable(true);
		btnGo.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent ae) {
				// Set up the GUI
				finish = 0;
				round = 0;
				observationCanvas.show(map);
				discoveryCanvas.clear();
				btnStop.setDisable(false);
				btnLoad.setDisable(true);
				btnGo.setDisable(true);
				lblRound.setText("Round:\t" + round);
				lblFinish.setText("Finish:\t" + finish);
				// Set up the Swabians
				swabian = new Position[NUMBEROFSWABIANS];
				for (int i = 0; i < NUMBEROFSWABIANS; ++i) 
					swabian[i] = new Position(map.getStart());
				// initialize semaphore and play animation
				semaphore = new Semaphore(1);
				timeline.playFromStart();
				// start up AI
				ai = new AI();
			}
		});

		// Set the "Stop" button 
		btnStop.setMinWidth(150);
		btnStop.setDisable(true);
		btnStop.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent ae) {
				btnStop.setDisable(true);
				btnLoad.setDisable(false);
				btnGo.setDisable(false);
				timeline.stop();
			}
		});

		// Add them to the box. 
		getChildren().addAll(btnLoad, btnGo, btnStop, new Separator(), lblRound, lblFinish);

	}

	
	private void playRound() {
		
		// Check if accessible
		if (!semaphore.tryAcquire()) 
			return;
		
		// New round
		++round;
		lblRound.setText("Round:\t" + round);
		
		// Call up all active Swabians
		for (int i = 0; i < NUMBEROFSWABIANS; ++i) { 
			if (swabian[i] != null) {
			// Call up AI and accept move
				Environment e = new Environment(map, swabian[i]);
				discoveryCanvas.show(e);
				Direction dir = ai.move(i, e);
			// Legal move?
				Position newpos = (new Position(swabian[i])).direct(dir);
				boolean swabianCollision = false;
				for (int j = 0; j < NUMBEROFSWABIANS; ++j) {
					if (swabian[j] != null && swabian[j].equals(newpos)) 
						swabianCollision = true;
				}
				if (swabianCollision)
					continue;
				MapElement me = map.getAt(newpos);
				if (me == MapElement.WATER || me == MapElement.START) 
					continue;
			// Display move --- in finish???
				observationCanvas.show(map, swabian[i], false);
                swabian[i] = newpos;
                if (swabian[i].equals(map.getFinish())) {
                	swabian[i] = null;
                	++finish;
    				lblFinish.setText("Finish:\t" + finish);
                } else {
                	observationCanvas.show(map, swabian[i], true);
                }
			// All Finished ??
                if (finish == NUMBEROFSWABIANS) {
    				btnStop.setDisable(true);
    				btnLoad.setDisable(false);
    				btnGo.setDisable(false);
                	timeline.stop();                	
                }
			}
		}
		// open the way
		semaphore.release();
	}
	
}
