package org.vaadin.treegrid.demo;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.ui.Notification;
import org.vaadin.treegrid.TreeGrid;
import org.vaadin.treegrid.TreeGridContainer;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

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

//        // Initialize our new UI component
//        final TreeGrid grid = new TreeGrid();
//        grid.setWidth(800, Unit.PIXELS);
////        grid.setDataSource
//
//        CustomerService service = CustomerService.getInstance();
////        grid.setContainerDataSource(new TreeGridContainer(Customer.class, service.findAll()));
//        grid.setContainerDataSource(new BeanItemContainer<Customer>(Customer.class, service.findAll()));
////        grid.setContainerDataSource(createContainer(100));
//
//
//        // Show it in the middle of the screen
//        final VerticalLayout layout = new VerticalLayout();
////        layout.setStyleName("demoContentLayout");
//        layout.setSizeFull();
//        layout.addComponent(grid);
////        layout.setComponentAlignment(component, Alignment.MIDDLE_CENTER);
//        setContent(layout);

        // with hierarchical container
        final TreeGrid grid = new TreeGrid();
        grid.setWidth(600, Unit.PIXELS);

//        CustomerService service = CustomerService.getInstance();
//        TreeGridContainer container = new TreeGridContainer();
//
//        List<Customer> data = service.findAll();
////        container.addContainerProperty()
//        for (Customer c : data) {
//            container.addItem(c);
//        }
//
//        grid.setContainerDataSource((HierarchicalContainer)container);

        // TreeGridContainer
        TreeGridContainer container = new TreeGridContainer();
        container.addContainerProperty(NAME_PROPERTY, String.class, "");
        container.addContainerProperty(HOURS_PROPERTY, Integer.class, 0);
        container.addContainerProperty(MODIFIED_PROPERTY, Date.class, new Date());
        populateWithRandomHierarchicalData(container);
        grid.setContainerDataSource(container);

        final VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.addComponent(grid);

        setContent(layout);
    }

//    private HierarchicalContainer createDemoDataSource() {
//        // demo data
//        List<Customer> customers = CustomerService.getInstance().findAll();
//
//        // create container
//        HierarchicalContainer container = new HierarchicalContainer();
//        container.
//    }

    private HierarchicalContainer createContainer(int nItems) {
        if(nItems<30) {
            nItems=30;
        }
        final String[] names={"Billy", "Willy","Timmy","Bob","Mog","Rilley", "Ville","Bobby", "Moby", "Ben"};
        final String[] lastName={"Black","White","Anaya","Anders","Andersen","Anderson","Andrade","Andre","Andres","Andrew","Andrews"};
        final int  minIncome=1500;
        final int maxIncome=4000;

        final HierarchicalContainer container=new HierarchicalContainer();
        container.addContainerProperty("id", Integer.class, 0);
        container.addContainerProperty("name", String.class, "");
        container.addContainerProperty("lastName", String.class, "");
        container.addContainerProperty("income", Integer.class, 0);

        for(int i=0;i<nItems;i++) {
            final Object itemId=""+i;
            final Item item=container.addItem(itemId);
            item.getItemProperty("id").setValue(i);
            item.getItemProperty("name").setValue(getValueFromArray(names));
            item.getItemProperty("lastName").setValue(getValueFromArray(lastName));
            item.getItemProperty("income").setValue(generateIncome(minIncome,maxIncome));
            container.addItem(itemId);
        }
        createHierarcy(container,nItems);
        Notification.show(nItems+ " created" );
        return container;
    }

    private void addParent(HierarchicalContainer container,String item,String parent) {
        if(container.getItem(item)!=null) {
            if(container.getItem(parent)!=null) {
                container.setParent(item,parent);
            }
        }
    }

    private void createHierarcy(HierarchicalContainer container,int nItems) {
        final int nLevels=5;
        for(int i=0;i<nItems;i++) {
            final String itemId=""+i;
            if((i%nLevels)==0) {

            }
            else if ((i%nLevels)==2) {
                addParent(container,itemId,(i-2)+"");
            }
            else {
                addParent(container,itemId,(i-1)+"");
            }
        }
    }

    private int generateIncome(int min,int max) {
        final Random rand=new Random();
        final int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    private String getValueFromArray(String[] list) {
        final int size=list.length;
        final Random rand=new Random();
        final int index=rand.nextInt(size);
        return list[index];
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
