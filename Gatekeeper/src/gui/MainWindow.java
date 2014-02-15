package gui;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Map.Entry;

import model.Clip;
import model.Constants;
import model.Landmark;
import model.Settings;
import model.Tape;
import model.Timeline;
import model.TimelineBuilder;
import model.Settings.ClipBaseTypes;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class MainWindow extends Application{

	/* I'M WRITING THIS HERE AS A REMINDER IF I EVER NEED TO GET JAVAFX RUNNING IN ECLIPSE IN THE FUTURE.
	 * PROJECT > PROPERTIES > JAVA BUILD PATH > ADD EXTERNAL JARS
	 * IN C:\Program Files\Java\jdk1.7.0_45\jre\lib YOU'LL FIND jfxrt.jar. ADD THAT!!!
	 */

	
	private boolean paused = false;
	private ArrayList<Tape> tapes;
	private TimelinePlayer timelinePlayer;
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		CleanUpTempDir();
		TimelinePlayerTest(primaryStage);
		
		VideoPlayerTest(primaryStage);
		
		SerializeTestTape("C:\\Users\\MarnBeast\\Videos\\atmosfear clips\\Main Tape\\");
		SerializeTestTape("C:\\Users\\MarnBeast\\Videos\\atmosfear clips\\Booster 1\\");
		
		//TimelineTest(new String[]{
		//		"C:\\Users\\MarnBeast\\Videos\\atmosfear clips\\Main Tape\\TestTape.gktape",
		//		"C:\\Users\\MarnBeast\\Videos\\atmosfear clips\\Booster 1\\TestTape.gktape"});
		//		"C:\\Users\\MarnBeast\\Videos\\atmosfear clips\\Main Tape\\Main Tape.gktape",
		//		"C:\\Users\\MarnBeast\\Videos\\atmosfear clips\\Booster 1\\Booster 1.gktape"});
	}
	
	public void TimelinePlayerTest(final Stage primaryStage)
	{
		// Initialize Window
		final Group root = new Group();
		final Scene scene = new Scene(root, 200, 100);
		
		primaryStage.setScene(scene);
		scene.getStylesheets().add(MainWindow.class.getResource("GKStyle.css").toExternalForm());
		//System.out.println(MainWindow.class.getResource("GKStyle.css"));
		primaryStage.show();
		
		// Throw on a label
		Label messageLabel = new Label("Building Timeline");
		Label progressLabel = new Label("0");
		VBox vBox = new VBox();
		vBox.getChildren().addAll(messageLabel, progressLabel);
		root.getChildren().add(vBox);
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>(){
			@Override
			public void handle(WindowEvent arg0) {
				if(timelinePlayer != null)
				{
					timelinePlayer.stop();
				}
				if(tapes != null){
					for(Tape tape : tapes){
						tape.closeClips();
					}
				}
				Platform.exit();
			}
		});
		
		// Built Timeline and run Timeline Player
		Task<Timeline> timelineTask = new Task<Timeline>(){
			@Override
			protected Timeline call() throws Exception
			{
				String[] tapePaths = new String[]{
						"C:\\Users\\MarnBeast\\Videos\\atmosfear clips\\Main Tape\\TestTape.gktape",//};
						"C:\\Users\\MarnBeast\\Videos\\atmosfear clips\\Booster 1\\TestTape.gktape"};
								
				tapes = new ArrayList<>();
				// Load Tapes - 50%
				double progress = 0.0;
				for (String tapePath : tapePaths)
				{
					try
					{
						updateMessage("Loading Tape '" + (new File(tapePath)).getName()+ "'");
						updateProgress(progress, 100.0);
						progress += 50.0/(double)tapePaths.length;
						Tape tape = Tape.loadTape(tapePath);
						tape.loadClipsMedia();
						tape.saveTape(tapePath);
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
					//tape.loadClipsMedia();
					
					gameSettings.addTapeIncludes(tape, EnumSet.of(
							ClipBaseTypes.INTRO,
							ClipBaseTypes.FILLER,
							ClipBaseTypes.MISC,
							ClipBaseTypes.END));
				}
				
				// Build Timeline - 50%
				updateMessage("Building Timeline");
				updateProgress(50.0, 100.0);
				TimelineBuilder tBuilder = new TimelineBuilder();
				
				TimelineBuilder.ProgressObserver pObserver = new TimelineBuilder.ProgressObserver()
				{
					@Override
					public void progressUpdate(double iterations, double totalIterations)
					{
						updateProgress(iterations, totalIterations);
					}
				};

				tBuilder.addTBProgressObserver(pObserver);
				
				Timeline timeline = tBuilder.createTimeline(tapes.toArray(new Tape[0]), gameSettings,
						1200,
						Constants.DEFAULT_TRANSITION_TIME);				

				updateMessage("Timeline Created!");
				
				return timeline;
			}
		};
		
		messageLabel.textProperty().bind(timelineTask.messageProperty());
		progressLabel.textProperty().bind(timelineTask.progressProperty().multiply(100.0).asString());
		
		timelineTask.setOnSucceeded(new EventHandler<WorkerStateEvent>(){
			@Override
			public void handle(WorkerStateEvent event)
			{
				final Timeline timeline = ((Task<Timeline>)event.getSource()).getValue();
				
				for(double clipTime : timeline.getClipTimes())
				{
					Clip clip = timeline.getClip(clipTime);
					System.out.println(clip.getLength() + "\t" + clip.getVideo().getSource());
				}
				TapePlayerWindow playerWindow = new TapePlayerWindow(timeline);
				playerWindow.show();
				playerWindow.getTimelinePlayer().play();
				
//				timelinePlayer = new TimelinePlayer(timeline);
//				final TimelinePlayerControls playerControls = new TimelinePlayerControls(timelinePlayer);
//				root.getChildren().add(playerControls);
//				
//				timelinePlayer.play();
//				timelinePlayer.setOnReady(new Runnable()
//				{
//					@Override
//					public void run()
//					{
//						double width = timelinePlayer.getWidth();
//						double height = timelinePlayer.getHeight();
//						Scene scene = primaryStage.getScene();
//						double frameWidth = primaryStage.getWidth() - scene.getWidth();
//						double frameHeight = primaryStage.getHeight() - scene.getHeight();
//						primaryStage.setWidth(width + frameWidth);
//						primaryStage.setHeight(height + frameHeight);
//						
//						
//						ChangeListener<Object> updateSizeListener = new ChangeListener<Object>()
//						{
//							@Override
//							public void changed(ObservableValue<?> observable, Object oldValue,
//									Object newValue)
//							{
//								playerControls.setWidth(primaryStage.getWidth());
//								playerControls.setHeight(primaryStage.getHeight());
//								
//								//System.out.print();
//							}
//						};
//						primaryStage.widthProperty().addListener(updateSizeListener);
//						primaryStage.heightProperty().addListener(updateSizeListener);
//					}
//				});
			}

		});
		
		new Thread(timelineTask).start();	
	}
	
	/**
	 * Must run this on application startup because JavaFX currently has a bug where the 
	 * Media object holds on to it's source file until the application is terminated.
	 * I've got deleteOnExit running but it doesn't work all of the time.
	 */
	private void CleanUpTempDir()
	{
		File dir = new File(Constants.getTempLocation());
		if(dir.exists()){
			for (File file : dir.listFiles()) {
				file.delete();
			}
		}
	}
	
	
	
	
	
	/* HERE LIE OLD TESTS */
	
	
	
	
	public void VideoPlayerTest(Stage primaryStage)
	{
		Group root = new Group();
		
		String projectRootDirectory = getHostServices().getDocumentBase();
		Media media = new Media(projectRootDirectory + "src/tests/Clip01.mp4");
		
		final VideoPlayer player = new VideoPlayer(media);
		//player.setHeight(200);
		final VideoPlayer player2 = new VideoPlayer(media);
		//player2.setHeight(200);
		final VideoPlayer player3 = new VideoPlayer(media);
		//player3.setHeight(200);
		final VideoPlayer player4 = new VideoPlayer(media);
		//player4.setHeight(200);
		
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

		player.widthProperty().addListener(new ChangeListener<Number>(){

			@Override
			public void changed(ObservableValue<? extends Number> arg0,
					Number arg1, Number arg2) {
				System.out.println("Listener: " + player.getWidth());
			}
			
		});
		
		Thread thread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				player.setPlayerWidth(-1);
				player2.setPlayerWidth(-1);
				player3.setPlayerWidth(-1);
				player4.setPlayerWidth(-1);
				player.setPlayerHeight(-1);
				player2.setPlayerHeight(-1);
				player3.setPlayerHeight(-1);
				player4.setPlayerHeight(-1);
				player.play();
				try
				{
					Thread.sleep(2000);
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(player.getWidth());
				player.setPlayerWidth(500);
				//player.setHeight(500);
				player2.setPlayerWidth(500);
				player3.setPlayerWidth(500);
				player4.setPlayerWidth(500);
				
				player2.play();
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(player.getWidth());
				
				player.setPlayerWidth(200);
				player2.setPlayerWidth(200);
				player3.setPlayerWidth(200);
				player4.setPlayerWidth(200);
				
				player3.play();
				try
				{
					Thread.sleep(2000);
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(player.getWidth());
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
	
	public Timeline TimelineTest(String[] tapePaths)
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
		
		Timeline timeline = new TimelineBuilder().createTimeline(
				tapes.toArray(new Tape[0]),
				gameSettings,
				Constants.DEFAULT_TOTAL_GAME_TIME,
				Constants.DEFAULT_TRANSITION_TIME);
		
		return timeline;
//		for (Entry<Double, Clip> clipTime: timeline)
//		{
//			Double time = clipTime.getKey();
//			Clip clip = clipTime.getValue();
//			String vidPath = clip.getVideo().getSource();
//			
//			String typesString = "";
//			for (String type : clip.getTypes())
//			{
//				if(!type.equals(Constants.DEFAULT_TYPES[0]))
//				{
//					typesString += type + " ";
//				}
//			}
//			
//			//String[] splitPath = vidPath.split("/");
//			//vidPath = splitPath[splitPath.length-2] + "/" + splitPath[splitPath.length-1];
//			System.out.println(time + "\t" + typesString + "\t" + vidPath);
//		}
	}

}
