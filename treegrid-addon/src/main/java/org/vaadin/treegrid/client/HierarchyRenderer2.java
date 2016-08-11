package org.vaadin.treegrid.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.renderers.WidgetRenderer;
import com.vaadin.client.widget.grid.RendererCellReference;
import com.vaadin.shared.ui.grid.GridState;

import elemental.json.JsonObject;

public class HierarchyRenderer2 extends WidgetRenderer<String, Widget> {

    private static final String CLASS_TREE_GRID_NODE = "v-tree-grid-node";
    private static final String CLASS_TREE_GRID_CELL_CONTENT = "v-tree-grid-cell-content";
    private static final String CLASS_TREE_GRID_EXPAND_BUTTON = "v-tree-grid-expand-button";
    private static final String CLASS_COLLAPSED = "collapsed";
    private static final String CLASS_EXPANDED = "expanded";
    private static final String CLASS_DEPTH = "depth-";

    @Override
    public Widget createWidget() {
//        return new HierarchyItem();
//        LayoutPanel panel = new LayoutPanel();

//        LayoutPanel panel = GWT.create(LayoutPanel.class);
//        panel.add(new SimplePanel());
//        panel.add(new Label("test"));
//        return panel;
//        return new HierarchyItem2();
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

//    public static class HierarchyItem2 extends ComplexPanel {
//
//        Element e1;
//        Element e2;
//
//        public HierarchyItem2() {
////            Element wrapper = Document.get().createDivElement();
////            setElement(wrapper);
//
////            wrapper.appendChild(new Label("test"));
//
//
//            setElement(Document.get().createDivElement());
//
//            e1 = Document.get().createSpanElement();
//            e2 = Document.get().createDivElement();
//
//            getElement().appendChild(e1);
//            getElement().appendChild(e2);
////            getElement().appendChild(Document.get().createSpanElement());
////            getElement().appendChild(Document.get().createDivElement());
//
//            e1.addClassName("v-tree-grid-expand-button");
//            e1.addClassName("expanded");
//
//            e2.getStyle().setDisplay(Style.Display.INLINE_BLOCK);
//
////            DOM.sinkBitlessEvent(e1, Event.CLICK);
//            Event.sinkEvents(e1, Event.ONCLICK);
//            Event.setEventListener(e1, new EventListener() {
//                @Override
//                public void onBrowserEvent(Event event) {
//                    if (Event.ONCLICK == event.getTypeInt()) {
//    //                    widget.fireEvent(null);
//                        Window.alert("click");
//
//                        event.stopPropagation();
//                        event.preventDefault();
//                    }
//                }
//            });
//        }
//
//        public void setText(String text) {
//            e2.setInnerText(text);
//        }
//    }

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
            for (int i = 0; i < 3; i++) {
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

//                sinkEvents(Event.ONCLICK);
//                addDomHandler(new ClickHandler() {
//                    @Override
//                    public void onClick(ClickEvent event) {
//
//                    }
//                }, ClickEvent.getType());

                addDomHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        // TODO: 11/08/16 rpc call

                        event.stopPropagation();
                        event.preventDefault();
                    }
                }, ClickEvent.getType());

            }
        }
    }

    enum ExpanderState {
        EXPANDED, COLLAPSED, LEAF;
    }
}
