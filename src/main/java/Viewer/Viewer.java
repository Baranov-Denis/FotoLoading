package Viewer;

import Controller.Controller;
import Model.PathPreset;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;

public class Viewer extends JFrame implements KeyListener {
    static JFrame frame = new JFrame();
    static JPanel panel = new JPanel();
    final Controller controller;
    public String message = "Ready";
    private String sourcePath;
    private String destinationPath;
    private boolean loading;
    private int progressBarValue;
    private boolean copySelected;
    private ArrayList<PathPreset> listOfPresets;
    private String currentPreset;

    public void setCurrentPreset(String currentPreset) {
        this.currentPreset = currentPreset;
    }

    public Viewer(Controller controller) {
        this.controller = controller;
        listOfPresets = controller.getListOfPresets();


        setFrameLocation();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(346, 410);
        frame.setTitle("PhotoLoader");

        frame.setAlwaysOnTop(false);
        frame.setResizable(false);
        //size of frame won't be changed
        frame.add(panel);

        panel.setPreferredSize(new Dimension(340, 415));
        panel.setBackground(MyColors.BACKGROUND);
    }

    public void setListOfPresets(ArrayList<PathPreset> listOfPresets) {
        this.listOfPresets = listOfPresets;
    }

    public void setCopySelected(boolean copySelected) {
        this.copySelected = copySelected;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setProgressBarValue(int progressBarValue) {
        this.progressBarValue = progressBarValue;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public void setDestinationPath(String destinationPath) {
        this.destinationPath = destinationPath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    private void setFrameLocation() {
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;
        frame.setLocation((screenWidth - 360), (screenHeight - 450));

    }

    public void runMainPage() {

        panel.removeAll();

        ImageIcon img = new ImageIcon("H:\\FotoLoading\\photo.png");
        frame.setIconImage(img.getImage());
        currentPreset = listOfPresets.get(0).getPresetName();

        JMenuBar menuBar = new JMenuBar();

        menuBar.setBackground(MyColors.BUTTON_COLOR);
        menuBar.setPreferredSize(new Dimension(330, 30));
        menuBar.setBorder(new BevelBorder(BevelBorder.RAISED));

        JMenu menu = new JMenu("Menu");
        menu.setForeground(MyColors.FONT);
        menu.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JMenu presets = new JMenu("Presets");
        presets.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JMenuItem saveCurrentPreset = new JMenuItem("Save current preset");
        saveCurrentPreset.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JMenuItem deleteCurrentPreset = new JMenuItem("Delete current preset");
        deleteCurrentPreset.setCursor(new Cursor(Cursor.HAND_CURSOR));

        saveCurrentPreset.addActionListener(this::savingCurrentPreset);

        deleteCurrentPreset.addActionListener(this::deletingCurrentPreset);

        menuBar.add(menu);
        menu.add(presets);
        menu.add(saveCurrentPreset);
        menu.add(deleteCurrentPreset);


        createListOfPresets(presets);






        JLabel textAboveSourceButton = new JLabel("Choose source path:");


        MyButton changeSourcePath = new MyButton(sourcePath);
        changeSourcePath.addActionListener(this::chooseSourcePath);


        JLabel textAboveDestinationButton = new JLabel("Choose destination path:");

        MyButton changeDestinationPath = new MyButton(destinationPath);
        changeDestinationPath.addActionListener(this::chooseDestinationPath);

        JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
        messageLabel.setPreferredSize(new Dimension(320, 41));
        messageLabel.setBackground(MyColors.FONT);
        messageLabel.setFont(new Font("San-Serif", Font.BOLD, 20));

        ButtonGroup buttonGroup = new ButtonGroup();


        JRadioButton copyRadioButton = new MyRadioButton("Copy Photos");
        copyRadioButton.addActionListener(e -> controller.setCopySelected(true));

        JRadioButton moveRadioButton = new MyRadioButton("Move Photos");
        moveRadioButton.addActionListener(e -> controller.setCopySelected(false));


        buttonGroup.add(copyRadioButton);
        buttonGroup.add(moveRadioButton);


        MyButton executeCopyPhotoButton = new MyButton("Start Copying Photo");
        executeCopyPhotoButton.setPreferredSize(new Dimension(320, 100));
        executeCopyPhotoButton.addActionListener(e -> controller.runExecuteCopyPhoto());

        MyButton chancelButton = new MyButton("Chancel");
        chancelButton.setPreferredSize(new Dimension(320, 100));
        chancelButton.addActionListener(e -> controller.chancelProcess());

        JProgressBar progressBar = new JProgressBar();
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(320, 41));
        progressBar.setBackground(MyColors.FONT);
        progressBar.setForeground(MyColors.BUTTON_COLOR);
        progressBar.setBorderPainted(false);
        progressBar.setFont(new Font("San-Serif", Font.BOLD, 20));

        if (copySelected) {
            copyRadioButton.setSelected(true);
            executeCopyPhotoButton.setText("Start Copying Photo");
        } else {
            moveRadioButton.setSelected(true);
            executeCopyPhotoButton.setText("Start Moving Photo");
        }


        frame.setFocusable(true);
        frame.addKeyListener(this);

        panel.setFocusable(false);
        changeSourcePath.setFocusable(false);
        changeDestinationPath.setFocusable(false);

        frame.setJMenuBar(menuBar);

        panel.add(textAboveSourceButton);
        panel.add(changeSourcePath);
        panel.add(textAboveDestinationButton);
        panel.add(changeDestinationPath);
        panel.add(copyRadioButton);
        panel.add(moveRadioButton);

        if (loading) {
            progressBar.setValue(progressBarValue);
            panel.add(progressBar);
            panel.add(chancelButton);
        } else {
            panel.add(messageLabel);
            panel.add(executeCopyPhotoButton);
        }

        SwingUtilities.updateComponentTreeUI(frame);
    }


    private void deletingCurrentPreset(ActionEvent e){
        JFrame frame = new JFrame("Confirm deleting");
        frame.setSize(342, 180);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        JPanel panel = new JPanel();
        panel.setBackground(MyColors.BACKGROUND);
        JLabel label =
                new JLabel("<html><div style=\"text-align: center\">Do you want to delete <br>\""+ currentPreset +
                "\"?</div></html>");
        label.setFont(MyFonts.myFont);
        frame.add(panel);
        panel.add(label);

        MyButton yes = new MyButton("Yes");
        MyButton chancel = new MyButton("Chancel");


        panel.add(yes);
        panel.add(chancel);
        yes.addActionListener(r->{
            if(!currentPreset.equals("Default")) {
                controller.deleteCurrentPreset(currentPreset);
            }
            frame.dispose();
        });
        chancel.addActionListener(t->{
            frame.dispose();
        });
    }


    private void savingCurrentPreset(ActionEvent e){
        JFrame frame = new JFrame("Enter Preset Name:");
        frame.setSize(350, 170);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        JPanel panel = new JPanel();
        panel.setBackground(MyColors.BACKGROUND);
        JTextField fieldForName = new JTextField();
        fieldForName.setFont(MyFonts.myFont);
        fieldForName.setBackground(MyColors.BACKGROUND);
        fieldForName.setBorder(new BevelBorder(BevelBorder.LOWERED));
        fieldForName.setFocusable(true);
        fieldForName.setPreferredSize(new Dimension(320,30));
        MyButton enter = new MyButton("Save");
        MyButton chancel = new MyButton("Chancel");
        frame.add(panel);
        panel.add(fieldForName);
        panel.add(enter);
        panel.add(chancel);
        enter.addActionListener(r->{
            String  nameForPreset = fieldForName.getText();
            controller.saveCurrentPreset(nameForPreset);
            currentPreset = nameForPreset;
            frame.dispose();
        });
        chancel.addActionListener(t->{
            frame.dispose();
        });

    }


    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!loading) {
                controller.runExecuteCopyPhoto();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }


    private void chooseSourcePath(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(sourcePath));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        int temp = chooser.showDialog(panel, "Choose input Directory");
        if (temp == JFileChooser.APPROVE_OPTION) {
            File directory = chooser.getSelectedFile();
            String newSourcePath = directory.toString();
            controller.setSourcePath(newSourcePath, destinationPath);
        }
    }

    private void chooseDestinationPath(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(destinationPath));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        int temp = chooser.showDialog(panel, "Choose output Directory");
        if (temp == JFileChooser.APPROVE_OPTION) {
            File directory = chooser.getSelectedFile();
            String newDestinationPath = directory.toString();
            controller.setSourcePath(sourcePath, newDestinationPath);
        }
    }

    private void createListOfPresets(JMenu presets){
        for (PathPreset preset : listOfPresets){
            if(!preset.getPresetName().equals("Default")) {
               JMenuItem presetName = new JMenuItem(preset.getPresetName());
                presets.add(presetName);
                presetName.addActionListener(e -> {
                    controller.loadPreset(preset.getPresetName());
                    setCurrentPreset(preset.getPresetName());
                });

            }
        }
    }

}
