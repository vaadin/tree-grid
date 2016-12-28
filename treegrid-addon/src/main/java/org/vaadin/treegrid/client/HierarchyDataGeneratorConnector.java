package org.vaadin.treegrid.client;

import org.vaadin.treegrid.HierarchyDataGenerator;

import com.vaadin.client.ServerConnector;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.shared.ui.Connect;

@Connect(HierarchyDataGenerator.class)
public class HierarchyDataGeneratorConnector extends AbstractExtensionConnector {
    @Override
    protected void extend(ServerConnector target) {
        // NOP
    }
}
