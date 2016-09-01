package org.vaadin.treegrid;

import com.vaadin.data.Container;

// TODO: 31/08/16 Should this interface exist?
public interface TreeGridContainer extends Container.Indexed,
        Container.Hierarchical {

    public int getDepth(Object itemId);
}
