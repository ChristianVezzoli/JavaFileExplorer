package fileExplorer;

import java.io.IOException;

public class Controller {

    private Model model;

    public Controller(Model model) {
        this.model = model;
    }

    public void selectNextFile() {
        model.selectNextFile();
    }

    public void selectPreviousFile() {
        model.selectPreviousFile();
    }

    public void startingLoadFiles() {
        model.loadFiles();
    }

    public void goToSelectedDir() {
        model.goToSelectedDir();
    }

    public void goToParentDir() {
        model.goToParentDir();
    }

    public void getFiles() {
        model.getFiles();
    }

    public void openFileManager() throws IOException {
        model.openFileManager();
    }
}
