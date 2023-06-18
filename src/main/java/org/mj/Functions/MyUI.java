package org.mj.Functions;

import javax.swing.*;
import java.awt.*;

public class MyUI {
    private static JPanel createBoxXWith(Component... components){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        for (Component c : components)
            panel.add(c);
        return panel;
    }
    public static JPanel placeRight(Component component) {
        return createBoxXWith(Box.createHorizontalGlue(), component);
    }

    public static JPanel placeLeft(Component component) {
        return createBoxXWith(component, Box.createHorizontalGlue());
    }

    public static JPanel placeCenter(Component component) {
        return createBoxXWith(Box.createHorizontalGlue(), component, Box.createHorizontalGlue());
    }
}
