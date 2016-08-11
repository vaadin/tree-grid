package org.vaadin.treegrid.client;

import com.vaadin.client.connectors.GridConnector;
import com.vaadin.client.widgets.Grid;
import com.vaadin.shared.ui.Connect;
import elemental.json.JsonObject;
import org.vaadin.treegrid.*;

/**
 * Created by adam on 02/08/16.
 */
@Connect(org.vaadin.treegrid.TreeGrid.class)
public class TreeGridConnector extends GridConnector {

    @Override
    public TreeGrid getWidget() {
        return (TreeGrid) super.getWidget();
    }
}
