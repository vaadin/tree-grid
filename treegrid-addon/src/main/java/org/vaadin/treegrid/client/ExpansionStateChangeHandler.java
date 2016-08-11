package org.vaadin.treegrid.client;

import com.google.gwt.event.shared.EventHandler;

@Deprecated
public interface ExpansionStateChangeHandler extends EventHandler{
    void onExpand();
    void onCollapse();
}
