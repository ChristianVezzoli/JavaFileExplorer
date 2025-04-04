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

    /// Access to controller
    private final Controller controller;

    private Screen screen;
    private final TextGraphics text;

    private final int SIDE_OFFSET = 1;

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


    public void viewMain() throws IOException {


        this.drawGUI();

        controller.startingLoadFiles();

        while (true) {

            // read input
            KeyStroke keyStroke = screen.readInput();

            // Esc -> quit
            if (keyStroke.getKeyType() == KeyType.Escape)
                break;
                // j -> next file
            else if (keyStroke.getCharacter() == 'j')
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
        }
    }


    public void drawGUI() {

        // hide cursor
        screen.setCursorPosition(null);

        // set rows
        for (int i = 0; i < screen.getTerminalSize().getRows() - 1; i++) {
            text.setCharacter(0, i, '|');
            text.setCharacter(screen.getTerminalSize().getColumns() - 1, i, '|');
        }

        // set columns
        for (int i = 0; i < screen.getTerminalSize().getColumns() - 1; i++) {
            text.setCharacter(i, 0, '-');
            text.setCharacter(i, screen.getTerminalSize().getRows() - 1, '-');
        }

        // set corners
        text.setCharacter(0, 0, '*');
        text.setCharacter(0, screen.getTerminalSize().getRows() - 1, '*');
        text.setCharacter(screen.getTerminalSize().getColumns() - 1, 0, '*');
        text.setCharacter(screen.getTerminalSize().getColumns() - 1, screen.getTerminalSize().getRows() - 1, '*');

        // draw title
        String title = "File Explorer";
        text.setBackgroundColor(TextColor.ANSI.GREEN);
        text.putString((screen.getTerminalSize().getColumns() - 1) / 2 - title.length() / 2, 0, title);
        text.setBackgroundColor(TextColor.ANSI.DEFAULT);

        try {
            screen.refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void flushFiles() {
        for (int i = SIDE_OFFSET; i < screen.getTerminalSize().getRows() - 1 - SIDE_OFFSET; i++) {
            text.putString(SIDE_OFFSET, i, " ".repeat(screen.getTerminalSize().getColumns() - 1 - 2 * SIDE_OFFSET));
        }
    }

    /// take all the files and draw them in the filesWindow
    public void drawFiles(List<String> files, int currentFileIndex) {
        //flush old files
        this.flushFiles();

        if (files.isEmpty()) {
            try {
                screen.refresh();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        // half the size of the screen
        int halfSize = (screen.getTerminalSize().getRows() - 1) / 2;

        // put the current file
        text.setBackgroundColor(TextColor.ANSI.WHITE);
        text.setForegroundColor(TextColor.ANSI.BLACK);
        text.putString(SIDE_OFFSET, halfSize, files.get(currentFileIndex));
        text.setBackgroundColor(TextColor.ANSI.DEFAULT);
        text.setForegroundColor(TextColor.ANSI.DEFAULT);

        // put the files before -> checks that the files are in bound of the half screen and the list
        for (int i = halfSize - 1; i > SIDE_OFFSET &&
                currentFileIndex - halfSize + i >= 0; i--)
            text.putString(SIDE_OFFSET, i, files.get(currentFileIndex - halfSize + i));

        // put the files afterwards -> checks that the files are in bound of the half screen and the list
        for (int i = halfSize + 1; i < screen.getTerminalSize().getRows() - 1 - SIDE_OFFSET &&
                currentFileIndex - halfSize + i < files.size(); i++)
            text.putString(SIDE_OFFSET, i, files.get(currentFileIndex - halfSize + i));

        // update
        try {
            screen.refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
