package fileExplorer;

import com.googlecode.lanterna.TextColor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        /* Default config values */
        int PARENT_SIZE = 5;
        int FILE_SIZE = 2;
        TextColor FILE_BACKGROUND_DEFAULT = TextColor.ANSI.DEFAULT;
        TextColor FILE_FOREGROUND_DEFAULT = TextColor.ANSI.DEFAULT;
        TextColor FILE_BACKGROUND_CURRENT = TextColor.ANSI.WHITE;
        TextColor FILE_FOREGROUND_CURRENT = TextColor.ANSI.BLACK;
        int TABS_SPACES = 4;
        int REFRESH_TIME_MILLIS = 500;
        int CURRENT_FILE_CONTENTS_MAX_SIZE = 50;

        System.setProperty("jdk.console", "java.base");

        // try reading config
        try {
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
                        case "CURRENT_FILE_CONTENTS_MAX_SIZE":
                            CURRENT_FILE_CONTENTS_MAX_SIZE = Integer.parseInt(value);
                            break;
                        default:
                            throw new IllegalArgumentException();
                    }
                }
            } catch (FileNotFoundException e) {
                System.out.println("Config file not found. Using default values.");
            }

        } catch (Exception e) {
            System.out.println("Error in Config. Aborting...");
        }

        // initialize classes
        Model model = new Model(CURRENT_FILE_CONTENTS_MAX_SIZE);
        Controller controller = new Controller(model);
        View view = new View(controller,
                PARENT_SIZE, FILE_SIZE,
                TABS_SPACES, REFRESH_TIME_MILLIS,
                FILE_BACKGROUND_DEFAULT, FILE_FOREGROUND_DEFAULT,
                FILE_BACKGROUND_CURRENT, FILE_FOREGROUND_CURRENT);
        model.setView(view);

        // start
        try {
            view.viewMain();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // exit
        System.exit(0);
    }
}