package org.vaadin.treegrid.demo;

import org.vaadin.treegrid.container.Measurable;

import com.vaadin.data.Container;
import com.vaadin.data.util.DefaultItemSorter;

public class DemoItemSorter extends DefaultItemSorter {
    Container container;

    public DemoItemSorter() {
        super(new DefaultItemSorter.DefaultPropertyValueComparator());
    }

    @Override
    public void setSortProperties(Container.Sortable container, Object[] propertyId, boolean[] ascending) {
        super.setSortProperties(container, propertyId, ascending);
        this.container = container;
    }

    @Override
    public int compare(Object o1, Object o2) {

        // find comparable siblings
        int d1 = getDepth(o1);
        int d2 = getDepth(o2);
        while (d1 > d2) {
            o1 = getHierarchical().getParent(o1);
            d1--;
        }
        while (d2 > d1) {
            o2 = getHierarchical().getParent(o2);
            d2--;
        }
        while (getHierarchical().getParent(o1) != getHierarchical().getParent(o2)) {
            o1 = getHierarchical().getParent(o1);
            o2 = getHierarchical().getParent(o2);
        }

        return super.compare(o1, o2);
    }

    private Container.Hierarchical getHierarchical() {
        return (Container.Hierarchical) container;
    }

    private int getDepth(Object itemId) {
        int depth = 0;
        if (container instanceof Measurable) {
            depth = ((Measurable) container).getDepth(itemId);
        } else {
            Object id = itemId;
            while (!getHierarchical().isRoot(id)) {
                id = getHierarchical().getParent(id);
                depth++;
            }
        }

        return depth;
    }
}
