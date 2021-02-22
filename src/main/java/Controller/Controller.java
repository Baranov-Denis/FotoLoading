package Controller;

import Model.Model;
import Viewer.Viewer;

import javax.swing.*;

public class Controller extends SwingWorker<Integer, Integer> {
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

            Thread modelThread =  new Thread(model);
            modelThread.start();
            //Thread b = new Thread(new p(viewer, model));
           // b.start();
     //   new p(viewer, model).run();
       //Thread c = new Thread(viewer);
      // c.start();

//updateProgressBar();
             execute();
          //  viewer.runMainPage();





    }
//-------------------------------------------------------------------------------------------------
    public void setSourcePath(String sourcePath,String destination) {
        model.setSourcePath(sourcePath);
        model.setDestinationPath(destination);
        model.writeSettings(sourcePath,destination);
        model.readSettings();
        viewer.setSourcePath(model.getSourcePath());
        viewer.setDestinationPath(model.getDestinationPath());

        viewer.runMainPage();
    }

    @Override
    protected void done() {


    }

    private void updateProgressBar(){
viewer.setLoading(true);
try{
    while(viewer.isLoading()) {
        if(model.getProcessOfDone() == 100) viewer.setLoading(false);

       viewer.setProgressBarValue(model.getProcessOfDone());
        Thread.sleep(10);
        SwingUtilities.invokeLater(viewer::runMainPage);
      //  viewer.runMainPage();

    }
}catch (Exception t){

}
    }

    @Override
    protected Integer doInBackground() throws Exception {
updateProgressBar();
        return null;
    }

    public void chancelProcess() {
        model.setOperationContinues(false);
        viewer.setLoading(false);

        SwingUtilities.invokeLater(viewer::runMainPage);
    }
}
