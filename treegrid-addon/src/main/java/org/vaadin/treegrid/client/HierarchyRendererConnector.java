package org.vaadin.treegrid.client;

import com.google.web.bindery.event.shared.HandlerRegistration;
import com.vaadin.client.connectors.ClickableRendererConnector;
import com.vaadin.client.renderers.ClickableRenderer;
import com.vaadin.shared.ui.Connect;

import elemental.json.JsonObject;

@Connect(org.vaadin.treegrid.HierarchyRenderer.class)
public class HierarchyRendererConnector extends
        ClickableRendererConnector<String> {

    @Override
    protected HandlerRegistration addClickHandler(
            ClickableRenderer.RendererClickHandler<JsonObject> rendererClickHandler) {
        return getRenderer().addClickHandler(rendererClickHandler);
    }

    @Override
    public HierarchyRenderer getRenderer() {
        return (HierarchyRenderer) super.getRenderer();
    }
}
