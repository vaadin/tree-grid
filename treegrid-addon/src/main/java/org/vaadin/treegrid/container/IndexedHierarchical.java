package org.vaadin.treegrid.container;

import com.vaadin.data.Container;

/**
 * Combined Container interface for TreeGrid data sources. The interface combines {@link
 * com.vaadin.data.Container.Indexed} (needed for Grid) and {@link com.vaadin.data.Container.Hierarchical} (needed for
 * displaying hierarchical data).
 */
public interface IndexedHierarchical extends Container.Indexed, Container.Hierarchical {
}
