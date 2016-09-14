package org.vaadin.treegrid.client;

import org.vaadin.treegrid.NavigationExtension;

import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.extensions.AbstractExtensionConnector;
//import com.vaadin.client.widget.grid.BrowserEventHandler;
import com.vaadin.client.widget.grid.EventCellReference;
import com.vaadin.shared.communication.ServerRpc;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.grid.GridState;

import elemental.json.JsonObject;

@Connect(NavigationExtension.class)
public class NavigationExtensionConnector extends AbstractExtensionConnector {
    @Override
    protected void extend(ServerConnector target) {
        final TreeGrid grid = getParent().getWidget();

//        grid.addBrowserEventHandler(6, new BrowserEventHandler<JsonObject>() {
//            @Override
//            public boolean onEvent(Event event, EventCellReference<JsonObject> cell) {
//                if (event.getType().equals(BrowserEvents.KEYDOWN)) {
//
//                    // Navigate within hierarchy with ALT/OPTION + ARROW KEY when hierarchy column is selected
//                    if (isHierarchyColumn(cell) && event.getAltKey() && (event.getKeyCode() == KeyCodes.KEY_LEFT
//                            || event.getKeyCode() == KeyCodes.KEY_RIGHT)) {
//
//                        // Hierarchy metadata
//                        boolean collapsed, leaf;
//                        int depth, parentIndex;
//                        if (cell.getRow().hasKey(GridState.JSONKEY_ROWDESCRIPTION)) {
//                            JsonObject rowDescription = cell.getRow().getObject(GridState.JSONKEY_ROWDESCRIPTION);
//                            collapsed = rowDescription.getBoolean("collapsed");
//                            leaf = rowDescription.getBoolean("leaf");
//                            depth = (int) rowDescription.getNumber("depth");
//                            parentIndex = (int) rowDescription.getNumber("parentIndex");
//
//                            switch (event.getKeyCode()) {
//                            case KeyCodes.KEY_RIGHT:
//                                if (!leaf) {
//                                    if (collapsed) {
//                                        NodeCollapseRpc rpc = getRpcProxy(NodeCollapseRpc.class);
//                                        String rowKey = getParent().getRowKey(cell.getRow());
//                                        rpc.toggleCollapse(rowKey);
//                                    } else {
//                                        // Focus on next row
//                                        grid.focusCell(cell.getRowIndex() + 1, cell.getColumnIndex());
//                                    }
//                                }
//                                break;
//                            case KeyCodes.KEY_LEFT:
//                                if (!collapsed) {
//                                    // collapse node
//                                    NodeCollapseRpc rpc = getRpcProxy(NodeCollapseRpc.class);
//                                    rpc.toggleCollapse(getParent().getRowKey(cell.getRow()));
//                                } else if (depth > 0) {
//                                    // jump to parent
//                                    grid.focusCell(parentIndex, cell.getColumnIndex());
//                                }
//                                break;
//                            }
//                        }
//                        return true;
//                    }
//                }
//                return false;
//            }
//        });
    }

    private boolean isHierarchyColumn(EventCellReference<JsonObject> cell) {
        return cell.getColumn().getRenderer() instanceof HierarchyRenderer;
    }

    @Override
    public TreeGridConnector getParent() {
        return (TreeGridConnector) super.getParent();
    }

    public interface NodeCollapseRpc extends ServerRpc {
        void toggleCollapse(String rowKey);
    }
}
