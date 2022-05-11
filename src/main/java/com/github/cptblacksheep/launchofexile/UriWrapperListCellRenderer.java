package com.github.cptblacksheep.launchofexile;

import com.github.cptblacksheep.launchofexile.datamanagement.UriWrapper;

import javax.swing.*;
import java.awt.*;

public class UriWrapperListCellRenderer implements ListCellRenderer<UriWrapper> {
    private final DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

    @Override
    public Component getListCellRendererComponent(
            JList<? extends UriWrapper> list, UriWrapper value, int index, boolean isSelected, boolean cellHasFocus) {

        JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);

        renderer.setText(value.getName());

        if (!value.isEnabled())
            renderer.setForeground(Color.GRAY);

        return renderer;
    }
}
