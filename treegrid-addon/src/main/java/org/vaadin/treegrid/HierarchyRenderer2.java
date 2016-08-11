package org.vaadin.treegrid;

import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.Renderer;

public class HierarchyRenderer2 extends Grid.AbstractRenderer<String> {
    public HierarchyRenderer2(Class presentationType) {
        super(presentationType);
    }

    private Renderer renderer;

    public HierarchyRenderer2(Renderer renderer) {
        super(renderer.getPresentationType());


    }
}
