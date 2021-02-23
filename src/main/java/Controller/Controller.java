package Controller;

import Model.Model;
import Viewer.Viewer;

import javax.swing.*;

public class Controller {
    private final Model model;
    private Viewer viewer;


    public Controller(Model model) {
        this.model = model;
    }

    public void addViewer(Viewer viewer) {
        this.viewer = viewer;
    }

    public void runApp() {
        model.readSettings();
        viewer.setSourcePath(model.getSourcePath());
        viewer.setDestinationPath(model.getDestinationPath());
        SwingUtilities.invokeLater(viewer::runMainPage);
//        viewer.runMainPage();

    }

    //------------------------------------------- Run App ----------------------------------------------
    //--------------------------------------------------------------------------------------------------

    public void runExecuteCopyPhoto() {

        model.setOperationContinues(true);
        viewer.setLoading(true);

        Thread modelThread = new Thread(model);
        modelThread.start();

        sleepThread(10);


        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                updateProgressBar();
                return null;
            }

            protected void done() {
                model.setNumberOfDonePhotos(0);
            }
        }.execute();

    }

    //-------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------


    public void setSourcePath(String sourcePath, String destination) {
        model.setSourcePath(sourcePath);
        model.setDestinationPath(destination);
        model.writeSettings(sourcePath, destination);
        model.readSettings();
        viewer.setSourcePath(model.getSourcePath());
        viewer.setDestinationPath(model.getDestinationPath());
        SwingUtilities.invokeLater(viewer::runMainPage);
    }


    private void updateProgressBar() {
            while (viewer.isLoading()) {
                if (model.getProcessOfDone() == 100) viewer.setLoading(false);
                viewer.setProgressBarValue(model.getProcessOfDone());
                SwingUtilities.invokeLater(viewer::runMainPage);
                sleepThread(500);
            }
    }


    private void sleepThread(int millis){
        try{
            Thread.sleep(millis);
        }catch (Exception e){
            System.out.println("Error while Thread sleep");
        }
    }


    public void chancelProcess() {
        model.setOperationContinues(false);
        viewer.setLoading(false);
        SwingUtilities.invokeLater(viewer::runMainPage);
    }
}
