package org.vaadin.treegrid.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.renderers.ClickableRenderer;
import com.vaadin.client.renderers.Renderer;
import com.vaadin.client.renderers.WidgetRenderer;
import com.vaadin.client.widget.grid.RendererCellReference;
import com.vaadin.shared.ui.grid.GridState;

import elemental.json.JsonObject;

public class HierarchyRenderer extends ClickableRenderer<Object, Widget> {

    private static final String CLASS_TREE_GRID_NODE = "v-tree-grid-node";
    private static final String CLASS_TREE_GRID_CELL_CONTENT = "v-tree-grid-cell-content";
    private static final String CLASS_TREE_GRID_EXPANDER = "v-tree-grid-expander";
    private static final String CLASS_COLLAPSED = "collapsed";
    private static final String CLASS_EXPANDED = "expanded";
    private static final String CLASS_DEPTH = "depth-";

    private Renderer innerRenderer;

    @Override
    public Widget createWidget() {
        return new HierarchyItem(CLASS_TREE_GRID_NODE);
    }

    @Override
    public void render(RendererCellReference cell, Object data, Widget widget) {

        JsonObject row = (JsonObject) cell.getRow();

        int depth = 0;
        boolean leaf = false;
        boolean collapsed = false;
        if (row.hasKey(GridState.JSONKEY_ROWDESCRIPTION)) {
            JsonObject rowDescription = row.getObject(GridState.JSONKEY_ROWDESCRIPTION);

            depth = (int) rowDescription.getNumber("depth");
            leaf = rowDescription.getBoolean("leaf");
            collapsed = rowDescription.getBoolean("collapsed");
        }

        HierarchyItem cellWidget = (HierarchyItem) widget;
        cellWidget.setDepth(depth);

        if (leaf) {
            cellWidget.setExpanderState(ExpanderState.LEAF);
        } else if (collapsed) {
            cellWidget.setExpanderState(ExpanderState.COLLAPSED);
        } else {
            cellWidget.setExpanderState(ExpanderState.EXPANDED);
        }

        // inner cell
        if (innerRenderer instanceof WidgetRenderer) {
            ((WidgetRenderer) innerRenderer).render(cell, data, ((HierarchyItem) widget).content);
        } else {
            innerRenderer.render(new HierarchyRendererCellReferenceWrapper(cell,
                    ((HierarchyItem) widget).content.getElement()), data);
        }
    }

    void setInnerRenderer(Renderer innerRenderer) {
        this.innerRenderer = innerRenderer;
    }

    private class HierarchyItem extends Composite {

        private FlowPanel panel;
        private Expander expander;
        private Widget content;

        private HierarchyItem(String className) {
            panel = new FlowPanel();
            panel.getElement().addClassName(className);

            expander = new Expander();
            expander.getElement().addClassName(CLASS_TREE_GRID_EXPANDER);

            if (innerRenderer instanceof WidgetRenderer) {
                content = ((WidgetRenderer) innerRenderer).createWidget();
            } else {
                // TODO: 20/09/16 create more general widget?
                content = new HTML();
            }

            content.getElement().addClassName(CLASS_TREE_GRID_CELL_CONTENT);

            panel.add(expander);
            panel.add(content);

            expander.addClickHandler(HierarchyRenderer.this);

            initWidget(panel);
        }

        private void setDepth(int depth) {
            String classNameToBeReplaced = getFullClassName(CLASS_DEPTH, panel.getElement().getClassName());
            if (classNameToBeReplaced == null) {
                panel.getElement().addClassName(CLASS_DEPTH + depth);
            } else {
                panel.getElement().replaceClassName(classNameToBeReplaced, CLASS_DEPTH + depth);
            }
        }

        private String getFullClassName(String prefix, String classNameList) {
            int start = classNameList.indexOf(prefix);
            int end = start + prefix.length();
            if (start > -1) {
                while (end < classNameList.length() && classNameList.charAt(end) != ' ') {
                    end++;
                }
                return classNameList.substring(start, end);
            }
            return null;
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

        private class Expander extends Widget implements HasClickHandlers {

            private Expander() {
                Element span = DOM.createSpan();
                setElement(span);
            }

            @Override
            public HandlerRegistration addClickHandler(ClickHandler handler) {
                return addDomHandler(handler, ClickEvent.getType());
            }
        }
    }

    enum ExpanderState {
        EXPANDED, COLLAPSED, LEAF;
    }
}
