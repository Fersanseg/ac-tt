package com.actt.actt.utils;

import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

public class ControlOperations {
    public static <T> List<T> GetNodesByType(Pane pane, Class<T> type) {
        var children = pane.getChildren();
        if (children == null || children.isEmpty())
            return null;

        // Find all the immediate T children (if any)
        List<T> out = new ArrayList<>(children
                .filtered(type::isInstance)
                .stream()
                .map(type::cast)
                .toList());

        // Find all the immediate Pane children (if any) and call GetNodesByType recursively in search of T nodes
        children
            .filtered(Pane.class::isInstance)
            .stream()
            .map(Pane.class::cast)
            .forEach(p -> {
                if (p != null) {
                    List<T> auxT = GetNodesByType(p, type);
                    if (auxT != null)
                        out.addAll(auxT);
                }
            });

        return out;
    }
}
