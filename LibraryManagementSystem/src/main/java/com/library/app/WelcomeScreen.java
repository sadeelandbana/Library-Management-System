package com.library.app;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import com.library.service.LibraryService;

public class WelcomeScreen extends JFrame {
	private static final long serialVersionUID = 1L;
	private LibraryService libraryService;

    // Unified Color Palette
    private final Color backgroundColor = new Color(247, 241, 236); // #F7F1EC - Vanilla Mist
    private final Color panelColor = new Color(216, 195, 176);     // #D8C3B0 - Pale Biscuit
    private final Color textColor = new Color(56, 43, 38);        // #382B26 - Dark Roast (for H1 Titles & Primary Buttons)
    private final Color secondaryTextColor = new Color(156, 126, 101); // #9C7E65 - Soft Leather (Secondary Titles/Borders)
    private final Color buttonPrimaryBgColor = new Color(56, 43, 38); // #382B26 - Dark Roast (Primary Buttons)
    private final Color buttonPrimaryHoverColor = new Color(80, 60, 50); // Lighter Dark Roast
    private final Color buttonSecondaryBgColor = new Color(111, 86, 65); // #6F5641 - Mudstone (Secondary Buttons)
    private final Color buttonSecondaryHoverColor = new Color(140, 110, 90); // Lighter Mudstone
    private final Color borderColor = new Color(156, 126, 101);    // #9C7E65 - Soft Leather (for rounded borders where needed)

    // Unified Font Scheme
    private final Font titleFont = new Font("Arial", Font.BOLD, 24);
    private final Font subtitleFont = new Font("Arial", Font.ITALIC, 20);
    private final Font headingFont = new Font("Arial", Font.BOLD, 20);
    private final Font paragraphFont = new Font("Arial", Font.PLAIN, 16);
    private final Font labelFont = new Font("Arial", Font.BOLD, 14);
    private final Font fieldFont = new Font("Arial", Font.PLAIN, 14);
    private final Font buttonFont = new Font("Arial", Font.BOLD, 14);

    public WelcomeScreen(LibraryService service) {
        super("Library Management System");
        this.libraryService = service;

        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

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
        introText.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(borderColor, 1, 15), new EmptyBorder(20,20,20,20)));
        centerPanel.add(imageLabel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(introText), BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        JButton exitButton = createStyledButton("Exit", buttonSecondaryBgColor, buttonSecondaryHoverColor, textColor, buttonFont);
        exitButton.addActionListener(e -> System.exit(0));
        JButton startButton = createStyledButton("Get Started", buttonPrimaryBgColor, buttonPrimaryHoverColor, Color.WHITE, buttonFont);
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

    private JButton createStyledButton(String text, Color bg, Color hover, Color fg, Font font){
        JButton btn = new JButton(text);
        btn.setFont(font); btn.setBackground(bg); btn.setForeground(fg);
        btn.setFocusPainted(false); btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new RoundedBorder(bg, 1, 15)); // Apply rounded border to buttons
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e){ btn.setBackground(hover); }
            public void mouseExited(MouseEvent e){ btn.setBackground(bg); }
        });
        return btn;
    }

    // Custom Rounded Border Class
    class RoundedBorder extends AbstractBorder {
        private Color color;
        private int thickness;
        private int radius;
        private Insets insets;

        RoundedBorder(Color color, int thickness, int radius) {
            this.color = color;
            this.thickness = thickness;
            this.radius = radius;
            this.insets = new Insets(radius, radius, radius, radius);
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x, y, width - thickness, height - thickness, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return insets;
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.top = insets.right = insets.bottom = radius;
            return insets;
        }
    }
}
