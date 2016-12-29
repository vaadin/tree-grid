package org.vaadin.treegrid.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.vaadin.client.widget.escalator.RowContainer;
import com.vaadin.client.widget.grid.CellReference;
import com.vaadin.client.widget.grid.events.AbstractGridMouseEventHandler;
import com.vaadin.client.widget.grid.events.GridClickEvent;
import com.vaadin.shared.ui.grid.GridConstants;

/**
 * Class to set as value of {@link com.vaadin.client.widgets.Grid#clickEvent}. <br/>
 * Differs from {@link GridClickEvent} only in allowing events to originate form hierarchy widget.
 *
 */
class TreeGridClickEvent extends GridClickEvent {

    TreeGridClickEvent(TreeGrid grid,
            CellReference<?> targetCell) {
        super(grid, targetCell);
    }

    @Override
    public TreeGrid getGrid() {
        return (TreeGrid) super.getGrid();
    }

    @Override
    protected void dispatch(AbstractGridMouseEventHandler.GridClickHandler handler) {
        EventTarget target = getNativeEvent().getEventTarget();
        if (!Element.is(target)) {
            // Target is not an element
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
