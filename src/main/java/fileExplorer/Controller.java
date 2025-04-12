package fileExplorer;

import java.io.IOException;

public class Controller {

    private Model model;

    private Thread thread;

    public Controller(Model model) {
        this.model = model;
    }

    public void selectNextFile() {
        thread.interrupt();
        thread = new Thread(() -> {
            model.selectNextFile();
        });
        thread.start();
    }

    public void selectPreviousFile() {
        thread.interrupt();
        thread = new Thread(() -> {
            model.selectPreviousFile();
        });
        thread.start();
    }

    public void startingLoadFiles() {
        thread = new Thread(() -> {
            model.loadFiles();
        });
        thread.start();
    }

    public void goToSelectedDir() {
        thread.interrupt();
        thread = new Thread(() -> {
            model.goToSelectedDir();
        });
        thread.start();
    }

    public void goToParentDir() {
        thread.interrupt();
        thread = new Thread(() -> {
            model.goToParentDir();
        });
        thread.start();
    }

    public void getFiles() {
        thread.interrupt();
        thread = new Thread(() -> {
            model.getFiles();
        });
        thread.start();
    }

    public void openFileManager() throws IOException {
        thread.interrupt();
        thread = new Thread(() -> {
            model.openFileManager();
        });
        thread.start();
    }
}
