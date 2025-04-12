package fileExplorer;

import java.io.IOException;

public class Controller {

    private Model model;

    private Thread thread;

    public Controller(Model model) {
        this.model = model;
    }

    public void selectNextFile() {
        // interrupt old thread and wait for it to terminate
        thread.interrupt();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // issue new instruction
        thread = new Thread(() -> {
            model.selectNextFile();
        });
        thread.start();
    }

    public void selectPreviousFile() {
        // interrupt old thread and wait for it to terminate
        thread.interrupt();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // issue new instruction
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
        // interrupt old thread and wait for it to terminate
        thread.interrupt();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // issue new instruction
        thread = new Thread(() -> {
            model.goToSelectedDir();
        });
        thread.start();
    }

    public void goToParentDir() {
         // interrupt old thread and wait for it to terminate
        thread.interrupt();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // issue new instruction
        thread = new Thread(() -> {
            model.goToParentDir();
        });
        thread.start();
    }

    public void getFiles() {
        // interrupt old thread and wait for it to terminate
        thread.interrupt();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // issue new instruction
        thread = new Thread(() -> {
            model.getFiles();
        });
        thread.start();
    }

    public void openFileManager() throws IOException {
        // interrupt old thread and wait for it to terminate
        thread.interrupt();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // issue new instruction
        thread = new Thread(() -> {
            model.openFileManager();
        });
        thread.start();
    }
}
