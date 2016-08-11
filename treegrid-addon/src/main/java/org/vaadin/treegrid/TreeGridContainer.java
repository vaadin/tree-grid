package org.vaadin.treegrid;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractContainer;

import java.util.Collection;

/**
 * Created by adam on 29/07/16.
 */
@Deprecated
public class TreeGridContainer extends AbstractContainer implements Container.Sortable {
    @Override
    public void sort(Object[] propertyId, boolean[] ascending) {

    }

    @Override
    public Collection<?> getSortableContainerPropertyIds() {
        return null;
    }

    @Override
    public Object nextItemId(Object itemId) {
        return null;
    }

    @Override
    public Object prevItemId(Object itemId) {
        return null;
    }

    @Override
    public Object firstItemId() {
        return null;
    }

    @Override
    public Object lastItemId() {
        return null;
    }

    @Override
    public boolean isFirstId(Object itemId) {
        return false;
    }

    @Override
    public boolean isLastId(Object itemId) {
        return false;
    }

    @Override
    public Object addItemAfter(Object previousItemId) throws UnsupportedOperationException {
        return null;
    }

    @Override
    public Item addItemAfter(Object previousItemId, Object newItemId) throws UnsupportedOperationException {
        return null;
    }

    @Override
    public Item getItem(Object itemId) {
        return null;
    }

    @Override
    public Collection<?> getContainerPropertyIds() {
        return null;
    }

    @Override
    public Collection<?> getItemIds() {
        return null;
    }

    @Override
    public Property getContainerProperty(Object itemId, Object propertyId) {
        return null;
    }

    @Override
    public Class<?> getType(Object propertyId) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean containsId(Object itemId) {
        return false;
    }

    @Override
    public Item addItem(Object itemId) throws UnsupportedOperationException {
        return null;
    }

    @Override
    public Object addItem() throws UnsupportedOperationException {
        return null;
    }

    @Override
    public boolean removeItem(Object itemId) throws UnsupportedOperationException {
        return false;
    }

    @Override
    public boolean addContainerProperty(Object propertyId, Class<?> type, Object defaultValue) throws UnsupportedOperationException {
        return false;
    }

    @Override
    public boolean removeContainerProperty(Object propertyId) throws UnsupportedOperationException {
        return false;
    }

    @Override
    public boolean removeAllItems() throws UnsupportedOperationException {
        return false;
    }
}
