package org.vaadin.treegrid;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Logger;

import org.vaadin.treegrid.client.TreeGridState;
import org.vaadin.treegrid.container.ContainerCollapsibleWrapper;
import org.vaadin.treegrid.container.IndexedContainerHierarchicalWrapper;
import org.vaadin.treegrid.event.CollapseEvent;
import org.vaadin.treegrid.event.ExpandEvent;

import com.vaadin.data.Collapsible;
import com.vaadin.data.Container;
import com.vaadin.server.EncodeResult;
import com.vaadin.server.JsonCodec;
import com.vaadin.server.ServerRpcManager;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.grid.GridColumnState;
import com.vaadin.shared.ui.grid.GridConstants;
import com.vaadin.shared.ui.grid.GridServerRpc;
import com.vaadin.ui.ConnectorTracker;
import com.vaadin.ui.Grid;

import elemental.json.JsonObject;

/**
 * A grid component for displaying tabular hierarchical data.
 * <p>
 * Grid is always bound to a {@link com.vaadin.data.Container.Indexed} but is not a Container of any kind on itself.
 * <p>
 * For more information please see {@link Grid}'s documentation.
 */
public class TreeGrid extends Grid {

    private static final Logger logger = Logger.getLogger(TreeGrid.class.getName());

    public TreeGrid() {
        super();

        // Replace GridServerRpc with custom one to fix column reorder issue (#6).
        swapServerRpc();

        // Attaches hierarchy data to the row
        HierarchyDataGenerator.extend(this);

        // Override keyboard navigation
        NavigationExtension.extend(this);
    }

    @Override
    public void setContainerDataSource(Container.Indexed container) {
        if (container != null) {
            if (!(container instanceof Container.Hierarchical)) {
                container = new IndexedContainerHierarchicalWrapper(container);
            }

            if (!(container instanceof Collapsible)) {
                container = new ContainerCollapsibleWrapper(container);
            }
        }
        super.setContainerDataSource(container);
    }

    public void setHierarchyColumn(Object propertyId) {

        Column hierarchyColumn = getColumn(propertyId);

        if (hierarchyColumn == null) {
            logger.warning(String.format("Column does not exist with propertyId: %s", propertyId.toString()));
            return;
        }

        GridColumnState columnState = null;

        // Using reflection to access Grid.Column's private getState() method
        try {
            Method getStateMethod = Grid.Column.class.getDeclaredMethod("getState");
            getStateMethod.setAccessible(true);
            columnState = (GridColumnState) getStateMethod.invoke(hierarchyColumn);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (columnState != null) {
            getState().hierarchyColumnId = columnState.id;
        }
    }

    /**
     * Adds an ExpandListener to this TreeGrid.
     *
     * @param listener
     *         the listener to add
     * @since 0.7.6
     */
    public void addExpandListener(ExpandEvent.ExpandListener listener) {
        addListener(ExpandEvent.class, listener, ExpandEvent.ExpandListener.EXPAND_METHOD);
    }

    /**
     * Removes an ExpandListener from this TreeGrid.
     *
     * @param listener
     *         the listener to remove
     * @since 0.7.6
     */
    public void removeExpandListener(ExpandEvent.ExpandListener listener) {
        removeListener(ExpandEvent.class, listener, ExpandEvent.ExpandListener.EXPAND_METHOD);
    }

    /**
     * Adds a CollapseListener to this TreeGrid.
     *
     * @param listener
     *         the listener to add
     * @since 0.7.6
     */
    public void addCollapseListener(CollapseEvent.CollapseListener listener) {
        addListener(CollapseEvent.class, listener, CollapseEvent.CollapseListener.COLLAPSE_METHOD);
    }

    /**
     * Removes a CollapseListener from this TreeGrid.
     *
     * @param listener
     *         the listener to remove
     * @since 0.7.6
     */
    public void removeCollapseListener(CollapseEvent.CollapseListener listener) {
        removeListener(CollapseEvent.class, listener, CollapseEvent.CollapseListener.COLLAPSE_METHOD);
    }


    @Override
    protected TreeGridState getState() {
        return (TreeGridState) super.getState();
    }

    void toggleExpansion(Object itemId) {
        if (getContainerDataSource() instanceof Collapsible) {
            Collapsible container = (Collapsible) getContainerDataSource();

            boolean collapsed = container.isCollapsed(itemId);

            // Expand or collapse the item
            container.setCollapsed(itemId, !collapsed); // Collapsible

            // Fire expand or collapse event
            if (collapsed) {
                fireEvent(new ExpandEvent(this, getContainerDataSource().getItem(itemId), itemId));
            } else {
                fireEvent(new CollapseEvent(this, getContainerDataSource().getItem(itemId), itemId));
            }
        }
    }

    /**
     * Replaces GridServerRpc instance with a custom one created by {@link #createRpc(GridServerRpc)}.
     * <p> Used as a temporary fix for https://github.com/vaadin/tree-grid/issues/6.
     */
    private void swapServerRpc() {
        try {
            // Get original RPC
            ServerRpcManager gridServerRpcManager = getRpcManager(GridServerRpc.class.getName());
            Method getImplementation = gridServerRpcManager.getClass().getDeclaredMethod("getImplementation");
            getImplementation.setAccessible(true);
            GridServerRpc oldRpc = (GridServerRpc) getImplementation.invoke(gridServerRpcManager);

            // Override RPC
            GridServerRpc newRpc = createRpc(oldRpc);

            // Replace old RPC with new one
            registerRpc(newRpc, GridServerRpc.class);

        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Custom server rpc that wraps GridServerRpc. Overrides {@link GridServerRpc#columnsReordered(List, List)} and
     * {@link GridServerRpc#columnVisibilityChanged(String, boolean, boolean)} methods and delegates all others to the
     * wrapped instance.
     */
    private GridServerRpc createRpc(final GridServerRpc innerRpc) {
        return new GridServerRpc() {
            @Override
            public void sort(String[] columnIds, SortDirection[] directions, boolean userOriginated) {
                // Delegated to wrapped RPC
                innerRpc.sort(columnIds, directions, userOriginated);
            }

            @Override
            public void itemClick(String rowKey, String columnId, MouseEventDetails details) {
                // Delegated to wrapped RPC
                innerRpc.itemClick(rowKey, columnId, details);
            }

            @Override
            public void contextClick(int rowIndex, String rowKey, String columnId, GridConstants.Section section,
                    MouseEventDetails details) {
                // Delegated to wrapped RPC
                innerRpc.contextClick(rowIndex, rowKey, columnId, section, details);
            }

            /**
             * Different from the one in Grid that it uses {@link Class#getField(String)} to be able to
             * access field in super class.
             */
            @Override
            public void columnsReordered(List<String> newColumnOrder, List<String> oldColumnOrder) {
                // Copied from wrapped RPC and modified
                final String diffStateKey = "columnOrder";
                ConnectorTracker connectorTracker = getUI()
                        .getConnectorTracker();
                JsonObject diffState = connectorTracker.getDiffState(TreeGrid.this);
                // discard the change if the columns have been reordered from
                // the server side, as the server side is always right
                if (getState(false).columnOrder.equals(oldColumnOrder)) {
                    // Don't mark as dirty since client has the state already
                    getState(false).columnOrder = newColumnOrder;
                    // write changes to diffState so that possible reverting the
                    // column order is sent to client
                    assert diffState
                            .hasKey(diffStateKey) : "Field name has changed";
                    Type type = null;
                    try {
                        type = (getState(false).getClass()
                                .getField(diffStateKey)
                                .getGenericType());
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                    EncodeResult encodeResult = JsonCodec.encode(
                            getState(false).columnOrder, diffState, type,
                            connectorTracker);

                    diffState.put(diffStateKey, encodeResult.getEncodedValue());
                    fireEvent(new ColumnReorderEvent(TreeGrid.this, true));
                } else {
                    // make sure the client is reverted to the order that the
                    // server thinks it is
                    diffState.remove(diffStateKey);
                    markAsDirty();
                }
            }

            /**
             * Different from the one in Grid that it uses {@link Class#getField(String)} to be able to
             * access field in super class.
             */
            @Override
            public void columnVisibilityChanged(String id, boolean hidden, boolean userOriginated) {
                // Copied from wrapped RPC and modified
                try {
                    final Column column;
                    Method getColumnByColumnId = Grid.class.getDeclaredMethod("getColumnByColumnId", String.class);
                    column = (Column) getColumnByColumnId.invoke(TreeGrid.this, id);

                    Method getState = Column.class.getDeclaredMethod("getState");

                    final GridColumnState columnState = (GridColumnState) getState.invoke(column);

                    if (columnState.hidden != hidden) {
                        columnState.hidden = hidden;

                        final String diffStateKey = "columns";
                        ConnectorTracker connectorTracker = getUI()
                                .getConnectorTracker();
                        JsonObject diffState = connectorTracker
                                .getDiffState(TreeGrid.this);

                        assert diffState
                                .hasKey(diffStateKey) : "Field name has changed";
                        Type type = null;
                        try {
                            type = (getState(false).getClass().getSuperclass()
                                    .getDeclaredField(diffStateKey)
                                    .getGenericType());
                        } catch (NoSuchFieldException e) {
                            e.printStackTrace();
                        } catch (SecurityException e) {
                            e.printStackTrace();
                        }
                        EncodeResult encodeResult = JsonCodec.encode(
                                getState(false).columns, diffState, type,
                                connectorTracker);

                        diffState.put(diffStateKey, encodeResult.getEncodedValue());

                        fireEvent(new ColumnVisibilityChangeEvent(TreeGrid.this, column, hidden,
                                userOriginated));
                    }

                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void columnResized(String id, double pixels) {
                // Delegated to wrapped RPC
                innerRpc.columnResized(id, pixels);
            }
        };
    }
}
