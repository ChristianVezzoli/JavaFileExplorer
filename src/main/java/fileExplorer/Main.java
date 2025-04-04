package fileExplorer;

import java.io.Console;

public class Main {
    public static void main(String[] args) {
        Model model = new Model();
        Controller controller = new Controller(model);
        View view = new View(controller);
        model.setView(view);

        try {view.viewMain(); } catch (Exception e) {e.printStackTrace();}
    }
}