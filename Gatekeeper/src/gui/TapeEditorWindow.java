package gui;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TapeEditorWindow extends Application
{

	public static void main(String[] args) 
	{
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception 
	{
		Scene scene = new Scene(new Group(), 900, 600);
		TapeEditor tapeEditor = new TapeEditor();
		((Group) scene.getRoot()).getChildren().addAll(tapeEditor);
		
		stage.setTitle("Tape Editor");
		stage.setScene(scene);
		stage.show();
	}

}
