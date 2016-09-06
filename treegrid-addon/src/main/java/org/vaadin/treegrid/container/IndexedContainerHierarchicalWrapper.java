package org.vaadin.treegrid.container;

import java.util.List;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.ContainerHierarchicalWrapper;

public class IndexedContainerHierarchicalWrapper extends
        ContainerHierarchicalWrapper implements Container.Indexed {

    private Container.Indexed container;
    
    public IndexedContainerHierarchicalWrapper(
            Container.Indexed toBeWrapped) {
        super(toBeWrapped);

        container = toBeWrapped;
    }

    @Override
    public int indexOfId(Object itemId) {
        return container.indexOfId(itemId);
    }

    @Override
    public Object getIdByIndex(int index) {
        return container.getIdByIndex(index);
    }

    @Override
    public List<?> getItemIds(int startIndex, int numberOfItems) {
        return container.getItemIds(startIndex, numberOfItems);
    }

    @Override
    public Object addItemAt(int index) throws UnsupportedOperationException {
        return container.addItemAt(index);
    }

    @Override
    public Item addItemAt(int index, Object newItemId) throws
            UnsupportedOperationException {
        return container.addItemAt(index, newItemId);
    }

    @Override
    public Object nextItemId(Object itemId) {
        return container.nextItemId(itemId);
    }

    @Override
    public Object prevItemId(Object itemId) {
        return container.prevItemId(itemId);
    }

    @Override
    public Object firstItemId() {
        return container.firstItemId();
    }

    @Override
    public Object lastItemId() {
        return container.lastItemId();
    }

    @Override
    public boolean isFirstId(Object itemId) {
        return container.isFirstId(itemId);
    }

    @Override
    public boolean isLastId(Object itemId) {
        return container.isLastId(itemId);
    }

    @Override
    public Object addItemAfter(Object previousItemId) throws
            UnsupportedOperationException {
        return container.addItemAfter(previousItemId);
    }

    @Override
    public Item addItemAfter(Object previousItemId, Object newItemId) throws
            UnsupportedOperationException {
        return container.addItemAfter(previousItemId, newItemId);
    }
}
