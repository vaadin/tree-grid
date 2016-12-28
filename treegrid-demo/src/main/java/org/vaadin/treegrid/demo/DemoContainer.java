package org.vaadin.treegrid.demo;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.vaadin.treegrid.container.Measurable;

import com.vaadin.data.Collapsible;
import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.server.Resource;

public class DemoContainer extends HierarchicalContainer implements Collapsible, Measurable {

    static final String PROPERTY_NAME = "Name";
    static final String PROPERTY_HOURS = "Hours done";
    static final String PROPERTY_MODIFIED = "Last modified";
    static final String PROPERTY_ICON = "Icon";

    public DemoContainer() {
        addContainerProperty(PROPERTY_NAME, String.class, "");
        addContainerProperty(PROPERTY_HOURS, Integer.class, 0);
        addContainerProperty(PROPERTY_MODIFIED, Date.class, new Date());
        addContainerProperty(PROPERTY_ICON, Resource.class, null);

        for (Object[] r : DataSource.getRoot()) {
            addItem(r);
        }

        setItemSorter(new DemoItemSorter());
    }

    private Object addItem(Object[] values) {
        Item item = addItem((Object) values);
        setProperties(item, values);
        return values;
    }

    private Object addChild(Object[] values, Object parentId) {
        Item item = addItemAfter(parentId, values);
        setProperties(item, values);
        setParent(values, parentId);
        return values;
    }

    private void setProperties(Item item, Object[] values) {
        item.getItemProperty(PROPERTY_NAME).setValue(values[0]);
        item.getItemProperty(PROPERTY_HOURS).setValue(values[1]);
        item.getItemProperty(PROPERTY_MODIFIED).setValue(values[2]);
        item.getItemProperty(PROPERTY_ICON).setValue(values[3]);
    }

    @Override
    public void setCollapsed(Object itemId, boolean collapsed) {
        expandedNodes.put(itemId, !collapsed);

        if (collapsed) {
            // remove children
            removeChildrenRecursively(itemId);
        } else {
            // lazy load children
            addChildren(itemId);
        }
    }

    private void addChildren(Object itemId) {
        for (Object[] child : DataSource.getChildren(itemId)) {
            Object childId = addChild(child, itemId);
            if (Boolean.TRUE.equals(expandedNodes.get(childId))) {
                addChildren(childId);
            }
        }
    }

    private boolean removeChildrenRecursively(Object itemId) {
        boolean success = true;
        Collection<?> children2 = getChildren(itemId);
        if (children2 != null) {
            Object[] array = children2.toArray();
            for (int i = 0; i < array.length; i++) {
                boolean removeItemRecursively = removeItemRecursively(
                        this, array[i]);
                if (!removeItemRecursively) {
                    success = false;
                }
            }
        }
        return success;

    }

    @Override
    public boolean hasChildren(Object itemId) {
        return !DataSource.isLeaf(itemId);
    }

    private Map<Object, Boolean> expandedNodes = new HashMap<>();

    @Override
    public boolean isCollapsed(Object itemId) {
        return !Boolean.TRUE.equals(expandedNodes.get(itemId));
    }

    @Override
    public int getDepth(Object itemId) {
        int depth = 0;
        while (!isRoot(itemId)) {
            depth ++;
            itemId = getParent(itemId);
        }
        return depth;
    }
}
