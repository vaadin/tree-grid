package org.vaadin.treegrid.client;

import com.vaadin.client.connectors.AbstractRendererConnector;
import com.vaadin.shared.ui.Connect;

@Connect(org.vaadin.treegrid.HierarchyRenderer2.class)
public class HierarchyRenderer2Connector extends AbstractRendererConnector<String> {
    @Override
    public HierarchyRenderer2 getRenderer() {
        return (HierarchyRenderer2) super.getRenderer();
    }
}
