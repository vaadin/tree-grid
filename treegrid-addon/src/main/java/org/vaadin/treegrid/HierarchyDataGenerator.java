package org.vaadin.treegrid;

import org.vaadin.treegrid.client.TreeGridState;
import org.vaadin.treegrid.container.Measurable;

import com.vaadin.data.Collapsible;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.server.JsonCodec;
import com.vaadin.server.communication.data.DataGenerator;
import com.vaadin.shared.ui.grid.GridState;
import com.vaadin.ui.Grid;

import elemental.json.JsonObject;
import elemental.json.JsonValue;

/**
 * Metadata generator for hierarchy information
 */
public class HierarchyDataGenerator extends Grid.AbstractGridExtension implements DataGenerator {

	private HierarchyDataGenerator(TreeGrid grid) {
		super(grid);
	}

	static HierarchyDataGenerator extend(TreeGrid grid) {
		return new HierarchyDataGenerator(grid);
	}

	@Override
	public void generateData(Object itemId, Item item, JsonObject rowData) {
		HierarchyData hierarchyData = new HierarchyData();

		// calculate depth
		int depth = 0;
		if (getContainer() instanceof Measurable) {
			depth = ((Measurable) getContainer()).getDepth(itemId); // Measurable
		} else {
			Object id = itemId;
			while (!getContainer().isRoot(id)) {
				id = getContainer().getParent(id);
				depth++;
			}
		}
		hierarchyData.setDepth(depth);

		// set collapsed state
		if (getContainer() instanceof Collapsible) {
			hierarchyData.setCollapsed(((Collapsible) getContainer()).isCollapsed(itemId)); // Collapsible
		}

		// set leaf state
		hierarchyData.setLeaf(!getContainer().hasChildren(itemId)); // Hierarchical

		// set index of parent node
		hierarchyData.setParentIndex(getContainer().indexOfId(getContainer().getParent(itemId))); // Indexed (indexOfId)
																									// and Hierarchical
																									// (getParent)

		JsonValue value = JsonCodec.encode(hierarchyData, null, HierarchyData.class, getUI().getConnectorTracker())
				.getEncodedValue();

		// add hierarchy information to row as metadata
		rowData.put(TreeGridState.JSONKEY_HIERARCHYDATA, value);
	}

	@Override
	public void destroyData(Object itemId) {
		// Nothing to clean up
	}

	/**
	 * Get container data source that implements
	 * {@link com.vaadin.data.Container.Indexed} and
	 * {@link com.vaadin.data.Container.Hierarchical} as well.
	 *
	 * @return TreeGrid's container data source
	 */
	@SuppressWarnings("unchecked")
	private <T extends Container.Indexed & Container.Hierarchical> T getContainer() {
		// TreeGrid's data source has to implement both Indexed and Hierarchical so it
		// is safe to cast.
		return (T) getParentGrid().getContainerDataSource();
	}
}
