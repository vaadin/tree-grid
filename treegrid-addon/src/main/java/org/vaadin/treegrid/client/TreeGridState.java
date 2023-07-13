package org.vaadin.treegrid.client;

import com.vaadin.shared.ui.grid.GridState;

public class TreeGridState extends GridState {

	public static final String JSONKEY_HIERARCHYDATA = "hd";

    /**
     * Contains ID of hierarchy column set by the developer
     */
    public String hierarchyColumnId;
}
