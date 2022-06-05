package com.github.cptblacksheep.launchofexile;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.github.cptblacksheep.launchofexile.components.BooleanUriWrapperTableCellRenderer;
import com.github.cptblacksheep.launchofexile.components.UriWrapperTableCellRenderer;
import com.github.cptblacksheep.launchofexile.components.UriWrapperTableModel;
import com.github.cptblacksheep.launchofexile.datamanagement.*;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Objects;

public class LaunchOfExileMain {
    private static boolean skipLauncherEnabled = false;
    private static Settings settings;
    private static ApplicationManager applicationManager;
    private static WebsiteManager websiteManager;
    private static JsonSerializer jsonSerializer;
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
    private JLabel lblPoeVersion;
    private JScrollPane scrollPaneTools;
    private JScrollPane scrollPaneWebsites;
    private JLabel lblPoeExeLocation;
    private JTextField tfPoeExeLocation;
    private JButton btnSetPoeExeLocation;
    private JSeparator separatorLaunch;
    private JButton btnLaunchPoeOnly;
    private JButton btnRenameTool;
    private JButton btnRenameWebsite;
    private JCheckBox checkBoxEnableDarkMode;
    private JCheckBox checkBoxEnableAhkSupport;
    private JTextField tfAhkExeLocation;
    private JButton btnSetAhkExeLocation;
    private JLabel lblAhkExeLocation;
    private JPanel panelLaunchButtons;
    private JPanel panelSettings;
    private JTabbedPane tabbedPane;
    private JPanel panelPoeVersion;
    private JPanel panelPoeExeLocation;
    private JPanel panelAhkExeLocation;
    private JLabel lblLoEVersion;
    private JCheckBox checkBoxShowUpdateNotifications;
    private JButton btnCheckForUpdates;
    private JPanel panelToolButtons;
    private JPanel panelWebsiteButtons;
    private JButton btnChangeToolPath;
    private JButton btnChangeWebsiteUrl;

    private LaunchOfExileMain() {
        addItemsToComboBoxVersion();
        tfPoeExeLocation.setText(settings.getPoeExeLocation());
        tfAhkExeLocation.setText(settings.getAhkExeLocation());

        comboBoxVersion.setSelectedItem(settings.getSelectedPoeVersion());
        setPoeExeComponentVisibilityByPoeVersion(settings.getSelectedPoeVersion());

        checkBoxEnableAhkSupport.setSelected(settings.isAhkSupportEnabled());
        setAHKComponentVisibility(settings.isAhkSupportEnabled());

        checkBoxEnableDarkMode.setSelected(settings.isDarkModeEnabled());
        checkBoxShowUpdateNotifications.setSelected(settings.isUpdateNotificationsEnabled());

        createJTablesAndModels();

        lblLoEVersion.setText("v" + UpdateChecker.VERSION);
        panelMain.requestFocusInWindow();

        btnAddTool.addActionListener(e -> showAddToolDialog());

        btnRemoveTool.addActionListener(e -> {
            int selectedRow = tableTools.getSelectedRow();

            if (selectedRow < 0)
                return;

            modelTools.removeUriWrapper(selectedRow);

            if (selectedRow > 0)
                tableTools.setRowSelectionInterval(selectedRow - 1, selectedRow - 1);
        });

        btnRenameTool.addActionListener(e -> {
            int selectedRow = tableTools.getSelectedRow();

            if (selectedRow < 0)
                return;

            renameUriWrapper(modelTools, selectedRow, tableTools);
        });

        btnChangeToolPath.addActionListener(e -> {
            int selectedRow = tableTools.getSelectedRow();

            if (selectedRow < 0)
                return;

            showChangeToolPathDialog(selectedRow);
        });

        btnAddWebsite.addActionListener(e -> showAddWebsiteDialog());

        btnRemoveWebsite.addActionListener(e -> {
            int selectedRow = tableWebsites.getSelectedRow();

            if (selectedRow < 0)
                return;

            modelWebsites.removeUriWrapper(selectedRow);

            if (selectedRow > 0)
                tableWebsites.setRowSelectionInterval(selectedRow - 1, selectedRow - 1);
        });

        btnRenameWebsite.addActionListener(e -> {
            int selectedRow = tableWebsites.getSelectedRow();

            if (selectedRow < 0)
                return;

            renameUriWrapper(modelWebsites, selectedRow, tableWebsites);
        });

        btnChangeWebsiteUrl.addActionListener(e -> {
            int selectedRow = tableWebsites.getSelectedRow();

            if (selectedRow < 0)
                return;

            showChangeWebsiteUrlDialog(selectedRow);
        });

        btnSetPoeExeLocation.addActionListener(e -> showSetPoeExeLocationDialog());

        comboBoxVersion.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                PoeVersion selection = (PoeVersion) comboBoxVersion.getSelectedItem();
                settings.setSelectedPoeVersion(selection);
                setPoeExeComponentVisibilityByPoeVersion(selection);
                jsonSerializer.saveSettings();
            }
        });

        btnLaunch.addActionListener(e -> launchOpenAllExit());

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

        checkBoxEnableDarkMode.addActionListener(e -> {
            settings.setDarkModeEnabled(checkBoxEnableDarkMode.isSelected());
            jsonSerializer.saveSettings();
        });

        checkBoxEnableAhkSupport.addActionListener(e -> {
            boolean ahkSupportSelected = checkBoxEnableAhkSupport.isSelected();

            settings.setAhkSupportEnabled(ahkSupportSelected);
            setAHKComponentVisibility(ahkSupportSelected);

            if (!ahkSupportSelected) {
                applicationManager.getApplications().forEach(application -> {
                    if (application.getUri().toLowerCase().endsWith(".ahk"))
                        application.setEnabled(false);
                });
                tableTools.repaint();
                jsonSerializer.saveData();
            }

            jsonSerializer.saveSettings();
        });

        btnSetAhkExeLocation.addActionListener(e -> showSetAhkExeLocationDialog());

        checkBoxShowUpdateNotifications.addActionListener(e -> {
            settings.setUpdateNotificationsEnabled(checkBoxShowUpdateNotifications.isSelected());
            jsonSerializer.saveSettings();
        });

        btnCheckForUpdates.addActionListener(e -> {
            boolean newVersionAvailable = UpdateChecker.checkForNewVersion();

            if (newVersionAvailable)
                UpdateChecker.showNewVersionDialog(false);
            else
                JOptionPane.showMessageDialog(null, "LoE is up to date.",
                        "Launch of Exile - Update", JOptionPane.INFORMATION_MESSAGE);
        });

        tableTools.getModel().addTableModelListener(e -> {
            int row = e.getFirstRow();
            int column = e.getColumn();

            if (column == 0) {
                UriWrapper tool = modelTools.getUriWrapper(row);
                boolean isAhkFile = tool.getUri().toLowerCase().endsWith(".ahk");

                if (isAhkFile && !checkBoxEnableAhkSupport.isSelected() && tool.isEnabled()) {
                    tool.setEnabled(false);
                    JOptionPane.showMessageDialog(
                            null, """
                                    Failed to enable .ahk tool.

                                    Set "Enable .ahk support" checkbox to resolve.""",
                            "Launch of Exile - Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            tableTools.repaint();
            jsonSerializer.saveData();
        });

        tableWebsites.getModel().addTableModelListener(e -> {
            int row = e.getFirstRow();
            int column = e.getColumn();

            tableWebsites.repaint();
            jsonSerializer.saveData();
        });
    }

    public static void initialize() {
        settings = Settings.getSettings();
        applicationManager = new ApplicationManager();
        websiteManager = new WebsiteManager();
        jsonSerializer = new JsonSerializer(applicationManager, websiteManager);

        jsonSerializer.loadDataAndSettings();

        if (settings.isDarkModeEnabled()) FlatDarkLaf.setup();
        else FlatLightLaf.setup();

        UpdateChecker.startupCheckForNewVersion();

        if (skipLauncherEnabled) {
            launchOpenAllExit();
        }

        JFrame frame = new JFrame("Launch of Exile");
        frame.setContentPane(new LaunchOfExileMain().tabbedPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(false);
        frame.setSize(new Dimension(750, 550));
        frame.setLocationRelativeTo(null);
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(
                LaunchOfExileMain.class.getClassLoader().getResource("Launch_of_Exile.png")));
        frame.setVisible(true);

    }

    private static void launchOpenAllExit() {
        websiteManager.openAllEnabledWebsites();
        applicationManager.startAllEnabledApplications();
        applicationManager.startPoe(settings.getSelectedPoeVersion());
        System.exit(0);
    }

    private void setPoeExeComponentVisibilityByPoeVersion(PoeVersion poeVersion) {
        switch (Objects.requireNonNull(poeVersion)) {
            case STEAM -> setPoeExeComponentVisibility(false);
            case STANDALONE -> setPoeExeComponentVisibility(true);
        }
    }

    private void setPoeExeComponentVisibility(boolean visible) {
        lblPoeExeLocation.setVisible(visible);
        tfPoeExeLocation.setVisible(visible);
        btnSetPoeExeLocation.setVisible(visible);
    }

    private void setAHKComponentVisibility(boolean visible) {
        lblAhkExeLocation.setVisible(visible);
        tfAhkExeLocation.setVisible(visible);
        btnSetAhkExeLocation.setVisible(visible);
    }

    private void createJTablesAndModels() {
        UriWrapperTableCellRenderer renderer = new UriWrapperTableCellRenderer();
        BooleanUriWrapperTableCellRenderer booleanRenderer = new BooleanUriWrapperTableCellRenderer();

        modelTools = new UriWrapperTableModel(applicationManager.getApplications(),
                "Launch", "Name", "Path");

        tableTools.setModel(modelTools);
        tableTools.getTableHeader().setReorderingAllowed(false);
        tableTools.getTableHeader().setResizingAllowed(false);
        tableTools.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableTools.setDefaultRenderer(String.class, renderer);

        TableColumn toolsEnabledColumn = tableTools.getColumnModel().getColumn(0);
        toolsEnabledColumn.setCellRenderer(booleanRenderer);
        TableColumn toolsNameColumn = tableTools.getColumnModel().getColumn(1);
        TableColumn toolsPathColumn = tableTools.getColumnModel().getColumn(2);
        toolsEnabledColumn.setMaxWidth(50);
        toolsNameColumn.setMinWidth(150);
        toolsPathColumn.setMinWidth(300);

        modelWebsites = new UriWrapperTableModel(websiteManager.getWebsites(),
                "Launch", "Name", "URL");

        tableWebsites.setModel(modelWebsites);
        tableWebsites.getTableHeader().setReorderingAllowed(false);
        tableWebsites.getTableHeader().setResizingAllowed(false);
        tableWebsites.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableWebsites.setDefaultRenderer(String.class, renderer);

        TableColumn websitesEnabledColumn = tableWebsites.getColumnModel().getColumn(0);
        websitesEnabledColumn.setCellRenderer(booleanRenderer);
        TableColumn websitesNameColumn = tableWebsites.getColumnModel().getColumn(1);
        TableColumn websitesUrlColumn = tableWebsites.getColumnModel().getColumn(2);
        websitesEnabledColumn.setMaxWidth(50);
        websitesNameColumn.setMinWidth(150);
        websitesUrlColumn.setMinWidth(300);

    }

    private void renameUriWrapper(UriWrapperTableModel model, int modelRow, JTable table) {
        UriWrapper uriWrapper = model.getUriWrapper(modelRow);

        String newName;
        if (!uriWrapper.getName().isBlank() && uriWrapper.getName().length() <= 40)
            newName = JOptionPane.showInputDialog(null,
                    "Enter new name for \"" + uriWrapper.getName() + "\":",
                    "Launch of Exile - Rename", JOptionPane.QUESTION_MESSAGE);
        else
            newName = JOptionPane.showInputDialog(null, "Enter new name:",
                    "Launch of Exile - Rename", JOptionPane.QUESTION_MESSAGE);

        if (newName == null)
            return;

        model.setUriWrapperName(newName, modelRow);
        table.setRowSelectionInterval(modelRow, modelRow);
    }

    private void showSetPoeExeLocationDialog() {
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
    }

    private void showSetAhkExeLocationDialog() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Launch of Exile - Set AHK .exe location");
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileFilter(new FileNameExtensionFilter(".exe", "exe"));

        int returnValue = fc.showDialog(null, "Set location");

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            String exeLocation = fc.getSelectedFile().getAbsolutePath();
            tfAhkExeLocation.setText(exeLocation);
            settings.setAhkExeLocation(exeLocation);
            jsonSerializer.saveSettings();
        }
    }

    private void showAddWebsiteDialog() {
        JTextField tfUrl = new JTextField();
        JTextField tfName = new JTextField();

        tfUrl.addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent e) {
                JComponent component = e.getComponent();
                component.requestFocusInWindow();
                component.removeAncestorListener(this);
            }

            @Override
            public void ancestorRemoved(AncestorEvent e) {
            }

            @Override
            public void ancestorMoved(AncestorEvent e) {
            }
        });

        Object[] inputFields = {"URL:", tfUrl, "Name (optional):", tfName};

        int option = JOptionPane.showConfirmDialog(null, inputFields,
                "Launch of Exile - Add website", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (option != JOptionPane.OK_OPTION)
            return;

        String url = tfUrl.getText();

        if (url.isBlank())
            return;

        String name = tfName.getText();
        UriWrapper website = name.isBlank() ? new UriWrapper(url) : new UriWrapper(url, name);
        modelWebsites.addUriWrapper(website);
        tableWebsites.setRowSelectionInterval(modelWebsites.getRowCount() - 1, modelWebsites.getRowCount() - 1);
    }

    private void showAddToolDialog() {
        JFileChooser fc = createToolFileChooser("Launch of Exile - Add tool");
        int returnValue = fc.showDialog(null, "Add to tools");

        if (returnValue != JFileChooser.APPROVE_OPTION)
            return;

        String pathname = fc.getSelectedFile().getAbsolutePath();

        if (!toolExistsAndHasValidExtension(pathname))
            return;

        String name = JOptionPane.showInputDialog(null, "Set name (optional):",
                "Launch of Exile - Add tool", JOptionPane.QUESTION_MESSAGE);

        UriWrapper tool = name.isBlank() ? new UriWrapper(pathname) : new UriWrapper(pathname, name);
        modelTools.addUriWrapper(tool);
        tableTools.setRowSelectionInterval(modelTools.getRowCount() - 1, modelTools.getRowCount() - 1);
    }

    private void showChangeToolPathDialog(int modelRow) {
        UriWrapper tool = modelTools.getUriWrapper(modelRow);

        JFileChooser fc;
        if (!tool.getName().isBlank() && tool.getName().length() <= 40)
            fc = createToolFileChooser("Launch of Exile - Change path of \"" + tool.getName() + "\"");
        else
            fc = createToolFileChooser("Launch of Exile - Change tool path");

        int returnValue = fc.showDialog(null, "Change tool path");

        if (returnValue != JFileChooser.APPROVE_OPTION)
            return;

        String pathname = fc.getSelectedFile().getAbsolutePath();

        if (!toolExistsAndHasValidExtension(pathname))
            return;

        modelTools.setUriWrapperUri(pathname, modelRow);
        tableTools.setRowSelectionInterval(modelRow, modelRow);
    }

    private JFileChooser createToolFileChooser(String dialogTitle) {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle(dialogTitle);
        fc.setAcceptAllFileFilterUsed(false);

        if (settings.isAhkSupportEnabled())
            fc.setFileFilter(new FileNameExtensionFilter(".exe, .jar, .ahk", "exe", "jar", "ahk"));
        else
            fc.setFileFilter(new FileNameExtensionFilter(".exe, .jar", "exe", "jar"));

        return fc;
    }

    private boolean toolExistsAndHasValidExtension(String pathname) {
        Path path;

        try {
            path = Path.of(pathname);
        } catch (InvalidPathException e) {
            JOptionPane.showMessageDialog(null, "Invalid path.",
                    "Launch of Exile - Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (!path.toFile().exists()) {
            JOptionPane.showMessageDialog(null, "File doesn't exist.",
                    "Launch of Exile - Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        String[] validExtensions = {".exe", ".jar", ".ahk"};
        boolean extensionIsValid = ApplicationManager.extensionIsValid(pathname, validExtensions);

        if (!extensionIsValid) {
            JOptionPane.showMessageDialog(null, "File extension isn't valid.",
                    "Launch of Exile - Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    private void showChangeWebsiteUrlDialog(int modelRow) {
        UriWrapper website = modelWebsites.getUriWrapper(modelRow);

        String newUrl;
        if (!website.getName().isBlank() && website.getName().length() <= 40)
            newUrl = JOptionPane.showInputDialog(null,
                    "Enter new URL for \"" + website.getName() + "\":",
                    "Launch of Exile - Change website URL", JOptionPane.QUESTION_MESSAGE);
        else
            newUrl = JOptionPane.showInputDialog(null, "Enter new URL:",
                    "Launch of Exile - Change website URL", JOptionPane.QUESTION_MESSAGE);

        if (newUrl == null || newUrl.isBlank())
            return;

        modelWebsites.setUriWrapperUri(newUrl, modelRow);
        tableWebsites.setRowSelectionInterval(modelRow, modelRow);
    }

    private void addItemsToComboBoxVersion() {
        comboBoxVersion.addItem(PoeVersion.STEAM);
        comboBoxVersion.addItem(PoeVersion.STANDALONE);
    }

    public static void main(String[] args) {
        if (args.length > 0 && "-skiplauncher".equals(args[0]))
            skipLauncherEnabled = true;

        initialize();
    }

}
