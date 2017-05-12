package org.vaadin.treegrid.client;

import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.vaadin.client.WidgetUtil;
import com.vaadin.client.widget.escalator.RowContainer;
import com.vaadin.client.widget.grid.events.AbstractGridMouseEventHandler;
import com.vaadin.client.widget.grid.events.GridClickEvent;
import com.vaadin.client.widgets.Grid;
import com.vaadin.shared.ui.grid.GridConstants;

/**
 * {@inheritDoc}
 * <p>
 * Differs from {@link GridClickEvent} only in allowing events to originate form hierarchy widget.
 */
class TreeGridClickEvent extends GridClickEvent {

    public static final Type<AbstractGridMouseEventHandler.GridClickHandler> TYPE = new Type<>(
            BrowserEvents.CLICK, new TreeGridClickEvent());

    public TreeGridClickEvent() {
    }

    @Override
    public TreeGrid getGrid() {
        // Copied from AbstractGridMouseEvent.getGrid() and changed to find TreeGrid class
        EventTarget target = getNativeEvent().getEventTarget();
        if (!Element.is(target)) {
            return null;
        }
        return WidgetUtil.findWidget(Element.as(target), TreeGrid.class);
    }

    @Override
    public Type<AbstractGridMouseEventHandler.GridClickHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AbstractGridMouseEventHandler.GridClickHandler handler) {
        EventTarget target = getNativeEvent().getEventTarget();
        if (!Element.is(target)) {
            // Target is not an element
            return;
        }

        Grid<?> grid = getGrid();
        if (grid == null) {
            // Target is not an element of a grid
            return;
        }

        // Ignore event if originated from child widget
        // except when from hierarchy widget
        Element targetElement = Element.as(target);
        if (getGrid().isElementInChildWidget(targetElement) &&
                !HierarchyRenderer.isElementInHierarchyWidget(targetElement)) {
            return;
        }

        final RowContainer container = getGrid().getEscalator().findRowContainer(targetElement);
        if (container == null) {
            // No container for given element
            return;
        }

        GridConstants.Section section = GridConstants.Section.FOOTER;
        if (container == getGrid().getEscalator().getHeader()) {
            section = GridConstants.Section.HEADER;
        } else if (container == getGrid().getEscalator().getBody()) {
            section = GridConstants.Section.BODY;
        }

        doDispatch(handler, section);
    }
}
