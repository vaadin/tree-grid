package org.vaadin.treegrid.demo;

import javax.servlet.annotation.WebServlet;

import org.vaadin.treegrid.TreeGrid;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Grid;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("demo")
@Title("TreeGrid Add-on Demo")
@SuppressWarnings("serial")
public class DemoUI extends UI
{

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = DemoUI.class, widgetset = "org.vaadin.treegrid.demo.DemoWidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {

        final TreeGrid grid = new TreeGrid();
        grid.setWidth(600, Unit.PIXELS);

        DemoContainer container = new DemoContainer();
        grid.setContainerDataSource(container);

        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.setColumnReorderingAllowed(true);
        grid.setEditorEnabled(true);

        final VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.addComponent(grid);

        setContent(layout);
    }
}
