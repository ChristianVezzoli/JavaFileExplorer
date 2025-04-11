package fileExplorer;

public class Main {
    public static void main(String[] args) {
        System.setProperty("jdk.console", "java.base");
        Model model = new Model();
        Controller controller = new Controller(model);

        // if config fails, abort
        try {
            View view = new View(controller);
            model.setView(view);
            try {view.viewMain(); } catch (Exception e) {e.printStackTrace();}
        } catch (Exception e) {
            System.out.println("Error in Config. Aborting...");
        }

        // exit
        System.exit(0);
    }
}