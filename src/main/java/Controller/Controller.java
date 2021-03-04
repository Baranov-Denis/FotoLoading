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
        viewer.setSourcePath(model.getSourcePathName());
        viewer.setDestinationPath(Model.getDestinationPathName());
        viewer.setMessage(model.getMessageToViewer());
        viewer.setCopySelected(model.isCopySelected());
        SwingUtilities.invokeLater(viewer::runMainPage);
    }

    //------------------------------------------- Run App ----------------------------------------------
    //--------------------------------------------------------------------------------------------------

    public void runExecuteCopyPhoto() {

        model.setCopyingContinues(true);
        viewer.setLoading(true);
        viewer.setMessage("");
        viewer.setCopySelected(model.isCopySelected());


        Thread modelThread = new Thread(model);
        modelThread.start();
       try {
            Thread.sleep(10);
        } catch (Exception empty) {
        }


        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                updateProgressBar();
                return null;
            }

            protected void done() {
               // model.setCopiedFilesSize(0);
                viewer.setLoading(false);
                viewer.setMessage(model.getMessageToViewer());

                SwingUtilities.invokeLater(viewer::runMainPage);

            }
        }.execute();

    }

    //-------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------

    private boolean isCopyingDone(){
       return model.getPercentOfDone() == 100;
    }


    public void setSourcePath(String sourcePath, String destination) {
        model.setSourcePathName(sourcePath);
        Model.setDestinationPathName(destination);
        model.writeSettings(sourcePath, destination);
        model.readSettings();
        viewer.setSourcePath(model.getSourcePathName());
        viewer.setDestinationPath(Model.getDestinationPathName());
        SwingUtilities.invokeLater(viewer::runMainPage);
    }


    private void updateProgressBar() {
        while (viewer.isLoading()) {
            if (isCopyingDone()) viewer.setLoading(false);
            viewer.setProgressBarValue(model.getPercentOfDone());
            SwingUtilities.invokeLater(viewer::runMainPage);
            sleepThread(500);
        }
    }


    private void sleepThread(int millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception e) {
            System.out.println("Error while Thread sleep");
        }
    }


    public void chancelProcess() {
        model.setCopyingContinues(false);
        viewer.setLoading(false);
        SwingUtilities.invokeLater(viewer::runMainPage);

    }

    public void setCopySelected(boolean b) {
        model.setCopySelected(b);
viewer.setCopySelected(b);
        viewer.runMainPage();
    }
}
