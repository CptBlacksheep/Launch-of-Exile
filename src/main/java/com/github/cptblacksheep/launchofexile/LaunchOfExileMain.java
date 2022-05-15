package com.github.cptblacksheep.launchofexile;

import com.formdev.flatlaf.FlatLightLaf;
import com.github.cptblacksheep.launchofexile.components.UriWrapperTableCellRenderer;
import com.github.cptblacksheep.launchofexile.components.UriWrapperTableModel;
import com.github.cptblacksheep.launchofexile.datamanagement.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.util.Objects;

public class LaunchOfExileMain {
    private static boolean skipLauncherEnabled = false;
    private final Settings settings;
    private final ApplicationManager applicationManager;
    private final WebsiteManager websiteManager;
    private final JsonSerializer jsonSerializer;
    private UriWrapperTableModel modelTools;
    private UriWrapperTableModel modelWebsites;
    private JPanel panelMain;
    private JTable tableTools;
    private JTable tableWebsites;
    private JButton btnLaunch;
    private JButton btnAddTool;
    private JButton btnRemoveTool;
    private JLabel lblTools;
    private JLabel lblWebsites;
    private JButton btnAddWebsite;
    private JButton btnRemoveWebsite;
    private JComboBox<PoeVersion> comboBoxVersion;
    private JLabel lblVersion;
    private JScrollPane scrollPaneTools;
    private JScrollPane scrollPaneWebsites;
    private JLabel lblPoeExeLocation;
    private JTextField tfPoeExeLocation;
    private JButton btnSetPoeExeLocation;
    private JSeparator separatorLaunch;
    private JButton btnLaunchPoeOnly;
    private JPanel panelVersion;
    private JPanel panelPoeExeLocation;
    private JButton btnEnableDisableTool;
    private JButton btnEnableDisableWebsite;
    private JButton btnRenameTool;
    private JButton btnRenameWebsite;

    private LaunchOfExileMain() {
        settings = Settings.getSettings();
        applicationManager = new ApplicationManager();
        websiteManager = new WebsiteManager();
        jsonSerializer = new JsonSerializer(applicationManager, websiteManager);

        comboBoxVersion.addItem(PoeVersion.STEAM);
        comboBoxVersion.addItem(PoeVersion.STANDALONE);

        loadDataAndSettings();

        if (skipLauncherEnabled)
            launchOpenAllExit();

        createJTablesAndModels();

        btnAddTool.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Launch of Exile - Add tool");
            fc.setAcceptAllFileFilterUsed(false);
            fc.setFileFilter(new FileNameExtensionFilter(".exe, .ahk", "exe", "ahk"));

            int returnValue = fc.showDialog(null, "Add to tools");

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                String toolPath = fc.getSelectedFile().getAbsolutePath();
                UriWrapper tool = new UriWrapper(toolPath);
                modelTools.addUriWrapper(tool);
                jsonSerializer.saveData();
                tableTools.setRowSelectionInterval(modelTools.getRowCount() - 1, modelTools.getRowCount() - 1);
            }
        });

        btnRemoveTool.addActionListener(e -> {
            int selectedRow = tableTools.getSelectedRow();

            if (selectedRow < 0)
                return;

            UriWrapper tool = modelTools.getUriWrapper(selectedRow);

            if (tool == null)
                return;

            modelTools.removeUriWrapper(selectedRow);
            jsonSerializer.saveData();

            if (selectedRow > 0)
                tableTools.setRowSelectionInterval(selectedRow - 1, selectedRow - 1);
        });

        btnEnableDisableTool.addActionListener(e -> {
            int selectedRow = tableTools.getSelectedRow();

            if (selectedRow < 0)
                return;

            UriWrapper tool = modelTools.getUriWrapper(selectedRow);

            if (tool == null)
                return;

            tool.setEnabled(!tool.isEnabled());
            tableTools.repaint();
            jsonSerializer.saveData();
            tableTools.setRowSelectionInterval(selectedRow, selectedRow);
        });

        btnRenameTool.addActionListener(e -> {
            int selectedRow = tableTools.getSelectedRow();

            if (selectedRow < 0)
                return;

            UriWrapper tool = modelTools.getUriWrapper(selectedRow);

            if (tool == null)
                return;

            rename(tool);
            tableTools.repaint();
            tableTools.setRowSelectionInterval(selectedRow, selectedRow);
        });

        btnAddWebsite.addActionListener(e -> {
            showAddWebsiteDialog();
        });

        btnRemoveWebsite.addActionListener(e -> {
            int selectedRow = tableWebsites.getSelectedRow();

            if (selectedRow < 0)
                return;

            UriWrapper website = modelWebsites.getUriWrapper(selectedRow);

            if (website == null)
                return;

            modelWebsites.removeUriWrapper(selectedRow);
            jsonSerializer.saveData();

            if (selectedRow > 0)
                tableWebsites.setRowSelectionInterval(selectedRow - 1, selectedRow - 1);
        });

        btnEnableDisableWebsite.addActionListener(e -> {
            int selectedRow = tableWebsites.getSelectedRow();

            if (selectedRow < 0)
                return;

            UriWrapper website = modelWebsites.getUriWrapper(selectedRow);

            if (website == null)
                return;

            website.setEnabled(!website.isEnabled());
            tableWebsites.repaint();
            jsonSerializer.saveData();
            tableWebsites.setRowSelectionInterval(selectedRow, selectedRow);
        });

        btnRenameWebsite.addActionListener(e -> {
            int selectedRow = tableWebsites.getSelectedRow();

            if (selectedRow < 0)
                return;

            UriWrapper website = modelWebsites.getUriWrapper(selectedRow);

            if (website == null)
                return;

            rename(website);
            tableWebsites.repaint();

            tableWebsites.setRowSelectionInterval(selectedRow, selectedRow);
        });

        btnSetPoeExeLocation.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Launch of Exile - Set PoE .exe location");
            fc.setAcceptAllFileFilterUsed(false);
            fc.setFileFilter(new FileNameExtensionFilter(".exe", "exe"));

            int returnValue = fc.showDialog(null, "Set location");

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                String exeLocation = fc.getSelectedFile().getAbsolutePath();
                tfPoeExeLocation.setText(exeLocation);
                settings.setPoeExeLocation(exeLocation);
                jsonSerializer.saveSettings();
            }
        });

        comboBoxVersion.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                setComponentVisibilityBasedOnComboBoxVersion();
                jsonSerializer.saveSettings();
            }
        });

        btnLaunch.addActionListener(e -> {
            launchOpenAllExit();
        });

        btnLaunchPoeOnly.addActionListener(e -> {
            applicationManager.startPoe(settings.getSelectedPoeVersion());
            System.exit(0);
        });

        tableTools.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                tableTools.clearSelection();
            }
        });

        tableWebsites.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                tableWebsites.clearSelection();
            }
        });

    }

    public static void initialize() {
        FlatLightLaf.setup();

        JFrame frame = new JFrame("Launch of Exile");
        frame.setContentPane(new LaunchOfExileMain().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(false);
        frame.setSize(new Dimension(700, 500));
        frame.setLocationRelativeTo(null);
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(
                LaunchOfExileMain.class.getClassLoader().getResource("Launch_of_Exile.png")));
        frame.setVisible(true);

    }

    private void loadDataAndSettings() {
        jsonSerializer.loadDataAndSettings();
        tfPoeExeLocation.setText(settings.getPoeExeLocation());
        comboBoxVersion.setSelectedItem(settings.getSelectedPoeVersion());
        setComponentVisibilityBasedOnComboBoxVersion();
    }

    private void setComponentVisibilityBasedOnComboBoxVersion() {
        PoeVersion selection = (PoeVersion) comboBoxVersion.getSelectedItem();

        if (Objects.equals(selection, PoeVersion.STEAM)) {
            lblPoeExeLocation.setVisible(false);
            tfPoeExeLocation.setVisible(false);
            btnSetPoeExeLocation.setVisible(false);
            settings.setSelectedPoeVersion(PoeVersion.STEAM);
        } else if (Objects.equals(selection, PoeVersion.STANDALONE)) {
            lblPoeExeLocation.setVisible(true);
            tfPoeExeLocation.setVisible(true);
            btnSetPoeExeLocation.setVisible(true);
            settings.setSelectedPoeVersion(PoeVersion.STANDALONE);
        }
    }

    private void createJTablesAndModels() {
        modelTools = new UriWrapperTableModel(applicationManager.getApplications(),
                "Name", "Path");

        tableTools.setModel(modelTools);
        tableTools.getTableHeader().setReorderingAllowed(false);
        tableTools.getTableHeader().setResizingAllowed(false);
        tableTools.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        UriWrapperTableCellRenderer renderer = new UriWrapperTableCellRenderer();
        tableTools.setDefaultRenderer(String.class, renderer);

        TableColumn toolsNameColumn = tableTools.getColumnModel().getColumn(0);
        TableColumn pathNameColumn = tableTools.getColumnModel().getColumn(1);
        toolsNameColumn.setMinWidth(150);
        pathNameColumn.setMinWidth(300);

        modelWebsites = new UriWrapperTableModel(websiteManager.getWebsites(),
                "Name", "URL");

        tableWebsites.setModel(modelWebsites);
        tableWebsites.getTableHeader().setReorderingAllowed(false);
        tableWebsites.getTableHeader().setResizingAllowed(false);
        tableWebsites.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableWebsites.setDefaultRenderer(String.class, renderer);

        TableColumn websiteNameColumn = tableWebsites.getColumnModel().getColumn(0);
        TableColumn websiteUrlColumn = tableWebsites.getColumnModel().getColumn(1);
        websiteNameColumn.setMinWidth(150);
        websiteUrlColumn.setMinWidth(300);

    }

    private void rename(UriWrapper uriWrapper) {
        String newName = JOptionPane.showInputDialog(null, "Enter new name:",
                "Launch of Exile - Rename", JOptionPane.QUESTION_MESSAGE);

        if (newName == null)
            return;

        uriWrapper.setName(newName);
        jsonSerializer.saveData();
    }

    private void launchOpenAllExit() {
        websiteManager.openAllEnabledWebsites();
        applicationManager.startAllEnabledApplications();
        applicationManager.startPoe(settings.getSelectedPoeVersion());
        System.exit(0);
    }

    private void showAddWebsiteDialog() {
        JTextField tfUrl = new JTextField();
        JTextField tfName = new JTextField();

        Object[] inputFields = {"URL:", tfUrl, "Name (optional):", tfName};

        int option = JOptionPane.showConfirmDialog(null, inputFields,
                "Launch of Exile - Add website", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (option != JOptionPane.OK_OPTION)
            return;

        String url = tfUrl.getText();

        if (url == null || url.isBlank())
            return;

        String name = tfName.getText();

        UriWrapper website;
        if (name == null || name.isBlank())
            website = new UriWrapper(url);
        else
            website = new UriWrapper(url, name);

        modelWebsites.addUriWrapper(website);
        jsonSerializer.saveData();
        tableWebsites.setRowSelectionInterval(modelWebsites.getRowCount() - 1, modelWebsites.getRowCount() - 1);
    }

    public static void main(String[] args) {
        if (args.length > 0 && "-skiplauncher".equals(args[0]))
            skipLauncherEnabled = true;

        initialize();
    }

}
