package org.vaadin.treegrid.client;

import org.vaadin.treegrid.NavigationExtension;

import com.vaadin.client.ServerConnector;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.shared.communication.ServerRpc;
import com.vaadin.shared.ui.Connect;

@Connect(NavigationExtension.class)
public class NavigationExtensionConnector extends AbstractExtensionConnector {
    @Override
    protected void extend(ServerConnector target) {

    }

    @Override
    public TreeGridConnector getParent() {
        return (TreeGridConnector) super.getParent();
    }

    public interface NodeCollapseRpc extends ServerRpc {
        void toggleCollapse(String rowKey);
    }
}
