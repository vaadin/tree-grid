package org.vaadin.treegrid.event;

import java.lang.reflect.Method;
import java.util.EventListener;

import com.vaadin.data.Item;
import com.vaadin.ui.Component;
import com.vaadin.util.ReflectTools;

/**
 * An event that is fired when an item is expanded in TreeGrid.
 *
 * @author Vaadin Ltd
 * @since 0.7.6
 */
public class ExpandEvent extends Component.Event {

    private final Item item;
    private final Object itemId;

    /**
     * Construct an expand event.
     *
     * @param source
     *         the tree grid this event originated from
     * @param item
     *         the item that was expanded
     * @param itemId
     *         the id of the item that was expanded
     */
    public ExpandEvent(Component source, Item item, Object itemId) {
        super(source);
        this.item = item;
        this.itemId = itemId;
    }

    /**
     * Get the expanded item that triggered this event.
     *
     * @return the expanded item
     */
    public Item getExpandedItem() {
        return this.item;
    }

    /**
     * Get the id of the expanded item that triggered this event.
     *
     * @return the expanded item's id
     */
    public Object getExpandedItemId() {
        return this.itemId;
    }

    /**
     * Item expand event listener.
     */
    public interface ExpandListener extends EventListener {

        public static final Method EXPAND_METHOD = ReflectTools.findMethod(
                ExpandListener.class, "itemExpand", ExpandEvent.class);

        /**
         * Callback method for when an item has been expanded.
         *
         * @param event
         *         the expand event
         */
        public void itemExpand(ExpandEvent event);
    }
}
