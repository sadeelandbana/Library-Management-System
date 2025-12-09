package com.library.app;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import com.library.service.LibraryService;
import com.library.model.UserAccount;

public class LoginScreen extends JFrame {
    private static final long serialVersionUID = 1L;
    private LibraryService libraryService;
    private JTextField emailField;
    private JPasswordField passwordField;

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

    public LoginScreen(LibraryService service){
        super("Library Login");
        this.libraryService = service;

        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(20,20));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(new EmptyBorder(30,40,30,40));

        JLabel titleLabel = new JLabel("User Login", SwingConstants.CENTER);
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(textColor);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // ---------------- FORM PANEL ----------------
        JPanel formPanel = new JPanel(new GridLayout(0, 1, 15, 15));
        formPanel.setBackground(panelColor);
        formPanel.setBorder(new CompoundBorder(
                new RoundedBorder(borderColor, 1, 15),
                new EmptyBorder(25,25,25,25)
        ));

        // EMAIL ROW
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(labelFont);
        emailLabel.setForeground(textColor);

        JPanel emailRow = new JPanel(new BorderLayout(10, 0));
        emailRow.setBackground(panelColor);
        JLabel emailIcon = new JLabel(new ImageIcon("icons/email.png"));  // غيّر المسار
        emailField = new JTextField();
        emailField.setFont(fieldFont);
        emailField.setBorder(new RoundedBorder(borderColor, 1, 10)); // Rounded border for text field
        emailField.setPreferredSize(new Dimension(250, 40));
        emailRow.add(emailIcon, BorderLayout.WEST);
        emailRow.add(emailField, BorderLayout.CENTER);

        // PASSWORD ROW
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(labelFont);
        passLabel.setForeground(textColor);

        JPanel passRow = new JPanel(new BorderLayout(10, 0));
        passRow.setBackground(panelColor);
        JLabel passIcon = new JLabel(new ImageIcon("icons/password.png")); // غيّر المسار
        passwordField = new JPasswordField();
        passwordField.setFont(fieldFont);
        passwordField.setBorder(new RoundedBorder(borderColor, 1, 10)); // Rounded border for password field
        passwordField.setPreferredSize(new Dimension(250, 40));
        passRow.add(passIcon, BorderLayout.WEST);
        passRow.add(passwordField, BorderLayout.CENTER);

        // إضافة الحقول للوحة
        formPanel.add(emailLabel);
        formPanel.add(emailRow);
        formPanel.add(passLabel);
        formPanel.add(passRow);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // ---------------- BUTTON PANEL ----------------
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10,10));
        buttonPanel.setBackground(backgroundColor);

        JButton backBtn = new JButton("← Back");
        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register Now");

        // Button styling unified
        styleButton(loginBtn, buttonPrimaryBgColor, buttonPrimaryHoverColor, Color.WHITE, buttonFont);
        styleButton(registerBtn, buttonPrimaryBgColor, buttonPrimaryHoverColor, Color.WHITE, buttonFont);
        styleButton(backBtn, buttonSecondaryBgColor, buttonSecondaryHoverColor, Color.WHITE, buttonFont);

        buttonPanel.add(backBtn);
        buttonPanel.add(loginBtn);
        buttonPanel.add(registerBtn);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Actions
        loginBtn.addActionListener(e -> handleLogin());
        registerBtn.addActionListener(e -> {
            dispose();
            new RegistrationScreen(libraryService).setVisible(true);
        });
        backBtn.addActionListener(e -> {
            dispose();
            new WelcomeScreen(libraryService).setVisible(true);
        });

        setContentPane(mainPanel);
        setVisible(true);
    }

    // ---------------- BUTTON STYLE ----------------
    private void styleButton(JButton btn, Color bg, Color hover, Color fg, Font font){
        btn.setFont(font);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorder(new RoundedBorder(bg, 1, 15)); // Apply rounded border to buttons
        btn.setPreferredSize(new Dimension(140, 40));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited(MouseEvent e) { btn.setBackground(bg); }
        });
    }

    private void handleLogin(){
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if(email.isEmpty() || password.isEmpty()){
            JOptionPane.showMessageDialog(this,"Fill all fields!");
            return;
        }

        UserAccount user = libraryService.login(email, password);
        if(user != null){
            JOptionPane.showMessageDialog(this, "Login successful as " + user.getRole());
            dispose();
            new LibraryFrame(libraryService, user).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid email or password!");
        }
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
