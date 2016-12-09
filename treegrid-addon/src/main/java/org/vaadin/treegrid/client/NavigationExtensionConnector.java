package org.vaadin.treegrid.client;

import org.vaadin.treegrid.NavigationExtension;

import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.widget.grid.EventCellReference;
import com.vaadin.client.widget.grid.GridEventHandler;
import com.vaadin.client.widgets.Grid;
import com.vaadin.shared.communication.ServerRpc;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.grid.GridState;

import elemental.json.JsonObject;

@Connect(NavigationExtension.class)
public class NavigationExtensionConnector extends AbstractExtensionConnector {
    @Override
    protected void extend(ServerConnector target) {
        final TreeGrid grid = getParent().getWidget();

        grid.addBrowserEventHandler(5, new GridEventHandler<JsonObject>() {
            @Override
            public void onEvent(Grid.GridEvent<JsonObject> event) {
                if (event.isHandled()) {
                    return;
                }

                Event domEvent = event.getDomEvent();

                if (domEvent.getType().equals(BrowserEvents.KEYDOWN)) {

                    // Navigate within hierarchy with ALT/OPTION + ARROW KEY when hierarchy column is selected
                    if (isHierarchyColumn(event.getCell()) && domEvent.getAltKey() && (
                            domEvent.getKeyCode() == KeyCodes.KEY_LEFT
                                    || domEvent.getKeyCode() == KeyCodes.KEY_RIGHT)) {

                        // Hierarchy metadata
                        boolean collapsed, leaf;
                        int depth, parentIndex;
                        if (event.getCell().getRow().hasKey(GridState.JSONKEY_ROWDESCRIPTION)) {
                            JsonObject rowDescription = event.getCell().getRow()
                                    .getObject(GridState.JSONKEY_ROWDESCRIPTION);
                            collapsed = rowDescription.getBoolean("collapsed");
                            leaf = rowDescription.getBoolean("leaf");
                            depth = (int) rowDescription.getNumber("depth");
                            parentIndex = (int) rowDescription.getNumber("parentIndex");

                            switch (domEvent.getKeyCode()) {
                            case KeyCodes.KEY_RIGHT:
                                if (!leaf) {
                                    if (collapsed) {
                                        toggleCollapse(getParent().getRowKey(event.getCell().getRow()));
                                    } else {
                                        // Focus on next row
                                        grid.focusCell(event.getCell().getRowIndex() + 1,
                                                event.getCell().getColumnIndex());
                                    }
                                }
                                break;
                            case KeyCodes.KEY_LEFT:
                                if (!collapsed) {
                                    // collapse node
                                    toggleCollapse(getParent().getRowKey(event.getCell().getRow()));
                                } else if (depth > 0) {
                                    // jump to parent
                                    grid.focusCell(parentIndex, event.getCell().getColumnIndex());
                                }
                                break;
                            }
                        }
                        event.setHandled(true);
                        return;
                    }
                }
                event.setHandled(false);
            }
        });
    }

    private boolean isHierarchyColumn(EventCellReference<JsonObject> cell) {
        return cell.getColumn().getRenderer() instanceof HierarchyRenderer;
    }

    void toggleCollapse(String rowKey) {
        getRpcProxy(NodeCollapseRpc.class).toggleCollapse(rowKey);
    }

    @Override
    public TreeGridConnector getParent() {
        return (TreeGridConnector) super.getParent();
    }

    public interface NodeCollapseRpc extends ServerRpc {
        void toggleCollapse(String rowKey);
    }
}
