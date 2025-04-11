package fileExplorer;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.invoke.LambdaConversionException;
import java.util.*;

public class View {

    // access to controller
    private final Controller controller;

    // graphics
    private Screen screen;
    private final TextGraphics text;

    // is the program over
    public boolean END_OF_PROGRAM = false;

    // if terminal is smaller than this, do not update
    private final int DRAW_THRESHOLD = 5;

    /* CUSTOMIZABLE */
    // size of every section: parent (left), current, file (right) -> the current is calculated by the other 2
    private int PARENT_SIZE = 5; //one fifth
    private int FILE_SIZE = 2; // half

    private TextColor FILE_BACKGROUND_DEFAULT = TextColor.ANSI.DEFAULT;
    private TextColor FILE_BACKGROUND_CURRENT = TextColor.ANSI.WHITE;
    private TextColor FILE_FOREGROUND_DEFAULT = TextColor.ANSI.DEFAULT;
    private TextColor FILE_FOREGROUND_CURRENT = TextColor.ANSI.BLACK;

    // when reading file preview, tabs become the specified number of spaces
    private int TABS_SPACES = 4;

    // time between refresh for screen size update
    private int REFRESH_TIME_MILLIS = 500;

    public View(Controller controller) throws Exception {
        // read config
        File configFile = new File(System.getProperty("user.home") + "/.config/fileExplorer.conf");
        try {
            Scanner scanner = new Scanner(configFile);
            // read config file
            while (scanner.hasNextLine()) {
                String config = scanner.nextLine();
                String variable = config.split("=")[0].toUpperCase();
                // ignore empty lines and comments
                if (variable.isEmpty() || variable.startsWith("//"))
                    continue;
                String value = config.split("=")[1].toUpperCase();
                switch (variable) {
                    case "PARENT_SIZE":
                        PARENT_SIZE = Integer.parseInt(value);
                        break;
                    case "FILE_SIZE":
                        FILE_SIZE = Integer.parseInt(value);
                        break;
                    case "FILE_BACKGROUND_DEFAULT":
                        FILE_BACKGROUND_DEFAULT = TextColor.ANSI.valueOf(value);
                        break;
                    case "FILE_BACKGROUND_CURRENT":
                        FILE_BACKGROUND_CURRENT = TextColor.ANSI.valueOf(value);
                        break;
                    case "FILE_FOREGROUND_DEFAULT":
                        FILE_FOREGROUND_DEFAULT = TextColor.ANSI.valueOf(value);
                        break;
                    case "FILE_FOREGROUND_CURRENT":
                        FILE_FOREGROUND_CURRENT = TextColor.ANSI.valueOf(value);
                        break;
                    case "TABS_SPACES":
                        TABS_SPACES = Integer.parseInt(value);
                        break;
                    case "REFRESH_TIME_MILLIS":
                        REFRESH_TIME_MILLIS = Integer.parseInt(value);
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Config file not found. Using default values.");
        } catch (Exception e) {
            throw new Exception("Error in Config file");
        }

        this.controller = controller;
        // initialize
        try {
            screen = new DefaultTerminalFactory().createScreen();
            screen.startScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
        text = screen.newTextGraphics();
    }

    public void checkForScreenSizeChange() {
        while (!END_OF_PROGRAM) {
            if (screen.doResizeIfNecessary() != null) {
                if (screen.getTerminalSize().getRows() < DRAW_THRESHOLD || screen.getTerminalSize().getColumns() < DRAW_THRESHOLD)
                    continue;
                this.flushScreen();
                controller.getFiles();
            }
            try {
                Thread.sleep(REFRESH_TIME_MILLIS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public void viewMain() throws IOException {
        controller.startingLoadFiles();

        // If the screen is resized, redraw the screen (so it in a thread so the main is not blocked)
        new Thread(this::checkForScreenSizeChange).start();

        while (!END_OF_PROGRAM) {
            try {
                // read input
                KeyStroke keyStroke = screen.readInput();

                // Esc -> quit
                if (keyStroke.getKeyType() == KeyType.Escape ||
                        // if the terminal window is closed
                        keyStroke.getKeyType() == KeyType.EOF ||
                        // CTRL D
                        (keyStroke.isCtrlDown() &&
                                keyStroke.getCharacter() == 'd'))
                    END_OF_PROGRAM = true;
                    // j -> next file
                else if (keyStroke.isCtrlDown() || keyStroke.getCharacter() == 'j')
                    controller.selectNextFile();
                    // k -> previous file
                else if (keyStroke.getCharacter() == 'k')
                    controller.selectPreviousFile();
                    // enter selected dir
                else if (keyStroke.getCharacter() == 'l')
                    controller.goToSelectedDir();
                    // go to parent dir
                else if (keyStroke.getCharacter() == 'h')
                    controller.goToParentDir();
                else if (keyStroke.getKeyType() == KeyType.Enter)
                    controller.openFileManager();
            } catch (Exception e) {
                System.err.println("Pressed an unexpected key");
            }
        }
    }

    public void flushScreen() {
        for (int i = 0; i < screen.getTerminalSize().getRows(); i++) {
            text.putString(0, i, " ".repeat(screen.getTerminalSize().getColumns()));
        }
    }

    public void redrawParentDirectoryFiles(List<ViewFilePair> files, int currentFileIndex) {

        int startCol = 0;
        int endCol = screen.getTerminalSize().getColumns() / PARENT_SIZE;

        // flush old files
        for (int i = 0; i < screen.getTerminalSize().getRows(); i++)
            text.putString(startCol, i, " ".repeat(endCol - startCol));

        // if file is empty, draw nothing
        if (files == null || files.isEmpty() || currentFileIndex < 0) {
            try {
                screen.refresh();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        // draw new files
        int halfRow = screen.getTerminalSize().getRows() / 2;

        // print files
        for (int i = Math.max(halfRow - currentFileIndex, 0);
                i < (Math.min(halfRow + files.size() - currentFileIndex, 2 * halfRow));
                i++) {

            // mark currently selected file (it's at halfRow)
            if (i == halfRow){
                text.setBackgroundColor(FILE_BACKGROUND_CURRENT);
                text.setForegroundColor(FILE_FOREGROUND_CURRENT);
            }

            // folder are bold
            if (files.get(currentFileIndex - halfRow + i).isDirectory)
                text.setModifiers(EnumSet.of(SGR.BOLD));

            // print file
            text.putString(startCol, i, files.get(currentFileIndex - halfRow + i).fileName.length() < endCol - startCol ?
                    files.get(currentFileIndex - halfRow + i).fileName :
                    files.get(currentFileIndex - halfRow + i).fileName.substring(0, endCol - startCol));

            // reset modifiers
            text.setBackgroundColor(FILE_BACKGROUND_DEFAULT);
            text.setForegroundColor(FILE_FOREGROUND_DEFAULT);
            text.disableModifiers(SGR.BOLD);
        }



        // update screen
        try {
            screen.refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void redrawCurrentDirectoryFiles(List<ViewFilePair> files, int currentFileIndex) {
        int startCol = screen.getTerminalSize().getColumns() / PARENT_SIZE;
        int endCol = screen.getTerminalSize().getColumns() - screen.getTerminalSize().getColumns() / FILE_SIZE;

        // flush old files
        for (int i = 0; i < screen.getTerminalSize().getColumns(); i++)
            text.putString(startCol, i, " ".repeat(endCol - startCol));

        if (files.isEmpty() || currentFileIndex < 0) {
            try {
                screen.refresh();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        // draw new files
        int halfRow = screen.getTerminalSize().getRows() / 2;

        // print files
        for (int i = Math.max(halfRow - currentFileIndex, 0);
                i < (Math.min(halfRow + files.size() - currentFileIndex, 2 * halfRow));
                i++) {

            // mark currently selected file (it's at halfRow)
            if (i == halfRow){
                text.setBackgroundColor(FILE_BACKGROUND_CURRENT);
                text.setForegroundColor(FILE_FOREGROUND_CURRENT);
            }

            // folder are bold
            if (files.get(currentFileIndex - halfRow + i).isDirectory)
                text.setModifiers(EnumSet.of(SGR.BOLD));

            // print file
            text.putString(startCol, i, files.get(currentFileIndex - halfRow + i).fileName.length() < endCol - startCol ?
                    files.get(currentFileIndex - halfRow + i).fileName :
                    files.get(currentFileIndex - halfRow + i).fileName.substring(0, endCol - startCol));

            // reset colors
            text.setBackgroundColor(FILE_BACKGROUND_DEFAULT);
            text.setForegroundColor(FILE_FOREGROUND_DEFAULT);
            text.disableModifiers(SGR.BOLD);
        }

        // update screen
        try {
            screen.refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void redrawCurrentFileContents(String fileContents) {

        int startCol = screen.getTerminalSize().getColumns() - screen.getTerminalSize().getColumns() / FILE_SIZE;
        int endCol = screen.getTerminalSize().getColumns();

        // sanitize input from control characters
        // replace tabs with spaces
        fileContents = fileContents.replaceAll("\t", " ".repeat(TABS_SPACES));
        // remove every control character that is not \n
        fileContents = fileContents.replaceAll("[\\x00-\\x09\\x0b\\x0c\\x0e-\\x1f\\x7f]", "");


        List<String> lines = Arrays.stream(fileContents.split("\n"))
                .flatMap(line -> {
                    List<String> subLines = new ArrayList<>();
                    for (int i = 0; i < line.length(); i+=(endCol - startCol))
                        subLines.add(line.substring(i, Math.min(i + (endCol - startCol), line.length())));
                    return subLines.stream();
                })
                .toList();

        // flush old file
        for (int i = 0; i < screen.getTerminalSize().getRows(); i++)
            text.putString(startCol, i, " ".repeat(endCol - startCol));

        // draw file contents
        for (int i = 0; i < (Math.min(lines.size(), screen.getTerminalSize().getRows())); i++)
            text.putString(startCol, i, lines.get(i));

        try {
            screen.refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
