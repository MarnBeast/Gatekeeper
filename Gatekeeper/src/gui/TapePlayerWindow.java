package gui;

import model.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class TapePlayerWindow extends Stage{
	
	private TimelinePlayer timelinePlayer;
	private TimelinePlayerControls playerControls;
	
	public TapePlayerWindow(Timeline timeline)
	{
        this.setTitle("Gatekeeper Player");
		Group root = new Group();
		Scene scene = new Scene(root, 200, 100, Color.BLACK);
		this.setScene(scene);
		scene.getStylesheets().add(MainWindow.class.getResource("GKStyle.css").toExternalForm());
		root.getStyleClass().add("gkplayer");
		
		timelinePlayer = new TimelinePlayer(timeline);
		playerControls = new TimelinePlayerControls(timelinePlayer);
		root.getChildren().add(playerControls);
		playerControls.getStyleClass().add("gkplayer");
		
		setOnCloseRequest(new EventHandler<WindowEvent>(){

			@Override
			public void handle(WindowEvent arg0) {
				timelinePlayer.stop();
			}
			
		});
		
		final Stage primaryStage = this;
		timelinePlayer.setOnReady(new Runnable()
		{
			@Override
			public void run()
			{
				double width = timelinePlayer.getCurrentVideoPlayer().getMedia().getWidth();
				double height = timelinePlayer.getCurrentVideoPlayer().getMedia().getHeight();
				final Scene scene = primaryStage.getScene();
				double frameWidth = primaryStage.getWidth() - scene.getWidth();
				double frameHeight = primaryStage.getHeight() - scene.getHeight();
				
				ChangeListener<Object> updateSizeListener = new ChangeListener<Object>()
				{
					@Override
					public void changed(ObservableValue<?> observable, Object oldValue,
							Object newValue)
					{
						double stageWidth = primaryStage.getWidth();
						double sceneWidth = scene.getWidth();
						double stageHeight = primaryStage.getHeight();
						double sceneHeight = scene.getHeight();
						double frameWidth = primaryStage.getWidth() - scene.getWidth();
						double frameHeight = primaryStage.getHeight() - scene.getHeight();
						
						playerControls.setWidth(primaryStage.getWidth() - frameWidth);
						playerControls.setHeight(primaryStage.getHeight() - frameHeight);
						
						double layoutX = 0;
						double layoutY = 0;
						
						double diff = primaryStage.getWidth() - playerControls.getWidth() - frameWidth;
						if(diff > 0)
						{
							layoutX = diff / 2;
						}
						
						diff = primaryStage.getHeight() - playerControls.getHeight() - frameHeight;
						if(diff > 0)
						{
							layoutY = diff / 2;
						}
						
						playerControls.setLayoutX(layoutX);
						playerControls.setLayoutY(layoutY);
					}
				};
				scene.widthProperty().addListener(updateSizeListener);
				scene.heightProperty().addListener(updateSizeListener);
				
				primaryStage.setWidth(width + frameWidth);
				primaryStage.setHeight(height + frameHeight);
			}
		});
	}
	
	public TimelinePlayer getTimelinePlayer()
	{
		return timelinePlayer;
	}
	
}
