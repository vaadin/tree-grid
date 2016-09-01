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

    public TreeGrid() {
        super();

        // Attaches hierarchy data to the row
        addExtension(new HierarchyDataGenerator());

        // Override keyboard navigation
        NavigationExtension.extend(this);


    }

    @Deprecated
    public void setContainerDataSource(TreeGridContainer container) {
        super.setContainerDataSource(container);

        // TODO: 31/08/16 Possibly set hierarchy column explicitly or at least take into account the select column
        Column expandableColumn = getColumns().get(0);
        setHierarchyRenderer(expandableColumn); // TODO: 31/08/16 implement composite, possibly not including server side renderer
    }


//    public <T extends Container.Indexed & Container.Hierarchical> void setContainerDataSource(T container) {
//
//    }
    // TODO: 31/08/16 make the caller pass a Hierarchical container
    @Override
    public void setContainerDataSource(Container.Indexed container) {
        if (!(container instanceof Container.Hierarchical)) {
            throw new IllegalArgumentException("Container must implement Hierarchical interface");
        }

        // TODO: 31/08/16 maybe implement something similar to TreeTable (below) for containers that don't implement Hierarchical
//        if (container != null && !(container instanceof Container.Hierarchical)) {
//            container = new ContainerHierarchicalWrapper(container);
//        }
//
//        if (container != null && !(container instanceof Container.Ordered)) {
//            container = new HierarchicalContainerOrderedWrapper(
//                    (Container.Hierarchical) container);
//        }

        super.setContainerDataSource(container);
    }

    void toggleExpansion(Object itemId) {
        Collapsible container = (Collapsible) getContainerDataSource();
        container.setCollapsed(itemId, !container.isCollapsed(itemId)); // Collapsible

        // TODO: 01/09/16 Does additional support needed for collapsed item visibility?
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

    private class HierarchyDataGenerator extends AbstractGridExtension implements DataGenerator {
        @Override
        public void generateData(Object itemId, Item item, JsonObject rowData) {

            TreeGridContainer container = (TreeGridContainer) getContainerDataSource();

            HierarchyData hierarchyData = new HierarchyData();
            hierarchyData.setDepth(container.getDepth(itemId)); // ? TreeGridContainer
            hierarchyData.setExpanded(!((Collapsible) container).isCollapsed(itemId));   // Collapsible
            hierarchyData.setLeaf(!container.hasChildren(itemId));  // Hierarchical
            hierarchyData.setParentIndex(container.indexOfId(container.getParent(itemId))); // Indexed (indexOfId) and Hierarchical (getParent)

            rowData.put(GridState.JSONKEY_ROWDESCRIPTION, JsonCodec
                    .encode(hierarchyData, null, HierarchyData.class,
                            getUI().getConnectorTracker()).getEncodedValue());
        }

        @Override
        public void destroyData(Object itemId) {

        }
    }
}
