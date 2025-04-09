package fileExplorer;

import com.googlecode.lanterna.terminal.swing.TerminalScrollController;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class Model {

    // access view
    private View view;

    // model attributes
    private File parentDirectory;
    private List<File> parentDirectoryFiles;
    private File currentDirectory;
    private List<File> currentDirectoryFiles;
    private File currentFile;

    // comparator
    Comparator<File> comparator;

    public Model() {
        parentDirectoryFiles = new ArrayList<>();
        currentDirectoryFiles = new ArrayList<>();
        currentDirectory = new File(System.getProperty("user.home"));
        parentDirectory = currentDirectory.getParentFile();
        // initialize comparator with name
        comparator = Comparator.comparing(File::getName);
        // get first currentFile
        try {
            currentFile = Arrays.stream(currentDirectory.listFiles()).sorted(comparator).toList().getFirst();
        } catch (NullPointerException e) {
            currentFile = null;
        }

    }

    public void setView(View view) {
        this.view = view;
    }

    public void loadFiles() {
        overwriteCurrentDirectoryFiles();
        overwriteParentDirectoryFiles();
    }

    public void overwriteParentDirectoryFiles() {

        if (parentDirectory == null) {
            parentDirectoryFiles.clear();
            this.sendParentDirectoryFilesToView();
            return;
        }

        // take new files
        File[] filesArray = parentDirectory.listFiles();
        assert filesArray != null;

        // clear old files
        parentDirectoryFiles.clear();
        parentDirectoryFiles.addAll(Arrays.stream(filesArray)
                // order by comparator
                .sorted(comparator)
                .toList());


        this.sendParentDirectoryFilesToView();
    }

    public void overwriteCurrentDirectoryFiles() {

        // take new files
        File[] filesArray = currentDirectory.listFiles();
        assert filesArray != null;


        // clear old files
        currentDirectoryFiles.clear();
        currentDirectoryFiles.addAll(Arrays.stream(filesArray)
                // order by comparator
                .sorted(comparator)
                .toList());

        this.sendCurrentDirectoryFilesToView();
    }

    /** transforms the current file into its next */
    public void selectNextFile() {
        if (currentDirectoryFiles.isEmpty())
            return;
        currentFile = currentDirectoryFiles.get((currentDirectoryFiles.indexOf(currentFile) + 1) % currentDirectoryFiles.size());

        // update view
        this.sendCurrentDirectoryFilesToView();
    }

    /** transforms the current file into its previous*/
    public void selectPreviousFile() {
        if (currentDirectoryFiles.isEmpty())
            return;
        currentFile = currentDirectoryFiles.get((currentDirectoryFiles.indexOf(currentFile) - 1 + currentDirectoryFiles.size()) % currentDirectoryFiles.size());

        // update view
        this.sendCurrentDirectoryFilesToView();
    }

    public void sendParentDirectoryFilesToView() {
        view.redrawParentDirectoryFiles(parentDirectoryFiles.stream().map(File::getName).collect(Collectors.toList()),
                parentDirectoryFiles.indexOf(currentDirectory));
    }

    public void sendCurrentDirectoryFilesToView() {
        view.redrawCurrentDirectoryFiles(currentDirectoryFiles.stream().map(File::getName).collect(Collectors.toList()),
                currentDirectoryFiles.indexOf(currentFile));
    }

    /** Send files, when requested by view */
    public void getFiles() {
        this.sendCurrentDirectoryFilesToView();
        this.sendParentDirectoryFilesToView();
    }

    public void goToSelectedDir() {
        // if the file is not a directory, do nothing
        if (currentFile == null || !currentFile.isDirectory())
            return;

        parentDirectory = currentDirectory;
        currentDirectory = currentFile;
        try {
            currentFile = Arrays.stream(currentFile.listFiles()).sorted(comparator).toList().getFirst();
        } catch (Exception e) {
            currentFile = null;
        }

        this.overwriteParentDirectoryFiles();
        this.overwriteCurrentDirectoryFiles();


    }

    public void goToParentDir() {
        // if I am at root, do nothing
        if (parentDirectory == null)
            return;

        currentFile = currentDirectory;
        currentDirectory = parentDirectory;
        parentDirectory = parentDirectory.getParentFile();

        this.overwriteCurrentDirectoryFiles();
        this.overwriteParentDirectoryFiles();
    }
}
