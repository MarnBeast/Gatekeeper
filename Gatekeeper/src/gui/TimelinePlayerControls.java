package gui;

import model.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.layout.Region;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

public class TimelinePlayerControls extends Region{

	private TimelinePlayer timelinePlayer;
	
	private boolean paused = true;
	private boolean movingSlider = false;
	
	public TimelinePlayerControls(final TimelinePlayer timelinePlayer) 
	{
		// The TimelinePlayer should be behind all controls, so add it first
		this.timelinePlayer = timelinePlayer;
		getChildren().add(timelinePlayer);		
		
		// Seek Slider
		final Slider seekSlider = new Slider();
		Timeline timeline = timelinePlayer.getTimeline();
		seekSlider.setMin(timeline.getVideoStartTime());
		seekSlider.setMax(timeline.getTotalGameTime());
		seekSlider.setValue(timeline.getVideoStartTime());
		getChildren().add(seekSlider);
		
		// Pause with Spacebar
		KeyPauseEventHandler keyPauseHandler = new KeyPauseEventHandler();
		this.setOnKeyPressed(keyPauseHandler);
		
		// Pause with mouse click
		ClickPauseEventHandler clickPauseHandler = new ClickPauseEventHandler();
		this.setOnMouseClicked(clickPauseHandler);
		
		// Extend handlers to children
		for (Node node : getChildren()) {
			node.setOnKeyPressed(keyPauseHandler);
			String nodeType = node.getClass().getName();
			if(!nodeType.startsWith("javafx.scene.control")){	// don't assign this for regularly clickable things - eg. buttons, sliders, etc.
				node.setOnMouseClicked(clickPauseHandler);
			}
		}
		
		timelinePlayer.currentGameTimeProperty().addListener(new ChangeListener<Duration>(){
			@Override
			public void changed(ObservableValue<? extends Duration> observed,
					Duration orig, Duration current) {
				if(!movingSlider){
					seekSlider.setValue(current.toSeconds());
				}
			}
		});
		
		timelinePlayer.widthProperty().addListener(new ChangeListener<Number>(){
			@Override
			public void changed(ObservableValue<? extends Number> observed,
					Number orig, Number current) {
				seekSlider.setMinWidth(current.doubleValue() - 5);
				seekSlider.setMaxWidth(current.doubleValue() - 5);
			}
		});
		
		seekSlider.setOnMouseClicked(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent arg0) {
				double seekVal = seekSlider.getValue();
				timelinePlayer.seek(Duration.seconds(seekVal));
			}
		});
		
		seekSlider.setOnMousePressed(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent arg0) {
				movingSlider = true;
			}
		});

		seekSlider.setOnMouseReleased(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent arg0) {
				movingSlider = false;
			}
		});
	}
	
	private void controlPlayPause(TimelinePlayer player)
	{
    	if(!paused) {
    		player.pause();
    		paused = true;
    	}
    	else {
			player.play();
			paused = false;
		}
	}
	
	
	private class KeyPauseEventHandler implements EventHandler<KeyEvent> {
		@Override
		public void handle(KeyEvent t) {
			if(t.getCode().isWhitespaceKey()){
				controlPlayPause(timelinePlayer);
			}
		}
	}
	
	private class ClickPauseEventHandler implements EventHandler<MouseEvent>{
		@Override
		public void handle(MouseEvent t) {
			if(t.isPrimaryButtonDown())
			{
				controlPlayPause(timelinePlayer);
			}
		}
	}
	
	public void setWidth(double width)
	{
		timelinePlayer.setWidth(width);
	}
	
	public void setHeight(double height)
	{
		timelinePlayer.setHeight(height);
	}

}
