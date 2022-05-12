package com.github.cptblacksheep.launchofexile.components;

import com.github.cptblacksheep.launchofexile.datamanagement.UriWrapper;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.util.List;
import java.util.Objects;

public class UriWrapperTableModel extends AbstractTableModel implements TableModel {
    private final String[] columnNames;
    private final List<UriWrapper> uriWrappers;

    public UriWrapperTableModel(List<UriWrapper> uriWrappers, String firstColumnName, String secondColumnName) {
        this.uriWrappers = Objects.requireNonNull(uriWrappers);
        columnNames = new String[]{firstColumnName, secondColumnName};
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getRowCount() {
        return uriWrappers.size();
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int row, int column) {
        UriWrapper uriWrapper = getUriWrapper(row);

        return switch (column) {
            case 0 -> uriWrapper.getName();
            case 1 -> uriWrapper.getUri();
            default -> null;
        };
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        UriWrapper uriWrapper = getUriWrapper(row);

        switch (column) {
            case 0 -> uriWrapper.setName((String) value);
            case 1 -> uriWrapper.setUri((String) value);
        }

        fireTableCellUpdated(row, column);
    }

    public UriWrapper getUriWrapper(int row) {
        return uriWrappers.get(row);
    }

    public void addUriWrapper(UriWrapper uriWrapper) {
        insertUriWrapper(getRowCount(), uriWrapper);
    }

    public void insertUriWrapper(int row, UriWrapper uriWrapper) {
        uriWrappers.add(row, Objects.requireNonNull(uriWrapper));
        fireTableRowsInserted(row, row);
    }

    public void removeUriWrapper(int row) {
        uriWrappers.remove(row);
        fireTableRowsDeleted(row, row);
    }

    @Override
    public Class<?> getColumnClass(int column) {
        return switch (column) {
            case 0, 1 -> String.class;
            default -> Object.class;
        };
    }
}
