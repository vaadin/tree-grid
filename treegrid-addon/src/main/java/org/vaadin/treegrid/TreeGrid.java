package org.vaadin.treegrid;

import org.vaadin.treegrid.client.TreeGridClientRpc;
import org.vaadin.treegrid.client.TreeGridServerRpc;
import org.vaadin.treegrid.client.TreeGridState;

import com.vaadin.shared.MouseEventDetails;

// This is the server-side UI component that provides public API 
// for TreeGrid
public class TreeGrid extends com.vaadin.ui.AbstractComponent {

	private int clickCount = 0;

	// To process events from the client, we implement ServerRpc
	private TreeGridServerRpc rpc = new TreeGridServerRpc() {

		// Event received from client - user clicked our widget
		public void clicked(MouseEventDetails mouseDetails) {
			
			// Send nag message every 5:th click with ClientRpc
			if (++clickCount % 5 == 0) {
				getRpcProxy(TreeGridClientRpc.class)
						.alert("Ok, that's enough!");
			}
			
			// Update shared state. This state update is automatically 
			// sent to the client. 
			getState().text = "You have clicked " + clickCount + " times";
		}
	};

	public TreeGrid() {

		// To receive events from the client, we register ServerRpc
		registerRpc(rpc);
	}

	// We must override getState() to cast the state to TreeGridState
	@Override
	protected TreeGridState getState() {
		return (TreeGridState) super.getState();
	}
}
