package org.vaadin.leif.selectrenderer;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.annotations.JavaScript;
import com.vaadin.shared.JavaScriptExtensionState;
import com.vaadin.ui.JavaScriptFunction;
import com.vaadin.ui.renderers.AbstractJavaScriptRenderer;
import com.vaadin.util.ReflectTools;

import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonValue;

@JavaScript("SelectRenderer.js")
public class SelectRenderer extends AbstractJavaScriptRenderer<String> {
    private Map<String, Integer> reverseLookup = new HashMap<String, Integer>();

    public static class SelectRendererState extends JavaScriptExtensionState {
        public List<String> options = new ArrayList<String>();
    }

    public static class SelectChangeEvent extends EventObject {
        private Object itemId;
        private String selectedValue;
        private int selectedIndex;

        public SelectChangeEvent(SelectRenderer source, Object itemId,
                String selectedValue, int selectedIndex) {
            super(source);
            this.itemId = itemId;
            this.selectedValue = selectedValue;
            this.selectedIndex = selectedIndex;
        }

        public SelectRenderer getSelectRenderer() {
            return (SelectRenderer) getSource();
        }

        public Object getItemId() {
            return itemId;
        }

        public String getSelectedValue() {
            return selectedValue;
        }

        public int getSelectedIndex() {
            return selectedIndex;
        }
    }

    public interface SelectChangeListener extends Serializable {
        public static final Method SELECT_CHANGE_METHOD = ReflectTools
                .findMethod(SelectChangeListener.class, "selectionChange",
                        SelectChangeEvent.class);

        public void selectionChange(SelectChangeEvent event);
    }

    public SelectRenderer(String... options) {
        this(Arrays.asList(options));
    }

    public SelectRenderer(List<String> options) {
        super(String.class);
        getState().options.addAll(options);

        for (int i = 0; i < options.size(); i++) {
            reverseLookup.put(options.get(i), Integer.valueOf(i));
        }

        addFunction("change", new JavaScriptFunction() {
            @Override
            public void call(JsonArray arguments) {
                String rowKey = arguments.getString(0);
                int selectedIndex = (int) arguments.getNumber(1);
                
                Object itemId = getItemId(rowKey);
                String selectedValue = getState().options.get(selectedIndex);

                fireEvent(new SelectChangeEvent(SelectRenderer.this, itemId,
                        selectedValue, selectedIndex));
            }
        });
    }

    public void addSelectChangeListener(SelectChangeListener listener) {
        addListener(SelectChangeEvent.class, listener,
                SelectChangeListener.SELECT_CHANGE_METHOD);
    }

    public void removeSelectChangeListener(SelectChangeListener listener) {
        removeListener(SelectChangeEvent.class, listener,
                SelectChangeListener.SELECT_CHANGE_METHOD);
    }

    @Override
    protected SelectRendererState getState() {
        return (SelectRendererState) super.getState();
    }

    @Override
    public JsonValue encode(String value) {
        Integer index = reverseLookup.get(value);
        if (index == null) {
            return Json.createNull();
        } else {
            return Json.create(index.doubleValue());
        }
    }
}
