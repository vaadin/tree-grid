package org.vaadin.treegrid.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.renderers.ClickableRenderer;
import com.vaadin.client.widget.grid.RendererCellReference;
import com.vaadin.shared.ui.grid.GridState;
import elemental.json.JsonObject;

public class HierarchyRenderer extends ClickableRenderer<String, Widget> {

    private static final String CLASS_TREE_GRID_NODE = "v-tree-grid-node";
    private static final String CLASS_TREE_GRID_CELL_CONTENT = "v-tree-grid-cell-content";
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
    public Widget createWidget() {
//        HTML html = GWT.create(HTML.class);
//        html.addClickHandler(this);
//        return html;

        return new HierarchyItem(CLASS_TREE_GRID_NODE);
    }



    @Override
    public void render(RendererCellReference cell, String data,
            Widget widget) {
//        ((HierarchyItem2) widget).setText(data);

        JsonObject row = (JsonObject) cell.getRow();

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



        HierarchyItem cellWidget = (HierarchyItem) widget;
        cellWidget.setText(data);
        cellWidget.setDepth(depth);

        if (leaf) {
            cellWidget.setExpanderState(ExpanderState.LEAF);
        } else if (expanded) {
            cellWidget.setExpanderState(ExpanderState.EXPANDED);
        } else {
            cellWidget.setExpanderState(ExpanderState.COLLAPSED);
        }
    }

    private class HierarchyItem extends Composite {

        private FlowPanel panel;
        private Expander expander;
        private HTML content;

        private HierarchyItem(String className) {
            panel = new FlowPanel();
            panel.getElement().addClassName(className);

            expander = new Expander();
            expander.getElement().addClassName(CLASS_TREE_GRID_EXPAND_BUTTON);

            content = GWT.create(HTML.class);
            content.getElement().addClassName(CLASS_TREE_GRID_CELL_CONTENT);

            panel.add(expander);
            panel.add(content);

            initWidget(panel);
        }

        private void setText(String text) {
            this.content.setText(text);
        }

        private void setDepth(int depth) {
            // indentation
            // TODO: 01/08/16 Is this method for removing old indentation effective?
            for (int i = 0; i < 4; i++) {
                panel.getElement().removeClassName(CLASS_DEPTH + i);
            }
            panel.getElement().addClassName(CLASS_DEPTH + depth);
        }

        private void setExpanderState(ExpanderState state) {
            switch (state) {
            case EXPANDED:
                expander.getElement().removeClassName(CLASS_COLLAPSED);
                expander.getElement().addClassName(CLASS_EXPANDED);
                break;
            case COLLAPSED:
                expander.getElement().removeClassName(CLASS_EXPANDED);
                expander.getElement().addClassName(CLASS_COLLAPSED);
                break;
            case LEAF:
            default:
                expander.getElement().removeClassName(CLASS_COLLAPSED);
                expander.getElement().removeClassName(CLASS_EXPANDED);
            }
        }

        private class Expander extends Widget {

//            private enum State {
//                EXPANDED, COLLAPSED, LEAF;
//            }

            private Expander() {
                Element span = DOM.createSpan();
                setElement(span);

                addDomHandler(HierarchyRenderer.this, ClickEvent.getType());
            }
        }
    }

    enum ExpanderState {
        EXPANDED, COLLAPSED, LEAF;
    }
}
