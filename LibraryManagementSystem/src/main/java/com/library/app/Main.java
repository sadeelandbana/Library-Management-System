package com.library.app;

import javax.swing.SwingUtilities;
import com.library.service.LibraryService;

public class Main {

    public static void main(String[] args) {
        // Shared backend service
        LibraryService libraryService = new LibraryService();

        // Launch GUI
        SwingUtilities.invokeLater(() -> {
            new WelcomeScreen(libraryService).setVisible(true);
        });
    }
}
