package Controller;

import Model.Model;
import Viewer.Viewer;

public class p implements Runnable{
    private Viewer viewer;
    private Model model;

    public p(Viewer viewer, Model model) {
        this.viewer = viewer;
        this.model = model;

    }

    @Override
    public void run() {

        try{
          //  System.out.println("==============================================================================" +
            //  viewer.isLoading());
            while(viewer.isLoading()) {
                System.out.println("==============================================================================" + viewer.isLoading());
                System.out.println("------------" + model.getProcessOfDone() + "    " + viewer.isLoading());
                viewer.setProgressBarValue(model.getProcessOfDone());
                Thread.sleep(100);
                viewer.runMainPage();

            }
        }catch (Exception t){

        }
    }
}
