package org.vaadin.treegrid.client;

import com.vaadin.client.widgets.Grid;
import elemental.json.JsonObject;

/**
 * Created by adam on 02/08/16.
 */
public class TreeGrid extends Grid<JsonObject> {

    void setFocus(int rowIndex, int columnIndex) {
        focusCell(rowIndex, columnIndex);
    }

//    public void addExpandHandler() {
//
//    }
}
