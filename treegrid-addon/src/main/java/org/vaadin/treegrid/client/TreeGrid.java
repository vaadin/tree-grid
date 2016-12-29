package org.vaadin.treegrid.client;

import com.google.gwt.dom.client.Element;
import com.vaadin.client.widgets.Escalator;
import com.vaadin.client.widgets.Grid;

import elemental.json.JsonObject;

public class TreeGrid extends Grid<JsonObject> {

    /**
     * Method for accessing the private {@link Grid#focusCell(int, int)} method from this package
     */
    native void focusCell(int rowIndex, int columnIndex)/*-{
        this.@com.vaadin.client.widgets.Grid::focusCell(II)(rowIndex, columnIndex);
    }-*/;

    /**
     * Expose {@link Grid#getEscalator()} method to current package
     * @return
     */
    @Override
    protected Escalator getEscalator() {
        return super.getEscalator();
    }

    /**
     * Method for accessing the private {@link Grid#isElementInChildWidget(Element)} method from this package
     */
    native boolean isElementInChildWidget(Element e)/*-{
        return this.@com.vaadin.client.widgets.Grid::isElementInChildWidget(*)(e);
    }-*/;
}
