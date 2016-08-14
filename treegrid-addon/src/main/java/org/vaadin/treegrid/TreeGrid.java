package org.vaadin.treegrid;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.server.JsonCodec;
import com.vaadin.server.communication.data.DataGenerator;
import com.vaadin.shared.ui.grid.GridState;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.ClickableRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;

import elemental.json.JsonObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.vaadin.treegrid.client.*;

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

//        for (Object itemId : container.getItemIds()) {
//            container.getParent(itemId);
//
//            HierarchyData hd = new HierarchyData();
//            // TODO: 12/08/16 only works because of the order. store data in container instead
//            hd.setDepth(container.getParent(itemId) != null ? hierarchyData.get(container.getParent(itemId)).getDepth() + 1 : 0);
//            hd.setExpanded(true);
//            hd.setLeaf(container.getChildren(itemId) == null);
//            hd.setVisible(true);
//            hd.setParentIndex(container.indexOfId(itemId));
//            hierarchyData.put(itemId, hd);
//
//            if (container.getParent(itemId) != null) {
//                hierarchyData.get(container.getParent(itemId)).getChildren().add(hd);
//            }
//        }

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

//        ((Container.Filterable) container).addContainerFilter(new Container.Filter() {
//            @Override
//            public boolean passesFilter(Object itemId, Item item) throws UnsupportedOperationException {
//                return hierarchyData.get(itemId).isVisible();
//            }
//
//            @Override
//            public boolean appliesToProperty(Object propertyId) {
//                return false;
//            }
//        });
        itemVisibilityFilter = new ItemVisibilityFilter();
        ((Container.Filterable) container).addContainerFilter(itemVisibilityFilter);
    }

    public void setExpandableColumn(Object propertyId) {
        this.expandableColumnPropertyId = propertyId;
    }

    ////    @Override
//    public void setContainerDataSource(TreeGridContainer container) {
//        super.setContainerDataSource(container);
//
//        Column expandableColumn = getColumns().get(0);
//
//        Object propId = expandableColumn.getPropertyId();
//
//
//
//        // Render hierarchy on first column
//        setHierarchyRenderer(expandableColumn);
//    }

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

    private void handleExpansion(Object itemId) {
        HierarchyData hd = hierarchyData.get(itemId);

        if (hd.isExpanded()) {
            hd.setExpanded(false);
            hideChildren(hd);
        } else {
            hd.setExpanded(true);
            showChildren(hd);
        }

//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//
//        }

        // TODO: 09/08/16 insert child data
//        getContainerDataSource().addItemAfter(previousPropertyId)
        // TODO: 09/08/16 remove child data: remove or hide?

        // TODO: 29/07/16 HACK, set property or reapply filter. TODO do that without resending everything to client
        ((Container.Filterable) getContainerDataSource()).addContainerFilter(itemVisibilityFilter);
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
//        HierarchyRenderer renderer = new HierarchyRenderer(org.vaadin.treegrid.HierarchyData.class);

        // Listen to click events
        renderer.addClickListener(new ClickableRenderer.RendererClickListener() {
            @Override
            public void click(ClickableRenderer.RendererClickEvent rendererClickEvent) {
                // handle hierarchy click events
                Object itemId = rendererClickEvent.getItemId();

//                handleExpansion(itemId);
                toggleExpansion(itemId);

//                if (getContainer().doFilterContainer(true)) {
//                    getContainer().fireItemSetChange();
//                }
            }
        });

//        HRenderer renderer = new HRenderer(String.class);

        column.setRenderer(renderer);

//        getColumn("email").setRenderer(new HtmlRenderer());


        // ---
//        column.setRenderer(new HierarchyRenderer(String.class));
//        column.setRenderer(new HierarchyRenderer2(String.class));
        // ---


//        column.setRenderer(new HierarchyRenderer2(column.getRenderer()));

//        // Set renderer on column
//        column.setRenderer(new _HierarchyRenderer(ExpandableCell.class), new Converter<ExpandableCell, Object>() {
//
//            @Override
//            public Object convertToModel(ExpandableCell expandableCell, Class<?> aClass, Locale locale) throws ConversionException {
//                throw new UnsupportedOperationException("Not implemented");
//            }
//
//            @Override
//            public ExpandableCell convertToPresentation(Object o, Class<? extends ExpandableCell> aClass, Locale locale) throws ConversionException {
//                ExpandableCell cell = new ExpandableCell();
//
//                cell.setValue(o);
//                cell.setDepth(hierarchyData.get(o).indentation);
//                cell.setExpanded(hierarchyData.get(o).open);
//
//                return cell;
//            }
//
//            @Override
//            public Class<Object> getModelType() {
//                return Object.class;
//            }
//
//            @Override
//            public Class<ExpandableCell> getPresentationType() {
//                return ExpandableCell.class;
//            }
//        });
    }

    public TreeGridContainer getContainer() {
        return (TreeGridContainer) super.getContainerDataSource();
    }

    private class HierarchyDataGenerator extends AbstractGridExtension implements DataGenerator {
        @Override
        public void generateData(Object itemId, Item item, JsonObject rowData) {

//            rowData.put(GridState.JSONKEY_ROWDESCRIPTION,
//                    JsonCodec.encode(hierarchyData.get(itemId), null, HierarchyData.class,
//                            getUI().getConnectorTracker()).getEncodedValue());
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
//            return hierarchyData.get(itemId).isVisible();

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
