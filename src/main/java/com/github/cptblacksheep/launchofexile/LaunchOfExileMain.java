package com.github.cptblacksheep.launchofexile;

import com.formdev.flatlaf.FlatLightLaf;
import com.github.cptblacksheep.launchofexile.components.UriWrapperTableCellRenderer;
import com.github.cptblacksheep.launchofexile.components.UriWrapperTableModel;
import com.github.cptblacksheep.launchofexile.datamanagement.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.Objects;

public class LaunchOfExileMain {
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
        applicationManager = new ApplicationManager();
        websiteManager = new WebsiteManager();
        jsonSerializer = new JsonSerializer(applicationManager, websiteManager);

        comboBoxVersion.addItem(PoeVersion.STEAM);
        comboBoxVersion.addItem(PoeVersion.STANDALONE);

        loadData();

        createJTablesAndModels();

        btnAddTool.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Add tool");
            fc.setAcceptAllFileFilterUsed(false);
            fc.setFileFilter(new FileNameExtensionFilter(".exe", "exe"));

            int returnValue = fc.showDialog(null, "Add to tools");

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                String toolPath = fc.getSelectedFile().getAbsolutePath();
                UriWrapper tool = new UriWrapper(toolPath);
                modelTools.addUriWrapper(tool);
                jsonSerializer.saveData();
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
        });

        btnAddWebsite.addActionListener(e -> {
            String websiteUrl = JOptionPane.showInputDialog("Insert URL to add:");

            if (websiteUrl != null && !websiteUrl.isBlank()) {
                UriWrapper website = new UriWrapper(websiteUrl);
                modelWebsites.addUriWrapper(website);
                jsonSerializer.saveData();
            }
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
        });

        btnSetPoeExeLocation.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Poe .exe location");
            fc.setAcceptAllFileFilterUsed(false);
            fc.setFileFilter(new FileNameExtensionFilter(".exe", "exe"));

            int returnValue = fc.showDialog(null, "Set location");

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                String exeLocation = fc.getSelectedFile().getAbsolutePath();
                tfPoeExeLocation.setText(exeLocation);
                applicationManager.setPoeExeLocation(exeLocation);
                jsonSerializer.saveData();
            }
        });

        comboBoxVersion.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                setComponentVisibilityBasedOnComboBoxVersion();
                jsonSerializer.saveData();
            }
        });

        btnLaunch.addActionListener(e -> {
            websiteManager.openAllEnabledWebsites();
            applicationManager.startAllEnabledApplications();
            applicationManager.startPoe(applicationManager.getSelectedPoeVersion());
            System.exit(0);
        });

        btnLaunchPoeOnly.addActionListener(e -> {
            applicationManager.startPoe(applicationManager.getSelectedPoeVersion());
            System.exit(0);
        });


    }

    public static void initialize() {
        FlatLightLaf.setup();

        JFrame frame = new JFrame("Launch of Exile");
        frame.setContentPane(new LaunchOfExileMain().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setSize(new Dimension(700, 500));
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(
                LaunchOfExileMain.class.getClassLoader().getResource("Launch_of_Exile.png")));
        frame.setVisible(true);

    }

    private void loadData() {
        jsonSerializer.loadData();
        tfPoeExeLocation.setText(applicationManager.getPoeExeLocation());
        comboBoxVersion.setSelectedItem(applicationManager.getSelectedPoeVersion());
        setComponentVisibilityBasedOnComboBoxVersion();
    }

    private void setComponentVisibilityBasedOnComboBoxVersion() {
        PoeVersion selection = (PoeVersion) comboBoxVersion.getSelectedItem();

        if (Objects.equals(selection, PoeVersion.STEAM)) {
            lblPoeExeLocation.setVisible(false);
            tfPoeExeLocation.setVisible(false);
            btnSetPoeExeLocation.setVisible(false);
            applicationManager.setSelectedPoeVersion(PoeVersion.STEAM);
        } else if (Objects.equals(selection, PoeVersion.STANDALONE)) {
            lblPoeExeLocation.setVisible(true);
            tfPoeExeLocation.setVisible(true);
            btnSetPoeExeLocation.setVisible(true);
            applicationManager.setSelectedPoeVersion(PoeVersion.STANDALONE);
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
        String newName = JOptionPane.showInputDialog("Enter new name:");

        if (newName == null)
            return;

        uriWrapper.setName(newName);
        jsonSerializer.saveData();
    }

    public static void main(String[] args) {
        initialize();
    }

}
