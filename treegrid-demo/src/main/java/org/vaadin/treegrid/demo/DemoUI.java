package org.vaadin.treegrid.demo;

import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import javax.servlet.annotation.WebServlet;

import org.vaadin.treegrid.TreeGrid;
import org.vaadin.treegrid.TreeGridContainer;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
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

        TreeGridContainer container = new TreeGridContainer();
        container.addContainerProperty(NAME_PROPERTY, String.class, "");
        container.addContainerProperty(HOURS_PROPERTY, Integer.class, 0);
        container.addContainerProperty(MODIFIED_PROPERTY, Date.class,
                new Date());
        populateWithRandomHierarchicalData(container);
        grid.setContainerDataSource(container);

        final VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.addComponent(grid);

        setContent(layout);
    }

    private static final String NAME_PROPERTY = "Name";
    private static final String HOURS_PROPERTY = "Hours done";
    private static final String MODIFIED_PROPERTY = "Last modified";

    private void populateWithRandomHierarchicalData(final TreeGridContainer container) {
        final Random random = new Random();
        int hours = 0;

        final Object allProjects = addItem(container, new Object[] {"All Projects", 0, new Date()});
        for (final int year : Arrays.asList(2010, 2011, 2012, 2013)) {
            int yearHours = 0;
            final Object yearId = addItem(container, new Object[] { "Year " + year, yearHours, new Date()});
            container.setParent(yearId, allProjects);
            for (int project = 1; project < random.nextInt(4) + 2; project++) {
                int projectHours = 0;
                final Object projectId = addItem(container, new Object[] { "Customer Project " + project,
                                projectHours, new Date() });
                container.setParent(projectId, yearId);
                for (final String phase : Arrays.asList("Implementation",
                        "Planning", "Prototype")) {
                    final int phaseHours = random.nextInt(50);
                    final Object phaseId = addItem(container, new Object[] { phase,
                            phaseHours, new Date() });
                    container.setParent(phaseId, projectId);
                    container.setChildrenAllowed(phaseId, false);
//                  todo  container.setCollapsed(phaseId, false);
                    projectHours += phaseHours;
                }
                yearHours += projectHours;
                container.getItem(projectId).getItemProperty(HOURS_PROPERTY)
                        .setValue(projectHours);
//                sample.setCollapsed(projectId, false);
            }
            hours += yearHours;
            container.getItem(yearId).getItemProperty(HOURS_PROPERTY)
                    .setValue(yearHours);
//            sample.setCollapsed(yearId, false);
        }
        container.getItem(allProjects).getItemProperty(HOURS_PROPERTY)
                .setValue(hours);
//        sample.setCollapsed(allProjects, false);
    }

    private Object addItem(HierarchicalContainer container, Object[] values) {
        Item item = container.addItem(values);
        item.getItemProperty(NAME_PROPERTY).setValue(values[0]);
        item.getItemProperty(HOURS_PROPERTY).setValue(values[1]);
        item.getItemProperty(MODIFIED_PROPERTY).setValue(values[2]);

        return values;
    }
}
