package org.vaadin.treegrid.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.TableCellElement;
import com.vaadin.client.widget.escalator.FlyweightCell;
import com.vaadin.client.widget.grid.RendererCellReference;
import com.vaadin.client.widget.grid.RowReference;

class HierarchyRendererCellReferenceWrapper extends RendererCellReference {

    private Element element;

    public HierarchyRendererCellReferenceWrapper(RendererCellReference cell, Element element) {
        super(getRowReference(cell));
        set(getFlyweightCell(cell), cell.getColumnIndex(), cell.getColumn());

        this.element = element;
    }

    @Override
    public TableCellElement getElement() {
        return (TableCellElement) element;
    }

    private native static RowReference<Object> getRowReference(RendererCellReference cell)
        /*-{
            return cell.@com.vaadin.client.widget.grid.CellReference::getRowReference();
        }-*/;

    private native static FlyweightCell getFlyweightCell(RendererCellReference cell)
        /*-{
            return cell.@com.vaadin.client.widget.grid.RendererCellReference::cell;
        }-*/;
}