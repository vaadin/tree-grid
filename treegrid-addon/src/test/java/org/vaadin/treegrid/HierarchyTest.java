package org.vaadin.treegrid;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.data.util.IndexedContainer;

import junit.framework.Assert;

@RunWith(Parameterized.class)
public class HierarchyTest {

    private static final String PROPERTY_NAME = "name";

    private TreeGrid grid;
    private Container.Indexed container;

    public HierarchyTest(Container.Indexed container) {
        this.container = container;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{{new HierarchicalContainer()}, {new IndexedContainer()}});
    }

    @Before
    public void createContainer() {
        grid = new TreeGrid();
        grid.setContainerDataSource(container);

        populateContainer((Container.Hierarchical) grid.getContainerDataSource());
    }

    @Test
    public void testHierarchy() {
        Container.Hierarchical container = (Container.Hierarchical) grid.getContainerDataSource();
        Collection roots = container.rootItemIds();

        Assert.assertEquals("There should be one root item", 1, roots.size());

        Iterator it = roots.iterator();
        Object root = it.next();

        Collection children = container.getChildren(root);

        Assert.assertEquals("There should be three children", 3, children.size());

        it = children.iterator();
        Object item010 = it.next();

        Assert.assertEquals("Item's value should be \"010\"", "010",
                container.getItem(item010).getItemProperty(PROPERTY_NAME).getValue());

        Collection grandchildren = container.getChildren(item010);

        Assert.assertEquals("There should be three children of \"010\"", 3, grandchildren.size());

        it = grandchildren.iterator();
        it.next();
        Object item012 = it.next();

        Assert.assertEquals("Item's value should be \"012\"", "012",
                container.getItem(item012).getItemProperty(PROPERTY_NAME).getValue());
    }

    /*

    -- 000
        |
         -- 010
             |
              -- 011
             |
              -- 012
             |
              -- 013
        |
         -- 020
             |
              -- 021
             |
              -- 022
        |
         -- 030

    */

    private void populateContainer(Container.Hierarchical container) {

        container.addContainerProperty(PROPERTY_NAME, String.class, "");

        Object root = "000";
        addItem(root, container);

        // add children
        Object[] children = {"010", "020", "030"};
        addChildren(children, root, container);

        // add grandchildren
        addChildren(new Object[]{"011", "012", "013"}, children[0], container);
        addChildren(new Object[]{"021", "022"}, children[1], container);
    }

    private Item addItem(Object itemId, Container container) {
        Item item = container.addItem(itemId);
        item.getItemProperty(PROPERTY_NAME).setValue(itemId);
        return item;
    }

    private void addChildren(Object[] itemIds, Object parentId, Container.Hierarchical container) {
        for (Object itemId : itemIds) {
            addItem(itemId, container);
            container.setParent(itemId, parentId);
        }
    }
}
