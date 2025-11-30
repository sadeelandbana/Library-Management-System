package com.library.app;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import com.library.service.LibraryService;

public class WelcomeScreen extends JFrame {
	private static final long serialVersionUID = 1L;
	private LibraryService libraryService;

    public WelcomeScreen(LibraryService service) {
        super("Library Management System");
        this.libraryService = service;

        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        Color backgroundColor = new Color(240, 248, 255);
        Color panelColor = new Color(224, 235, 255);
        Color textColor = new Color(25, 25, 112);
        Color buttonBgColor = new Color(245, 245, 245);
        Color buttonHoverColor = new Color(220, 220, 220);
        Font titleFont = new Font("Arial", Font.BOLD, 36);
        Font subtitleFont = new Font("Arial", Font.ITALIC, 24);
        Font paragraphFont = new Font("Arial", Font.PLAIN, 16);
        Font buttonTextFont = new Font("Arial", Font.BOLD, 16);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(new EmptyBorder(40, 50, 40, 50));

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        JLabel titleLabel = new JLabel("Library Management System", SwingConstants.CENTER);
        titleLabel.setFont(titleFont); titleLabel.setForeground(textColor); titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel subtitleLabel = new JLabel("Manage Books, CDs, and Members Efficiently", SwingConstants.CENTER);
        subtitleLabel.setFont(subtitleFont); subtitleLabel.setForeground(textColor.darker()); subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(titleLabel); headerPanel.add(Box.createVerticalStrut(10)); headerPanel.add(subtitleLabel);

        // Center
        JPanel centerPanel = new JPanel(new BorderLayout(20, 20));
        centerPanel.setOpaque(false);
        JLabel imageLabel = new JLabel(new ImageIcon("src/main/java/com/library/app/library.jpg"));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JTextArea introText = new JTextArea(
            "Welcome to the Library Management System!\n\n" +
            "This system helps you manage library resources including books, CDs, and users. " +
            "Admins can register users, manage borrowings, track fines, " +
            "while users can borrow and return items."
        );
        introText.setFont(paragraphFont); introText.setForeground(textColor); introText.setBackground(panelColor);
        introText.setWrapStyleWord(true); introText.setLineWrap(true); introText.setEditable(false);
        introText.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(textColor.brighter(), 1), new EmptyBorder(20,20,20,20)));
        centerPanel.add(imageLabel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(introText), BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        JButton exitButton = createStyledButton("Exit", buttonTextFont, buttonBgColor, buttonHoverColor, textColor);
        exitButton.addActionListener(e -> System.exit(0));
        JButton startButton = createStyledButton("Get Started", buttonTextFont, buttonBgColor, buttonHoverColor, textColor);
        startButton.addActionListener(e -> {
            dispose();
            new LoginScreen(libraryService).setVisible(true);
        });
        buttonPanel.add(exitButton); buttonPanel.add(startButton);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        setVisible(true);
    }

    private JButton createStyledButton(String text, Font font, Color bg, Color hover, Color fg){
        JButton btn = new JButton(text);
        btn.setFont(font); btn.setBackground(bg); btn.setForeground(fg);
        btn.setFocusPainted(false); btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e){ btn.setBackground(hover); }
            public void mouseExited(MouseEvent e){ btn.setBackground(bg); }
        });
        return btn;
    }
}
