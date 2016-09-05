package org.vaadin.treegrid.container;

/**
 * Container interface that that gives the Container the ability to calculate the item's depth in the hierarchy.
 */
public interface Measurable {

    /**
     * Returns the given item's depth in the hierarchy.
     *
     * @param itemId
     *         ID of the item whose depth is to be measured
     * @return the depth of the item i.e. number of recursive parents
     */
    public int getDepth(Object itemId);
}
