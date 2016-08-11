package org.vaadin.treegrid.demo;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.ui.Notification;
import org.vaadin.treegrid.TreeGrid;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

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

        // Initialize our new UI component
        final TreeGrid grid = new TreeGrid();
        grid.setWidth(800, Unit.PIXELS);
//        grid.setDataSource

        CustomerService service = CustomerService.getInstance();
//        grid.setContainerDataSource(new TreeGridContainer(Customer.class, service.findAll()));
        grid.setContainerDataSource(new BeanItemContainer<Customer>(Customer.class, service.findAll()));
//        grid.setContainerDataSource(createContainer(100));


        // Show it in the middle of the screen
        final VerticalLayout layout = new VerticalLayout();
//        layout.setStyleName("demoContentLayout");
        layout.setSizeFull();
        layout.addComponent(grid);
//        layout.setComponentAlignment(component, Alignment.MIDDLE_CENTER);
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

}
