package gui;

import java.util.Iterator;
import java.util.Set;
import java.util.TimerTask;
import java.util.Map.Entry;
import java.util.Timer;

import model.Clip;
import model.Timeline;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.util.Duration;


public class TimelinePlayer extends Region
{
	private VideoPlayer foregroundVideoPlayer;
	private VideoPlayer backgroundVideoPlayer;
	private Timeline timeline;
	private Iterator<Double> timesIterator;
	
	private double width = -1;
	private double height = -1;
	
	private double currentTime = Double.NaN;
	private double nextTime = Double.NaN;
	private double pausedTime = 0.0;
	private boolean paused = true;
	
	Timer timer;
	TimerTask timerTask;
	
	public TimelinePlayer(Timeline timeline)
	{
		this.timeline = timeline;
		this.timesIterator = timeline.getClipTimes().iterator();
		Double[] array = timeline.getClipTimes().toArray(new Double[0]);
		init();
	}
	
	private void init()
	{
		if(timesIterator != null && timesIterator.hasNext())
		{
			currentTime = timesIterator.next();
			Clip clip = timeline.getClip(currentTime);
			if(clip != null)
			{
				foregroundVideoPlayer = new VideoPlayer(clip.getVideo());
				getChildren().add(foregroundVideoPlayer);
			}
			if(timesIterator.hasNext())
			{
				nextTime = timesIterator.next();
			}
		}
		
		ChangeListener<Object> updateSizeListener = new ChangeListener<Object>()
				{
					@Override
					public void changed(ObservableValue<?> observable, Object oldValue,
							Object newValue)
					{
						updateSize();
					}
				};
				
		this.widthProperty().addListener(updateSizeListener);
		this.heightProperty().addListener(updateSizeListener);
	}
	
	
	private class LoadNextClipTask extends TimerTask
	{
		@Override
		public void run()
		{
			Platform.runLater(new Runnable(){
				@Override
				public void run()
				{
					if(backgroundVideoPlayer != null)
					{
						getChildren().remove(backgroundVideoPlayer);
					}
					
					Clip clip = timeline.getClip(nextTime);
					backgroundVideoPlayer = foregroundVideoPlayer;
					foregroundVideoPlayer = new VideoPlayer(clip.getVideo());
					setWidth(width);
					setHeight(height);
					
					Duration transitionDuration = Duration.seconds(timeline.getTransitionTime());
					FadeTransition fade = new FadeTransition(
							transitionDuration, foregroundVideoPlayer);
					fade.setFromValue(0.0);
					fade.setToValue(100.0);
					fade.play();
					
					getChildren().add(foregroundVideoPlayer);
					foregroundVideoPlayer.play();
		
					currentTime = nextTime;
					if(timesIterator.hasNext())
					{
						timerTask = new LoadNextClipTask();
						
						nextTime = timesIterator.next();
						long delay = (long)(1000.0 * (nextTime - currentTime));
						timer = new Timer();
						timer.schedule(timerTask, delay);
					}
				}
			});
			
		}
	}
	
	
	
	private void updateSize()
	{
		if(width != -1 || height != -1)
		{
			double w = this.getWidth();
			double h = this.getHeight();
			
			// only do work on this if we have loaded the gui elements 
			// and have a valid w and h
			if(w > 1.0 && h > 1.0)
			{
				double scaleX = (width != -1) ? ((double) width)/w : 
					 ((double) height)/h;
				double scaleY = (height != -1) ? ((double) height)/h :
					((double) width)/w ;

				double transX = (w - (w * scaleX)) / 2;
				double transY = (h - (h * scaleY)) / 2;
	
				//this.setTranslateX(-transX);
				//this.setTranslateY(-transY);
				this.setScaleX(scaleX);
				this.setScaleY(scaleY);
			}
		}
		else
		{
			setScaleX(1);
			setScaleY(1);
		}
	}
	
	/**
	 * Sets the width of the video player.
	 * @param width - The desired width, in pixels. Setting this to -1 keeps the original
	 * aspect ratio of the played media, or the original width of the media if height is
	 * also -1.
	 */
	public void setWidth(double width)
	{
		this.width = width;
		if(foregroundVideoPlayer != null) foregroundVideoPlayer.setWidth(width);
		if(backgroundVideoPlayer != null) backgroundVideoPlayer.setWidth(width);
		//updateSize();
	}
	
	/**
	 * Sets the height of the video player.
	 * @param height - The desired height, in pixels. Setting this to -1 keeps the original
	 * aspect ratio of the played media, or the original height of the media if width is
	 * also -1.
	 */
	public void setHeight(double height)
	{
		this.height = height;
		if(foregroundVideoPlayer != null) foregroundVideoPlayer.setHeight(height);
		if(backgroundVideoPlayer != null) backgroundVideoPlayer.setHeight(height);
		//updateSize();
	}
	
	public void play()
	{
		if(foregroundVideoPlayer!= null && !paused)
		{
			foregroundVideoPlayer.play();
			timerTask = new LoadNextClipTask();
			
			long delay = (long)(1000.0 * (nextTime - currentTime - pausedTime));
			timer = new Timer();
			timer.schedule(timerTask, delay);
			pausedTime = 0.0;
			paused = false;
		}
	}
	
	public void pause()
	{
		foregroundVideoPlayer.pause();
		timer.cancel();
		pausedTime = foregroundVideoPlayer.getCurrentTime().toSeconds();
		paused = true;
	}
	
	public void seek(Duration seekTime)
	{
		boolean origPaused = paused;
		double seekTimeD = seekTime.toSeconds();
		if(!paused)
		{
			pause();
		}
		
		Set<Double> clipTimes = timeline.getClipTimes();
		double clipTime = (double) clipTimes.toArray()[0];
		for (Double time : clipTimes)
		{
			if(seekTimeD > time)
			{
				break;
			}
			clipTime = time;
		}
		
		Clip clip = timeline.getClip(clipTime);
		foregroundVideoPlayer = new VideoPlayer(clip.getVideo());
		pausedTime = seekTimeD - clipTime;
		Duration newSeekTime = Duration.seconds(pausedTime);
		foregroundVideoPlayer.seek(newSeekTime);
		
		if(!origPaused)
		{
			play();
		}
	}

	public void setOnReady(Runnable arg0)
	{
		foregroundVideoPlayer.setOnReady(arg0);
	}
}
