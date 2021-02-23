package Controller;

import Model.Model;
import Viewer.Viewer;

import javax.swing.*;

public class Controller extends SwingWorker<Void, Void> {
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
        viewer.runMainPage();

    }


    //--------------------------------------------------------------------------------------------------
    public void runExecuteCopyPhoto() {
        model.setOperationContinues(true);
        viewer.setLoading(true);

        Thread modelThread = new Thread(model);
        modelThread.start();

        try{
            Thread.sleep(1);
        }catch (Exception e){}


        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                System.out.println(" --------------------------------------------------------  - " + model.getProcessOfDone());

                // Simulate doing something useful.
                viewer.setLoading(true);
                viewer.runMainPage();

                updateProgressBar();

                return null;
            }
            protected void done(){
              model.setNumberOfDonePhotos(0);
            }
        }.execute();


        //  viewer.runMainPage();


    }

    //-------------------------------------------------------------------------------------------------
    public void setSourcePath(String sourcePath, String destination) {
        model.setSourcePath(sourcePath);
        model.setDestinationPath(destination);
        model.writeSettings(sourcePath, destination);
        model.readSettings();
        viewer.setSourcePath(model.getSourcePath());
        viewer.setDestinationPath(model.getDestinationPath());

        viewer.runMainPage();
    }

    @Override
    protected void done() {


    }

    private void updateProgressBar() {
        viewer.setLoading(true);
        viewer.runMainPage();

        try {

            while (viewer.isLoading()) {
                if (model.getProcessOfDone() == 100) viewer.setLoading(false);

                viewer.setProgressBarValue(model.getProcessOfDone());

                viewer.runMainPage();
                Thread.sleep(500);
            }
            model.setNumberOfDonePhotos(0);
        } catch (Exception t) {

        }
    }

    @Override
    protected Void doInBackground() throws Exception {
        updateProgressBar();
        return null;
    }

    public void chancelProcess() {
        model.setOperationContinues(false);
        viewer.setLoading(false);

        SwingUtilities.invokeLater(viewer::runMainPage);
    }
}
