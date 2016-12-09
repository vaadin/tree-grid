package org.vaadin.treegrid.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

class DataSource {

    private static List<Object[]> rootNodes = new ArrayList<>();

    private static Map<Object, List<Object[]>> children = new HashMap<>();

    private static Set<Object[]> leaves = new HashSet<>();

    static {
        populateWithRandomHierarchicalData();
    }

    static List<Object[]> getRoot() {
        return rootNodes;
    }

    static List<Object[]> getChildren(Object parent) {
        return children.get(parent);
    }

    static boolean isLeaf(Object itemId) {
        return leaves.contains(itemId);
    }

    private static void populateWithRandomHierarchicalData() {
        final Random random = new Random();
        int hours = 0;

        final Object[] allProjects = new Object[] {"All Projects", 0, new Date()};
        for (final int year : Arrays.asList(2010, 2011, 2012, 2013)) {
            int yearHours = 0;
            final Object[] yearId = new Object[] { "Year " + year, yearHours, new Date()};
            addChild(allProjects, yearId);
            for (int project = 1; project < random.nextInt(4) + 2; project++) {
                int projectHours = 0;
                final Object[] projectId = new Object[] { "Customer Project " + project,
                        projectHours, new Date() };
                addChild(yearId, projectId);
                for (final String phase : Arrays.asList("Implementation",
                        "Planning", "Prototype")) {
                    final int phaseHours = random.nextInt(50);
                    final Object[] phaseId = new Object[] { phase,
                            phaseHours, new Date() };
                    leaves.add(phaseId);
                    addChild(projectId, phaseId);
                    projectHours += phaseHours;
                    projectId[1] = projectHours;
                }
                yearHours += projectHours;
                yearId[1] = yearHours;
            }
            hours += yearHours;
            allProjects[1] = hours;
        }

        rootNodes.add(allProjects);
    }

    private static void addChild(Object parent, Object[] child) {
        if (!children.containsKey(parent)) {
            children.put(parent, new ArrayList<Object[]>());
        }
        children.get(parent).add(child);
    }
}
