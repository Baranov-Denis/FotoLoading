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

    public void setDestinationPath(String destinationPath) {
        this.destinationPath = destinationPath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public Viewer(Controller controller) {
        this.controller = controller;
    }

    public void runMainPage(){
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(340, 340);
        frame.setTitle("PhotoLoader");
        frame.setLocation(1300, 300);
        frame.setLayout(new FlowLayout());
        frame.setAlwaysOnTop(true);
        frame.add(panel);
        panel.setPreferredSize(new Dimension(340, 585));
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

        MyButton executeCopyPhoto = new MyButton("Start Copying Photo");
        executeCopyPhoto.addActionListener(e -> controller.runExecuteCopyPhoto());



        panel.add(textAboveSourceButton);
        panel.add(changeSourcePath);
        panel.add(textAboveDestinationButton);
        panel.add(changeDestinationPath);
        panel.add(executeCopyPhoto);
        SwingUtilities.updateComponentTreeUI(frame);
    }


}
