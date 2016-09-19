package org.vaadin.treegrid.client;

import java.util.Map;

import com.google.web.bindery.event.shared.HandlerRegistration;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.connectors.AbstractRendererConnector;
import com.vaadin.client.connectors.GridConnector;
import com.vaadin.client.renderers.ClickableRenderer;
import com.vaadin.client.widgets.Grid;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.grid.GridColumnState;

import elemental.json.JsonObject;

@Connect(org.vaadin.treegrid.TreeGrid.class)
public class TreeGridConnector extends GridConnector {

    @Override
    public TreeGrid getWidget() {
        return (TreeGrid) super.getWidget();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        // Change renderer of hierarchy column
        if (stateChangeEvent.hasPropertyChanged("columns")) {

            // TODO: 26/09/16 Make hierarchy column selectable
            GridColumnState columnState = getState().columns.get(0);
            Grid.Column column = getColumnIdToColumn().get(columnState.id);

            HierarchyRenderer wrapperRenderer = getHierarchyRenderer();
            wrapperRenderer.setInnerRenderer(((AbstractRendererConnector) columnState.rendererConnector).getRenderer());
            column.setRenderer(wrapperRenderer);
        }
    }

    // Hack to access private column map
    private native Map<String, ? extends Grid.Column> getColumnIdToColumn()
        /*-{
            return this.@com.vaadin.client.connectors.GridConnector::columnIdToColumn;
        }-*/;

    private HierarchyRenderer hierarchyRenderer;

    private HierarchyRenderer getHierarchyRenderer() {
        if (hierarchyRenderer == null) {
            hierarchyRenderer = new HierarchyRenderer();
        }
        return hierarchyRenderer;
    }

    // Expander click event handling

    private HandlerRegistration expanderClickHandlerRegistration;

    @Override
    protected void init() {
        super.init();

        expanderClickHandlerRegistration = getHierarchyRenderer()
                .addClickHandler(new ClickableRenderer.RendererClickHandler<JsonObject>() {
                    @Override
                    public void onClick(ClickableRenderer.RendererClickEvent<JsonObject> event) {
                        NavigationExtensionConnector navigation = getNavigationExtensionConnector();
                        if (navigation != null) {
                            navigation.toggleCollapse(getRowKey(event.getRow()));
                        }
                    }
                });
    }

    @Override
    public void onUnregister() {
        super.onUnregister();

        expanderClickHandlerRegistration.removeHandler();
    }

    private NavigationExtensionConnector getNavigationExtensionConnector() {
        for (ServerConnector c : getChildren()) {
            if (c instanceof NavigationExtensionConnector) {
                return (NavigationExtensionConnector) c;
            }
        }
        return null;
    }
}
