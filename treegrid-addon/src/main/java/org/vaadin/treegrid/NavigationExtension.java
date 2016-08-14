package org.vaadin.treegrid;

import com.vaadin.ui.Grid;
import org.vaadin.treegrid.client.NavigationExtensionConnector;

/**
 * Created by adam on 01/08/16.
 */
public class NavigationExtension extends Grid.AbstractGridExtension {

    private NavigationExtension(TreeGrid grid) {
        super(grid);

        registerRpc(new NavigationExtensionConnector.NodeExpansionRpc() {
            @Override
            public void toggleExpansion(String rowKey) {
                Object itemId = getItemId(rowKey);
//                grid.handleExpansion(itemId);
                grid.toggleExpansion(itemId);
            }
        });
    }

    public static NavigationExtension extend(TreeGrid grid) {
        return new NavigationExtension(grid);
    }
}
