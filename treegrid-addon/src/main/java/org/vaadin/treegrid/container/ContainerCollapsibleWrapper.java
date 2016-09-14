package org.vaadin.treegrid.container;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.vaadin.data.Collapsible;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractContainer;

public class ContainerCollapsibleWrapper extends AbstractContainer implements Container.Indexed, Collapsible,
        Container.ItemSetChangeNotifier, Container.ItemSetChangeListener {

    private Indexed container;

    private Set<Object> expandedItemIds = new HashSet<>();
    private List<Object> visibleItemIds = new LinkedList<>();

    public ContainerCollapsibleWrapper(Indexed container) {
        if (!(container instanceof Hierarchical)) {
            throw new IllegalArgumentException("Container must implement the Hierarchical interface");
        }

        this.container = container;

        // Root items are visible
        visibleItemIds.addAll(((Hierarchical) container).rootItemIds());

        // Listen for item set changes
        if (container instanceof ItemSetChangeNotifier) {
            ((ItemSetChangeNotifier) container).addItemSetChangeListener(this);
        }
    }

    // Container

    @Override
    public Item getItem(Object itemId) {
        return container.getItem(itemId);
    }

    @Override
    public Collection<?> getContainerPropertyIds() {
        return container.getContainerPropertyIds();
    }

    @Override
    public Collection<?> getItemIds() {
        return Collections.unmodifiableList(visibleItemIds);
    }

    @Override
    public Property getContainerProperty(Object itemId, Object propertyId) {
        return container.getContainerProperty(itemId, propertyId);
    }

    @Override
    public Class<?> getType(Object propertyId) {
        return container.getType(propertyId);
    }

    @Override
    public int size() {
        return visibleItemIds.size();
    }

    @Override
    public boolean containsId(Object itemId) {
        return visibleItemIds.contains(itemId);
    }

    @Override
    public Item addItem(Object itemId) throws UnsupportedOperationException {
        // In addition, visibleItemIds rebuilt while handling ItemSetChangeEvent
        return container.addItem(itemId);
    }

    @Override
    public Object addItem() throws UnsupportedOperationException {
        // In addition, visibleItemIds rebuilt while handling ItemSetChangeEvent
        return container.addItem();
    }

    @Override
    public boolean removeItem(Object itemId) throws UnsupportedOperationException {
        // In addition, visibleItemIds rebuilt while handling ItemSetChangeEvent
        return container.removeItem(itemId);
    }

    @Override
    public boolean addContainerProperty(Object propertyId, Class<?> type, Object defaultValue) throws
            UnsupportedOperationException {
        return container.addContainerProperty(propertyId, type, defaultValue);
    }

    @Override
    public boolean removeContainerProperty(Object propertyId) throws UnsupportedOperationException {
        return container.removeContainerProperty(propertyId);
    }

    @Override
    public boolean removeAllItems() throws UnsupportedOperationException {
        // In addition, visibleItemIds cleared while handling ItemSetChangeEvent
        return container.removeAllItems();
    }

    // Ordered

    @Override
    public Object nextItemId(Object itemId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object prevItemId(Object itemId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object firstItemId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object lastItemId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isFirstId(Object itemId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isLastId(Object itemId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object addItemAfter(Object previousItemId) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Item addItemAfter(Object previousItemId, Object newItemId) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    // Indexed

    @Override
    public int indexOfId(Object itemId) {
        return visibleItemIds.indexOf(itemId);
    }

    @Override
    public Object getIdByIndex(int index) {
        return visibleItemIds.get(index);
    }

    @Override
    public List<?> getItemIds(int startIndex, int numberOfItems) {
        if (startIndex < 0) {
            throw new IndexOutOfBoundsException("Start index cannot be negative! startIndex=" + startIndex);
        }

        if (startIndex > visibleItemIds.size()) {
            throw new IndexOutOfBoundsException(
                    "Start index exceeds container size! startIndex=" + startIndex + " containerLastItemIndex=" + (
                            visibleItemIds.size() - 1));
        }

        if (numberOfItems < 1) {
            if (numberOfItems == 0) {
                return Collections.emptyList();
            }

            throw new IllegalArgumentException("Cannot get negative amount of items! numberOfItems=" + numberOfItems);
        }

        int endIndex = startIndex + numberOfItems;

        if (endIndex > visibleItemIds.size()) {
            endIndex = visibleItemIds.size();
        }

        return Collections.unmodifiableList(visibleItemIds.subList(startIndex, endIndex));
    }

    @Override
    public Object addItemAt(int index) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Item addItemAt(int index, Object newItemId) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    // Hierarchical

    @Override
    public Collection<?> getChildren(Object itemId) {
        return ((Hierarchical) container).getChildren(itemId);
    }

    @Override
    public Object getParent(Object itemId) {
        return ((Hierarchical) container).getParent(itemId);
    }

    @Override
    public Collection<?> rootItemIds() {
        return ((Hierarchical) container).rootItemIds();
    }

    @Override
    public boolean setParent(Object itemId, Object newParentId) throws UnsupportedOperationException {
        return ((Hierarchical) container).setParent(itemId, newParentId);
    }

    @Override
    public boolean areChildrenAllowed(Object itemId) {
        return ((Hierarchical) container).areChildrenAllowed(itemId);
    }

    @Override
    public boolean setChildrenAllowed(Object itemId, boolean areChildrenAllowed) throws UnsupportedOperationException {
        return ((Hierarchical) container).setChildrenAllowed(itemId, areChildrenAllowed);
    }

    @Override
    public boolean isRoot(Object itemId) {
        return ((Hierarchical) container).isRoot(itemId);
    }

    @Override
    public boolean hasChildren(Object itemId) {
        return ((Hierarchical) container).hasChildren(itemId);
    }

    // Collapsible

    @Override
    public void setCollapsed(Object itemId, boolean collapsed) {
        if (collapsed) {
            expandedItemIds.remove(itemId);
            hideDescendants(itemId);
        } else {
            expandedItemIds.add(itemId);
            showDescendants(itemId);
        }

        fireItemSetChange();
    }

    @Override
    public boolean isCollapsed(Object itemId) {
        return !expandedItemIds.contains(itemId);
    }

    private void showDescendants(Object parentId) {
        insertChildrenIfParentExpandedRecursively(indexOfId(parentId) + 1, parentId);
    }

    private int insertChildrenIfParentExpandedRecursively(int index, Object parentId) {
        if (!isCollapsed(parentId)) {
            for (Object childId : ensureNotNull(((Hierarchical) container).getChildren(parentId))) {
                visibleItemIds.add(index++, childId);
                index = insertChildrenIfParentExpandedRecursively(index, childId);
            }
        }
        return index;
    }

    private void hideDescendants(Object parentId) {
        // TODO: 22/09/16 consider removing between this node and next sibling
        removeChildrenRecursively(parentId);
    }

    private void removeChildrenRecursively(Object parentId) {
        for (Object childId : ensureNotNull(((Hierarchical) container).getChildren(parentId))) {
            boolean wasVisible = visibleItemIds.remove(childId);
            if (wasVisible) {
                removeChildrenRecursively(childId);
            }
        }
    }

    private Collection<?> ensureNotNull(Collection<?> collection) {
        return collection == null ? Collections.emptyList() : collection;
    }

    // ItemSetChangeNotifier

    @Override
    public void addItemSetChangeListener(ItemSetChangeListener listener) {
        super.addItemSetChangeListener(listener);
    }

    @Override
    @Deprecated
    public void addListener(ItemSetChangeListener listener) {
        this.addItemSetChangeListener(listener);
    }

    @Override
    public void removeItemSetChangeListener(ItemSetChangeListener listener) {
        super.removeItemSetChangeListener(listener);
    }

    @Override
    @Deprecated
    public void removeListener(ItemSetChangeListener listener) {
        this.removeItemSetChangeListener(listener);
    }

    // ItemSetChangeListener

    @Override
    public void containerItemSetChange(ItemSetChangeEvent event) {
        if (!container.equals(event.getContainer())) {
            return;
        }
        // Rebuild visible items list
        visibleItemIds.clear();
        for (Object rootId : ensureNotNull(((Hierarchical) container).rootItemIds())) {
            visibleItemIds.add(rootId);
            showDescendants(rootId);
        }

        // Forward the event
        fireItemSetChange(event);
    }
}
