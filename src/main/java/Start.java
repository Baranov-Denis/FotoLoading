import Controller.Controller;
import Model.Model;
import Viewer.Viewer;

public class Start {
    public static void main(String[] args) {
        Model appModel = new Model();
        Controller mainController = new Controller(appModel);
        Viewer appViewer = new Viewer(mainController);
        mainController.addViewer(appViewer);

        mainController.runApp();
    }
}
