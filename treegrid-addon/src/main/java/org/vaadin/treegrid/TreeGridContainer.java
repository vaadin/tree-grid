package org.vaadin.treegrid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.data.Collapsible;
import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;

public class TreeGridContainer extends HierarchicalContainer implements
        Collapsible {

    private Map<Object, HierarchyData> hierarchyData = new HashMap<>();

    public void addChildren(List<Object> items, Object parentId) {
        for (Object item : items) {
            addItem(item);
            setParent(item, parentId);
        }
    }

    @Override
    public Item addItem(Object itemId) {
        Item item = super.addItem(itemId);

        hierarchyData.put(itemId, new HierarchyData());

        return item;
    }

    private Map<Object, Object> parents = new HashMap<>();

    @Override
    public boolean setParent(Object itemId, Object newParentId) {
        if (super.setParent(itemId, newParentId)) {
            // parent index
            hierarchyData.get(itemId).setParentIndex(indexOfId(newParentId));

            // leaf
            hierarchyData.get(newParentId).setLeaf(false);

            // depth
            hierarchyData.get(itemId).setDepth(hierarchyData.get(newParentId).getDepth() + 1);

            // set parents, todo either super's modifier to protected or add extra method there
            parents.put(itemId, newParentId);

            return true;
        }

        return false;
    }

    public Object getParent(Object itemId, boolean unfiltered) {
        return unfiltered ? this.parents.get(itemId) : super.getParent(itemId);
    }

    @Override
    protected boolean doFilterContainer(boolean hasFilters) {
        boolean itemSetChanged = super.doFilterContainer(hasFilters);

        // parent index
        for (Object itemId : getVisibleItemIds()) {
            hierarchyData.get(itemId).setParentIndex(indexOfId(getParent(itemId)));
        }

        return itemSetChanged;
    }

    // Make it visible in the package
    @Override
    protected void fireItemSetChange() {
        super.fireItemSetChange();
    }

    @Override
    public void setCollapsed(Object itemId, boolean collapsed) {
        hierarchyData.get(itemId).setExpanded(!collapsed);
    }

    @Override
    public boolean isCollapsed(Object itemId) {
        return !hierarchyData.get(itemId).isExpanded();
    }

    HierarchyData getHierarchyData(Object itemId) {
        return hierarchyData.get(itemId);
    }



//    private class HierarchyInformation {
//        private int depth;
//    }
}
