package org.vaadin.treegrid;

import org.vaadin.treegrid.container.ContainerCollapsibleWrapper;
import org.vaadin.treegrid.container.IndexedContainerHierarchicalWrapper;
import org.vaadin.treegrid.container.Measurable;

import com.vaadin.data.Collapsible;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.server.JsonCodec;
import com.vaadin.server.communication.data.DataGenerator;
import com.vaadin.shared.ui.grid.GridState;
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

    public TreeGrid() {
        super();

        // Attaches hierarchy data to the row
        addExtension(new HierarchyDataGenerator());

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

    void toggleExpansion(Object itemId) {
        if (getContainerDataSource() instanceof Collapsible) {
            Collapsible container = (Collapsible) getContainerDataSource();
            container.setCollapsed(itemId, !container.isCollapsed(itemId)); // Collapsible
        }
    }

    /**
     * Get container data source that implements {@link com.vaadin.data.Container.Indexed} and {@link
     * com.vaadin.data.Container.Hierarchical} as well.
     *
     * @return TreeGrid's container data source
     */
    @SuppressWarnings("unchecked")
    private <T extends Container.Indexed & Container.Hierarchical> T getContainer() {
        // TreeGrid's data source has to implement both Indexed and Hierarchical so it is safe to cast.
        return (T) super.getContainerDataSource();
    }

    /**
     * Metadata generator for hierarchy information
     */
    private class HierarchyDataGenerator extends AbstractGridExtension implements DataGenerator {
        @Override
        public void generateData(Object itemId, Item item, JsonObject rowData) {
//            Container.Indexed container = getContainerDataSource();

            HierarchyData hierarchyData = new HierarchyData();

            // calculate depth
            int depth = 0;
            if (getContainer() instanceof Measurable) {
                depth = ((Measurable) getContainer()).getDepth(itemId);  // Measurable
            } else {
                Object id = itemId;
                while (!getContainer().isRoot(id)) {
                    id = getContainer().getParent(id);
                    depth++;
                }
            }
            hierarchyData.setDepth(depth);

            // set collapsed state
            if (getContainer() instanceof Collapsible) {
                hierarchyData.setCollapsed(((Collapsible) getContainer()).isCollapsed(itemId));  // Collapsible
            }

            // set leaf state
            hierarchyData.setLeaf(!getContainer().hasChildren(itemId));  // Hierarchical

            // set index of parent node
            hierarchyData.setParentIndex(getContainer()
                    .indexOfId(getContainer().getParent(itemId))); // Indexed (indexOfId) and Hierarchical (getParent)

            // add hierarchy information to row as metadata
            rowData.put(GridState.JSONKEY_ROWDESCRIPTION,
                    JsonCodec.encode(hierarchyData, null, HierarchyData.class, getUI().getConnectorTracker())
                            .getEncodedValue());
        }

        @Override
        public void destroyData(Object itemId) {
            // nothing to clean up
        }
    }
}
