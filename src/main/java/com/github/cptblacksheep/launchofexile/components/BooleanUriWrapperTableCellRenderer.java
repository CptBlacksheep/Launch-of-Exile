package com.github.cptblacksheep.launchofexile.components;

import com.github.cptblacksheep.launchofexile.datamanagement.UriWrapper;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class BooleanUriWrapperTableCellRenderer extends JCheckBox implements TableCellRenderer {

    private static final Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

    public BooleanUriWrapperTableCellRenderer() {
        setLayout(new GridBagLayout());
        setMargin(new Insets(0, 0, 0, 0));
        setHorizontalAlignment(JLabel.CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        if (value instanceof Boolean)
            setSelected((boolean) value);

        if (!isSelected) {
            setBackground(table.getBackground());
            setForeground(table.getForeground());
        } else {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        }

        setBorder(noFocusBorder);

        return this;
    }
}
