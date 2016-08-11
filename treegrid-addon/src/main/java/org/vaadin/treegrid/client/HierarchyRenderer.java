package org.vaadin.treegrid.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.renderers.ClickableRenderer;
import com.vaadin.client.widget.grid.RendererCellReference;
import com.vaadin.shared.ui.grid.GridState;
import elemental.json.JsonObject;

public class HierarchyRenderer extends ClickableRenderer<String, HTML> {

    private static final String CLASS_TREE_GRID_NODE = "v-tree-grid-node";
    private static final String CLASS_TREE_GRID_EXPAND_BUTTON = "v-tree-grid-expand-button";
    private static final String CLASS_COLLAPSED = "collapsed";
    private static final String CLASS_EXPANDED = "expanded";

    private static final String CLASS_DEPTH = "depth-";

//    private static Map<Object, HierarchyData> hierarchyData = new HashMap<>();
//    static {
//        for (int i = 0; i < 30; i++) {
//            HierarchyData d = new HierarchyData();
//            d.indentation = i % 3;
//            d.open = true;
//        }
//    }

    @Override
    public HTML createWidget() {
        HTML html = GWT.create(HTML.class);

//        Label l = GWT.create(Label.class);
//        l.addClickHandler(this);
//        html.att
        html.addClickHandler(this);
        return html;
    }



    @Override
    public void render(RendererCellReference rendererCellReference, String value, HTML widget) {

        JsonObject row = (JsonObject) rendererCellReference.getRow();

        int depth = 0;
        boolean leaf = false;
        boolean expanded = false;
        boolean visible = false;
        if (row.hasKey(GridState.JSONKEY_ROWDESCRIPTION)) {
            JsonObject rowDescription = row.getObject(GridState.JSONKEY_ROWDESCRIPTION);

            depth = (int) rowDescription.getNumber("depth");
            leaf = rowDescription.getBoolean("leaf");
            expanded = rowDescription.getBoolean("expanded");
            visible = rowDescription.getBoolean("visible");
        }

        Element widgetElement = widget.getElement();
        widgetElement.addClassName(CLASS_TREE_GRID_NODE);

        // indentation
        // TODO: 01/08/16 Is this method for removing old indentation effective?
        for (int i = 0; i < 3; i++) {
            widgetElement.removeClassName(CLASS_DEPTH + i);
        }
        widgetElement.addClassName(CLASS_DEPTH + depth);

        SpanElement treeSpacer = Document.get().createSpanElement();

//        Event.sinkEvents(treeSpacer, Event.ONCLICK);
//        Event.setEventListener(treeSpacer, new EventListener() {
//            @Override
//            public void onBrowserEvent(Event event) {
//                if (Event.ONCLICK == event.getTypeInt()) {
////                    widget.fireEvent(null);
//
//                    event.stopPropagation();
//                    event.preventDefault();
//                }
//            }
//        });
//        Event.sinkEvents(treeSpacer, Event.ONCLICK);
//        Event.setEventListener(treeSpacer, new EventListener() {
//            @Override
//            public void onBrowserEvent(Event event) {
//                if (Event.ONCLICK == event.getTypeInt()) {
//                    // TODO: 09/08/16 rpc
//
//                }
//            }
//        });

        if (!treeSpacer.hasClassName(CLASS_TREE_GRID_EXPAND_BUTTON)) {
            treeSpacer.addClassName(CLASS_TREE_GRID_EXPAND_BUTTON);
        }
        if (!leaf) {
            if (expanded) {
                treeSpacer.removeClassName(CLASS_COLLAPSED);
                treeSpacer.addClassName(CLASS_EXPANDED);
            } else {
                treeSpacer.removeClassName(CLASS_EXPANDED);
                treeSpacer.addClassName(CLASS_COLLAPSED);
            }
        } else {
            treeSpacer.removeClassName(CLASS_COLLAPSED);
            treeSpacer.removeClassName(CLASS_EXPANDED);
        }

        widget.setHTML(value);
        widgetElement.insertFirst(treeSpacer);

//        Label l = GWT.create(Label.class);
//        l.setText("C");
//        l.addClickHandler(this);
//        widgetElement.insertFirst(l.getElement());
    }

    class HierarchyPanel extends Composite {
        private HierarchyPanel() {
            super();

//            Widget treeSpacer = GWT.create(Widget.class);
//            Widget content = GWT.create(Widget.class);
//
//            add(treeSpacer);
//            add(content);
        }


    }
}
