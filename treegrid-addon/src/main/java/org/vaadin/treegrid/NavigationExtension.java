package org.vaadin.treegrid;

import com.vaadin.ui.Grid;

import org.vaadin.treegrid.client.NavigationExtensionConnector;

public class NavigationExtension extends Grid.AbstractGridExtension {

    private NavigationExtension(TreeGrid grid) {
        super(grid);

        registerRpc(new NavigationExtensionConnector.NodeCollapseRpc() {
            @Override
            public void toggleCollapse(String rowKey) {
                Object itemId = getItemId(rowKey);
                grid.toggleExpansion(itemId);
            }
        });
    }

    public static NavigationExtension extend(TreeGrid grid) {
        return new NavigationExtension(grid);
    }
}
