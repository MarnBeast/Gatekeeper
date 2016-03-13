package gui;

import model.Clip;
import javafx.scene.control.Control;


public class ClipCard extends Control
{
	public ClipCard(Clip clip)
	{
		getStyleClass().add("clip-card");

	}

	@Override
	public String getUserAgentStylesheet() {
		return ClipCard.class.getResource("clipcard.css").toExternalForm();
	}

}
