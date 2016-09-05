package org.vaadin.treegrid;

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

    /**
     * Metadata generator for hierarchy information
     */
    private class HierarchyDataGenerator extends AbstractGridExtension implements DataGenerator {
        @Override
        public void generateData(Object itemId, Item item, JsonObject rowData) {

            TreeGridContainer container = (TreeGridContainer) getContainerDataSource();

            HierarchyData hierarchyData = new HierarchyData();
            hierarchyData.setDepth(container.getDepth(itemId)); // ? TreeGridContainer
            hierarchyData.setExpanded(!((Collapsible) container).isCollapsed(itemId));   // Collapsible
            hierarchyData.setLeaf(!container.hasChildren(itemId));  // Hierarchical
            hierarchyData.setParentIndex(container
                    .indexOfId(container.getParent(itemId))); // Indexed (indexOfId) and Hierarchical (getParent)

            rowData.put(GridState.JSONKEY_ROWDESCRIPTION, JsonCodec
                    .encode(hierarchyData, null, HierarchyData.class,
                            getUI().getConnectorTracker()).getEncodedValue());
        }

        @Override
        public void destroyData(Object itemId) {

        }
    }
}
