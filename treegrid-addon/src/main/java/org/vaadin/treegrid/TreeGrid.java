package org.vaadin.treegrid;

import java.lang.reflect.Method;
import java.util.logging.Logger;

import org.vaadin.treegrid.client.TreeGridState;
import org.vaadin.treegrid.container.ContainerCollapsibleWrapper;
import org.vaadin.treegrid.container.IndexedContainerHierarchicalWrapper;

import com.vaadin.data.Collapsible;
import com.vaadin.data.Container;
import com.vaadin.shared.ui.grid.GridColumnState;
import com.vaadin.ui.Grid;

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

    @Override
    protected TreeGridState getState() {
        return (TreeGridState) super.getState();
    }

    void toggleExpansion(Object itemId) {
        if (getContainerDataSource() instanceof Collapsible) {
            Collapsible container = (Collapsible) getContainerDataSource();
            container.setCollapsed(itemId, !container.isCollapsed(itemId)); // Collapsible
        }
    }
}
