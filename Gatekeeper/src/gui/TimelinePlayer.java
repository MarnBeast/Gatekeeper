package gui;

import java.util.Iterator;
import java.util.Set;
import java.util.TimerTask;
import java.util.Timer;

import org.omg.CORBA.PUBLIC_MEMBER;

import model.Clip;
import model.Timeline;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Transition;
import javafx.animation.TransitionBuilder;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
	
	private SimpleObjectProperty<Duration> currentGameTime = new SimpleObjectProperty<Duration>();
	private CurrentTimeChangeListener currentTimeChangeListener = new CurrentTimeChangeListener();
	
	Timer timer;
	TimerTask timerTask;
	
	public TimelinePlayer(Timeline timeline)
	{
		this.timeline = timeline;
		this.timesIterator = timeline.getClipTimes().iterator();
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
				foregroundVideoPlayer.currentTimeProperty().addListener(currentTimeChangeListener);
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
						//updateSize();
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
					backgroundVideoPlayer.currentTimeProperty().removeListener(currentTimeChangeListener);
					foregroundVideoPlayer = new VideoPlayer(clip.getVideo());
					foregroundVideoPlayer.currentTimeProperty().addListener(currentTimeChangeListener);
					setWidth(width);
					setHeight(height);
					
					
					Duration transitionDuration = Duration.seconds(timeline.getTransitionTime());
					FadeTransition fade = new FadeTransition(
							transitionDuration, foregroundVideoPlayer);
					fade.setFromValue(0.0);
					fade.setToValue(100.0);
					fade.play();
					
					VolumeTransition backVolumeTrans = new VolumeTransition(
							transitionDuration.divide(2), transitionDuration.divide(2), backgroundVideoPlayer);
					backVolumeTrans.setFromValue(backgroundVideoPlayer.volumeProperty().get());
					backVolumeTrans.setToValue(0.0);
					
					VolumeTransition foreVolumeTrans = new VolumeTransition(
							transitionDuration.divide(2), foregroundVideoPlayer);
					foreVolumeTrans.setFromValue(0.0);
					foreVolumeTrans.setToValue(backgroundVideoPlayer.volumeProperty().get());

					backVolumeTrans.play();
					foreVolumeTrans.play();
					
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
		super.setWidth(width);
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
		super.setHeight(height);
		//updateSize();
	}
	
	public void play()
	{
		if(foregroundVideoPlayer!= null && paused)
		{
			foregroundVideoPlayer.play();
			timerTask = new LoadNextClipTask();
			
			long delay = (long)(1000.0 * (nextTime - currentTime - pausedTime));
			if(delay > 0)
			{
				timer = new Timer();
				timer.schedule(timerTask, delay);
			}
			
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
		// Pause first to stop the clip timer
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
			if(seekTimeD < time)
			{
				break;
			}
			clipTime = time;
		}
		
		// Get sought clip
		Clip clip = timeline.getClip(clipTime);
		
		getChildren().remove(foregroundVideoPlayer);
		foregroundVideoPlayer.currentTimeProperty().removeListener(currentTimeChangeListener);
		foregroundVideoPlayer = new VideoPlayer(clip.getVideo());
		foregroundVideoPlayer.currentTimeProperty().addListener(currentTimeChangeListener);
		setWidth(width);
		setHeight(height);
		
		getChildren().add(foregroundVideoPlayer);
		
		pausedTime = seekTimeD - clipTime;
		Duration newSeekTime = Duration.seconds(pausedTime);
		foregroundVideoPlayer.seek(newSeekTime);
		
		// Set iterator to sought clip position
		this.timesIterator = timeline.getClipTimes().iterator();
		Double iterClipTime = timesIterator.next();
		while(iterClipTime != clipTime && timesIterator.hasNext())
		{
			iterClipTime = timesIterator.next();
		}
		
		// Set new current and next times
		currentTime = clipTime;
		if(timesIterator.hasNext())
		{
			nextTime = timesIterator.next();
		}
		else 
		{
			nextTime = currentTime;
		}
		
		// Unpause if we weren't paused to start
		if(!origPaused)
		{
			play();
		}
	}

	public void setOnReady(Runnable arg0)
	{
		foregroundVideoPlayer.setOnReady(arg0);
	}
	
	public boolean isPaused()
	{
		return paused;
	}

	
	public Timeline getTimeline()
	{
		return this.timeline;
	}
	
	
	
	public ReadOnlyObjectProperty<Duration> currentGameTimeProperty()
	{
		return currentGameTime;
	}
	
	private class CurrentTimeChangeListener implements ChangeListener<Duration>
	{
		@Override
		public void changed(ObservableValue<? extends Duration> observed,
				Duration oldTime, Duration newTime)
		{
			currentGameTime.set(Duration.seconds(currentTime + newTime.toSeconds()));
		}
	}
	
	private class VolumeTransition extends Transition
	{
		VideoPlayer player;
		double fromVal = 1.0;
		double toVal = 0.0;
		
		double startAtFrac = 0.0;
		
		public VolumeTransition(Duration transitionDuration, Duration delayDuration, VideoPlayer player)
		{
			setCycleDuration(transitionDuration.add(delayDuration));
			setCycleCount(1);
			
			startAtFrac = delayDuration.toMillis() / transitionDuration.add(delayDuration).toMillis();
			
			this.player = player;
		}
		public VolumeTransition(Duration transitionDuration, VideoPlayer player)
		{
			this(transitionDuration, Duration.millis(0), player);
		}
		
		@Override
		protected void interpolate(double frac)
		{
			if(frac > startAtFrac)
			{     
				double volume = fromVal + (toVal - fromVal) * (frac-startAtFrac) / (1.0 - startAtFrac);
				player.volumeProperty().set(volume);
			}
		}
		
		public void setFromValue(double fromVal)
		{
			this.fromVal = fromVal;
		}
		
		public void setToValue(double toVal)
		{
			this.toVal = toVal;
		}
	}
	
}
