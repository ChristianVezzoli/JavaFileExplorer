package fileExplorer;

public class ViewFilePair {

    public final String fileName;
    public final Boolean isDirectory;

    public ViewFilePair(String fileName, Boolean isDirectory) {
        this.fileName = fileName;
        this.isDirectory = isDirectory;
    }

}
