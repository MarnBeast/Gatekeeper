package model;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Random;
import java.util.Map.Entry;

import model.Settings.ClipBaseTypes;


public class TimelineBuilder
{
	public TimelineBuilder()
	{
		progObservers = new ArrayList<>();
	}

	/* TIMELINE CREATION */
	
	public Timeline createTimeline(Tape[] tapes, Settings settings, double totalGameTime, double transitionTime)
	{
		/* Check parameters for validity */
		checkCreateTimelineParams(tapes, settings, totalGameTime, transitionTime);
		
		/* Start creating the timeline! */
		Timeline timeline = new Timeline(transitionTime);
		Tape clipPool = createClipPool(tapes, settings);
		
		Clip[] intros = clipPool.getClips(ClipBaseTypes.INTRO);
		Clip[] endings = clipPool.getClips(ClipBaseTypes.END);
		Clip[] fillers = clipPool.getClips(ClipBaseTypes.FILLER);
		Clip[] miscs = clipPool.getClips(ClipBaseTypes.MISC);
		
		settings.setLandmarkClips(tapes);
		Landmark[] landmarks = settings.getLandmarks();
		
		
		Random random = new Random();
		Clip clip;
		double curRuntime = 0.0;
		int lastFillerIndex = 0;
		int lastLandmarkIndex = 0;
		// The curposition occurrs in the middle of the position window.
		// Thus, our +- offset is the poswindow / 2
		double positionWindowOffset = settings.getPositionWindow() / 2.0; 
		
		/* ADD AN INTRO */
		if(intros.length > 0)
		{
			int randIndex = random.nextInt(intros.length);
			clip = intros[randIndex];
			// The intro clip length should not be included in the total game time.
			// For this reason, I set the start time of the intro clip to be the
			// negative clip length minus transition time.
			double introTransTime = transitionTime;
			if(introTransTime > clip.getLength())
			{
				introTransTime = 0;
			}
			timeline.addClip(clip, (clip.getLength() - introTransTime) * (-1.0));
		}
		
		/* ADD GAME CLIPS */
		while(curRuntime < totalGameTime)
		{
			updateProgress(50 + curRuntime*50.0/totalGameTime, 100.0);
			/* ADD FILLER */
			if(fillers.length > 0)
			{
				// Fillers should not be random, they progress in intensity throughout the game
				double curPlacePerc = curRuntime * 100.0 / totalGameTime;
				while(fillers[lastFillerIndex].getPlacePercent() < curPlacePerc
						&& lastFillerIndex < fillers.length - 1)
				{
					lastFillerIndex++;
				}
				clip = fillers[lastFillerIndex];
				timeline.addClip(clip, curRuntime);
				
				double transTime = transitionTime;
				if(transTime > clip.getLength() / 2.0)
				{
					transTime = 0;
				}
				curRuntime += clip.getLength() - transTime;
			}
			
			/* ADD LANDMARK */
			if(lastLandmarkIndex < landmarks.length && 
					curRuntime > landmarks[lastLandmarkIndex].getTime())
			{
				Landmark curLandmark = landmarks[lastLandmarkIndex];
				
				HashMap<String, Double> landmarkBiases = (HashMap<String, Double>) settings.getBiases().clone();
				landmarkBiases.remove(curLandmark.getType());	// We want ALL clips of that type, we don't want ANY of them biased out, only extras biased in.
				Clip[] landClips = biasClips(curLandmark.getClips(), landmarkBiases);
				
				int randIndex = random.nextInt(landClips.length);
				clip = landClips[randIndex];
				// remove the clip from all of the matching landmarks so we don't reuse it later.
				for (Landmark landmark : landmarks)
				{
					if(landmark.getType() == curLandmark.getType())
					{
						landmark.removeClip(clip);
					}
				}
				timeline.addClip(clip, curLandmark.getTime());
				
				double transTime = transitionTime;
				if(transTime > clip.getLength() / 2.0)
				{
					transTime = 0;
				}
				curRuntime = curLandmark.getTime() + clip.getLength() - transTime;
				
				lastLandmarkIndex++;
			}
			
			/* ADD MISC */
			else if(miscs.length > 0 && curRuntime < totalGameTime)
			{
				// Misc clips should be random within the position window
				double curPlacePerc = curRuntime * 100.0 / totalGameTime;
				
				// get position window range
				int lowerIndex = 0;
				int upperIndex = 0;
				while(miscs[lowerIndex].getPlacePercent() < curPlacePerc - positionWindowOffset
						&& lowerIndex < miscs.length - 1)
				{
					lowerIndex++;
				}
				
				upperIndex = lowerIndex;
				while(miscs[upperIndex].getPlacePercent() < curPlacePerc + positionWindowOffset 
						&& upperIndex < miscs.length - 1)
				{
					upperIndex++;
				}
				
				int randIndex = lowerIndex + random.nextInt(upperIndex - lowerIndex);
				clip = miscs[randIndex];
				
				// We don't want to add a game clip here if the game is going to end or hit a landmark 
				// immediately afterwards. Rather, just add another filler clip instead. If there are no
				// fillers, use a misc clip.
				double timeLeftAfter = curRuntime + clip.getLength() * 2;
				if(fillers.length == 0 ||
						timeLeftAfter < totalGameTime &&
						( lastLandmarkIndex >= landmarks.length ||
						  timeLeftAfter < landmarks[lastLandmarkIndex].getTime() ))
				{
					timeline.addClip(clip, curRuntime);
					// remove all occurrences of that clip so we don't reuse it
					while(clipPool.removeClip(clip, ClipBaseTypes.MISC))
					miscs = clipPool.getClips(ClipBaseTypes.MISC);
					
					double transTime = transitionTime;
					if(transTime > clip.getLength() / 2.0)
					{
						transTime = 0;
					}
					curRuntime += clip.getLength() - transTime;
				}
			}
		}
		
		/* ADD AN ENDING */
		if(endings.length > 0)
		{
			int randIndex = random.nextInt(endings.length);
			clip = endings[randIndex];
			timeline.addClip(clip,totalGameTime);
		}
		
		return timeline;
	}
	
	private Tape createClipPool(Tape[] tapes, Settings settings)
	{
		Tape clipPool = new Tape();
		clipPool.setDefaultSettings(settings);
		HashMap<String, Double> biases = settings.getBiases();
		
		double tapesDone = 0.0;
		double step = 50.0/(double)tapes.length;
		for (Tape tape : tapes)
		{
			EnumSet<ClipBaseTypes> tapeIncludes = settings.getTapeIncludes(tape);
			
			updateProgress(tapesDone * step, 100.0);
			if(tapeIncludes.contains(Settings.ClipBaseTypes.INTRO))
			{
				clipPool.addClips(
						biasClips(tape.getClips(Settings.ClipBaseTypes.INTRO), biases),
						Settings.ClipBaseTypes.INTRO);
			}

			updateProgress(tapesDone * step + step * 0.25, 100.0);
			if(tapeIncludes.contains(Settings.ClipBaseTypes.END))
			{
				clipPool.addClips(
						biasClips(tape.getClips(Settings.ClipBaseTypes.END), biases),
						Settings.ClipBaseTypes.END);
			}

			updateProgress(tapesDone * step + step * 0.5, 100.0);
			if(tapeIncludes.contains(Settings.ClipBaseTypes.FILLER))
			{
				clipPool.addClips(
						biasClips(tape.getClips(Settings.ClipBaseTypes.FILLER), biases),
						Settings.ClipBaseTypes.FILLER);
			}

			updateProgress(tapesDone * step + step * 0.75, 100.0);
			if(tapeIncludes.contains(Settings.ClipBaseTypes.MISC))
			{
				clipPool.addClips(
						biasClips(tape.getClips(Settings.ClipBaseTypes.MISC), biases),
						Settings.ClipBaseTypes.MISC);
			}
		}
		return clipPool;
	}
	

	private Clip[] biasClips(Clip[] clips, HashMap<String, Double> biases)
	{
		// TODO I think I see a problem with this. Clips with more types will be weighted more heavily
		// than tapes with less types. EX: Clip1 is FUNNY, DIFFICULT, CREEPY. Clip2 is GENEROUS.
		// If FUNNY=50%, DIFFICULT=10%, CREEPY=50%, and GENEROUS=100%, clip1 may still be weighted more
		// heavily than clip2 because it will have a 50% chance of being added TWICE, once for FUNNY
		// and AGAIN for CREEPY. Whereas, clip2 will only be guaranteed to be added ONCE for GENEROUS.

		ArrayList<Clip> retClips = new ArrayList<Clip>();
		
		for (Entry<String, Double> bias : biases.entrySet())
		{
			double percBias = bias.getValue();
			String typeID = bias.getKey();
			ArrayList<Clip> typedClips = new ArrayList<Clip>();
			
			// Get all clips matching this bias that aren't forbidden (0% on another type)
			for (Clip clip : clips)
			{
				if(clip.getTypes().contains(typeID))
				{
					boolean forbidden = false;
					for (String clipType : clip.getTypes())
					{
						Double otherBias = biases.get(clipType);
						if(otherBias != null && otherBias <= 0.0)
						{
							forbidden = true;
							break;
						}
					}
					if(!forbidden)
					{
						typedClips.add(clip);
					}
				}
			}
			
			if(typedClips.size() > 0)
			{
				// Bias > 100 indicates that all clips of that type should be included
				// at least once, or more than once if > 100.
				while(percBias >= 100.0)
				{
					retClips.addAll(typedClips);
					percBias -= 100.0;
				}
				
				// Bias < 100 but > 0 indicates that only some clips should be included.
				double typedCount = typedClips.size();
				while(percBias > 0)
				{
					Random random = new Random();
					int randIndex = random.nextInt(typedClips.size());
					retClips.add(typedClips.remove(randIndex));
					percBias -= 100.0/typedCount;
				}
			}
		}
		return retClips.toArray(new Clip[0]);
	}
	
	private Clip[] altBiasClips(Clip[] clips, HashMap<String, Double> biases)
	{
		// The key difference between this implementation and the old one is that we are
		// no longer guaranteed to have a percentage of clips of a spec type included, 
		// rather there is a percent chance that they will be included. This actually could
		// be a problem, since this create timeline algorithm relies on at least some clips
		// being returned in the event that the bias is > 0.
		ArrayList<Clip> retClips = new ArrayList<Clip>();
		for (Clip clip : clips)
		{
			// Average the bias of the clip types
			double totalBias = 0;
			for (String typeID : clip.getTypes())
			{
				double bias = biases.get(typeID);
				if(bias <= 0)	// forbidden clip type
				{
					totalBias = 0;
					break;
				}
				else
				{
					totalBias += bias;
				}
			}
			
			totalBias /= clip.getTypes().size();
			
			// Bias > 100 indicates that all clips of that type should be included
			// at least once, or more than once if > 100.
			while(totalBias > 100.0)
			{
				retClips.add(clip);
				totalBias -= 100.0;
			}
			
			// Bias < 100 but > 0 indicates that there's only a chance it should be included.
			if(Math.random()*100.0 < totalBias)
			{
				retClips.add(clip);
			}
		}
		return retClips.toArray(new Clip[0]);
	}
	
	private void checkCreateTimelineParams(Tape[] tapes, Settings settings, double totalGameTime, double transitionTime)
	{
		// TAPES
		boolean fail = false;
		if(tapes.length == 0) fail = true;
		if(!fail)
		{
			for (Tape tape : tapes)
			{
				if(tape == null)
				{
					fail = true;
				}
			}
		}
		if(fail)
		{
			throw new IllegalArgumentException("Please ensure that the specified tapes are valid.");
		}
		
		// SETTINGS
		fail = true;
		for (Tape tape : tapes)
		{
			EnumSet<ClipBaseTypes> tapeInc = settings.getTapeIncludes(tape);
			if(tapeInc == null)
			{
				throw new IllegalArgumentException("Please ensure that tape inclusion settings are specified for all provided tapes.");
			}
			else if (!tapeInc.isEmpty())
			{
				fail = false;
			}
		}
		if(fail)
		{
			throw new IllegalArgumentException("Please ensure that at least one tape has at least one clip base type included in the tape inclusion settings.");
		}
		
		// GAME/TRANS TIME
		if(totalGameTime < 0)
		{
			throw new IllegalArgumentException("Please ensure that the total game time is greater than or equal to 0.");
		}
		  // Let's allow transition time to be < 0. All this means is that there will be a videoless pause between clips.
		  // If that's what they want, let's let them have it!
	}
	
	
	// OBSERVERS 
	
	public interface ProgressObserver
	{
		public void progressUpdate(double iterations, double totalIterations);
	}
	
	private ArrayList<ProgressObserver> progObservers;
	
	public void addTBProgressObserver(ProgressObserver o)
	{
		progObservers.add(o);
	}
	public void removeTBProgressObserver(ProgressObserver o)
	{
		progObservers.remove(o);
	}
	private void updateProgress(double iterations, double totalIterations)
	{
		for (ProgressObserver observer : progObservers)
		{
			observer.progressUpdate(iterations, totalIterations);
		}
	}
	
}
