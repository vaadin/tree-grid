package org.vaadin.treegrid;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.server.JsonCodec;
import com.vaadin.server.communication.data.DataGenerator;
import com.vaadin.shared.ui.grid.GridState;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.ClickableRenderer;

import elemental.json.JsonObject;

// This is the server-side UI component that provides public API 
// for TreeGrid
public class TreeGrid extends Grid {

    // Stores hierarchy data
    private Map<Object, HierarchyData> hierarchyData = new HashMap<>();

    private Container.Filter itemVisibilityFilter;

    @Deprecated
    private Object expandableColumnPropertyId;

    public TreeGrid() {
        super();

        // Attaches hierarchy data to the row
        addExtension(new HierarchyDataGenerator());

        // Override keyboard navigation
        NavigationExtension.extend(this);


    }

    public void setContainerDataSource(TreeGridContainer container) {
        super.setContainerDataSource(container);

        Column expandableColumn = getColumns().get(0);
        setHierarchyRenderer(expandableColumn);

        // TODO: 13/08/16 filter
        itemVisibilityFilter = new ItemVisibilityFilter();
        container.addContainerFilter(itemVisibilityFilter);
    }

    @Override
    public void setContainerDataSource(Container.Indexed container) {
        super.setContainerDataSource(container);

        Column expandableColumn = getColumns().get(0);
        setHierarchyRenderer(expandableColumn);
//        setHierarchyRenderer(getColumn("email"));

        getColumn("email").setConverter(new Converter<String, String>() {
            @Override
            public String convertToModel(String value,
                    Class<? extends String> targetType, Locale locale) throws
                    ConversionException {
                return null;
            }

            @Override
            public String convertToPresentation(String value,
                    Class<? extends String> targetType, Locale locale) throws
                    ConversionException {
                return String.format("<a href=#>%s</a>", value);
            }

            @Override
            public Class<String> getModelType() {
                return String.class;
            }

            @Override
            public Class<String> getPresentationType() {
                return String.class;
            }
        });
//        getColumn("email").setRenderer(new HtmlRenderer());

        // FIXME fake hierarchy data, to be removed
        for (int i = 0; i < container.size(); i++) {
            HierarchyData hd = new HierarchyData();
            hd.setDepth(i % 3);
            hd.setExpanded(true);
            hd.setLeaf(i % 3 == 2);
            hd.setVisible(true);
            hierarchyData.put(container.getIdByIndex(i), hd);
        }

        for (int i = 0; i < container.size() - 1; i++) {
            if (i % 3 < 2) {
                HierarchyData hd = hierarchyData.get(container.getIdByIndex(i));
                hd.getChildren().add(hierarchyData.get(container.getIdByIndex(i+1)));
            }
        }

        itemVisibilityFilter = new ItemVisibilityFilter();
        ((Container.Filterable) container).addContainerFilter(itemVisibilityFilter);
    }

    public void setExpandableColumn(Object propertyId) {
        this.expandableColumnPropertyId = propertyId;
    }

    private void hideChildren(HierarchyData hd) {
        for (HierarchyData chd : hd.getChildren()) {
            chd.setVisible(false);
            hideChildren(chd);
        }
    }

    private void showChildren(HierarchyData hd) {
        for (HierarchyData chd : hd.getChildren()) {
            chd.setVisible(true);
            if (chd.isExpanded()) {
                showChildren(chd);
            }
        }
    }

    void toggleExpansion(Object itemId) {
        getContainer().setCollapsed(itemId, !getContainer().isCollapsed(itemId));

        if (getContainer().doFilterContainer(true)) {
            getContainer().fireItemSetChange();
        }
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

    public TreeGridContainer getContainer() {
        return (TreeGridContainer) super.getContainerDataSource();
    }

    private class HierarchyDataGenerator extends AbstractGridExtension implements DataGenerator {
        @Override
        public void generateData(Object itemId, Item item, JsonObject rowData) {

            rowData.put(GridState.JSONKEY_ROWDESCRIPTION,
                    JsonCodec.encode(getContainer().getHierarchyData(itemId), null, HierarchyData.class,
                            getUI().getConnectorTracker()).getEncodedValue());
        }

        @Override
        public void destroyData(Object itemId) {

        }
    }

    private class ItemVisibilityFilter implements Container.Filter {
        @Override
        public boolean passesFilter(Object itemId, Item item) throws UnsupportedOperationException {

            // TODO: 14/08/16 root
//            return getContainer().isRoot(itemId) || !getContainer().isCollapsed(getContainer().getParent(itemId));
            boolean root = getContainer().getHierarchyData(itemId).getDepth() == 0;
            boolean ancestorCollapsed = false;
            Object parentId = itemId;
            while ((parentId = getContainer().getParent(parentId, true)) != null && !ancestorCollapsed) {
                if (getContainer().isCollapsed(parentId)) {
                    ancestorCollapsed = true;
                }
            }

            return root || !ancestorCollapsed;
//            return getContainer().getHierarchyData(itemId).getDepth() == 0 ||
//                    !getContainer().isCollapsed(getContainer().getParent(itemId, true));
        }

        @Override
        public boolean appliesToProperty(Object propertyId) {
            return false;
        }
    }

    // TODO: 11/08/16 setCollapsed(itemId, boolean)
}
