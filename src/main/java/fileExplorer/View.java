package fileExplorer;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.io.IOException;
import java.util.List;

public class View {

    // access to controller
    private final Controller controller;

    // graphics
    private Screen screen;
    private final TextGraphics text;

    // is the program over
    public boolean END_OF_PROGRAM = false;

    // size of every section: parent (left), current, file (right) -> the current is calculated by the other 2
    private final int PARENT_SIZE = 3; //one third
    private final int FILE_SIZE = 3; // onw third

    // if terminal is smaller than this, do not update
    private final int DRAW_THRESHOLD = 5;

    public View(Controller controller) {
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
        }
    }

    public void viewMain() throws IOException {
        controller.startingLoadFiles();

        // If the screen is resized, redraw the screen (so it in a thread so the main is not blocked)
        new Thread(this::checkForScreenSizeChange).start();

        while (!END_OF_PROGRAM) {
            // read input
            KeyStroke keyStroke = screen.readInput();

            try {
                // Esc -> quit
                if (keyStroke.getKeyType() == KeyType.Escape ||
                        // if the terminal window is closed
                        keyStroke.getKeyType() == KeyType.EOF ||
                        // CTRL D
                        (keyStroke.isCtrlDown() &&
                                keyStroke.getCharacter() == 'd')
                ) {
                    END_OF_PROGRAM = true;
                    System.exit(0);
                }
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
            } catch (NullPointerException _) {
                System.err.println("Pressed an unexpected key: " + keyStroke.getKeyType());
            }
        }

    }

    public void flushScreen() {
        for (int i = 0; i < screen.getTerminalSize().getRows(); i++) {
            text.putString(0, i, " ".repeat(screen.getTerminalSize().getColumns()));
        }
    }

    public void redrawParentDirectoryFiles(List<String> files, int currentFileIndex) {

        int startCol = 0;
        int endCol = screen.getTerminalSize().getColumns() / PARENT_SIZE;

        // flush old files
        for (int i = 0; i < screen.getTerminalSize().getRows(); i++)
            text.putString(startCol, i, " ".repeat(endCol - startCol));

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

        // put the current file
        text.setBackgroundColor(TextColor.ANSI.WHITE);
        text.setForegroundColor(TextColor.ANSI.BLACK);
        text.putString(startCol, halfRow, files.get(currentFileIndex).length() < endCol - startCol ?
                files.get(currentFileIndex) :
                files.get(currentFileIndex).substring(0, endCol - startCol));
        text.setBackgroundColor(TextColor.ANSI.DEFAULT);
        text.setForegroundColor(TextColor.ANSI.DEFAULT);

        // put the files before -> checks that the files are in bound of the half screen and the list
        for (int i = halfRow - 1; i >= 0 &&
                currentFileIndex - halfRow + i >= 0; i--)
            text.putString(startCol, i, files.get(currentFileIndex - halfRow + i).length() < endCol - startCol ?
                    files.get(currentFileIndex - halfRow + i) :
                    files.get(currentFileIndex - halfRow + i).substring(0, endCol - startCol));

        // put the files afterwards -> checks that the files are in bound of the half screen and the list
        for (int i = halfRow + 1; i < screen.getTerminalSize().getRows() &&
                currentFileIndex - halfRow + i < files.size(); i++)
            text.putString(startCol, i, files.get(currentFileIndex - halfRow + i).length() < endCol - startCol ?
                    files.get(currentFileIndex - halfRow + i) :
                    files.get(currentFileIndex - halfRow + i).substring(0, endCol - startCol));
        try {
            screen.refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void redrawCurrentDirectoryFiles(List<String> files, int currentFileIndex) {
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

        // put the current file
        text.setBackgroundColor(TextColor.ANSI.WHITE);
        text.setForegroundColor(TextColor.ANSI.BLACK);
        text.putString(startCol, halfRow, files.get(currentFileIndex).length() < endCol - startCol ?
                files.get(currentFileIndex) :
                files.get(currentFileIndex).substring(0, endCol - startCol));
        text.setBackgroundColor(TextColor.ANSI.DEFAULT);
        text.setForegroundColor(TextColor.ANSI.DEFAULT);

        // put the files before -> checks that the files are in bound of the half screen and the list
        for (int i = halfRow - 1; i >= 0 &&
                currentFileIndex - halfRow + i >= 0; i--)
            text.putString(startCol, i, files.get(currentFileIndex - halfRow + i).length() < endCol - startCol ?
                    files.get(currentFileIndex - halfRow + i) :
                    files.get(currentFileIndex - halfRow + i).substring(0, endCol - startCol));

        // put the files afterwards -> checks that the files are in bound of the half screen and the list
        for (int i = halfRow + 1; i < screen.getTerminalSize().getRows() &&
                currentFileIndex - halfRow + i < files.size(); i++)
            text.putString(startCol, i, files.get(currentFileIndex - halfRow + i).length() < endCol - startCol ?
                    files.get(currentFileIndex - halfRow + i) :
                    files.get(currentFileIndex - halfRow + i).substring(0, endCol - startCol));
        try {
            screen.refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
