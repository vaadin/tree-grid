# TreeGrid Add-on for Vaadin 7

TreeGrid is a UI component add-on for Vaadin 7.

## Building and running demo

git clone https://github.com/vaadin/vaadin-tree-grid  
mvn clean install
cd treegrid-demo
mvn jetty:run

To see the demo, navigate to http://localhost:8080/

Note that applying patch files from treegrid-addon/src/main/resources/patches might be needed for successful compilation.

## Development with Eclipse IDE

For further development of this add-on, the following tool-chain is recommended:
- Eclipse IDE
- m2e wtp plug-in (install it from Eclipse Marketplace)
- Vaadin Eclipse plug-in (install it from Eclipse Marketplace)
- JRebel Eclipse plug-in (install it from Eclipse Marketplace)
- Chrome browser

### Importing project

Choose File > Import... > Existing Maven Projects

Note that Eclipse may give "Plugin execution not covered by lifecycle configuration" errors for pom.xml. Use "Permanently mark goal resources in pom.xml as ignored in Eclipse build" quick-fix to mark these errors as permanently ignored in your project. Do not worry, the project still works fine. 

### Debugging server-side

If you have not already compiled the widgetset, do it now by running vaadin:install Maven target for treegrid-root project.

If you have a JRebel license, it makes on the fly code changes faster. Just add JRebel nature to your treegrid-demo project by clicking project with right mouse button and choosing JRebel > Add JRebel Nature

To debug project and make code modifications on the fly in the server-side, right-click the treegrid-demo project and choose Debug As > Debug on Server. Navigate to http://localhost:8080/treegrid-demo/ to see the application.

### Debugging client-side

Debugging client side code in the treegrid-demo project:
  - run "mvn vaadin:run-codeserver" on a separate console while the application is running
  - activate Super Dev Mode in the debug window of the application or by adding ?superdevmode to the URL
  - You can access Java-sources and set breakpoints inside Chrome if you enable source maps from inspector settings.
 
## Release notes

### Version 1.0
- Basic functionality for displaying hierarchical data in Grid
- CSS configurable hierarchy indicators
- Expand/collapse functionality on mouse click
- Programmatically settable hierarchy column
- Keyboard navigation (Alt/Option + Arrow keys)
- Support for custom renderer on hierarchy column
- Support for custom depth calculation logic
- Support for custom collapse logic

## Roadmap

## Issue tracking

The issues for this add-on are tracked on its github.com page. All bug reports and feature requests are appreciated. 

## Contributions

Contributions are welcome, but there are no guarantees that they are accepted as such. Process for contributing is the following:
- Fork this project
- Create an issue to this project about the contribution (bug or feature) if there is no such issue about it already. Try to keep the scope minimal.
- Develop and test the fix or functionality carefully. Only include minimum amount of code needed to fix the issue.
- Refer to the fixed issue in commit
- Send a pull request for the original project
- Comment on the original issue that you have implemented a fix for it

## License & Author

# Developer Guide

## Getting started

Here is a simple example on how to try out the add-on component:

```java
import org.vaadin.treegrid.container.Measurable;
import com.vaadin.data.Collapsible;
// ...

public class DemoContainer extends HierarchicalContainer implements Collapsible, Measurable {
    // ...
}
```

```java
public class DemoUI extends UI {
    // ...

    @Override
    protected void init(VaadinRequest request) {
        final TreeGrid grid = new TreeGrid();

        DemoContainer container = new DemoContainer();
        grid.setContainerDataSource(container);

        grid.setHierarchyColumn(DemoContainer.NAME_PROPERTY);

        grid.setColumnReorderingAllowed(true);

        // ...
    }
}
```

For a more comprehensive example, see treegrid-demo/src/main/java/org/vaadin/treegrid/demo/DemoUI.java
