package com.github.cptblacksheep.launchofexile.components;

import com.github.cptblacksheep.launchofexile.datamanagement.UriWrapper;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class UriWrapperTableCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        Component renderer = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        UriWrapper uriWrapper = (UriWrapper) value;

        if (!uriWrapper.isEnabled())
            renderer.setBackground(Color.RED);

        return renderer;
    }
}
