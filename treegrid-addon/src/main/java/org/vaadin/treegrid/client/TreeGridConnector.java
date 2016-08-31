package org.vaadin.treegrid.client;

import com.vaadin.client.connectors.GridConnector;
import com.vaadin.shared.ui.Connect;

@Connect(org.vaadin.treegrid.TreeGrid.class)
public class TreeGridConnector extends GridConnector {

    @Override
    public TreeGrid getWidget() {
        return (TreeGrid) super.getWidget();
    }
}
