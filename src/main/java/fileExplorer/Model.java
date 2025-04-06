package fileExplorer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Model {

    // access view
    private View view;

    private File currentDirectory;
    private File parentDirectory;
    private List<File> currentDirFiles;
    private int currentFileIndex;

    public Model() {
        currentDirFiles = new ArrayList<>();
        currentDirectory = new File(System.getProperty("user.home"));
        parentDirectory = currentDirectory.getParentFile();
    }

    public void setView(View view) {
        this.view = view;
    }

    public void loadCurrentDirectoryFiles() {

        // if file is not directory or is root, do nothing
        File[] filesArr = currentDirectory.listFiles();
        if (filesArr == null) {
            return;
        }

        currentDirFiles.clear();
        currentDirFiles.addAll(Arrays.stream(filesArr)
                // order by name
                .sorted(Comparator.comparing(File::getName))
                .toList());

        this.sendFilesToView();
    }

    /** Calls view to draw the currentDirFiles */
    public void sendFilesToView() {
        view.drawFiles(currentDirFiles.stream().map(File::getName).toList(), currentFileIndex);
    }

    public void selectNextFile() {
        if (currentDirFiles.isEmpty())
            return;
        currentFileIndex = (currentFileIndex + 1) % currentDirFiles.size();
        this.sendFilesToView();
    }

    public void selectPreviousFile() {
        if (currentDirFiles.isEmpty())
            return;
        currentFileIndex = (currentFileIndex - 1 + currentDirFiles.size()) % currentDirFiles.size();
        this.sendFilesToView();
    }

    public void goToSelectedDir() {
        if (currentDirFiles.isEmpty())
            return;
        parentDirectory = currentDirectory;
        currentDirectory = currentDirFiles.get(currentFileIndex);
        currentFileIndex = 0;
        loadCurrentDirectoryFiles();
    }

    public void goToParentDir() {
        if (parentDirectory == null)
            return;
        currentDirectory = parentDirectory;
        parentDirectory = parentDirectory.getParentFile();
        currentFileIndex = 0;
        loadCurrentDirectoryFiles();
    }
}
