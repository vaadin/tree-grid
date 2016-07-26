package org.vaadin.treegrid.client;

import com.google.gwt.user.client.ui.Label;

// Extend any GWT Widget
public class TreeGridWidget extends Label {

	public TreeGridWidget() {

		// CSS class-name should not be v- prefixed
		setStyleName("treegrid");

		// State is set to widget in TreeGridConnector		
	}

}