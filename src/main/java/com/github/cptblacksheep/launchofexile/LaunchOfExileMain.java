package com.github.cptblacksheep.launchofexile;

import com.formdev.flatlaf.FlatLightLaf;
import com.github.cptblacksheep.launchofexile.datamanagement.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.Objects;

public class LaunchOfExileMain {
    private final ApplicationManager applicationManager;
    private final WebsiteManager websiteManager;
    private final JsonSerializer jsonSerializer;
    private JPanel panelMain;
    private JList<UriWrapper> listTools;
    private final DefaultListModel<UriWrapper> modelTools;
    private JList<UriWrapper> listWebsites;
    private final DefaultListModel<UriWrapper> modelWebsites;
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

    private LaunchOfExileMain() {
        modelTools = new DefaultListModel<>();
        modelWebsites = new DefaultListModel<>();
        listTools.setModel(modelTools);
        listWebsites.setModel(modelWebsites);

        applicationManager = new ApplicationManager();
        websiteManager = new WebsiteManager();
        jsonSerializer = new JsonSerializer(applicationManager, websiteManager);

        comboBoxVersion.addItem(PoeVersion.STEAM);
        comboBoxVersion.addItem(PoeVersion.STANDALONE);

        loadData();

        btnAddTool.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Add tool");
            fc.setAcceptAllFileFilterUsed(false);
            fc.setFileFilter(new FileNameExtensionFilter(".exe", "exe"));

            int returnValue = fc.showDialog(null, "Add to tools");

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                String toolPath = fc.getSelectedFile().getAbsolutePath();
                UriWrapper tool = new UriWrapper(toolPath);
                modelTools.addElement(tool);
                applicationManager.addApplication(tool);
                jsonSerializer.saveData();
            }
        });

        btnRemoveTool.addActionListener(e -> {
            UriWrapper tool = listTools.getSelectedValue();

            if (tool != null) {
                modelTools.removeElement(tool);
                applicationManager.removeApplication(tool);
                jsonSerializer.saveData();
            }
        });

        btnAddWebsite.addActionListener(e -> {
            String websiteUrl = JOptionPane.showInputDialog("Insert URL to add:");

            if (!websiteUrl.isBlank()) {
                UriWrapper website = new UriWrapper(websiteUrl);
                modelWebsites.addElement(website);
                websiteManager.addWebsite(website);
                jsonSerializer.saveData();
            }
        });

        btnRemoveWebsite.addActionListener(e -> {
            UriWrapper website = listWebsites.getSelectedValue();

            if (website != null) {
                modelWebsites.removeElement(website);
                websiteManager.removeWebsite(website);
                jsonSerializer.saveData();
            }
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
                setElementVisibilityBasedOnComboBoxVersion();
                jsonSerializer.saveData();
            }
        });

        btnLaunch.addActionListener(e -> {
            websiteManager.openAllWebsites();
            applicationManager.startAllApplications();
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
        modelTools.addAll(applicationManager.getApplications());
        modelWebsites.addAll(websiteManager.getWebsites());
        tfPoeExeLocation.setText(applicationManager.getPoeExeLocation());
        comboBoxVersion.setSelectedItem(applicationManager.getSelectedPoeVersion());
        setElementVisibilityBasedOnComboBoxVersion();
    }

    private void setElementVisibilityBasedOnComboBoxVersion() {
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

    public static void main(String[] args) {
        initialize();
    }

}
