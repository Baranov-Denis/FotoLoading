package Viewer;

import Controller.Controller;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

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


    public Viewer(Controller controller) {
        this.controller = controller;
        setFrameLocation();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(346, 415);
        frame.setTitle("PhotoLoader");

        frame.setLayout(new FlowLayout());
        frame.setAlwaysOnTop(false);
        frame.add(panel);

        panel.setPreferredSize(new Dimension(340, 415));
        panel.setBackground(MyColors.BACKGROUND);
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

/***
 *New
 *
 */
        JMenuBar menuBar = new JMenuBar();

        menuBar.setPreferredSize(new Dimension(330, 30));
        menuBar.setBackground(MyColors.BUTTON_COLOR);
        menuBar.setMargin(new Insets(-5, 0, 0, 0));
        menuBar.setForeground(MyColors.FONT);
        menuBar.setBorder(new BevelBorder(BevelBorder.RAISED));
        JMenu menu = new JMenu("Menu");
        menu.setForeground(MyColors.FONT);
        menu.setCursor(new Cursor(Cursor.HAND_CURSOR));
  


        JMenuItem load = new JMenuItem("Load");
        menuBar.add(menu);
        menu.add(load);
/***
 *
 *
 */

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

        /***
         *
         * New
         */
        panel.add(menuBar);
        /***
         *
         *
         */
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

}
