package gui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.Region;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;


public class VideoPlayer extends Region
{
	private Media media;
	private MediaPlayer mplayer;
	private MediaView mview;

	private double width = -1.0;
	private double height = -1.0;
	
	public VideoPlayer()
	{
		this(null);
	}

	public VideoPlayer(Media media)
	{
		this.media = media;
		getStyleClass().setAll("gkplayer");
		init();
	}
	
	private void init()
	{
		mplayer = new MediaPlayer(media);
		mview = new MediaView(mplayer);
		getChildren().add(mview);
		mview.setMouseTransparent(true);	// so that clicks go to the VideoPlayer object
		
		//mview.visibleProperty().set(false);
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
			this.setScaleX(1);
			this.setScaleY(1);
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
		this.mview.setFitWidth(width);
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
		this.mview.setFitHeight(height);
		//updateSize();
	}
	
	public void play()
	{
		mplayer.play();
	}
	
	public void pause()
	{
		mplayer.pause();
	}
	
	public void seek(Duration seekTime)
	{
		mplayer.seek(seekTime);
	}
	
	public void stop()
	{
		mplayer.stop();
	}

	public void setOnReady(Runnable arg0)
	{
		mplayer.setOnReady(arg0);
	}
	
	public Duration getCurrentTime()
	{
		return mplayer.getCurrentTime();
	}
	
	public ReadOnlyObjectProperty<Duration> currentTimeProperty()
	{
		return mplayer.currentTimeProperty();
	}
	
	public DoubleProperty volumeProperty()
	{
		return mplayer.volumeProperty();
	}
}
