package org.vaadin.treegrid.client;

import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.widget.grid.BrowserEventHandler;
import com.vaadin.client.widget.grid.EventCellReference;
import com.vaadin.shared.communication.ServerRpc;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.grid.GridState;
import elemental.json.JsonObject;
import org.vaadin.treegrid.NavigationExtension;

/**
 * Created by adam on 01/08/16.
 */
@Connect(NavigationExtension.class)
public class NavigationExtensionConnector extends AbstractExtensionConnector {
    @Override
    protected void extend(ServerConnector target) {
        final TreeGrid grid = getParent().getWidget();

        grid.addBrowserEventHandler(6, new BrowserEventHandler<JsonObject>() {
            @Override
            public boolean onEvent(Event event, EventCellReference<JsonObject> cell) {
//                if (true) {
//                    return false;
//                }
                if (event.getType().equals(BrowserEvents.KEYDOWN)) {
                    if (isHierarchyColumn(cell) &&
                            (event.getKeyCode() == KeyCodes.KEY_LEFT || event.getKeyCode() == KeyCodes.KEY_RIGHT)) {

                        boolean expanded = false;
                        boolean leaf = false;
                        int depth;
                        int parentIndex;
                        if (cell.getRow().hasKey(GridState.JSONKEY_ROWDESCRIPTION)) {
                            JsonObject rowDescription = cell.getRow().getObject(GridState.JSONKEY_ROWDESCRIPTION);
                            expanded = rowDescription.getBoolean("expanded");
                            leaf = rowDescription.getBoolean("leaf");
                            depth = (int) rowDescription.getNumber("depth");
                            parentIndex = (int) rowDescription.getNumber("parentIndex");

                            switch (event.getKeyCode()) {
                                case KeyCodes.KEY_RIGHT:
                                    if (!leaf) {
                                        if (expanded) {
                                            // Focus on next row
                                            grid.setFocus(cell.getRowIndex() + 1, cell.getColumnIndex());
                                            // TODO: 03/08/16 how to handle different selection modes?
                                        } else {
                                            NodeExpansionRpc rpc = getRpcProxy(NodeExpansionRpc.class);
                                            String rowKey = getParent().getRowKey(cell.getRow());
                                            rpc.toggleExpansion(rowKey);
                                        }
                                    }
                                    break;
                                case KeyCodes.KEY_LEFT:
                                    if (expanded) {
                                        // collapse node
                                        NodeExpansionRpc rpc = getRpcProxy(NodeExpansionRpc.class);
                                        rpc.toggleExpansion(getParent().getRowKey(cell.getRow()));
                                    } else if (depth > 0) {
                                        // jump to parent
                                        grid.setFocus(parentIndex, cell.getColumnIndex());
                                        // TODO: 04/08/16
//                                        cell.getGrid().scrollToRow(rowIndex);
                                    }
                                    break;
                            }
                        }
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private boolean isHierarchyColumn(EventCellReference<JsonObject> cell) {
        return cell.getColumn().getRenderer() instanceof HierarchyRenderer;
    }

    @Override
    public TreeGridConnector getParent() {
        return (TreeGridConnector) super.getParent();
    }

    public interface NodeExpansionRpc extends ServerRpc {
        void toggleExpansion(String rowKey);
    }
}
