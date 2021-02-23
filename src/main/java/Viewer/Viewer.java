package Viewer;

import Controller.Controller;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class Viewer extends JFrame {

    static JFrame frame = new JFrame();
    static JPanel panel = new JPanel();
    final Controller controller;

    private String sourcePath;
    private String destinationPath;
    public String message = "          ";
    private boolean loading;
    private int progressBarValue;

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

    public Viewer(Controller controller) {
        this.controller = controller;
        setFrameLocation();
    }

    private void setFrameLocation(){
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;
        frame.setLocation((screenWidth/2)-(340/2), (screenHeight/2)-(293));
    }

    public void runMainPage(){
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(346, 335);
        frame.setTitle("PhotoLoader");

        frame.setLayout(new FlowLayout());
        frame.setAlwaysOnTop(true);
        frame.add(panel);

        panel.setPreferredSize(new Dimension(340, 586));
        panel.setBackground(MyColors.BACKGROUND);
        panel.removeAll();



        JLabel textAboveSourceButton = new JLabel("Choose source path:");


        MyButton changeSourcePath = new MyButton(sourcePath);
        changeSourcePath.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File(sourcePath));
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);
            int temp = chooser.showDialog(panel, "Choose input Directory");
            if (temp == JFileChooser.APPROVE_OPTION) {
                File directory = chooser.getSelectedFile();
                String newSourcePath = directory.toString();
                controller.setSourcePath(newSourcePath,destinationPath);
            }
        });



        JLabel textAboveDestinationButton = new JLabel("Choose destination path:");

        MyButton changeDestinationPath = new MyButton(destinationPath);
        changeDestinationPath.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File(destinationPath));
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);
            int temp = chooser.showDialog(panel, "Choose output Directory");
            if (temp == JFileChooser.APPROVE_OPTION) {
                File directory = chooser.getSelectedFile();
                String newDestinationPath = directory.toString();
                controller.setSourcePath(sourcePath,newDestinationPath);
            }
        });

        JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
        messageLabel.setPreferredSize(new Dimension(320,41));


        MyButton executeCopyPhotoButton = new MyButton("Start Copying Photo");
        executeCopyPhotoButton.setPreferredSize(new Dimension(320,100));
        executeCopyPhotoButton.addActionListener(e -> controller.runExecuteCopyPhoto());

        MyButton chancelButton = new MyButton("Chancel");
        chancelButton.setPreferredSize(new Dimension(320,100));
        chancelButton.addActionListener(e -> controller.chancelProcess());

        JProgressBar progressBar = new JProgressBar();
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(320, 41));
        progressBar.setBackground(MyColors.BACKGROUND);
        progressBar.setForeground(MyColors.BUTTON_COLOR);
        progressBar.setBorderPainted(false);
        progressBar.setFont(new Font("San-Serif" ,Font.BOLD,20));

        panel.add(textAboveSourceButton);
        panel.add(changeSourcePath);
        panel.add(textAboveDestinationButton);
        panel.add(changeDestinationPath);
        if(loading){
            progressBar.setValue(progressBarValue);
            panel.remove(executeCopyPhotoButton);
            panel.add(progressBar);
            panel.add(chancelButton);
        }else {
            panel.add(messageLabel);
            panel.add(executeCopyPhotoButton);
        }
        
        SwingUtilities.updateComponentTreeUI(frame);
    }




}
