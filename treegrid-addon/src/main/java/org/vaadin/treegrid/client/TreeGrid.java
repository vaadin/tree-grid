package org.vaadin.treegrid.client;

import com.vaadin.client.widgets.Grid;

import elemental.json.JsonObject;

public class TreeGrid extends Grid<JsonObject> {

    // Method for accessing Grid's private focusCell(int, int) method from this package
    native void focusCell(int rowIndex, int columnIndex)
        /*-{
            this.@com.vaadin.client.widgets.Grid::focusCell(II)(rowIndex, columnIndex);
        }-*/;


}
