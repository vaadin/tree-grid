package org.vaadin.treegrid.client;

import com.google.gwt.event.shared.GwtEvent;

@Deprecated
public class ExpansionStateChangeEvent extends
        GwtEvent<ExpansionStateChangeHandler> {

    private HierarchyRenderer2.ExpanderState newState;

    private static final Type<ExpansionStateChangeHandler> TYPE = new Type<ExpansionStateChangeHandler>();

    @Override
    public Type<ExpansionStateChangeHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ExpansionStateChangeHandler handler) {
        switch (newState) {
        case COLLAPSED:
            handler.onCollapse();
            break;
        case EXPANDED:
            handler.onExpand();
            break;
        }
    }

    void setNewState(HierarchyRenderer2.ExpanderState state) {
        this.newState = state;
    }
}
