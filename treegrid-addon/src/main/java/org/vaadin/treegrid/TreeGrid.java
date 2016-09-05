package org.vaadin.treegrid;

import org.vaadin.treegrid.container.IndexedHierarchical;
import org.vaadin.treegrid.container.Measurable;

import com.vaadin.data.Collapsible;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.server.JsonCodec;
import com.vaadin.server.communication.data.DataGenerator;
import com.vaadin.shared.ui.grid.GridState;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.ClickableRenderer;

import elemental.json.JsonObject;

public class TreeGrid extends Grid {

    private Column hierarchyColumn;

    public TreeGrid() {
        super();

        // Attaches hierarchy data to the row
        addExtension(new HierarchyDataGenerator());

        // Override keyboard navigation
        NavigationExtension.extend(this);
    }

    @Override
    public void setContainerDataSource(Container.Indexed container) {
        if (container != null && !(container instanceof Container.Hierarchical)) {
            container = new ContainerHierarchicalIndexedWrapper(container);
        }
        super.setContainerDataSource(container);
    }

    public void setHierarchyColumn(Object propertyId) {
        hierarchyColumn = getColumn(propertyId);

        // TODO: 31/08/16 implement composite, possibly not including server side renderer
        setHierarchyRenderer(hierarchyColumn);
    }

    void toggleExpansion(Object itemId) {
        // TODO: 05/09/16 Use strategy?
        if (getContainerDataSource() instanceof Collapsible) {
            Collapsible container = (Collapsible) getContainerDataSource();
            container.setCollapsed(itemId, !container.isCollapsed(itemId)); // Collapsible
        }

        // TODO: 01/09/16 Is additional support needed for collapsed item visibility?
    }

    private void setHierarchyRenderer(Column column) {
        // Instantiate hierarchy renderer
        HierarchyRenderer renderer = new HierarchyRenderer(String.class);

        // Listen to click events
        renderer.addClickListener(new ClickableRenderer.RendererClickListener() {
            @Override
            public void click(ClickableRenderer.RendererClickEvent rendererClickEvent) {
                // handle hierarchy click events
                Object itemId = rendererClickEvent.getItemId();
                toggleExpansion(itemId);
            }
        });

        column.setRenderer(renderer);
    }

    /* TreeGrid's data source implements both Indexed and Hierarchical hence this override is safe */
    @Override
    public IndexedHierarchical getContainerDataSource() {
        return (IndexedHierarchical) super.getContainerDataSource();
    }

    /**
     * Metadata generator for hierarchy information
     */
    private class HierarchyDataGenerator extends AbstractGridExtension implements DataGenerator {
        @Override
        public void generateData(Object itemId, Item item, JsonObject rowData) {
            IndexedHierarchical container = getContainerDataSource();

            HierarchyData hierarchyData = new HierarchyData();

            // calculate depth
            int depth = 0;
            if (container instanceof Measurable) {
                depth = ((Measurable) container).getDepth(itemId);  // Measurable
            } else {
                Object id = itemId;
                while (!container.isRoot(id)) {
                    id = container.getParent(id);
                    depth++;
                }
            }
            hierarchyData.setDepth(depth);

            // set collapsed state
            if (container instanceof Collapsible) {
                hierarchyData.setExpanded(!((Collapsible) container).isCollapsed(itemId));  // Collapsible
            } else {
                // TODO: 05/09/16 add default support for collapse if container doesn't support it?
                hierarchyData.setExpanded(true);
            }

            // set leaf state
            hierarchyData.setLeaf(!container.hasChildren(itemId));  // Hierarchical

            // set index of parent node
            hierarchyData.setParentIndex(container
                    .indexOfId(container.getParent(itemId))); // Indexed (indexOfId) and Hierarchical (getParent)

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
