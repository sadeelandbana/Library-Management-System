package com.library.app;

import javax.swing.SwingUtilities;
import com.library.service.LibraryService;

public class Main {

    public static void main(String[] args) {
        LibraryService libraryService = new LibraryService();
        SwingUtilities.invokeLater(() -> {
            new WelcomeScreen(libraryService).setVisible(true);
        });
    }
}
