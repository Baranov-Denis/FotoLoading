package Viewer;

import Controller.Controller;

import javax.swing.*;
import java.awt.*;

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


        MyButton changeSourcePath = new MyButton(sourcePath);
        MyButton changeDestinationPath = new MyButton(destinationPath);

        MyButton executeCopyPhoto = new MyButton("Start Copying Photo");
        executeCopyPhoto.addActionListener(e -> controller.runExecuteCopyPhoto());



        panel.add(changeSourcePath);
        panel.add(changeDestinationPath);
        panel.add(executeCopyPhoto);
        SwingUtilities.updateComponentTreeUI(frame);


    }
}
