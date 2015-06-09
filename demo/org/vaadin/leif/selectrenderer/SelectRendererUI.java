package org.vaadin.leif.selectrenderer;

import java.util.ArrayList;

import javax.servlet.annotation.WebServlet;

import org.vaadin.leif.selectrenderer.SelectRenderer.SelectChangeEvent;
import org.vaadin.leif.selectrenderer.SelectRenderer.SelectChangeListener;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("valo")
public class SelectRendererUI extends UI {

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = SelectRendererUI.class)
    public static class Servlet extends VaadinServlet {
        // Empty
    }

    @Override
    protected void init(VaadinRequest request) {
        ArrayList<String> options = new ArrayList<String>();
        for (int i = 0; i < 10; i++) {
            options.add("option " + i);
        }

        SelectRenderer selectRenderer = new SelectRenderer(options);
        selectRenderer.addSelectChangeListener(new SelectChangeListener() {
            @Override
            public void selectionChange(SelectChangeEvent event) {
                Notification.show("Selection change to "
                        + event.getSelectedValue() + " for item "
                        + event.getItemId());
            }
        });

        Grid grid = new Grid();
        grid.addColumn("Caption");
        grid.addColumn("Dropdown").setRenderer(selectRenderer);

        for (int i = 0; i < 1000; i++) {
            grid.addRow(Integer.toString(i), "option " + (i % 10));
        }

        VerticalLayout layout = new VerticalLayout(grid);
        layout.setMargin(true);
        setContent(layout);
    }

}