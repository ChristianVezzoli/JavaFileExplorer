package fileExplorer;

public class Main {
    public static void main(String[] args) {
        System.setProperty("jdk.console", "java.base");
        Model model = new Model();
        Controller controller = new Controller(model);
        View view = new View(controller);
        model.setView(view);

        try {view.viewMain(); } catch (Exception e) {e.printStackTrace();}

        // exit
        System.exit(0);
    }
}