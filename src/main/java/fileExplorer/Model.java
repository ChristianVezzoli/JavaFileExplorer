package fileExplorer;

import java.io.File;
import java.io.IOException;
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
    private String currentFileContents;

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
            Scanner scanner = new Scanner(currentFile);
            while (scanner.hasNextLine()) {
                currentFileContents += scanner.nextLine();
            }

        } catch (Exception e) {
            currentFile = null;
            currentFileContents = "CAN'T READ FILE";
        }

    }

    public void setView(View view) {
        this.view = view;
    }

    public void loadFiles() {
        overwriteCurrentDirectoryFiles();
        overwriteParentDirectoryFiles();
        overwriteCurrentFileContents();
    }

    private void overwriteParentDirectoryFiles() {

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

    private void overwriteCurrentDirectoryFiles() {

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

    private void overwriteCurrentFileContents() {
        currentFileContents = "";
        if (currentFile != null) {
            if(currentFile.isFile()) {
                try {
                    Scanner scanner = new Scanner(currentFile);
                    while (scanner.hasNextLine()) {
                        currentFileContents += (scanner.nextLine() + "\n");
                    }
                } catch (Exception e) {
                    currentFileContents = "CAN'T READ FILE";
                }
            } else if (currentFile.isDirectory()) {
                if (currentFile.canRead()) {
                    currentFileContents = Arrays.stream(currentFile.listFiles())
                            .sorted(comparator)
                            .map(File::getName)
                            .reduce("", (a, b) -> a + "\n" + b);
                } else {
                    currentFileContents = "CAN'T READ DIRECTORY";
                }
            }
        }
        this.sendCurrentFileContentsToView();
    }

    /** transforms the current file into its next */
    public void selectNextFile() {
        if (currentDirectoryFiles.isEmpty())
            return;
        currentFile = currentDirectoryFiles.get((currentDirectoryFiles.indexOf(currentFile) + 1) % currentDirectoryFiles.size());

        this.overwriteCurrentFileContents();

        // update view
        this.sendCurrentDirectoryFilesToView();
    }

    /** transforms the current file into its previous*/
    public void selectPreviousFile() {
        if (currentDirectoryFiles.isEmpty())
            return;
        currentFile = currentDirectoryFiles.get((currentDirectoryFiles.indexOf(currentFile) - 1 + currentDirectoryFiles.size()) % currentDirectoryFiles.size());

        this.overwriteCurrentFileContents();

        // update view
        this.sendCurrentDirectoryFilesToView();
    }

    public void sendParentDirectoryFilesToView() {
        view.redrawParentDirectoryFiles(parentDirectoryFiles.stream()
                        .map(file -> new ViewFilePair(file.getName(), file.isDirectory()))
                        .collect(Collectors.toList()),
                parentDirectoryFiles.indexOf(currentDirectory));
    }

    public void sendCurrentDirectoryFilesToView() {
        view.redrawCurrentDirectoryFiles(currentDirectoryFiles.stream()
                        .map(file -> new ViewFilePair(file.getName(), file.isDirectory()))
                        .collect(Collectors.toList()),
                currentDirectoryFiles.indexOf(currentFile));
    }

    public void sendCurrentFileContentsToView() {
        view.redrawCurrentFileContents(currentFileContents);
    }

    /** Send files, when requested by view */
    public void getFiles() {
        this.sendCurrentDirectoryFilesToView();
        this.sendParentDirectoryFilesToView();
        this.sendCurrentFileContentsToView();
    }

    public void goToSelectedDir() {
        // if the file is not a directory or can't read it, do nothing
        if (currentFile == null || !currentFile.isDirectory() || !currentFile.canRead())
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
        this.overwriteCurrentFileContents();

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
        this.overwriteCurrentFileContents();
    }

    public void openFileManager() {
        String[] args = new String[]{"xdg-open", currentFile.toString()};
        try {
            new ProcessBuilder(args).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
