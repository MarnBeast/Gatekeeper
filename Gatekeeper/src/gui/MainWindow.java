package gui;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import model.Constants;
import model.Tape;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.stage.Stage;

public class MainWindow extends Application{

	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Group root = new Group();
		
		String projectRootDirectory = getHostServices().getDocumentBase();
		Media media = new Media(projectRootDirectory + "src/tests/Clip01.mp4");
		
		final VideoPlayer player = new VideoPlayer(media);
		player.setWidth(200);
		final VideoPlayer player2 = new VideoPlayer(media);
		player2.setWidth(200);
		final VideoPlayer player3 = new VideoPlayer(media);
		player3.setWidth(200);
		final VideoPlayer player4 = new VideoPlayer(media);
		player4.setWidth(200);
		
		HBox hbox = new HBox();
		HBox hbox2 = new HBox();
		VBox vBox = new VBox();
		hbox.getChildren().addAll(player, player2);//, player2, player3, player4);
		hbox2.getChildren().addAll(player3, player4);//, player2, player3, player4);
		HBox.setMargin(player, new Insets(5));
		HBox.setMargin(player2, new Insets(5));
		HBox.setMargin(player3, new Insets(5));
		HBox.setMargin(player4, new Insets(5));
		vBox.getChildren().addAll(hbox, hbox2);
		
		root.getChildren().add(vBox);
		
		Scene scene = new Scene(root, 600, 400);
		primaryStage.setScene(scene);
		scene.getStylesheets().add(MainWindow.class.getResource("GKStyle.css").toExternalForm());
		//System.out.println(MainWindow.class.getResource("GKStyle.css"));
		primaryStage.show();
		
		player.play();
		player2.play();
		player3.play();
		player4.play();
		
		Thread thread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					Thread.sleep(5000);
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				player.setWidth(500);
				player2.setWidth(500);
				player3.setWidth(500);
				player4.setWidth(500);
				
				SerializeTestTape("C:\\Users\\MarnBeast\\Videos\\atmosfear clips\\Main Tape\\");
			}
		});
		
		thread.start();
	}
	
	
	public void SerializeTestTape(String basePath)
	{
		File folder = new File(basePath);
		File[] listOfFiles = folder.listFiles();
		String[] filePaths = new String[listOfFiles.length];
		int fileIndex = 0;
		for (File file : listOfFiles)
		{
			filePaths[fileIndex] = file.toURI().toString();
			fileIndex++;
		}
		
		Tape tape = new Tape();
		tape.addClips(filePaths, true);
		try
		{
			tape.saveTape(basePath + "TestTape" + Constants.TAPE_EXTENSION);
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
