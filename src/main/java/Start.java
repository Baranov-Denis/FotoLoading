import Controller.Controller;
import Model.Model;
import Viewer.Viewer;

import javax.swing.*;

public class Start {
    public static void main(String[] args) {
        Model appModel = new Model();
        Controller mainController = new Controller(appModel);
        Viewer appViewer = new Viewer(mainController);
        mainController.addViewer(appViewer);


        SwingUtilities.invokeLater(mainController::runApp);
    }
}
