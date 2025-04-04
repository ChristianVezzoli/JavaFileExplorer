package fileExplorer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Model {

    // access view
    private View view;

    private File parentDir;
    private List<File> files;
    private int currentFileIndex;

    public Model() {
        files = new ArrayList<>();
    }

    public void setView(View view) {
        this.view = view;
    }

    public void loadFiles(String path) {
        File dir = new File(path);

        // if file is not directory or is root, do nothing
        File[] filesArr = dir.listFiles();
        if (filesArr == null) {
            return;
        }

        files.clear();
        files.addAll(Arrays.stream(filesArr)
                // order by name
                .sorted(Comparator.comparing(File::getName))
                .toList());

        currentFileIndex = 0;
        parentDir = dir.getParentFile();

        // call to draw
        view.drawFiles(files.stream().map(File::getName).toList(), currentFileIndex);
    }


    public void selectNextFile() {
        if (files.isEmpty())
            return;
        currentFileIndex = (currentFileIndex + 1) % files.size();
        view.drawFiles(files.stream().map(File::getName).toList(), currentFileIndex);
    }

    public void selectPreviousFile() {
        if (files.isEmpty())
            return;
        currentFileIndex = (currentFileIndex - 1 + files.size()) % files.size();
        view.drawFiles(files.stream().map(File::getName).toList(), currentFileIndex);
    }

    public void goToSelectedDir() {
        if (files.isEmpty())
            return;
        loadFiles(files.get(currentFileIndex).getPath());
    }

    public void goToParentDir() {
        if (parentDir == null)
            return;
        loadFiles(parentDir.getPath());
    }
}
