package org.vaadin.treegrid.container;

import java.util.Collection;

import com.vaadin.data.Container;
import com.vaadin.data.util.GeneratedPropertyContainer;

public class GeneratedPropertyContainerHierarchicalWrapper<T extends Container.Indexed & Container.Hierarchical> extends
        GeneratedPropertyContainer implements Container.Hierarchical {

    public GeneratedPropertyContainerHierarchicalWrapper(T container) {
        super(container);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getWrappedContainer() {
        return (T) super.getWrappedContainer();
    }

    @Override
    public Collection<?> getChildren(Object itemId) {
        return getWrappedContainer().getChildren(itemId);
    }

    @Override
    public Object getParent(Object itemId) {
        return getWrappedContainer().getParent(itemId);
    }

    @Override
    public Collection<?> rootItemIds() {
        return getWrappedContainer().rootItemIds();
    }

    @Override
    public boolean setParent(Object itemId, Object newParentId) throws UnsupportedOperationException {
        return getWrappedContainer().setParent(itemId, newParentId);
    }

    @Override
    public boolean areChildrenAllowed(Object itemId) {
        return getWrappedContainer().areChildrenAllowed(itemId);
    }

    @Override
    public boolean setChildrenAllowed(Object itemId, boolean areChildrenAllowed) throws UnsupportedOperationException {
        return getWrappedContainer().setChildrenAllowed(itemId, areChildrenAllowed);
    }

    @Override
    public boolean isRoot(Object itemId) {
        return getWrappedContainer().isRoot(itemId);
    }

    @Override
    public boolean hasChildren(Object itemId) {
        return getWrappedContainer().hasChildren(itemId);
    }
}
