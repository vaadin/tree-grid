package org.vaadin.treegrid.demo;

import java.util.Arrays;

import javax.servlet.annotation.WebServlet;

import org.vaadin.treegrid.TreeGrid;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Property;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.ImageRenderer;

@Theme("demo")
@Title("Vaadin TreeGrid Add-on Demo")
@SuppressWarnings("serial")
public class DemoUI extends UI {

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = DemoUI.class, widgetset = "org.vaadin.treegrid.demo.DemoWidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    private static final String DEMO_SIMPLE = "Simple demo";
    private static final String DEMO_IMAGE = "Image demo";

    @Override
    protected void init(VaadinRequest request) {

        final VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setMargin(true);
        layout.setSizeFull();

        final ComboBox combo = new ComboBox(null, Arrays.asList(DEMO_SIMPLE, DEMO_IMAGE));
        combo.setNullSelectionAllowed(false);
        combo.setSizeUndefined();
        layout.addComponent(combo);

        final VerticalLayout layout2 = new VerticalLayout();
        final TreeGrid grid = new TreeGrid();
        grid.setWidth(800, Unit.PIXELS);

        DemoContainer container = new DemoContainer();
        grid.setContainerDataSource(container);

        grid.getColumn(DemoContainer.PROPERTY_ICON).setRenderer(new ImageRenderer());
        grid.getColumn(DemoContainer.PROPERTY_ICON).setEditable(false);

        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.setColumnReorderingAllowed(true);
        grid.setEditorEnabled(true);
        grid.setHeightMode(HeightMode.ROW);
        grid.setHeightByRows(15);

        layout2.addComponent(grid);
        layout.addComponent(layout2);
        layout.setExpandRatio(layout2, 1);

        combo.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                switch ((String) event.getProperty().getValue()) {
                case DEMO_IMAGE:
                    grid.getColumn(DemoContainer.PROPERTY_ICON).setHidden(false);
                    grid.setHierarchyColumn(DemoContainer.PROPERTY_ICON);
                    grid.setColumnOrder(
                            DemoContainer.PROPERTY_ICON,
                            DemoContainer.PROPERTY_NAME,
                            DemoContainer.PROPERTY_HOURS,
                            DemoContainer.PROPERTY_MODIFIED);
                    setTheme("demo2");
                    break;
                case DEMO_SIMPLE:
                default:
                    grid.getColumn(DemoContainer.PROPERTY_ICON).setHidden(true);
                    grid.setHierarchyColumn(DemoContainer.PROPERTY_NAME);
                    grid.setColumnOrder(
                            DemoContainer.PROPERTY_NAME,
                            DemoContainer.PROPERTY_HOURS,
                            DemoContainer.PROPERTY_MODIFIED);
                    setTheme("demo");
                    break;
                }
            }
        });

        combo.select(DEMO_SIMPLE);
        setSizeFull();

        setContent(layout);
    }
}
