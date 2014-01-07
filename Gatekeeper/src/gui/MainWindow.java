package gui;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Map.Entry;

import model.Clip;
import model.Constants;
import model.Landmark;
import model.Settings;
import model.Tape;
import model.Timeline;
import model.Settings.ClipBaseTypes;

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
		
			//		q
		
		Thread thread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
//				try
//				{
//					Thread.sleep(5000);
//				} catch (InterruptedException e)
//				{
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				player.setWidth(500);
//				player2.setWidth(500);
//				player3.setWidth(500);
//				player4.setWidth(500);
				
				//SerializeTestTape("C:\\Users\\MarnBeast\\Videos\\atmosfear clips\\Main Tape\\");
				//SerializeTestTape("C:\\Users\\MarnBeast\\Videos\\atmosfear clips\\Booster 1\\");
				
				TimelineTest(new String[]{
						"C:\\Users\\MarnBeast\\Videos\\atmosfear clips\\Main Tape\\TestTape.gktape",
						"C:\\Users\\MarnBeast\\Videos\\atmosfear clips\\Booster 1\\TestTape.gktape"});
				//		"C:\\Users\\MarnBeast\\Videos\\atmosfear clips\\Main Tape\\Main Tape.gktape",
				//		"C:\\Users\\MarnBeast\\Videos\\atmosfear clips\\Booster 1\\Booster 1.gktape"});
			}
		});
		
		thread.start();
	}
	
	
	public void SerializeTestTape(String basePath)
	{
		File folder = new File(basePath);
		FileFilter filter = new FileFilter()
		{
			@Override
			public boolean accept(File pathname)
			{
				return pathname.getPath().matches(Constants.SUPPORTED_MEDIA_EXTENSIONS);
			}
		};
		File[] listOfFiles = folder.listFiles(filter);
		String[] filePaths = new String[listOfFiles.length];
		
		for (int i = 0; i < listOfFiles.length; i++)
		{
			filePaths[i]=listOfFiles[i].getPath(); 
		}
		Tape tape = new Tape();
		tape.addClips(filePaths, ClipBaseTypes.MISC, true);
		
		Clip[] clips = tape.getClips(ClipBaseTypes.MISC);
		
		Clip clip = clips[0];										// Intro
		tape.removeClip(clip, ClipBaseTypes.MISC);
		tape.addClip(clip, ClipBaseTypes.INTRO);
		clip.addType(Constants.DEFAULT_TYPES[1]);
		
		clip = clips[clips.length-1];								// Ending
		tape.removeClip(clip, ClipBaseTypes.MISC);
		tape.addClip(clip, ClipBaseTypes.END);
		clip.addType(Constants.DEFAULT_TYPES[2]);
		
		for(int i = 1; i < clips.length-1; i++)
		{
			if(i%2 > 0)
			{
				clip = clips[i];									// Filler
				tape.removeClip(clip, ClipBaseTypes.MISC);
				tape.addClip(clip, ClipBaseTypes.FILLER);
				clip.addType(Constants.DEFAULT_TYPES[3]);
			}
		}
		
		clips[16].addType("Soul Rangers");
		clips[22].addType("Soul Rangers Release");
		clips[10].addType("Black Hole Release");
		clips[56].addType("Black Hole Release");
		clips[81].addType("Black Hole Release");
		
//		clips[18].addType("Soul Rangers");
//		clips[20].addType("Soul Rangers Release");
//		clips[16].addType("Black Hole Release");
//		clips[36].addType("Black Hole Release");
		
		Settings settings = new Settings();
		settings.addLandmark("Soul Rangers", 600);
		settings.addLandmark("Soul Rangers Release", 800);
		
		try
		{
			tape.saveTape(basePath + "TestTape" + Constants.TAPE_EXTENSION);
			System.out.println("DONE SAVING");
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("ERROR: " + e.getMessage());
		}
	}
	
	public void TimelineTest(String[] tapePaths)
	{
		ArrayList<Tape> tapes = new ArrayList<>();
		for (String tapePath : tapePaths)
		{
			try
			{
				Tape tape = Tape.loadTape(tapePath);
				tapes.add(tape);
			} catch (ClassNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("ERROR: " + e.getMessage());
			}
		}

		Settings gameSettings = new Settings();
		gameSettings.addLandmark("Soul Rangers", 600);
		gameSettings.addBias("Soul Rangers", 0.0);	// bias it out so that it's only added from the landmark
		for (Tape tape : tapes)
		{
			tape.loadClipsMedia();
			
			gameSettings.addTapeIncludes(tape, EnumSet.of(
					ClipBaseTypes.INTRO,
					ClipBaseTypes.FILLER,
					ClipBaseTypes.MISC,
					ClipBaseTypes.END));
		}
		
		Timeline timeline = Timeline.createTimeline(
				tapes.toArray(new Tape[0]),
				gameSettings,
				Constants.DEFAULT_TOTAL_GAME_TIME,
				Constants.DEFAULT_TRANSITION_TIME);
		
		for (Entry<Double, Clip> clipTime: timeline.getSortedClipTimes())
		{
			Double time = clipTime.getKey();
			Clip clip = clipTime.getValue();
			String vidPath = clip.getVideo().getSource();
			
			String typesString = "";
			for (String type : clip.getTypes())
			{
				if(!type.equals(Constants.DEFAULT_TYPES[0]))
				{
					typesString += type + " ";
				}
			}
			
			//String[] splitPath = vidPath.split("/");
			//vidPath = splitPath[splitPath.length-2] + "/" + splitPath[splitPath.length-1];
			System.out.println(time + "\t" + typesString + "\t" + vidPath);
		}
	}

}
