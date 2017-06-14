package org.vaadin.treegrid.event;

import java.lang.reflect.Method;
import java.util.EventListener;

import com.vaadin.data.Item;
import com.vaadin.ui.Component;
import com.vaadin.util.ReflectTools;

/**
 * An event that is fired when an item is collapsed in TreeGrid.
 *
 * @author Vaadin Ltd
 * @since 0.7.6
 */
public class CollapseEvent extends Component.Event {

    private final Item item;
    private final Object itemId;

    /**
     * Construct a collapse event.
     *
     * @param source
     *         the tree grid this event originated from
     * @param item
     *         the item that was collapsed
     * @param itemId
     *         the id of the item that was collapsed
     */
    public CollapseEvent(Component source, Item item, Object itemId) {
        super(source);
        this.item = item;
        this.itemId = itemId;
    }

    /**
     * Get the collapsed item that triggered this event.
     *
     * @return the collapsed item
     */
    public Item getCollapsedItem() {
        return this.item;
    }

    /**
     * Get the id of the collapsed item that triggered this event.
     *
     * @return the collapsed item's id
     */
    public Object getCollapsedItemId() {
        return this.itemId;
    }

    /**
     * Item collapsed event listener.
     */
    public interface CollapseListener extends EventListener {

        public static final Method COLLAPSE_METHOD = ReflectTools.findMethod(
                CollapseListener.class, "itemCollapse", CollapseEvent.class);

        /**
         * Callback method for when an item has been collapsed.
         *
         * @param event
         *         the collapse event
         */
        public void itemCollapse(CollapseEvent event);
    }
}
