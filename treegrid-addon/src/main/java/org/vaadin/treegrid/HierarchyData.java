package org.vaadin.treegrid;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by adam on 28/07/16.
 */
public class HierarchyData {
    private int depth;
    private boolean expanded;
    private boolean leaf = true;
    private boolean visible;
    private int parentIndex;

    // TODO: 28/07/16 reference to children
    private Set<HierarchyData> children = new HashSet<>();

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public Set<HierarchyData> getChildren() {
        return children;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public int getParentIndex() {
        return parentIndex;
    }

    public void setParentIndex(int parentIndex) {
        this.parentIndex = parentIndex;
    }
}
